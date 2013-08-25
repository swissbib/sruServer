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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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

        String listenerPort = System.getProperty("listenerHTTPPort","80");

        Component sruComponent = new Component();
        sruComponent.getServers().add(Protocol.HTTP, Integer.parseInt(listenerPort));
        sruComponent.getClients().add(Protocol.CLAP);
        sruComponent.getClients().add(Protocol.FILE);
        sruComponent.getDefaultHost().attach(new SRUApplication());
        sruComponent.start();

    }




    @Override
    public Restlet createInboundRoot() {


        //get the properties for resources
        String marc2DC =  System.getProperty("marc2DublinCoreTemplate","/home/swissbib/environment/code/sruRestlet/resources/xslt/MARC21slim2OAIDC.nonamespace.xsl");
        String diagnoseDir =  System.getProperty("diagnoseDir","file:///home/swissbib/environment/code/sruRestlet/src/org/swissbib/sru/resources/diagnose/");
        String configuredSOLRServer =  System.getProperty("solrServer","http://localhost:8080/solr/sb-biblio");
        String mappingFieldsProps =  System.getProperty("mappingFieldsProps","/home/swissbib/environment/code/sruRestlet/resources/xslt/MARC21slim2OAIDC.nonamespace.xsl");


        //Connection to Solr server
        HashMap<String,Object>  hM =  new HashMap<String, Object>();


        HttpSolrServer  solrServer = new HttpSolrServer(configuredSOLRServer);
        hM.put("solrServer",solrServer);


        ConcurrentHashMap<String,Templates> templatesMap = new ConcurrentHashMap<String, Templates>();
        final TransformerFactory tF = TransformerFactory.newInstance();


        //load the templates
        //todo: load templates resources with RESTlet means
        final Source marc2DCNoNamespace = new StreamSource(new File(marc2DC));

        try {
            Templates templates =  tF.newTemplates(marc2DCNoNamespace);
            templatesMap.put("m2DCnoNs",templates);


        } catch (TransformerConfigurationException trConfiguration) {

            //todo: logging with Restlet
            trConfiguration.printStackTrace();
        }

        hM.put("templatesMap",templatesMap);

        hM.put("representationClass","org.swissbib.sru.solr.SolrStringRepresenation");


        //load the sru search / index field ampping
        try {

            File configMapFile = new File(mappingFieldsProps);

            FileInputStream fi = new FileInputStream(configMapFile);
            Properties configProps = new Properties();
            configProps.load(fi);

            Enumeration<Object> propKeys  = configProps.keys();

            HashMap<String,ArrayList<String>> searchFieldMapping = new HashMap<String,ArrayList<String>>();
            while (propKeys.hasMoreElements()) {

                String sruFieldAsKey = (String) propKeys.nextElement();
                String mappedIndexFieldsForKey = configProps.getProperty(sruFieldAsKey);

                ArrayList<String>  indexFields =  new ArrayList<String>();
                indexFields.addAll(Arrays.asList(mappedIndexFieldsForKey.split("##")));
                searchFieldMapping.put(sruFieldAsKey,indexFields);

            }

            hM.put("searchMapping",searchFieldMapping);
            getContext().setAttributes(hM);

        }  catch (IOException ioEx) {


            //todo: how to handle Exceptions in Restlet createInboundRoot
            ioEx.printStackTrace();
        }



        //configure the router
        //todo: should this be more configurable
        Router router = new Router(getContext());
        router.attach("/search",
                SearchRetrieveSolr.class);

        router.attach("/xslfiles/{filename}",
                SRUFileResources.class);


        Directory directory = new Directory(getContext(), diagnoseDir);
        router.attach("/diagnose", directory);

        return router;

    }


}
