package com.baldy.marklogic.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.baldy.marklogic.MarkLogicData;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 * Implemented methods:
 * <ol>
 *      <li> T save(T entity)
 *      <li> List save(Iterable entities)
 *      <li> T findOne(String)
 *      <li> List findAll()
 *      <li> Page findAll(Pageable page)
 * </ol>
 * Custom methods below.
 */
public interface MarkLogicRepository <T extends MarkLogicData> extends JpaRepository<T, String> {

    List<T> keyValueQuery(String key, String value);
    Page<T> keyValueQuery(String key, String value, Pageable page);

}
