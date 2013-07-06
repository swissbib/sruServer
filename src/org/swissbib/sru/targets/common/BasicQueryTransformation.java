package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/21/13
 * Time: 8:47 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BasicQueryTransformation implements CQLQueryTransformationInterface {

    /**
     * CQLNode to be processed by target
      */
    protected CQLNode cqlNode = null;
    protected HttpSolrServer solrServer = null;


    @Override
    public void init(String cqlQuery, HttpSolrServer sorlServer) throws Exception {


        this.solrServer = solrServer;
        System.out.println(cqlQuery);

        CQLParser cqlP = new CQLParser();
        try {
            cqlNode = cqlP.parse(cqlQuery);
        } catch (CQLParseException pE) {
            cqlNode = null;
            pE.printStackTrace();
        }
    }


    @Override
    public abstract void runQuery() throws Exception;

    @Override
    public abstract String getResult();
}
