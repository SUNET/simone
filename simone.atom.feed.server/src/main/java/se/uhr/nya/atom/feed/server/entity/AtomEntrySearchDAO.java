package se.uhr.nya.atom.feed.server.entity;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import se.uhr.nya.util.uuid.TimestampUtil;
import se.uhr.nya.util.uuid.UniqueIdentifier;

public class AtomEntrySearchDAO {

	private JdbcTemplate jdbcTemplate;

	public AtomEntrySearchDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<AtomEntry> searchAtomEntriesUR(AtomEntrySearchParams query) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"SELECT SORT_ORDER, ENTRY_ID, ENTRY_CONTENT_TYPE, FEED_ID, SUBMITTED, XMLSERIALIZE(ENTRY_XML AS CLOB(1M)) AS ENTRY_XML ");
		sql.append(" FROM ATOM_ENTRY WHERE 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (!buildSearchWhereClause(query, sql, args)) {
			return Collections.emptyList();
		}
		sql.append(" ORDER BY SORT_ORDER DESC, SUBMITTED DESC, ENTRY_ID DESC FETCH FIRST " + query.getMaxResults()
				+ " ROWS ONLY WITH UR	");

		return jdbcTemplate.query(sql.toString(), args.toArray(), new AtomEntryRowMapper());
	}

	public int searchAtomEntriesCountUR(AtomEntrySearchParams query) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ATOM_ENTRY WHERE 1 = 1 ");
		List<Object> args = new ArrayList<Object>();
		if (!buildSearchWhereClause(query, sql, args)) {
			return 0;
		}
		sql.append(" WITH UR");

		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql.toString(), args.toArray());
		if (rowSet.next()) {
			return rowSet.getInt(1);
		}
		return 0;
	}

	private boolean buildSearchWhereClause(AtomEntrySearchParams query, StringBuilder sql, List<Object> sqlParams) {
		if (query.getEducationOrgId() != null) {
			sql.append(
					" AND XMLEXISTS('$d//*:educationOrgId[text() = $e]' PASSING ENTRY_XML AS \"d\", CAST(? AS VARCHAR(128)) as \"e\") ");
			sqlParams.add(query.getEducationOrgId());
		}
		if (!query.getStudentIds().isEmpty()) {
			List<String> xmlExists = query.getStudentIds()
					.stream()
					.map(id -> "XMLEXISTS('$d//*:studentId[text() = $p]' PASSING ENTRY_XML AS \"d\", CAST(? AS VARCHAR(128)) as \"p\")")
					.collect(toList());
			sql.append(format(" AND (%s) ", join(" OR ", xmlExists)));
			sqlParams.addAll(query.getStudentIds());
		}
		if (query.getCourseId() != null) {
			sql.append(
					" AND XMLEXISTS('$d//*:courseOfferingId[text() = $c]' PASSING ENTRY_XML AS \"d\", CAST(? AS VARCHAR(128)) as \"c\") ");
			sqlParams.add(query.getCourseId());
		}
		if (query.getProgramId() != null) {
			sql.append(" AND XMLEXISTS('$d//*:programId[text() = $p]' PASSING ENTRY_XML AS \"d\", CAST(? AS VARCHAR(128)) as \"p\") ");
			sqlParams.add(query.getProgramId());
		}
		if (query.getFromDate() != null) {
			sql.append(" AND SUBMITTED >= ?");
			sqlParams.add(TimestampUtil.forUTCColumn(query.getFromDate()));
		}
		if (query.getToDate() != null) {
			sql.append(" AND SUBMITTED <= ?");
			sqlParams.add(TimestampUtil.forUTCColumn(query.getToDate()));
		}

		if (query.getBeforeId() != null) {
			Date beforeDate = getSubmitted(query.getBeforeId());
			if (beforeDate == null) {
				return false;
			}
			sql.append(" AND ((SUBMITTED = ? AND ENTRY_ID < ?) OR SUBMITTED < ?) ");
			sqlParams.add(beforeDate);
			sqlParams.add(query.getBeforeId().toByteArray());
			sqlParams.add(beforeDate);
		}
		return true;
	}

	private Date getSubmitted(UniqueIdentifier entryId) {
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT SUBMITTED FROM ATOM_ENTRY WHERE ENTRY_ID = ?", entryId.toByteArray());
		if (rowSet.next()) {
			return rowSet.getDate("SUBMITTED");
		}
		return null;
	}
}
