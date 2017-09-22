package org.swissbib.sru.targets.common;

import org.apache.commons.lang.text.StrSubstitutor;
import java.io.*;
import java.util.Map;

public class ResponseCreator {

    final String versionNumber = "1.1";


    public ResponseHeaderCreator responseXMLHeaderCreator = (Map<String,String> valuesMap,
                                                            String templatePath) -> {

        String headerTemplate = this.readResource(templatePath);
        valuesMap.put("versionNumber", versionNumber);
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(headerTemplate);
    };



    private String readResource(String path) {

        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();


        //path has to be reachable from the Classpath (subdirs of WEB-INF/classes
        try (InputStream xmlHeader = this.getClass().getClassLoader()
                .getResourceAsStream(path)) {

            Reader in = new InputStreamReader(xmlHeader, "UTF-8");
            for (; ; ) {
                int numberOfReadChars = in.read(buffer, 0, buffer.length);
                if (numberOfReadChars < 0)
                    break;
                out.append(buffer, 0, numberOfReadChars);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }

        return out.toString();

    }

}
