 package com.flinkjobs.flinkjobs;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flinkjobs.config.BoundedOutOfOrdernessGenerator;
import com.flinkjobs.config.KafkaObjectDeserializationSchema;
import com.flinkjobs.config.MongoDBSink;
import com.flinkjobs.constants.Constant;
import com.flinkjobs.packetStucture.ApiLog;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class FlinkjobsApplication {
	private static final Logger logger = LoggerFactory.getLogger(FlinkjobsApplication.class);
	private static final int DEFAULT_PARALLELISM = 10;

	public static void main(String[] args) throws Exception {
		System.out.println("Java Version: " + System.getProperty("java.version"));
        // Set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ParameterTool parameter = ParameterTool.fromArgs(args);
        int parallelism = parameter.getInt("dbThreads", DEFAULT_PARALLELISM);

        // Kafka configuration
        Properties kafkaProperties = new Properties();
		kafkaProperties.setProperty("bootstrap.servers", Constant.BOOTSTRAP_SERVER);
		kafkaProperties.setProperty("group.id", Constant.GROUP_ID);
		kafkaProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

     // Initialize KafkaSource
     			KafkaSource<ApiLog> kafkaSource = KafkaSource.<ApiLog>builder()
     					.setBootstrapServers(Constant.BOOTSTRAP_SERVER)
     					.setTopics(Constant.CONSUMER_TOPIC)
     					.setGroupId(Constant.GROUP_ID)
     					.setStartingOffsets(OffsetsInitializer.latest())
     					.setValueOnlyDeserializer(new KafkaObjectDeserializationSchema()).build();

     			// Configure watermark strategy to handle out-of-order events.
     			WatermarkStrategy<ApiLog> watermarkStrategy = WatermarkStrategy
     					.forGenerator(ctx -> new BoundedOutOfOrdernessGenerator())
     					.withTimestampAssigner((event, timestamp) -> event.getEventTimestamp());

        // Create data stream from Kafka
		DataStream<ApiLog> stream = env
				.fromSource(kafkaSource, watermarkStrategy, "Kafka Source")
				.returns(TypeInformation.of(ApiLog.class))
				.rebalance();

     // Validate and create MongoDB collection.
     			MongoClient mongoClient = MongoClients.create(Constant.MONGODB_URI);
     			MongoDatabase database = mongoClient.getDatabase(Constant.MONGODB_DATABASE_SCHEMA);
     			String currentMonthCollectionName = Constant.getCurrentMonthCollectionName(Constant.MONGODB_DATABASE_COLLECTION);
     			ensureCollectionExists(database, currentMonthCollectionName);
     			// Close the client after ensuring the collection exists.
     			mongoClient.close(); 
     			
        // Add MongoDB sink
        MongoDBSink mongoSink = new MongoDBSink(Constant.MONGODB_URI, Constant.MONGODB_DATABASE_SCHEMA, Constant.MONGODB_DATABASE_COLLECTION);
        
        // Add MongoDB sink to the Flink job with parallelism
     	stream.addSink(mongoSink).name("MongoDB Sink").setParallelism(parallelism);

        // Execute the job
        env.execute("Flink Kafka to MongoDB Job");
    }
	
	/**
	 * Ensures the collection exists in the database.
	 */
	private static void ensureCollectionExists(MongoDatabase database, String collectionName) {
		if (!database.listCollectionNames().into(new ArrayList<>()).contains(collectionName)) {
			database.createCollection(collectionName);
			logger.info("Created new collection: " + collectionName);
		} else {
			logger.info("Using existing collection: " + collectionName);
		}
	}

}