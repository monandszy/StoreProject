package code.infrastructure.configuration;

import code._ComponentScanMarker;
import lombok.AllArgsConstructor;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.postgresql.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
@AllArgsConstructor
@ComponentScan(basePackageClasses = _ComponentScanMarker.class)
@PropertySource(value = "classpath:database.properties")
@EnableTransactionManagement
public class TestApplicationConfiguration {

   private final Environment environment;

   @Bean
   public SimpleDriverDataSource databaseDataSource() {
      SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
      dataSource.setDriver(new Driver());
      dataSource.setUrl(environment.getProperty("jdbc.url"));
      dataSource.setUsername(environment.getProperty("jdbc.user"));
      dataSource.setPassword(environment.getProperty("jdbc.pass"));
      dataSource.setSchema(environment.getProperty("jdbc.schema"));
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

   @Bean
   PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(databaseDataSource());
   }

   @Bean
   public TransactionTemplate transactionTemplate() {
      return new TransactionTemplate(txManager());
   }
}