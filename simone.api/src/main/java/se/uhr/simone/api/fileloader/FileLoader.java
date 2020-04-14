package se.uhr.simone.api.fileloader;

/**
 * Defines a job that is started when a file with specific properties is placed in the dropin directory.      
 * 
 * @see FileLoaderDescriptor
 */

public interface FileLoader {

	enum Result {
		SUCCESS,
		ERROR
	}

	/**
	 * Execute the job.
	 * 
	 * @param context The extension context.
	 * @return The result of the operation.
	 */

	Result execute(ExtensionContext context);
}
