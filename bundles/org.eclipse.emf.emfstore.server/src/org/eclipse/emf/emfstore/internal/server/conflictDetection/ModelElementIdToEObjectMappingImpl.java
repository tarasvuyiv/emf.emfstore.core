/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller
 * Maximilian Koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.conflictDetection;

import static org.eclipse.emf.emfstore.internal.server.model.versioning.operations.util.OperationUtil.isCreateDelete;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.Project;
import org.eclipse.emf.emfstore.internal.common.model.impl.ESModelElementIdToEObjectMappingImpl;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.server.ESCloseableIterable;

/**
 * @author emueller
 * @author koegel
 */
public class ModelElementIdToEObjectMappingImpl implements ModelElementIdToEObjectMapping {

	private final Map<String, EObject> idToEObjectMapping;
	private final ModelElementIdToEObjectMapping delegateMapping;
	private ESModelElementIdToEObjectMappingImpl apiImpl;

	/**
	 * Constructor.
	 *
	 * @param mapping
	 *            an initial mapping from {EObject}s to their {@link ModelElementId}s
	 */
	public ModelElementIdToEObjectMappingImpl(ModelElementIdToEObjectMapping mapping) {
		delegateMapping = mapping;
		idToEObjectMapping = new LinkedHashMap<String, EObject>();
	}

	/**
	 * Constructor.
	 *
	 * @param mapping
	 *            an initial mapping from {EObject}s to their {@link ModelElementId}s
	 * @param changePackages
	 *            a list of {@link AbstractChangePackage}s whose involved model elements should
	 *            be added to the mapping
	 */
	public ModelElementIdToEObjectMappingImpl(ModelElementIdToEObjectMapping mapping,
		List<AbstractChangePackage> changePackages) {
		this(mapping);
		for (final AbstractChangePackage changePackage : changePackages) {
			put(changePackage);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param mapping
	 *            an initial mapping from {EObject}s to their
	 *            {@link org.eclipse.emf.emfstore.common.model.ESModelElementId ESModelElementId}s
	 * @param operationsList1 list of operations for which involved model elements should
	 *            be added to the mapping
	 * @param operationsList2 list of operations for which involved model elements should
	 *            be added to the mapping
	 */
	public ModelElementIdToEObjectMappingImpl(ModelElementIdToEObjectMapping mapping,
		List<AbstractOperation> operationsList1, List<AbstractOperation> operationsList2) {
		this(mapping);
		this.put(operationsList1);
		this.put(operationsList2);
	}

	/**
	 * Constructor.
	 *
	 * @param project the project which contains the initial mapping from {EObject}s to their
	 *            {@link org.eclipse.emf.emfstore.common.model.ESModelElementId ESModelElementId}s
	 * @param changePackage a {@link AbstractChangePackage}s whose involved model elements should
	 *            be added to the mapping
	 */
	public ModelElementIdToEObjectMappingImpl(Project project, AbstractChangePackage changePackage) {
		this(project);
		this.put(changePackage);
	}

	/**
	 * Adds all model elements that are involved in operations contained in the {@link AbstractChangePackage} and their
	 * respective IDs into the mapping.
	 *
	 * @param changePackages
	 *            the {@link AbstractChangePackage} whose model elements should be added to the mapping
	 */
	public void put(List<AbstractChangePackage> changePackages) {
		for (final AbstractChangePackage changePackage : changePackages) {
			put(changePackage);
		}
	}

	/**
	 * Adds all model elements that are involved in operations contained in the {@link AbstractChangePackage} and their
	 * respective IDs into the mapping.
	 *
	 * @param changePackage
	 *            the {@link AbstractChangePackage} whose model elements should be added to the mapping
	 */
	public void put(AbstractChangePackage changePackage) {
		final ESCloseableIterable<AbstractOperation> operations = changePackage.operations();
		try {
			for (final AbstractOperation operation : operations.iterable()) {
				scanOperationIntoMapping(operation);
			}
		} finally {
			operations.close();
		}
	}

	/**
	 * Put the given elements from the operations to the mapping.
	 *
	 * @param operations the operations
	 */
	public void put(Iterable<AbstractOperation> operations) {
		for (final AbstractOperation op : operations) {
			scanOperationIntoMapping(op);
		}
	}

	private void scanOperationIntoMapping(AbstractOperation operation) {

		if (operation instanceof CompositeOperation) {
			final CompositeOperation composite = (CompositeOperation) operation;
			for (final AbstractOperation subOp : composite.getSubOperations()) {
				scanOperationIntoMapping(subOp);
			}
			return;
		}

		if (isCreateDelete(operation)) {
			final CreateDeleteOperation createDeleteOperation = (CreateDeleteOperation) operation;

			if (createDeleteOperation.getEObjectToIdMap().keySet().contains(null)) {
				// illegal state
				ModelUtil.logWarning(MessageFormat.format(
					Messages.ModelElementIdToEObjectMappingImpl_CreateDeleteOp_NullKey,
					createDeleteOperation.getIdentifier()));
			}

			// only put model elements into idToEObjectMapping if they aren't already contained in the delegate mapping
			for (final Map.Entry<EObject, ModelElementId> entry : createDeleteOperation.getEObjectToIdMap()
				.entrySet()) {
				final ModelElementId modelElementId = entry.getValue();
				final EObject modelElement = entry.getKey();
				if (delegateMapping.get(modelElementId) == null) {
					idToEObjectMapping.put(modelElementId.getId(), modelElement);
				}
			}
		}
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.common.model.ESIdToEObjectMapping#get(java.lang.Object)
	 */
	public EObject get(ModelElementId modelElementId) {
		final EObject eObject = delegateMapping.get(modelElementId);
		if (eObject != null) {
			return eObject;
		}
		if (modelElementId == null) {
			return null;
		}
		return idToEObjectMapping.get(modelElementId.getId());
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 */
	public ESModelElementIdToEObjectMappingImpl toAPI() {

		if (apiImpl == null) {
			apiImpl = createAPI();
		}

		return apiImpl;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 */
	public ESModelElementIdToEObjectMappingImpl createAPI() {
		return new ESModelElementIdToEObjectMappingImpl(this);
	}
}
