package org.swissbib.sru.targets.common;

import org.apache.commons.lang.text.StrSubstitutor;
import org.swissbib.sru.resources.RequestedSchema;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

import static  java.util.Comparator.comparing;


public class ResponseCreator {

    final String versionNumber = "1.1";

    private static  Map <RequestedSchema,ResponseHeaderCreator> headerCreators;

    public static ResponseHeaderCreator responseXMLHeaderCreator = (Map<String,String> valuesMap,
                                                                    String templatePath) -> {

        String headerTemplate = readResource(templatePath);
        //valuesMap.put("versionNumber", versionNumber);
        StrSubstitutor sub = new StrSubstitutor(valuesMap);
        return sub.replace(headerTemplate);
    };


    static {
        headerCreators = new HashMap<>();
        headerCreators.put(RequestedSchema.dcswissbib,responseXMLHeaderCreator);
    }







    private static String readResource(String path) {

        String s = "";
        s.compareToIgnoreCase("");

        //List<String> names = Arrays.asList("Mal", "Wash", "Kaylee", "Inara",    "Zoë", "Jayne", "Simon", "River", "Shepherd Book");
        //Optional<String> first = names.stream().filter(name -> name.startsWith("C")).findFirst();

        //names.sort(comparing(Apple::getWeight));


        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();


        //path has to be reachable from the Classpath (subdirs of WEB-INF/classes
        try (InputStream xmlHeader = ResponseCreator.class.getClassLoader()
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
