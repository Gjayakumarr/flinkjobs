package com.flinkjobs.packetStucture;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ApiLog implements Serializable {
	public static final long serialVersionUID = 1L;

	private String endApi;
    private long dateTime;
    private String host;
    private String userAgent;
    private String requestFormat;
    @JsonProperty("exception")
    private boolean isException;
    private int responseCode;
    private boolean success;
    private long eventTimestamp;

    // Getters and setters
    public String getEndApi() {
        return endApi;
    }

    public void setEndApi(String endApi) {
        this.endApi = endApi;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getRequestFormat() {
        return requestFormat;
    }

    public void setRequestFormat(String requestFormat) {
        this.requestFormat = requestFormat;
    }

    public boolean isException() {
        return isException;
    }

    public void setIsException(boolean isException) {
		this.isException = isException;
	}

	public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    
    public long getEventTimestamp() {
		return eventTimestamp;
	}

	public void setEventTimestamp(long eventTimestamp) {
		this.eventTimestamp = eventTimestamp;
	}

	// JSON deserialization
    public static ApiLog fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ApiLog.class);
    }
}