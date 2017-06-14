package se.uhr.simone.core.admin.control;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

import se.uhr.simone.core.admin.control.ResponsePath;

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
	
	@Test
	public void shouldHandleNullValue() throws Exception {
		ResponsePath path1 = ResponsePath.of(null);
		ResponsePath path2 = ResponsePath.of(null);

		assertTrue(path1.equals(path2));
	}
}
