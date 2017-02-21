package se.uhr.simone.atom.feed.server.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AtomFeed implements Serializable {

	public static final long MAX_NUMBER_OF_ENTRIES_IN_FEED = 100;

	private long id;
	private Long nextFeedId;
	private Long previousFeedId;
	private String xml;
	private List<AtomEntry> entries = new ArrayList();

	public AtomFeed(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getNextFeedId() {
		return nextFeedId;
	}

	public void setNextFeedId(Long nextFeedId) {
		this.nextFeedId = nextFeedId;
	}

	public Long getPreviousFeedId() {
		return previousFeedId;
	}

	public void setPreviousFeedId(Long previousFeedId) {
		this.previousFeedId = previousFeedId;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public List<AtomEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<AtomEntry> entries) {
		this.entries = entries;
	}

	public boolean addEntry(AtomEntry atomEntry) {
		if (isFull()) {
			return false;
		}
		getEntries().add(atomEntry);
		return true;
	}

	public boolean isFull() {
		return entries.size() >= AtomFeed.MAX_NUMBER_OF_ENTRIES_IN_FEED;
	}

	public AtomFeed createNextAtomFeed() {
		long nextId = this.id + 1;
		AtomFeed atomFeed = new AtomFeed(nextId);
		atomFeed.setPreviousFeedId(this.id);
		this.nextFeedId = nextId;

		return atomFeed;
	}
}
