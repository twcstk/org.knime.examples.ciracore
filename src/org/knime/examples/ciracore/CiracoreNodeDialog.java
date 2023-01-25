package org.knime.examples.ciracore;

import static org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType.CREDENTIALS;
import static org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType.NONE;
import static org.knime.core.node.defaultnodesettings.SettingsModelAuthentication.AuthenticationType.USER_PWD;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentAuthentication;
import org.knime.core.node.defaultnodesettings.DialogComponentButtonGroup;
import org.knime.core.node.defaultnodesettings.DialogComponentString;
import org.knime.core.node.defaultnodesettings.SettingsModelAuthentication;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.ConnectionContainer.ConnectionType;

/**
 * This is an example implementation of the node dialog of the
 * "Ciracore" node.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}. In general, one can create an
 * arbitrary complex dialog using Java Swing.
 * 
 * @author dv
 */
public class CiracoreNodeDialog extends NodeDialogPane {

	private final CiracoreConnectionSettings m_settings = new CiracoreConnectionSettings();

    private final SettingsModelAuthentication m_authModel = m_settings.getAuthModel();

    private final SettingsModelString m_authDbModel = m_settings.getAuthdbModel();

    private final SettingsModelString m_hosts = m_settings.getHostsModel();

    private DialogComponentAuthentication m_auth =
            new DialogComponentAuthentication(m_authModel, null, NONE, USER_PWD, CREDENTIALS);

//    private final DialogComponentKeyValueEdit m_options = new DialogComponentKeyValueEdit(m_settings.getOptionsModel());

    /**
     * Creates new instance
     */
    public CiracoreNodeDialog() {
        addTab("Settings", createSettingsPanel());
        // addTab("Advanced", createAdvancedPanel());
    }

    private JComponent createSettingsPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.add(createHostPanel(), gbc);
        gbc.gridy++;
        panel.add(createAuthPanel(), gbc);

        //add resizeable box to move components to top
        gbc.gridy++;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(Box.createVerticalBox(), gbc);

        m_authModel.addChangeListener(e -> {
            m_authDbModel.setEnabled(NONE != m_authModel.getAuthenticationType());
        });

        return panel;
    }

    private JPanel createHostPanel() {
//        final DialogComponentButtonGroup connectionType = new DialogComponentButtonGroup(
//            m_settings.getConnectionTypeModel(), null, false, ConnectionType.values());

        final JPanel hostPanel = new JPanel(new GridBagLayout());
        hostPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Connection "));

        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 5, 0, 0);
        hostPanel.add(new JLabel("Type: "), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 0, 0, 0);
        // hostPanel.add(connectionType.getComponentPanel(), gbc);

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        // hostPanel.add(m_hosts.getStringValue(), gbc);
        return hostPanel;
    }

    private JPanel createAuthPanel() {
        final DialogComponentString db =
                new DialogComponentString(m_authDbModel, "Authentication database:", false, 40);

        final JPanel authPanel = new JPanel(new GridBagLayout());
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        authPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
            " Authentication "));
        gbc.gridwidth = 2;
        authPanel.add(m_auth.getComponentPanel(), gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 20, 0, 0);
        authPanel.add(db.getComponentPanel(), gbc);
        gbc.gridx++;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        authPanel.add(new JLabel(), gbc);
        return authPanel;
    }

//    private JComponent createAdvancedPanel() {
//        return m_options.getComponentPanel();
//    }

    @Override
    public void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs)
        throws NotConfigurableException {
        try {
            m_settings.loadSettingsFrom(settings);
        } catch (InvalidSettingsException ex) {
            // ignore
        }
        m_auth.loadSettingsFrom(settings, specs, getCredentialsProvider());
        // m_options.loadSettingsFrom(settings, specs);
    }

    @Override
    public void saveSettingsTo(final NodeSettingsWO settings) throws InvalidSettingsException {
        // m_hosts.stopCellEditing();
        m_auth.saveSettingsTo(settings);
//        m_options.saveSettingsTo(settings);

        m_settings.saveSettingsTo(settings);
    }

    @Override
    public void onCancel() {
//        m_hosts.cancelCellEditing();
//        m_options.cancelCellEditing();
    }
    
    
}

