package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PLSResponse {

	private boolean gracePeriod;
	private boolean status;

	public boolean getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(boolean gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "PLSResponse [gracePeriod=" + gracePeriod + ", status=" + status + "]";
	}

}
