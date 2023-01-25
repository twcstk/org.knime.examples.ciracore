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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

import org.knime.core.node.InvalidSettingsException;

/**
 * Editor component for the {@link SettingsModelHosts} settings.
 *
 * @author Alexander Bondaletov
 */
public class HostsTablePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    /** Default text for the add button. */
    private static final String ADD_BUTTON_TEXT = "Add";

    /** Default text for the remove button. */
    private static final String REMOVE_BUTTON_TEXT = "Remove";

    /** Default text for the remove all button. */
    private static final String REMOVE_ALL_BUTTON_TEXT = "Remove All";

    private HostsTableModel m_hostsModel;
    private JTable m_hostsTable;

    /** the add button. */
    private final JButton m_addButton = new JButton(ADD_BUTTON_TEXT);

    /** the remove button. */
    private final JButton m_removeButton = new JButton(REMOVE_BUTTON_TEXT);

    /** the remove all button. */
    private final JButton m_removeAllButton = new JButton(REMOVE_ALL_BUTTON_TEXT);

    private SettingsModelHosts m_settings;

    /**
     * @param settings The settings model.
     *
     */
    public HostsTablePanel(final SettingsModelHosts settings) {
        m_settings = settings;

        m_hostsModel = new HostsTableModel(settings);
        m_hostsTable = new JTable(m_hostsModel);
        m_hostsTable.setFillsViewportHeight(false);
        m_hostsTable.getTableHeader().setReorderingAllowed(false);
        m_hostsTable.setPreferredScrollableViewportSize(new Dimension(200, 50));
        m_hostsTable.getSelectionModel().addListSelectionListener(e -> toggleButtons());

        for (int i = 0; i < m_hostsTable.getColumnCount(); i++) {
            m_hostsTable.getColumnModel().getColumn(i).setCellRenderer(new ValidationCellRenderer());
        }

        addButtonListeners();
        addComponents();
        setBorder(BorderFactory.createTitledBorder("Instances"));
    }

    private void addButtonListeners() {
        m_addButton.addActionListener(e -> onAdd());
        m_removeButton.addActionListener(e -> onRemove());
        m_removeAllButton.addActionListener(e -> onRemoveAll());
    }

    private void addComponents() {
        setLayout(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1f;
        gbc.weighty = 1f;
        gbc.gridwidth = 4;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 2, 5, 3);
        gbc.fill = GridBagConstraints.BOTH;

        final JScrollPane scrollPane = new JScrollPane(m_hostsTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(new JPanel().add(scrollPane), gbc);

        ++gbc.gridy;
        gbc.gridwidth = 1;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(m_addButton, gbc);
        ++gbc.gridx;
        add(m_removeButton, gbc);
        ++gbc.gridx;
        add(m_removeAllButton, gbc);
    }

    private void toggleButtons() {
        m_removeButton.setEnabled(m_hostsTable.getSelectedRowCount() > 0);
        boolean removeAllPossible = m_hostsTable.getRowCount() > 0;
        m_removeAllButton.setEnabled(removeAllPossible);
    }

    private void onAdd() {
        m_settings.addNewHost();
        m_hostsTable.editCellAt(m_settings.getHosts().size() - 1, 0);
        m_hostsTable.requestFocus();
    }

    private void onRemove() {
        int row = m_hostsTable.getSelectedRow();
        if (row > -1) {
            m_settings.remove(row);
        }
    }

    private void onRemoveAll() {
        m_settings.removeAll();
    }

    /**
     * Forces the cell editor to stop the editing mode.
     *
     * @throws InvalidSettingsException
     */
    public void stopCellEditing() throws InvalidSettingsException {
        TableCellEditor editor = m_hostsTable.getCellEditor();
        if (editor != null) {
            final boolean success = editor.stopCellEditing();
            if (!success) {
                throw new InvalidSettingsException("Some settings are invalid. Please check it again.");
            }
        }
    }

    /**
     * Force the cell editor to cancel editing.
     */
    public void cancelCellEditing() {
        TableCellEditor editor = m_hostsTable.getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }
    }

    private static class ValidationCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        private static final Color PROPER_INPUT_COLOR = new Color(255, 255, 255);
        private static final Color WRONG_INPUT_COLOR = new Color(255, 120, 120);

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
            final boolean hasFocus, final int row, final int column) {
            JLabel comp = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            try {
                HostsTableModel model = (HostsTableModel)table.getModel();
                model.validate(row, column);
                comp.setToolTipText("");
                if (!isSelected) {
                    comp.setBackground(PROPER_INPUT_COLOR);
                }
            } catch (InvalidSettingsException e) {
                comp.setToolTipText(e.getMessage());
                comp.setBackground(WRONG_INPUT_COLOR);
            }

            return comp;
        }
    }
}
