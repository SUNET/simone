package se.uhr.nya.integration.sim.server.admin.control;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import se.uhr.nya.integration.sim.admin.rs.ResponseBodyRepresentation;

@ApplicationScoped
public class SimulatedRSResponseBody {

	private final Map<ResponsePath, ResponseBodyRepresentation> overrides = new HashMap<>();

	public void setOverride(String path, ResponseBodyRepresentation response) {
		overrides.put(ResponsePath.of(path), response);
	}

	public ResponseBodyRepresentation getOverride(String path) {
		return overrides.get(ResponsePath.of(path));
	}

	public void deleteOverride(String path) {
		overrides.remove(ResponsePath.of(path));
	}
}
