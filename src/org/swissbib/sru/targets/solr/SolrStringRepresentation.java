package org.swissbib.sru.targets.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Resource;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class SolrStringRepresentation extends SRUBasicRepresentation {


    private final static Pattern pNoXMLDeclaration = Pattern.compile("<record.*?</record>",Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern pStartRecordTag = Pattern.compile("<record xmlns:xs.*?>");
    private final static Pattern pEndRecordTag = Pattern.compile("</record>");
    private final static Pattern pStartDataTag = Pattern.compile("<datafield");
    private final static Pattern pEndDataTag = Pattern.compile("</datafield>");
    private final static Pattern pStartSubFieldTag = Pattern.compile("<subfield");
    private final static Pattern pEndSubFieldTag = Pattern.compile("</subfield>");
    private final static Pattern pStartControlFieldTag = Pattern.compile("<controlfield");
    private final static Pattern pEndControlFieldTag = Pattern.compile("</controlfield>");
    private final static Pattern pStartLeaderTag = Pattern.compile("<leader>");
    private final static Pattern pEndLeaderTag = Pattern.compile("</leader>");
    private final static Pattern pHoldingsPattern = Pattern.compile("<record>(.*?)</record>");

    private final static Pattern pWrongSubfieldstructure = Pattern.compile("<subfielddatafield.*?</subfielddatafield>",Pattern.MULTILINE | Pattern.DOTALL);
    private final static Pattern pWrongSubfieldDubstructure = Pattern.compile("<subfielddubfield.*?</subfielddubfield>",Pattern.MULTILINE | Pattern.DOTALL);




    protected long startPage = 0;
    protected QueryResponse qR = null;
    protected Context context = null;
    protected RequestedSchema schema = null;
    protected Form queryParams = null;
    protected String cqlQuery = null;

    public SolrStringRepresentation(QueryResponse qR, Context context, Form queryParams, RequestedSchema schema) {

        super();
        this.qR = qR;
        this.context = context;
        this.schema = schema;

        this.queryParams = queryParams;

        this.cqlQuery =  queryParams.getFirstValue("query");




    }

    @Override
    public Representation getRepresentation() {




        SolrDocumentList result =  qR.getResults();
        startPage = result.getStart();
        long incrementalStart = result.getStart();




        Iterator<SolrDocument> iterator =  qR.getResults().iterator();
        String uH =  queryParams.getFirstValue("x-info-10-get-holdings");

        boolean useHoldings = uH != null ? Boolean.valueOf(uH) : false;


        StringBuilder sB = new StringBuilder();

        sB.append("<?xml version=\"1.0\" ?>");
        //sB.append("<?xml-stylesheet type=\"text/xsl\" href=\"/xslfiles/searchRetrieveResponse.xsl\"?>");

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
                    sB.append(createMarcNoNS(doc,incrementalStart,useHoldings));
                    break;
                case marcNS:
                    sB.append(createMarcNS(doc,incrementalStart,useHoldings));
                    break;
                default:

            }
            incrementalStart++;

        }
        sB.append("</records>\n");

        if (startPage + result.size() < result.getNumFound() -1) {
            sB.append("<nextRecordPosition>").append(startPage + result.size()).append("</nextRecordPosition>\n");
        }

        sB.append("<echoedSearchRetrieveRequest>\n");
        sB.append("<version>1.1</version>\n");
        sB.append("<query>").append("<![CDATA[").append(this.cqlQuery).append("]]>").append("</query>\n");
        sB.append("<startRecord>").append(startPage).append("</startRecord>");
        sB.append("<maximumRecords>").append(result.size()).append("</maximumRecords>\n");
        sB.append("<recordPacking>").append(queryParams.getFirstValue("recordPacking")).append("</recordPacking>\n");
        sB.append("<recordSchema>").append(schema.toString()).append("</recordSchema>\n");
        sB.append("<resultSetTTL>0</resultSetTTL>\n");
        sB.append("</echoedSearchRetrieveRequest>\n");
        sB.append("<sortKeys/>\n");

        sB.append("</searchRetrieveResponse>\n");

        //System.out.println("next result");
        //System.out.println(sB.toString());
        return new StringRepresentation(sB.toString(),MediaType.TEXT_XML);
        //return new StringRepresentation(sB.toString(),MediaType.TEXT_PLAIN);
    }


    private String createMarcNoNS (SolrDocument doc, long position, boolean useHoldings)  {


        StringBuilder sB = new StringBuilder();

        sB.append("<record>");
        sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
        sB.append("<recordPacking>xml</recordPacking>");

        sB.append("<recordData>");

        String record = (String) doc.getFieldValue("fullrecord");
        //String holdings = (String) doc.getFieldValue("holdings");
        Matcher m =  pNoXMLDeclaration.matcher(record);
        Boolean found = m.find();
        String  transformedRecord = "";
        if (found) {

            transformedRecord = m.group();

        }



        //sB.append(transformedRecord);
        sB.append(replaceWrongStructure(transformedRecord));
        sB.append("</recordData>");
        sB.append("<recordPosition>").append(position).append("</recordPosition>");


        //sB.append("<queryTime xmlns=\"urn:swissbib-sru:queryTime\">").append(this.qR.getQTime()).append("</queryTime>");
        if (useHoldings) {
            sB.append("<extraRecordData>");
            sB.append("<holdings xmlns=\"urn:swissbib-sru:holdings\">");

            String holdings = (String) doc.getFieldValue("holdings");
            Matcher hM = pHoldingsPattern.matcher(holdings);
            if (hM.find()) {
                String allHoldings = hM.group(1);


                sB.append(allHoldings);
            }

            sB.append("</holdings>");

            sB.append("</extraRecordData>");
        }



        sB.append("</record>");

        return sB.toString();




    }

    private String replaceWrongStructure(String recordToAnalyze) {

        Matcher matcher = pWrongSubfieldstructure.matcher(recordToAnalyze);
        String intermediate = matcher.replaceAll("");

        matcher = pWrongSubfieldDubstructure.matcher(intermediate);
        String toReturn = matcher.replaceAll("");


        return toReturn;
    }


    private String createMarcNS (SolrDocument doc, long position, boolean useHoldings)  {


        //the old OCLC SRU/W server used a lot of different namespaces which IMHO weren't necessary.
        //for the sake of backwards compatibility we create the namespaces in the same way as it was done by OCLC
        //the transformation is done using regex - guess it's faster than XSLT transformations
        //so far not tested

        StringBuilder sB = new StringBuilder();

        sB.append("<record>");
        sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
        sB.append("<recordPacking>xml</recordPacking>");

        sB.append("<recordData>");

        String record = (String) doc.getFieldValue("fullrecord");
        //String holdings = (String) doc.getFieldValue("holdings");
        Matcher m =  pNoXMLDeclaration.matcher(record);
        Boolean found = m.find();
        String  transformedRecord = "";
        if (found) {

            transformedRecord = m.group();
            transformedRecord = replaceWrongStructure(transformedRecord);
            transformedRecord =  pStartRecordTag.matcher(transformedRecord).replaceAll("<srw_marc:record xmlns:marc=\"http://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd\" xmlns:srw_marc=\"info:srw/schema/1/marcxml-v1.1\">");
            transformedRecord =  pEndRecordTag.matcher(transformedRecord).replaceAll("</srw_marc:record>");
            transformedRecord =  pStartDataTag.matcher(transformedRecord).replaceAll("<marc:datafield");
            transformedRecord   =  pEndDataTag.matcher(transformedRecord).replaceAll("</marc:datafield>");
            transformedRecord = pStartSubFieldTag.matcher(transformedRecord).replaceAll("<marc:subfield");
            transformedRecord = pEndSubFieldTag.matcher(transformedRecord).replaceAll("</marc:subfield>");
            transformedRecord = pStartControlFieldTag.matcher(transformedRecord).replaceAll("<marc:controlfield");
            transformedRecord = pEndControlFieldTag.matcher(transformedRecord).replaceAll("</marc:controlfield>");
            transformedRecord = pStartLeaderTag.matcher(transformedRecord).replaceAll("<marc:leader>");
            transformedRecord = pEndLeaderTag.matcher(transformedRecord).replaceAll("</marc:leader>");

        }

        sB.append(transformedRecord);
        sB.append("</recordData>");
        sB.append("<recordPosition>").append(position).append("</recordPosition>");


        if (useHoldings) {
            sB.append("<extraRecordData>");
            sB.append("<ns2:extraData  xmlns=\"http://oclc.org/srw/extraData\"  xmlns:ns2=\"http://oclc.org/srw/extraData\" >");
            sB.append("<ns4:holdings xmlns=\"urn:oclc-srw:holdings\" xmlns:ns4=\"urn:oclc-srw:holdings\">");

            String holdings = (String) doc.getFieldValue("holdings");
            Matcher hM = pHoldingsPattern.matcher(holdings);
            if (hM.find()) {
                String allHoldings = hM.group(1);

                allHoldings =  pStartDataTag.matcher(allHoldings).replaceAll("<ns4:datafield");
                allHoldings   =  pEndDataTag.matcher(allHoldings).replaceAll("</ns4:datafield>");
                allHoldings = pStartSubFieldTag.matcher(allHoldings).replaceAll("<ns4:subfield");
                allHoldings = pEndSubFieldTag.matcher(allHoldings).replaceAll("</ns4:subfield>");

                sB.append(allHoldings);
            }

            sB.append("</ns4:holdings>");
            sB.append("</ns2:extraData>");
            sB.append("</extraRecordData>");

        }


        sB.append("</record>");

        return sB.toString();
    }



    @SuppressWarnings("unchecked")
    private String createDCNoNS (SolrDocument doc)  {


        ConcurrentMap<String,Object> attributes = context.getAttributes();
        Templates template =  ((ConcurrentHashMap<String,Templates>) attributes.get("templatesMap")).get("m2DCnoNs");
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
