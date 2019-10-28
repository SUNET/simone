package se.uhr.simone.core.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class SqlScriptReaderTest {

	@Test
	public void testGetStatements() {

		SqlScriptReader cut = new SqlScriptReader(this.getClass().getResourceAsStream("/test.sql"));

		List<String> stmts = cut.getStatements();

		assertThat(stmts).hasSize(8);

		for (String stmt : stmts) {
			assertThat(stmt).doesNotContain(";");
		}
	}
}
