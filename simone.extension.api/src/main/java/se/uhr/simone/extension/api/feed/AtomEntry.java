package se.uhr.simone.extension.api.feed;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AtomEntry {

	private String atomEntryId;
	private Content content;
	private Long feedId;
	private String title;
	private Timestamp submitted;
	private List<AtomCategory> atomCategories = new ArrayList<>();
	private List<AtomLink> links = new ArrayList<>();
	private List<Person> author = new ArrayList<>();
	private Content summary;

	private AtomEntry(AtomEntryBuilder builder) {
		this.atomEntryId = builder.fAtomEntryId;
		this.content = builder.content;
		this.submitted = builder.fSubmitted;
		this.atomCategories = builder.categories;
		this.feedId = builder.feedId;
		this.title = builder.title;
		this.links = builder.links;
		this.author = builder.author;
		this.summary = builder.summary;
	}

	public static AtomEntryIdBuilder builder() {
		return new AtomEntryBuilder();
	}

	public String getAtomEntryId() {
		return atomEntryId;
	}

	public Content getContent() {
		return content;
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

	public List<Person> getAuthors() {
		return author;
	}

	public void setAuthors(List<Person> author) {
		this.author = author;
	}

	public Content getSummary() {
		return summary;
	}

	public static class AtomEntryBuilder implements AtomEntryIdBuilder, SubmittedBuilder, ContentBuilder, Build {

		private Long feedId;
		private String fAtomEntryId;
		private String title;
		private Timestamp fSubmitted;
		private List<AtomLink> links = new ArrayList<>();
		private Content content;
		private List<AtomCategory> categories = new ArrayList<>();
		private List<Person> author = new ArrayList<>();
		private Content summary;

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
		public SubmittedBuilder withAtomEntryId(String atomEntryId) {
			this.fAtomEntryId = atomEntryId;
			return this;
		}

		@Override
		public Build withContent(Content content) {
			this.content = content;
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

		@Override
		public Build withAuthor(List<Person> author) {
			this.author = author;
			return this;
		}

		@Override
		public Build withSummary(Content summary) {
			this.summary = summary;
			return this;
		}

	}

	public interface AtomEntryIdBuilder {

		public SubmittedBuilder withAtomEntryId(String atomEntryId);
	}

	public interface SubmittedBuilder {

		public ContentBuilder withSubmittedNow();

		public ContentBuilder withSubmitted(Timestamp submitted);
	}

	public interface ContentBuilder {

		Build withContent(Content content);

		Build withAlternateLinks(AtomLink... link);
	}

	public interface Build {

		public Build withFeedId(Long id);

		public Build withCategory(AtomCategory atomCategory);

		public Build withCategories(List<AtomCategory> atomCategories);

		public Build withTitle(String title);

		public Build withLinks(AtomLink... link);

		public Build withAuthor(List<Person> authors);

		public Build withSummary(Content summary);

		public AtomEntry build();
	}
}
