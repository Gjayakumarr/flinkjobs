package com.flinkjobs.packetStucture;

import java.io.Serializable;

public class EndPointRequestAndResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String endReq; // Shortened: endpointRequest
	private String endRes; // Shortened: endpointResponse

	/**
	 * @return the endReq
	 */
	public String getEndReq() {
		return endReq;
	}

	/**
	 * @param endReq the endReq to set
	 */
	public void setEndReq(String endReq) {
		this.endReq = endReq;
	}

	/**
	 * @return the endRes
	 */
	public String getEndRes() {
		return endRes;
	}

	/**
	 * @param endRes the endRes to set
	 */
	public void setEndRes(String endRes) {
		this.endRes = endRes;
	}

}
