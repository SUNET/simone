package se.uhr.simone.atom.feed.server.entity;

import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class DataSourceParameterResolver implements ParameterResolver, AfterEachCallback {

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
		throws ParameterResolutionException {
		return (parameterContext.getParameter().getType() == DataSource.class);
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
		throws ParameterResolutionException {
		EmbeddedDataSource ds = new EmbeddedDataSource();
		ds.setDatabaseName("memory:test");
		ds.setCreateDatabase("create");
		Flyway flyway = Flyway.configure().dataSource(ds).load();
		flyway.migrate();
		return ds;
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		try {
			DriverManager.getConnection("jdbc:derby:memory:test;drop=true");
		} catch (SQLException e) {
			// empty			
		}
	}

}
