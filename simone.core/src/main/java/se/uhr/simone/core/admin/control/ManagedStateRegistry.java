package se.uhr.simone.core.admin.control;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ManagedStateRegistry {

	private final ConcurrentMap<String, ManagedState> instanceMap = new ConcurrentHashMap<>();

	private ManagedStateRegistry(){}

	private static class SingletonHelper {
		private static final ManagedStateRegistry INSTANCE = new ManagedStateRegistry();
	}

	public static ManagedStateRegistry getInstance() {
		return SingletonHelper.INSTANCE;
	}

	public void register(String name, ManagedState simulatedInstance) {
		instanceMap.put(name, simulatedInstance);
	}

	public ManagedState get(String name) {
		return instanceMap.get(name);
	}
}
