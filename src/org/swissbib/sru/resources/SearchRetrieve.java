package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.swissbib.sru.targets.common.SolrSaxRepresentation;
import org.swissbib.sru.targets.solr.SOLRQueryTransformation;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
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

        SaxRepresentation saxRepresentation = null;


        try {



            sQ.init(query,solrServer);


            QueryResponse qR = sQ.runQuery();


            saxRepresentation = new SolrSaxRepresentation(qR);



        }
        catch (Exception ex) {
            ex.printStackTrace();

        }


        return saxRepresentation;



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
