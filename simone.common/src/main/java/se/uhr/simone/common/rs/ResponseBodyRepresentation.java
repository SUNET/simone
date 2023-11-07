package se.uhr.simone.common.rs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@XmlRootElement(name = "responsebody")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseBodyRepresentation extends ResponseRepresentation {

	@Schema(required = true, description = "The body to return, JSON quotes must be escaped")
	@XmlElement(required = true)
	private String body;

	public ResponseBodyRepresentation() {
	}

	private ResponseBodyRepresentation(String path, int code, String body) {
		super(path, code);
		this.body = body;
	}

	public static ResponseBodyRepresentation of(String path, int code, String body) {
		return new ResponseBodyRepresentation(path, code, body);
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
