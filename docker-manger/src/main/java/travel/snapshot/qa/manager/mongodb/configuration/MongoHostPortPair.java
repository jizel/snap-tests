package travel.snapshot.qa.manager.mongodb.configuration;

/**
 * Helper class which pairs together Mongo host and port in case you need to construct connection URL with more Mongo
 * instances in case you are clustering them.
 */
public class MongoHostPortPair {

    private final String host;
    private final int port;

    public MongoHostPortPair(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MongoHostPortPair that = (MongoHostPortPair) o;

        if (port != that.port) return false;
        return host.equals(that.host);

    }

    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        return result;
    }
}