package se.uhr.simone.core.admin.boundary;

import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import se.uhr.simone.admin.rs.ResponseBodyRepresentation;
import se.uhr.simone.admin.rs.ResponseRepresentation;
import se.uhr.simone.core.admin.control.ManagedFeedResponse;
import se.uhr.simone.core.admin.control.ManagedRSResponse;
import se.uhr.simone.core.admin.control.ManagedRSResponseBody;
import se.uhr.simone.core.admin.control.ManagedStateRegistry;
import se.uhr.simone.core.boundary.Managed;

@Tag(name = "admin")
public class RSResource {

	private static final String INSTANCE_NAME = "simone.instance.name";

	private final ManagedRSResponse simulatedResponse;

	private final ManagedRSResponseBody simulatedResponseResponseBody;

	@Context
	private Configuration myConfiguration;

	public RSResource(ManagedRSResponse simulatedResponse, ManagedRSResponseBody simulatedResponseResponseBody) {
		this.simulatedResponse = simulatedResponse;
		this.simulatedResponseResponseBody = simulatedResponseResponseBody;
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Answer with specified code for all REST requests", description = "Enters a state where all REST requests are answered with the specified status code")
	@APIResponse(responseCode = "200", description = "Success")
	@PUT
	@Path("/code/global")
	public Response setGlobalCode(
			@RequestBody(name = "The HTTP status code", required = true, content = @Content(schema = @Schema(type = SchemaType.INTEGER), example = "401")) int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@Operation(summary = "Answer normally for all REST requests", description = "Resumes normal state")
	@APIResponse(responseCode = "200", description = "Success")
	@DELETE
	@Path("/code/global")
	public Response resetGlobalResponseCode() {
		simulatedResponse.setGlobalCode(ManagedFeedResponse.NORMAL_STATUS_CODE);
		simulatedResponse.resetCodeForAllPaths();
		return Response.ok().build();
	}

	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Operation(summary = "Answer with specified code for a specific REST requests", description = "Enters a state where a specific REST requests are answered with the specified status code")
	@APIResponse(responseCode = "200", description = "Success")
	@PUT
	@Path("/code/path")
	public Response setResponseCodeForPath(ResponseRepresentation response) {
		simulatedResponse.setCodeForPath(response);
		return Response.ok().build();
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Answer with normal code for a specific REST requests", description = "Resumes normal state for specified path")
	@APIResponse(responseCode = "200", description = "Success")
	@DELETE
	@Path("/code/path")
	public Response resetResponseCodeForPath(
			@RequestBody(name = "The REST path, i.e. the path sans web context", required = true, content = @Content(schema = @Schema(type = SchemaType.STRING))) String path) {
		simulatedResponse.resetCodeForPath(path.length() != 0 ? path : null);
		return Response.ok().build();
	}

	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Operation(summary = "Answer with specified code and body for a specific REST requests", description = "Enters a state where a specific REST requests are answered with the specified status code and body")
	@APIResponse(responseCode = "200", description = "Success")
	@PUT
	@Path("/body")
	public Response setResponseOverride(ResponseBodyRepresentation response) {
		simulatedResponseResponseBody.setOverride(response.getPath(), response);
		return Response.ok().build();
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Answer with specified code and body for a specific REST requests", description = "Enters a state where a specific REST requests are answered with the specified status code and body")
	@APIResponse(responseCode = "200", description = "Success")
	@DELETE
	@Path("/body")
	public Response setDefaultResponseCode(
			@RequestBody(name = "The REST path, i.e. the path sans web context", required = true, content = @Content(schema = @Schema(type = SchemaType.STRING))) String path) {
		simulatedResponseResponseBody.deleteOverride(path);
		return Response.ok().build();
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Delay REST requests", description = "Delay each REST request with the specified time, set 0 to resume to normal")
	@APIResponse(responseCode = "200", description = "Success")
	@PUT
	@Path("/delay")
	public Response setDelay(@Parameter(name = "Time in seconds") int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@Consumes(MediaType.TEXT_PLAIN)
	@Operation(summary = "Rate limit REST requests", description = "Set a rate limit for requests to the api, set 0 to remove limit")
	@APIResponse(responseCode = "200", description = "Success")
	@PUT
	@Path("/ratelimit")
	public Response setRatelimit(@Parameter(name = "Requests per seconds") int requestsPerSecond) {
		simulatedResponse.setRateLimit(requestsPerSecond);
		return Response.ok().build();
	}

	@Provider
	public static class RsServiceEnablerConfigurer implements DynamicFeature {

		@Override
		public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			Class<?> clazz = resourceInfo.getResourceClass();

			if (clazz.isAnnotationPresent(Managed.class)) {
				Managed managed = clazz.getAnnotation(Managed.class);
				context.register(new RsServiceFilter(managed.value()), 1);
			}
		}
	}

	public static class RsServiceFilter implements ContainerResponseFilter {

		private final String name;

		public RsServiceFilter(String name) {
			this.name = name;
		}

		@Override
		public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
			var simulatedInstance = ManagedStateRegistry.getInstance().get(name);

			if(simulatedInstance == null) {
				return;
			}

			String requestPath = requestContext.getUriInfo().getAbsolutePath().getPath();

			handleDelay();

			if (simulatedInstance.simulatedRSResponse().getCode() != ManagedFeedResponse.NORMAL_STATUS_CODE) {
				responseContext.setStatus(simulatedInstance.simulatedRSResponse().getCode());
			}

			ResponseBodyRepresentation overrideBody = simulatedInstance.simulatedRSResponseBody().getOverride(requestPath);

			if (overrideBody != null) {
				responseContext.setEntity(overrideBody.getBody());
				responseContext.setStatus(overrideBody.getCode());
			}

			ResponseRepresentation overrideStatus = simulatedInstance.simulatedRSResponse().getCodeForPath(requestPath);

			if (overrideStatus != null) {
				responseContext.setStatus(overrideStatus.getCode());
			}

			handleThrottling(responseContext);
		}

		private void handleThrottling(ContainerResponseContext responseContext) {
			var simulatedInstance = ManagedStateRegistry.getInstance().get(name);

			if (simulatedInstance.simulatedRSResponse().throttle()) {
				if (!simulatedInstance.simulatedRSResponse().getBucket().tryConsume(1)) {
					responseContext.setStatus(Status.TOO_MANY_REQUESTS.getStatusCode());
				}
			}
		}

		private void handleDelay() {
			var simulatedInstance = ManagedStateRegistry.getInstance().get(name);

			if (simulatedInstance.simulatedRSResponse().getDelay() != 0) {
				try {
					Thread.sleep(simulatedInstance.simulatedRSResponse().getDelay() * 1_000L);
				} catch (InterruptedException e) {
					throw new WebApplicationException(e);
				}
			}
		}
	}
}
