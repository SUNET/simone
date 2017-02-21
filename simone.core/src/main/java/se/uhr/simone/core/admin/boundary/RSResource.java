package se.uhr.simone.core.admin.boundary;

import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import io.swagger.annotations.Api;
import se.uhr.simone.admin.rs.ResponseBodyRepresentation;
import se.uhr.simone.admin.rs.ResponseRepresentation;
import se.uhr.simone.core.admin.control.SimulatedFeedResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponse;
import se.uhr.simone.core.admin.control.SimulatedRSResponseBody;
import se.uhr.simone.core.boundary.AdminCatagory;
import se.uhr.simone.core.boundary.FeedCatagory;

@Api(tags = {"rest admin"})
@AdminCatagory
@Path("admin/rs/response")
public class RSResource {

	@Inject
	private SimulatedRSResponse simulatedResponse;

	@Inject
	private SimulatedRSResponseBody simulatedResponseResponseBody;

	@PUT
	@Path("code/global")
	public Response setGlobalCode(int statusCode) {
		simulatedResponse.setGlobalCode(statusCode);
		return Response.ok().build();
	}

	@DELETE
	@Path("code/global")
	public Response resetGlobalResponseCode() {
		simulatedResponse.setGlobalCode(SimulatedFeedResponse.NORMAL_STATUS_CODE);
		simulatedResponse.resetCodeForAllPaths();
		return Response.ok().build();
	}

	@PUT
	@Path("code/path")
	public Response setResponseCodeForPath(ResponseRepresentation response) {
		simulatedResponse.setCodeForPath(response);
		return Response.ok().build();
	}

	@DELETE
	@Path("code/path")
	public Response resetResponseCodeForPath(String path) {
		simulatedResponse.resetCodeForPath(path);
		return Response.ok().build();
	}

	@PUT
	@Path("body")
	public Response setResponseOverride(ResponseBodyRepresentation response) {
		simulatedResponseResponseBody.setOverride(response.getPath(), response);
		return Response.ok().build();
	}

	@DELETE
	@Path("body")
	public Response setDefaultResponseCode(String path) {
		simulatedResponseResponseBody.deleteOverride(path);
		return Response.ok().build();
	}

	@PUT
	@Path("delay")
	public Response setDelay(int timeInSeconds) {
		simulatedResponse.setDelay(timeInSeconds);
		return Response.ok().build();
	}

	@Provider
	@ServerInterceptor
	public static class RsServiceEnablerInterceptor implements PostProcessInterceptor, PreProcessInterceptor, AcceptedByMethod {

		@Inject
		private SimulatedRSResponse simulatedResponse;

		@Inject
		private SimulatedRSResponseBody simulatedResponseResponseBody;

		@Context
		HttpServletRequest servletRequest;

		@Override
		public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker method) throws Failure, WebApplicationException {
			ResponseBodyRepresentation override = simulatedResponseResponseBody.getOverride(servletRequest.getPathInfo());

			if (override != null) {
				return new ServerResponse(override.getBody(), override.getCode(), new Headers<Object>());
			} else {
				return null;
			}
		}

		@Override
		public void postProcess(ServerResponse response) {
			if (simulatedResponse.getCode() != SimulatedFeedResponse.NORMAL_STATUS_CODE) {
				response.setStatus(simulatedResponse.getCode());
			}

			ResponseRepresentation override = simulatedResponse.getCodeForPath(servletRequest.getPathInfo());

			if (override != null) {
				response.setStatus(override.getCode());
			}

			handleDelay();
		}

		@Override
		public boolean accept(Class declaring, Method method) {
			return !(declaring.isAnnotationPresent(AdminCatagory.class) || declaring.isAnnotationPresent(FeedCatagory.class));
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
