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
package org.eclipse.emf.emfstore.internal.client.model;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.emfstore.client.ILocalProject;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileDownloadStatus;
import org.eclipse.emf.emfstore.internal.client.model.filetransfer.FileInformation;
import org.eclipse.emf.emfstore.internal.client.model.impl.OperationManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.RemoteProject;
import org.eclipse.emf.emfstore.internal.client.properties.PropertyManager;
import org.eclipse.emf.emfstore.internal.common.model.EMFStoreProperty;
import org.eclipse.emf.emfstore.internal.common.model.IdentifiableElement;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.server.exceptions.EMFStoreException;
import org.eclipse.emf.emfstore.server.exceptions.FileTransferException;
import org.eclipse.emf.emfstore.server.model.FileIdentifier;
import org.eclipse.emf.emfstore.server.model.ProjectId;
import org.eclipse.emf.emfstore.server.model.accesscontrol.OrgUnitProperty;
import org.eclipse.emf.emfstore.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.operations.AbstractOperation;

/**
 * <!-- begin-user-doc --> A representation of the model object ' <em><b>Project Container</b></em>'.
 * 
 * @extends IProject
 *          <!-- end-user-doc
 *          -->
 * 
 *          <p>
 *          The following features are supported:
 *          <ul>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProject <em> Project</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectId
 *          <em>Project Id</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectName
 *          <em>Project Name</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectDescription
 *          <em>Project Description</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getEvents <em> Events</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getUsersession
 *          <em>Usersession</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getLastUpdated
 *          <em>Last Updated</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getBaseVersion
 *          <em>Base Version</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getResourceCount
 *          <em>Resource Count</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#isDirty <em> Dirty</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getOldLogMessages
 *          <em>Old Log Messages</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getLocalOperations
 *          <em>Local Operations</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getNotifications
 *          <em>Notifications</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getEventComposite
 *          <em>Event Composite</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getNotificationComposite
 *          <em>Notification Composite </em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getWaitingUploads
 *          <em>Waiting Uploads</em>}</li>
 *          <li>{@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProperties
 *          <em>Properties</em>}</li>
 *          <li>
 *          {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getChangedSharedProperties
 *          <em>Changed Shared Properties</em>}</li>
 *          </ul>
 *          </p>
 * 
 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace()
 * @model
 * @generated
 */
public interface ProjectSpace extends IdentifiableElement, ILocalProject {

	/**
	 * Adds a file to this project space. The file will be uploaded to the
	 * EMFStore upon a commit. As long as the file is not yet committed, it can
	 * be removed by first retrieving the {@link FileInformation} via {@link #getFileInfo(FileIdentifier)} and then
	 * remove it via {@link FileInformation#cancelPendingUpload()}.
	 * 
	 * @param file
	 *            to be added to the project space
	 * @return The file identifier the file was assigned to. This identifier can
	 *         be used to retrieve the file later on
	 * @throws FileTransferException
	 *             if any error occurs
	 * 
	 * @generated NOT
	 */
	FileIdentifier addFile(File file) throws FileTransferException;

	/**
	 * Adds a list of operations to this project space.
	 * 
	 * @param operations
	 *            the list of operations to be added
	 * 
	 * @generated NOT
	 */
	void addOperations(List<? extends AbstractOperation> operations);

	/**
	 * Begin a composite operation on the projectSpace.
	 * 
	 * @return a handle to abort or complete the operation
	 * 
	 * @generated NOT
	 */
	CompositeOperationHandle beginCompositeOperation();

	/**
	 * Export all local changes to a file.
	 * 
	 * @param file
	 *            the file being exported to
	 * @throws IOException
	 *             if writing to the given file fails
	 * 
	 * @generated NOT
	 */
	void exportLocalChanges(File file) throws IOException;

	/**
	 * Export all local changes to a file.
	 * 
	 * @param file
	 *            the file being exported to
	 * @param progressMonitor
	 *            the progress monitor that should be used while exporting
	 * @throws IOException
	 *             if writing to the given file fails
	 * 
	 * @generated NOT
	 */
	void exportLocalChanges(File file, IProgressMonitor progressMonitor) throws IOException;

	/**
	 * Export a project to the given file.
	 * 
	 * @param file
	 *            the file being exported to
	 * @throws IOException
	 *             if writing to the given file fails
	 * 
	 * @generated NOT
	 */
	void exportProject(File file) throws IOException;

	/**
	 * Export a project to the given file.
	 * 
	 * @param file
	 *            the file being exported to
	 * @param progressMonitor
	 *            the progress monitor that should be used during the export
	 * @throws IOException
	 *             if writing to the given file fails
	 * 
	 * @generated NOT
	 */
	void exportProject(File file, IProgressMonitor progressMonitor) throws IOException;

	/**
	 * Returns the value of the '<em><b>Base Version</b></em>' containment
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Base Version</em>' containment reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Base Version</em>' containment reference.
	 * @see #setBaseVersion(PrimaryVersionSpec)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_BaseVersion()
	 * @model containment="true" resolveProxies="true" required="true"
	 * @generated
	 */
	PrimaryVersionSpec getBaseVersion();

	/**
	 * Returns the value of the '<em><b>Changed Shared Properties</b></em>'
	 * reference list. The list contents are of type {@link org.eclipse.emf.emfstore.internal.common.model.internal.common.model.EMFStoreProperty}.
	 * <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Changed Shared Properties</em>' map isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Changed Shared Properties</em>' reference
	 *         list.
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_ChangedSharedProperties()
	 * @model
	 * @generated
	 */
	EList<EMFStoreProperty> getChangedSharedProperties();

	/**
	 * Returns the value of the '<em><b>Workspace</b></em>' container reference.
	 * It is bidirectional and its opposite is '
	 * {@link org.eclipse.emf.emfstore.internal.internal.client.model.Workspace#getProjectSpaces
	 * <em>Project Spaces</em>}'. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Workspace</em>' container reference isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Workspace</em>' container reference.
	 * @see #setWorkspace(Workspace)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_Workspace()
	 * @see org.eclipse.emf.emfstore.internal.internal.client.model.Workspace#getProjectSpaces
	 * @model opposite="projectSpaces" transient="false"
	 * @generated
	 */
	Workspace getWorkspace();

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getWorkspace
	 * <em>Workspace</em>} ' container reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Workspace</em>' container reference.
	 * @see #getWorkspace()
	 * @generated
	 */
	void setWorkspace(Workspace value);

	/**
	 * Returns the value of the '<em><b>Local Change Package</b></em>'
	 * containment reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Local Change Package</em>' containment reference isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Local Change Package</em>' containment
	 *         reference.
	 * @see #setLocalChangePackage(ChangePackage)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_LocalChangePackage()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	ChangePackage getLocalChangePackage();

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getLocalChangePackage
	 * <em>Local Change Package</em>}' containment reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Local Change Package</em>'
	 *            containment reference.
	 * @see #getLocalChangePackage()
	 * @generated
	 */
	void setLocalChangePackage(ChangePackage value);

	/**
	 * Returns the value of the '<em><b>Merged Version</b></em>' containment
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Merged Version</em>' containment reference isn't clear, there really should be more of
	 * a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Merged Version</em>' containment reference.
	 * @see #setMergedVersion(PrimaryVersionSpec)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_MergedVersion()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	PrimaryVersionSpec getMergedVersion();

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getMergedVersion
	 * <em>Merged Version</em>}' containment reference. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Merged Version</em>' containment
	 *            reference.
	 * @see #getMergedVersion()
	 * @generated
	 */
	void setMergedVersion(PrimaryVersionSpec value);

	/**
	 * @return a list of the change packages between two PrimarySpecVersions.
	 * @param sourceVersion
	 *            the source version spec
	 * @param targetVersion
	 *            the target version spec
	 * @throws EMFStoreException
	 *             if any error in the EmfStore occurs
	 * @generated NOT
	 */
	List<ChangePackage> getChanges(VersionSpec sourceVersion, VersionSpec targetVersion) throws EMFStoreException;

	/**
	 * Gets a file with a specific identifier. If the file is not cached
	 * locally, it is tried to download the file if a connection to the sever
	 * exists. If the file cannot be found locally and not on the server (or the
	 * server isn't reachable), a FileTransferException is thrown. Such an
	 * exception is also thrown if other errors occur while trying to download
	 * the file. The method returns not the file itself, because it does not
	 * block in case of downloading the file. Instead, it returns a status
	 * object which can be queried for the status of the download. Once the
	 * download is finished ( status.isFinished() ), the file can be retrieved
	 * from this status object by calling status.getTransferredFile().
	 * 
	 * @param fileIdentifier
	 *            file identifier string.
	 * @return a status object that can be used to retrieve various information
	 *         about the file.
	 * @throws FileTransferException
	 *             if any error occurs retrieving the files
	 * 
	 * @generated NOT
	 */
	FileDownloadStatus getFile(FileIdentifier fileIdentifier) throws FileTransferException;

	/**
	 * Gets the file information for a specific file identifier. This file
	 * information can be used to access further details of a file (if it
	 * exists, is cached, is a pending upload). It can also be used to alter the
	 * file in limited ways (like removing a pending upload). The
	 * FileInformation class is basically a facade to keep the interface in the
	 * project space small (only getFileInfo) while still providing a rich
	 * interface for files.
	 * 
	 * @param fileIdentifier
	 *            the file identifier for which to get the information
	 * @return the information for that identifier.
	 * 
	 * @generated NOT
	 */
	FileInformation getFileInfo(FileIdentifier fileIdentifier);

	/**
	 * Returns the value of the '<em><b>Last Updated</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Updated</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Last Updated</em>' attribute.
	 * @see #setLastUpdated(Date)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_LastUpdated()
	 * @model
	 * @generated
	 */
	Date getLastUpdated();

	/**
	 * Gathers all local operations and canonizes them.
	 * 
	 * @param canonized
	 *            true if the operations should be canonized
	 * @return the list of operations
	 * 
	 * @generated NOT
	 */
	ChangePackage getLocalChangePackage(boolean canonized);

	/**
	 * Returns the value of the '<em><b>Local Operations</b></em>' containment
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Local Operations</em>' containment reference isn't clear, there really should be more
	 * of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Local Operations</em>' containment
	 *         reference.
	 * @see #setLocalOperations(OperationComposite)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_LocalOperations()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	OperationComposite getLocalOperations();

	/**
	 * Returns the value of the '<em><b>Old Log Messages</b></em>' attribute
	 * list. The list contents are of type {@link java.lang.String}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Old Log Messages</em>' attribute list isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Old Log Messages</em>' attribute list.
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_OldLogMessages()
	 * @model
	 * @generated
	 */
	EList<String> getOldLogMessages();

	/**
	 * Get the {@link OperationManager} for this {@link ProjectSpace}.
	 * 
	 * @return the operation manager
	 * @generated NOT
	 */
	OperationManager getOperationManager();

	/**
	 * Returns the value of the '<em><b>Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Project</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Project</em>' containment reference.
	 * @see #setProject(Project)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_Project()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	Project getProject();

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' containment
	 * reference list. The list contents are of type {@link org.eclipse.emf.emfstore.internal.common.model.internal.common.model.EMFStoreProperty}.
	 * <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' map isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Properties</em>' containment reference
	 *         list.
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_Properties()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<EMFStoreProperty> getProperties();

	/**
	 * Get the {@link PropertyManager} for this {@link ProjectSpace}.
	 * 
	 * @return the property manager
	 * @generated NOT
	 */
	PropertyManager getPropertyManager();

	/**
	 * Returns the value of the '<em><b>Usersession</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Usersession</em>' reference isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Usersession</em>' reference.
	 * @see #setUsersession(Usersession)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_Usersession()
	 * @model
	 * @generated
	 */
	Usersession getUsersession();

	/**
	 * Returns the value of the '<em><b>Waiting Uploads</b></em>' containment
	 * reference list. The list contents are of type {@link org.eclipse.emf.emfstore.internal.internal.server.model.FileIdentifier}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waiting Uploads</em>' containment reference list isn't clear, there really should be
	 * more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Waiting Uploads</em>' containment reference
	 *         list.
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_WaitingUploads()
	 * @model containment="true" resolveProxies="true"
	 * @generated
	 */
	EList<FileIdentifier> getWaitingUploads();

	/**
	 * Import changes from a file.
	 * 
	 * @param fileName
	 *            the file name to import from
	 * @throws IOException
	 *             if file access fails
	 * @generated NOT
	 */
	void importLocalChanges(String fileName) throws IOException;

	/**
	 * Initialize the project space and its resources.
	 * 
	 * @generated NOT
	 */
	void init();

	/**
	 * Initialize the resources of the project space.
	 * 
	 * @param resourceSet
	 *            the resource set the project space should use
	 * @generated NOT
	 */
	void initResources(ResourceSet resourceSet);

	/**
	 * Returns the resource set of the ProjectSpace.
	 * 
	 * @return resource set of the ProjectSpace
	 */
	ResourceSet getResourceSet();

	/**
	 * Sets the resource set of the project space.
	 * 
	 * @param resourceSet
	 *            the resource set to be used by this project space
	 */
	void setResourceSet(ResourceSet resourceSet);

	/**
	 * Returns the value of the '<em><b>Dirty</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dirty</em>' attribute isn't clear, there really should be more of a description
	 * here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dirty</em>' attribute.
	 * @see #setDirty(boolean)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_Dirty()
	 * @model
	 * @generated
	 */
	boolean hasUncommitedChanges();

	/**
	 * Shows whether projectSpace is transient.
	 * 
	 * @return true, if transient.
	 * 
	 * @generated NOT
	 */
	boolean isTransient();

	/**
	 * Determines whether the project is up to date, that is, whether the base
	 * revision and the head revision are equal.
	 * 
	 * @return true, if the project is up to date, false otherwise
	 * @throws EMFStoreException
	 *             if the head revision can not be resolved
	 * 
	 * @generated NOT
	 */
	boolean isUpdated() throws EMFStoreException;

	/**
	 * Will make the projectSpace transient, it will not make its content or
	 * changes persistent. Can only be called before the resources or the
	 * project space have been initialized.
	 * 
	 * @generated NOT
	 */
	void makeTransient();

	/**
	 * Revert all local changes in the project space. Returns the state of the
	 * project to that of the project space base version.
	 * 
	 * @generated NOT
	 */
	void revert();

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getBaseVersion
	 * <em>Base Version</em>}' containment reference. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Base Version</em>' containment
	 *            reference.
	 * @see #getBaseVersion()
	 * @generated
	 */
	void setBaseVersion(PrimaryVersionSpec value);

	/**
	 * Returns the value of the '<em><b>Resource Count</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Resource Count</em>' attribute isn't clear, there really should be more of a
	 * description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Resource Count</em>' attribute.
	 * @see #setResourceCount(int)
	 * @see org.eclipse.emf.emfstore.internal.common.model.internal.client.model.ModelPackage#getProjectSpace_ResourceCount()
	 * @model
	 * @generated
	 */
	int getResourceCount();

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getResourceCount
	 * <em>Resource Count</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Resource Count</em>' attribute.
	 * @see #getResourceCount()
	 * @generated
	 */
	void setResourceCount(int value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#isDirty
	 * <em>Dirty</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Dirty</em>' attribute.
	 * @see #hasUncommitedChanges()
	 * @generated
	 */
	void setDirty(boolean value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getLastUpdated
	 * <em>Last Updated</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Last Updated</em>' attribute.
	 * @see #getLastUpdated()
	 * @generated
	 */
	void setLastUpdated(Date value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getLocalOperations
	 * <em>Local Operations</em>}' containment reference. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Local Operations</em>' containment
	 *            reference.
	 * @see #getLocalOperations()
	 * @generated
	 */
	void setLocalOperations(OperationComposite value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProject
	 * <em>Project</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Project</em>' containment reference.
	 * @see #getProject()
	 * @generated
	 */
	void setProject(Project value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectDescription
	 * <em>Project Description</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Project Description</em>' attribute.
	 * @see #getProjectDescription()
	 * @generated
	 */
	void setProjectDescription(String value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectId
	 * <em>Project Id</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Project Id</em>' containment
	 *            reference.
	 * @see #getProjectId()
	 * @generated
	 */
	void setProjectId(ProjectId value);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getProjectName
	 * <em>Project Name</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Project Name</em>' attribute.
	 * @see #getProjectName()
	 * @generated
	 */
	void setProjectName(String value);

	/**
	 * Sets a new OrgUnitProperty for the current user.
	 * 
	 * @param property
	 *            the new property
	 * @generated NOT
	 */
	void setProperty(OrgUnitProperty property);

	/**
	 * Sets the value of the ' {@link org.eclipse.emf.emfstore.internal.internal.client.model.ProjectSpace#getUsersession
	 * <em>Usersession</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Usersession</em>' reference.
	 * @see #getUsersession()
	 * @generated
	 */
	void setUsersession(Usersession value);

	/**
	 * Transmit the OrgUnitproperties to the server.
	 * 
	 * @generated NOT
	 */
	void transmitProperties();

	/**
	 * 
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.emfstore.internal.client.api.IProject#getHistoryInfos(org.eclipse.emf.emfstore.internal.internal.server.model.api.query.IHistoryQuery)
	 * 
	 * @generated NOT
	 */

	ProjectId getProjectId();

	RemoteProject getRemoteProject() throws EMFStoreException;

	List<AbstractOperation> getOperations();

} // ProjectContainer