package se.uhr.simone.core.admin.boundary;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

public class FileUploadForm {

	@FormParam("name")
	@PartType(MediaType.TEXT_PLAIN)
	private String name;

	@FormParam("content")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	private InputStream content;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}
}
