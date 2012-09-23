package org.inmobi.gigo.tsdb;

import org.apache.log4j.Logger;
import org.inmobi.gigo.storage.Storage;
import org.inmobi.gigo.types.Metric;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

public class TSDBStorage implements Storage {
    private static final Logger LOG = Logger.getLogger(TSDBStorage.class);

    private static final String TSDB_HOST_NAME = "gigo.tsdb.host.name";
    private static final String TSDB_PORT = "gigo.tsdb.host.port";

    private static final int MINUTE = 60 * 1000;

    private static final DateFormat formatter =
            new SimpleDateFormat("yyyy-MM-dd HH:mm");

    static {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private String host;
    private int port;

    private Socket socket;

    public void init(Properties properties) throws IOException {
        this.host = properties.getProperty(TSDB_HOST_NAME);
        this.port = Integer.parseInt(properties.getProperty(TSDB_PORT));
        connect();
    }

    public void write(Metric metric) throws IOException {
        Row row = toRow(metric);
        socket.getOutputStream().write(row.toString().getBytes());
        socket.getOutputStream().write('\n');
    }

    private Row toRow(Metric metric) {
        Tag[] tags = new Tag[3];
        tags[0] = new Tag("emitter", metric.getEmitter());
        tags[1] = new Tag("host", metric.getSource());
        tags[2] = new Tag("timestamp", formatter.format(metric.getTimeStamp()));
        long counter = (metric.getCounters().get("DEFAULT")).longValue();
        return new Row(metric.getBucket(), tags,
                metric.getUpdateTime(), counter);
    }

    private boolean notOpen(Socket socket) {
        return socket == null || !socket.isConnected() || socket.isClosed();
    }

    private void connect() throws IOException {
        SocketAddress address = new InetSocketAddress(host, port);
        socket = new Socket();
        socket.connect(address, MINUTE);
        socket.setSoTimeout(MINUTE);
    }

    public void close() {
        try {
            if (!notOpen(socket)) {
                socket.close();
            }
        } catch (IOException ignore) { }
    }
}
