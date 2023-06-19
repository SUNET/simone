package se.uhr.simone.core;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import se.uhr.simone.api.entity.DatabaseAdmin;
import se.uhr.simone.api.fileloader.FileLoaderDescriptor;

public class SimOne {

	private final DataSource dataSource;

	private final DatabaseAdmin databaseAdmin;

	private final List<FileLoaderDescriptor> fileLoaderDescriptors;

	private final Path dropinDirectory;

	private SimOne(DataSource dataSource, DatabaseAdmin databaseAdmin, List<FileLoaderDescriptor> fileLoaderDescriptors,
			Path dropinDirectory) {
		this.dataSource = dataSource;

		// optional
		this.databaseAdmin = databaseAdmin;
		this.fileLoaderDescriptors = fileLoaderDescriptors;
		this.dropinDirectory = dropinDirectory;
	}

	public DataSource dataSource() {
		return dataSource;
	}

	public Optional<DatabaseAdmin> databaseAdmin() {
		return Optional.ofNullable(databaseAdmin);
	}

	public List<FileLoaderDescriptor> fileLoaderDescriptors() {
		return fileLoaderDescriptors;
	}

	public Optional<Path> dropinDirectory() {
		return Optional.ofNullable(dropinDirectory);
	}

	static SimOneBuilder builder() {
		return new SimOneBuilder();
	}

	interface OptionalStage {

		OptionalStage withDatabaseAdmin(DatabaseAdmin databaseAdmin);

		OptionalStage withFileLoaderDescriptors(List<FileLoaderDescriptor> fileLoaderDescriptors);

		OptionalStage withDropinDirectory(Path dropinDirectory);
	}

	public static class SimOneBuilder implements OptionalStage {

		private DataSource dataSource;
		private DatabaseAdmin databaseAdmin;
		private List<FileLoaderDescriptor> fileLoaderDescriptors;
		private Path dropinDirectory;

		public OptionalStage withDataSource(DataSource dataSource) {
			this.dataSource = dataSource;
			return this;
		}

		@Override
		public OptionalStage withDatabaseAdmin(DatabaseAdmin databaseAdmin) {
			this.databaseAdmin = databaseAdmin;
			return this;
		}
		@Override
		public OptionalStage withFileLoaderDescriptors(List<FileLoaderDescriptor> fileLoaderDescriptors) {
			this.fileLoaderDescriptors = fileLoaderDescriptors;
			return this;
		}
		@Override
		public OptionalStage withDropinDirectory(Path dropinDirectory) {
			this.dropinDirectory = dropinDirectory;
			return this;
		}

		public SimOne build() {
			return new SimOne(dataSource, databaseAdmin, fileLoaderDescriptors, dropinDirectory);
		}
	}

}
