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
import org.swissbib.sru.targets.common.SRUBasicRepresentation;
import org.swissbib.sru.targets.solr.SolrStringRepresenation;
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
public class SearchRetrieveSolr extends SearchRetrieveBasic {


    @Get()
    public Representation getSearchResult() throws Exception {

        //s. auch http://restlet.org/learn/2.0/firstResource
        //ich kann  @Get("xml") angeben - schneller?


        super.init();



        Context context =  getContext();
        ConcurrentMap<String,Object> attributes = context.getAttributes();
        HttpSolrServer solrServer =  (HttpSolrServer) attributes.get("solrServer");


        SOLRQueryTransformation sQ = new SOLRQueryTransformation();
        Representation rep = null;

        try {



            Form queryParams = getRequest().getResourceRef().getQueryAsForm();

            sQ.init(queryParams,solrServer);
            QueryResponse qR = sQ.runQuery();


            String repClass =  (String) attributes.get("representationClass");

            SRUBasicRepresentation basicRepresenation = null;

            if (repClass.equalsIgnoreCase("org.swissbib.sru.solr.SolrXSLTTransRepresentation")) {

                basicRepresenation = new SolrXSLTTransRepresentation(qR);
            } else {
                basicRepresenation = new SolrStringRepresenation(qR,context,queryParams, this.schemaType);
            }


            rep =  basicRepresenation.getRepresentation();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return rep;

    }



}
