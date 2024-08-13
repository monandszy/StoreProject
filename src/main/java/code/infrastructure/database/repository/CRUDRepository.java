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

@Repository
@AllArgsConstructor
public class CRUDRepository<T> {

  SimpleDriverDataSource simpleDriverDataSource;

  public Integer add(Object table, MapSqlParameterSource params) {
    SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
    simpleJdbcInsert.setTableName(table.toString());
    simpleJdbcInsert.setGeneratedKeyName("id");
    simpleJdbcInsert.setSchemaName("spring_store");
    return (Integer) simpleJdbcInsert.executeAndReturnKey(params);
  }

  public List<T> get(Object table, Object where, Object sign, Object what, RowMapper<T> rowMapper) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    String sql = "SELECT * FROM spring_store.%s WHERE %s %s ?".formatted(table, where, sign);
    return jdbcTemplate.query(sql, rowMapper, what);
  }

  public List<T> get(Object table, Object where, Object equalsWhat, RowMapper<T> rowMapper) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    String sql = "SELECT * FROM spring_store.%s WHERE %s = ?".formatted(table, where);
    return jdbcTemplate.query(sql, rowMapper, equalsWhat);
  }

  public void updateById(String sql, MapSqlParameterSource parameters) {
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(simpleDriverDataSource);
    namedParameterJdbcTemplate.update(sql, parameters);
  }

  public void delete(Object table, Object where, Object sign, Object what) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    String sql = "DELETE FROM spring_store.%s WHERE %s %s ?".formatted(table, where, sign);
    jdbcTemplate.update(sql, what);
  }

  public void delete(Object table, Object where, Object equalsWhat) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    String sql = "DELETE FROM spring_store.%s WHERE %s = ?".formatted(table, where);
    jdbcTemplate.update(sql, equalsWhat);
  }

  public void deleteInList(Object table, Object where, List<String> list) {
    if (list.isEmpty()) return;
    String sql;
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    if (list.size() == 1) {
      sql = "DELETE FROM spring_store.%s WHERE %s = %s".formatted(table, where, list.getFirst());
    } else {
      String preparedStringList = "(" + String.join(",", list) + ")";
      sql = "DELETE FROM spring_store.%s WHERE %s in %s".formatted(table, where, preparedStringList);
    }
    jdbcTemplate.update(sql);
  }

  public void deleteNotInList(Object table, Object where, List<String> list) {
    String sql;
    JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
    if (list.size() == 1) {
      sql = "DELETE FROM spring_store.%s WHERE %s != %s".formatted(table, where, list.getFirst());
    } else {
      String preparedStringList = "(" + String.join(",", list) + ")";
      sql = "DELETE FROM spring_store.%s WHERE %s not in %s".formatted(table, where, preparedStringList);
    }
    System.out.println(sql);
    System.out.println(list.size());
    jdbcTemplate.update(sql);
  }
}