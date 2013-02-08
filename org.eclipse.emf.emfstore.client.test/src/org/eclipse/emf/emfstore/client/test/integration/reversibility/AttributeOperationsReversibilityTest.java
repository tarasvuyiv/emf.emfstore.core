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
package org.eclipse.emf.emfstore.client.test.integration.reversibility;

import static org.junit.Assert.assertTrue;

import org.eclipse.emf.emfstore.client.test.integration.forward.IntegrationTestHelper;
import org.eclipse.emf.emfstore.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.server.exceptions.EMFStoreException;
import org.junit.Test;

/**
 * @author Hodaie
 */
public class AttributeOperationsReversibilityTest extends OperationsReversibilityTest {

	private long randomSeed = 1;

	/**
	 * Finds an attribute with isMany = true and moves elements inside this attribute.
	 * 
	 * @throws EMFStoreException EmfStoreException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void multiAttributeMoveReversibilityTest() throws SerializationException, EMFStoreException {
		System.out.println("MultiAttributeMoveReversibilityTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				testHelper.doMultiAttributeMove();
				getTestProjectSpace().revert();
			}
		}.run(false);

		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));
	}

	/**
	 * 1. Get a random model element form test project; 2. get randomly one of its attributes. 3. change the attribute
	 * 
	 * @throws EMFStoreException EmfStoreException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void attributeChangeReversibilityTest() throws SerializationException, EMFStoreException {
		System.out.println("AttributeChangeReversibilityTest");

		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testHelper.doChangeAttribute();
			}

		}.run(false);

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getTestProjectSpace().revert();
			}
		}.run(false);

		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));

	}

	/**
	 * Change the same attribute on a randomly selected ME twice.
	 * 
	 * @throws EMFStoreException EmfStoreException
	 * @throws SerializationException SerializationException
	 */
	@Test
	public void attributeTransitiveChangeReversibilityTest() throws SerializationException, EMFStoreException {
		System.out.println("AttributeTransitiveChangeReversibilityTest");
		final IntegrationTestHelper testHelper = new IntegrationTestHelper(randomSeed, getTestProject());
		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				testHelper.doAttributeTransitiveChange();
			}
		}.run(false);

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				getTestProjectSpace().revert();
			}
		}.run(false);

		assertTrue(ModelUtil.areEqual(getTestProject(), getCompareProject()));
	}

}