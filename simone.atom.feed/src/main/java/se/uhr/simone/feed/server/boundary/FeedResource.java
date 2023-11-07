package se.uhr.simone.feed.server.boundary;

import java.net.URI;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import se.uhr.simone.atom.feed.server.control.FeedConverter;
import se.uhr.simone.atom.feed.server.entity.AtomFeed;
import se.uhr.simone.atom.feed.server.entity.AbstractFeedRepository;

@Produces({ MediaType.APPLICATION_ATOM_XML + "; charset=utf-8", MediaType.APPLICATION_ATOM_XML })
public abstract class FeedResource {

	FeedConverter feedConverter;

	AbstractFeedRepository feedRepository;

	protected FeedResource(FeedConverter feedConverter, AbstractFeedRepository feedRepository) {
		this.feedConverter = feedConverter;
		this.feedRepository = feedRepository;
	}

	/**
	 * Returns the recent {@link AtomFeed} as XML if it does exist, otherwise null.
	 * 
	 * @param baseUri The feed URI
	 * @return A HTTP result
	 */
	public Response getRecentFeedXml(URI baseUri) {
		String feedXml = replaceTemplateValues(feedConverter.convertFeedToXml(feedRepository.getRecentFeed(), baseUri));
		return Response.ok().entity(feedXml).build();
	}

	/**
	 * Returns the {@link AtomFeed} as XML if it does exist, otherwise null.
	 * 
	 * @param id The feed to reed.
	 * @param baseUri The feed URI
	 * @return A HTTP result
	 */
	public Response getFeedXml(long id, URI baseUri) {
		AtomFeed atomFeed = feedRepository.getFeedById(id);
		if (atomFeed == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		String feedXml = atomFeed.getXml();

		if (feedXml == null) {
			feedXml = feedConverter.convertFeedToXml(atomFeed, baseUri);
		}
		String xml = replaceTemplateValues(feedXml);
		return Response.ok().entity(xml).build();
	}

	protected abstract String replaceTemplateValues(String xml);

	protected String replaceValues(String xml, Set<Entry<String, String>> templateMapping) {
		if (templateMapping == null) {
			return xml;
		}
		String tempXml = xml;
		for (Entry<String, String> entry : templateMapping) {
			tempXml = tempXml.replaceAll(entry.getKey(), entry.getValue());
		}
		return tempXml;
	}
}
