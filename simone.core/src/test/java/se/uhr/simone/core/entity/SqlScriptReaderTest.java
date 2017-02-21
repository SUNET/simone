package se.uhr.simone.core.entity;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import se.uhr.simone.core.entity.SqlScriptReader;

public class SqlScriptReaderTest {

	@Test
	public void testGetStatements() {

		SqlScriptReader cut = new SqlScriptReader(this.getClass().getResourceAsStream("/test.sql"));

		List<String> stmts = cut.getStatements();

		assertThat(stmts, hasSize(8));

		for (String stmt : stmts) {
			assertThat(stmt, not(containsString(";")));
		}
	}
}
