/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * mkoegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Tests if the currently selected project space has unsaved changes.
 *
 * @author mkoegel
 *
 */
public class CurrentProjectSpaceHasUnsavedChangesTester extends PropertyTester {

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 *      java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null) {
			return false;
		}
		final IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return false;
		}
		final ISelection selection = activeWorkbenchWindow.getSelectionService().getSelection();
		if (selection instanceof StructuredSelection) {
			final StructuredSelection structuredSelection = (StructuredSelection) selection;
			final Object firstElement = structuredSelection.getFirstElement();
			if (firstElement instanceof EObject) {
				final ProjectSpace projectSpace = ModelUtil.getParent(ProjectSpace.class, (EObject) firstElement);
				if (projectSpace != null) {
					return expectedValue.equals(projectSpace.hasUnsavedChanges());
				}
			}
		}
		return false;
	}

}
