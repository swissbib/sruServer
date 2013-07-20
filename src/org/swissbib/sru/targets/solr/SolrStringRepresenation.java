package org.swissbib.sru.targets.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.restlet.Context;
import org.restlet.data.Form;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/14/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class SolrStringRepresenation extends SRUBasicRepresentation {


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




    protected long startPage = 0;



    QueryResponse qR = null;
    Context context = null;
    RequestedSchema schema = null;
    Form queryParams = null;

    public SolrStringRepresenation (QueryResponse qR, Context context,Form queryParams, RequestedSchema schema) {

        super();
        this.qR = qR;
        this.context = context;
        this.schema = schema;

        this.queryParams = queryParams;




    }

    @Override
    public Representation getRepresentation() {


        startPage = qR.getResults().getStart();

        Iterator<SolrDocument> iterator =  qR.getResults().iterator();
        String uH =  queryParams.getFirstValue("x-info-10-get-holdings");

        boolean useHoldings = uH != null ? Boolean.valueOf(uH) : false;


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
                    sB.append(createMarcNoNS(doc,startPage,useHoldings));
                    break;
                case marcNS:
                    sB.append(createMarcNS(doc,startPage,useHoldings));
                    break;
                default:

            }
            startPage++;

        }
        sB.append("</records>\n");
        sB.append("</searchRetrieveResponse>\n");

        return new StringRepresentation(sB.toString(),MediaType.TEXT_XML);
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

        sB.append(transformedRecord);
        sB.append("</recordData>");
        sB.append("<recordPosition>").append(position).append("</recordPosition>");

        sB.append("<extraRecordData>");
        sB.append("<queryTime xmlns=\"urn:swissbib-sru:queryTime\">").append(this.qR.getQTime()).append("</queryTime>");
        if (useHoldings) {

            sB.append("<holdings xmlns=\"urn:swissbib-sru:holdings\">");

            String holdings = (String) doc.getFieldValue("holdings");
            Matcher hM = pHoldingsPattern.matcher(holdings);
            if (hM.find()) {
                String allHoldings = hM.group(1);


                sB.append(allHoldings);
            }

            sB.append("</holdings>");

        }

        sB.append("</extraRecordData>");

        sB.append("</record>");

        return sB.toString();




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
