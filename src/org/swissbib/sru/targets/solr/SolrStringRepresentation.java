package org.swissbib.sru.targets.solr;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.Record;
import org.marcjson.MarcInJSON;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.swissbib.sru.resources.RequestedSchema;
import org.swissbib.sru.targets.common.SRUBasicRepresentation;
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

        sB.append(schema != RequestedSchema.jsonswissbib ?
                this.createSRUXMLHeader(String.valueOf(qR.getResults().getNumFound())):
                this.createSRUJsonHeader(String.valueOf(qR.getResults().getNumFound())));


        while (iterator.hasNext()) {

            SolrDocument doc = iterator.next();

            switch (schema) {
                case dcswissbib:
                    sB.append(createDCswissbib(doc));
                    break;
                case dcOCLC:
                    sB.append(createDCoclc(doc));
                    break;
                case marcswissbib:
                    sB.append(createMarcNoNS(doc,incrementalStart,useHoldings));
                    break;
                case marcOCLC:
                    sB.append(createMarcNS(doc,incrementalStart,useHoldings));
                    break;

                case jsonswissbib:
                    sB.append(createJson(doc,incrementalStart,useHoldings)).append(", ");
                    break;


                default:

            }
            incrementalStart++;

        }

        StringBuilder temB = new StringBuilder();
        String t = sB.toString();
        temB.append( t.substring(0,t.length() - 2));


        String nextPage = (startPage + result.size() < result.getNumFound() -1) ?
                String.valueOf(startPage + result.size()) : null;


        temB.append(schema != RequestedSchema.jsonswissbib ?
                this.createSRUXMLFooter(String.valueOf(String.valueOf(result.size())),nextPage):
                this.createSRUJsonFooter(String.valueOf(String.valueOf(result.size())),nextPage));


        MediaType mt = schema != RequestedSchema.jsonswissbib ? MediaType.TEXT_XML : MediaType.APPLICATION_JSON;

        return new StringRepresentation(temB.toString(),mt);
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


    private String createJson (SolrDocument doc, long position, boolean useHoldings)  {


        StringBuilder sB = new StringBuilder();


        String record = (String) doc.getFieldValue("fullrecord");

        if (useHoldings) {

            String holdings = (String) doc.getFieldValue("holdings");
            Matcher hM = pHoldingsPattern.matcher(holdings);
            if (hM.find()) {
                String allHoldings = hM.group(1);


                Pattern p =  Pattern.compile(   "<datafield\\s*?tag=\"949\".*?</datafield>|<datafield\\s*?tag=\"852\".*?</datafield>",
                        Pattern.CASE_INSENSITIVE |
                                Pattern.MULTILINE |
                                Pattern.DOTALL);
                Matcher match = p.matcher(record);
                if (match.find()) {
                    record = match.replaceAll("");
                }


                Pattern pi = Pattern.compile("(?<=</datafield>)\\s*?(?=</record>)");

                Matcher mi = pi.matcher(record);
                record = mi.replaceFirst(allHoldings);


            }

        }



        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(record.getBytes());

        // read it with BufferedReader
        //BufferedReader br = new BufferedReader(new InputStreamReader(is));

        Record rec = null;

        try {
            MarcXmlReader reader = new MarcXmlReader(is);
            rec =  reader.hasNext() ? reader.next() : null;
        } catch (Throwable th) {
            th.printStackTrace();
        }


        if (null != rec) {
            try {
                sB.append(MarcInJSON.record_to_marc_in_json(rec));
            } catch (IOException ioEx) {
                ioEx.printStackTrace(System.out);
            } catch (Throwable th) {
                th.printStackTrace(System.out);
            }
        }
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
    private String createDCswissbib (SolrDocument doc)  {


        ConcurrentMap<String,Object> attributes = context.getAttributes();
        Templates template =  ((ConcurrentHashMap<String,Templates>) attributes.get("templatesMap")).get("m2DCswissbib");
        StringBuilder sB = new StringBuilder();

        try {

            final Transformer transformer = template.newTransformer();


            sB.append("<record>");
            sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1-light</recordSchema>");
            sB.append("<recordPacking>xml</recordPacking>");

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

    @SuppressWarnings("unchecked")
    private String createDCoclc (SolrDocument doc)  {


        ConcurrentMap<String,Object> attributes = context.getAttributes();
        Templates template =  ((ConcurrentHashMap<String,Templates>) attributes.get("templatesMap")).get("m2DCoclc");
        StringBuilder sB = new StringBuilder();

        try {

            final Transformer transformer = template.newTransformer();


            sB.append("<record>");
            sB.append("<recordSchema>info:srw/schema/1/marcxml-v1.1</recordSchema>");
            sB.append("<recordPacking>xml</recordPacking>");

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

    protected String createSRUXMLHeader (String numberOfHits) {

        StringBuilder sB = new StringBuilder();

        sB.append("<?xml version=\"1.0\" ?>");
        if (schema == RequestedSchema.dcOCLC || schema == RequestedSchema.marcOCLC) {
            sB.append("<searchRetrieveResponse xmlns=\"http://www.loc.gov/zing/srw\" >\n");
        } else {
            sB.append("<searchRetrieveResponse>\n");
        }

        sB.append("<version>1.1</version>");
        sB.append("<numberOfRecords>");
        sB.append(numberOfHits);
        sB.append("</numberOfRecords>");
        if (schema == RequestedSchema.dcOCLC || schema == RequestedSchema.marcOCLC) {
            sB.append("<records xmlns:ns1=\"http://www.loc.gov/zing/srw\" >\n");
        } else {
            sB.append("<records>\n");
        }


        return sB.toString();

    }

    protected String createSRUXMLFooter (String maxRecords, String nextRecordPosition) {

        StringBuilder sB = new StringBuilder();

        sB.append("</records>\n");

        if (null != nextRecordPosition) {
            sB.append("<nextRecordPosition>").append(nextRecordPosition).append("</nextRecordPosition>\n");
        }

        sB.append("<echoedSearchRetrieveRequest>\n");
        sB.append("<version>1.1</version>\n");
        sB.append("<query>").append("<![CDATA[").append(this.cqlQuery).append("]]>").append("</query>\n");
        sB.append("<startRecord>").append(startPage).append("</startRecord>");
        sB.append("<maximumRecords>").append(maxRecords).append("</maximumRecords>\n");
        sB.append("<recordPacking>").append(queryParams.getFirstValue("recordPacking")).append("</recordPacking>\n");
        sB.append("<recordSchema>").append(schema.toString()).append("</recordSchema>\n");
        sB.append("<resultSetTTL>0</resultSetTTL>\n");
        sB.append("</echoedSearchRetrieveRequest>\n");
        sB.append("<sortKeys/>\n");

        sB.append("</searchRetrieveResponse>\n");


        return sB.toString();

    }
    protected String createSRUJsonHeader (String numberOfHits) {

        StringBuilder sB = new StringBuilder();

        sB.append("{\"collection\" : [");

        return sB.toString();

    }

    protected String createSRUJsonFooter (String maxRecords, String nextRecordPosition) {

        StringBuilder sB = new StringBuilder();


        sB.append("]}");

        return sB.toString();

    }



}
