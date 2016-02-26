package com.baldy.marklogic.factory;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

import com.baldy.marklogic.MarkLogicConnectionDetails;
import com.baldy.marklogic.repo.MarkLogicRepositoryImpl;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 *
 */
public  class MarkLogicRepositoryFactory extends JpaRepositoryFactory {

   private final MarkLogicConnectionDetails conn;

   public MarkLogicRepositoryFactory(EntityManager entityManager, MarkLogicConnectionDetails conn) {
       super(entityManager);
       Assert.notNull(entityManager);
       this.conn = conn;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(
           RepositoryMetadata metadata, EntityManager entityManager) {

       Class<?> repositoryInterface = metadata.getRepositoryInterface();
       JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(metadata.getDomainType());
       return new MarkLogicRepositoryImpl(entityInformation, entityManager, repositoryInterface, this.conn);
   }

   @Override
   protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
       return MarkLogicRepositoryImpl.class;
   }

}