/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   2021-05-15 (Alexander Bondaletov): created
 */
package org.knime.examples.ciracore.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.port.PortObjectSpec;

/**
 * {@link SettingsModel} implementation to store hosts settings. Host-port pairs are saved as strings array in a form of
 * "host:port".
 *
 * @author Alexander Bondaletov
 */
public class SettingsModelHosts extends SettingsModel {

    private final String m_configName;

    private final List<HostsEntry> m_hosts;

    /**
     * @param configName The config name.
     *
     */
    public SettingsModelHosts(final String configName) {
        m_configName = configName;
        m_hosts = new ArrayList<>();
    }

    /**
     * @return the hosts
     */
    public List<HostsEntry> getHosts() {
        return m_hosts;
    }

    /**
     * Add new host entry.
     */
    public void addNewHost() {
        HostsEntry host = new HostsEntry("", 27017);
        add(host);
    }

    private void add(final HostsEntry host) {
        m_hosts.add(host);
        notifyChangeListeners();
    }

    /**
     * Removes the host entry.
     *
     * @param idx Index of the entry to remove.
     */
    public void remove(final int idx) {
        m_hosts.remove(idx);
        notifyChangeListeners();
    }

    /**
     * Removes all of the host entries.
     */
    public void removeAll() {
        m_hosts.clear();
        notifyChangeListeners();
    }

    /**
     * Returns the hosts string in a form of host1:port1[,host2:port2],...
     *
     * @return The hosts string.
     */
    public String getHostsString() {
        return m_hosts.stream().map(HostsEntry::toString).collect(Collectors.joining(","));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelHosts createClone() {
        SettingsModelHosts clone = new SettingsModelHosts(m_configName);
        for (HostsEntry e : m_hosts) {
            clone.m_hosts.add(e.clone());
        }
        return clone;
    }

    @Override
    protected String getModelTypeID() {
        return "SMID_HostsSettings";
    }

    @Override
    protected String getConfigName() {
        return m_configName;
    }

    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        try {
            loadSettingsForModel(settings);
        } catch (InvalidSettingsException ex) {
            throw new NotConfigurableException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings) throws InvalidSettingsException {
        saveSettingsForModel(settings);
    }

    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        SettingsModelHosts temp = new SettingsModelHosts(m_configName);
        temp.loadSettingsFrom(settings);
        temp.validate();
    }

    /**
     * Validates consistency of the settings.
     *
     * @throws InvalidSettingsException
     */
    public void validate() throws InvalidSettingsException {
        if (m_hosts.isEmpty()) {
            throw new InvalidSettingsException("At least one host have to be specified");
        }
        for (HostsEntry h : m_hosts) {
            h.validate();
        }
    }

    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings) throws InvalidSettingsException {
        m_hosts.clear();
        String[] strings = settings.getStringArray(m_configName);
        for (String str : strings) {
            m_hosts.add(new HostsEntry(str));
        }
        notifyChangeListeners();
    }

    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        String[] array =
            m_hosts.stream().map(HostsEntry::toString).collect(Collectors.toList()).toArray(new String[]{});
        settings.addStringArray(m_configName, array);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

    /**
     * @author Alexander Bondaletov
     */
    public class HostsEntry {
        private String m_host;
        private Integer m_port;

        /**
         * @param host
         * @param port
         */
        public HostsEntry(final String host, final Integer port) {
            m_host = host;
            m_port = port;
        }

        /**
         * @param hostString
         * @throws InvalidSettingsException
         */
        public HostsEntry(final String hostString) throws InvalidSettingsException {
            final String[] strings = hostString.split(":");

            if (strings.length == 1) {
                m_host = strings[0];
                m_port = null;
            } else if (strings.length == 2) {
                m_host = strings[0];
                try {
                    m_port = Integer.parseInt(strings[1]);
                } catch (NumberFormatException e) {
                    throw new InvalidSettingsException("Malformed host string: " + hostString, e);
                }
            } else {
                throw new InvalidSettingsException("Malformed host string: " + hostString);
            }
        }

        /**
         * @return the host
         */
        public String getHost() {
            return m_host;
        }

        /**
         * @param host the host to set
         */
        public void setHost(final String host) {
            m_host = host;
            notifyChangeListeners();
        }

        /**
         * @return the port could be <code>null</code>
         */
        public Integer getPort() {
            return m_port;
        }

        /**
         * @param port the port to set or <code>null</code>
         */
        public void setPort(final Integer port) {
            m_port = port;
            notifyChangeListeners();
        }

        /**
         * Validates the host field.
         *
         * @throws InvalidSettingsException
         */
        public void validateHost() throws InvalidSettingsException {
            if (m_host == null || m_host.isEmpty()) {
                throw new InvalidSettingsException("Host cannot be empty");
            }
        }

        /**
         * Validates the port field.
         *
         * @throws InvalidSettingsException
         */
        public void validatePort() throws InvalidSettingsException {
            if (m_port == null) {
                return;
            }
            if (m_port < 1) {
                throw new InvalidSettingsException("Port cannot be smaller than 1");
            }
            if (m_port > 65535) {
                throw new InvalidSettingsException("Port cannot be greater than 65535");
            }
        }

        /**
         * Validates the host and port fields.
         *
         * @throws InvalidSettingsException
         */
        public void validate() throws InvalidSettingsException {
            validateHost();
            validatePort();
        }

        @Override
        public String toString() {
            return m_host + (m_port != null ? (":" + m_port) : "");
        }

        @Override
        public HostsEntry clone() {
            return new HostsEntry(m_host, m_port);
        }
    }
}
