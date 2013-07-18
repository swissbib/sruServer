package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.resource.ServerResource;

import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/18/13
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchRetrieveBasic extends ServerResource{



    protected RequestedSchema schemaType;

    protected String query = null;

    protected void init () throws Exception {



        Form queryParams = getRequest().getResourceRef().getQueryAsForm();

        //todo: no query -> diagnostics error
        //todo: no schema: default dc

        String q = queryParams.getFirstValue("query");

        if (null == q) {
            throw new Exception("no query");

        } else {
            this.query = q;
        }


        String recordSchema = queryParams.getFirstValue("recordSchema");
        if (null == recordSchema) {
            schemaType = RequestedSchema.marcNoNs;
        } else {

            switch (recordSchema) {

                case "dcnons":
                    schemaType = RequestedSchema.dcNoNS;
                    break;
                case "dcns":
                    schemaType = RequestedSchema.dcNS;
                    break;
                case "marcnons":
                    schemaType = RequestedSchema.marcNoNs;
                    break;
                case "marcns":
                    schemaType = RequestedSchema.marcNS;
                    break;
                case "jsonnons":
                    schemaType = RequestedSchema.jsonNoNS;
                    break;
                case "jsonns":
                    schemaType = RequestedSchema.jsonNS;
                    break;

                default:
                    schemaType = RequestedSchema.marcNoNs;
            }
        }

    }
}
