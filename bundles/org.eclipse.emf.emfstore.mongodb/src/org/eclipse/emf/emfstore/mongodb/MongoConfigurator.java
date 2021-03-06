/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Johannes Faltermeier - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.mongodb;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipselabs.mongo.IMongoProvider;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * This class is registered as an OSGi component and configures the mongoDB driver.
 *
 * @author jfaltermeier
 *
 */
public class MongoConfigurator {

	private ConfigurationAdmin configurationAdmin;

	/**
	 * Binds the configuration admin.
	 *
	 * @param configurationAdmin the configuration admin
	 */
	void bindConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	/**
	 * Configures the mongoDB driver.
	 *
	 * @throws IOException if config update fails
	 */
	void activate() throws IOException {
		final Configuration config = configurationAdmin.createFactoryConfiguration(
			"org.eclipselabs.mongo.provider", null); //$NON-NLS-1$
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(IMongoProvider.PROP_URI, MongoDBConfiguration.INSTANCE.getMongoURIPrefix());
		config.update(properties);
	}

}
