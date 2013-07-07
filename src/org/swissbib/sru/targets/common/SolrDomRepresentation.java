package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/7/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrDomRepresentation  {

    QueryResponse qR = null;

    public SolrDomRepresentation(QueryResponse qR) {

        this.qR = qR;

    }


    public DomRepresentation getDom() {

        Iterator<SolrDocument> iterator =  qR.getResults().iterator();
        DomRepresentation domR = null;

        StringBuilder sB = new StringBuilder();

        sB.append("<test>\n");

        while (iterator.hasNext()) {


            SolrDocument doc = iterator.next();

            String record = (String) doc.getFieldValue("fullrecord");
            //String holdings = (String) doc.getFieldValue("holdings");


            sB.append(record.substring(21));
            //sB.append(holdings);


        }
        sB.append("</test>\n");

        try {

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = factory.newDocumentBuilder();

            String test = sB.toString();

            ByteArrayInputStream a = new ByteArrayInputStream(test.getBytes("utf-8"));
            Document aDocument = documentBuilder.parse(a);

            domR = new DomRepresentation(MediaType.APPLICATION_XML, aDocument);

        } catch (ParserConfigurationException pCE){

        } catch (UnsupportedEncodingException uE) {
            uE.printStackTrace();
        }  catch (SAXException sE) {
            sE.printStackTrace();


        } catch (IOException ioE) {
            ioE.printStackTrace();
        }


        return domR;

    }


}
