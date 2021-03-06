/*******************************************************************************
 * Copyright (c) 2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.server.model.versioning.impl.persistent;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.operations.AbstractOperation;

import com.google.common.base.Optional;

/**
 * A file-based iterator for {@link AbstractOperation}s.
 *
 * @author emueller
 * @since 1.5
 *
 */
public class OperationIterator implements Iterator<AbstractOperation> {

	private Optional<AbstractOperation> operation;
	private OperationEmitter operationEmitter;
	// private ReadLineCapable reader;
	private boolean isInitialized;
	private final String operationsFilePath;
	private final Direction direction;

	/**
	 * Constructor.
	 *
	 * @param operationsFilePath
	 *            the absolute path to the operations file
	 * @param direction
	 *            the reading {@link Direction}
	 */
	public OperationIterator(String operationsFilePath, Direction direction) {
		this.operationsFilePath = operationsFilePath;
		this.direction = direction;
		init();
	}

	private void init() {
		operationEmitter = new OperationEmitter(direction, new File(operationsFilePath));
		isInitialized = true;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		if (!isInitialized) {
			init();
		}
		try {
			operation = operationEmitter.tryEmit();
			final boolean hasNext = operation.isPresent();
			if (!hasNext) {
				close();
			}
			return hasNext;
		} catch (final IOException ex) {
			// TODO
			// replace operations file
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#next()
	 */
	public AbstractOperation next() {
		if (operation == null) {
			hasNext();
		}
		return operation.get();
	}

	/**
	 *
	 * {@inheritDoc}
	 *
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		throw new NotImplementedException();
	}

	/**
	 * Closes the underlying operations file.
	 */
	public void close() {
		operationEmitter.close();
	}
}
