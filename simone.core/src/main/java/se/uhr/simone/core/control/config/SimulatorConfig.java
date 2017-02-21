package se.uhr.simone.core.control.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.uhr.simone.core.control.Constants;
import se.uhr.simone.extension.api.config.Config;

public class SimulatorConfig implements Config {

	private final static Logger LOG = LoggerFactory.getLogger(SimulatorConfig.class);

	private final static String CONFIG_FILE_NAME = "config.ini";

	private final Properties properties = new Properties();

	public SimulatorConfig() throws IOException {
		Path dropinFile = Constants.DROPIN_DIRECTORY.resolve(CONFIG_FILE_NAME);

		if (Files.exists(dropinFile)) {
			LOG.debug("Reading config from: " + dropinFile);
			try (Reader reader = new FileReader(dropinFile.toFile())) {
				properties.load(reader);
			}
		} else {
			try (InputStream is = SimulatorConfig.class.getResourceAsStream("/" + CONFIG_FILE_NAME)) {
				properties.load(is);
			}
		}
	}

	@Override
	public Initialization getInitialization() {
		return new SimulatorInitialization();
	}

	class SimulatorInitialization implements Initialization {

		private final static String INITIALIZATION_EMPTY_DB = "initialization.empty.db";

		@Override
		public boolean emptyDataBase() {
			return Boolean.parseBoolean(properties.getProperty(INITIALIZATION_EMPTY_DB));
		}
	}
}
