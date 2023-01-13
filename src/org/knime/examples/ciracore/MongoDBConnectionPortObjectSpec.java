package org.knime.examples.ciracore;

import org.knime.core.node.port.*;
import java.io.IOException;

import javax.swing.JComponent;

import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;

public class MongoDBConnectionPortObjectSpec implements PortObjectSpec {

    @Override
    public JComponent[] getViews() {
        return new JComponent[0];
    }
}