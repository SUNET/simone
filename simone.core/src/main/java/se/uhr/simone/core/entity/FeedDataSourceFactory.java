package se.uhr.simone.core.entity;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@ApplicationScoped
public class FeedDataSourceFactory {

	@Resource(mappedName = "java:/jdbc/FEED")
	private DataSource ds;

	@Produces
	@FeedDS
	public DataSource getFeedDataSource() {
		return ds;
	}
}
