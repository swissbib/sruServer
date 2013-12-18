package org.swissbib.sru.resources;

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



public enum RequestedSchema {

    dcOCLC("info:srw/schema/1/dc-v1.1"),
    dcswissbib("info:sru/schema/1/dc-v1.1-light"),
    marcOCLC("info:srw/schema/1/marcxml-v1.1"),
    marcswissbib("info:sru/schema/1/marcxml-v1.1-light");

    private final String value;

    private RequestedSchema(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }



}
