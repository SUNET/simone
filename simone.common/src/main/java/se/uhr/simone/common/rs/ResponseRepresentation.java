package se.uhr.simone.common.rs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseRepresentation {

	@Schema(required = true, description = "The REST path, i.e. the path sans web context")
	@XmlElement
	private String path;

	@Schema(required = true, description = "The HTTP status code")
	@XmlElement
	private int code;

	public ResponseRepresentation() {
	}

	protected ResponseRepresentation(String path, int code) {
		this.path = path;
		this.code = code;
	}

	public static ResponseRepresentation of(String path, int code) {
		return new ResponseRepresentation(path, code);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
