package org.knime.examples.ciracore;

import java.io.File;
import java.io.IOException;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.mongodb2.connection.MongoDBConnectionRegistry;
import org.knime.mongodb2.connection.port.MongoDBConnectionPortObject;
import org.knime.mongodb2.connection.port.MongoDBConnectionPortObjectSpec;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase; 



/**
 * This is an example implementation of the node model of the
 * "Ciracore" node.
 * 
 * This example node performs simple number formatting
 * ({@link String#format(String, Object...)}) using a user defined format string
 * on all double columns of its input table.
 *
 * @author dv
 */
public class CiracoreNodeModel extends NodeModel {
	 /**
	 * The logger is used to print info/warning/error messages to the KNIME console
	 * and to the KNIME log file. Retrieve it via 'NodeLogger.getLogger' providing
	 * the class of this node model.
	 */
	private static final NodeLogger LOGGER = NodeLogger.getLogger(CiracoreNodeModel.class);


	private final CiracoreConnectionSettings m_settings = new CiracoreConnectionSettings();

    private String m_connectionId;

    private MongoClient m_client;

    /**
     * Creates new instance
     */
    protected CiracoreNodeModel() {
        super(new PortType[]{} , new PortType[]{ PortObject.TYPE });
    }

    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {
    	LOGGER.info("This is configure method.");
    	LOGGER.info("configure() : " + inSpecs);
    	m_connectionId = CiracoreConnectionRegistry.getInstance().getKey();
    	LOGGER.info("configure() :  m_connectionId =  " + m_connectionId);
        return new PortObjectSpec[]{createSpec()};
    }

    private MongoDBConnectionPortObjectSpec createSpec() {
    	LOGGER.info("This is createSpec() method.");
    	LOGGER.info("createSpec() : m_connectionId = " + m_connectionId);
        return new MongoDBConnectionPortObjectSpec(m_connectionId);
    }

    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {
    	LOGGER.info("This is execute method.");
    	m_client = m_settings.createClient(getCredentialsProvider());
    	LOGGER.info("execute() : m_client = " + m_client);
    	MongoDBConnectionRegistry.getInstance().register(m_connectionId, m_client);

        //Make a call to validate connection and auth settings
        m_client.listDatabases().first();

        return new PortObject[]{ new CiracorePortObject(createSpec()) };
    }


    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        setWarningMessage("Connection no longer available. Please re-execute the node.");
    }

    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        // nothing to save
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_settings.saveSettingsTo(settings);
    }

    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.validateSettings(settings);
    }

    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_settings.loadSettingsFrom(settings);
    }

    @Override
    protected void reset() {
        if (m_client != null) {
        	MongoDBConnectionRegistry.getInstance().deregister(m_connectionId);
            m_client = null;
        }
        m_connectionId = null;
    }

    @Override
    protected void onDispose() {
        reset();
    }
}

