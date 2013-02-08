package org.eclipse.emf.emfstore.server.model.api.versionspec;

import org.eclipse.emf.emfstore.internal.server.model.versioning.VersionsFactory;

public interface IVersionSpec {

	IVersionFactory FACTORY = VersionsFactory.INSTANCE;

	String HEAD = "HEAD";

	String BASE = "BASE";

	String BRANCH_DEFAULT_NAME = "trunk";

	// magic global variable
	String GLOBAL = "___GLOBAL___";

	String getBranch();

}