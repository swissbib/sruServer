package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.swissbib.sru.targets.solr.SolrXSLTTransRepresentation;
import org.swissbib.sru.targets.solr.SOLRQueryTransformation;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/20/13
 * Time: 10:19 PM
 * To change this template use File | Settings | File Templates.
 */
//public class SearchRetrieveSolr extends Restlet {
public class SearchRetrieveSolr extends ServerResource {


//    public SearchRetrieveSolr (Context context) {
//        super(context);
//    }


    @Get()
    public Representation getSearchResult() throws IOException {

        //s. auch http://restlet.org/learn/2.0/firstResource
        //ich kann  @Get("xml") angeben - schneller?

        Context context =  getContext();
        ConcurrentMap<String,Object> attributes = context.getAttributes();
        HttpSolrServer solrServer =  (HttpSolrServer) attributes.get("solrServer");


        Form queryParams = getRequest().getResourceRef().getQueryAsForm();

        //todo: no query -> diagnostics error
        //todo: no schema: default dc

        String query = queryParams.getFirstValue("query");
        String recordSchema = queryParams.getFirstValue("recordSchema");

        //recordSchema=info:srw/schema/1/dc-v1.1


        SOLRQueryTransformation sQ = new SOLRQueryTransformation();
        SaxRepresentation saxRepresentation = null;



        DomRepresentation dR = null;
        StringRepresentation sR = null;

        Representation rep = null;




        try {

            sQ.init(query,solrServer);
            QueryResponse qR = sQ.runQuery();
            //saxRepresentation = new SolrSaxRepresentation(qR);
            //dR = new SolrDomRepresentation(qR).getDom();

            //sR = (StringRepresentation)  new SolrStringRepresenation(qR).getDom();

            SolrXSLTTransRepresentation xsltTrans = new SolrXSLTTransRepresentation(qR);
            rep =  xsltTrans.getRepresentation();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


        //return saxRepresentation;
        //return dR;

        return rep;
        //return sR;



    }



}
