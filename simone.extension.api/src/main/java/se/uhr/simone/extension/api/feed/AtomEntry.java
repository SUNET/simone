package se.uhr.simone.extension.api.feed;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AtomEntry {

	private AtomEntryId atomEntryId;
	private String xml;
	private Long feedId;
	private Timestamp submitted;
	private List<AtomCategory> atomCategories = new ArrayList<>();

	private AtomEntry(AtomEntryBuilder builder) {
		this.atomEntryId = builder.fAtomEntryId;
		this.xml = builder.fXml;
		this.submitted = builder.fSubmitted;
		this.atomCategories = builder.categories;
		this.feedId = builder.feedId;
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

	public static class AtomEntryBuilder implements AtomEntryIdBuilder, SubmittedBuilder, Build {

		private Timestamp fSubmitted;
		private AtomEntryId fAtomEntryId;
		private String fXml;
		private List<AtomCategory> categories = new ArrayList<>();
		private Long feedId;

		@Override
		public AtomEntry build() {
			return new AtomEntry(this);
		}

		@Override
		public Build withSubmittedNow() {
			this.fSubmitted = new Timestamp(System.currentTimeMillis());
			return this;
		}

		@Override
		public Build withSubmitted(Timestamp submitted) {
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
	}

	public interface AtomEntryIdBuilder {

		public SubmittedBuilder withAtomEntryId(AtomEntryId atomEntryId);
	}

	public interface SubmittedBuilder {

		public Build withSubmittedNow();

		public Build withSubmitted(Timestamp submitted);
	}

	public interface Build {

		public Build withFeedId(Long id);

		public Build withXml(String xml);

		public Build withCategory(AtomCategory atomCategory);

		public Build withCategories(List<AtomCategory> atomCategories);

		public AtomEntry build();
	}
}
