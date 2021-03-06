/*******************************************************************************
 * Copyright (c) 2008-2014 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Maximilian Koegel, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.impl;

import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver;
import org.eclipse.emf.emfstore.internal.client.model.CompositeOperationHandle;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ESWorkspaceProviderImpl;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.notification.recording.NotificationRecorder;
import org.eclipse.emf.emfstore.internal.client.observers.OperationObserver;
import org.eclipse.emf.emfstore.internal.common.ESDisposable;
import org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection;
import org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.semantic.SemanticCompositeOperation;

/**
 * This class acts as a simple wrapper around the operation recorder and provides convenience methods
 * for undoing operations and handling composite operations.
 *
 * @author koegel
 * @author emueller
 */
public class OperationManager implements OperationRecorderListener, ESDisposable, ESCommandObserver,
	IdEObjectCollectionChangeObserver {

	private final OperationRecorder operationRecorder;
	private final ProjectSpace projectSpace;

	/**
	 * Constructor.
	 *
	 * @param projectSpace
	 *            the project space the operation manager should be attached to
	 */
	public OperationManager(ProjectSpaceBase projectSpace) {
		operationRecorder = new OperationRecorder(projectSpace);
		ESWorkspaceProviderImpl.getObserverBus().register(operationRecorder);
		operationRecorder.addOperationRecorderListener(this);
		configureOperationRecorder();
		this.projectSpace = operationRecorder.getProjectSpace();
	}

	private void configureOperationRecorder() {
		final OperationRecorderConfig config = operationRecorder.getConfig();

		// incoming cross-references are cut off by default
		config.setCutOffIncomingCrossReferences(
			Configuration.getClientBehavior().isCutOffIncomingCrossReferencesActivated());

		// usage of commands is not forced by default
		config
			.setForceCommands(
				Configuration.getClientBehavior().isForceCommandsActived());

		// cut elements are added automatically as regular model elements by default
		config.setDenyAddCutElementsToModelElements(
			Configuration.getClientBehavior().isDenyAddCutElementsToModelElementsFeatureActived());

		config.setOperationModifier(
			Configuration.getClientBehavior().getOperationModifier());
	}

	/**
	 * Undo the last operation of the projectSpace.
	 */
	public void undoLastOperation() {
		projectSpace.undoLastOperations(1);
	}

	/**
	 * Notifies all operations observer that an operation has been undone.
	 *
	 * @param operation
	 *            the operation that has been undone
	 */
	public void notifyOperationUndone(AbstractOperation operation) {
		ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(OperationObserver.class)
			.operationUndone(projectSpace, operation);
	}

	/**
	 * Notify the operation observer that an operation has just completed.
	 *
	 * @param operation
	 *            the operation
	 */
	void notifyOperationExecuted(AbstractOperation operation) {
		ESWorkspaceProviderImpl
			.getObserverBus()
			.notify(OperationObserver.class)
			.operationExecuted(projectSpace, operation);
	}

	/**
	 * Aborts the current composite operation.
	 */
	public void abortCompositeOperation() {
		undoLastOperation();
		operationRecorder.abortCompositeOperation();
	}

	/**
	 * Complete the current composite operation.
	 */
	public void endCompositeOperation() {
		notifyOperationExecuted(operationRecorder.getCompositeOperation());
		operationRecorder.endCompositeOperation();
	}

	/**
	 * Replace and complete the current composite operation.
	 *
	 * @param semanticCompositeOperation
	 *            the semantic operation that replaces the composite operation
	 */
	public void endCompositeOperation(SemanticCompositeOperation semanticCompositeOperation) {
		projectSpace.getLocalChangePackage().removeAtEnd(1);
		projectSpace.getLocalChangePackage().add(semanticCompositeOperation);
		endCompositeOperation();
	}

	/**
	 * Opens up a handle for creating a composite operation.
	 *
	 * @return the handle for the composite operation
	 */
	public CompositeOperationHandle beginCompositeOperation() {
		return operationRecorder.beginCompositeOperation();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.client.model.impl.OperationRecorderListener#operationsRecorded(java.util.List)
	 */
	public void operationsRecorded(List<? extends AbstractOperation> operations) {
		projectSpace.addOperations(operations);
	}

	/**
	 * Clears all recorded operations.
	 *
	 * @return the cleared operations
	 */
	public List<AbstractOperation> clearOperations() {
		return operationRecorder.clearOperations();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.ESDisposable#dispose()
	 */
	public void dispose() {
		ESWorkspaceProviderImpl.getObserverBus().unregister(operationRecorder);
		operationRecorder.removeOperationRecorderListener(this);
	}

	/**
	 * Returns the notification recorder.
	 *
	 * @return the notification recorder
	 */
	public NotificationRecorder getNotificationRecorder() {
		return operationRecorder.getNotificationRecorder();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandStarted(org.eclipse.emf.common.command.Command)
	 */
	public void commandStarted(Command command) {
		operationRecorder.commandStarted(command);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandCompleted(org.eclipse.emf.common.command.Command)
	 */
	public void commandCompleted(Command command) {
		operationRecorder.commandCompleted(command);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.client.changetracking.ESCommandObserver#commandFailed(org.eclipse.emf.common.command.Command,
	 *      java.lang.Exception)
	 */
	public void commandFailed(Command command, Exception exception) {
		operationRecorder.commandFailed(command, exception);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#notify(org.eclipse.emf.common.notify.Notification,
	 *      org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection, org.eclipse.emf.ecore.EObject)
	 */
	public void notify(Notification notification, IdEObjectCollection collection, EObject modelElement) {
		operationRecorder.notify(notification, collection, modelElement);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementAdded(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementAdded(IdEObjectCollection collection, EObject modelElement) {
		operationRecorder.modelElementAdded(collection, modelElement);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#modelElementRemoved(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection,
	 *      org.eclipse.emf.ecore.EObject)
	 */
	public void modelElementRemoved(IdEObjectCollection collection, EObject modelElement) {
		operationRecorder.modelElementRemoved(collection, modelElement);
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.model.util.IdEObjectCollectionChangeObserver#collectionDeleted(org.eclipse.emf.emfstore.internal.common.model.IdEObjectCollection)
	 */
	public void collectionDeleted(IdEObjectCollection collection) {
		operationRecorder.collectionDeleted(collection);
	}

	/**
	 * Starts change recording.
	 */
	public void startChangeRecording() {
		operationRecorder.startChangeRecording();
	}

	/**
	 * Stops change recording.
	 */
	public void stopChangeRecording() {
		operationRecorder.stopChangeRecording();
	}

	/**
	 * Returns the configuration options for the operation recorder.
	 *
	 * @return the operation recorder configuration options
	 */
	public OperationRecorderConfig getRecorderConfig() {
		return operationRecorder.getConfig();
	}

	/**
	 * Notifies the manager that a command has been completed.
	 *
	 * @param command
	 *            the {@link Command} that has been completed
	 * @param isNestedCommand
	 *            whether the completed command is a command inside another one.
	 *            If the completed command is nested, the {@link OperationRecorder}'s
	 *            internal state maintains the state of a command still being run
	 */
	public void commandCompleted(Command command, boolean isNestedCommand) {
		operationRecorder.commandCompleted(command, isNestedCommand);
	}

	/**
	 * Whether the operation manager considers a command is being run.
	 *
	 * @return <code>true</code> if a command is being run, <code>false</code> otherwise
	 */
	public boolean isCommandRunning() {
		return operationRecorder.isCommandRunning();
	}
}