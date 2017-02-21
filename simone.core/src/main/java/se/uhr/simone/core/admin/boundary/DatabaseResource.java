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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.uhr.simone.admin.api.file.FileLoadResultRepresentation;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.control.extension.ExtensionManager;
import se.uhr.simone.extension.api.entity.DatabaseAdmin;
import se.uhr.simone.extension.api.feed.UniqueIdentifier;
import se.uhr.simone.extension.api.fileloader.ExtensionContext;
import se.uhr.simone.extension.api.fileloader.FileLoader;
import se.uhr.simone.extension.api.fileloader.FileLoaderDescriptor;

@Api(tags = { "database admin" })
@Stateless
@AdminCatagory
@Path("admin/database")
public class DatabaseResource {

	@Inject
	private Instance<DatabaseAdmin> databaseAdmin;

	@Inject
	private ExtensionManager extensionManager;

	@ApiOperation(value = "Loads the database", notes = "This has the same effects as dropping a file in the dropin directory, example curl -X POST --header 'Content-Type: multipart/form-data' --header 'Accept: application/json' -F name=test.txt -F \"content=@orders.txt\" 'http://localhost:8080/sim/api/admin/database", response = FileLoadResultRepresentation.class)
	@ApiImplicitParams({
			@ApiImplicitParam(dataType = "string", name = "name", value = "name of file", paramType = "formData"),
			@ApiImplicitParam(name = "content", value = "file", required = true, dataType = "java.io.File", paramType = "form") })
	@POST
	public Response load(@ApiParam(hidden = true) @MultipartForm FileUploadForm fileUpload) throws IOException {

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
				.entity(FileLoadResultRepresentation.of(idList, errorLog.toString())).build();
	}

	@ApiOperation(value = "Empty the database")
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
