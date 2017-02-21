package se.uhr.simone.core.admin.control;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulatedFeedResponse {

	public static final int NORMAL_STATUS_CODE = -1;

	private int delay = 0;

	private int code = NORMAL_STATUS_CODE;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int feedDelay) {
		this.delay = feedDelay;
	}

	public int getCode() {
		return code;
	}

	public void setGlobalCode(int code) {
		this.code = code;
	}
}
