/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * emueller
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.importexport.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.util.ResourceHelper;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.util.FileUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.PrimaryVersionSpec;

/**
 * Exports a {@link ProjectSpace}.
 * 
 * @author emueller
 */
public class ExportProjectSpaceController extends ProjectSpaceBasedExportController {

	/**
	 * Constructor.
	 * 
	 * @param projectSpace the {@link ProjectSpace} that should be exported
	 */
	public ExportProjectSpaceController(ProjectSpace projectSpace) {
		super(projectSpace);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredNames()
	 */
	public String[] getFilteredNames() {
		return new String[] { "EMFStore project space (*" + ExportImportDataUnits.ProjectSpace.getExtension() + ")",
			"All Files (*.*)" };
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilteredExtensions()
	 */
	public String[] getFilteredExtensions() {
		return new String[] { "*" + ExportImportDataUnits.ProjectSpace.getExtension(), "*.*" };
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getLabel()
	 */
	public String getLabel() {
		return "project space";
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getFilename()
	 */
	public String getFilename() {
		final PrimaryVersionSpec baseVersion = getProjectSpace().getBaseVersion();
		return "ProjectSpace_" + getProjectSpace().getProjectName() + "@"
			+ (baseVersion == null ? 0 : baseVersion.getIdentifier())
			+ ExportImportDataUnits.ProjectSpace.getExtension();
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#getParentFolderPropertyKey()
	 */
	public String getParentFolderPropertyKey() {
		return "org.eclipse.emf.emfstore.client.ui.exportProjectSpacePath";
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#execute(java.io.File,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(File file, IProgressMonitor progressMonitor) throws IOException {

		if (!FileUtil.getExtension(file).equals(ExportImportDataUnits.ProjectSpace.getExtension())) {
			file = new File(file.getAbsoluteFile() + ExportImportDataUnits.ProjectSpace.getExtension());
		}

		final ProjectSpace copiedProjectSpace = ModelUtil.clone(getProjectSpace());
		copiedProjectSpace.setUsersession(null);

		final Project clonedProject = ModelUtil.clone(getProjectSpace().getProject());

		ResourceHelper.putElementIntoNewResourceWithProject(file.getAbsolutePath(), copiedProjectSpace,
			copiedProjectSpace.getProject());

		final String projectPath = FilenameUtils.removeExtension(file.getAbsolutePath())
			+ ExportImportDataUnits.Project.getExtension();
		final File projectFile = new File(projectPath);
		projectFile.createNewFile();
		ResourceHelper.putElementIntoNewResource(projectFile.getAbsolutePath(), clonedProject);
	}

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.importexport.IExportImportController#isExport()
	 */
	public boolean isExport() {
		return true;
	}
}
