package se.uhr.simone.core.admin.control;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ResponsePathTest {

	@Test
	public void testEquals() {
		ResponsePath path1 = ResponsePath.of("///my/path");
		ResponsePath path2 = ResponsePath.of("my/path//");

		assertThat(path1).isEqualTo(path2);
	}

	@Test
	public void testHashcode() {
		ResponsePath path1 = ResponsePath.of("///my/path");
		ResponsePath path2 = ResponsePath.of("my/path//");

		assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
	}

	@Test
	public void shouldHandleNullValue() throws Exception {
		ResponsePath path1 = ResponsePath.of(null);
		ResponsePath path2 = ResponsePath.of(null);

		assertThat(path1).isEqualTo(path2);
	}
}
