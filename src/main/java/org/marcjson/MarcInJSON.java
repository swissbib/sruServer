/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.marcjson;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.marc4j.marc.impl.Verifier;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.VariableField;
import org.marc4j.marc.Subfield;


/**
 *
 * @author dueberb (https://www.lib.umich.edu/users/dueberb)
 * https://github.com/billdueber/marc4j_extra_reader_writers/blob/master/src/org/marc4j/MarcInJSON.java
 */
public class MarcInJSON {

    static private final MarcFactory factory = MarcFactory.newInstance();
    static private final ObjectMapper mapper= new ObjectMapper();
    
    static public Record new_from_hash(Map<String, Object> m) {
        Record record = factory.newRecord();

        // Set the leader
        String leaderstr = (String) m.get("leader");
        record.setLeader(factory.newLeader(leaderstr));
        ArrayList<Map> fields = (ArrayList<Map>) m.get("fields");
        for (Map f :   fields) {
            String tag = (String) f.keySet().toArray()[0];
            if (Verifier.isControlField(tag)) {
                String value = (String) f.get(tag);
                record.addVariableField(factory.newControlField(tag, value));
            } else {
                char ind1 = ' ';
                char ind2 = ' ';
                Map fdata = (Map<String, Object>) f.get(tag);
                if (fdata.containsKey("ind1")) {
                    ind1 = ((String) fdata.get("ind1")).charAt(0);
                }
                if (fdata.containsKey("ind2")) {
                    ind2 = ((String) fdata.get("ind2")).charAt(0);
                }

                DataField df = factory.newDataField(tag, ind1, ind2);
                ArrayList<Map> subfields = (ArrayList<Map>) fdata.get("subfields");

                for (Map<String, String> sub : subfields) {
                    String code = (String) sub.keySet().toArray()[0];
                    df.addSubfield(factory.newSubfield(code.charAt(0), sub.get(code)));
                }
                record.addVariableField(df);
            }
        }


        return record;

    }

    static public Record new_from_marc_in_json(String str) throws java.io.IOException {
        return new_from_hash(mapper.readValue(str, Map.class));
    }

    static public String record_to_marc_in_json(Record r) throws java.io.IOException  {
        HashMap h = record_to_hash(r);
        return mapper.writeValueAsString(h);
    }

    static public HashMap record_to_hash(Record r) {
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("leader", r.getLeader().toString());
        ArrayList fields = new ArrayList();
        m.put("fields", fields);
        for (Object vfo : r.getVariableFields()) {
            VariableField vf = (VariableField) vfo;
            String tag = vf.getTag();
            if (Verifier.isControlField(tag)) {
                HashMap<String, String> fmap = new HashMap();
                ControlField cf = (ControlField) vf;
                fmap.put(tag, cf.getData());
                fields.add(fmap);
            } else {
                HashMap<String, HashMap> fmap = new HashMap();
                DataField df = (DataField) vf;
                HashMap<String, Object> fdata = new HashMap();
                fdata.put("ind1", String.valueOf(df.getIndicator1()));
                fdata.put("ind2", String.valueOf(df.getIndicator2()));
                ArrayList subfields = new ArrayList();
                fdata.put("subfields", subfields);
                for (Object sfo : df.getSubfields()) {
                    Subfield sf = (Subfield) sfo;
                    HashMap<String, String> sfmap = new HashMap();
                    sfmap.put(String.valueOf(sf.getCode()), sf.getData());
                    subfields.add(sfmap);
                }
                fmap.put(tag, fdata);
                fields.add(fmap);
            }

        }

        return m;
    }

}
