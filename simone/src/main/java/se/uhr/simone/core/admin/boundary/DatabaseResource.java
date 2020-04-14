package se.uhr.simone.core.admin.boundary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import se.uhr.simone.admin.api.file.FileLoadResultRepresentation;
import se.uhr.simone.api.entity.DatabaseAdmin;
import se.uhr.simone.api.feed.UniqueIdentifier;
import se.uhr.simone.api.fileloader.ExtensionContext;
import se.uhr.simone.api.fileloader.FileLoader;
import se.uhr.simone.api.fileloader.FileLoaderDescriptor;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.control.extension.ExtensionManager;

@Tag(name = "admin")
@AdminCatagory
@Path("/admin/database")
public class DatabaseResource {

	@Inject
	Instance<DatabaseAdmin> databaseAdmin;

	@Inject
	ExtensionManager extensionManager;

	@Operation(summary = "Loads the database", description = "This has the same effects as dropping a file in the dropin directory")
	@APIResponse(responseCode = "200", description = "Status and list of order ids", content = @Content(schema = @Schema(implementation = FileLoadResultRepresentation.class)))
	@Parameters(value = { @Parameter(name = "name", description = "the name of the file", style = ParameterStyle.FORM),
			@Parameter(name = "content", description = "the content of the file", style = ParameterStyle.FORM) })
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@POST
	public Response load(Map<String, String> parts) throws IOException {
		List<String> idList = new ArrayList<>();
		StringBuilder errorLog = new StringBuilder();
		boolean success = true;

		if (parts.containsKey("name") && parts.containsKey("content")) {

			for (FileLoaderDescriptor ext : extensionManager.getFileExtensions(parts.get("name"))) {
				try (BufferedReader reader = new BufferedReader(new StringReader(parts.get("content")))) {
					FileLoader fileLoader = ext.createJob(reader);

					RestExtensionContext context = new RestExtensionContext();

					FileLoader.Result loadResult = fileLoader.execute(context);

					idList.addAll(context.getEventIdList());
					errorLog.append(context.getErrorLog());

					if (loadResult != FileLoader.Result.SUCCESS) {
						success = false;
					}
				}
			}

			return Response.status(success ? Status.OK : Status.BAD_REQUEST)
					.entity(FileLoadResultRepresentation.of(idList, errorLog.toString()))
					.build();
		} else {
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	@Operation(summary = "Empty the database")
	@APIResponse(responseCode = "200", description = "Success")
	@DELETE
	public Response deleteTables() {
		for (DatabaseAdmin db : databaseAdmin) {
			db.dropTables();
		}

		return Response.ok().build();
	}

	static class RestExtensionContext implements ExtensionContext {

		private String errorMessage;
		private final List<String> eventIdList = new ArrayList<>();

		public String getErrorLog() {
			return errorMessage;
		}

		@Override
		public void addEventId(UniqueIdentifier uid) {
			eventIdList.add(uid.getValue());
		}

		public List<String> getEventIdList() {
			return eventIdList;
		}

		@Override
		public void setErrorMessage(String message) {
			errorMessage = message;
		}
	}
}
