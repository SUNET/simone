package se.uhr.simone.admin.rs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseRepresentation {

	@ApiModelProperty(required = true, value = "The REST path, i.e. the path sans web context")
	@XmlElement
	private String path;

	@ApiModelProperty(required = true, value = "The HTTP status code")
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
