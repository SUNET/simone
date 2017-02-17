package se.uhr.nya.integration.sim.server.control.mbean;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import se.uhr.nya.integration.sim.extension.api.entity.DatabaseAdmin;

@Singleton
@Startup
public class Database extends AbstractMBean implements DatabaseMXBean {

	@Inject
	private Instance<DatabaseAdmin> databaseAdmin;

	public Database() {
		super("simulator");
	}

	@Override
	public void clear() {
		for (DatabaseAdmin db : databaseAdmin) {
			db.dropTables();
		}
	}
}
