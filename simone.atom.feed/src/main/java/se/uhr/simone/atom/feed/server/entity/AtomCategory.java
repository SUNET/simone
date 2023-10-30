package se.uhr.simone.atom.feed.server.entity;

import java.io.Serializable;
import java.util.Optional;

public class AtomCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Term term;
	private final Label label;

	private AtomCategory(AtomCategoryBuilder builder) {
		this.term = builder.term;
		this.label = builder.label;
	}

	public Term getTerm() {
		return term;
	}

	public Optional<Label> getLabel() {
		return Optional.ofNullable(label);
	}

	public static class Term implements Serializable {

		private static final long serialVersionUID = 1L;

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

		private static final long serialVersionUID = 1L;

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

	public static TermStep builder() {
		return new AtomCategoryBuilder();
	}

	public static class AtomCategoryBuilder implements TermStep, Build {

		private Term term;
		private Label label;

		@Override
		public AtomCategory build() {
			return new AtomCategory(this);
		}

		@Override
		public Build withLabel(Label label) {
			this.label = label;
			return this;
		}

		@Override
		public Build withTerm(Term term) {
			this.term = term;
			return this;
		}

	}

	public interface TermStep {

		Build withTerm(Term term);
	}

	public interface Build {

		AtomCategory build();

		Build withLabel(Label label);
	}
}