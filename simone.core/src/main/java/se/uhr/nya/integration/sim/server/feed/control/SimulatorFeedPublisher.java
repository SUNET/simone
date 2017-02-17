package se.uhr.nya.integration.sim.server.feed.control;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import se.uhr.nya.integration.sim.extension.api.feed.AtomCategory;
import se.uhr.nya.integration.sim.extension.api.feed.AtomEntry;
import se.uhr.nya.integration.sim.extension.api.feed.AtomEntry.AtomEntryId;
import se.uhr.nya.integration.sim.extension.api.feed.FeedPublisher;
import se.uhr.nya.integration.sim.server.admin.control.FeedBlocker;
import se.uhr.nya.integration.sim.server.feed.entity.SimFeedRepository;
import se.uhr.nya.util.uuid.UniqueIdentifier;

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

	private se.uhr.nya.atom.feed.server.entity.AtomEntry convert(AtomEntry atomEntry) {

		Long nextSortOrder = simFeedRepository.getNextSortOrder();

		return se.uhr.nya.atom.feed.server.entity.AtomEntry.builder()
				.withAtomEntryId(convert(atomEntry.getAtomEntryId()))
				.withSortOrder(nextSortOrder)
				.withSubmitted(atomEntry.getSubmitted())
				.withXml(atomEntry.getXml())
				.withCategories(convert(atomEntry.getAtomCategories()))
				.build();
	}

	private static se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId convert(AtomEntryId atomEntryId) {
		return se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId.of(UniqueIdentifier.of(atomEntryId.getId().getUuid()), atomEntryId.getContentType());
	}

	private static List<se.uhr.nya.atom.feed.server.entity.AtomCategory> convert(List<AtomCategory> atomCategories) {
		List<se.uhr.nya.atom.feed.server.entity.AtomCategory> res = new ArrayList<>();

		for (AtomCategory category : atomCategories) {
			res.add(se.uhr.nya.atom.feed.server.entity.AtomCategory.of(
					se.uhr.nya.atom.feed.server.entity.AtomCategory.Term.of(category.getTerm().getValue()),
					se.uhr.nya.atom.feed.server.entity.AtomCategory.Label.of(category.getLabel().getValue())));
		}

		return res;
	}
}
