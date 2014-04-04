/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * chodnick
 ******************************************************************************/
package org.eclipse.emf.emfstore.client.changetracking.test.notification;

import org.eclipse.emf.emfstore.client.changetracking.test.notification.AttributeNotificationTest;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.ContainmentNotificationTest;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.MoveNotificationTest;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.MultiReferenceNotificationTest;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.NotificationToOperationConverterTest;
import org.eclipse.emf.emfstore.client.changetracking.test.notification.ReferenceNotificationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test Suite for running all tests of workspace.
 * 
 * @author chodnick
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ 
	AttributeNotificationTest.class, 
	ReferenceNotificationTest.class, 
	MoveNotificationTest.class,
	MultiReferenceNotificationTest.class, 
	ContainmentNotificationTest.class, 
	NotificationToOperationConverterTest.class })
public class AllNotificationTests {

}