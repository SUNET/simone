package se.uhr.simone.core.control.filemonitor;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.uhr.simone.core.control.Constants;
import se.uhr.simone.core.control.extension.ExtensionManager;
import se.uhr.simone.extension.api.feed.UniqueIdentifier;
import se.uhr.simone.extension.api.fileloader.ExtensionContext;
import se.uhr.simone.extension.api.fileloader.FileLoader;
import se.uhr.simone.extension.api.fileloader.FileLoaderDescriptor;

public class DirectoryMonitor {

	private static final String ERROR_LOG_SUFFIX = ".error.log";
	private static final String JOB_ERROR_SUFFIX = ".error";
	private static final String JOB_DONE_SUFFIX = ".done";

	private static final List<String> USED_SUFFIXES = Arrays.asList(ERROR_LOG_SUFFIX, JOB_ERROR_SUFFIX, JOB_DONE_SUFFIX);

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMonitor.class);

	private final Path dropinDirectory;

	private final WatchService watcher;

	private final ExtensionManager extensionManager;

	@Inject
	public DirectoryMonitor(ExtensionManager extensionManager) throws IOException {
		this(extensionManager, Constants.DROPIN_DIRECTORY);
	}

	public DirectoryMonitor(ExtensionManager extensionManager, Path dropinDirectory) throws IOException {
		this.extensionManager = extensionManager;
		this.dropinDirectory = dropinDirectory;

		if (!Files.exists(dropinDirectory)) {
			throw new IllegalArgumentException(dropinDirectory + " does not exist");
		}

		if (!Files.isDirectory(dropinDirectory)) {
			throw new IllegalArgumentException(dropinDirectory + " is not a directory");
		}

		LOG.info("monitoring directory " + dropinDirectory);

		watcher = FileSystems.getDefault().newWatchService();
		dropinDirectory.register(watcher, ENTRY_CREATE);
	}

	public void runAvailableJobs() {
		try {
			List<DirectoryFileJob> jobs = getAvailableJobs();

			for (DirectoryFileJob job : jobs) {
				executeJobAndLog(job);
			}
		} catch (IOException e) {
			LOG.error("Can't create job", e);
		}
	}

	private void executeJobAndLog(DirectoryFileJob job) {
		Path file = job.getPath();
		try {
			FileUtil.deleteWithSuffix(file, USED_SUFFIXES);

			Path logfile = FileUtil.getPathWithSuffix(file, ERROR_LOG_SUFFIX);

			try (BufferedWriter log = Files.newBufferedWriter(logfile, Charset.defaultCharset(), StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING)) {

				LOG.info("execute job: " + job.getPath());

				FileExtensionContext context = new FileExtensionContext();
				
				FileLoader.Result result = job.getFileJob().execute(context);
				
				log.append(context.getErrorMessage());

				log.close();

				if (result == FileLoader.Result.SUCCESS) {
					LOG.info("job finished successfully");
					FileUtil.renameWithSuffix(file, JOB_DONE_SUFFIX);
					Files.deleteIfExists(logfile);
				} else {
					LOG.info("job finished with errors, se log for more information: " + logfile);
					FileUtil.renameWithSuffix(file, JOB_ERROR_SUFFIX);
				}
			}
		} catch (IOException e) {
			LOG.error("Can't initiate job", e);
		}
	}

	private List<DirectoryFileJob> getAvailableJobs() throws IOException {
		List<DirectoryFileJob> res = new ArrayList<>();

		WatchKey key = null;

		while ((key = watcher.poll()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				if (OVERFLOW.equals(kind)) {
					continue;
				} else if (ENTRY_CREATE.equals(kind)) {
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();

					FileLoaderDescriptor desc = getJobDescriptor(filename.getFileName().toString());

					Path path = dropinDirectory.resolve(filename);

					if (desc != null) {
						Reader reader = Files.newBufferedReader(path, Charset.defaultCharset());

						res.add(new DirectoryFileJob(desc.createJob(reader), path));
					} else {
						LOG.debug("No match for: {}", filename);
					}
				}
			}

			if (!key.reset()) {
				LOG.error("Can't monitor dropin directory " + dropinDirectory);
			}
		}

		return res;
	}

	private FileLoaderDescriptor getJobDescriptor(String filename) {
		for (FileLoaderDescriptor jobDesc : extensionManager.getFileExtensions(filename)) {
			if (!FileUtil.hasSuffix(filename, USED_SUFFIXES)) {
				return jobDesc;
			}
		}

		return null;
	}

	static class FileExtensionContext implements ExtensionContext {

		private String errorMessage;

		FileExtensionContext() {
		}

		@Override
		public void addEventId(UniqueIdentifier uid) {
		}


		@Override
		public void setErrorMessage(String message) {
			errorMessage = message;
			
		}
		
		String getErrorMessage() {
			return errorMessage;
		}
	}

	static class DirectoryFileJob {

		private final FileLoader fileJob;
		private final Path path;

		public DirectoryFileJob(FileLoader fileJob, Path path) {
			super();
			this.fileJob = fileJob;
			this.path = path;
		}

		public FileLoader getFileJob() {
			return fileJob;
		}

		public Path getPath() {
			return path;
		}
	}
}
