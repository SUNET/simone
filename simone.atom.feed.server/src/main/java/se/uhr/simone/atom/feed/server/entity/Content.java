package se.uhr.simone.atom.feed.server.entity;

public class Content {

	private String summary;

	private Content(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public static Content of(String summary) {
		return new Content(summary);
	}

}
