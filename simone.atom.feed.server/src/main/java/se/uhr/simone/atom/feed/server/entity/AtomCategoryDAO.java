package se.uhr.simone.atom.feed.server.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import se.uhr.simone.atom.feed.server.entity.AtomCategory.Build;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Label;
import se.uhr.simone.atom.feed.server.entity.AtomCategory.Term;
import se.uhr.simone.atom.feed.server.entity.AtomEntry.AtomEntryId;

public class AtomCategoryDAO {

	private JdbcTemplate jdbcTemplate;

	public AtomCategoryDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public boolean isConnected(AtomCategory atomCategory, AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT 1 FROM ATOM_CATEGORY WHERE TERM=? AND ENTRY_ID=?");
		return jdbcTemplate.queryForRowSet(sql.toString(), atomCategory.getTerm().getValue(), atomEntryId.getId()).next();
	}

	public void connectEntryToCategory(AtomEntryId atomEntryId, AtomCategory atomCategory) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ATOM_CATEGORY (ENTRY_ID, TERM, LABEL) VALUES (?,?,?)");
		jdbcTemplate.update(sql.toString(), atomEntryId.getId(), atomCategory.getTerm().getValue(),
				atomCategory.getLabel().map(Label::getValue).orElse(null));
	}

	public List<AtomCategory> getCategoriesForAtomEntry(AtomEntryId atomEntryId) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT TERM, LABEL FROM ATOM_CATEGORY WHERE ENTRY_ID=? ");
		return jdbcTemplate.query(sql.toString(), new AtomCategoryRowMapper(), atomEntryId.getId());
	}

	private static class AtomCategoryRowMapper implements RowMapper<AtomCategory> {

		@Override
		public AtomCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
			String labelValue = rs.getString("LABEL");
			Build builder = AtomCategory.builder().withTerm(Term.of(rs.getString("TERM")));
			if (Objects.nonNull(labelValue)) {
				builder.withLabel(Label.of(labelValue));
			}
			return builder.build();
		}
	}
}