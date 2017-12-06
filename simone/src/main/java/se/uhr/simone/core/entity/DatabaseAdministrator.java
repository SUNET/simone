package se.uhr.simone.core.entity;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.uhr.simone.extension.api.entity.DatabaseAdmin;

@ApplicationScoped
public class DatabaseAdministrator implements DatabaseAdmin {

	private final static Logger LOG = LoggerFactory.getLogger(DatabaseAdministrator.class);

	@Inject
	@FeedDS
	private DataSource ds;

	private Flyway flyway = new Flyway();

	@Transactional(TxType.NOT_SUPPORTED)
	public void initialize() {
		flyway.setDataSource(ds);
		flyway.migrate();
	}

	@Transactional(TxType.NOT_SUPPORTED)
	@Override
	public void dropTables() {

		try (Connection connection = ds.getConnection()) {
			DbSupport db = DbSupportFactory.createDbSupport(connection, false);

			Schema<?> schema = db.getOriginalSchema();
			schema.clean();
			flyway.migrate();
		} catch (SQLException e) {
			LOG.error("Could not clean schema", e);
		}
	}
}
