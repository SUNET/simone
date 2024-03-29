package se.uhr.simone.common.feed;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "category")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomCategoryRepresentation {

	@XmlElement
	private String term;

	@XmlElement
	private String label;

	public AtomCategoryRepresentation() {
	}

	private AtomCategoryRepresentation(String term, String label) {
		this.term = term;
		this.label = label;
	}

	public static AtomCategoryRepresentation of(String term, String label) {
		return new AtomCategoryRepresentation(term, label);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}