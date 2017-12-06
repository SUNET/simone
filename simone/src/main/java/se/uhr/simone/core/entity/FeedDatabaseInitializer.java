package se.uhr.simone.core.entity;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class FeedDatabaseInitializer {

	@Inject
	private DatabaseAdministrator databaseAdministrator;

	@PostConstruct
	public void postConstruct() {
		databaseAdministrator.initialize();
	}
}
