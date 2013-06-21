package org.swissbib.sru;

import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

import org.swissbib.sru.resources.SearchRetrieve;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/20/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SRUApplication extends Application {

    public static void main(String[] args) throws Exception {
        //Server sruServer = new Server(Protocol.HTTP, 8111);
        //sruServer.setNext(new SRUApplication());
        //sruServer.start();

        Component mailServer = new Component();
        mailServer.getServers().add(Protocol.HTTP, 8111);
        mailServer.getDefaultHost().attach(new SRUApplication());
        mailServer.start();

    }


    /**
     * Creates a root Restlet to trace requests.
     */
    @Override
    public Restlet createInboundRoot() {

//        SearchRetrieve sru = new SearchRetrieve(getContext());

//        Router router = new Router(getContext());
        //router.attach("/search/{accountId}/mails/{mailId}",
//        router.attach("http://localhost:8111/search",
//                sru);
//        return router;

        Router router = new Router(getContext());

        router.attach("/search",
                SearchRetrieve.class);

//        router.attach("/accounts/{accountId}/mails/{mailId}",
//                MailServerResource.class);
        return router;




    }


}
