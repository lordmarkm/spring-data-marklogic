package com.baldy.marklogic.repo;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.baldy.marklogic.MarkLogicConnectionDetails;
import com.baldy.marklogic.MarkLogicData;
import com.baldy.marklogic.exception.NotImplementedException;
import com.google.common.collect.Lists;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.StringQueryDefinition;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 *
 */
@NoRepositoryBean
public class MarkLogicRepositoryImpl<T extends MarkLogicData> extends SimpleJpaRepository<T, String>
    implements MarkLogicRepository<T> , Serializable {

    private static final long serialVersionUID = 1L;
    static Logger LOG = LoggerFactory.getLogger(MarkLogicRepositoryImpl.class);
    private final JpaEntityInformation<T, ?> entityInformation;

    //Marklogic stuff here
    private final DatabaseClient dbClient;
    private final XMLDocumentManager docMgr;
    private final QueryManager queryMgr;
    private final String[] collections;

    /**
     * Creates a new {@link SimpleJpaRepository} to manage objects of the given
     * {@link JpaEntityInformation}.
     */
    public MarkLogicRepositoryImpl (JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
            Class<?> springDataRepositoryInterface, MarkLogicConnectionDetails conn) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;

        //Marklogic stuff here
        LOG.debug("Creating database client.");
        try {
            DatabaseClientFactory.getHandleRegistry().register(JAXBHandle.newFactory(entityInformation.getJavaType()));
        } catch (JAXBException e) {
            throw new RuntimeException("Could not create JAXBHandle for class! class=" + entityInformation.getJavaType());
        }

        this.dbClient = DatabaseClientFactory.newClient(
                conn.getHost(),
                conn.getPort(),
                conn.getUsername(),
                conn.getPassword(),
                Authentication.valueOf(conn.getAuthenticationType()));
        this.docMgr = this.dbClient.newXMLDocumentManager();
        this.queryMgr = this.dbClient.newQueryManager();
        
        String[] cols;
        try {
            cols = entityInformation.getJavaType().newInstance().getCollections().toArray(new String[]{});
        } catch (Exception e) {
            cols = null;
        }
        this.collections = cols;
    }

    /*********************************************************************
     * Implemented Spring-data methods that we have come to expect go here
     *********************************************************************/

    @Override
    public <S extends T> S save(S entity) {
        LOG.info("Gonna try and save something!");
        try {
            LOG.info("Saving entity with uri={}", entity.getUri());
            writeObjectAsXml(entity.getUri(), entity, entity.getCollections().toArray(new String[]{}));
        } catch (IOException e) {
            LOG.error("Unable to save entity! entity=" + entity);
        }
        return entity;
    }

    @Override
    public <S extends T> List<S> save(Iterable<S> entities) {
        for (S entity : entities) {
            save(entity);
        }
        return Lists.newArrayList(entities);
    }

    @Override
    public T findOne(String uri) {
        return docMgr.readAs(uri, this.entityInformation.getJavaType());
    }

    @Override
    public List<T> keyValueQuery(String key, String value) {
        //Yup, this line is necessary
        queryMgr.setPageLength(-1);

        RawQueryByExampleDefinition query = toQueryByExampleQuery(key, value);
        SearchHandle resultsHandle =  queryMgr.search(query, new SearchHandle());

        return toList(resultsHandle);
    }

    @Override
    public Page<T> keyValueQuery(String key, String value, Pageable page) {
        queryMgr.setPageLength(page.getPageSize());
        int start = page.getPageSize() * (page.getPageNumber() - 1) + 1;

        RawQueryByExampleDefinition query = toQueryByExampleQuery(key, value);
        SearchHandle resultsHandle = new SearchHandle();

        LOG.debug("Paginated search. start={}", start);
        queryMgr.search(query, resultsHandle, start);

        return new PageImpl<>(toList(resultsHandle), page, resultsHandle.getTotalResults());
    }

    private RawQueryByExampleDefinition toQueryByExampleQuery(String key, String value) {
        String rawXMLQuery =
                "<q:qbe xmlns:q='http://marklogic.com/appservices/querybyexample'>"+
                  "<q:query>" +
                    "<" + key + ">" + value + "</" + key + ">" +
                  "</q:query>" +
                "</q:qbe>";
        StringHandle rawHandle = new StringHandle(rawXMLQuery);

        RawQueryByExampleDefinition query = queryMgr.newRawQueryByExampleDefinition(rawHandle);
        if (null != this.collections) {
            query.setCollections(this.collections);
        }

        return query;
    }

    @Override
    public List<T> findAll() {
        //Yup, this line is necessary
        queryMgr.setPageLength(-1);

        StringQueryDefinition query = queryMgr.newStringDefinition();
        query.setCollections(this.collections);
        SearchHandle resultsHandle = new SearchHandle();
        queryMgr.search(query, resultsHandle);
        return toList(resultsHandle);
    }

    @Override
    public Page<T> findAll(Pageable page) {
        queryMgr.setPageLength(page.getPageSize());
        int start = page.getPageSize() * (page.getPageNumber() - 1) + 1;

        StringQueryDefinition query = queryMgr.newStringDefinition();
        query.setCollections(this.collections);
        SearchHandle resultsHandle = new SearchHandle();
        queryMgr.search(query, resultsHandle, start);

        return new PageImpl<>(toList(resultsHandle), page, resultsHandle.getTotalResults());
    }

    private List<T> toList(SearchHandle resultsHandle) {
        List<T> entityList = Lists.newArrayList();
        for (MatchDocumentSummary result: resultsHandle.getMatchResults()) {
            entityList.add(findOne(result.getUri()));
        }
        return entityList;
    }

    /*********************************************************************
     * Unimplemented Spring-data methods that we have come to expect go here
     *********************************************************************/

    /**
     * Sample method stopper
     */
    @Override
    public List<T> findAll(Sort sort) {
        throw new NotImplementedException();
    }

    /*********************************************************************
     * Marklogic specific methods
     *********************************************************************/

    private void writeObjectAsXml(String docId, Object data, String... collections) throws IOException {
        StringWriter stringWriter = new StringWriter();
        JAXB.marshal(data, stringWriter);
        writeXml(docId, stringWriter.toString(), collections);
    }

    private void writeXml(String docId, String data) throws IOException {
        writeXml(docId, data, (String[]) null);
    }

    private void writeXml(String docId, String data, String... collections) throws IOException {
        LOG.debug("Writing data to db. docId={}, data={}, collections={}", docId, data, collections);

        //Acquire the content
        InputStream docStream = IOUtils.toInputStream(data, "UTF-8");

        //Create a handle on the content
        InputStreamHandle handle = new InputStreamHandle(docStream);

        DocumentMetadataHandle metadata = null;
        if (null != collections) {
            metadata = new DocumentMetadataHandle();
            metadata.getCollections().addAll(collections);
        }

        //Write the document content
        docMgr.write(docId, metadata, handle);
    }
} 
