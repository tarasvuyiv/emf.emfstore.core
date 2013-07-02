/*******************************************************************************
 * Copyright (c) 2013 EclipseSource Muenchen GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.test.config;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider;

public class ServerLocationProvider extends DefaultServerWorkspaceLocationProvider {

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.server.DefaultServerWorkspaceLocationProvider#getRootDirectory()
	 */
	@Override
	protected String getRootDirectory() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
	}
}
