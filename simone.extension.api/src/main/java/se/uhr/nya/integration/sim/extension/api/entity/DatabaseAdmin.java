package se.uhr.nya.integration.sim.extension.api.entity;

public interface DatabaseAdmin {

	/**
	 * User has requested to clear the database.
	 */

	void dropTables();

}
