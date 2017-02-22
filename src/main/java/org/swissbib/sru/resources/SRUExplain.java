package org.swissbib.sru.resources;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.swissbib.sru.targets.common.SRUException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * [...description of the type ...]
 * <p/>
 * <p/>
 * <p/>
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * <p/>
 * Date: 1/3/14
 * Time: 3:40 PM
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





public class SRUExplain extends ServerResource {

    //private Context context;

    //public SRUExplain(Context context) {

    //    this.context = context;

    //}



    @Get()
    @SuppressWarnings("unchecked")
    public Representation getSRUExplanation() throws Exception {

        Representation tr  = null;


        try {

            Context context =  getContext();

            Form queryParams = getRequest().getResourceRef().getQueryAsForm();

            String operation = queryParams.getFirstValue("operation");

            if (operation != null &&  operation.equalsIgnoreCase("explain")) {

                //Beipiel für den Zugriff auf explain operation DNB
                //wget -Otest.xml 'http://services.dnb.de/sru/authorities?version=1.1&operation=searchRetrieve&query=WOE%3Dsozialistenkongress%20and%20COD%3Ds&recordSchema=RDFxml'
                //nur auf sb-coai1 und sb-coai2

                //todo: different explain responses for special subDBs - if no subdb one defaultdb
                //perhaps sub type similar to search
                final String subdb = getAttribute("subdb");


                ConcurrentMap<String,Object> attributes = context.getAttributes();
                String sruExplain = (String)   attributes.get("sruExplain");

                Reference ref = LocalReference.createFileReference(sruExplain);
                ClientResource r = new ClientResource(ref);
                Representation templateFile = r.get();

                HashMap<String, Object> myhash = new HashMap<String,Object>();

                ArrayList<String> aL = new ArrayList<String>();

                HashMap<String,ArrayList<String>> searchMapping = (HashMap<String,ArrayList<String>>)    attributes.get("searchMapping");
                //HashMap<String,HashMap<String,String>> searchMapping = (HashMap<String,ArrayList<String>>)    attributes.get("searchMapping");

                //ArrayList<IndexDescription>  indexDescriptions = new ArrayList<IndexDescription>();
                ArrayList<HashMap<String,String>>  indexDescriptions = new ArrayList<HashMap<String, String>>();
                for ( String key :  searchMapping.keySet()) {
                    ArrayList keyValues = searchMapping.get(key);
                    String keyValuesS = keyValues.toString();

                    HashMap<String,String> hm = new HashMap<String, String>();
                    hm.put("id", key);
                    hm.put("fields", keyValuesS);
                    //Zugriff auf Objektproperties in Velocity???
                    //IndexDescription id = new IndexDescription(key , keyValuesS);

                    indexDescriptions.add(hm);

                }

                myhash.put("allIndexes", indexDescriptions);

                tr = new TemplateRepresentation(templateFile,
                        myhash, MediaType.TEXT_XML);

            } else {
                throw new SRUException("used operation: " + (operation != null ? operation: ""), "wrong or missing operation");
            }
        } catch (SRUException sruException) {
            tr = sruException.getRepresentation();
        } catch (Exception ex) {
            SRUException sruex = new SRUException(null, null, ex);
            sruex.setUseExceptionMessage(true);
            tr = sruex.getRepresentation();
        }


        return tr;
    }


}

class IndexDescription {
    public String id;
    public String solrfields;


    public IndexDescription(String id, String fields) {
        this.id = id;
        this.solrfields = fields;
    }


}
