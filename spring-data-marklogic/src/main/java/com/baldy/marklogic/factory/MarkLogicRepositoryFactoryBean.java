package com.baldy.marklogic.factory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.baldy.marklogic.MarkLogicConnectionDetails;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 *
 */
public class MarkLogicRepositoryFactoryBean <T extends JpaRepository<S, ID>, S, ID extends Serializable>
    extends JpaRepositoryFactoryBean<T, S, ID> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        MarkLogicConnectionDetails conn = new MarkLogicConnectionDetails();
        Resource resource = new ClassPathResource("marklogic.properties");
        Properties env;
        try {
            env = PropertiesLoaderUtils.loadProperties(resource);
            conn.setHost(env.getProperty("db.host"));
            conn.setPort(Integer.parseInt(env.getProperty("db.port")));
            conn.setUsername(env.getProperty("db.writer_user"));
            conn.setPassword(env.getProperty("db.writer_password"));
            conn.setAuthenticationType(env.getProperty("db.authentication_type"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not read marklogic props");
        }

        return new MarkLogicRepositoryFactory(entityManager, conn);
    }

}