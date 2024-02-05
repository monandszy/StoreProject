package code.infrastructure.database.repository;

import code.business.dao.ProducerDAO;
import code.domain.Producer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
   private final CRUDRepository<Producer> crudRepository;
   private final Map<Integer, Producer> loadedProducers = new TreeMap<>();


   private static MapSqlParameterSource getObjectToTableMap(Producer producer) {
      MapSqlParameterSource params = new MapSqlParameterSource()
              .addValue("name", producer.getName())
              .addValue("address", producer.getAddress());
      return params;
   }

   @Override
   public Integer add(Producer producer) {
      if (Objects.nonNull(producer.getId()))
         return updateWhereId(producer.getId(), producer.getParams()).getId();
      MapSqlParameterSource params = getObjectToTableMap(producer);
      return crudRepository.add("producer", params);
   }

   private static RowMapper<Producer> getProducerRowMapper() {
      RowMapper<Producer> producerRowMapper = (resultSet, rowNum) -> Producer.builder()
              .id(resultSet.getInt("id"))
              .name(resultSet.getString("name"))
              .address(resultSet.getString("address"))
              .build();
      return producerRowMapper;
   }

   @Override
   public Optional<Producer> getById(Integer id) {
      if (loadedProducers.containsKey(id)) {
         return Optional.of(loadedProducers.get(id));
      }
      RowMapper<Producer> producerRowMapper = getProducerRowMapper();
      Optional<Producer> any = crudRepository.get("producer", "id", id, producerRowMapper).stream().findAny();
      if (any.isPresent()) {
         Producer loadedProducer = any.get();
         loadedProducers.put(loadedProducer.getId(), loadedProducer);
      }
      return any;
   }

   String UPDATE_SQL = "UPDATE zajavka_store.producer SET name = :name, address = :address  WHERE id = :id";

   private static MapSqlParameterSource getParameterToNamedMap(Integer producerId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
              .addValue("name", params[0])
              .addValue("address", params[1])
              .addValue("id", producerId);
      return mapSqlParameterSource;
   }

   @Override
   public Producer updateWhereId(Integer producerId, String[] params) {
      MapSqlParameterSource mapSqlParameterSource = getParameterToNamedMap(producerId, params);
      crudRepository.updateById(UPDATE_SQL, mapSqlParameterSource);
      loadedProducers.remove(producerId);
      return getById(producerId).orElseThrow();
   }

   @Override
   public void deleteById(Integer producerId) {
      crudRepository.delete("producer", "id", producerId);
      loadedProducers.remove(producerId);
   }

   @Override
   public void deleteAll() {
      crudRepository.delete("producer", 1, 1);
      loadedProducers.clear();
   }

   @Override
   public List<Producer> getAll() {
      return crudRepository.get("producer", 1, 1, getProducerRowMapper());
   }
}