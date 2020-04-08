package se.uhr.simone.core.feed.control;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.transaction.Transactional;

import se.uhr.simone.core.admin.control.FeedBlocker;
import se.uhr.simone.core.feed.entity.SimFeedRepository;
import se.uhr.simone.extension.api.feed.AtomCategory;
import se.uhr.simone.extension.api.feed.AtomEntry;
import se.uhr.simone.extension.api.feed.AtomLink;
import se.uhr.simone.extension.api.feed.Content;
import se.uhr.simone.extension.api.feed.FeedPublisher;
import se.uhr.simone.extension.api.feed.Person;

@Dependent
public class SimulatorFeedPublisher implements FeedPublisher {

	@Inject
	SimFeedRepository simFeedRepository;

	@Inject
	FeedBlocker feedBlocker;

	@Transactional
	@Override
	public void publish(AtomEntry atomEntry) {
		if (!feedBlocker.isBlocked()) {
			simFeedRepository.saveAtomEntry(convert(atomEntry));
		}
	}

	private se.uhr.simone.atom.feed.server.entity.AtomEntry convert(AtomEntry atomEntry) {

		Long nextSortOrder = simFeedRepository.getNextSortOrder();

		return se.uhr.simone.atom.feed.server.entity.AtomEntry.builder()
				.withAtomEntryId(atomEntry.getAtomEntryId())
				.withSortOrder(nextSortOrder)
				.withSubmitted(atomEntry.getSubmitted())
				.withContent(convertContent(atomEntry.getContent()))
				.withCategories(convert(atomEntry.getAtomCategories()))
				.withTitle(atomEntry.getTitle())
				.withLinks(convertLinks(atomEntry.getLinks()))
				.withSummary(convertContent(atomEntry.getSummary()))
				.withAuthor(convertPersons(atomEntry.getAuthors()))
				.build();
	}

	private static se.uhr.simone.atom.feed.server.entity.Content convertContent(Content content) {

		return content == null ? new se.uhr.simone.atom.feed.server.entity.Content()
				: se.uhr.simone.atom.feed.server.entity.Content.builder()
						.withValue(content.getValue())
						.withContentType(content.getContentType().orElse(null))
						.build();
	}

	private List<se.uhr.simone.atom.feed.server.entity.Person> convertPersons(List<Person> authors) {
		return authors.stream().map(SimulatorFeedPublisher::convertPersons).collect(Collectors.toList());
	}

	private static se.uhr.simone.atom.feed.server.entity.Person convertPersons(Person author) {
		return se.uhr.simone.atom.feed.server.entity.Person.of(author.getName());
	}

	private static se.uhr.simone.atom.feed.server.entity.AtomLink convert(AtomLink link) {
		return se.uhr.simone.atom.feed.server.entity.AtomLink.builder()
				.withRel(link.getRel())
				.withHref(link.getHref())
				.withType(link.getType())
				.build();
	}

	private static List<se.uhr.simone.atom.feed.server.entity.AtomLink> convertLinks(List<AtomLink> links) {
		return links.stream().map(SimulatorFeedPublisher::convert).collect(Collectors.toList());
	}

	private static List<se.uhr.simone.atom.feed.server.entity.AtomCategory> convert(List<AtomCategory> atomCategories) {
		List<se.uhr.simone.atom.feed.server.entity.AtomCategory> res = new ArrayList<>();

		for (AtomCategory category : atomCategories) {
			se.uhr.simone.atom.feed.server.entity.AtomCategory.Build builder =
					se.uhr.simone.atom.feed.server.entity.AtomCategory.builder()
							.withTerm(se.uhr.simone.atom.feed.server.entity.AtomCategory.Term.of(category.getTerm().getValue()));
			category.getLabel()
					.ifPresent(
							label -> builder.withLabel(se.uhr.simone.atom.feed.server.entity.AtomCategory.Label.of(label.getValue())));
			res.add(builder.build());
		}

		return res;
	}
}
