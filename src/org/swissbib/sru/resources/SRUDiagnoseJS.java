package org.swissbib.sru.resources;

import org.restlet.Context;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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
 * Time: 1:46 PM
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




public class SRUDiagnoseJS extends ServerResource {


    @Get()
    @SuppressWarnings("unchecked")
    public Representation getJS() throws Exception {

        TemplateRepresentation tr  = null;

        Context context =  getContext();
        ConcurrentMap<String,Object> attributes = context.getAttributes();
        String jsResource = (String)   attributes.get("jsResource");

        Reference ref = LocalReference.createFileReference(jsResource);
        ClientResource r = new ClientResource(ref);
        Representation templateFile = r.get();

        HashMap<String, Object> myhash = new HashMap<String,Object>();

        ArrayList<String> aL = new ArrayList<String>();

        HashMap<String,ArrayList<String>> searchMapping = (HashMap<String,ArrayList<String>>)    attributes.get("searchMapping");

        for ( String key :  searchMapping.keySet()) {
            aL.add(key);

        }

        String sruSearchURL = (String)   attributes.get("sruSearchURL");
        String sruExplainURL = (String)   attributes.get("sruExplainURL");

        myhash.put("basicActionValue", sruSearchURL);
        myhash.put("basicActionExplainValue", sruExplainURL);
        myhash.put("sruIndexes", aL);

        tr = new TemplateRepresentation(templateFile,
                myhash, MediaType.TEXT_PLAIN);

        final String result = tr.getText();
        return tr;
    }


}
