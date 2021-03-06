package org.swissbib.sru.resources;

import org.restlet.data.Form;
import org.restlet.resource.ServerResource;
import org.swissbib.sru.targets.common.SRUException;


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
        String operation = queryParams.getFirstValue("operation");

        if ((null == q && null == operation) ||
                (null == q && !operation.equalsIgnoreCase("explain"))) {
            throw new SRUException("missing query parameter", "missing query parameter");

        } else {
            this.query = q;
        }


        String recordSchema = queryParams.getFirstValue("recordSchema");
        if (null == recordSchema) {
            schemaType = RequestedSchema.dcOCLC;
        } else {


            if (recordSchema.equalsIgnoreCase("info:sru/schema/1/dc-v1.1-light")) {
                schemaType = RequestedSchema.dcswissbib;
            } else if (recordSchema.equalsIgnoreCase("info:srw/schema/1/dc-v1.1")) {
                schemaType = RequestedSchema.dcOCLC;
            } else if (recordSchema.equalsIgnoreCase("info:srw/schema/1/marcxml-v1.1-light")) {
                schemaType = RequestedSchema.marcswissbib;
            } else if ( recordSchema.equalsIgnoreCase("info:srw/schema/1/marcxml-v1.1")) {
                schemaType = RequestedSchema.marcOCLC;
            } else if (recordSchema.equalsIgnoreCase("info:sru/schema/json")) {
                schemaType = RequestedSchema.jsonswissbib;
            } else if (recordSchema.equalsIgnoreCase("info:sru/schema/1/aoisadxml")) {
                schemaType = RequestedSchema.aoisadxml;
            } else {
                schemaType = RequestedSchema.dcOCLC;
            }


        }

    }
}
