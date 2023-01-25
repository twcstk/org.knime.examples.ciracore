package org.knime.examples.ciracore;


import java.util.Optional;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;
import org.knime.core.node.port.PortObjectSpec;
// import org.knime.mongodb2.connection.MongoDBConnectionRegistry;

import com.mongodb.client.MongoClient;

public class CiracorePortObjectSpec extends AbstractSimplePortObjectSpec {
    /**
     * Serializer class
     */
    public static final class Serializer
        extends AbstractSimplePortObjectSpecSerializer<CiracorePortObjectSpec> {
    }

    private static final String CFG_CONNECTION_ID = "connectionId";

    private String m_connectionId;

    /**
     * Default constructor.
     */
    public CiracorePortObjectSpec() {
        this(null);
    }

    /**
     * @param connectionId The connection id.
     */
    public CiracorePortObjectSpec(final String connectionId) {
        m_connectionId = connectionId;
    }

    /**
     * @return the connection id
     */
    public String getConnectionId() {
        return m_connectionId;
    }

    /**
     * @return the {@link MongoClient} if available.
     */
    public Optional<MongoClient> getClient() {
        return CiracoreConnectionRegistry.getInstance().retrieve(m_connectionId);
    }
	@Override
	protected void save(ModelContentWO model) {
		   model.addString(CFG_CONNECTION_ID, m_connectionId);
	}

	@Override
	protected void load(ModelContentRO model) throws InvalidSettingsException {
		m_connectionId = model.getString(CFG_CONNECTION_ID);
	}

	 @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CiracorePortObjectSpec [m_connectionId=");
        builder.append(m_connectionId);
        builder.append(']');
        return builder.toString();
    }
}
