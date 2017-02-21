package se.uhr.simone.atom.feed.server.entity;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import se.uhr.simone.atom.feed.utils.UniqueIdentifier;

public class AtomEntrySearchParams {

	private String personNumber;
	private UniqueIdentifier beforeId;
	private String educationOrgId;
	private String courseId;
	private String programId;
	private Date fromDate;
	private Date toDate;
	private int maxResults;
	private Set<String> studentIds;

	public AtomEntrySearchParams(String personNumber, String educationOrgId, String courseId, String programId, Date fromDate,
			Date toDate, UniqueIdentifier beforeId, int maxResults) {
		super();
		this.personNumber = personNumber;
		this.educationOrgId = educationOrgId;
		this.courseId = courseId;
		this.programId = programId;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.maxResults = maxResults;
		this.beforeId = beforeId;
		this.studentIds = new LinkedHashSet<>();
	}

	public String getPersonNumber() {
		return personNumber;
	}

	public String getEducationOrgId() {
		return educationOrgId;
	}

	public String getCourseId() {
		return courseId;
	}

	public String getProgramId() {
		return programId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public Set<String> getStudentIds() {
		return studentIds;
	}

	public UniqueIdentifier getBeforeId() {
		return beforeId;
	}

	public void addStudentId(String studentId) {
		this.studentIds.add(studentId);
	}
}
