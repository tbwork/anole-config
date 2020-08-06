package org.tbwork.anole.loader.core.model;


import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 原始KV数据
 */
@Data
public class RawKV {


    /**
     * 配置Key
     */
    private String key;

    /**
     * 配置值
     */
    private String value;

    public RawKV(String [] parts){
        this.key = parts[0].trim();
        this.value = parts[1].trim();
    }
}
