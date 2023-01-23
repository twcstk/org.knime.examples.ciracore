package org.knime.examples.ciracore;

import java.io.IOException;

import javax.swing.JComponent;

import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.database.*;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;

import com.mongodb.connection.*;
import com.mongodb.client.*; 

public class ConnectionPortObject implements PortObject {
//	private final ConnectionId conn = null;
//	public ConnectionPortObject(final Connection connection) {
//        this.connection = connection;
//    }
//    public Connection getConnection() {
//        return connection;
//    }
    //implement the serialization methods

	private final MongoClient m_mongoClient;
    private final String m_host;
    private final int m_port;
    private final String m_database;
    private final MongoDBConnectionPortObjectSpec m_spec = new MongoDBConnectionPortObjectSpec();
    
    public void save(final PortObjectZipOutputStream out) throws IOException {
        // Serialize the connection object
    }

    public void load(final PortObjectZipInputStream in) throws IOException {
        // Deserialize the connection object
    }

	@Override
	public PortObjectSpec getSpec() {
		// TODO Auto-generated method stub
		return m_spec;
	}

	@Override
	public JComponent[] getViews() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

    public ConnectionPortObject(final MongoClient mongoClient, final String host, final int port, final String database) {
        m_mongoClient = mongoClient;
        m_host = host;
        m_port = port;
        m_database = database;
    }

    public MongoClient getMongoClient() {
        return m_mongoClient;
    }

    public String getHost() {
        return m_host;
    }

    public int getPort() {
        return m_port;
    }

    public String getDatabase() {
        return m_database;
    }

    @Override
    public String getSummary() {
        return "MongoDB Connection PortObject: host=" + m_host + ", port=" + m_port + ", database=" + m_database;
    }

}
