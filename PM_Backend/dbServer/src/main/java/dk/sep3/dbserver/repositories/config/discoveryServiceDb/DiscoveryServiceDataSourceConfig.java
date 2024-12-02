package dk.sep3.dbserver.repositories.config.discoveryServiceDb;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

// Configured, following this guide: https://mmafrar.medium.com/configuring-multiple-data-sources-with-spring-boot-2-and-spring-data-jpa-8e236844e80f
// and https://medium.com/@sharatnaik1996/connect-and-use-multiple-datasources-in-spring-boot-java-1192286ab361
/** <p>Defines necessary configuration data for the Database Server repository/Db through Spring Boot and JPA.
 * This is required, since each Database Server has access to multiple different databases,
 * meaning default JPA configuration is not an option.</p>*/
@Configuration
@ConditionalOnProperty(name = "discovery.datasource.enabled", havingValue = "true", matchIfMissing = true)
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "discoveryEntityManagerFactory",
    transactionManagerRef = "discoveryTransactionManager",
    basePackages = {"dk.sep3.dbserver.repositories.discoveryServiceDb"})
public class DiscoveryServiceDataSourceConfig
{
  @Bean(name = "discoveryDataSourceProperties")
  @ConfigurationProperties("db2.datasource")
  public DataSourceProperties discoveryDataSourceProperties(){
    return new DataSourceProperties();
  }

  @Bean(name = "discoveryDataSource")
  @ConfigurationProperties("db2.datasource.configuration")
  public DataSource discoveryDataSource(@Qualifier("discoveryDataSourceProperties") DataSourceProperties dataSourceProperties){
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Bean(name = "discoveryEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean discoveryEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("discoveryDataSource") DataSource discoveryDataSource){
    Map<String, String> discoveryJpaProperties = new HashMap<>();
    discoveryJpaProperties.put("hibernate.hbm2ddl.auto", "validate");

    return builder
        .dataSource(discoveryDataSource)
        .packages("dk.sep3.dbserver.model.discoveryService.db_entities")
        .persistenceUnit("discoveryDataSource")
        .properties(discoveryJpaProperties)
        .build();
  }

  @Bean(name = "discoveryTransactionManager")
  public PlatformTransactionManager discoveryTransactionManager(@Qualifier("discoveryEntityManagerFactory") EntityManagerFactory discoveryEntityManagerFactory) {
    return new JpaTransactionManager(discoveryEntityManagerFactory);
  }
}
