package org.swissbib.sru.targets.common;

import java.util.ArrayList;


import java.util.Collections;
import java.util.HashMap;

/**
 * [...description of the type ...]
 * <p/>
 * <p/>
 * <p/>
 * Copyright (C) project swissbib, University Library Basel, Switzerland
 * http://www.swissbib.org  / http://www.swissbib.ch / http://www.ub.unibas.ch
 * <p/>
 * Date: 1/7/14
 * Time: 11:24 AM
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
public class UtilsCQLRelationsIndexMapping {

    HashMap<String, RelationMappings>  mappings = new HashMap<String, RelationMappings>();

    private class RelationMappings {

        private String defaultRelation;
        private ArrayList<String> allowedRelations;

        RelationMappings (String defaultRelation, ArrayList<String> allowedRelations ) {

            this.defaultRelation = defaultRelation;

            ArrayList<String> relationsCaseInsensitive = new ArrayList<String>(allowedRelations.size());
            for (String r : allowedRelations) {
                relationsCaseInsensitive.add(r.toLowerCase());
            }

            this.allowedRelations = relationsCaseInsensitive;

        }

        public boolean isRelationAllowed (String relation) {
            return this.allowedRelations.contains(relation.toLowerCase());
        }

        public String getDefaultRelation ()  {
            return this.defaultRelation;
        }

    }


    public void addMapping(String indexName, String mappings) {

        String allowedRelations = mappings.substring(0,mappings.lastIndexOf("!"));
        String defaultRelation = mappings.substring(mappings.lastIndexOf("!") + 1);

        ArrayList<String> aM = new ArrayList<String>();
        Collections.addAll(aM,allowedRelations.split("#"));

        RelationMappings rm = new RelationMappings(defaultRelation,aM);

        this.mappings.put(indexName,rm);



    }

    public boolean isRelationAllowed(String indexName, String relation) {
        return mappings.get(indexName).isRelationAllowed(relation);
    }

    public String getDefaultRelation(String indexName) {
        return mappings.get(indexName).getDefaultRelation();
    }




}
