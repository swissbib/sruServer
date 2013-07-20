package org.swissbib.sru.targets.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.swissbib.sru.targets.common.BasicQueryTransformation;
import org.z3950.zing.cql.*;


import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 6/21/13
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SOLRQueryTransformation extends BasicQueryTransformation {


    protected SolrDocumentList result = null;

    protected ArrayList<HashMap<String,String>> listOfResults = new ArrayList<HashMap<String, String>>();


    //private final static SolrServer solrServer;

    //static {
    //    solrServer = new HttpSolrServer("http://localhost:8080/solr/sb-biblio");
    //}




    //private final String serverURL = "http://search.swissbib.ch/solr/sb-biblio/select?";
    //private final String defaultParams = "&wt=xml&indent=true";
    //http://localhost:8111/search?query=fulltext+=+%22hello%22+and+title+=+%22hello%22

    @Override
    public QueryResponse runQuery() throws Exception {

        if (null == cqlNode)
            throw new Exception ("target not initialzed");

        //StringBuffer luceneStringQuery = new StringBuffer().append("/sb-biblio/select?");
        StringBuffer luceneStringQuery = new StringBuffer();
        makeLuceneQuery(cqlNode,luceneStringQuery);


        SolrQuery parameters = new SolrQuery();
        parameters.set("q", luceneStringQuery.toString());
        parameters.set("qt","dismax") ;
        parameters.set("defType","edismax") ;
        parameters.set("start","10");
        //seems that edismax needs a default query field
        //todo: have a closer look into the pre condition
        parameters.set("df","bla") ; //should be a default field if no one is defined in the configuration of the server

        return this.searchServer.query(parameters);


    }

    @Override
    public SolrDocumentList getResult() {
        return result;
    }


    private void makeLuceneQuery(CQLNode node, StringBuffer sb) {
        if(node instanceof CQLBooleanNode) {
            CQLBooleanNode cbn=(CQLBooleanNode)node;
            sb.append("(");
            makeLuceneQuery(cbn.getLeftOperand(), sb);
            if(node instanceof CQLAndNode)
                sb.append(" AND ");
            else if(node instanceof CQLNotNode)
                sb.append(" NOT ");
            else if(node instanceof CQLOrNode)
                sb.append(" OR ");
            else sb.append(" UnknownBoolean(").append(cbn).append(") ");

            makeLuceneQuery(cbn.getRightOperand(), sb);
            sb.append(")");
        }
        else if(node instanceof CQLTermNode) {
            CQLTermNode ctn=(CQLTermNode)node;
            String index=ctn.getIndex();

            //String newIndex=(String)indexMappings.get(index);
            String newIndex=index;

            if(newIndex!=null)
                index=newIndex;

            if(!index.equals(""))
                sb.append(index).append(":");

            String term=ctn.getTerm();
            if(ctn.getRelation().getBase().equals("=") ||
                    ctn.getRelation().getBase().equals("scr")) {
                if(term.indexOf(' ')>=0)
                    sb.append('"').append(term).append('"');
                else
                    sb.append(ctn.getTerm());
            }
            else if(ctn.getRelation().getBase().equals("any")) {
                if(term.indexOf(' ')>=0)
                    sb.append('(').append(term).append(')');
                else
                    sb.append(ctn.getTerm());
            }
            else if(ctn.getRelation().getBase().equals("all")) {
                if(term.indexOf(' ')>=0) {
                    sb.append('(');
                    StringTokenizer st=new StringTokenizer(term);
                    while(st.hasMoreTokens()) {
                        sb.append(st.nextToken());
                        if(st.hasMoreTokens())
                            sb.append(" AND ");
                    }
                    sb.append(')');
                }
                else
                    sb.append(ctn.getTerm());
            }
            else
                sb.append("Unsupported Relation: ").append(ctn.getRelation().getBase());
        }
        else sb.append("UnknownCQLNode(").append(node).append(")");
    }



    /*

    first try with HTTP connection -> less performant!

   @Override
    public void runQuery() throws Exception {

        if (null == cqlNode)
            throw new Exception ("target not initialzed");

        StringBuffer luceneStringQuery = new StringBuffer();
        makeLuceneQuery(cqlNode,luceneStringQuery);

        //URLEncoder.encode(this.makeQuery(q), "UTF-8")
        StringBuilder urlBuilder = new StringBuilder();
        //urlBuilder.append(serverURL).append(URLEncoder.encode("q=" + luceneStringQuery.toString() + defaultParams,"UTF-8"));

        urlBuilder.append(serverURL).append("q=").append(URLEncoder.encode(luceneStringQuery.toString(),"UTF-8" )).append(defaultParams);
        //urlBuilder.append(serverURL).append("q=" + luceneStringQuery.toString() + defaultParams);



        HTTPConnectionHandling cH = new HTTPConnectionHandling();

        System.out.println(urlBuilder.toString());

        HttpURLConnection connection = cH.getHTTPConnection(urlBuilder.toString(), false);

        InputStream is = (InputStream) connection.getContent();
        String response = new Scanner( is ).useDelimiter( "\\Z" ).next();
        System.out.print(response);

        //InputStreamReader in = new InputStreamReader((InputStream) connection.getContent());
        //BufferedReader buff = new BufferedReader(in);
        //StringBuilder sB = new StringBuilder();
        //String line;
        //do {
        //    line = buff.readLine();
        //    if (null != line) sB.append(buff.readLine());
        //} while (line != null);


        //System.out.print(sB.toString());

        //To change body of implemented methods use File | Settings | File Templates.
    }


     */

}
