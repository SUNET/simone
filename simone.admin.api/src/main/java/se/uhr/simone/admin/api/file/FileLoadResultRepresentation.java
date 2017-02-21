package se.uhr.simone.admin.api.file;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "fileloadresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileLoadResultRepresentation {

	@XmlElement
	private List<String> eventIdList = new ArrayList<>();

	@XmlElement
	private String errorLog;

	public FileLoadResultRepresentation() {
	}

	private FileLoadResultRepresentation(List<String> eventIdList, String errorLog) {
		super();
		this.eventIdList = eventIdList;
		this.errorLog = errorLog;
	}

	public static FileLoadResultRepresentation of(List<String> eventId, String errorLog) {
		return new FileLoadResultRepresentation(eventId, errorLog);
	}

	public List<String> getEventIdList() {
		return eventIdList;
	}

	public void setEventIdList(List<String> eventIdList) {
		this.eventIdList = eventIdList;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}
}
