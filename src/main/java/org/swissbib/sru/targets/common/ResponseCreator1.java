package org.swissbib.sru.targets.common;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.Map;
import java.util.function.Function;

public class ResponseCreator1 implements Function <SolrDocument, String> {





    private Map values;

    public ResponseCreator1(Map<String,String> values) {
        this.values = values;
    }

    public String addHeader(Map<String,String> values) {
        return "";
    }

    public static String addDocuments(Map<String,String> values, SolrDocumentList docs) {
        return "";
    }

    public static String addDocuments1(Map<String,String> values) {
        return "hello again";
    }


    public String addFooter() {
        return "";
    }


    @Override
    public String apply(SolrDocument entries) {
        return "so doch nicht";
    }
}
