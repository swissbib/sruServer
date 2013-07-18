package org.swissbib.sru.targets.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.swissbib.sru.resources.RequestedSchema;
import org.swissbib.sru.targets.common.SRUBasicRepresentation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/14/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrStringRepresenation extends SRUBasicRepresentation {

    QueryResponse qR = null;
    Context context = null;
    RequestedSchema schema = null;

    public SolrStringRepresenation (QueryResponse qR, Context context, RequestedSchema schema) {

        super();
        this.qR = qR;
        this.context = context;
        this.schema = schema;
    }

    @Override
    public Representation getRepresentation() {


        Iterator<SolrDocument> iterator =  qR.getResults().iterator();
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

            SolrDocument doc = iterator.next();

            switch (schema) {
                case dcNoNS:
                    sB.append(createDCNoNS(doc));
                    break;
                case dcNS:
                    break;
                case marcNoNs:
                    sB.append(createMarcNoNS(doc));
                    break;
                case marcNS:
                    break;
                default:

            }





            //sB.append(holdings);


        }
        sB.append("</records>\n");
        sB.append("</searchRetrieveResponse>\n");

        return new StringRepresentation(sB.toString(),MediaType.TEXT_XML);
    }


    private String createMarcNoNS (SolrDocument doc)  {


        StringBuilder sB = new StringBuilder();

        sB.append("<record>");
        sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
        sB.append("<recordPacking>xml</recordPacking>");

        sB.append("<recordData>");


        String record = (String) doc.getFieldValue("fullrecord");
        //String holdings = (String) doc.getFieldValue("holdings");


        sB.append(record.substring(21));

        sB.append("</recordData>");
        sB.append("</record>");

        return sB.toString();
    }


    private String createDCNoNS (SolrDocument doc)  {


        ConcurrentMap<String,Object> attributes = context.getAttributes();
        Templates template =  (Templates)((ConcurrentHashMap<String,Templates>) attributes.get("templatesMap")).get("m2DCnoNs");
        StringBuilder sB = new StringBuilder();

        try {

            final Transformer transformer = template.newTransformer();


            sB.append("<record>");
            sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
            sB.append("<recordPacking>dc</recordPacking>");

            sB.append("<recordData>");

            String record = (String) doc.getFieldValue("fullrecord");
            Source sourceRecord =  new StreamSource(new StringReader(record));

            StringWriter sw = new StringWriter() ;
            Result streamResult = new StreamResult(sw);

            transformer.transform(sourceRecord,streamResult);

            sB.append(sw.toString().substring(38)) ;
            //sB.append(sw.toString()) ;


            sB.append("</recordData>");
            sB.append("</record>");

            return sB.toString();
        } catch (TransformerException tE ) {
            tE.printStackTrace();
        }


        return sB.toString();
    }



}
