package se.uhr.simone.atom.feed.server.control;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.impl.Atom10Generator;

import se.uhr.simone.atom.feed.server.entity.AtomCategory;
import se.uhr.simone.atom.feed.server.entity.AtomEntry;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;

public class FeedConverter {

	/**
	 * Convert the {@link AtomFeed} to xml.
	 * 
	 * @param atomFeed The {@link AtomFeed} to convert.
	 * @param baseUri The {@link URI} used for links to previous and next feed.
	 * @return xml in {@link MediaType#APPLICATION_ATOM_XML} format.
	 */
	public String convertFeedToXml(AtomFeed atomFeed, URI baseUri) {
		Feed feed = convertFeed(atomFeed, baseUri);
		try {
			WireFeedGenerator gen = new Atom10Generator();
			Format format = Format.getRawFormat();
			format.setOmitDeclaration(false);
			format.setOmitEncoding(false);
			XMLOutputter outputter = new XMLOutputter(format);
			return outputter.outputString(gen.generate(feed));
		} catch (FeedException e) {
			throw new IllegalStateException("Failed to convert feed to xml: " + e.getMessage(), e);
		}
	}

	private Feed convertFeed(AtomFeed atomFeed, URI baseUri) {
		Feed feed = new Feed();
		feed.setId("urn:id:" + atomFeed.getId());
		feed.setFeedType("atom_1.0");
		Content title = new Content();
		title.setType(Content.TEXT);
		title.setValue("Title");
		feed.setTitleEx(title);
		feed.setUpdated(new Date());

		feed.setEntries(convertEntries(atomFeed.getEntries()));

		List<Link> links = new ArrayList<>();

		if (atomFeed.getPreviousFeedId() != null) {
			links.add(LinkBuilder.previousArchive(baseUri, Long.toString(atomFeed.getPreviousFeedId())));
		}

		if (atomFeed.getNextFeedId() != null) {
			links.add(LinkBuilder.nextArchive(baseUri, Long.toString(atomFeed.getNextFeedId())));
			links.add(LinkBuilder.self(baseUri, Long.toString(atomFeed.getId())));
		} else {
			links.add(LinkBuilder.recent(baseUri));
			links.add(LinkBuilder.via(baseUri, Long.toString(atomFeed.getId())));
		}

		feed.setOtherLinks(links);

		return feed;
	}

	private List<Entry> convertEntries(List<AtomEntry> entries) {

		List<Entry> convertedEntries = new ArrayList<>();

		for (AtomEntry entry : entries) {
			Entry convertedEntry = new Entry();
			convertedEntry.setId(entry.getAtomEntryId().getId().getValue());
			convertedEntry.setUpdated(entry.getSubmitted());
			convertedEntry.setCategories(getConvertedCategories(entry));
			convertedEntry.setContents(Arrays.asList(getContent(entry)));
			convertedEntries.add(convertedEntry);
		}

		return convertedEntries;
	}

	private List<Category> getConvertedCategories(AtomEntry entry) {
		List<Category> convertedCategories = new ArrayList();

		for (AtomCategory atomCategory : entry.getAtomCategories()) {
			Category category = new Category();
			category.setLabel(atomCategory.getLabel().getValue());
			category.setTerm(atomCategory.getTerm().getValue());
			convertedCategories.add(category);
		}

		return convertedCategories;
	}

	public Content getContent(AtomEntry entry) {
		Content content = new Content();
		content.setType(entry.getAtomEntryId().getContentType());
		content.setValue(entry.getXml());
		return content;
	}

	public static class LinkBuilder {

		public static Link previousArchive(URI path, String id) {
			return build("prev-archive", path, id);
		}

		public static Link nextArchive(URI path, String id) {
			return build("next-archive", path, id);
		}

		public static Link self(URI path, String id) {
			return build("self", path, id);
		}

		public static Link recent(URI path) {
			return build("self", path, "recent");
		}

		public static Link via(URI path, String id) {
			return build("via", path, id);
		}

		private static Link build(String rel, URI path, String id) {
			Link link = new Link();

			link.setRel(rel);
			link.setType(MediaType.APPLICATION_ATOM_XML);
			link.setHref(UriBuilder.fromUri(path).segment(id).build().toString());
			return link;
		}
	}
}