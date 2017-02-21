package se.uhr.simone.atom.feed.server.entity;

import java.io.Serializable;

public class AtomCategory implements Serializable {

	private final Term term;
	private final Label label;

	private AtomCategory(Term term, Label label) {
		this.term = term;
		this.label = label;
	}

	public static AtomCategory of(Term term, Label label) {
		return new AtomCategory(term, label);
	}

	public Term getTerm() {
		return term;
	}

	public Label getLabel() {
		return label;
	}

	public static class Term implements Serializable {

		private String value;

		private Term(String value) {
			this.value = value;
		}

		public static Term of(String value) {
			return new Term(value);
		}

		public String getValue() {
			return value;
		}
	}

	public static class Label implements Serializable {

		private String value;

		private Label(String value) {
			this.value = value;
		}

		public static Label of(String value) {
			return new Label(value);
		}

		public String getValue() {
			return value;
		}
	}
}