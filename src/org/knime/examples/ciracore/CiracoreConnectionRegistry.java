package org.knime.examples.ciracore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.mongodb.client.MongoClient;

public class CiracoreConnectionRegistry {
	private static CiracoreConnectionRegistry INSTANCE;

    private final Map<String, MongoClient> m_connections;

    private CiracoreConnectionRegistry() {
        m_connections = new HashMap<>();
    }

    /**
     * Returns the instance of this registry.
     *
     * @return the instance of this registry
     */
    public static synchronized CiracoreConnectionRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CiracoreConnectionRegistry();
        }
        return INSTANCE;
    }

    /**
     * Registers a connection to the registry.
     *
     * @param key the unique key for the session
     * @param client the connection to be registered
     * @see MongoDBConnectionRegistry#getKey()
     */
    @SuppressWarnings("resource")
    public synchronized void register(final String key, final MongoClient client) {
        final MongoClient existing = m_connections.get(key);
        if (existing != null) {
            if (!existing.equals(client)) {
                throw new IllegalArgumentException("Different connection with key: " + key + " already exists");
            }
            return;
        }
        m_connections.put(key, client);
    }

    /**
     * @return a new unique key that can be used to register a {@link MongoClient}
     */
    public String getKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * Retrieve a connection for the given key.
     *
     * @param key key for the connection to be retrieved
     * @return Optional containing the connection, if present
     */
    @SuppressWarnings("resource")
    public synchronized Optional<MongoClient> retrieve(final String key) {
        final MongoClient client = m_connections.get(key);
        if (client != null) {
            return Optional.of(client);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deregister connection from the registry and closes the client connection.
     *
     * @param key key for the connection to be deregistered
     */
    public synchronized void deregister(final String key) {
        try (MongoClient client = m_connections.remove(key)){
            client.close();
        }
    }

    /**
     * Checks if the provided key has a connection in the registry.
     *
     * @param key key for the connection
     * @return true if the key is in the registry
     */
    public synchronized boolean contains(final String key) {
        return m_connections.containsKey(key);
    }
	
	

}
