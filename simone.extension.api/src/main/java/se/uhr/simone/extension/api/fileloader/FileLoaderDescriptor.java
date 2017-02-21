package se.uhr.simone.extension.api.fileloader;

import java.io.Reader;

/**
 * Describes a file loader. A file loader is a task that is performed when a file with specific
 * properties is placed in the dropin directory.    
 * 
 * @see FileLoader
 */

public interface FileLoaderDescriptor {

	/**
	 * Check if the specified file is acceptable for this type of job.
	 * 
	 * @param filename The file name,
	 * @return <code>true</code> if this job should work on the specified file, <code>false</code> otherwise.
	 */

	boolean accept(String filename);

	/**
	 * Create a new job instance for this type of job.
	 * 
	 * @param reader A file reader to read from.
	 * @return A new job instance.
	 */

	FileLoader createJob(Reader reader);
}
