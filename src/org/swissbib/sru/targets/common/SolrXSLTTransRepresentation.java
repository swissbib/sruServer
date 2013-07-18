package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/16/13
 * Time: 5:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class SolrXSLTTransRepresentation {


    QueryResponse qR = null;

    public SolrXSLTTransRepresentation (QueryResponse qR) {

        this.qR = qR;

    }


    public Representation getRepresentation() {

        StringRepresentation sR = null;


        final TransformerFactory tF = TransformerFactory.newInstance();

        final Source styleSheet = new StreamSource(new File("/home/swissbib/environment/code/sruRestlet/resources/xslt/MARC21slim2OAIDC.nonamespace.xsl"));

        try {

            Templates templates =  tF.newTemplates(styleSheet);




            final Transformer transformer = templates.newTransformer();

            Iterator<SolrDocument> iterator =  qR.getResults().iterator();

            while (iterator.hasNext()) {

                SolrDocument doc = iterator.next();
                String record = (String) doc.getFieldValue("fullrecord");

                Source sourceRecord =  new StreamSource(new StringReader (record));

                StringWriter sw = new StringWriter() ;
                Result streamResult = new StreamResult(sw);

                transformer.transform(sourceRecord,streamResult);

                sR  = new StringRepresentation(sw.toString(), MediaType.TEXT_XML);

                //System.out.println(sw.toString());



            }



        } catch (TransformerConfigurationException cE) {
            cE.printStackTrace();
        }catch (TransformerException tE) {
            tE.printStackTrace();
        }

        return sR;


    }



}
