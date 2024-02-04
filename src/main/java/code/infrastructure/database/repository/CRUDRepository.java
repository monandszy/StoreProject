package code.infrastructure.database.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CRUDRepository<T> {

   SimpleDriverDataSource simpleDriverDataSource;

   public Integer add(Object table, MapSqlParameterSource params) {
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName(table.toString());
      simpleJdbcInsert.setGeneratedKeyName("id");
      simpleJdbcInsert.setSchemaName("zajavka_store");
      return (Integer) simpleJdbcInsert.executeAndReturnKey(params);
   }

   public List<T> get(Object table, Object where, Object equalsWhat, RowMapper<T> rowMapper) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "SELECT * FROM zajavka_store.%s WHERE %s = ?".formatted(table, where);
      return jdbcTemplate.query(sql, rowMapper, equalsWhat);
   }

   public void updateById(String sql, MapSqlParameterSource parameters) {
      NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(simpleDriverDataSource);
      namedParameterJdbcTemplate.update(sql, parameters);
   }

   public void delete(Object table, Object where, Object equalsWhat) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.%s WHERE %s = ?".formatted(table, where);
      jdbcTemplate.update(sql, equalsWhat);
   }
}