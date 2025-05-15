package com.flinkjobs.packetStucture;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaObject implements Serializable, Comparable<KafkaObject> {

	private static final long serialVersionUID = 1L;
	private ApiLog apiLog;
	private long eventTimestamp;
	private String data;

	public KafkaObject() {
		super();
	}

	@Override
	public int compareTo(KafkaObject o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the apiLogInterceptorReqRes
	 */
	public ApiLog getApiLog() {
		return apiLog;
	}

	/**
	 * @param apiLogInterceptorReqRes the apiLogInterceptorReqRes to set
	 */
	public void setApiLog(ApiLog apiLog) {
		this.apiLog = apiLog;
	}

	/**
	 * @return the eventTimestamp
	 */
	public long getEventTimestamp() {
		return eventTimestamp;
	}

	/**
	 * @param eventTimestamp the eventTimestamp to set
	 */
	public void setEventTimestamp(long eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("KafkaObject [apiLog=");
		builder.append(apiLog);
		builder.append(", eventTimestamp=");
		builder.append(eventTimestamp);
		builder.append(", data=");
		builder.append(data);
		builder.append("]");
		return builder.toString();
	}

}