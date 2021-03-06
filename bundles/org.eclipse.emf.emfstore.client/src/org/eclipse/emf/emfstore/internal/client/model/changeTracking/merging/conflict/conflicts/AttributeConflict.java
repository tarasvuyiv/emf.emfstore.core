/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Otto von Wesendonk, Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.conflicts;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.DecisionManager;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictDescription;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.ConflictOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.VisualConflict;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.conflict.options.MergeTextOption;
import org.eclipse.emf.emfstore.internal.client.model.changeTracking.merging.util.DecisionUtil;
import org.eclipse.emf.emfstore.internal.server.conflictDetection.ConflictBucket;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnkownFeatureException;

/**
 * Conflict for two attribute operations.
 *
 * @author wesendon
 * @author emueller
 */
public class AttributeConflict extends VisualConflict {

	private static final String ATTRIBUTE_GIF = "attribute.gif"; //$NON-NLS-1$
	private static final String THEIR_VALUE_KEY = "theirvalue"; //$NON-NLS-1$
	private static final String OLD_VALUE_KEY = "oldvalue"; //$NON-NLS-1$
	private static final String MY_VALUE_KEY = "myvalue"; //$NON-NLS-1$
	private static final String ATTRIBUTE_CONFLICT_KEY = "attributeconflict"; //$NON-NLS-1$

	/**
	 * Default constructor.
	 *
	 * @param conflictBucket the conflict
	 * @param decisionManager decisionmanager
	 */
	public AttributeConflict(ConflictBucket conflictBucket, DecisionManager decisionManager) {
		super(conflictBucket, decisionManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConflictDescription initConflictDescription(ConflictDescription description) {
		description.setDescription(DecisionUtil.getDescription(ATTRIBUTE_CONFLICT_KEY, getDecisionManager()
			.isBranchMerge()));
		description.add(MY_VALUE_KEY, getMyOperation(AttributeOperation.class).getNewValue());
		description.add(OLD_VALUE_KEY, getMyOperation(AttributeOperation.class).getOldValue());
		description.add(THEIR_VALUE_KEY, getTheirOperation(AttributeOperation.class).getNewValue());
		description.setImage(ATTRIBUTE_GIF);

		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initConflictOptions(List<ConflictOption> options) {
		initOptionsWithOutMerge(options, true);
	}

	/**
	 * Allows to init options, without adding a merge text option.
	 *
	 * @param options list of options
	 * @param withMerge true, if merge text option ({@link MergeTextOption}) should be added
	 */
	protected void initOptionsWithOutMerge(List<ConflictOption> options, boolean withMerge) {
		final AttributeOperation attributeOperation = getMyOperation(AttributeOperation.class);
		final ConflictOption myOption = new ConflictOption(attributeOperation.getNewValue(),
			ConflictOption.OptionType.MyOperation);
		myOption.setDetailProvider(DecisionUtil.WIDGET_MULTILINE);
		myOption.addOperations(getMyOperations());
		options.add(myOption);

		final ConflictOption theirOption = new ConflictOption(
			getTheirOperation(AttributeOperation.class).getNewValue(),
			ConflictOption.OptionType.TheirOperation);
		theirOption.setDetailProvider(DecisionUtil.WIDGET_MULTILINE);
		theirOption.addOperations(getTheirOperations());
		options.add(theirOption);

		final EObject eObject = getDecisionManager().getModelElement(attributeOperation.getModelElementId());
		EStructuralFeature feature;
		boolean isMultiline = false;
		try {
			feature = attributeOperation.getFeature(eObject);
			isMultiline = isMultiline(eObject, feature);
		} catch (final UnkownFeatureException e) {
			// ignore
		}

		if (withMerge && DecisionUtil.detailsNeeded(this) && isMultiline) {
			final MergeTextOption mergeOption = new MergeTextOption();
			mergeOption.add(myOption);
			mergeOption.add(theirOption);
			options.add(mergeOption);
		}
	}

	private boolean isMultiline(EObject eObject, EStructuralFeature attribute) {
		final ComposedAdapterFactory composedAdapterFactory = new ComposedAdapterFactory(
			ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
		final AdapterFactoryItemDelegator adapterFactoryItemDelegator = new AdapterFactoryItemDelegator(
			composedAdapterFactory);
		final IItemPropertyDescriptor propertyDescriptor = adapterFactoryItemDelegator
			.getPropertyDescriptor(eObject, attribute);
		boolean isMultiLine = false;
		if (propertyDescriptor != null) {
			isMultiLine = propertyDescriptor.isMultiLine(eObject);
		}
		composedAdapterFactory.dispose();
		return isMultiLine;
	}
}