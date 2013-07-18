package org.swissbib.sru.targets.solr;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
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
 * Date: 7/14/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrStringRepresenation {

    QueryResponse qR = null;

    public SolrStringRepresenation (QueryResponse qR) {

        this.qR = qR;

    }




    public Representation getDom() {

        Iterator<SolrDocument> iterator =  qR.getResults().iterator();
        StringRepresentation representation = null;

        StringBuilder sB = new StringBuilder();

        //sB.append("<searchRetrieveResponse xmlns=\"http://www.loc.gov/zing/srw/\">\n");
        sB.append("<searchRetrieveResponse>\n");
        sB.append("<version>1.1</version>");
        sB.append("<numberOfRecords>");
        sB.append(qR.getResults().getNumFound());
        sB.append("</numberOfRecords>");
        sB.append("<records>\n");
        //sB.append("<extraRecordData>\n";
        //sB.append("<rel:qure xmlns:rel=\"xmlns:rob=\"info:srw/extension/2/relevancy\">0.965</rel:rank>\n" +
        //        "    </extraRecordData> ")

        //sB.append("<records xmlns:ns1=\"http://www.loc.gov/zing/srw/\">");





        while (iterator.hasNext()) {

            sB.append("<record>");
            sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
            sB.append("<recordPacking>xml</recordPacking>");

            sB.append("<recordData>");

            SolrDocument doc = iterator.next();

            String record = (String) doc.getFieldValue("fullrecord");
            //String holdings = (String) doc.getFieldValue("holdings");


            sB.append(record.substring(21));

            sB.append("</recordData>");
            sB.append("</record>");

            //sB.append(holdings);


        }
        sB.append("</records>\n");
        sB.append("</searchRetrieveResponse>\n");


        StringRepresentation sR = new StringRepresentation(sB.toString(),MediaType.TEXT_XML);

        return sR;


    }


}
