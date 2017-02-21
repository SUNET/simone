package se.uhr.simone.extension.api.entity;

public interface DatabaseAdmin {

	/**
	 * User has requested to clear the database.
	 */

	void dropTables();

}
