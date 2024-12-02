package dk.sep3.dbserver.repositories.config.PmDb;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
/** <p>Defines necessary configuration data for the PasswordManager repository/Db through Spring Boot and JPA.
 * This is required, since each Database Server has access to multiple different databases,
 * meaning default JPA configuration is not an option.</p>*/
@Configuration
@ConditionalOnProperty(name = "userDatabase.datasource.enabled", havingValue = "true", matchIfMissing = true)
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager",
    basePackages = {"dk.sep3.dbserver.repositories.PmDb"})
public class PmDataSourceConfig
{
  @Primary
  @Bean(name = "primaryDataSourceProperties")
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties primaryDataSourceProperties(){
    return new DataSourceProperties();
  }

  @Primary
  @Bean(name = "primaryDataSource")
  @ConfigurationProperties("spring.datasource.configuration")
  public DataSource primaryDataSource(@Qualifier("primaryDataSourceProperties") DataSourceProperties dataSourceProperties){
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean(name = "primaryEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("primaryDataSource") DataSource primaryDataSource){
    Map<String, String> primaryJpaProperties = new HashMap<>();
    primaryJpaProperties.put("hibernate.hbm2ddl.auto", "validate");

    return builder
        .dataSource(primaryDataSource)
        .packages("dk.sep3.dbserver.model.Pm.db_entities")
        .persistenceUnit("primaryDataSource")
        .properties(primaryJpaProperties)
        .build();
  }

  @Primary
  @Bean(name = "primaryTransactionManager")
  public PlatformTransactionManager primaryTransactionManager(@Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
    return new JpaTransactionManager(primaryEntityManagerFactory);
  }
}
