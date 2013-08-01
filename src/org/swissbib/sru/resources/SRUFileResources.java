package org.swissbib.sru.resources;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.io.File;
import java.io.IOException;

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


public class SRUFileResources extends ServerResource {

    @Get()
    public Representation getFiles() throws Exception {

        getAttribute("filename");

        StringRepresentation r = null;
        try {

            Representation file = new ClientResource(
                    LocalReference.createClapReference(getClass().getPackage())
                            + "/xslfiles/" + getAttribute("filename")).get();

            r = new StringRepresentation(file.getText(),MediaType.APPLICATION_W3C_XSLT);


            //LocalReference lr =  LocalReference.createClapReference(getClass().getPackage());

            //String s = lr.toString();

            //new ClientResource(LocalReference.createClapReference(getClass().getPackage() ) + "/xslfiles/hello.txt").
            //String hh = getClass().getPackage() + "/xslfiles/hello.txt";

            //File f = LocalReference.createClapReference(getClass().getPackage() + "/xslfiles/hello.txt").getFile();

            //r = new FileRepresentation(f, MediaType.TEXT_PLAIN);

        //Representation r = new ClientResource(LocalReference.createClapReference(getClass().getPackage()) + "/xslfiles/hello.txt").get();



            //String s1 =  r.getText();
            //System.out.println(s1);
        } catch (Exception ioE) {
            ioE.printStackTrace();
        }

        return r;

    }

}
