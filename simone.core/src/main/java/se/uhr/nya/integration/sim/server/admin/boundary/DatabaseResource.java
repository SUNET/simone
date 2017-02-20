package se.uhr.nya.integration.sim.server.admin.boundary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.util.GenericType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import se.uhr.nya.integration.sim.admin.api.file.FileLoadResultRepresentation;
import se.uhr.nya.integration.sim.extension.api.entity.DatabaseAdmin;
import se.uhr.nya.integration.sim.extension.api.feed.UniqueIdentifier;
import se.uhr.nya.integration.sim.extension.api.fileloader.ExtensionContext;
import se.uhr.nya.integration.sim.extension.api.fileloader.FileLoader;
import se.uhr.nya.integration.sim.extension.api.fileloader.FileLoaderDescriptor;
import se.uhr.nya.integration.sim.server.boundary.AdminCatagory;
import se.uhr.nya.integration.sim.server.control.extension.ExtensionManager;

@Api(tags = { "database admin" })
@Stateless
@AdminCatagory
@Path("admin/database")
public class DatabaseResource {

	@Inject
	private Instance<DatabaseAdmin> databaseAdmin;

	@Inject
	private ExtensionManager extensionManager;

	@ApiOperation(value = "Loads the database", notes = "This has the same effects as dropping a file in the dropin directory", response = FileLoadResultRepresentation.class)
	@POST
	@Consumes("multipart/form-data")
	public Response updateFromAF26File(MultipartFormDataInput input) throws IOException {

		String fileName = input.getFormDataPart("name", new GenericType<String>() {
		});

		InputStream is = input.getFormDataPart("content", new GenericType<InputStream>() {
		});

		List<String> idList = new ArrayList<>();
		StringBuilder errorLog = new StringBuilder();
		boolean success = true;

		for (FileLoaderDescriptor ext : extensionManager.getFileExtensions(fileName)) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
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

		private final Writer errorWriter = new StringWriter();
		private final List<String> eventIdList = new ArrayList<>();

		@Override
		public Writer getErrorWriter() {
			return errorWriter;
		}

		public String getErrorLog() {
			return errorWriter.toString();
		}

		@Override
		public void addEventId(UniqueIdentifier uid) {
			eventIdList.add(uid.getValue());
		}

		public List<String> getEventIdList() {
			return eventIdList;
		}
	}
}
