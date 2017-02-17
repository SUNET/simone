package se.uhr.nya.integration.sim.server.control.filemonitor;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class FileUtilTest {

	@Test
	public void testGetPathWithSuffix() throws Exception {
		Path tmp = File.createTempFile("test", ".txt").toPath();
		Path suffixed = FileUtil.getPathWithSuffix(tmp, ".txt");
		assertThat(suffixed.getFileName().toString(), endsWith(".txt.txt"));
	}

	@Test
	public void testDeleteWithSuffix() throws IOException {

		Path base = File.createTempFile("test", ".txt").toPath();

		Path f1 = FileUtil.getPathWithSuffix(base, "1");
		Path f2 = FileUtil.getPathWithSuffix(base, "2");

		Files.createFile(f1);
		Files.createFile(f2);

		assertTrue(Files.exists(f1));
		assertTrue(Files.exists(f2));

		FileUtil.deleteWithSuffix(base, "1", "2");

		assertFalse(Files.exists(f1));
		assertFalse(Files.exists(f2));
	}

	@Test
	public void testRenameWithSuffix() throws IOException {
		Path base = File.createTempFile("test", ".txt").toPath();

		assertTrue(Files.exists(base));

		FileUtil.renameWithSuffix(base, "1");

		assertFalse(Files.exists(base));
		assertTrue(Files.exists(FileUtil.getPathWithSuffix(base, "1")));
	}

	@Test
	public void testHasSuffix() {
		assertTrue(FileUtil.hasSuffix("test.txt", ".txt", ".doc"));
		assertFalse(FileUtil.hasSuffix("test.t", ".txt", ".doc"));
	}
}
