package se.uhr.simone.core.control.filemonitor;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class FileUtilTest {

	@Test
	public void testGetPathWithSuffix() throws Exception {
		Path tmp = File.createTempFile("test", ".txt").toPath();
		Path suffixed = FileUtil.getPathWithSuffix(tmp, ".txt");
		assertThat(suffixed.getFileName().toString()).endsWith(".txt.txt");
	}

	@Test
	public void testDeleteWithSuffix() throws IOException {

		Path base = File.createTempFile("test", ".txt").toPath();

		Path f1 = FileUtil.getPathWithSuffix(base, "1");
		Path f2 = FileUtil.getPathWithSuffix(base, "2");

		Files.createFile(f1);
		Files.createFile(f2);

		assertThat(Files.exists(f1)).isTrue();
		assertThat(Files.exists(f2)).isTrue();

		FileUtil.deleteWithSuffix(base, "1", "2");

		assertThat(Files.exists(f1)).isFalse();
		assertThat(Files.exists(f2)).isFalse();
	}

	@Test
	public void testRenameWithSuffix() throws IOException {
		Path base = File.createTempFile("test", ".txt").toPath();

		assertThat(Files.exists(base)).isTrue();

		FileUtil.renameWithSuffix(base, "1");

		assertThat(Files.exists(base)).isFalse();
		assertThat(Files.exists(FileUtil.getPathWithSuffix(base, "1"))).isTrue();
	}

	@Test
	public void testHasSuffix() {
		assertThat(FileUtil.hasSuffix("test.txt", ".txt", ".doc")).isTrue();
		assertThat(FileUtil.hasSuffix("test.t", ".txt", ".doc")).isFalse();
	}
}
