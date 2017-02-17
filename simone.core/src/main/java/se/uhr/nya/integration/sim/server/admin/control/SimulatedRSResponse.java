package se.uhr.nya.integration.sim.server.admin.control;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import se.uhr.nya.integration.sim.admin.rs.ResponseRepresentation;

@ApplicationScoped
public class SimulatedRSResponse {

	public static final int NORMAL_RESPONSE_CODE = -1;

	private final Map<ResponsePath, ResponseRepresentation> overrides = new HashMap<>();

	private int delay = 0;

	private int code = NORMAL_RESPONSE_CODE;

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

	public void resetCodeForPath(String path) {
		overrides.remove(ResponsePath.of(path));
	}

	public void resetCodeForAllPaths() {
		overrides.clear();
	}
}
