package org.swissbib.sru.targets.common;

import java.util.Map;

@FunctionalInterface
public interface ResponseHeaderCreator {

    String createStructure(Map<String,String> values,
                            String templatePath);


}
