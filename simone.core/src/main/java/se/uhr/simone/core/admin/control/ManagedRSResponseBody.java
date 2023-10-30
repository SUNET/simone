package se.uhr.simone.core.admin.control;

import java.util.HashMap;
import java.util.Map;
import se.uhr.simone.common.rs.ResponseBodyRepresentation;

public class ManagedRSResponseBody {

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
