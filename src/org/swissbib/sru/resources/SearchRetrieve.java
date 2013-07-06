package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
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
//public class SearchRetrieve extends Restlet {
public class SearchRetrieve extends ServerResource {


//    public SearchRetrieve (Context context) {
//        super(context);
//    }


    @Get
    public Representation toXml() throws IOException {

        //Application test = this.getApplication();
        Context c =  getContext();
        //Series<Parameter> params = c.getParameters() ;
        ConcurrentMap<String,Object> attributes = c.getAttributes();
        HttpSolrServer solrServer =  (HttpSolrServer) attributes.get("solrServer");


        Form queryParams = getRequest().getResourceRef().getQueryAsForm();
        String query = queryParams.getFirstValue("query");

        SOLRQueryTransformation sQ = new SOLRQueryTransformation();

        try {

            sQ.init(query,solrServer);
            sQ.runQuery();

            String result = sQ.getResult();
            System.out.println(result);

        }catch (Exception ex) {

            ex.printStackTrace();

        }




        DomRepresentation result = new DomRepresentation();

        return result;


    }

    /*
    @Override
    public void handle(Request request, Response response) {
        String entity = "Method       : " + request.getMethod()
                + "\nResource URI : "
                + request.getResourceRef()
                + "\nIP address   : "
                + request.getClientInfo().getAddress()
                + "\nAgent name   : "
                + request.getClientInfo().getAgentName()
                + "\nAgent version: "
                + request.getClientInfo().getAgentVersion();
        response.setEntity(entity, MediaType.TEXT_PLAIN);
    }
     */


}
