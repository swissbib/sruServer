package org.swissbib.sru.targets.common;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/21/13
 * Time: 8:42 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CQLQueryTransformation {

    public void init(String cqlQuery) throws Exception;

    public void runQuery() throws Exception;

    public String getResult();

}
