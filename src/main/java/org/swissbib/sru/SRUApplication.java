package org.swissbib.sru;

import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.restlet.*;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.swissbib.sru.resources.*;
import org.swissbib.sru.targets.common.UtilsCQLRelationsIndexMapping;

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
        //sruComponent.getClients().add(Protocol.CLAP);
        sruComponent.getClients().add(Protocol.FILE);
        sruComponent.getDefaultHost().attach(new SRUApplication());
        sruComponent.start();

    }




    @Override
    public synchronized Restlet createInboundRoot() {


        //get the properties for resources
        String marc2DC =  System.getProperty("marc2DublinCoreTemplate","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/xslt/MARC21slim2OAIDC.swissbib.xsl");
        String marc2DCOCLC =  System.getProperty("marc2DublinCoreTemplateOCLC","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/xslt/MARC21slim2OAIDC.oclc.xsl");

        String diagnoseDir =  System.getProperty("diagnoseDir","file:///home/swissbib/environment/code/sruWebAppRestLet/build/resources/diagnose");
        //String configuredSOLRServer =  System.getProperty("solrServer","http://search.swissbib.ch/solr/sb-biblio###defaultdb");
        //String configuredSOLRServer =  System.getProperty("solrServer","http://localhost:8081/solr/sb-biblio###defaultdb");
        String configuredSOLRServer =  System.getProperty("solrServer","http://sb-s20.swissbib.unibas.ch:8080/solr/sb_biblio###defaultdb");
        String mappingFieldsProps =  System.getProperty("mappingFieldsProps","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/mapping/mapping.solr.properties");
        String mappingCQLRelations =  System.getProperty("mappingCQLRelations","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/mapping/mapping.cqlrelations.properties");

        String formResource =  System.getProperty("formResource","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/diagnose/index.html");
        String jsResource =  System.getProperty("jsResource","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/diagnose/js/srudiagnose.js");
        String sruSearchURL =  System.getProperty("sruSearchURL","http://localhost:8080/sru/search");
        String sruExplainURL =  System.getProperty("sruExplainURL","http://localhost:8080/sru/explain");
        String sruExplain =  System.getProperty("sruExplain","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/explain/explain.swissbib.default.xml");
        //String filterDBs = System.getProperty("filterDBs","institution:Z16 OR institution:A208 OR institution:A196 OR institution:B463 OR institution:B464 OR institution:B465 OR institution:B466 OR union:RE71 OR itemid_isn_mv:HSG_P* OR itemid_isn_mv:HSG_AL875* OR     itemid_isn_mv:HSG_AL304* OR itemid_isn_mv:HSG_AL414* OR itemid_isn_mv:HSG_AN701* OR itemid_isn_mv:HSG_AL220* OR itemid_isn_mv:HSG_AL221* OR itemid_isn_mv:HSG_AL222* OR itemid_isn_mv:HSG_AL304* OR itemid_    isn_mv:HSG_AL414* OR itemid_isn_mv:HSG_AN701* OR itemid_isn_mv:HSG_MB3300* OR itemid_isn_mv:HSG_MD4* OR itemid_isn_mv:HSG_ME2* OR itemid_isn_mv:HSG_ME3* OR itemid_isn_mv:HSG_ME4* OR itemid_isn_mv:HSG_ME8*     OR itemid_isn_mv:HSG_MF43* OR itemid_isn_mv:HSG_MF7050 OR itemid_isn_mv:HSG_MF8* OR itemid_isn_mv:HSG_MK16* OR itemid_isn_mv:HSG_MK17* OR itemid_isn_mv:HSG_MK38* OR itemid_isn_mv:HSG_MK7* OR itemid_isn_m    v:HSG_QD030 OR itemid_isn_mv:HSG_QD050 OR itemid_isn_mv:HSG_QP44* OR itemid_isn_mv:HSG_QP45* OR itemid_isn_mv:HSG_QP82* OR itemid_isn_mv:HSG_Jus* OR itemid_isn_mv:HSG_GHug* OR itemid_isn_mv:LUUHL_P* OR cl    assif_ddc:34* OR classif_udc:34* OR classif_udc:04* OR classif_rvk:P* OR classif_072:s1dr OR classif_072:s2dr OR classif_912:rw OR classif_912:rs OR classif_912:dr OR classif_912:/8[0-9]0/ OR classif_912:    ZB34* OR classif_912:M11* OR classif_912:M12* OR classif_912:M91 OR sublocal:340###jusdb");
        String filterDBs = System.getProperty("filterDBs","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/mapping/mapping.views.properties");

        String xsltDir =  System.getProperty("xsltDir","/home/swissbib/environment/code/sruWebAppRestLet/build/resources/xslt/");
        String lastCommit = System.getProperty("lastCommit", "https://github.com/swissbib/sruServer");




        System.out.println("marc2DublinCoreTemplate: " + marc2DC);
        System.out.println("marc2DublinCoreTemplateOCLC: " + marc2DCOCLC);
        System.out.println("diagnoseDir: " + diagnoseDir);
        System.out.println("solrServer: " + configuredSOLRServer);
        System.out.println("mappingFieldsProps: " + mappingFieldsProps);
        System.out.println("mappingCQLRelationsProps: " + mappingCQLRelations);
        System.out.println("formResource: " + formResource);
        System.out.println("jsResource: " + jsResource);
        System.out.println("sruSearchURL: " + sruSearchURL);
        System.out.println("sruExplainURL: " + sruExplainURL);
        System.out.println("sruExplain: " + sruExplain);
        System.out.println("lastCommit: " + lastCommit);


        //Connection to Solr server
        HashMap<String,Object>  hM =  new HashMap<String, Object>();

        //HttpSolrServer  solrServer = new HttpSolrServer(configuredSOLRServer);
        hM.put("solrServer",configureSearchServer(configuredSOLRServer));
        hM.put("filterDBs",configureFilterDBs(filterDBs));

        hM.put("formResource",formResource);
        hM.put("jsResource",jsResource);
        hM.put("sruSearchURL",sruSearchURL);
        hM.put("sruExplainURL",sruExplainURL);
        hM.put("sruExplain",sruExplain);

        hM.put("xsltDir",xsltDir);
        hM.put("lastCommit", lastCommit);

        ConcurrentHashMap<String,Templates> templatesMap = new ConcurrentHashMap<String, Templates>();
        final TransformerFactory tF = TransformerFactory.newInstance();


        //load the templates
        //todo: load templates resources with RESTlet means
        final Source marc2DCswissbib = new StreamSource(new File(marc2DC));
        final Source marc2DCoclc = new StreamSource(new File(marc2DCOCLC));

        try {
            Templates templates =  tF.newTemplates(marc2DCswissbib);
            templatesMap.put("m2DCswissbib",templates);

            templates =  tF.newTemplates(marc2DCoclc);
            templatesMap.put("m2DCoclc",templates);

        } catch (TransformerConfigurationException trConfiguration) {

            //todo: logging with Restlet
            trConfiguration.printStackTrace();
        }

        hM.put("templatesMap",templatesMap);


        //load the sru search / index field mapping
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

            hM.put("searchMapping", searchFieldMapping);

        }  catch (IOException ioEx) {


            //todo: how to handle Exceptions in Restlet createInboundRoot -> diagnose message!
            ioEx.printStackTrace();
        }



        //load the mapping for allowed CQL relations for every defined index
        try {

            File configCQLRelatuonsFile = new File(mappingCQLRelations);

            FileInputStream fi = new FileInputStream(configCQLRelatuonsFile);
            Properties configProps = new Properties();
            configProps.load(fi);

            Enumeration<Object> propKeys  = configProps.keys();

            UtilsCQLRelationsIndexMapping riMapping = new UtilsCQLRelationsIndexMapping();
            while (propKeys.hasMoreElements()) {




                String sruFieldAsKey = (String) propKeys.nextElement();
                String mappedRelations = configProps.getProperty(sruFieldAsKey);

                riMapping.addMapping(sruFieldAsKey,mappedRelations);
            }

            hM.put("cqlRelationsMapping", riMapping);

        }  catch (IOException ioEx) {


            //todo: how to handle Exceptions in Restlet createInboundRoot -> diagnose message!
            ioEx.printStackTrace();
        }

        getContext().setAttributes(hM);


        //configure the router
        //todo: should this be more configurable
        Router router = new Router(getContext());
        router.attach("/search",
                SearchRetrieveSolr.class);


        router.attach("/explain",
                SRUExplain.class);

        router.attach("/explain/{subdb}",
                SRUExplain.class);



        router.attach("/search/{subdb}",
                SearchRetrieveSolrSubDB.class);



        router.attach("/xslfiles/{filename}",
                SRUFileResources.class);

        router.attach("/form",
                SRUDiagnoseForm.class);
        router.attach("/js",
                SRUDiagnoseJS.class);



        Directory directory = new Directory(getContext(), diagnoseDir);
        router.attach("/diagnose", directory);

        return router;

    }

    private HashMap <String,HttpSolrClient> configureSearchServer(String configurations) {


        String[]  configuration = configurations.split("####");
        HashMap <String,HttpSolrClient> configuredSolrServers = new HashMap<String, HttpSolrClient>();
        for (String server: configuration) {

            String[] nameAndURL = server.split("###");
            HttpSolrClient searchServer =  new HttpSolrClient(nameAndURL[0]);
            searchServer.setParser(new BinaryResponseParser());
            searchServer.setRequestWriter(new BinaryRequestWriter());
            configuredSolrServers.put(nameAndURL[1],searchServer);

        }

        return configuredSolrServers;

    }


    private HashMap <String,String> configureFilterDBs(String mappingViews) {

        HashMap<String, String> filterDBs = new HashMap<String, String>();

        try {

        File configCQLRelatuonsFile = new File(mappingViews);

        FileInputStream fi = new FileInputStream(configCQLRelatuonsFile);
        Properties configProps = new Properties();
        configProps.load(fi);

        Enumeration<Object> propKeys  = configProps.keys();

        while (propKeys.hasMoreElements()) {

            String viewName = (String) propKeys.nextElement();
            String viewFilter = configProps.getProperty(viewName);

            filterDBs.put(viewName,viewFilter);
        }


        } catch (FileNotFoundException fnFEx) {
            fnFEx.printStackTrace();
        } catch (IOException ioExc) {
            ioExc.printStackTrace();
        }

        /*
        HashMap<String, String> filterDBs = new HashMap<String, String>();

        if (configurations != null) {

            String[]  configuration = configurations.split("####");

            for (String server: configuration) {

                String[] nameAndFilter = server.split("###");

                filterDBs.put(nameAndFilter[1],nameAndFilter[0]);

            }

        }
        */

        return filterDBs;
    }

}
