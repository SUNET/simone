package se.uhr.nya.integration.sim.admin.rs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "responsebody")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResponseBodyRepresentation extends ResponseRepresentation {

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
