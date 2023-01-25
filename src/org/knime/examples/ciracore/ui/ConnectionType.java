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
 *   1 Jun 2021 (Tobias): created
 */
package org.knime.examples.ciracore.ui;

import org.knime.core.node.util.ButtonGroupEnumInterface;

/**
 *
 * @author Tobias Koetter, KNIME GmbH, Konstanz, Germany
 */
public enum ConnectionType implements ButtonGroupEnumInterface {

    /**
     * Standard connection type.
     */
    STANDARD("Standard", "Standard connection", "mongodb"),
    /**
     * Seed list based connection type.
     */
    SRV("SRV", "DNS seed list based connection", "mongodb+srv");

    private String m_name;
    private String m_toolTip;
    private String m_urlPrefix;

    ConnectionType(final String name, final String toolTip, final String urlPrefix) {
        m_name = name;
        m_toolTip = toolTip;
        m_urlPrefix = urlPrefix;
    }

    /**
     * @param command the action command
     * @return the {@link ConnectionType} for the command
     * @see #getActionCommand()
     */
    public static ConnectionType get(final String command) {
        return ConnectionType.valueOf(command);
    }

    @Override
    public String getText() {
        return m_name;
    }

    @Override
    public String getActionCommand() {
        return name();
    }

    @Override
    public String getToolTip() {
        return m_toolTip;
    }

    /**
     * The connection string prefix to use. For details see
     * https://docs.mongodb.com/manual/reference/connection-string.
     *
     * @return the urlPrefix to use when connecting
     */
    public String getUrlPrefix() {
        return m_urlPrefix;
    }

    @Override
    public boolean isDefault() {
        return this.equals(STANDARD);
    }

}
