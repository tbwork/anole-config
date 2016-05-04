package org.anole.infrastructure.model;

public class AnoleConfigItemWithBLOBs extends AnoleConfigItem {
    private String value;

    private String description;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}