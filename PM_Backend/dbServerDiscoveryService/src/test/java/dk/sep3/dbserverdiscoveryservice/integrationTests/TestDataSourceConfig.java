package dk.sep3.dbserverdiscoveryservice.integrationTests;

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
import org.springframework.context.annotation.Profile;
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
@Profile("test")
@Configuration
@ConditionalOnProperty(name = "testdb.datasource.enabled", havingValue = "true", matchIfMissing = true)
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "testdbEntityManagerFactory",
    transactionManagerRef = "testdbTransactionManager",
    basePackages = {"dk.sep3.dbserver.repositories.passwordManagerDb"})
public class TestDataSourceConfig
{
  @Primary
  @Bean(name = "testdbDataSourceProperties")
  @ConfigurationProperties("testdb.datasource")
  public DataSourceProperties testdbDataSourceProperties(){
    return new DataSourceProperties();
  }

  @Primary
  @Bean(name = "testdbDataSource")
  @ConfigurationProperties("testdb.datasource.configuration")
  public DataSource testdbDataSource(@Qualifier("testdbDataSourceProperties") DataSourceProperties dataSourceProperties){
    return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean(name = "testdbEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean testdbEntityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("testdbDataSource") DataSource testdbDataSource){
    Map<String, String> testdbJpaProperties = new HashMap<>();
    testdbJpaProperties.put("hibernate.hbm2ddl.auto", "create-drop");

    return builder
        .dataSource(testdbDataSource)
        .packages("dk.sep3.dbserver.model.passwordManager.db_entities")
        .persistenceUnit("testdbDataSource")
        .properties(testdbJpaProperties)
        .build();
  }

  @Primary
  @Bean(name = "testdbTransactionManager")
  public PlatformTransactionManager primaryTransactionManager(@Qualifier("testdbEntityManagerFactory") EntityManagerFactory testdbEntityManagerFactory) {
    return new JpaTransactionManager(testdbEntityManagerFactory);
  }
}
