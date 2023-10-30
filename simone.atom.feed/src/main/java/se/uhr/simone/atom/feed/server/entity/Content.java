package se.uhr.simone.atom.feed.server.entity;

import java.io.Serializable;
import java.util.Optional;

public class Content implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	private String contentType;

	private Content(ContentBuilder builder) {
		this.value = builder.value;
		this.contentType = builder.contentType;
	}

	public Content() {
	}

	public String getValue() {
		return value;
	}

	public Optional<String> getContentType() {
		return Optional.ofNullable(contentType);
	}

	public static ValueStep builder() {
		return new ContentBuilder();
	}

	public static class ContentBuilder implements ValueStep, BuildStep {

		private String value;
		private String contentType;

		@Override
		public BuildStep withValue(String value) {
			this.value = value;
			return this;
		}

		@Override
		public BuildStep withContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		@Override
		public Content build() {
			return new Content(this);
		}

	}

	public interface ValueStep {

		public BuildStep withValue(String value);

	}

	public interface BuildStep {

		public BuildStep withContentType(String contentType);

		public Content build();

	}
}
