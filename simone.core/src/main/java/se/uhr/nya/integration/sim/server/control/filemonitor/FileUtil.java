package se.uhr.nya.integration.sim.server.control.filemonitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	public static Path renameWithSuffix(Path path, String suffix) throws IOException {
		return Files.move(path, getPathWithSuffix(path, suffix), StandardCopyOption.REPLACE_EXISTING);
	}

	public static Path getPathWithSuffix(Path path, String suffix) {
		return path.getParent().resolve(path.getFileName() + suffix);
	}

	public static void deleteWithSuffix(Path file, List<String> suffix) throws IOException {
		for (String s : suffix) {
			Files.deleteIfExists(getPathWithSuffix(file, s));
		}
	}

	public static void deleteWithSuffix(Path file, String... suffix) throws IOException {
		deleteWithSuffix(file, Arrays.asList(suffix));
	}

	public static boolean hasSuffix(String filename, String... suffix) {
		return hasSuffix(filename, Arrays.asList(suffix));
	}

	public static boolean hasSuffix(String filename, List<String> suffix) {
		for (String s : suffix) {
			if (filename.endsWith(s)) {
				return true;
			}
		}

		return false;
	}
}
