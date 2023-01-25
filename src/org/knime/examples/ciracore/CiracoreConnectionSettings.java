package org.knime.examples.ciracore;

import static java.lang.String.format;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
// import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.workflow.CredentialsProvider;
import org.knime.examples.ciracore.ui.ConnectionType;
import org.knime.examples.ciracore.ui.MongoDBOption;
//import org.knime.mongodb2.connection.node.ui.MongoDBOption;
//import org.knime.mongodb2.connection.node.ui.SettingsModelHosts;
//import org.knime.mongodb2.connection.node.ui.SettingsModelKeyValue;
import org.knime.examples.ciracore.ui.SettingsModelHosts;
import org.knime.examples.ciracore.ui.SettingsModelKeyValue;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Connection settings for MongoDB
 *
 * @author Alexander Bondaletov
 */

public class CiracoreConnectionSettings {
	
	    private static final NodeLogger LOGGER = NodeLogger.getLogger(CiracoreConnectionSettings.class);

	    private static final String UTF_8 = "UTF-8";

	    private static final String HOSTS_CFG_KEY = "hosts";
	    private static final String AUTH_CFG_KEY = "auth";
	    private static final String AUTHDB_CFG_KEY = "authenticationDB";
	    private static final String CONNECTION_TYPE_CFG_KEY = "connectionType";

	    private static final String OPTIONS_CFG_KEY = "options";

	    private final SettingsModelHosts m_hosts;
	    private final SettingsModelAuthentication m_auth;
	    private final SettingsModelString m_authdb;
	    private final SettingsModelString m_connectionType;

	    private final SettingsModelKeyValue m_options;

	    /**
	     * Creates new instance
	     */
	    public CiracoreConnectionSettings() {
	        m_hosts = new SettingsModelHosts(HOSTS_CFG_KEY);
	        m_auth = new SettingsModelAuthentication(AUTH_CFG_KEY, AuthenticationType.USER_PWD);
	        m_authdb = new SettingsModelString(AUTHDB_CFG_KEY, "admin");
	        m_connectionType = new SettingsModelString(CONNECTION_TYPE_CFG_KEY, "STANDARD");
	        m_options = new SettingsModelKeyValue(OPTIONS_CFG_KEY, MongoDBOption.ALL_OPTIONS);
	    }

	    /**
	     * @return the hosts model
	     */
	    public SettingsModelHosts getHostsModel() {
	        return m_hosts;
	    }

	    /**
	     * @return the auth
	     */
	    public SettingsModelAuthentication getAuthModel() {
	        return m_auth;
	    }

	    /**
	     * @return the authdb model
	     */
	    public SettingsModelString getAuthdbModel() {
	        return m_authdb;
	    }

	    /**
	     * @return the authdb
	     */
	    public String getAuthdb() {
	        return m_authdb.getStringValue();
	    }

	    /**
	     * @return the connectionType model
	     */
	    public SettingsModelString getConnectionTypeModel() {
	        return m_connectionType;
	    }

	    /**
	     * @return the connectionType
	     */
	    public ConnectionType getConnectionType() {
	        return ConnectionType.get(m_connectionType.getStringValue());
	    }

	    /**
	     * @return the options model
	     */
	    public SettingsModelKeyValue getOptionsModel() {
	        return m_options;
	    }

	    /**
	     * Saves the settings to the given {@link NodeSettingsWO}.
	     *
	     * @param settings The settings.
	     */
	    public void saveSettingsTo(final NodeSettingsWO settings) {
	        m_hosts.saveSettingsTo(settings);
	        m_auth.saveSettingsTo(settings);
	        m_authdb.saveSettingsTo(settings);
	        m_connectionType.saveSettingsTo(settings);
	        m_options.saveSettingsTo(settings);
	    }

	    /**
	     * Loads settings from the given {@link NodeSettingsRO}.
	     *
	     * @param settings The settings.
	     * @throws InvalidSettingsException
	     */
	    public void loadSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
	        m_hosts.loadSettingsFrom(settings);
	        m_auth.loadSettingsFrom(settings);
	        m_authdb.loadSettingsFrom(settings);
	        m_connectionType.loadSettingsFrom(settings);
	        m_options.loadSettingsFrom(settings);
	    }

	    /**
	     * Validates settings in the given {@link NodeSettingsRO}.
	     *
	     * @param settings The settings.
	     * @throws InvalidSettingsException
	     */
	    public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
	        CiracoreConnectionSettings temp = new CiracoreConnectionSettings();
	        temp.loadSettingsFrom(settings);
	        temp.validate();
	    }

	    /**
	     * Validates settings consistency for this instance.
	     *
	     * @throws InvalidSettingsException
	     */
	    public void validate() throws InvalidSettingsException {
	         m_hosts.validate();
	    }

	    /**
	     * Creates and returns a MongoClient for the settings.
	     *
	     * @param provider The credentials provider.
	     *
	     * @return the client object
	     */
	    public MongoClient createClient(final CredentialsProvider provider) {
	        final String connectionString = buildConnectionString(provider);
	        logConnectionString(connectionString);
	        return MongoClients.create(new ConnectionString(connectionString));
	    }

	    private static void logConnectionString(final String connectionString) {
	        //Replace password part of the url with '***'
	        String masked = connectionString.replaceFirst(":[^:]*@", ":***@");
	        LOGGER.debug("MongoDB connection string: " + masked);
	    }

	    private String buildConnectionString(final CredentialsProvider credentials) {

	        final StringBuilder sb = new StringBuilder("mongo");
	        sb.append("://");
	        if (m_auth.getAuthenticationType() == AuthenticationType.USER_PWD
	            || m_auth.getAuthenticationType() == AuthenticationType.CREDENTIALS) {
	            final String userName = urlEncode(m_auth.getUserName(credentials));
	            final String password = urlEncode(m_auth.getPassword(credentials), true);
	            sb.append(userName).append(":").append(password).append("@");
	        }

	        sb.append(m_hosts.getHostsString());
	        sb.append("/");
	        if (m_authdb.isEnabled() && StringUtils.isNotBlank(m_authdb.getStringValue())) {
	            sb.append(urlEncode(m_authdb.getStringValue()));
	        }

	        final List<String> options = m_options.getAssignedKeys();
	        if (options.size() > 0) {
	            sb.append("?");
	            sb.append(options.stream().map(key -> key + "=" + urlEncode(m_options.getValue(key)))
	                .collect(Collectors.joining("&")));
	        }

	        return sb.toString();
	    }

	    private static String urlEncode(final String input) {
	        return urlEncode(input, false);
	    }

	    private static String urlEncode(final String input, final boolean password) {
	        if (input == null) {
	            return null;
	        }
	        //see com.mongodb.ConnectionString.urldecode(final String input, final boolean password)
	        try {
	            return URLEncoder.encode(input, UTF_8);
	        } catch (UnsupportedEncodingException e) {
	            if (password) {
	                throw new IllegalArgumentException("The password contains unsupported characters.");
	            } else {
	                throw new IllegalArgumentException(format("The connection string contains unsupported characters: '%s'."
	                        + "Encoding produced the following error: %s", input, e.getMessage()));
	            }
	        }
	    }
}
