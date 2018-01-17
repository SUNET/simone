package se.uhr.simone.extension.api.feed;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AtomEntry {

	private AtomEntryId atomEntryId;
	private String xml;
	private Long feedId;
	private String title;
	private Timestamp submitted;
	private List<AtomCategory> atomCategories = new ArrayList<>();
	private List<AtomLink> links = new ArrayList<>();

	private AtomEntry(AtomEntryBuilder builder) {
		this.atomEntryId = builder.fAtomEntryId;
		this.xml = builder.fXml;
		this.submitted = builder.fSubmitted;
		this.atomCategories = builder.categories;
		this.feedId = builder.feedId;
		this.title = builder.title;
		this.links = builder.links;
	}

	public static AtomEntryIdBuilder builder() {
		return new AtomEntryBuilder();
	}

	public AtomEntryId getAtomEntryId() {
		return atomEntryId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Long getFeedId() {
		return feedId;
	}

	public void setFeedId(Long feedId) {
		this.feedId = feedId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public List<AtomLink> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public void setLinks(List<AtomLink> links) {
		this.links = new ArrayList<>(links);
	}

	public Timestamp getSubmitted() {
		return submitted;
	}

	public List<AtomCategory> getAtomCategories() {
		return atomCategories;
	}

	public void setAtomCategories(List<AtomCategory> atomCategories) {
		this.atomCategories = atomCategories;
	}

	public static class AtomEntryId implements Serializable {

		private static final long serialVersionUID = 1L;

		private UniqueIdentifier entryId;
		private String contentType;

		private AtomEntryId(UniqueIdentifier id, String contentType) {
			this.entryId = id;
			this.contentType = contentType;
		}

		public static AtomEntryId of(UniqueIdentifier id, String contentType) {
			if (contentType == null) {
				throw new IllegalArgumentException("Content type cannot be null");
			}
			return new AtomEntryId(id, contentType);
		}

		public UniqueIdentifier getId() {
			return entryId;
		}

		public String getContentType() {
			return contentType;
		}
	}

	public static class AtomEntryBuilder implements AtomEntryIdBuilder, SubmittedBuilder, ContentBuilder, Build {

		private Long feedId;
		private AtomEntryId fAtomEntryId;
		private String title;
		private Timestamp fSubmitted;
		private List<AtomLink> links = new ArrayList<>();
		private String fXml;
		private List<AtomCategory> categories = new ArrayList<>();

		@Override
		public AtomEntry build() {
			return new AtomEntry(this);
		}

		@Override
		public ContentBuilder withSubmittedNow() {
			this.fSubmitted = new Timestamp(System.currentTimeMillis());
			return this;
		}

		@Override
		public ContentBuilder withSubmitted(Timestamp submitted) {
			this.fSubmitted = submitted;
			return this;
		}

		@Override
		public SubmittedBuilder withAtomEntryId(AtomEntryId atomEntryId) {
			this.fAtomEntryId = atomEntryId;
			return this;
		}

		@Override
		public Build withXml(String xml) {
			this.fXml = xml;
			return this;
		}

		@Override
		public Build withCategory(AtomCategory atomCategory) {
			this.categories.add(atomCategory);
			return this;
		}

		@Override
		public Build withFeedId(Long id) {
			this.feedId = id;
			return this;
		}

		@Override
		public Build withCategories(List<AtomCategory> atomCategories) {
			this.categories = atomCategories;
			return this;
		}

		@Override
		public Build withTitle(String title) {
			this.title = title;
			return this;
		}

		@Override
		public Build withAlternateLinks(AtomLink... links) {
			List<AtomLink> list = Arrays.asList(links);
			if (list.stream().map(AtomLink::getType).distinct().count() != list.size()) {
				throw new IllegalArgumentException("Alternate links must not have same type");
			}
			this.links.addAll(list);
			return this;
		}

		@Override
		public Build withLinks(AtomLink... links) {
			this.links.addAll(Arrays.asList(links));
			return this;
		}
	}

	public interface AtomEntryIdBuilder {

		public SubmittedBuilder withAtomEntryId(AtomEntryId atomEntryId);
	}

	public interface SubmittedBuilder {

		public ContentBuilder withSubmittedNow();

		public ContentBuilder withSubmitted(Timestamp submitted);
	}

	public interface ContentBuilder {

		Build withXml(String xml);

		Build withAlternateLinks(AtomLink... link);
	}

	public interface Build {

		public Build withFeedId(Long id);

		public Build withCategory(AtomCategory atomCategory);

		public Build withCategories(List<AtomCategory> atomCategories);

		public Build withTitle(String title);

		public Build withLinks(AtomLink... link);

		public AtomEntry build();
	}
}
