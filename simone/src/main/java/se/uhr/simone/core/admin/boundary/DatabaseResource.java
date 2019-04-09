package se.uhr.simone.core.admin.boundary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import se.uhr.simone.admin.api.file.FileLoadResultRepresentation;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.control.extension.ExtensionManager;
import se.uhr.simone.extension.api.entity.DatabaseAdmin;
import se.uhr.simone.extension.api.feed.UniqueIdentifier;
import se.uhr.simone.extension.api.fileloader.ExtensionContext;
import se.uhr.simone.extension.api.fileloader.FileLoader;
import se.uhr.simone.extension.api.fileloader.FileLoaderDescriptor;

@Tag(name = "admin")
@Stateless
@AdminCatagory
@Path("admin/database")
public class DatabaseResource {

	@Inject
	private Instance<DatabaseAdmin> databaseAdmin;

	@Inject
	private ExtensionManager extensionManager;

	@Operation(summary = "Loads the database", description = "This has the same effects as dropping a file in the dropin directory")
	@APIResponse(description = "Status and list of order ids", content = @Content(schema = @Schema(implementation = FileLoadResultRepresentation.class)))
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response load(@Parameter(name = "file to upload", style = ParameterStyle.FORM) @MultipartForm FileUploadForm fileUpload)
		throws IOException {

		List<String> idList = new ArrayList<>();
		StringBuilder errorLog = new StringBuilder();
		boolean success = true;

		for (FileLoaderDescriptor ext : extensionManager.getFileExtensions(fileUpload.getName())) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileUpload.getContent()))) {
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
	}

	@Operation(summary = "Empty the database")
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
