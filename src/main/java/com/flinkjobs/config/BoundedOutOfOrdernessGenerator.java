package com.flinkjobs.config;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;

import com.flinkjobs.packetStucture.ApiLog;
import com.flinkjobs.packetStucture.KafkaObject;

/**
 * This generator generates watermarks assuming that elements arrive out of
 * order, but only to a certain degree. The latest elements for a certain
 * timestamp t will arrive at most n milliseconds after the earliest elements
 * for timestamp t. This watermark will handle case of unordered data.
 */
public class BoundedOutOfOrdernessGenerator implements WatermarkGenerator<ApiLog> {

	private final long maxOutOfOrderness = 3500; // 3.5 seconds

	private long currentMaxTimestamp;

	/**
	 * Handles the incoming event and updates the current maximum timestamp.
	 *
	 * @param event          the event
	 * @param eventTimestamp the timestamp of the event
	 * @param output         the watermark output to emit the watermark
	 */
	@Override
	public void onEvent(ApiLog event, long eventTimestamp, WatermarkOutput output) {
		currentMaxTimestamp = Math.max(currentMaxTimestamp, eventTimestamp);
	}

	/**
	 * Periodically emits the watermark based on the latest maximum timestamp seen,
	 * minus the allowed out-of-orderness duration.
	 *
	 * @param output the watermark output to emit the watermark
	 */
	@Override
	public void onPeriodicEmit(WatermarkOutput output) {
		output.emitWatermark(new Watermark(currentMaxTimestamp - maxOutOfOrderness - 1));
	}
}

