package se.uhr.simone.api.entity;

public interface DatabaseAdmin {

	/**
	 * User has requested to clear the database.
	 */

	void dropTables();

}
