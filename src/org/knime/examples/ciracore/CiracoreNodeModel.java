package org.knime.examples.ciracore;

import java.io.File;
import java.io.IOException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.*;
import org.knime.core.node.port.PortType;
import org.knime.core.node.streamable.OutputPortRole;
import org.knime.core.node.port.database.*;
import com.mongodb.client.MongoDatabase; 
import com.mongodb.client.*; 


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

	/**
	 * The settings key to retrieve and store settings shared between node dialog
	 * and node model. In this case, the key for the number format String that
	 * should be entered by the user in the dialog.
	 */
	private static final String KEY_NUMBER_FORMAT = "number_format";
	private static final String KEY_HOST = "host";
	private static final String KEY_PORT = "port";

	/**
	 * The default number format String. This default will round to three decimal
	 * places. For an explanation of the format String specification please refer to
	 * https://docs.oracle.com/javase/tutorial/java/data/numberformat.html
	 */
	private static final String DEFAULT_NUMBER_FORMAT = "%.3f";
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 27017;
	private MongoClient m_client;

	/**
	 * The settings model to manage the shared settings. This model will hold the
	 * value entered by the user in the dialog and will update once the user changes
	 * the value. Furthermore, it provides methods to easily load and save the value
	 * to and from the shared settings (see:
	 * <br>
	 * {@link #loadValidatedSettingsFrom(NodeSettingsRO)},
	 * {@link #saveSettingsTo(NodeSettingsWO)}). 
	 * <br>
	 * Here, we use a SettingsModelString as the host is a String and port is an Integer. 
	 * There are models for all common data types. Also have a look at the comments 
	 * in the constructor of the {@link CiracoreNodeDialog} as the settings 
	 * models are also used to create simple dialogs.
	 */
	// private final SettingsModelString m_numberFormatSettings = createNumberFormatSettingsModel();
	private final SettingsModelString m_hostSettings = createHostSettingsModel();
	private final SettingsModelInteger m_portSettings = createPortSettingsModel();

	/**
	 * Constructor for the node model.
	 */
	protected CiracoreNodeModel() {
		/**
		 * Here we specify how many data input and output tables the node should have.
		 * In this case its one input and one output table.
		 */
		super(new PortType[] { } , new PortType[] {ConnectionPortObject.TYPE });
	}

	/**
	 * A convenience method to create a new settings model used for the host and the port
	 * This method will also be used in the {@link CiracoreNodeDialog}. 
	 * The settings model will sync via the above defined key.
	 * 
	 * @return a new SettingsModelString with the key for the number format String
	 */
//	static SettingsModelString createNumberFormatSettingsModel() {
//		return new SettingsModelString(KEY_NUMBER_FOMAT, DEFAULT_NUMBER_FORMAT);
//	}
	static SettingsModelString createHostSettingsModel() {
		return new SettingsModelString(KEY_HOST, DEFAULT_HOST);
	}
	static SettingsModelInteger createPortSettingsModel() {
		return new SettingsModelInteger(KEY_PORT, DEFAULT_PORT);
	}
	
//	/** {@inheritDoc} */
//	@Override
//	public OutputPortRole[] getOutputPortRoles()
//	{
//		String uniqueID = "The_MngoDB_Connection";
//		SettingsModel outputPort = SettingsModel.createPortType(uniqueID, ConnectionPortObject); 
//		return new OutputPortRole[] { OutputPortRole.NONDISTRIBUTED };
//	}
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inPort, final ExecutionContext exec)
			throws Exception {
		/*
		 * The functionality of the node is implemented in the execute method. This
		 * implementation will format each double column of the input table using a user
		 * provided format String. The output will be one String column for each double
		 * column of the input containing the formatted number from the input table. For
		 * simplicity, all other columns are ignored in this example.
		 * 
		 * Some example log output. This will be printed to the KNIME console and KNIME
		 * log.
		 */
		LOGGER.info("This is execute.");

		String uri = "mongodb://" + m_hostSettings.getStringValue() + ":" + m_portSettings.getIntValue();
		m_client = MongoClients.create(uri);
	    ConnectionPortObject output = new ConnectionPortObject(m_client, DEFAULT_HOST, DEFAULT_PORT, "daviddb");
	    return new PortObject[]{output};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
		/*
		 * Check if the node is executable, e.g. all required user settings are
		 * available and valid, or the incoming types are feasible for the node to
		 * execute. In case the node can execute in its current configuration with the
		 * current input, calculate and return the table spec that would result of the
		 * execution of this node. I.e. this method precalculates the table spec of the
		 * output table.
		 * 
		 * Here we perform a sanity check on the entered number format String. In this
		 * case we just try to apply it to some dummy double number. If there is a
		 * problem, an IllegalFormatException will be thrown. We catch the exception and
		 * wrap it in a InvalidSettingsException with an informative message for the
		 * user. The message should make clear what the problem is and how it can be
		 * fixed if this information is available. This will be displayed in the KNIME
		 * console and printed to the KNIME log. The log will also contain the stack
		 * trace.
		 */
//		String format = m_numberFormatSettings.getStringValue();
//		try {
//			String.format(format, 0.0123456789);
//		} catch (IllegalFormatException e) {
//			throw new InvalidSettingsException(
//					"The entered format is not a valid pattern String! Reason: " + e.getMessage(), e);
//		}

		/*
		 * Similar to the return type of the execute method, we need to return an array
		 * of DataTableSpecs with the length of the number of outputs ports of the node
		 * (as specified in the constructor). The resulting table created in the execute
		 * methods must match the spec created in this method. As we will need to
		 * calculate the output table spec again in the execute method in order to
		 * create a new data container, we create a new method to do that.
		 */
		// Creating a Mongo client 
//	    MongoClient mongo = new MongoClient( m_hostSettings.getStringValue(), m_portSettings.getIntValue() ); 
		// DataTableSpec inputTableSpec = inSpecs[0];
		// return new DataTableSpec[] { createOutputSpec(inputTableSpec) };
		return new DataTableSpec[] { null };
	}

	/**
	 * Creates the output table spec from the input spec. For each double column in
	 * the input, one String column will be created containing the formatted double
	 * value as String.
	 * 
	 * @param inputTableSpec
	 * @return
	 */
	private DataTableSpec createOutputSpec(DataTableSpec inputTableSpec) {
		List<DataColumnSpec> newColumnSpecs = new ArrayList<>();
		// Iterate over the input column specs
		for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
			DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);
			/*
			 * If the column is a double column (hence there are double cells), we create a
			 * new DataColumnSpec with column type String and a new column name. Here, we
			 * wrap the original column name with 'Formatted(...)'.
			 */
			if (columnSpec.getType().getCellClass().equals(DoubleCell.class)) {
				String newName = "Formatted(" + columnSpec.getName() + ")";
				DataColumnSpecCreator specCreator = new DataColumnSpecCreator(newName, StringCell.TYPE);
				newColumnSpecs.add(specCreator.createSpec());
			}
		}

		// Create and return a new DataTableSpec from the list of DataColumnSpecs.
		DataColumnSpec[] newColumnSpecsArray = newColumnSpecs.toArray(new DataColumnSpec[newColumnSpecs.size()]);
		return new DataTableSpec(newColumnSpecsArray);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
		/*
		 * Save user settings to the NodeSettings object. SettingsModels already know how to
		 * save them self to a NodeSettings object by calling the below method. In general,
		 * the NodeSettings object is just a key-value store and has methods to write
		 * all common data types. Hence, you can easily write your settings manually.
		 * See the methods of the NodeSettingsWO.
		 */
		m_hostSettings.saveSettingsTo(settings);
		m_portSettings.saveSettingsTo(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * Load (valid) settings from the NodeSettings object. It can be safely assumed that
		 * the settings are validated by the method below.
		 * 
		 * The SettingsModel will handle the loading. After this call, the current value
		 * (from the view) can be retrieved from the settings model.
		 */
		m_hostSettings.loadSettingsFrom(settings);
		m_portSettings.loadSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
		/*
		 * Check if the settings could be applied to our model e.g. if the user provided
		 * format String is empty. In this case we do not need to check as this is
		 * already handled in the dialog. Do not actually set any values of any member
		 * variables.
		 */
		m_hostSettings.validateSettings(settings);
		m_portSettings.validateSettings(settings);
	}

	@Override
	protected void loadInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything that is
		 * handed to the output ports is loaded automatically (data returned by the execute
		 * method, models loaded in loadModelContent, and user settings set through
		 * loadSettingsFrom - is all taken care of). Only load the internals
		 * that need to be restored (e.g. data used by the views).
		 */
	}

	@Override
	protected void saveInternals(File nodeInternDir, ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {
		/*
		 * Advanced method, usually left empty. Everything
		 * written to the output ports is saved automatically (data returned by the execute
		 * method, models saved in the saveModelContent, and user settings saved through
		 * saveSettingsTo - is all taken care of). Save only the internals
		 * that need to be preserved (e.g. data used by the views).
		 */
	}

	@Override
	protected void reset() {
		/*
		 * Code executed on a reset of the node. Models built during execute are cleared
		 * and the data handled in loadInternals/saveInternals will be erased.
		 */
	}
}

