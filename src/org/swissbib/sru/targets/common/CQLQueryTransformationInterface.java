package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/21/13
 * Time: 8:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CQLQueryTransformationInterface {

    public void init(String cqlQuery, HttpSolrServer sorlServer) throws Exception;

    public QueryResponse runQuery() throws Exception;

    public SolrDocumentList getResult();

}
