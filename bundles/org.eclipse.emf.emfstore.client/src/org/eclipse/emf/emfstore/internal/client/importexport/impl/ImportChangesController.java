/*******************************************************************************
 * Copyright (c) 2008-2015 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.importexport.impl;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController;
import org.eclipse.emf.emfstore.internal.client.model.impl.ProjectSpaceBase;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * A controller for importing changes which then will be applied upon
 * a given {@link ProjectSpaceBase}.
 *
 * @author emueller
 *
 */
public class ImportChangesController implements IExportImportController {

	private final ProjectSpaceBase projectSpace;

	/**
	 * Constructor.
	 *
	 * @param projectSpace
	 *            the {@link ProjectSpaceBase} upon which to apply the changes being imported
	 */
	public ImportChangesController(ProjectSpaceBase projectSpace) {
		this.projectSpace = projectSpace;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getLabel()
	 */
	public String getLabel() {
		return Messages.ImportChangesController_Changes;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredNames()
	 */
	public String[] getFilteredNames() {
		return new String[] {
			MessageFormat.format(Messages.ImportChangesController_ChangePackageFileType_Filter,
				ExportImportDataUnits.Change.getExtension()),
			Messages.ImportChangesController_AllFilesFilter
		};
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredExtensions()
	 */
	public String[] getFilteredExtensions() {
		return new String[] { "*" + ExportImportDataUnits.Change.getExtension(), "*.*" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getParentFolderPropertyKey()
	 */
	public String getParentFolderPropertyKey() {
		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#execute(java.io.File,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(File file, IProgressMonitor progressMonitor) throws IOException {

		final ResourceSetImpl resourceSet = new ResourceSetImpl();
		final Resource resource = resourceSet.getResource(URI.createFileURI(file.getAbsolutePath()), true);
		final EList<EObject> directContents = resource.getContents();

		// sanity check
		if (directContents.size() != 1 && !(directContents.get(0) instanceof ChangePackage)) {
			throw new IOException(Messages.ImportChangesController_CorruptFile);
		}

		final AbstractChangePackage changePackage = (AbstractChangePackage) directContents.get(0);

		// / TODO
		// if (!projectSpace.isInitialized()) {
		// projectSpace.init();
		// }

		final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
		try {
			projectSpace.applyOperations(operations.iterable(), true);
		} finally {
			operations.close();
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilename()
	 */
	public String getFilename() {
		return null;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#isExport()
	 */
	public boolean isExport() {
		return false;
	}

}
