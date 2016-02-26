package com.baldy.marklogic.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.baldy.marklogic.factory.MarkLogicRepositoryFactoryBean;
import com.baldy.marklogic.test.service.TaxiService;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = TaxiService.class,
    repositoryFactoryBeanClass = MarkLogicRepositoryFactoryBean.class,
    repositoryImplementationPostfix = "CustomImpl"
)
@ComponentScan
@EnableAutoConfiguration
public class TestConfig {

}
