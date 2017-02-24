package se.uhr.simone.admin.rs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "responsebody")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseBodyRepresentation extends ResponseRepresentation {

	@ApiModelProperty(required = true, value = "The body to return, JSON quotes must be escaped")
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
