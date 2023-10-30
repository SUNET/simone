package se.uhr.simone.common.client;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class SimoneAdminClient {

	private final WebTarget target;

	private SimoneAdminClient(Builder builder) {
		this.target = builder.client.target(builder.baseUri);
	}

	public static IClientStage builder() {
		return new Builder();
	}

	public interface IClientStage {

		IBaseUriStage withClient(Client client);
	}

	public interface IBaseUriStage {

		IBuildStage withBaseUri(URI baseUri);
	}

	public interface IBuildStage {

		SimoneAdminClient build();
	}

	public static final class Builder implements IClientStage, IBaseUriStage, IBuildStage {

		private Client client;
		private URI baseUri;

		private Builder() {
		}

		@Override
		public IBaseUriStage withClient(Client client) {
			this.client = client;
			return this;
		}

		@Override
		public IBuildStage withBaseUri(URI baseUri) {
			this.baseUri = baseUri;
			return this;
		}

		@Override
		public SimoneAdminClient build() {
			return new SimoneAdminClient(this);
		}
	}

	public DatabaseAdmin database() {
		return new DatabaseAdmin(target);
	}

	public FeedAdmin feed() {
		return new FeedAdmin(target);
	}

	public RestAdmin rest() {
		return new RestAdmin(target);
	}
}
