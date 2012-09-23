package org.inmobi.gigo.storage;

import org.inmobi.gigo.tsdb.TSDBStorage;

import java.io.IOException;
import java.util.Properties;

public class StorageFactory {

    public static Storage getStorage(Properties properties)
            throws IOException {

        Storage storage = new TSDBStorage();
        storage.init(properties);
        return storage;
    }
}
