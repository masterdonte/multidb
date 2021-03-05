package com.example.multidb.config;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/*@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.example.multidb.repository.postgres"}, entityManagerFactoryRef = "emfPostgres", transactionManagerRef= "tmrPostgres")
*/
public class PostgresConfig{
	
	@Autowired
	private Environment env;
	
    @Bean(name = "dataSourcePostgres")    
    public DataSource dataSource() throws SQLException {
    	BasicDataSource bads = new BasicDataSource();  
    	bads.setValidationQuery("select 1");
    	bads.setUrl(env.getProperty("spring.datasource.postgres.jdbc-url"));
    	bads.setUsername(env.getProperty("spring.datasource.postgres.username"));
    	bads.setPassword(env.getProperty("spring.datasource.postgres.password"));            
        bads.setDriverClassName(env.getProperty("spring.datasource.postgres.driver-class-name"));
    	bads.setMinIdle(5);
    	bads.setMaxIdle(20);
        bads.setMaxOpenPreparedStatements(180);
        return bads; 
    }

    @Bean(name = "emfPostgres")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSourcePostgres") DataSource dataSource) throws SQLException {
        return builder
            .dataSource(dataSource)
            .packages("com.example.multidb.model.postgres")
            .persistenceUnit("dataSourcePostgres")
            .build();
    }

    @Bean(name = "tmrPostgres")
    public PlatformTransactionManager transactionManager(@Qualifier("emfPostgres") EntityManagerFactory entityManagerFactory) throws SQLException{
        return new JpaTransactionManager(entityManagerFactory);
    }
    
    @Bean("jdbcPostgres")
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSourcePostgres") DataSource ccbsDataSource) {
        return new JdbcTemplate(ccbsDataSource);
    }

}
