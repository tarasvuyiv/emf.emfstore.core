/*******************************************************************************
 * Copyright 2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Tobias Verhoeven
 ******************************************************************************/
package org.eclipse.emf.emfstore.server.model;

import org.eclipse.emf.emfstore.common.model.ESFactory;

/**
 * A factory for creating {@link ESLogMessage} objects.
 * 
 * @author Tobias Verhoeven
 */
public interface ESLogMessageFactory extends ESFactory {

	/**
	 * Creates a new log message with the specified message and author.
	 * 
	 * @param message
	 *            the actual log message
	 * @param author
	 *            the author of the log message
	 * @return the created {@link ESLogMessage}
	 */
	ESLogMessage createLogMessage(String message, String author);
}