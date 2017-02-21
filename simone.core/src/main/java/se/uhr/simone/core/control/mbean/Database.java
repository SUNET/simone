package se.uhr.simone.core.control.mbean;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import se.uhr.simone.extension.api.entity.DatabaseAdmin;

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
