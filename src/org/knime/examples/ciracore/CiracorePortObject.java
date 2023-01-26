package org.knime.examples.ciracore;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

import com.mongodb.client.MongoClient;

public class CiracorePortObject extends AbstractSimplePortObject {

	 /** Standard type. */
    @SuppressWarnings("hiding")
    public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(CiracorePortObject.class);

    /**
     * Serializer class
     */
    public static final class Serializer extends AbstractSimplePortObjectSerializer<CiracorePortObject> {
    }

//    private MongoDBConnectionPortObjectSpec m_spec;
    private  MongoDBConnectionPortObjectSpec m_spec = new MongoDBConnectionPortObjectSpec();
    
    /**
     * Default constructor
     */
    public CiracorePortObject() {
        this(null);
    }

    /**
     * @param spec The {@link MongoDBConnectionPortObjectSpec} object.
     *
     */
    public CiracorePortObject(final MongoDBConnectionPortObjectSpec spec) {
        m_spec = spec;
    }

    /**
     * @return the {@link MongoClient} if available.
     * @throws InvalidSettingsException if the client is no longer available
     */
    public MongoClient getClient() throws InvalidSettingsException {
        return m_spec.getClient().orElseThrow(() -> new InvalidSettingsException(
            "MongoDB connection no longer available. Please re-execute the MongoDB Connector node."));
    }
    
	@Override
	public String getSummary() {
		return "UID:" + m_spec.getConnectionId();
	}

	@Override
	public PortObjectSpec getSpec() {
		return m_spec;
	}

	@Override
	protected void save(ModelContentWO model, ExecutionMonitor exec) throws CanceledExecutionException {
		// nothing to do here
	}

	@Override
	protected void load(ModelContentRO model, PortObjectSpec spec, ExecutionMonitor exec)
			throws InvalidSettingsException, CanceledExecutionException {
		if (spec instanceof MongoDBConnectionPortObjectSpec) {
            m_spec = (MongoDBConnectionPortObjectSpec)spec;
        }

	}
	
	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CiracorePortObject [spec=");
        builder.append(getSpec().toString());
        builder.append(']');
        return builder.toString();
    }

}
