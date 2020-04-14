package se.uhr.simone.api.feed;

import java.io.Serializable;
import java.util.Optional;

public class AtomCategory {

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

	public static class AtomCategoryBuilder implements TermStep, BuildStep {

		private Term term;
		private Label label;

		@Override
		public AtomCategory build() {
			return new AtomCategory(this);
		}

		@Override
		public BuildStep withLabel(Label label) {
			this.label = label;
			return this;
		}

		@Override
		public BuildStep withTerm(Term term) {
			this.term = term;
			return this;
		}

	}

	public interface TermStep {

		BuildStep withTerm(Term term);
	}

	public interface BuildStep {

		AtomCategory build();

		BuildStep withLabel(Label label);
	}
}
