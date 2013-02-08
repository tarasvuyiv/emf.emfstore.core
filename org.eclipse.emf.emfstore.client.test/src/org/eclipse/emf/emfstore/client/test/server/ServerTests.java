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
package org.eclipse.emf.emfstore.client.test.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfstore.client.test.SetupHelper;
import org.eclipse.emf.emfstore.client.test.WorkspaceTest;
import org.eclipse.emf.emfstore.common.CommonUtil;
import org.eclipse.emf.emfstore.common.model.ModelFactory;
import org.eclipse.emf.emfstore.common.model.Project;
import org.eclipse.emf.emfstore.common.model.util.ModelUtil;
import org.eclipse.emf.emfstore.common.model.util.SerializationException;
import org.eclipse.emf.emfstore.internal.client.model.Configuration;
import org.eclipse.emf.emfstore.internal.client.model.ServerInfo;
import org.eclipse.emf.emfstore.internal.client.model.Usersession;
import org.eclipse.emf.emfstore.internal.client.model.WorkspaceProvider;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.ConnectionManager;
import org.eclipse.emf.emfstore.internal.client.model.connectionmanager.KeyStoreManager;
import org.eclipse.emf.emfstore.internal.client.model.impl.RemoteProject;
import org.eclipse.emf.emfstore.internal.client.model.impl.WorkspaceBase;
import org.eclipse.emf.emfstore.internal.client.model.util.EMFStoreCommand;
import org.eclipse.emf.emfstore.server.ServerConfiguration;
import org.eclipse.emf.emfstore.server.exceptions.EMFStoreException;
import org.eclipse.emf.emfstore.server.exceptions.InvalidInputException;
import org.eclipse.emf.emfstore.server.model.AuthenticationInformation;
import org.eclipse.emf.emfstore.server.model.ProjectId;
import org.eclipse.emf.emfstore.server.model.SessionId;
import org.eclipse.emf.emfstore.server.model.accesscontrol.ACOrgUnitId;
import org.eclipse.emf.emfstore.server.model.accesscontrol.AccesscontrolFactory;
import org.eclipse.emf.emfstore.server.model.versioning.ChangePackage;
import org.eclipse.emf.emfstore.server.model.versioning.HistoryQuery;
import org.eclipse.emf.emfstore.server.model.versioning.LogMessage;
import org.eclipse.emf.emfstore.server.model.versioning.PrimaryVersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.TagVersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.VersionSpec;
import org.eclipse.emf.emfstore.server.model.versioning.VersioningFactory;
import org.eclipse.emf.emfstore.server.model.versioning.util.HistoryQueryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Superclass for server tests.
 * 
 * @author wesendon
 */

public abstract class ServerTests extends WorkspaceTest {

	private static SessionId sessionId;
	private static ConnectionManager connectionManager;
	private int projectsOnServerBeforeTest;
	private static HashMap<Class<?>, Object> arguments;
	private static ServerInfo serverInfo;

	public static void setServerInfo(ServerInfo newServerInfo) {
		serverInfo = newServerInfo;
	}

	public static ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * @return the sessionId
	 */
	public static SessionId getSessionId() {
		return sessionId;
	}

	public static void setSessionId(SessionId newSessionId) {
		sessionId = newSessionId;
	}

	/**
	 * @return the connectionManager
	 */
	public static ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public static void setConnectionManager(ConnectionManager newConnectionManager) {
		connectionManager = newConnectionManager;
	}

	public RemoteProject getRemoteProject() throws EMFStoreException {
		return getProjectSpace().getRemoteProject();
	}

	public ProjectId getProjectId() {
		return getProjectSpace().getProjectId();
	}

	public PrimaryVersionSpec getProjectVersion() throws EMFStoreException {
		return getRemoteProject().getHeadVersion(false);
	}

	/**
	 * @return the projectsOnServerBeforeTest
	 */
	public int getProjectsOnServerBeforeTest() {
		return projectsOnServerBeforeTest;
	}

	@Override
	@After
	public void teardown() throws IOException, SerializationException, EMFStoreException {
		super.teardown();
		for (RemoteProject project : getServerInfo().getRemoteProjects()) {
			project.delete();
		}
		Assert.assertEquals(0, getServerInfo().getRemoteProjects().size());
	}

	/**
	 * Start server and gain sessionid.
	 * 
	 * @throws EMFStoreException in case of failure
	 * @throws IOException
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws EMFStoreException, IOException {
		ServerConfiguration.setTesting(true);
		CommonUtil.setTesting(true);

		SetupHelper.addUserFileToServer(false);
		SetupHelper.startSever();
		setConnectionManager(WorkspaceProvider.getInstance().getConnectionManager());
		setServerInfo(SetupHelper.getServerInfo());
		// login();
		initArguments();
	}

	/**
	 * @param serverInfo serverinfo
	 * @throws EMFStoreException in case of failure
	 */
	protected static void login() throws EMFStoreException {
		SessionId sessionId = login(getServerInfo(), "super", "super").getSessionId();
		WorkspaceProvider.getInstance().getAdminConnectionManager().initConnection(getServerInfo(), sessionId);
		setSessionId(sessionId);
	}

	/**
	 * @param serverInfo serverinfo
	 * @param username username
	 * @param password password
	 * @return sessionId
	 * @throws EMFStoreException in case of failure
	 */
	protected static AuthenticationInformation login(ServerInfo serverInfo, String username, String password)
		throws EMFStoreException {
		return getConnectionManager().logIn(username, KeyStoreManager.getInstance().encrypt(password, serverInfo),
			serverInfo, Configuration.getClientVersion());
	}

	protected static void initArguments() {
		arguments = new LinkedHashMap<Class<?>, Object>();
		arguments.put(boolean.class, false);
		arguments.put(String.class, new String());
		arguments.put(SessionId.class, ModelUtil.clone(getSessionId()));
		arguments.put(ProjectId.class, org.eclipse.emf.emfstore.server.model.ModelFactory.eINSTANCE.createProjectId());
		arguments.put(PrimaryVersionSpec.class, VersioningFactory.eINSTANCE.createPrimaryVersionSpec());
		arguments.put(VersionSpec.class, VersioningFactory.eINSTANCE.createPrimaryVersionSpec());
		arguments.put(TagVersionSpec.class, VersioningFactory.eINSTANCE.createTagVersionSpec());
		arguments.put(LogMessage.class, VersioningFactory.eINSTANCE.createLogMessage());
		arguments.put(Project.class, ModelFactory.eINSTANCE.createProject());
		arguments.put(ChangePackage.class, VersioningFactory.eINSTANCE.createChangePackage());
		arguments.put(HistoryQuery.class, VersioningFactory.eINSTANCE.createPathQuery());
		arguments.put(ChangePackage.class, VersioningFactory.eINSTANCE.createChangePackage());
		arguments.put(ACOrgUnitId.class, AccesscontrolFactory.eINSTANCE.createACOrgUnitId());
	}

	/**
	 * Shuts down server after testing.
	 * 
	 * @throws IOException
	 *             in case cleanup/teardown fails
	 * @throws EMFStoreException
	 */
	@AfterClass
	public static void tearDownAfterClass() throws IOException, EMFStoreException {
		SetupHelper.stopServer();
		SetupHelper.cleanupWorkspace();
		SetupHelper.cleanupServer();
		SetupHelper.removeServerTestProfile();
	}

	/**
	 * Adds a project to the server before test.
	 * 
	 * @throws EMFStoreException in case of failure
	 */
	@Before
	public void beforeTest() throws EMFStoreException {
		new EMFStoreCommand() {
			@Override
			protected void doRun() {
				try {
					WorkspaceBase currentWorkspace = (WorkspaceBase) WorkspaceProvider.getInstance().getWorkspace();
					serverInfo = SetupHelper.getServerInfo();
					Usersession session = serverInfo.login("super", "super");
					session.setSavePassword(true);

					currentWorkspace.getServers().add(serverInfo);
					currentWorkspace.getUsersessions().add(session);
					currentWorkspace.save();
					getProjectSpace().shareProject(session, null);
				} catch (EMFStoreException e) {
					Assert.fail();
				}
			}
		}.run(false);
		// setProjectInfo(getConnectionManager().createProject(getSessionId(), "initialProject", "TestProject",
		// SetupHelper.createLogMessage("super", "a logmessage"), getProject()));
		this.projectsOnServerBeforeTest = 1;
		// TODO: OTS enable assert
		// Assert.assertEquals(projectsOnServerBeforeTest, getServerInfo().getRemoteProjects(true).size());
	}

	/**
	 * Removes all projects from server after test.
	 * 
	 * @throws EMFStoreException in case of failure
	 */
	@After
	public void afterTest() throws EMFStoreException {

		new EMFStoreCommand() {

			@Override
			protected void doRun() {
				try {
					for (RemoteProject remoteProject : getServerInfo().getRemoteProjects()) {
						remoteProject.delete(true);
					}
				} catch (EMFStoreException e) {
				}

				SetupHelper.cleanupServer();
			}
		}.run(false);
	}

	/**
	 * Sets up user on server.
	 * 
	 * @param name name of the user (must be specified in users.properties)
	 * @param role of type RolesPackage.eINSTANCE.getWriterRole() or RolesPackage.eINSTANCE.getReaderRole() ....
	 * @throws EMFStoreException in case of failure
	 */

	public ACOrgUnitId setupUsers(String name, EClass role) throws EMFStoreException {
		try {
			ACOrgUnitId orgUnitId = SetupHelper.createUserOnServer(name);
			SetupHelper.setUsersRole(orgUnitId, role, getProjectId());
			return orgUnitId;
		} catch (InvalidInputException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets up usersession.
	 * 
	 * @param name of the user (must be specified in users.properties)
	 * @param password of the user (must be specified in users.properties)
	 * @return established usersession
	 */
	public Usersession setUpUsersession(String username, String password) {
		Usersession usersession = org.eclipse.emf.emfstore.internal.client.model.ModelFactory.eINSTANCE.createUsersession();
		usersession.setServerInfo(getServerInfo());
		usersession.setUsername(username);
		usersession.setPassword(password);
		return usersession;
	}

	/**
	 * Compares two projects.
	 * 
	 * @param expected expected
	 * @param compare to be compared
	 */
	public static void assertEqual(Project expected, Project compare) {
		if (!ModelUtil.areEqual(expected, compare)) {
			throw new AssertionError("Projects are not equal.");
		}
	}

	/**
	 * Creates a historyquery.
	 * 
	 * @param ver1 source
	 * @param ver2 target
	 * @return historyquery
	 */
	public static HistoryQuery createHistoryQuery(PrimaryVersionSpec ver1, PrimaryVersionSpec ver2) {
		return HistoryQueryBuilder.pathQuery(ver1, ver2, false, false);
	}

	/**
	 * Get a default Parameter.
	 * 
	 * @param clazz parameter type
	 * @param b if false, null is returned
	 * @return parameter
	 */
	protected static Object getParameter(Class<?> clazz, boolean b) {
		if (clazz.equals(boolean.class)) {
			return false;
		}
		return (b) ? arguments.get(clazz) : null;
	}
}