package se.uhr.simone.core.control.filemonitor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import se.uhr.simone.core.control.extension.ExtensionManager;
import se.uhr.simone.extension.api.fileloader.ExtensionContext;
import se.uhr.simone.extension.api.fileloader.FileLoader;
import se.uhr.simone.extension.api.fileloader.FileLoaderDescriptor;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryMonitorTest {

	@Test
	public void testRunAvailableJobsSuccess() throws IOException, InterruptedException {

		ExtensionManager extensionManager = mock(ExtensionManager.class);

		Path dropin = Files.createTempDirectory("test");
		DirectoryMonitor mon = new DirectoryMonitor(extensionManager, dropin);
		Path jobfile = dropin.resolve("f1.test");

		FileLoader fakeJob = mock(FileLoader.class);
		given(fakeJob.execute(any(ExtensionContext.class))).willReturn(FileLoader.Result.SUCCESS);

		FileLoaderDescriptor fakeJobDesc = mock(FileLoaderDescriptor.class);
		given(fakeJobDesc.accept(anyString())).willReturn(true);
		given(fakeJobDesc.createJob(any(Reader.class))).willReturn(fakeJob);

		given(extensionManager.getFileExtensions(any(String.class))).willReturn(Arrays.asList(fakeJobDesc));

		mon.runAvailableJobs();

		verify(fakeJob, times(0)).execute(any(ExtensionContext.class));

		Files.createFile(jobfile);

		// We need to give the watch service some time to detected the new file
		Thread.sleep(1000);

		mon.runAvailableJobs();
		assertTrue(Files.exists(dropin.resolve("f1.test.done")));
		assertFalse(Files.exists(jobfile));

		verify(fakeJob, times(1)).execute(any(ExtensionContext.class));
	}

	@Test
	public void testRunAvailableJobsFailure() throws IOException, InterruptedException {

		ExtensionManager extensionManager = mock(ExtensionManager.class);

		Path dropin = Files.createTempDirectory("test");
		DirectoryMonitor mon = new DirectoryMonitor(extensionManager, dropin);

		Path jobfile = dropin.resolve("f1.test");

		FileLoader fakeJob = mock(FileLoader.class);
		given(fakeJob.execute(any(ExtensionContext.class))).willReturn(FileLoader.Result.ERROR);

		FileLoaderDescriptor fakeJobDesc = mock(FileLoaderDescriptor.class);
		given(fakeJobDesc.accept(anyString())).willReturn(true);
		given(fakeJobDesc.createJob(any(Reader.class))).willReturn(fakeJob);

		given(extensionManager.getFileExtensions(any(String.class))).willReturn(Arrays.asList(fakeJobDesc));

		mon.runAvailableJobs();

		verify(fakeJob, times(0)).execute(any(ExtensionContext.class));

		Files.createFile(jobfile);

		mon.runAvailableJobs();

		assertTrue(Files.exists(dropin.resolve("f1.test.error")));
		assertFalse(Files.exists(jobfile));

		verify(fakeJob, times(1)).execute(any(ExtensionContext.class));
	}
}
