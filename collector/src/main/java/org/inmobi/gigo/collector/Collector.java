package org.inmobi.gigo.collector;

import com.inmobi.messaging.ClientConfig;
import com.inmobi.messaging.Message;
import com.inmobi.messaging.consumer.MessageConsumer;
import com.inmobi.messaging.consumer.MessageConsumerFactory;
import org.apache.log4j.Logger;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TCompactProtocol;
import org.inmobi.gigo.storage.Storage;
import org.inmobi.gigo.storage.StorageFactory;
import org.inmobi.gigo.types.Metric;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Collector {
    private static final Logger LOG = Logger.getLogger(Collector.class);

    private static final TDeserializer DESERIALIZER =
            new TDeserializer(new TCompactProtocol.Factory());

    private static Boolean inited = false;

    public synchronized static void start(Properties properties)
            throws IOException  {
        if (inited) {
            throw new IOException("Service already initialized");
        } else {
            Thread service = new ConsumerService(properties);
            inited = true;
            service.start();
        }
    }

    private static class ConsumerService extends Thread {

        private final MessageConsumer consumer;
        private final Storage storage;

        public ConsumerService(Properties properties) throws IOException {
            LOG.info("Initializing Collector Service");
            Map<String, String> propertyMap = new HashMap<String, String>();
            for (Object key : properties.keySet()) {
                String keyString = key.toString();
                propertyMap.put(keyString, properties.getProperty(keyString));
            }
            ClientConfig clientConfig = new ClientConfig(propertyMap);
            consumer = MessageConsumerFactory.create(clientConfig);
            storage = StorageFactory.getStorage(properties);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Message message = consumer.next();
                    Metric metric = new Metric();
                    DESERIALIZER.deserialize(metric, message.getData().array());
                    storage.write(metric);
                } catch (InterruptedException e) {
                    LOG.info("Thread interrupted, Exiting ....");
                    break;
                } catch (Throwable t) {
                    LOG.error("Unable to read message", t);
                }
            }
            storage.close();
        }

        @Override
        public String toString() {
            return "ConsumerService" + super.toString();
        }
    }
}
