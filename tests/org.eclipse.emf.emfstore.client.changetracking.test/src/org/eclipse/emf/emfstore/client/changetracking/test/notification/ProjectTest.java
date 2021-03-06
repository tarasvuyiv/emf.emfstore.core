/*******************************************************************************
 * Copyright (c) 2014-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Edgar Mueller - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.changetracking.test.notification;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.common.cases.ESTest;
import org.eclipse.emf.emfstore.client.test.common.dsl.Add;
import org.eclipse.emf.emfstore.client.test.common.dsl.Create;
import org.eclipse.emf.emfstore.client.util.ESVoidCallable;
import org.eclipse.emf.emfstore.client.util.RunESCommand;
import org.eclipse.emf.emfstore.internal.client.model.ProjectSpace;
import org.eclipse.emf.emfstore.internal.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.internal.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.server.model.versioning.AbstractChangePackage;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.emf.emfstore.test.model.TestElement;
import org.junit.Test;

/**
 * Test case for Bug 450069 (https://bugs.eclipse.org/bugs/show_bug.cgi?id=450069).
 *
 * @author emueller
 *
 */
public class ProjectTest extends ESTest {

	@Test
	public void changeCrossReferenceToDeletedModelElementWithinProject() throws ESException, SerializationException {
		final TestElement foo = Create.testElement("foo"); //$NON-NLS-1$
		final TestElement bar = Create.testElement("bar"); //$NON-NLS-1$
		final TestElement baz = Create.testElement("baz"); //$NON-NLS-1$

		bar.getContainedElements().add(baz);

		Add.toProject(getLocalProject(), foo, bar);
		final ProjectSpace clonedProjectSpace = cloneProjectSpace(getProjectSpace());
		clearOperations();

		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				getProject().getModelElements().remove(bar);
				foo.getReferences().add(baz);
			}
		});

		assertTrue(getProject().contains(baz));

		final AbstractChangePackage localChangePackage = getProjectSpace().getLocalChangePackage();
		RunESCommand.run(new ESVoidCallable() {
			@Override
			public void run() {
				localChangePackage.reverse().apply(getProject());
			}
		});

		assertTrue(ModelUtil.areEqual(getProject(), clonedProjectSpace.getProject()));
	}
}
