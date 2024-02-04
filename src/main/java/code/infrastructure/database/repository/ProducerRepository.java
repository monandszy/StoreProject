package code.infrastructure.database.repository;

import code.business.dao.ProducerDAO;
import code.domain.Producer;
import code.domain.exception.LoadedObjectIsModifiedException;
import code.domain.exception.ObjectIdNotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@Repository
@RequiredArgsConstructor
public class ProducerRepository implements ProducerDAO {

   private final SimpleDriverDataSource simpleDriverDataSource;
   private final Map<Integer, Producer> loadedProducers = new TreeMap<>();

   @Override
   public Integer add(Producer producer) {
      if (Objects.nonNull(producer.getId()))
         throw new ObjectIdNotAllowedException();
      SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(simpleDriverDataSource);
      simpleJdbcInsert.setTableName("producer");
      simpleJdbcInsert.setGeneratedKeyName("id");
      simpleJdbcInsert.setSchemaName("zajavka_store");
      BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(producer);
      return (Integer) simpleJdbcInsert.executeAndReturnKey(parameterSource);
   }

   @Override
   public Optional<Producer> get(Integer id) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      RowMapper<Producer> producerRowMapper = (resultSet, rowNum) -> Producer.builder()
              .id(resultSet.getInt("id"))
              .name(resultSet.getString("name"))
              .address(resultSet.getString("address"))
              .build();
      String sql = "SELECT * FROM zajavka_store.producer WHERE id = ?";
      List<Producer> result = jdbcTemplate.query(sql, producerRowMapper, id);

      Optional<Producer> any = result.stream().findAny();
      if (any.isPresent()) {
         Producer loadedProducer = any.get();
         Integer loadedId = loadedProducer.getId();
         if (loadedProducers.containsKey(loadedId)) {
            Producer existingProducer = loadedProducers.get(loadedId);
            if (existingProducer.equals(loadedProducer)) {
               return Optional.of(existingProducer);
            } else {
               throw new LoadedObjectIsModifiedException();
            }
         } else {
            loadedProducers.put(loadedId, loadedProducer);
         }
      }
      return any;
   }

   @Override
   public Producer update(Integer producerId, String[] params) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "UPDATE zajavka_store.producer SET name = ?, address = ?  WHERE id = ?";
      jdbcTemplate.update(sql, params[0], params[1], producerId);
      loadedProducers.remove(producerId);
      return get(producerId).orElseThrow();
   }

   @Override
   public void delete(Integer producerId) {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(simpleDriverDataSource);
      String sql = "DELETE FROM zajavka_store.producer WHERE id = ?";
      jdbcTemplate.update(sql, producerId);
      loadedProducers.remove(producerId);

   }
}