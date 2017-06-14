package se.uhr.simone.core.admin.boundary;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import se.uhr.simone.admin.rs.ResponseBodyRepresentation;
import se.uhr.simone.admin.rs.ResponseRepresentation;
import se.uhr.simone.core.admin.control.SimulatedFeedResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponseBody;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.boundary.FeedCatagory;

@Api(tags = { "rest admin" })
@AdminCatagory
@Path("admin/rs/response")
public class RSResource {

	@Inject
	private SimulatedRSResponse simulatedResponse;

	@Inject
	private SimulatedRSResponseBody simulatedResponseResponseBody;

	@ApiOperation(value = "Answer with specified code for all REST requests", notes = "Enters a state where all REST requests are answered with the specified status code")
	@PUT
	@Path("code/global")
	public Response setGlobalCode(@ApiParam(value = "The HTTP status code", required = true) int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@ApiOperation(value = "Answer normally for all REST requests", notes = "Resumes normal state")
	@DELETE
	@Path("code/global")
	public Response resetGlobalResponseCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		simulatedResponse.resetCodeForAllPaths();
		return Response.ok().build();
	}

	@ApiOperation(value = "Answer with specified code for a specific REST requests", notes = "Enters a state where a specific REST requests are answered with the specified status code")
	@PUT
	@Path("code/path")
	public Response setResponseCodeForPath(ResponseRepresentation response) {
		simulatedResponse.setCodeForPath(response);
		return Response.ok().build();
	}

	@ApiOperation(value = "Answer with normal code for a specific REST requests", notes = "Resumes normal state for specified path")
	@DELETE
	@Path("code/path")
	public Response resetResponseCodeForPath(@ApiParam(value="The REST path, i.e. the path sans web context") String path) {
		simulatedResponse.resetCodeForPath(path.length() != 0 ? path : null);
		return Response.ok().build();
	}
	
	@ApiOperation(value = "Answer with specified code and body for a specific REST requests", notes = "Enters a state where a specific REST requests are answered with the specified status code and body")
	@PUT
	@Path("body")
	public Response setResponseOverride(ResponseBodyRepresentation response) {
		simulatedResponseResponseBody.setOverride(response.getPath(), response);
		return Response.ok().build();
	}

	@ApiOperation(value = "Answer with specified code and body for a specific REST requests", notes = "Enters a state where a specific REST requests are answered with the specified status code and body")	
	@DELETE
	@Path("body")
	public Response setDefaultResponseCode(@ApiParam(value="The REST path, i.e. the path sans web context") String path) {
		simulatedResponseResponseBody.deleteOverride(path);
		return Response.ok().build();
	}

	@ApiOperation(value = "Delay REST requests", notes = "Delay each REST request with the specified time, set 0 to resume to normal")
	@PUT
	@Path("delay")
	public Response setDelay(@ApiParam(value = "Time in seconds") int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@Provider
	public static class RsServiceEnablerConfigurer implements DynamicFeature {
		@Override
		public void configure(ResourceInfo resourceInfo, FeatureContext context) {
			Class<?> clazz = resourceInfo.getResourceClass();
			if (!(clazz.isAnnotationPresent(AdminCatagory.class) || clazz.isAnnotationPresent(FeedCatagory.class))) {
				context.register(RsServiceFilter.class, 1);
			}
		}
	}

	public static class RsServiceFilter implements ContainerResponseFilter {

		@Inject
		private SimulatedRSResponse simulatedResponse;

		@Inject
		private SimulatedRSResponseBody simulatedResponseResponseBody;

		@Context
		HttpServletRequest servletRequest;

		@Override
		public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
				throws IOException {

			handleDelay();

			if (simulatedResponse.getCode() != SimulatedFeedResponse.NORMAL_STATUS_CODE) {
				responseContext.setStatus(simulatedResponse.getCode());
			}

			ResponseBodyRepresentation overrideBody = simulatedResponseResponseBody
					.getOverride(servletRequest.getPathInfo());

			if (overrideBody != null) {
				responseContext.setEntity(overrideBody.getBody());
				responseContext.setStatus(overrideBody.getCode());
			}
			
			ResponseRepresentation overrideStatus = simulatedResponse.getCodeForPath(servletRequest.getPathInfo());

			if (overrideStatus != null) {
				responseContext.setStatus(overrideStatus.getCode());
			}
		}

		private void handleDelay() {
			if (simulatedResponse.getDelay() != 0) {
				try {
					Thread.sleep(simulatedResponse.getDelay() * 1_000L);
				} catch (InterruptedException e) {
					throw new WebApplicationException(e);
				}
			}
		}
	}
}
