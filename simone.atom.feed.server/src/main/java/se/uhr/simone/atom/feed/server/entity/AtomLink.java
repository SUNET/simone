package se.uhr.simone.atom.feed.server.entity;

import java.io.Serializable;
import java.util.Objects;

public class AtomLink implements Serializable {

	private static final String ALTERNATE = "alternate";

	private static final long serialVersionUID = 1L;

	private String rel;
	private String href;
	private String type;

	private AtomLink(AtomLinkBuilder builder) {
		rel = builder.rel;
		href = builder.href;
		type = builder.type;
	}

	public String getRel() {
		return rel;
	}

	public String getHref() {
		return href;
	}

	public String getType() {
		return type;
	}

	public boolean isAlternate() {
		return getRel().equals(ALTERNATE);
	}

	public static RelBuild builder() {
		return new AtomLinkBuilder();
	}

	public static class AtomLinkBuilder implements RelBuild, HrefBuild, Build {

		private String rel;
		private String href;
		private String type;

		@Override
		public AtomLink build() {
			return new AtomLink(this);
		}

		@Override
		public HrefBuild withRelAlternate() {
			this.rel = ALTERNATE;
			return this;
		}

		@Override
		public HrefBuild withRel(String rel) {
			if (isNullOrEmpty(rel)) {
				throw new IllegalArgumentException("Rel must have a value");
			}
			this.rel = rel;
			return this;
		}

		private boolean isNullOrEmpty(String rel) {
			return Objects.isNull(rel) || rel.isEmpty();
		}

		@Override
		public Build withType(String type) {
			this.type = type;
			return this;
		}

		@Override
		public Build withHref(String href) {
			if (isNullOrEmpty(href)) {
				throw new IllegalArgumentException("Href must have a value");
			}
			this.href = href;
			return this;
		}

	}

	public interface RelBuild {

		HrefBuild withRelAlternate();

		HrefBuild withRel(String rel);
	}

	public interface HrefBuild {

		Build withHref(String string);
	}

	public interface Build {

		AtomLink build();

		Build withType(String type);

	}
}
