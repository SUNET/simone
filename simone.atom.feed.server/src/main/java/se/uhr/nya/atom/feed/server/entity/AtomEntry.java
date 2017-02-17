package se.uhr.nya.atom.feed.server.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import se.uhr.nya.util.uuid.UniqueIdentifier;

public class AtomEntry implements Serializable {

	private AtomEntryId atomEntryId;
	private String xml;

	private Long feedId;
	private Long sortOrder;
	private Timestamp submitted;
	private List<AtomCategory> atomCategories = new ArrayList<>();

	private AtomEntry(AtomEntryBuilder builder) {
		this.atomEntryId = builder.atomEntryId;
		this.xml = builder.xml;
		this.submitted = builder.submitted;
		this.atomCategories = builder.categories;
		this.feedId = builder.feedId;
		this.sortOrder = builder.sortOrder;
	}

	public static AtomEntryIdBuilder builder() {
		return new AtomEntryBuilder();
	}

	public Long getSortOrder() {
		return sortOrder;
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

	public static class AtomEntryBuilder implements AtomEntryIdBuilder, AtomEntrySortOrderBuilder, SubmittedBuilder, Build {

		private Long sortOrder;
		private Timestamp submitted;
		private AtomEntryId atomEntryId;
		private String xml;
		private List<AtomCategory> categories = new ArrayList();
		private Long feedId;

		@Override
		public AtomEntry build() {
			return new AtomEntry(this);
		}

		@Override
		public Build withSubmittedNow() {
			this.submitted = new Timestamp(DateTime.now().getMillis());
			return this;
		}

		@Override
		public Build withSubmitted(@SuppressWarnings("hiding") Timestamp submitted) {
			this.submitted = submitted;
			return this;
		}

		@Override
		public AtomEntrySortOrderBuilder withAtomEntryId(@SuppressWarnings("hiding") AtomEntryId atomEntryId) {
			this.atomEntryId = atomEntryId;
			return this;
		}

		@Override
		public Build withXml(@SuppressWarnings("hiding") String xml) {
			this.xml = xml;
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
		public SubmittedBuilder withSortOrder(Long order) {
			this.sortOrder = order;
			return this;
		}
	}

	public interface AtomEntryIdBuilder {

		public AtomEntrySortOrderBuilder withAtomEntryId(AtomEntryId atomEntryId);
	}

	public interface AtomEntrySortOrderBuilder {

		public SubmittedBuilder withSortOrder(Long atomEntryEventId);
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