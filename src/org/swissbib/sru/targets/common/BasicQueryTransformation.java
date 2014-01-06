package org.swissbib.sru.targets.common;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.restlet.data.Form;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;

import java.util.ArrayList;
import java.util.HashMap;

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


public abstract class BasicQueryTransformation implements CQLQueryTransformationInterface {

    /**
     * CQLNode to be processed by target
      */
    protected CQLNode cqlNode = null;
    protected HttpSolrServer searchServer = null;
    protected Form inputParams = null;
    protected HashMap<String,ArrayList<String>> searchMapping = null;
    protected String cqlQuery = "";




    @Override
    public void init(Form inputParams, HttpSolrServer solrServer, HashMap<String,ArrayList<String>> searchMapping) throws Exception {

        //nur wenn icht null und length ==  0
        //setze dies in abgeleiteter Klasse für leeren String
        this.cqlQuery = inputParams.getFirstValue("query").trim();


        if (this.cqlQuery.equals("")) {
            //todo what if empty query -> how to transform it into *:* for CQL??
            //do this in derived class because it depends on the target
            //overwrite init!
            this.cqlQuery = "dc.anywhere=*:*";
            //throw new SRUException("missing query parameter", "missing query parameter");

        }

        this.inputParams = inputParams;
        this.searchMapping = searchMapping;

        this.searchServer = solrServer;
        //todo logging system
        //System.out.println(this.cqlQuery);

        CQLParser cqlP = new CQLParser();
        try {
            cqlNode = cqlP.parse(this.cqlQuery);
        } catch (CQLParseException pE) {

            SRUException sruex = new SRUException("cql parse Exception", "cql parse Exception", pE);
            sruex.setUseExceptionMessage(true);
            throw sruex;

        } catch (Throwable throwable) {
            SRUException sruex = new SRUException("undefined details", "undefined message", throwable);
            sruex.setUseExceptionMessage(true);
            throw sruex;

        }
    }


    @Override
    public abstract QueryResponse runQuery() throws Exception;

    @Override
    public abstract SolrDocumentList getResult();


    public String getCQLQuery() {
        return this.cqlQuery;
    }

}