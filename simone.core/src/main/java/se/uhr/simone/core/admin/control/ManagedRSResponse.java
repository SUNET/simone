package se.uhr.simone.core.admin.control;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import se.uhr.simone.common.rs.ResponseRepresentation;

public class ManagedRSResponse {

	public static final int NORMAL_RESPONSE_CODE = -1;

	private final Map<ResponsePath, ResponseRepresentation> overrides = new HashMap<>();

	private int delay = 0;

	private int code = NORMAL_RESPONSE_CODE;

	private Bucket bucket;

	private boolean throttle;

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public int getCode() {
		return code;
	}

	public void setGlobalCode(int code) {
		this.code = code;
	}

	public void setCodeForPath(ResponseRepresentation response) {
		overrides.put(ResponsePath.of(response.getPath()), response);
	}

	public ResponseRepresentation getCodeForPath(String path) {
		return overrides.get(ResponsePath.of(path));
	}

	public void setRateLimit(int rateLimit) {
		if (rateLimit > 0) {
			Bandwidth limit = Bandwidth.simple(rateLimit, Duration.ofSeconds(1));
			bucket = Bucket4j.builder().addLimit(limit).build();
			throttle = true;
		} else {
			throttle = false;
		}
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void resetCodeForPath(String path) {
		overrides.remove(ResponsePath.of(path));
	}

	public void resetCodeForAllPaths() {
		overrides.clear();
	}

	public boolean throttle() {
		return throttle;
	}

}
