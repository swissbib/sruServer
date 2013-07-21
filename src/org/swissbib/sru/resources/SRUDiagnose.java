package org.swissbib.sru.resources;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.swissbib.sru.targets.solr.SolrStringRepresenation;

/**
 * Created with IntelliJ IDEA.
 * User: swissbib
 * Date: 7/21/13
 * Time: 10:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class SRUDiagnose extends ServerResource {

    @Get()
    public Representation getFiles() throws Exception {

        Representation r = new StringRepresentation("hello", MediaType.TEXT_PLAIN);





        return r;



    }

}
