/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.controller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.Workspace;
import org.eclipse.emf.emfstore.internal.client.model.WorkspaceProvider;
import org.eclipse.emf.emfstore.internal.client.ui.handlers.AbstractEMFStoreUIController;
import org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin.ManageOrgUnitsDialog;
import org.eclipse.emf.emfstore.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.server.exceptions.EMFStoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * UI controller for managing org units.
 * 
 * @author emueller
 * 
 */
public class UIManageOrgUnitsController extends AbstractEMFStoreUIController<Void> {

	private final Usersession session;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the parent {@link Shell} to be used
	 * @param session
	 *            the session to be used for managing the org units
	 */
	public UIManageOrgUnitsController(Shell shell, Usersession session) {
		super(shell);
		this.session = session;
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.ui.common.MonitoredEMFStoreAction#doRun(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public Void doRun(IProgressMonitor progressMonitor) throws EMFStoreException {
		try {
			// TODO OTS
			final AdminBroker adminBroker = ((Workspace) WorkspaceProvider.getInstance().getWorkspace())
				.getAdminBroker(session);
			ManageOrgUnitsDialog dialog = new ManageOrgUnitsDialog(PlatformUI.getWorkbench().getDisplay()
				.getActiveShell(), adminBroker);
			dialog.create();
			dialog.open();
		} catch (final AccessControlException e) {
			MessageDialog.openError(getShell(), "Access denied ", e.getMessage());
		} catch (final EMFStoreException e) {
			MessageDialog.openError(getShell(), "Error while retrieving the admin broker", e.getMessage());
		}

		return null;
	}

}