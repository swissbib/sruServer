package org.swissbib.sru;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.swissbib.sru.resources.SearchRetrieve;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/20/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SRUApplication extends Application {


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
        HttpSolrServer  solrServer = new HttpSolrServer("http://search.swissbib.ch/solr/sb-biblio");
        HashMap<String,Object>  hM =  new HashMap<String, Object>();
        hM.put("solrServer",solrServer);
        getContext().setAttributes(hM);

        Router router = new Router(getContext());

        router.attach("/search",
                SearchRetrieve.class);

        return router;

    }


}
