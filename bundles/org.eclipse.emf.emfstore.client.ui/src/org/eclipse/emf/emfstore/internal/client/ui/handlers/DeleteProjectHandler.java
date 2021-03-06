/*******************************************************************************
 * Copyright (c) 2012-2016 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial implementation and API
 * Edgar Mueller - Bug 476839
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.handlers;

import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.ui.controller.UIDeleteProjectController;

/**
 * Handler for deleting a {@link ProjectSpace}.
 * It is assumed that the user previously has selected a {@link ProjectSpace} instance.
 *
 * @author ovonwesen
 * @author emueller
 */
public class DeleteProjectHandler extends AbstractEMFStoreHandler {

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.ui.handlers.AbstractEMFStoreHandler#handle()
	 */
	@Override
	public void handle() {
		if (!hasSelection(ProjectSpace.class)) {
			return;
		}
		new UIDeleteProjectController(getShell(), requireSelection(ProjectSpace.class).toAPI()).execute();
	}
}
