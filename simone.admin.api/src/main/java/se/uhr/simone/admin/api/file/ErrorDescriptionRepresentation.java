package se.uhr.simone.admin.api.file;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorDescriptionRepresentation {

	@XmlElement
	private String fileName;

	@XmlElement
	private int line;

	@XmlElement
	private String message;

	public ErrorDescriptionRepresentation() {
	}

	private ErrorDescriptionRepresentation(String fileName, int line, String message) {
		super();
		this.fileName = fileName;
		this.line = line;
		this.message = message;
	}

	public static ErrorDescriptionRepresentation of(String fileName, int line, String message) {
		return new ErrorDescriptionRepresentation(fileName, line, message);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
