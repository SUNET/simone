package se.uhr.simone.atom.feed.server.entity;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class DAOTestCase {

	protected DataSource ds = createDataSource();

	private Flyway flyway = new Flyway();

	private static EmbeddedDataSource createDataSource() {
		EmbeddedDataSource ds = new EmbeddedDataSource();
		ds.setDatabaseName("memory:test");
		ds.setCreateDatabase("create");
		return ds;
	}

	@BeforeEach
	public void setupDatabase() {
		flyway.setDataSource(ds);
		flyway.migrate();
	}

	@AfterEach
	public void deleteDataFromDatabase() {
		try {
			DriverManager.getConnection("jdbc:derby:memory:test;drop=true");
		} catch (SQLException e) {
			// empty			
		}
	}
}