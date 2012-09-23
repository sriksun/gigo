package org.inmobi.gigo.tsdb;

public class Tag {

    private String key;
    private String value;

    private static final String regEx = "[^a-zA-Z0-9/\\.\\-_\\*]";

    public Tag(String key, String value) {
        if (null != key) {
            this.key = key.replaceAll(regEx, "_");
        }
        if (null != value) {
            this.value = value.replaceAll(regEx, "_");
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        if (null == value || 0 == value.trim().length()) {
            return "NA";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toCommand() {
        return key + "=" + value;
    }

    public String toString() {
        return "{key='" + key + ", value='" + value + '}';
    }
}
