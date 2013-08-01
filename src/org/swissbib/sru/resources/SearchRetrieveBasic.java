package org.swissbib.sru.resources;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.resource.ServerResource;

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


public class SearchRetrieveBasic extends ServerResource{



    protected RequestedSchema schemaType;
    protected String query = null;


    protected void init () throws Exception {



        Form queryParams = getRequest().getResourceRef().getQueryAsForm();



        //todo: no query -> diagnostics error
        //todo: no schema: default dc

        String q = queryParams.getFirstValue("query");

        if (null == q) {
            throw new Exception("no query");

        } else {
            this.query = q;
        }


        String recordSchema = queryParams.getFirstValue("recordSchema");
        if (null == recordSchema) {
            schemaType = RequestedSchema.marcNoNs;
        } else {

            switch (recordSchema) {

                case "dcnons":
                    schemaType = RequestedSchema.dcNoNS;
                    break;
                case "dcns":
                    schemaType = RequestedSchema.dcNS;
                    break;
                case "marcnons":
                    schemaType = RequestedSchema.marcNoNs;
                    break;
                case "marcns":
                    schemaType = RequestedSchema.marcNS;
                    break;
                case "jsonnons":
                    schemaType = RequestedSchema.jsonNoNS;
                    break;
                case "jsonns":
                    schemaType = RequestedSchema.jsonNS;
                    break;

                default:
                    schemaType = RequestedSchema.marcNoNs;
            }
        }

    }
}
