package se.uhr.nya.integration.sim.server.admin.control;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResponsePathTest {

	@Test
	public void testEquals() {
		ResponsePath path1 = ResponsePath.of("///my/path");
		ResponsePath path2 = ResponsePath.of("my/path//");

		assertTrue(path1.equals(path2));
	}

	@Test
	public void testHashcode() {
		ResponsePath path1 = ResponsePath.of("///my/path");
		ResponsePath path2 = ResponsePath.of("my/path//");

		assertThat(path1.hashCode(), is(path2.hashCode()));
	}
}
