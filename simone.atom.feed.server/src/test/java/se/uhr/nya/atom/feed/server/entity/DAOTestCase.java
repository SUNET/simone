package se.uhr.nya.atom.feed.server.entity;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
@Ignore
public class DAOTestCase {

	protected static EmbeddedDatabase db = new EmbeddedDatabaseBuilder().addScript("feed_schema.sql").setType(
			EmbeddedDatabaseType.DERBY).build();

	@Configuration
	static class ContextConfiguration {

		@Bean
		public DataSourceTransactionManager transactionManager() {
			return new DataSourceTransactionManager(db);
		}
	}
}