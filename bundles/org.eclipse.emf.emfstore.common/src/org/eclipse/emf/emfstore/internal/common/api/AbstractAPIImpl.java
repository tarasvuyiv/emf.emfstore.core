/*******************************************************************************
 * Copyright (c) 2012-2013 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.common.api;

/**
 * <p>
 * Common base class for all API implementation classes.
 * </p>
 * <p>
 * Given an internal type that defines an {@link APIDelegate} to an API type and a concrete API implementation class
 * that delegates to the internal type by means of the {@link InternalAPIDelegator} interface, this class is responsible
 * for setting up the mapping from the API type to the internal one.
 * </p>
 *
 *
 * @author emueller
 *
 * @param <INTERNAL> the internal API type to be mapped onto an API implementation class
 * @param <API> the interface available in the API
 *
 * @see InternalAPIDelegator
 */
public abstract class AbstractAPIImpl<API, INTERNAL extends APIDelegate<API>>
	implements InternalAPIDelegator<API, INTERNAL> {

	private final INTERNAL internal;

	/**
	 * Constructor.
	 *
	 * @param internal
	 *            the internal API to delegate to
	 */
	protected AbstractAPIImpl(INTERNAL internal) {
		this.internal = internal;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.emfstore.internal.common.api.InternalAPIDelegator#toInternalAPI()
	 */
	public INTERNAL toInternalAPI() {
		return internal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (toInternalAPI() == null ? 0 : toInternalAPI().hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final AbstractAPIImpl<API, APIDelegate<API>> other = (AbstractAPIImpl<API, APIDelegate<API>>) obj;
		if (toInternalAPI() == null) {
			if (other.toInternalAPI() != null) {
				return false;
			}
		} else if (!toInternalAPI().equals(other.toInternalAPI())) {
			return false;
		}
		return true;
	}
}
