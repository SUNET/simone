package se.uhr.simone.core.control;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {

	public static final int DB_DEBUG_PORT = Integer.parseInt(System.getProperty("se.uhr.nya.integration.sim.db.debug.port", "61100"));

	public static Path DROPIN_DIRECTORY = Paths.get(System.getProperty("se.uhr.nya.integration.sim.dropin", "../dropin"));
}
