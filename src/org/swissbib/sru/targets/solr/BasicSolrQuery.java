package org.swissbib.sru.targets.solr;

import org.swissbib.sru.targets.syntaxtransformation.QueryInterface;
import org.z3950.zing.cql.CQLTermNode;

import java.util.ArrayList;

/**
 * [...description of the type ...]
 * <p/>
 * <p/>
 * <p/>
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * <p/>
 * Date: 12/30/13
 * Time: 3:13 PM
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2,
 * as published by the Free Software Foundation.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * <p/>
 * license:  http://opensource.org/licenses/gpl-2.0.php GNU General Public License
 *
 * @author Guenter Hipler  <guenter.hipler@unibas.ch>
 * @link http://www.swissbib.org
 * @link https://github.com/swissbib/xml2SearchDoc
 */
abstract public class BasicSolrQuery implements QueryInterface {

    protected   CQLTermNode cqlTermNode;

    public BasicSolrQuery(CQLTermNode cqlTermNode) {

        this.cqlTermNode = cqlTermNode;
    }



    @Override
    public abstract String getQueryClause();

    protected String mapCQLRelationToSOLRBooleanOperator() {

        String solrOperator = "AND";
        String cqlString = cqlTermNode.getRelation().toCQL();

        if (cqlString.equalsIgnoreCase("=") || cqlString.equalsIgnoreCase("ALL")) {

            /*
                This is the default relation, and the server can choose any appropriate relation or means of comparing the query term
                with the terms from the data being searched. If the term is numeric, the most commonly chosen relation is '=='.
                For a string term, either 'adj' or '==' as appropriate for the index and term
             */
            solrOperator = "AND";
        } else if (cqlString.equalsIgnoreCase("ANY")) {
            solrOperator = "OR";

        } else {
            //fallback -> just for the moment
            solrOperator = "OR";
        }

        return solrOperator;

    }



    protected String createQueryTerm() {


        String cqlString = this.cqlTermNode.getRelation().toCQL();

        StringBuilder queryTerm = new StringBuilder();



        if (cqlString.equalsIgnoreCase("exact")) {
            queryTerm.append("\"").append(this.cqlTermNode.getTerm()).append("\"");
        } else {
            queryTerm.append(this.cqlTermNode.getTerm());
        }

        return queryTerm.toString();

    }







}
