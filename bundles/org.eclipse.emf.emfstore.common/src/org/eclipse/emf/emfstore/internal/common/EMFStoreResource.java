/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLHelper;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

/**
 * EMFStore Resource, inherits from XMIResource and sets intrinsic ID to EObjectMap optimization.
 *
 * @author emueller
 */
public class EMFStoreResource extends XMIResourceImpl {

	/**
	 * Default constructor.
	 *
	 * @param uri
	 *            the URI of the resource
	 */
	public EMFStoreResource(final URI uri) {
		super(uri);
		setIntrinsicIDToEObjectMap(new LinkedHashMap<String, EObject>());
	}

	/**
	 * Initialize the ID to EObjects map and reverse map directly. The map must be consistent with each other.
	 *
	 * @param idToEObjectMap
	 *            a map from IDs to EObject in the resource
	 * @param eObjectToIdMap
	 *            a map from EObjects to IDs in the resource
	 */
	public void setIdToEObjectMap(final Map<String, EObject> idToEObjectMap,
		final Map<EObject, String> eObjectToIdMap) {
		this.idToEObjectMap = idToEObjectMap;
		eObjectToIDMap = eObjectToIdMap;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl#createXMLHelper()
	 */
	@Override
	protected XMLHelper createXMLHelper() {
		return new EMFStoreResourceHelper(this);
	}
}