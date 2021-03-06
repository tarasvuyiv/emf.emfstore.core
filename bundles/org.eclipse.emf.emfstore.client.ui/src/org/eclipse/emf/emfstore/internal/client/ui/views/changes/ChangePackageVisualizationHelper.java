/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * shterev
 * emueller
 * koegel
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.changes;

import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.common.OperationCustomLabelProvider;
import org.eclipse.emf.emfstore.internal.common.ExtensionRegistry;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementIdToEObjectMapping;
import org.eclipse.emf.emfstore.internal.common.model.ModelFactory;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceMoveOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnkownFeatureException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.provider.AbstractOperationItemProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * A helper class for the visualization of change packages.
 *
 * @author koegel
 * @author shterev
 * @author emueller
 */
public class ChangePackageVisualizationHelper {

	private static final String ICON_MODIFY_OVERLAY = "icons/modify_overlay.png"; //$NON-NLS-1$

	private static final String ICON_ADD_OVERLAY = "icons/add_overlay.png"; //$NON-NLS-1$

	private static final String ICON_DELETE_OVERLAY = "icons/delete_overlay.png"; //$NON-NLS-1$

	private static final String ICON_LINK_OVERLAY = "icons/link_overlay.png"; //$NON-NLS-1$

	private final ModelElementIdToEObjectMapping idToEObjectMapping;

	private OperationCustomLabelProvider customLabelProvider;

	/**
	 * Constructor.
	 *
	 * @param idToEObjectMapping
	 *            the ID to EObject mapping that is holding the EObjects that are going to be visualized
	 *            as part of the change packages
	 */
	public ChangePackageVisualizationHelper(ModelElementIdToEObjectMapping idToEObjectMapping) {
		this.idToEObjectMapping = idToEObjectMapping;
	}

	/**
	 * Get the overlay image for an operation.
	 *
	 * @param operation
	 *            the operation
	 * @return the ImageDescriptor
	 */
	public ImageDescriptor getOverlayImage(AbstractOperation operation) {
		String overlay = null;
		if (operation instanceof CreateDeleteOperation) {
			final CreateDeleteOperation op = (CreateDeleteOperation) operation;
			if (op.isDelete()) {
				overlay = ICON_DELETE_OVERLAY;
			} else {
				overlay = ICON_ADD_OVERLAY;
			}
		} else if (operation instanceof AttributeOperation) {
			final AttributeOperation op = (AttributeOperation) operation;
			if (op.getNewValue() == null) {
				overlay = ICON_DELETE_OVERLAY;
			} else if (op.getOldValue() == null) {
				overlay = ICON_ADD_OVERLAY;
			} else {
				overlay = ICON_MODIFY_OVERLAY;
			}
		} else if (operation instanceof SingleReferenceOperation) {
			final SingleReferenceOperation op = (SingleReferenceOperation) operation;
			if (op.getNewValue() == null) {
				overlay = ICON_DELETE_OVERLAY;
			} else {
				overlay = ICON_LINK_OVERLAY;
			}
		} else if (operation instanceof MultiReferenceOperation) {
			final MultiReferenceOperation op = (MultiReferenceOperation) operation;
			if (op.getReferencedModelElements().size() > 0) {
				overlay = ICON_LINK_OVERLAY;
			}
		} else if (operation instanceof MultiReferenceMoveOperation) {
			overlay = ICON_LINK_OVERLAY;
		} else {
			overlay = ICON_MODIFY_OVERLAY;
		}

		final ImageDescriptor overlayDescriptor = Activator.getImageDescriptor(overlay);
		return overlayDescriptor;
	}

	/**
	 * Get an image for the operation.
	 *
	 * @param emfProvider
	 *            the label provider
	 * @param operation
	 *            the operation
	 * @return an image
	 */
	public Image getImage(ILabelProvider emfProvider, AbstractOperation operation) {

		// check if a custom label provider can provide an image
		final Image image = getCustomOperationProviderLabel(operation);
		if (image != null) {
			return image;
		}

		return emfProvider.getImage(operation);
	}

	private Image getCustomOperationProviderLabel(AbstractOperation operation) {
		final OperationCustomLabelProvider customLabelProvider = getLabelProvider();
		if (customLabelProvider != null) {
			try {
				return (Image) customLabelProvider.getImage(operation);
				// BEGIN SUPRESS CATCH EXCEPTION
			} catch (final RuntimeException e) {
				// END SUPRESS CATCH EXCEPTION
				ModelUtil.logWarning(Messages.ChangePackageVisualizationHelper_CustomOperationProvider_LoadImageFailed,
					e);
			} finally {
				customLabelProvider.dispose();
			}
		}
		return null;
	}

	/**
	 * Returns a description for the given operation.
	 *
	 * @param op
	 *            the operation to generate a description for
	 * @return the description for the given operation
	 */
	public String getDescription(AbstractOperation op) {
		if (op instanceof CompositeOperation) {
			final CompositeOperation compositeOperation = (CompositeOperation) op;
			// artificial composite because of opposite ref, take description of
			// main operation
			if (compositeOperation.getMainOperation() != null) {
				return getDescription(compositeOperation.getMainOperation());
			}
		}

		// check of a custom operation label provider can provide a label
		final OperationCustomLabelProvider customLabelProvider = getLabelProvider();
		String decorate;
		try {
			decorate = decorate(customLabelProvider, op);
			return decorate;
		} finally {
			customLabelProvider.dispose();
		}
	}

	private OperationCustomLabelProvider getLabelProvider() {
		if (customLabelProvider == null) {
			customLabelProvider = ExtensionRegistry.INSTANCE.get(
				OperationCustomLabelProvider.ID, OperationCustomLabelProvider.class,
				new DefaultOperationLabelProvider(),
				true);
		}
		return customLabelProvider;
	}

	private String decorate(OperationCustomLabelProvider labelProvider, AbstractOperation op) {
		final String namesResolved = resolveIds(labelProvider, labelProvider.getDescription(op),
			AbstractOperationItemProvider.NAME_TAG__SEPARATOR, op);
		final String allResolved = resolveIds(labelProvider, namesResolved,
			AbstractOperationItemProvider.NAME_CLASS_TAG_SEPARATOR, op);
		if (op instanceof ReferenceOperation) {
			return resolveTypes(allResolved, (ReferenceOperation) op);
		}
		if (op instanceof CompositeOperation && ((CompositeOperation) op).getMainOperation() != null
			&& ((CompositeOperation) op).getMainOperation() instanceof ReferenceOperation) {
			return resolveTypes(allResolved, (ReferenceOperation) ((CompositeOperation) op).getMainOperation());
		}

		return allResolved;
	}

	private String resolveTypes(String unresolvedString, ReferenceOperation op) {
		final EObject modelElement = getModelElement(op.getModelElementId());
		String type;
		if (modelElement == null) {
			type = "ModelElement"; //$NON-NLS-1$
		} else {
			try {
				final EStructuralFeature feature = op.getFeature(modelElement);
				type = feature.getEType().getName();
			} catch (final UnkownFeatureException e) {
				type = "ModelElement"; //$NON-NLS-1$
			}
		}

		return unresolvedString.replace(AbstractOperationItemProvider.REFERENCE_TYPE_TAG_SEPARATOR, type);
	}

	private String resolveIds(OperationCustomLabelProvider labelProvider, String unresolvedString,
		String devider, AbstractOperation op) {
		final String[] strings = unresolvedString.split(devider);
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			if (isOdd(i)) {
				final ModelElementId modelElementId = ModelFactory.eINSTANCE.createModelElementId();
				modelElementId.setId(strings[i]);
				final EObject modelElement = getModelElement(modelElementId);
				if (modelElement != null) {
					stringBuilder.append(labelProvider.getModelElementName(modelElement));
				} else if (op instanceof CreateDeleteOperation) {
					final CreateDeleteOperation createDeleteOp = (CreateDeleteOperation) op;
					for (final Map.Entry<EObject, ModelElementId> entry : createDeleteOp.getEObjectToIdMap()) {
						if (entry.getValue().equals(modelElementId)) {
							stringBuilder.append(labelProvider.getModelElementName(entry.getKey()));
							break;
						}
					}
				}
			} else {
				stringBuilder.append(strings[i]);
			}
		}
		return stringBuilder.toString();
	}

	private boolean isOdd(int i) {
		final int res = i % 2;
		return res == -1 || res == 1;
	}

	/**
	 * Get a model element instance from the project for the given id.
	 *
	 * @param modelElementId
	 *            the id
	 * @return the model element instance
	 */
	public EObject getModelElement(ModelElementId modelElementId) {
		return idToEObjectMapping.get(modelElementId);
	}

	/**
	 *
	 */
	public void dispose() {
		if (customLabelProvider != null) {
			customLabelProvider.dispose();
		}
	}
}
