package org.tbwork.anole.loader.core.model;

import lombok.Data;
import org.tbwork.anole.loader.util.FileUtil;
import org.tbwork.anole.loader.util.StringUtil;

import java.io.InputStream;
import java.util.Map;

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
