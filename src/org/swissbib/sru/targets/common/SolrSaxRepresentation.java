package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.ext.xml.SaxRepresentation;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/7/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrSaxRepresentation extends SaxRepresentation {

    QueryResponse qR = null;

    final XMLInputFactory inputFactory = XMLInputFactory.newInstance();


    public SolrSaxRepresentation (QueryResponse qR) {

        this.qR = qR;

    }



    public void write(org.restlet.ext.xml.XmlWriter writer)
            throws IOException {

        Iterator<SolrDocument> iterator =  qR.getResults().iterator();

        try {

            writer.startDocument();
            writer.startElement("collection");
            while (iterator.hasNext()) {


                SolrDocument doc = iterator.next();

                String record = (String) doc.getFieldValue("fullrecord");
                String holdings = (String) doc.getFieldValue("holdings");


                try {

                    XMLStreamReader sR = inputFactory.createXMLStreamReader(   new ByteArrayInputStream(record.getBytes("utf-8")));

                    while (sR.hasNext()) {

                        int sE = sR.next();
                        switch (sE) {


                            case XMLStreamConstants.START_ELEMENT:
                                writer.startElement(sR.getLocalName());
                                if (sR.getLocalName().equalsIgnoreCase("subfield")) {


                                    //writer.characters(sR.getElementText());
                                    //writer.endElement("subfield");

                                }


                                break;
                            case XMLStreamConstants.END_ELEMENT:
                                writer.endElement(sR.getLocalName());
                                break;

                        }


                    }



                } catch (XMLStreamException streamException) {
                    streamException.printStackTrace();
                }


            }
            writer.endElement("collection");
            writer.endDocument();

        } catch (SAXException sE) {
            sE.printStackTrace();
        }

    }


}
