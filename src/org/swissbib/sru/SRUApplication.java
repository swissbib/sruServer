package org.swissbib.sru;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.swissbib.sru.resources.SRUFileResources;
import org.swissbib.sru.resources.SearchRetrieveSolr;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


/**
 * [...description of the type ...]
 *
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * Date: 8/1/13
 * Time: 11:40 AM
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * license:  http://opensource.org/licenses/gpl-2.0.php GNU General Public License
 *
 * @author Guenter Hipler  <guenter.hipler@unibas.ch>
 * @link http://www.swissbib.org
 * @link     https://github.com/swissbib/sruServer
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
        sruComponent.getClients().add(Protocol.CLAP);
        sruComponent.getClients().add(Protocol.FILE);
        sruComponent.getDefaultHost().attach(new SRUApplication());
        sruComponent.start();

    }




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

        /*

    var searchFieldNamesOptions = {
        "dc.anywhere"               :       "dc.anywhere",
        "dc.corporateName"          :       "dc.corporateName",
        "dc.creator"                :       "dc.creator",
        "dc.date"                   :       "dc.date",
        "dc.genreForm"              :       "dc.genreForm",
        "dc.identifier"             :       "dc.identifier",
        "dc.id"                     :       "dc.id",
        "dc.language"               :       "dc.language",
        "dc.medium"                 :       "dc.medium",
        "dc.personalName"           :       "dc.personalName",
        "dc.possessingInstitution"  :       "dc.possessingInstitution",
        "dc.subject"                :       "dc.subject",
        "dc.title"                  :       "dc.title",
        "dc.topicalSubject"         :       "dc.topicalSubject",
        "dc.uniformTitle"           :       "dc.uniformTitle",
        "dc.xNetwork"               :       "dc.xNetwork",
        "dc.xonline"                :       "dc.xonline"

    };



         */


        HashMap<String,ArrayList<String>> searchFieldMapping = new HashMap<String,ArrayList<String>>();

        searchFieldMapping.put("dc.anywhere",new ArrayList<String>( Arrays.asList("title_short","title_full_unstemmed",
                                                                       "title_full","title","title_alt","title_new",
                                                                        "series","author","author_fuller","contents","topic",
                                                                        "fulltext")));
        searchFieldMapping.put("dc.creator",new ArrayList<String>( Arrays.asList("author","author_fuller", "author_additional")));
        searchFieldMapping.put("dc.corporateName",new ArrayList<String>( Arrays.asList("author","author_fuller", "author_additional")));
        searchFieldMapping.put("dc.date",new ArrayList<String>( Arrays.asList("publishDate")));
        searchFieldMapping.put("dc.genreForm",new ArrayList<String>( Arrays.asList("title_full")));
        searchFieldMapping.put("dc.identifier",new ArrayList<String>( Arrays.asList("ctrlnum")));
        searchFieldMapping.put("dc.id",new ArrayList<String>( Arrays.asList("id")));
        searchFieldMapping.put("dc.language",new ArrayList<String>( Arrays.asList("language")));
        searchFieldMapping.put("dc.medium",new ArrayList<String>( Arrays.asList("navMediatype")));
        searchFieldMapping.put("dc.personalName",new ArrayList<String>( Arrays.asList("author","author_fuller", "author_additional")));
        searchFieldMapping.put("dc.possessingInstitution",new ArrayList<String>( Arrays.asList("institution")));
        searchFieldMapping.put("dc.subject",new ArrayList<String>( Arrays.asList("topic_unstemmed","topic", "geographic","era")));
        searchFieldMapping.put("dc.title",new ArrayList<String>( Arrays.asList("title_short","title_full_unstemmed",
                "title_full","title","title_alt","title_new",
                "series","series2")));
        searchFieldMapping.put("dc.topicalSubject",new ArrayList<String>( Arrays.asList("topic_unstemmed","topic", "geographic","era")));
        searchFieldMapping.put("dc.uniformTitle",new ArrayList<String>( Arrays.asList("title_short","title_full_unstemmed",
                "title_full","title","title_alt","title_new",
                "series","series2")));
        searchFieldMapping.put("dc.xNetwork",new ArrayList<String>( Arrays.asList("union")));
        searchFieldMapping.put("dc.xonline",new ArrayList<String>( Arrays.asList("union")));

        hM.put("searchMapping",searchFieldMapping);

        getContext().setAttributes(hM);

        Router router = new Router(getContext());

        router.attach("/search",
                SearchRetrieveSolr.class);

        router.attach("/xslfiles/{filename}",
                SRUFileResources.class);

        //todo: look for a better way to load resources
        Directory directory = new Directory(getContext(), "file:///home/swissbib/environment/code/sruRestlet/src/org/swissbib/sru/resources/diagnose/");
        router.attach("/diagnose", directory);






        return router;

    }


}
