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




    @Override
    public void init(Form inputParams, HttpSolrServer solrServer, HashMap<String,ArrayList<String>> searchMapping) throws Exception {

        String cqlQuery = inputParams.getFirstValue("query");

        if (null == cqlQuery) {
            throw new Exception("no query");

        }

        this.inputParams = inputParams;
        this.searchMapping = searchMapping;

        this.searchServer = solrServer;
        System.out.println(cqlQuery);

        CQLParser cqlP = new CQLParser();
        try {
            cqlNode = cqlP.parse(cqlQuery);
        } catch (CQLParseException pE) {
            cqlNode = null;
            pE.printStackTrace();
        }
    }


    @Override
    public abstract QueryResponse runQuery() throws Exception;

    @Override
    public abstract SolrDocumentList getResult();
}
