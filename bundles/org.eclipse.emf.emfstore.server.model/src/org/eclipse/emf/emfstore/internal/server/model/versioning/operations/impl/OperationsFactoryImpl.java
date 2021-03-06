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
package org.eclipse.emf.emfstore.internal.server.model.versioning.operations.impl;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.emfstore.internal.common.model.ModelElementId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CompositeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ContainmentType;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.CreateDeleteOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.ModelElementGroup;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeMoveOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiAttributeSetOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceMoveOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.MultiReferenceSetOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationGroup;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationId;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsFactory;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.OperationsPackage;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.SingleReferenceOperation;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.UnsetType;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 *
 * @generated
 */
public class OperationsFactoryImpl extends EFactoryImpl implements OperationsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public static OperationsFactory init() {
		try {
			final OperationsFactory theOperationsFactory = (OperationsFactory) EPackage.Registry.INSTANCE
				.getEFactory(OperationsPackage.eNS_URI);
			if (theOperationsFactory != null) {
				return theOperationsFactory;
			}
		} catch (final Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new OperationsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 *
	 * @generated
	 */
	public OperationsFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case OperationsPackage.COMPOSITE_OPERATION:
			return createCompositeOperation();
		case OperationsPackage.CREATE_DELETE_OPERATION:
			return createCreateDeleteOperation();
		case OperationsPackage.ATTRIBUTE_OPERATION:
			return createAttributeOperation();
		case OperationsPackage.MULTI_ATTRIBUTE_OPERATION:
			return createMultiAttributeOperation();
		case OperationsPackage.MULTI_ATTRIBUTE_SET_OPERATION:
			return createMultiAttributeSetOperation();
		case OperationsPackage.MULTI_ATTRIBUTE_MOVE_OPERATION:
			return createMultiAttributeMoveOperation();
		case OperationsPackage.SINGLE_REFERENCE_OPERATION:
			return createSingleReferenceOperation();
		case OperationsPackage.MULTI_REFERENCE_SET_OPERATION:
			return createMultiReferenceSetOperation();
		case OperationsPackage.MULTI_REFERENCE_OPERATION:
			return createMultiReferenceOperation();
		case OperationsPackage.MULTI_REFERENCE_MOVE_OPERATION:
			return createMultiReferenceMoveOperation();
		case OperationsPackage.OPERATION_ID:
			return createOperationId();
		case OperationsPackage.OPERATION_GROUP:
			return createOperationGroup();
		case OperationsPackage.MODEL_ELEMENT_GROUP:
			return createModelElementGroup();
		case OperationsPackage.EOBJECT_TO_MODEL_ELEMENT_ID_MAP:
			return (EObject) createEObjectToModelElementIdMap();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case OperationsPackage.CONTAINMENT_TYPE:
			return createContainmentTypeFromString(eDataType, initialValue);
		case OperationsPackage.UNSET_TYPE:
			return createUnsetTypeFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case OperationsPackage.CONTAINMENT_TYPE:
			return convertContainmentTypeToString(eDataType, instanceValue);
		case OperationsPackage.UNSET_TYPE:
			return convertUnsetTypeToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public CompositeOperation createCompositeOperation() {
		final CompositeOperationImpl compositeOperation = new CompositeOperationImpl();
		return compositeOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public CreateDeleteOperation createCreateDeleteOperation() {
		final CreateDeleteOperationImpl createDeleteOperation = new CreateDeleteOperationImpl();
		return createDeleteOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public AttributeOperation createAttributeOperation() {
		final AttributeOperationImpl attributeOperation = new AttributeOperationImpl();
		return attributeOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiAttributeOperation createMultiAttributeOperation() {
		final MultiAttributeOperationImpl multiAttributeOperation = new MultiAttributeOperationImpl();
		return multiAttributeOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiAttributeSetOperation createMultiAttributeSetOperation() {
		final MultiAttributeSetOperationImpl multiAttributeSetOperation = new MultiAttributeSetOperationImpl();
		return multiAttributeSetOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiAttributeMoveOperation createMultiAttributeMoveOperation() {
		final MultiAttributeMoveOperationImpl multiAttributeMoveOperation = new MultiAttributeMoveOperationImpl();
		return multiAttributeMoveOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public SingleReferenceOperation createSingleReferenceOperation() {
		final SingleReferenceOperationImpl singleReferenceOperation = new SingleReferenceOperationImpl();
		return singleReferenceOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiReferenceOperation createMultiReferenceOperation() {
		final MultiReferenceOperationImpl multiReferenceOperation = new MultiReferenceOperationImpl();
		return multiReferenceOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiReferenceSetOperation createMultiReferenceSetOperation() {
		final MultiReferenceSetOperationImpl multiReferenceSetOperation = new MultiReferenceSetOperationImpl();
		return multiReferenceSetOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public MultiReferenceMoveOperation createMultiReferenceMoveOperation() {
		final MultiReferenceMoveOperationImpl multiReferenceMoveOperation = new MultiReferenceMoveOperationImpl();
		return multiReferenceMoveOperation;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public OperationId createOperationId() {
		final OperationIdImpl operationId = new OperationIdImpl();
		return operationId;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public OperationGroup createOperationGroup() {
		final OperationGroupImpl operationGroup = new OperationGroupImpl();
		return operationGroup;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ModelElementGroup createModelElementGroup() {
		final ModelElementGroupImpl modelElementGroup = new ModelElementGroupImpl();
		return modelElementGroup;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public Map.Entry<EObject, ModelElementId> createEObjectToModelElementIdMap() {
		final EObjectToModelElementIdMapImpl eObjectToModelElementIdMap = new EObjectToModelElementIdMapImpl();
		return eObjectToModelElementIdMap;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public ContainmentType createContainmentTypeFromString(EDataType eDataType, String initialValue) {
		final ContainmentType result = ContainmentType.get(initialValue);
		if (result == null) {
			throw new IllegalArgumentException(
				"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertContainmentTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public UnsetType createUnsetTypeFromString(EDataType eDataType, String initialValue) {
		final UnsetType result = UnsetType.get(initialValue);
		if (result == null) {
			throw new IllegalArgumentException(
				"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public String convertUnsetTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @generated
	 */
	public OperationsPackage getOperationsPackage() {
		return (OperationsPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 *
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static OperationsPackage getPackage() {
		return OperationsPackage.eINSTANCE;
	}

} // OperationsFactoryImpl