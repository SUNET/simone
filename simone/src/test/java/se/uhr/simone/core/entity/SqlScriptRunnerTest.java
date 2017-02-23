package se.uhr.simone.core.entity;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import se.uhr.simone.core.entity.SqlScriptRunner;

public class SqlScriptRunnerTest {

	@Mock
	private JdbcTemplate jdbcTemplate;

	@InjectMocks
	private SqlScriptRunner runner;

	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecute() {

		runner.execute(this.getClass().getResourceAsStream("/test.sql"));

		ArgumentCaptor<String> stmtCaptor = ArgumentCaptor.forClass(String.class);

		verify(jdbcTemplate, times(8)).execute(stmtCaptor.capture());
	}
}
