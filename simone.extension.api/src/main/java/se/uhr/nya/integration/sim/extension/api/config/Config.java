package se.uhr.nya.integration.sim.extension.api.config;

public interface Config {

	/**
	 * Get Simulator initialization configuration.
	 */

	Initialization getInitialization();

	interface Initialization {

		/**
		 * Determines if the simulator should start with an empty database or not.
		 * 
		 * @return <code>true</code> if the database should be empty.
		 */

		boolean emptyDataBase();
	}
}
