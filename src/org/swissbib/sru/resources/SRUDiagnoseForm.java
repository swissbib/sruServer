package org.swissbib.sru.resources;

import org.restlet.Context;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

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


public class SRUDiagnoseForm extends ServerResource {

    @Get()
    public Representation getForm() throws Exception {

        //short explanation velocity template language
        //http://www.gruntz.ch/courses/sem/ss02/velocity.pdf
        TemplateRepresentation tr  = null;

        try {

            Context context =  getContext();
            ConcurrentMap<String,Object> attributes = context.getAttributes();
            String formResource = (String)   attributes.get("formResource");


            Reference ref = LocalReference.createFileReference(formResource);
            ClientResource r = new ClientResource(ref);
            Representation templateFile = r.get();

            HashMap<String, Object> myhash = new HashMap<String,Object>();

            String sruSearchURL = (String)   attributes.get("sruSearchURL");


            myhash.put("sruSearchURL", sruSearchURL);
            tr = new TemplateRepresentation(templateFile,
                    myhash, MediaType.TEXT_HTML);
            //final String result = tr.getText();

        } catch (Throwable e) {

            e.printStackTrace();

        }

        return tr;
    }

}
