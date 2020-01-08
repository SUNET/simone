package se.uhr.simone.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;

public class AtomAuthorDAO {

	private JdbcTemplate jdbcTemplate;

	public AtomAuthorDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean exists(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_AUTHOR WHERE ENTRY_ID = ?");
		return jdbcTemplate.queryForRowSet(sql.toString(), atomEntryId.getId()).next();
	}

	public void insert(AtomEntryId id, Person person) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ATOM_AUTHOR (ENTRY_ID, AUTHOR) VALUES (?,?)");
		jdbcTemplate.update(sql.toString(), id.getId(), person.getName());
	}

	public void delete(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ATOM_AUTHOR WHERE ENTRY_ID = ? ");
		jdbcTemplate.update(sql.toString(), atomEntryId.getId());
	}

	public List<Person> findBy(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT AUTHOR FROM ATOM_AUTHOR WHERE ENTRY_ID = ? ");
		return jdbcTemplate.query(sql.toString(), new AtomAuthorRowMapper(), atomEntryId.getId());
	}

	private static class AtomAuthorRowMapper implements RowMapper<Person> {

		@Override
		public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
			return Person.of(rs.getString("AUTHOR"));
		}
	}

}
