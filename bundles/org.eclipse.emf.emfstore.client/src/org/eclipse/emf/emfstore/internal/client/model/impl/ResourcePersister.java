/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfstore.client.ESLocalProject;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver;
import org.eclipse.emf.emfstore.client.handler.ESNotificationFilter;
import org.eclipse.emf.emfstore.client.observer.ESCommitObserver;
import org.eclipse.emf.emfstore.client.observer.ESUpdateObserver;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter.EmptyRemovalsFilter;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter.FilterStack;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter.TouchFilter;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.filter.TransientFilter;
import org.eclipse.emf.emfstore.internal.client.model.impl.api.ESLocalProjectImpl;
import org.eclipse.emf.emfstore.internal.client.model.util.WorkspaceUtil;
import org.eclipse.emf.emfstore.internal.common.EMFStoreResource;
import org.eclipse.emf.emfstore.internal.common.ESDisposable;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.NotificationInfo;
import org.eclipse.emf.emfstore.server.model.ESChangePackage;
import org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec;

/**
 * Saves any registered resources upon certain types of triggers like, for instance, update and commit.
 *
 * @author koegel
 * @author emueller
 */
public class ResourcePersister implements ESCommandObserver, IdEObjectCollectionChangeObserver, ESCommitObserver,
	ESUpdateObserver, ESDisposable {

	private final FilterStack filterStack;

	private boolean isDirty;

	/**
	 * Indicates whether a command is running.
	 */
	private boolean commandIsRunning;

	private Set<Resource> resources;

	private List<IDEObjectCollectionDirtyStateListener> listeners;

	private ESLocalProject localProject;

	/**
	 * Constructor.
	 *
	 * @param localProject
	 *            the {@link ESLocalProject} that will be associated with this persister
	 */
	public ResourcePersister(ESLocalProject localProject) {
		this.localProject = localProject;
		resources = new LinkedHashSet<Resource>();
		listeners = new ArrayList<IDEObjectCollectionDirtyStateListener>();
		filterStack = new FilterStack(new ESNotificationFilter[] {
			new TouchFilter(),
			new TransientFilter(),
			new EmptyRemovalsFilter()
		});
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandStarted(org.eclipse.emf.common.command.Command)
	 */
	public void commandStarted(Command command) {
		commandIsRunning = true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandCompleted(org.eclipse.emf.common.command.Command)
	 */
	public void commandCompleted(Command command) {
		commandIsRunning = false;
		saveDirtyResources(false);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandFailed(org.eclipse.emf.common.command.Command,
	 *      java.lang.Exception)
	 */
	public void commandFailed(Command command, Exception exception) {
		commandIsRunning = false;
	}

	/**
	 * Adds the given resource to the set of resources which should be saved by the persister.
	 *
	 * @param resource
	 *            the resource to be saved
	 */
	protected void addResource(Resource resource) {
		if (resource != null) {
			resources.add(resource);
		}
	}

	/**
	 * Save all dirty resources to disk now if auto-save is active.
	 * If auto-save is disabled, clients have to programmatically
	 * save the dirty resource set by setting the <code>force</code> parameter
	 * to true.
	 *
	 * @param force
	 *            whether to force the saving of resources
	 */
	public void saveDirtyResources(boolean force) {
		if (!force && !Configuration.getClientBehavior().isAutoSaveEnabled()) {
			return;
		}

		if (!force && !isDirty) {
			return;
		}

		if (resources == null) {
			// dispose may have been called
			return;
		}

		for (final Resource resource : resources) {

			if (resource.getURI() == null || resource.getURI().toString().equals(StringUtils.EMPTY)) {
				continue;
			}

			final ProjectSpace projectSpace = ESLocalProjectImpl.class.cast(localProject).toInternalAPI();
			if (EMFStoreResource.class.isInstance(resource) && resource == projectSpace.getProject().eResource()) {
				final Map<EObject, String> eObjectToIdMapping = new LinkedHashMap<EObject, String>(
					projectSpace.getProject().getEObjectToIdMapping());
				final Map<String, EObject> idToEObjectMapping = new LinkedHashMap<String, EObject>(
					projectSpace.getProject().getIdToEObjectMapping());
				EMFStoreResource.class.cast(resource).setIdToEObjectMap(idToEObjectMapping, eObjectToIdMapping);
			}

			try {
				if (projectSpace.getLocalChangePackage().eResource().getURI().equals(resource.getURI())) {
					projectSpace.getLocalChangePackage().save();
				} else {
					ModelUtil.saveResource(resource, WorkspaceUtil.getResourceLogger());
				}
			} catch (final IOException e) {
				throw new RuntimeException(
					MessageFormat.format(Messages.ResourcePersister_SaveFailed, resource.getURI()));
			}
		}

		isDirty = false;
		fireDirtyStateChangedNotification();
	}

	/**
	 * Determine if there is resources that still need to be saved.
	 *
	 * @return true if there is resource to be saved.
	 */
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * Add a dirty state change listener.
	 *
	 * @param listener the listener
	 */
	public void addDirtyStateChangeLister(IDEObjectCollectionDirtyStateListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a dirty state change listener.
	 *
	 * @param listener the listener
	 */
	public void removeDirtyStateChangeLister(IDEObjectCollectionDirtyStateListener listener) {
		listeners.remove(listener);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#notify(org.eclipse.emf.common.notify.Notification,
	 *      org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection, org.eclipse.emf.ecore.EObject)
	 */
	public void notify(Notification notification, IdEObjectCollection collection, EObject modelElement) {

		// filter unwanted notifications that did not change anything in the
		// state
		if (filterStack.check(new NotificationInfo(notification).toAPI(), collection)) {
			return;
		}

		isDirty = true;

		if (!commandIsRunning) {
			saveDirtyResources(false);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementAdded(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementAdded(IdEObjectCollection collection, EObject modelElement) {
		isDirty = true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementRemoved(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementRemoved(IdEObjectCollection collection, EObject modelElement) {

		cleanResources(modelElement);

		// save the collection's resource from where the element has been removed
		isDirty = true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#collectionDeleted(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection)
	 */
	public void collectionDeleted(IdEObjectCollection collection) {

	}

	private void cleanResources(EObject deletedElement) {

		final Set<EObject> allDeletedModelElements = ModelUtil.getAllContainedModelElements(deletedElement, false);
		allDeletedModelElements.add(deletedElement);

		// TODO: check whether resource is contained in resources??
		for (final EObject element : allDeletedModelElements) {
			final Resource childResource = element.eResource();
			if (childResource != null) {
				childResource.getContents().remove(element);
			}
		}
	}

	private void fireDirtyStateChangedNotification() {
		for (final IDEObjectCollectionDirtyStateListener listener : listeners) {
			listener.notifyAboutDirtyStateChange();
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESUpdateObserver#inspectChanges(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      java.util.List, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean inspectChanges(ESLocalProject project, List<ESChangePackage> changePackages,
		IProgressMonitor monitor) {
		if (localProject == project) {
			saveDirtyResources(true);
		}
		return true;
	}

	/**
	 *
	 * {@inheritDoc}
	 */
	public void updateCompleted(ESLocalProject project, IProgressMonitor monitor) {
		if (localProject == project) {
			saveDirtyResources(true);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESCommitObserver#inspectChanges(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.ESChangePackage, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean inspectChanges(ESLocalProject project, ESChangePackage changePackage, IProgressMonitor monitor) {
		if (localProject == project) {
			saveDirtyResources(true);
		}
		return true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.observer.ESCommitObserver#commitCompleted(org.eclipse.emf.emfstore.client.ESLocalProject,
	 *      org.eclipse.emf.emfstore.server.model.versionspec.ESPrimaryVersionSpec,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void commitCompleted(ESLocalProject project, ESPrimaryVersionSpec newRevision, IProgressMonitor monitor) {
		if (localProject == project) {
			saveDirtyResources(true);
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.ESDisposable#dispose()
	 */
	public void dispose() {
		listeners = null;
		resources = null;
		localProject = null;
	}
}