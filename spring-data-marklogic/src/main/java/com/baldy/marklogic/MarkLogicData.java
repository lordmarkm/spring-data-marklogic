package com.baldy.marklogic;

import java.util.List;

/**
 *
 * @author Mark Baldwin B. Martinez on Feb 23, 2016
 *
 */
public interface MarkLogicData {

    String getUri();
    List<String> getCollections();

}
