package org.swissbib.sru.targets.solr;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.BinaryResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.swissbib.sru.targets.common.BasicQueryTransformation;
import org.swissbib.sru.targets.common.SRUException;
import org.z3950.zing.cql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

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


public class SOLRQueryTransformation extends BasicQueryTransformation {


    protected SolrDocumentList result = null;

    protected ArrayList<HashMap<String,String>> listOfResults = new ArrayList<HashMap<String, String>>();

    protected String generalFilterQuery = null;


    //private final static SolrServer solrServer;

    //static {
    //    solrServer = new HttpSolrServer("http://localhost:8080/solr/sb-biblio");
    //}


    public void setGeneralFilterQuery(String fq) {

        this.generalFilterQuery = fq;
    }


    //private final String serverURL = "http://search.swissbib.ch/solr/sb-biblio/select?";
    //private final String defaultParams = "&wt=xml&indent=true";
    //http://localhost:8111/search?query=fulltext+=+%22hello%22+and+title+=+%22hello%22

    @Override
    public QueryResponse runQuery() throws Exception {

        QueryResponse response = null;

        try {

            if (null == cqlNode)  {
                throw new SRUException("no cqlNode -> target not initialized", "no cqlNode -> target not initialized");
            }


            StringBuffer luceneStringQuery = new StringBuffer();
            makeLuceneQuery(cqlNode,luceneStringQuery);


            SolrQuery parameters = new SolrQuery();
            parameters.set("q", luceneStringQuery.toString());

            String sR = inputParams.getFirstValue("startRecord");
            int startRecord = 0;

            try {
                 startRecord =  sR != null && sR.length() > 0 ? Integer.parseInt(sR) : 0;
            } catch (Exception ex)  {

                System.out.println("invalid start parameter -> use 0 as default");
            }

            //for backward compatibility - was 1 in the former version
            startRecord = startRecord == 1 ? 0 : startRecord;

            parameters.set("start",startRecord);
            parameters.set("rows", inputParams.getFirstValue("maximumRecords")) ;

            if (this.generalFilterQuery != null) {

                //SOLR needs this df parameter on the local as well as on the general level
                //grr - SOLR syntax...
                parameters.set("df","title_short");
                parameters.set("fq",this.generalFilterQuery);

            }


            //seems that edismax needs a default query field
            //todo: have a closer look into the pre condition
            //parameters.set("df","title_short") ; //should be a default field if no one is defined in the configuration of the server

            this.searchServer.setParser(new BinaryResponseParser());
            this.searchServer.setRequestWriter(new BinaryRequestWriter());

            response = this.searchServer.query(parameters);

        } catch (SRUException sruException) {
            throw sruException;

        } catch (Exception excep) {

            SRUException sruex = new SRUException("undefined details", "undefined message", excep);
            sruex.setUseExceptionMessage(true);
            throw sruex;

        } catch (Throwable throwable) {

            SRUException sruex = new SRUException("undefined details", "undefined message", throwable);
            sruex.setUseExceptionMessage(true);
            throw sruex;
        }

        return response;
    }

    @Override
    public SolrDocumentList getResult() {
        return result;
    }


    //for indexes used by OCLC look at http://oclc.org/developer/documentation/worldcat-search-api/complete-list-indexes
    private void makeLuceneQuery(CQLNode node, StringBuffer sb) throws Exception{
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
            IndexTermStructure iTS = this.getIndex(ctn);


            ArrayList <String> searchFields = this.searchMapping.get(iTS.index);

            String relationToUse = this.allowedRelationIndexMapping.isRelationAllowed(iTS.index,iTS.relation) ?
                                iTS.relation : this.allowedRelationIndexMapping.getDefaultRelation(iTS.index);

            EdismaxSolrQueryClause edismaxClause = new EdismaxSolrQueryClause(iTS.index,relationToUse,iTS.term,searchFields);

            sb.append(edismaxClause.getQueryClause());
        }

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
