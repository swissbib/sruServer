package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.swissbib.sru.targets.common.SRUBasicRepresentation;
import org.swissbib.sru.targets.common.SRUException;
import org.swissbib.sru.targets.solr.SolrStringRepresentation;

import org.swissbib.sru.targets.solr.SOLRQueryTransformation;

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
public class SearchRetrieveSolr extends SearchRetrieveBasic {


    @Get()
    @SuppressWarnings("unchecked")
    public Representation getSearchResult() throws Exception {

        //s. auch http://restlet.org/learn/2.0/firstResource
        //ich kann  @Get("xml") angeben - schneller?



        Representation rep = null;

        try {
            super.init();

            Context context =  getContext();
            ConcurrentMap<String,Object> attributes = context.getAttributes();
            HttpSolrServer solrServer =  (HttpSolrServer) attributes.get("solrServer");


            SOLRQueryTransformation sQ = new SOLRQueryTransformation();

            Form queryParams = getRequest().getResourceRef().getQueryAsForm();
            HashMap<String,ArrayList<String>> searchMapping = (HashMap<String,ArrayList<String>>)    attributes.get("searchMapping");

            sQ.init(queryParams,solrServer, searchMapping);
            QueryResponse qR = sQ.runQuery();


            //not generic
            //String repClass =  (String) attributes.get("representationClass");

            //we had differentiation between String and XSLT transformation - I guess no longer needed
            SRUBasicRepresentation basicRepresenation = new SolrStringRepresentation(qR,context,queryParams, this.schemaType);

            rep =  basicRepresenation.getRepresentation();

        } catch (SRUException sruException) {
            rep = sruException.getRepresentation();
        }
        catch (Exception ex) {
            SRUException sruex = new SRUException(null, null, ex);
            sruex.setUseExceptionMessage(true);
            rep = sruex.getRepresentation();
        }

        return rep;

    }



}
