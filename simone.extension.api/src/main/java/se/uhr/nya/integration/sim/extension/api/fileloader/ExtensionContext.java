package se.uhr.nya.integration.sim.extension.api.fileloader;

import java.io.Writer;

import se.uhr.nya.integration.sim.extension.api.feed.UniqueIdentifier;

public interface ExtensionContext {

	/**
	 * Get a writer to print error information on.
	 * 
	 * @return A writer instance.
	 */

	Writer getErrorWriter();

	/**
	 * Add event ID, these ID's is returned to the client if the job was submitted by a POST
	 * operation. 
	 * 
	 * @param uid Atom feed event ID.
	 */

	void addEventId(UniqueIdentifier uid);
}
