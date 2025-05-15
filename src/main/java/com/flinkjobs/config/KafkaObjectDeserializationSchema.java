package com.flinkjobs.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flinkjobs.packetStucture.ApiLog;
import com.flinkjobs.packetStucture.KafkaObject;

/**
 * Schema for serializing and deserializing KafkaObject instances with ApiLog.
 */
public class KafkaObjectDeserializationSchema
        implements DeserializationSchema<ApiLog>, SerializationSchema<ApiLog> {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(KafkaObjectDeserializationSchema.class);
    private final ObjectMapper objectMapper;

    public KafkaObjectDeserializationSchema() {
        this.objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        objectMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public TypeInformation<ApiLog> getProducedType() {
        return TypeInformation.of(ApiLog.class);
    }

    @Override
    public byte[] serialize(ApiLog element) {
        if (element == null) {
            logger.warn("Attempted to serialize a null KafkaObject.");
            return new byte[0];
        }

        try {
            return objectMapper.writeValueAsBytes(element);
        } catch (Exception e) {
            logger.error("Error serializing KafkaObject: {}", e.getMessage());
            return new byte[0];
        }
    }

    @Override
    public ApiLog deserialize(byte[] message) throws IOException {
        if (message == null || message.length == 0) {
            logger.warn("Received null or empty Kafka record.");
            return null;
        }

        try {
        	String line = new String(message, StandardCharsets.UTF_8);
        	ApiLog kafkaObject = objectMapper.readValue(line, ApiLog.class);
//            ApiLog apiLog = objectMapper.readValue(message, ApiLog.class);
//            kafkaObject.setApiLog(apiLog);  
            return kafkaObject;
        } catch (JsonParseException e) {
            logger.error("WARN: Invalid JSON encountered: " + new String(message) + " - " + e.getMessage());
        } catch (IOException e) {
            logger.error("ERROR: Error deserializing message: " + new String(message), e);
        } catch (Exception e) {
            logger.error("Unexpected error during deserialization: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean isEndOfStream(ApiLog nextElement) {
        return false;
    }
}
