package se.uhr.simone.common.feed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "feedevent")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomFeedEventRepresentation {

	@XmlElement
	private String contentType;

	@XmlElement
	private String content;

	@XmlElement
	private List<AtomCategoryRepresentation> categorys = new ArrayList<>();

	public AtomFeedEventRepresentation() {
	}

	private AtomFeedEventRepresentation(String contentType, String content, AtomCategoryRepresentation... categorys) {
		this.contentType = contentType;
		this.content = content;
		this.categorys = new ArrayList<>(Arrays.asList(categorys));
	}

	public static AtomFeedEventRepresentation of(String contentType, String content, AtomCategoryRepresentation... categorys) {
		return new AtomFeedEventRepresentation(contentType, content, categorys);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<AtomCategoryRepresentation> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<AtomCategoryRepresentation> categorys) {
		this.categorys = categorys;
	}
}
