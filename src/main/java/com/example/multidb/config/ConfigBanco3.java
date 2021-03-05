package com.example.multidb.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"com.example.multidb.repository.banco3"}, entityManagerFactoryRef = "entityManagerFactoryC", transactionManagerRef= "transactionManagerC")
public class ConfigBanco3 {
	
    @Bean(name = "dataSourceC")
    @ConfigurationProperties(prefix = "spring.datasource.banco3")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "entityManagerFactoryC")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder, @Qualifier("dataSourceC") DataSource dataSource) {
        return builder
            .dataSource(dataSource)
            .packages("com.example.multidb.model.banco3")
            .persistenceUnit("dataSourceC")
            .build();
    }

    @Bean(name = "transactionManagerC")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactoryC") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
    
    @Bean(name = "jdbcTemplateC")
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSourceC") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
