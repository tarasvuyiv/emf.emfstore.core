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
 * Philip Langer - Bug 445193: Allow configuration of the numbers/types of changes being applied by model mutator
 * Edgar Mueller - Bug 447483: ModelMutator should support cross-resource model generation and mutation, Refactorings
 ******************************************************************************/
package org.eclipse.emf.emfstore.modelmutator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.AddObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.AttributeChangeMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.DeleteObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.FeatureMapKeyMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.FeatureMapValueMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.MoveObjectMutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.Mutation;
import org.eclipse.emf.emfstore.internal.modelmutator.mutation.ReferenceChangeMutation;

import com.google.common.base.Predicate;

/**
 * Abstract implementation of an model mutator.
 *
 * @author Julian Sommerfeldt
 * @since 2.0
 *
 */
public abstract class ESAbstractModelMutator {

	private ESModelMutatorConfiguration modelMutatorConfig;

	private final Map<EReference, List<EClass>> referencesToClasses = new LinkedHashMap<EReference, List<EClass>>();

	// randomly eobjects are saved into this map and are used later instead of creating new ones
	private final Map<EClass, List<EObject>> freeObjects = new LinkedHashMap<EClass, List<EObject>>();

	private final Map<EClass, List<EObject>> allObjects = new LinkedHashMap<EClass, List<EObject>>();

	private ESModelMutatorUtil util;

	private int currentObjectCount;

	private int targetObjectCount;

	private int currentWidth = 1;

	private int currentDepth = 1;

	private List<Mutation> defaultMutationPrototypes;

	/**
	 * Default constructor.
	 *
	 * Leaves the config uninitialized.
	 */
	public ESAbstractModelMutator() {
		modelMutatorConfig = null;
		util = null;
		targetObjectCount = -1;
	}

	/**
	 * Constructor.
	 *
	 * @param config
	 *            The {@link ESModelMutatorConfiguration} to use for mutation.
	 */
	public ESAbstractModelMutator(ESModelMutatorConfiguration config) {
		modelMutatorConfig = config;
		util = new ESModelMutatorUtil(config);
		targetObjectCount = config.getMinObjectsCount();
	}

	/**
	 * Called before generation and mutation.
	 */
	public abstract void preMutate();

	/**
	 * Called after generation and mutation.
	 */
	public abstract void postMutate();

	/**
	 * Initial generation.
	 */
	public void generate() {
		preMutate();
		// generate till there are enough objects
		while (currentObjectCount < targetObjectCount) {
			createChildrenForRoot();
			currentWidth++;
			if (randomBoolean() && randomBoolean() && randomBoolean()) {
				currentDepth++;
			}
		}

		postMutate();
	}

	/**
	 * Mutation after an initial generation.
	 *
	 * @param ignoredFeatures
	 *            a set of features to be ignored while mutating
	 */
	public void mutate(Set<EStructuralFeature> ignoredFeatures) {
		if (modelMutatorConfig.getMutationCount() == -1) {
			performFullMutation(ignoredFeatures);
		} else {
			performConfiguredNumberOfMutations();
		}
	}

	/**
	 * Performs mutation until the given {@link Predicate} is met.
	 * The predicate input will be the the root object obtained by
	 * calling {@code getConfig()}.
	 *
	 * @param predicate
	 *            the predicate that must be fulfilled in order to stop mutation
	 */
	public void mutateUntil(final Predicate<EObject> predicate) {
		mutate(Collections.<EStructuralFeature> emptySet());
		while (!predicate.apply(getRootEObject())) {
			mutate(Collections.<EStructuralFeature> emptySet());
		}
	}

	private void performFullMutation(Set<EStructuralFeature> ignoredFeatures) {
		final EObject rootEObject = getRootEObject();
		deleteEObjects(rootEObject);

		currentObjectCount = ESModelMutatorUtil.getAllObjectsCount(rootEObject);

		generate();

		changeCrossReferences();

		mutateAttributes(ignoredFeatures);
	}

	/**
	 * Returns the root {@link EObject} of the {@link ESModelMutatorConfiguration}.
	 * Clients may override.
	 *
	 * @return the root {@link EObject} of the {@link ESModelMutatorConfiguration}
	 */
	protected EObject getRootEObject() {
		return modelMutatorConfig.getRootEObject();
	}

	private void performConfiguredNumberOfMutations() {
		final List<Mutation> mutations = getDefaultMutationPrototypes();

		int i = 0;
		while (i < modelMutatorConfig.getMutationCount()) {
			final int rndIdx = modelMutatorConfig.getRandom().nextInt(mutations.size());
			final Mutation nextMutation = mutations.get(rndIdx);
			final Mutation mutationToRun = nextMutation.clone();
			try {
				mutationToRun.apply();
				i++;
			} catch (final ESMutationException ex) {
				continue;
			}
		}
	}

	private List<Mutation> getDefaultMutationPrototypes() {
		if (defaultMutationPrototypes == null) {
			defaultMutationPrototypes = createDefaultMutationPrototypes();
		}
		return defaultMutationPrototypes;
	}

	private List<Mutation> createDefaultMutationPrototypes() {
		final List<Mutation> defaultMutationPrototypes = new ArrayList<Mutation>();
		defaultMutationPrototypes.add(new AddObjectMutation(util));
		defaultMutationPrototypes.add(new DeleteObjectMutation(util));
		defaultMutationPrototypes.add(new MoveObjectMutation(util));
		defaultMutationPrototypes.add(new AttributeChangeMutation(util));
		defaultMutationPrototypes.add(new ReferenceChangeMutation(util));
		defaultMutationPrototypes.add(new FeatureMapKeyMutation(util));
		defaultMutationPrototypes.add(new FeatureMapValueMutation(util));
		return defaultMutationPrototypes;
	}

	/**
	 * Create the children for the root object.
	 */
	public void createChildrenForRoot() {

		final EObject rootEObject = getRootEObject();

		// if the root depth should not be generated
		if (modelMutatorConfig.isDoNotGenerateRoot()) {
			boolean didCreateChild = false;
			// create children for each of the children of the root
			final List<EObject> eContents = new ArrayList<EObject>(rootEObject.eContents());
			try {
				for (final EObject obj : eContents) {
					didCreateChild |= createChildren(obj, 1);
				}
			} catch (final IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			if (!didCreateChild) {
				createChildren(rootEObject, 0);
			}
		} else {
			// if the root depth should be generated, create children for the root
			createChildren(rootEObject, 0);
		}
	}

	/**
	 * Create recursively direct and indirect children for a given {@link EObject} and its children.
	 *
	 * @param root The {@link EObject} for which children should be generated.
	 * @param depth The depth of the EObject in the total tree.
	 *
	 * @return {@code true}, if children have been created, {@code false} otherwise
	 */
	public boolean createChildren(EObject root, int depth) {
		if (currentObjectCount >= targetObjectCount) {
			return false;
		}

		// check if we reached the maximal depth
		if (depth >= currentDepth) {
			return false;
		}

		// create children for the current root object
		final List<EObject> children = createChildren(root);

		// create children for the children (one step deeper)
		for (final EObject obj : children) {
			createChildren(obj, depth + 1);
		}

		return !children.isEmpty();
	}

	/**
	 * Creates/deletes direct children for the given {@link EObject}.
	 *
	 * @param root The {@link EObject} for which children should be created.
	 * @return A list of the newly generated children
	 */
	public List<EObject> createChildren(EObject root) {
		final List<EObject> children = new ArrayList<EObject>();
		final Collection<EStructuralFeature> ignore = modelMutatorConfig.geteStructuralFeaturesToIgnore();

		// iterate over all references
		for (final EReference reference : root.eClass().getEAllContainments()) {

			// check if the reference is valid: not to be ignored AND can be set/added
			if (ignore.contains(reference) || !util.isValid(reference, root)) {
				continue;
			}

			// add remaining children (specified through modelMutatorConfig)
			final int init = currentWidth / 2 - root.eContents().size();

			// add children to fulfill width constraint
			for (int i = init; i > 0; i--) {
				final EClass eClass = getValidEClass(reference);
				if (eClass != null) {
					final EObject obj = getEObject(eClass, new LinkedHashSet<EStructuralFeature>(ignore));

					// randomly first changeCrossReferences
					if (randomBoolean()) {
						changeCrossReferences(obj);
					}

					// add directly to parent or add to free objects
					// only add if it is many or if it is only one
					if (reference.isMany() || i == 1) {
						addToParent(root, obj, reference);
						children.add(obj);
						currentObjectCount++;
						currentObjectCount += obj.eContents().size();
					} else {
						addToEClassToObjectsMap(obj, freeObjects);
					}

					addToEClassToObjectsMap(obj, allObjects);

					if (currentObjectCount >= targetObjectCount) {
						return children;
					}
				}
			}
		}

		return children;
	}

	private boolean randomBoolean() {
		return modelMutatorConfig.getRandom().nextBoolean();
	}

	/**
	 * Randomly deletes direct and indirect children of the given root {@link EObject}.
	 *
	 * @param root The {@link EObject} from which children should be deleted.
	 */
	public void deleteEObjects(EObject root) {
		final List<EObject> toDelete = new ArrayList<EObject>();
		final Random random = modelMutatorConfig.getRandom();
		final int maxDeleteCount = modelMutatorConfig.getMaxDeleteCount();

		// randomly select objects to delete
		int deleted = 0;
		for (final TreeIterator<EObject> it = root.eAllContents(); it.hasNext();) {
			final EObject obj = it.next();
			if (deleted < maxDeleteCount && randomBoolean()) {
				toDelete.add(obj);
				deleted++;
				addToEClassToObjectsMap(obj, freeObjects);
			}
			addToEClassToObjectsMap(obj, allObjects);
		}

		// check for delete modes to use
		final List<Integer> deleteModes = new ArrayList<Integer>();
		deleteModes.add(ESModelMutatorUtil.DELETE_DELETE_COMMAND);
		deleteModes.add(ESModelMutatorUtil.DELETE_CUT_CONTAINMENT);
		if (modelMutatorConfig.isUseEcoreUtilDelete()) {
			deleteModes.add(ESModelMutatorUtil.DELETE_ECORE);
		}

		// delete selected objects
		final int size = deleteModes.size();
		for (final EObject obj : new ArrayList<EObject>(toDelete)) {
			util.removeFullPerCommand(obj, deleteModes.get(random.nextInt(size)));
		}
	}

	private void addToEClassToObjectsMap(EObject obj, Map<EClass, List<EObject>> map) {
		List<EObject> objects = map.get(obj.eClass());
		if (objects == null) {
			objects = new ArrayList<EObject>();
			map.put(obj.eClass(), objects);
		}
		objects.add(obj);
	}

	/**
	 * Get a {@link EClass}, which is valid for the given {@link EReference}.
	 *
	 * @param reference The {@link EReference} to search a {@link EClass} for.
	 * @return A valid {@link EClass} for the given {@link EReference} or <code>null</code> if there is none.
	 */
	protected EClass getValidEClass(EReference reference) {
		// get already saved classes list
		List<EClass> classes = referencesToClasses.get(reference);

		// if there is none, create it
		if (classes == null) {

			// get all classes of the modelpackage
			classes = util.getAllEContainments(reference);

			// check if they should be ignored
			for (final EClass eClass : modelMutatorConfig.geteClassesToIgnore()) {
				classes.remove(eClass);
				classes.removeAll(util.getAllSubEClasses(eClass));
			}

			// remove classes which cannot have an instance
			for (final EClass eClass : new ArrayList<EClass>(classes)) {
				if (!ESModelMutatorUtil.canHaveInstance(eClass)) {
					classes.remove(eClass);
				}
			}

			// return null if there is no valid class
			if (classes.isEmpty()) {
				return null;
			}

			// save for future use
			referencesToClasses.put(reference, classes);
		}

		// randomly select one class
		final int index = modelMutatorConfig.getRandom().nextInt(classes.size());
		return classes.get(index);
	}

	/**
	 * Creates a new {@link EObject} and sets its attributes.
	 *
	 * @param eClass The {@link EClass} of the new {@link EObject}.
	 * @param ignoredFeatures
	 *            a set of features to be ignored while mutating
	 * @return The newly created and modified {@link EObject}.
	 */
	protected EObject getEObject(EClass eClass, Set<EStructuralFeature> ignoredFeatures) {
		final Random random = modelMutatorConfig.getRandom();
		EObject newObject = null;

		// try to get an already existing object if there is one
		final List<EObject> objects = freeObjects.get(eClass);
		if (objects != null && objects.size() != 0 && randomBoolean()) {
			newObject = objects.remove(random.nextInt(objects.size()));
		} else {
			newObject = EcoreUtil.create(eClass);
		}

		util.setEObjectAttributes(newObject, ignoredFeatures);
		return newObject;
	}

	/**
	 * Adds an {@link EObject} to the given parent.
	 *
	 * @param parent The {@link EObject} where to add the newObject
	 * @param newObject The new {@link EObject} to add to the parent.
	 * @param reference The {@link EReference} where to add the newObject.
	 */
	protected void addToParent(EObject parent, EObject newObject, EReference reference) {
		if (reference.isMany()) {
			util.addPerCommand(parent, reference, newObject, randomBoolean() ? 0 : null);
		} else {
			util.setPerCommand(parent, reference, newObject);
		}
	}

	/**
	 * Randomly mutates all attributes.
	 *
	 * @param ignoredFeatures
	 *            a set of features to be ignored while mutating
	 */
	public void mutateAttributes(Set<EStructuralFeature> ignoredFeatures) {
		final EObject rootEObject = getRootEObject();
		for (final TreeIterator<EObject> it = rootEObject.eAllContents(); it.hasNext();) {
			final EObject obj = it.next();
			util.setEObjectAttributes(obj, ignoredFeatures);
		}
	}

	/**
	 * Changes cross references of all {@link EObject}s in the model.
	 */
	public void changeCrossReferences() {
		for (final Entry<EClass, List<EObject>> entry : allObjects.entrySet()) {
			for (final EObject obj : entry.getValue()) {
				changeCrossReferences(obj);
			}
		}
	}

	/**
	 * Changes CrossReferences of an specific {@link EObject}.
	 *
	 * @param obj The {@link EObject} whose cross references should be changed
	 */
	public void changeCrossReferences(EObject obj) {
		for (final EReference reference : util.getValidCrossReferences(obj)) {
			for (final EClass referenceClass : util.getReferenceClasses(reference, allObjects.keySet())) {
				util.setReference(obj, referenceClass, reference, allObjects);
			}
		}
	}

	/**
	 * @return the modelMutatorConfig
	 */
	public ESModelMutatorConfiguration getConfig() {
		return modelMutatorConfig;
	}

	/**
	 * Sets the config to be used by the mutator.
	 *
	 * @param config
	 *            the config to be used by the mutator
	 */
	public void setConfig(ESModelMutatorConfiguration config) {
		modelMutatorConfig = config;
		util = new ESModelMutatorUtil(config);
		targetObjectCount = config.getMinObjectsCount();
	}

}
