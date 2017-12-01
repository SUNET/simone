package se.uhr.simone.core.control;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

	public static Path DROPIN_DIRECTORY = Paths.get(System.getProperty("se.uhr.simone.dropin", "dropin"));
}
