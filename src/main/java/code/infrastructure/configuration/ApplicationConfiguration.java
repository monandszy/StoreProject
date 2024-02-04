package code.infrastructure.configuration;

import code._ComponentScanMarker;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration
@ComponentScan(basePackageClasses = _ComponentScanMarker.class)
public class ApplicationConfiguration {

   @Bean
   public SimpleDriverDataSource databaseDataSource() {
      SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
      dataSource.setDriver(new Driver());
      dataSource.setUrl("jdbc:postgresql://localhost:5432/java_model");
      dataSource.setUsername("postgres");
      dataSource.setPassword("postgres");
      dataSource.setSchema("zajavka_store");
      return dataSource;
   }

   @Bean(initMethod = "migrate")
   Flyway flyway() {
      ClassicConfiguration configuration = new ClassicConfiguration();
      configuration.setBaselineOnMigrate(true);
      configuration.setLocations(new Location("filesystem:src/main/resources/database/migrations"));
      configuration.setDataSource(databaseDataSource());
      return new Flyway(configuration);
   }
}