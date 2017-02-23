package se.uhr.simone.core.feed.control;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.uhr.simone.atom.feed.utils.UniqueIdentifier;
import se.uhr.simone.core.admin.control.FeedBlocker;
import se.uhr.simone.core.feed.entity.SimFeedRepository;
import se.uhr.simone.extension.api.feed.AtomCategory;
import se.uhr.simone.extension.api.feed.AtomEntry;
import se.uhr.simone.extension.api.feed.FeedPublisher;
import se.uhr.simone.extension.api.feed.AtomEntry.AtomEntryId;

public class SimulatorFeedPublisher implements FeedPublisher {

	@Inject
	private SimFeedRepository simFeedRepository;

	@Inject
	private FeedBlocker feedBlocker;

	@Override
	public void publish(AtomEntry atomEntry) {
		if (!feedBlocker.isBlocked()) {
			simFeedRepository.saveAtomEntry(convert(atomEntry));
		}
	}

	private se.uhr.simone.atom.feed.server.entity.AtomEntry convert(AtomEntry atomEntry) {

		Long nextSortOrder = simFeedRepository.getNextSortOrder();

		return se.uhr.simone.atom.feed.server.entity.AtomEntry.builder()
				.withAtomEntryId(convert(atomEntry.getAtomEntryId()))
				.withSortOrder(nextSortOrder)
				.withSubmitted(atomEntry.getSubmitted())
				.withXml(atomEntry.getXml())
				.withCategories(convert(atomEntry.getAtomCategories()))
				.build();
	}

	private static se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId convert(AtomEntryId atomEntryId) {
		return se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId.of(UniqueIdentifier.of(atomEntryId.getId().getUuid()), atomEntryId.getContentType());
	}

	private static List<se.uhr.simone.atom.feed.server.entity.AtomCategory> convert(List<AtomCategory> atomCategories) {
		List<se.uhr.simone.atom.feed.server.entity.AtomCategory> res = new ArrayList<>();

		for (AtomCategory category : atomCategories) {
			res.add(se.uhr.simone.atom.feed.server.entity.AtomCategory.of(
					se.uhr.simone.atom.feed.server.entity.AtomCategory.Term.of(category.getTerm().getValue()),
					se.uhr.simone.atom.feed.server.entity.AtomCategory.Label.of(category.getLabel().getValue())));
		}

		return res;
	}
}
