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
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/21/13
 * Time: 8:10 AM
 * To change this template use File | Settings | File Templates.
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
