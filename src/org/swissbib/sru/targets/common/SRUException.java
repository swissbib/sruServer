package org.swissbib.sru.targets.common;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 * [...description of the type ...]
 * <p/>
 * <p/>
 * <p/>
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * <p/>
 * Date: 12/27/13
 * Time: 3:38 PM
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
public class SRUException extends Exception {

    private String diagDetails = null;
    private String diagMessage = null;
    private Throwable  throwable = null;
    private boolean useExceptionMessage = false;

    public SRUException(String diagDetails) {
        super();
        this.diagDetails = diagDetails;
    }


    public SRUException(String diagDetails, String diagMessage) {
        super();
        this.diagDetails = diagDetails;
        this.diagMessage = diagMessage;
    }

    public SRUException(String diagDetails, String diagMessage, Throwable throwable) {
        super(throwable);
        this.diagDetails = diagDetails;
        this.diagMessage = diagMessage;
        this.throwable = throwable;
    }

    public void setUseExceptionMessage (boolean useIt) {
        this.useExceptionMessage = useIt;
    }


    @Override
    public String getMessage() {


        return createMessageStructure();
    }

    public Representation getRepresentation() {

        return new StringRepresentation(createMessageStructure(), MediaType.TEXT_XML);

    }

    private String createMessageStructure () {

        /*
        example diagnose DNB
        <?xml version="1.0" encoding="UTF-8"?>
<searchRetrieveResponse xmlns="http://www.loc.gov/zing/srw/">
    <version>1.1</version>
    <diagnostics>
        <diag:diagnostic xmlns:diag="http://www.loc.gov/zing/srw/diagnostic/">
            <diag:uri>info:srw/diagnostic/1/4</diag:uri>
            <diag:details>Unsupported operation</diag:details>
            <diag:message>Unsupported operation / searchRetrie</diag:message>
        </diag:diagnostic>
    </diagnostics>
</searchRetrieveResponse>
         */

        StringBuilder sB = new StringBuilder();

        sB.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        sB.append("<searchRetrieveResponse xmlns=\"http://www.loc.gov/zing/srw/\">\n");
        sB.append("<version>1.1</version>\n");
        sB.append("<diagnostics>\n");
        sB.append("<diag:diagnostic xmlns:diag=\"http://www.loc.gov/zing/srw/diagnostic/\">\n");
        sB.append("<diag:uri>info:srw/diagnostic/1/4</diag:uri>\n");
        sB.append("<diag:details><![CDATA[").append(createDetails()).append("]]></diag:details>\n");
        sB.append("<diag:message><![CDATA[").append(createMessage()).append("]]></diag:message>\n");
        sB.append("</diag:diagnostic>\n");
        sB.append("</diagnostics>\n");
        sB.append("</searchRetrieveResponse>\n");
        return sB.toString();
    }

    private String createDetails() {

        StringBuilder details = new StringBuilder();

        if (this.diagDetails == null) {
            details.append("unknown details");
        }else {
            details.append(this.diagDetails);
        }

        return details.toString();
    }

    private String createMessage() {
        StringBuilder message = new StringBuilder();
        if (this.useExceptionMessage && this.throwable != null) {
            if (null != this.diagMessage) {
                message.append(this.diagMessage);
                if (this.throwable.getMessage() != null) {
                    message.append(this.throwable.getMessage());
                } else if (this.throwable.getLocalizedMessage() != null) {
                    message.append(this.throwable.getLocalizedMessage());
                }
                else {
                    for (StackTraceElement sE : this.throwable.getStackTrace()) {
                        message.append(sE.toString());
                    }
                }

            } else {
                message.append(this.throwable.getMessage());
            }

        }else if (null != this.diagMessage) {
            message.append(this.diagMessage);
        } else {
            message.append("unknown message");
        }

        return message.toString();

    }

}
