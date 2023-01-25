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
 *   2021-05-17 (Alexander Bondaletov): created
 */
package org.knime.examples.ciracore.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link KeyDescriptor} implementation representing available MongoDB connection options.
 *
 * @author Alexander Bondaletov
 */
public class MongoDBOption extends DefaultKeyDescriptor {

    /**
     * All available options.
     */
    public static final List<KeyDescriptor> ALL_OPTIONS = new ArrayList<>();

    static {
        ALL_OPTIONS.add(new IntOption("serverSelectionTimeoutMS",
            "How long the driver will wait for server selection to succeed before throwing an exception"));
        ALL_OPTIONS.add(new IntOption("localThresholdMS",
            "When choosing among multiple MongoDB servers to send a request, the driver will only send that request to a server whose ping time is less than or equal to the server with the fastest ping time plus the local threshold."));
        ALL_OPTIONS.add(new IntOption("heartbeatFrequencyMS",
            "The frequency that the driver will attempt to determine the current state of each server in the cluster."));
        ALL_OPTIONS.add(new MongoDBOption("replicaSet", "",
            "Implies that the hosts given are a seed list, and the driver will attempt to find all members of the set."));
        ALL_OPTIONS.add(new BooleanOption("serverSelectionTryOnce",
            "Single-threaded drivers only. When true, instructs the driver to scan the MongoDB deployment exactly once after server selection fails and then either select a server or raise an error."));

        ALL_OPTIONS.add(new BooleanOption("ssl", "Whether to connect using SSL."));
        ALL_OPTIONS.add(new BooleanOption("tls", "Whether to connect using TLS. Supersedes the ssl option"));
        ALL_OPTIONS.add(
            new BooleanOption("tlsInsecure", "If connecting with TLS, this option enables insecure TLS connections."));
        ALL_OPTIONS.add(
            new BooleanOption("sslInvalidHostNameAllowed", "Whether to allow invalid host names for SSL connections."));
        ALL_OPTIONS.add(
            new BooleanOption("tlsAllowInvalidHostnames", "Whether to allow invalid host names for TLS connections."));
        ALL_OPTIONS.add(new MongoDBOption("tlsCertificateKeyFile", "",
            "Specifies the location of a local .pem file that contains either the client's TLS/SSL X.509 certificate or the client's TLS/SSL certificate and key."));
        ALL_OPTIONS.add(new MongoDBOption("tlsCertificateKeyFilePassword", "",
            "Specifies the password to de-crypt the tlsCertificateKeyFile."));
        ALL_OPTIONS.add(new MongoDBOption("tlsCAFile", "",
            "Specifies the location of a local .pem file that contains the root certificate chain from the Certificate Authority. This file is used to validate the certificate presented by the mongod/mongos instance."));
        ALL_OPTIONS.add(new BooleanOption("tlsAllowInvalidCertificates",
            "Bypasses validation of the certificates presented by the mongod/mongos instance"));

        ALL_OPTIONS
            .add(new IntOption("connectTimeoutMS", "How long a connection can take to be opened before timing out."));
        ALL_OPTIONS.add(
            new IntOption("socketTimeoutMS", "How long a send or receive on a socket can take before timing out."));
        ALL_OPTIONS.add(new IntOption("maxIdleTimeMS",
            "Maximum idle time of a pooled connection. A connection that exceeds this limit will be closed"));
        ALL_OPTIONS.add(new IntOption("maxLifeTimeMS",
            "Maximum life time of a pooled connection. A connection that exceeds this limit will be closed"));

        ALL_OPTIONS.add(new IntOption("maxPoolSize", "The maximum number of connections in the connection pool."));
        ALL_OPTIONS.add(new IntOption("minPoolSize", "The minimum number of connections in the connection pool."));
        ALL_OPTIONS.add(new IntOption("waitQueueMultiple",
            "A number that the driver multiplies the maxPoolSize value to, to provide the maximum number of threads allowed to wait for a connection to become available from the pool. "));
        ALL_OPTIONS.add(new IntOption("waitQueueTimeoutMS",
            "The maximum wait time in milliseconds that a thread may wait for a connection to become available."));

        ALL_OPTIONS.add(new BooleanOption("safe",
            "true: the driver ensures that all writes are acknowledged by the MongoDB server, or else throws an exception. (see also w and wtimeoutMS)."));
        ALL_OPTIONS.add(new BooleanOption("journal",
            "true: the driver waits for the server to group commit to the journal file on disk."));
        ALL_OPTIONS.add(
            new MongoDBOption("w", "", "The driver adds { w : wValue } to all write commands. Implies safe=true."));
        ALL_OPTIONS.add(
            new IntOption("wtimeoutMS", "The driver adds { wtimeout : ms } to all write commands. Implies safe=true."));

        ALL_OPTIONS.add(new EnumOption("readPreference", "primary", "The read preference for this connection. ",
            "primary", "primaryPreferred", "secondary", "secondaryPreferred", "nearest"));
        ALL_OPTIONS.add(new MongoDBOption("readPreferenceTags", "",
            "A representation of a tag set as a comma-separated list of colon-separated key-value pairs"));
        ALL_OPTIONS.add(new IntOption("maxStalenessSeconds", "The maximum staleness in seconds."));

        ALL_OPTIONS.add(new MongoDBOption("appName", "", "Sets the logical name of the application."));

        ALL_OPTIONS.add(new MongoDBOption("compressors", "",
            "A comma-separated list of compressors to request from the server. The supported compressors currently are 'zlib', 'snappy' and 'zstd'."));
        ALL_OPTIONS.add(new IntOption("zlibCompressionLevel",
            " Integer value from -1 to 9 representing the zlib compression level."));

        ALL_OPTIONS.add(new BooleanOption("retryWrites",
            "If true the driver will retry supported write operations if they fail due to a network error. Defaults to true."));
        ALL_OPTIONS.add(new BooleanOption("retryReads",
            "If true the driver will retry supported read operations if they fail due to a network error. Defaults to true."));
        ALL_OPTIONS.add(new EnumOption("uuidRepresentation", "unspecified",
            "UUID representation to use when encoding instances of UUID", "unspecified", "standard", "javaLegacy",
            "csharpLegacy", "pythonLegacy"));
        ALL_OPTIONS.add(new BooleanOption("directConnection",
            "If true the driver will set the connection to be a direct connection to the host."));

        ALL_OPTIONS.add(new EnumOption("readConcernLevel", "local", "The level of isolation. ", "local", "majority",
            "linearizable", "available"));
    }

    /**
     * @param key The key to describe.
     * @param defaultValue The default value for key-value pairs with the given key.
     * @param description A description of what the key means.
     */
    protected MongoDBOption(final String key, final String defaultValue, final String description) {
        super(key, defaultValue, description);
    }

    static class IntOption extends MongoDBOption {
        protected IntOption(final String key, final String description) {
            this(key, 0, description);
        }

        protected IntOption(final String key, final int defaultValue, final String description) {
            super(key, String.valueOf(defaultValue), description);
        }

        @Override
        public void validateValue(final String value) throws IllegalArgumentException {
            super.validateValue(value);
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Number is expected", e);
            }
        }
    }

    static class EnumOption extends MongoDBOption {
        private final Set<String> m_options;

        protected EnumOption(final String key, final String defaultValue, final String description,
            final String... options) {
            super(key, defaultValue, description + " Accepted values: "
                + Arrays.asList(options).stream().collect(Collectors.joining(" | ")));
            m_options = new HashSet<>(Arrays.asList(options));
        }

        @Override
        public void validateValue(final String value) throws IllegalArgumentException {
            super.validateValue(value);
            if (!m_options.contains(value)) {
                throw new IllegalArgumentException("Unknown value: " + value);
            }
        }
    }

    static class BooleanOption extends EnumOption {
        protected BooleanOption(final String key, final String description) {
            this(key, false, description);
        }

        protected BooleanOption(final String key, final boolean defaultValue, final String description) {
            super(key, String.valueOf(defaultValue), description, Boolean.FALSE.toString(), Boolean.TRUE.toString());
        }

    }
}
