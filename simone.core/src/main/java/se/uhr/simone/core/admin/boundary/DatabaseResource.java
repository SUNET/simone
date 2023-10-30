package se.uhr.simone.core.admin.boundary;

import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import se.uhr.simone.common.db.FileLoadResultRepresentation;
import se.uhr.simone.core.SimOne;

public class DatabaseResource {

	private final SimOne simOne;

	public DatabaseResource(SimOne simOne) {
		this.simOne = simOne;
	}

	@Operation(summary = "Empty the database")
	@APIResponse(responseCode = "200", description = "Success")
	@DELETE
	public Response deleteTables() {
		simOne.clearDatabase();
		return Response.ok().build();
	}

	@Operation(summary = "Loads the database", description = "This is for backwards compatibility only, define a custom endpoint to load the database")
	@APIResponse(responseCode = "200", description = "Status and list of order ids", content = @Content(schema = @Schema(implementation = FileLoadResultRepresentation.class)))
	@Parameters(value = { @Parameter(name = "name", description = "the name of the file", style = ParameterStyle.FORM),
			@Parameter(name = "content", description = "the content of the file", style = ParameterStyle.FORM) })
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@POST
	public Response load(Map<String, String> parts) {
		if (parts.containsKey("name") && parts.containsKey("content")) {
			FileLoadResult result = loadFile(parts.get("name"), parts.get("content"));
			return Response.status(result.errorMessage == null ? Response.Status.OK : Response.Status.BAD_REQUEST)
					.entity(FileLoadResultRepresentation.of(result.eventIdList, result.errorMessage))
					.build();

		} else {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	/**
	 * This is for backwards compatibility only, define a custom endpoint to load the database
	 *
	 * @param name The file name
	 * @param content The file content
	 * @return The result of the load
	 * @deprecated Use a custom endpoint instead
	 */

	@Deprecated(forRemoval = true)
	protected FileLoadResult loadFile(String name, String content) {
		throw new UnsupportedOperationException("The endpoint exists for backwards compatibility");
	}

	protected static record FileLoadResult (String errorMessage, List<String> eventIdList){

	}
}
