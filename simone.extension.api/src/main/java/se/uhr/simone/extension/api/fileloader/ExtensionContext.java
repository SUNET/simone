package se.uhr.simone.extension.api.fileloader;

import se.uhr.simone.extension.api.feed.UniqueIdentifier;

public interface ExtensionContext {

	/**
	 * Set a description of the error
	 * 
	 * @param message The error description.
	 */

	void setErrorMessage(String message);

	/**
	 * Add event ID, these ID's is returned to the client if the job was submitted by a POST
	 * operation. 
	 * 
	 * @param uid Atom feed event ID.
	 */

	void addEventId(UniqueIdentifier uid);
}
