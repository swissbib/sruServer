package org.swissbib.sru.targets.common;

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
public abstract class BasicQueryTransformation implements CQLQueryTransformation{

    /**
     * CQLNode to be processed by target
      */
    protected CQLNode cqlNode = null;


    @Override
    public void init(String cqlQuery) throws Exception {
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
