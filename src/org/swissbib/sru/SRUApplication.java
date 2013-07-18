package org.swissbib.sru;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.swissbib.sru.resources.SearchRetrieveSolr;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/20/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SRUApplication extends Application {

    //Beispielabfrage:http://localhost:8111/search?query=fulltext+=+%22hallo%22+and+fulltext+=+%22a*%22


    public static void main(String[] args) throws Exception {

        //todo Unterschied Component / Server??

        //Server sruServer = new Server(Protocol.HTTP, 8111);
        //sruServer.setNext(new SRUApplication());
        //sruServer.start();

        Component sruComponent = new Component();
        sruComponent.getServers().add(Protocol.HTTP, 8111);
        sruComponent.getDefaultHost().attach(new SRUApplication());
        sruComponent.start();



    }



    /**
     * Creates a root Restlet to trace requests.
     */
    @Override
    public Restlet createInboundRoot() {


        //todo: make this configurable
        //HttpSolrServer  solrServer = new HttpSolrServer("http://search.swissbib.ch/solr/sb-biblio");
        HttpSolrServer  solrServer = new HttpSolrServer("http://localhost:8080/solr/sb-biblio");
        HashMap<String,Object>  hM =  new HashMap<String, Object>();
        hM.put("solrServer",solrServer);

        ConcurrentHashMap<String,Templates> templatesMap = new ConcurrentHashMap<String, Templates>();

        final TransformerFactory tF = TransformerFactory.newInstance();

        //todo: load templates resources with RESTlet means
        final Source marc2DCNoNamespace = new StreamSource(new File("/home/swissbib/environment/code/sruRestlet/resources/xslt/MARC21slim2OAIDC.nonamespace.xsl"));

        try {
            Templates templates =  tF.newTemplates(marc2DCNoNamespace);
            templatesMap.put("m2DCnoNs",templates);


        } catch (TransformerConfigurationException trConfiguration) {

            //todo: logging with Restlet
            trConfiguration.printStackTrace();
        }

        hM.put("templatesMap",templatesMap);

        //hM.put("representationClass","org.swissbib.sru.solr.SolrXSLTTransRepresentation");
        hM.put("representationClass","org.swissbib.sru.solr.SolrStringRepresenation");




        getContext().setAttributes(hM);

        Router router = new Router(getContext());

        router.attach("/search",
                SearchRetrieveSolr.class);

        return router;

    }


}
