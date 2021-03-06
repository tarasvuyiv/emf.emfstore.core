/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * ovonwesen
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIRemoveServerController;

/**
 * Handler for removing a server/repository.<br/>
 * It is assumed that the user previously has selected a {@link ServerInfo} instance.<br/>
 *
 * @author ovonwesen
 * @author emueller
 */
public class DeleteServerHandler extends AbstractEMFStoreHandler {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.handlers.AbstractEMFStoreHandler#handle()
	 */
	@Override
	public void handle() {
		new UIRemoveServerController(getShell(), requireSelection(
			ServerInfo.class).toAPI()).execute();
	}

}
