package org.inmobi.gigo.tsdb;

public class Row {

    //metric-name, timestamp, value, tag=value
    public static final String PUT_STRING = "put %s %s %s%s";
    public static final String STRING = "%s %s %s%s";

    private static final String regEx = "[^a-zA-Z0-9/\\.\\-_]";

    private String metric;
    private Tag[] tags;
    private Long timestamp;
    private long value;

    public Row(String metric, Tag[] tags, Long timestamp, long value) {
        this.metric = metric;
        this.tags = tags;
        if (null != tags) {
            for (Tag tag : tags) {
                tag.setValue(tag.getValue().replaceAll(regEx, "_"));
            }
        }
        this.timestamp = timestamp;
        this.value = value;
    }

    public String getMetric() {
        return metric;
    }

    public Tag[] getTags() {
        return tags;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public long getValue() {
        return value;
    }

    public String toCommand() {
        return String.format(PUT_STRING, getMetric(), getTimestamp(), getValue(), getTagString(getTags()));
    }

    public String toString() {
        return String.format(STRING, getMetric(), getTimestamp(), getValue(), getTagString(getTags()));
    }

    private String getTagString(Tag[] elements) {
        if (null == elements || 0 == elements.length) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length - 1; i++) {
            sb.append(" ").append(elements[i].toCommand()).append(" ");
        }
        if (elements.length != 0) {
            sb.append(" ").append(elements[elements.length - 1].toCommand()).append(" ");
        }
        return sb.toString();
    }
}
