package se.uhr.nya.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import se.uhr.nya.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.nya.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.nya.atom.feed.server.entity.AtomEntry.AtomEntryId;

public class AtomCategoryDAO {

	private JdbcTemplate jdbcTemplate;

	public AtomCategoryDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isConnected(AtomCategory atomCategory, AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_CATEGORY WHERE TERM=? AND LABEL=? AND ENTRY_ID=?");
		return jdbcTemplate.queryForRowSet(sql.toString(), atomCategory.getTerm().getValue(), atomCategory.getLabel().getValue(),
				atomEntryId.getId().toByteArray()).next();
	}

	public void connectEntryToCategory(AtomEntryId atomEntryId, AtomCategory atomCategory) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ATOM_CATEGORY (ENTRY_ID, LABEL, TERM) VALUES (?,?,?)");
		jdbcTemplate.update(sql.toString(), atomEntryId.getId().toByteArray(), atomCategory.getLabel().getValue(),
				atomCategory.getTerm().getValue());
	}

	public List<AtomCategory> getCategoriesForAtomEntry(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT LABEL, TERM FROM ATOM_CATEGORY WHERE ENTRY_ID=? ");
		return jdbcTemplate.query(sql.toString(), new AtomCategoryRowMapper(), atomEntryId.getId().toByteArray());
	}

	private static class AtomCategoryRowMapper implements RowMapper<AtomCategory> {

		@Override
		public AtomCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
			return AtomCategory.of(Term.of(rs.getString("TERM")), Label.of(rs.getString("LABEL")));
		}
	}
}