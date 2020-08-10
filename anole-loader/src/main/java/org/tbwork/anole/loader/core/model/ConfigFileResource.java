package org.tbwork.anole.loader.core.model;

import lombok.Data;

import java.io.InputStream;

@Data
public class ConfigFileResource {

    private String fullPath;
    private InputStream inputStream;
    private Integer order;


    public ConfigFileResource( String fullPath, InputStream inputStream, int order) {

        this.fullPath = fullPath;
        this.inputStream = inputStream;
        this.order = order;
    }
}
