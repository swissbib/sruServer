package org.swissbib.sru.targets.solr;

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
 * Time: 3:19 PM
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
public class EdismaxSolrQueryClause extends BasicSolrQuery {


    protected ArrayList<String> queryFields;


    public EdismaxSolrQueryClause(String cqlIndexName, String cqlRelation, String cqlTerm ) {
        super(cqlIndexName,cqlRelation,cqlTerm);
    }


    public EdismaxSolrQueryClause(String cqlIndexName, String cqlRelation, String cqlTerm,  ArrayList<String> queryFields) {
        super(cqlIndexName,cqlRelation,cqlTerm);

        this.queryFields = queryFields;

    }


    @Override
    public String getQueryClause() {
        //for the transformation compare
        //http://www.loc.gov/standards/sru/cql/contextSets/theCqlContextSet.html


        StringBuilder clause = new StringBuilder();
        clause.append("({!edismax qf=\"");

        int numberQueryFields = 1;
        for (String field: this.queryFields) {
            if (numberQueryFields < queryFields.size()) {
                clause.append(field).append(" ");
            }
            else {
                clause.append(field);
            }

            numberQueryFields++;
        }
        clause.append("\" q.op=").append(this.mapCQLRelationToSOLRBooleanOperator()).append(" df=title_short v='").append(this.createQueryTerm()).append("'})");

        return clause.toString();
    }
}
