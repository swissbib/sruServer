package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.swissbib.sru.targets.common.SRUBasicRepresentation;
import org.swissbib.sru.targets.common.SRUException;
import org.swissbib.sru.targets.common.UtilsCQLRelationsIndexMapping;
import org.swissbib.sru.targets.solr.SOLRQueryTransformation;
import org.swissbib.sru.targets.solr.SolrStringRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

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



//public class SearchRetrieveSolr extends Restlet {
public class SearchRetrieveSolrSubDB extends SearchRetrieveSolr {


    @Get()
    public Representation getSearchResult() throws Exception {

        return super.getSearchResult();

    }


    @Override
    @SuppressWarnings("unchecked")
    protected HttpSolrServer getSearchServer () {

        final String subdb = getAttribute("subdb");

        Context context =  getContext();
        ConcurrentMap<String,Object> attributes = context.getAttributes();
        HashMap <String,HttpSolrServer>  solrServers =  (HashMap <String,HttpSolrServer> ) attributes.get("solrServer");

        if (null != subdb && solrServers.containsKey(subdb)) {
            return solrServers.get(subdb);
        } else {
            return solrServers.get("defaultdb");
        }

    }


    @Override
    @SuppressWarnings("unchecked")
    protected String getGeneralFilterQuery () {
        final String subdb = getAttribute("subdb");

        Context context =  getContext();
        ConcurrentMap<String,Object> attributes = context.getAttributes();
        HashMap <String,String>  filterDBs =  (HashMap <String,String> ) attributes.get("filterDBs");

        if (null != subdb && null != filterDBs &&  filterDBs.containsKey(subdb)) {
            return filterDBs.get(subdb);
        } else {
            return null;
        }


    }


}
