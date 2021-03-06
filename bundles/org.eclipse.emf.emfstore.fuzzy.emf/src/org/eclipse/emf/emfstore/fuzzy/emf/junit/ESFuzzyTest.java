/*******************************************************************************
 * Copyright (c) 2012-2014 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Julian Sommerfeldt - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.fuzzy.emf.junit;

/**
 *
 * Class to get a connection between a test and a specific run of it.
 *
 * @author Julian Sommerfeldt
 * @since 2.0
 *
 */
public class ESFuzzyTest {

	private final String name;

	private final int seedCount;

	/**
	 * @param name
	 *            The name of the test.
	 * @param seedCount
	 *            The count (run) of the test.
	 */
	public ESFuzzyTest(String name, int seedCount) {
		this.name = name;
		this.seedCount = seedCount;
	}

	/**
	 * @return The name of the test.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return The run of this test.
	 */
	public int getSeedCount() {
		return seedCount;
	}
}
