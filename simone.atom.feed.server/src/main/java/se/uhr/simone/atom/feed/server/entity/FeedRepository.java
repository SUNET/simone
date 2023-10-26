package se.uhr.simone.atom.feed.server.entity;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class FeedRepository {

	private final AtomFeedDAO atomFeedDAO;
	private final AtomEntryDAO atomEntryDAO;
	private final AtomCategoryDAO atomCategoryDAO;
	private final AtomLinkDAO atomLinkDAO;
	private final AtomAuthorDAO atomAuthorDAO;

	protected FeedRepository(DataSource dataSource) {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		atomFeedDAO = createAtomFeedDAO(jdbcTemplate);
		atomEntryDAO = createAtomEntryDAO(jdbcTemplate);
		atomCategoryDAO = createAtomCategoryDAO(jdbcTemplate);
		atomLinkDAO = createAtomLinkDAO(jdbcTemplate);
		atomAuthorDAO = createAtomAuthorDAO(jdbcTemplate);
	}

	FeedRepository(AtomFeedDAO atomFeedDAO, AtomEntryDAO atomEntryDAO, AtomCategoryDAO atomCategoryDAO, AtomLinkDAO atomLinkDAO, AtomAuthorDAO atomAuthorDAO) {
		this.atomFeedDAO = atomFeedDAO;
		this.atomEntryDAO = atomEntryDAO;
		this.atomCategoryDAO = atomCategoryDAO;
		this.atomLinkDAO = atomLinkDAO;
		this.atomAuthorDAO = atomAuthorDAO;
	}

	public abstract void clear();

	protected AtomFeedDAO createAtomFeedDAO(JdbcTemplate jdbcTemplate) {
		return new AtomFeedDAO(jdbcTemplate);
	}

	protected AtomEntryDAO createAtomEntryDAO(JdbcTemplate jdbcTemplate) {
		return new AtomEntryDAO(jdbcTemplate);
	}

	protected AtomCategoryDAO createAtomCategoryDAO(JdbcTemplate jdbcTemplate) {
		return new AtomCategoryDAO(jdbcTemplate);
	}

	protected AtomLinkDAO createAtomLinkDAO(JdbcTemplate jdbcTemplate) {
		return new AtomLinkDAO(jdbcTemplate);
	}

	protected AtomAuthorDAO createAtomAuthorDAO(JdbcTemplate jdbcTemplate) {
		return new AtomAuthorDAO(jdbcTemplate);
	}

	public void saveAtomFeed(AtomFeed atomFeed) {
		if (atomFeedDAO.exists(atomFeed.getId())) {
			atomFeedDAO.update(atomFeed);
		} else {
			atomFeedDAO.insert(atomFeed);
		}

		for (AtomEntry atomEntry : atomFeed.getEntries()) {
			atomEntry.setFeedId(atomFeed.getId());
			saveAtomEntry(atomEntry);
		}
	}

	public void saveAtomEntry(AtomEntry atomEntry) {
		if (atomEntryDAO.exists(atomEntry.getAtomEntryId())) {
			atomEntryDAO.update(atomEntry);
			atomLinkDAO.delete(atomEntry.getAtomEntryId());
			atomAuthorDAO.delete(atomEntry.getAtomEntryId());
		} else {
			atomEntryDAO.insert(atomEntry);
		}

		for (AtomCategory atomCategory : atomEntry.getAtomCategories()) {
			if (!atomCategoryDAO.isConnected(atomCategory, atomEntry.getAtomEntryId())) {
				atomCategoryDAO.connectEntryToCategory(atomEntry.getAtomEntryId(), atomCategory);
			}
		}

		for (AtomLink atomLink : atomEntry.getAtomLinks()) {
			atomLinkDAO.insert(atomEntry.getAtomEntryId(), atomLink);
		}

		for (Person author : atomEntry.getAuthors()) {
			atomAuthorDAO.insert(atomEntry.getAtomEntryId(), author);
		}

	}

	public void saveAtomFeedXml(long feedId, String xml) {
		atomFeedDAO.saveAtomFeedXml(feedId, xml);
	}

	public AtomFeed getFeedById(long id) {
		AtomFeed atomFeed = null;
		try {
			atomFeed = atomFeedDAO.fetchBy(id);
		} catch (EmptyResultDataAccessException e) {
			return atomFeed;
		}
		atomFeed.setEntries(getEntriesForFeed(atomFeed));
		return atomFeed;
	}

	/**
	 * Will return the "recent" {@link AtomFeed} or null if not existing.
	 * 
	 * @return "recent" {@link AtomFeed} or null if not existing.
	 */
	public AtomFeed getRecentFeed() {
		AtomFeed recent = atomFeedDAO.fetchRecent();
		recent.setEntries(getEntriesForFeed(recent));
		return recent;
	}

	/**
	 * Will return all {@link AtomEntry}s that are not connected to a {@link AtomFeed}.
	 * 
	 * @return all {@link AtomEntry}s that are not connected to a {@link AtomFeed}
	 */
	public List<AtomEntry> getEntriesNotConnectedToFeed() {
		List<AtomEntry> entriesNotConnectedToFeed = atomEntryDAO.getEntriesNotConnectedToFeed();
		for (AtomEntry atomEntry : entriesNotConnectedToFeed) {
			atomEntry.setAtomCategories(atomCategoryDAO.getCategoriesForAtomEntry(atomEntry.getAtomEntryId()));
			atomEntry.setAtomLink(atomLinkDAO.findBy(atomEntry.getAtomEntryId()));
			atomEntry.setAuthors(atomAuthorDAO.findBy(atomEntry.getAtomEntryId()));
		}
		return entriesNotConnectedToFeed;
	}

	/**
	 * Will return a list of {@link AtomFeed}s that has no xml.
	 * 
	 * @return all {@link AtomFeed}s that has no xml.
	 */
	public List<AtomFeed> getFeedsWithoutXml() {
		List<AtomFeed> feedsWithoutXml = atomFeedDAO.getFeedsWithoutXml();
		for (AtomFeed atomFeed : feedsWithoutXml) {
			atomFeed.setEntries(getEntriesForFeed(atomFeed));
		}
		return feedsWithoutXml;
	}

	public String getLatestEntryIdForCategory(AtomCategory category) {
		try {
			return atomEntryDAO.getLatestEntryIdForCategory(category);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private List<AtomEntry> getEntriesForFeed(AtomFeed atomFeed) {
		List<AtomEntry> atomEntries = atomEntryDAO.getAtomEntriesForFeed(atomFeed.getId());
		for (AtomEntry atomEntry : atomEntries) {
			atomEntry.setAtomCategories(atomCategoryDAO.getCategoriesForAtomEntry(atomEntry.getAtomEntryId()));
			atomEntry.setAtomLink(atomLinkDAO.findBy(atomEntry.getAtomEntryId()));
			atomEntry.setAuthors(atomAuthorDAO.findBy(atomEntry.getAtomEntryId()));
		}
		return atomEntries;
	}
}
