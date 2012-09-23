package org.inmobi.gigo.storage;

import org.inmobi.gigo.types.Metric;

import java.io.IOException;
import java.util.Properties;

public interface Storage {

    void init(Properties properties) throws IOException;

    void write(Metric metric) throws IOException;

    void close();
}
