/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Zardosht Hodaie, gurcankarakoc, deser - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.views.emfstorebrowser.dialogs.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfstore.internal.client.model.AdminBroker;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.EMFStoreMessageDialog;
import org.eclipse.emf.emfstore.internal.server.exceptions.AccessControlException;
import org.eclipse.emf.emfstore.internal.server.model.ProjectInfo;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACGroup;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACOrgUnit;
import org.eclipse.emf.emfstore.internal.server.model.accesscontrol.ACUser;
import org.eclipse.emf.emfstore.server.exceptions.ESException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;

/**
 * This class sets the contents of tabs on the left side of OrgUnitManagmentGUI.
 * 
 * @author Hodaie, gurcankarakoc, deser
 */
public abstract class TabContent {

	/**
	 * @author deser
	 */
	private final class SimpleAlphabeticSorter extends ViewerSorter {

		private int dir = SWT.UP;

		public SimpleAlphabeticSorter(int dir) {
			super();
			this.dir = dir;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			int returnValue = 0;
			if (e1 instanceof ProjectInfo) {
				final ProjectInfo pi1 = (ProjectInfo) e1;
				final ProjectInfo pi2 = (ProjectInfo) e2;
				returnValue = pi1.getName().compareTo(pi2.getName());
			}
			if (e1 instanceof ACUser) {
				final ACUser u1 = (ACUser) e1;
				final ACUser u2 = (ACUser) e2;
				returnValue = u1.getName().compareTo(u2.getName());
			}

			if (e1 instanceof ACGroup) {
				final ACGroup g1 = (ACGroup) e1;
				final ACGroup g2 = (ACGroup) e2;
				returnValue = g1.getName().compareTo(g2.getName());
			}
			if (dir == SWT.DOWN) {
				returnValue = returnValue * -1;
			}
			return returnValue;
		}
	}

	private TableViewer tableViewer;
	private String tabName;

	/**
	 * The type of the current tab.
	 */
	private TabContent tab;

	/**
	 * AdminBroker is needed to communicate with server.
	 */
	private AdminBroker adminBroker;

	/**
	 * used to set input to properties form and update its table viewer upon. deletion of OrgUnits.
	 */
	private PropertiesForm form;

	/**
	 * Constructor.
	 * 
	 * @param tabName tab name
	 * @param adminBroker AdminBroker
	 * @param frm ProperitesForm
	 */
	public TabContent(String tabName, AdminBroker adminBroker, PropertiesForm frm) {
		this.tabName = tabName;
		this.adminBroker = adminBroker;
		form = frm;
	}

	/**
	 * Creates contents of each tab.
	 * 
	 * @param tabFolder parent
	 * @return contents composite
	 */
	protected Composite createContents(TabFolder tabFolder) {
		final Composite tabContent = new Composite(tabFolder, SWT.NONE);
		tabContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tabContent.setLayout(new GridLayout(1, false));

		// add actions
		final List<Action> actions = initActions();
		if (actions.size() > 0) {
			final ToolBar toolBar = new ToolBar(tabContent, SWT.FLAT | SWT.RIGHT);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(toolBar);
			final ToolBarManager toolBarManager = new ToolBarManager(toolBar);

			for (final Action action : actions) {
				toolBarManager.add(action);
			}
			toolBarManager.update(true);
		}

		// start list
		initList(tabContent);

		return tabContent;
	}

	/**
	 * Use to register actions for the tab.
	 * 
	 * @return list of actions or empty list
	 */
	protected abstract List<Action> initActions();

	/**
	 * @param parent is the Composite.
	 */
	protected void initList(Composite parent) {

		final Composite container = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);

		final TableColumnLayout tableColumnLayout = new TableColumnLayout();
		container.setLayout(tableColumnLayout);

		// tableColumnLayout.setColumnData(singleColumn, new ColumnWeightData(100));

		tableViewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.FULL_SELECTION);

		// GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.horizontalSpan = 2;
		// tableViewer.getTable().setLayoutData(gridData);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(false);
		table.setLinesVisible(true);

		final TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);

		tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(100));
		column.getColumn().setResizable(false);
		column.getColumn().setMoveable(false);

		// The getContentProvider-method fetches the specific ContentProvider.
		// Therefore this method has to be overridden in children of this class!
		tableViewer.setContentProvider(getContentProvider());

		// The same with the LabelProvider.
		tableViewer.setLabelProvider(getLabelProvider());
		tableViewer.setInput(new Object());

		tableViewer.getTable().setSortColumn(tableViewer.getTable().getColumn(0));
		tableViewer.getTable().setSortDirection(SWT.UP);

		final Listener sortListener = new Listener() {
			public void handleEvent(Event e) {
				int dir = tableViewer.getTable().getSortDirection();
				dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.setSorter(new SimpleAlphabeticSorter(dir));
			}

		};

		// connect the listener to the (first) column
		column.getColumn().addListener(SWT.Selection, sortListener);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				final List<EObject> selectedItems = getSelectedItem(event);
				if (selectedItems.size() > 0) {
					form.setInput(getSelectedItem(event).get(0));
				} else {
					form.setInput(null);
				}
			}
		});

		addDragNDropSupport();

	}

	/**
	 * @return The LabelProvider for the concrete TabContent.
	 */
	public abstract ITableLabelProvider getLabelProvider();

	/**
	 * @return The ContentProvider for the concrete TabContent.
	 */
	public abstract IStructuredContentProvider getContentProvider();

	/**
	 * This is used during first creation of tab folder to set initial input to properties form.
	 */
	public void viewStarted() {
		if (form.getCurrentInput() == null) {
			form.setInput((EObject) tableViewer.getElementAt(0));
		}

	}

	private void addDragNDropSupport() {
		int ops = DND.DROP_COPY;
		final Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };

		final DragSourceListener dragListener = new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {
				PropertiesForm.setDragNDropObjects(Collections.<ACOrgUnit> emptyList());
				PropertiesForm.setDragSource(StringUtils.EMPTY);
			}

			public void dragStart(DragSourceEvent event) {
				PropertiesForm.setDragSource(getName());
			}

			public void dragSetData(DragSourceEvent event) {
				final List<EObject> selectedItems = getSelectedItem(null);
				if (selectedItems.size() > 0) {
					final EObject selectedItem = selectedItems.get(0);
					if (selectedItem instanceof ACOrgUnit) {
						final List<ACOrgUnit> orgUnits = new ArrayList<ACOrgUnit>();
						for (final EObject item : selectedItems) {
							orgUnits.add(ACOrgUnit.class.cast(item));
						}
						PropertiesForm.setDragNDropObjects(orgUnits);
					}
				}
			}
		};
		tableViewer.addDragSupport(ops, transfers, dragListener);

		ops = DND.DROP_MOVE;
		final DropTargetListener dropListener = new DropTargetListener() {
			public void drop(DropTargetEvent event) {
				final List<ACOrgUnit> orgUnits = PropertiesForm.getDragNDropObjects();
				for (final ACOrgUnit orgUnit : orgUnits) {
					doDrop(orgUnit);
				}

				PropertiesForm.setDragNDropObjects(Collections.<ACOrgUnit> emptyList());
			}

			public void dragEnter(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {
			}

		};
		tableViewer.addDropSupport(ops, transfers, dropListener);

	}

	private void doDrop(ACOrgUnit orgUnit) {
		final EObject currentInput = form.getCurrentInput();
		if (currentInput == null) {
			return;
		}
		try {
			if (currentInput instanceof ProjectInfo) {
				final ProjectInfo projectInfo = (ProjectInfo) currentInput;

				adminBroker.removeParticipant(projectInfo.getProjectId(), orgUnit.getId());

			} else if (currentInput instanceof ACGroup) {
				final ACGroup group = (ACGroup) currentInput;
				adminBroker.removeMember(group.getId(), orgUnit.getId());

			} else if (currentInput instanceof ACUser) {
				final ACUser user = (ACUser) currentInput;
				adminBroker.removeGroup(user.getId(), ((ACGroup) orgUnit).getId());
			}
		} catch (final AccessControlException ex) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.TabContent_Insufficient_Access_Rights,
				Messages.TabContent_Not_Allowed_To_Remove_OrgUnit);
		} catch (final ESException ex) {
			EMFStoreMessageDialog.showExceptionDialog(ex);
		}
	}

	/**
	 * @return name of this tab
	 */
	private String getName() {
		return tabName;
	}

	/**
	 * This is called from user and group properties composites in order to update TableViewer, For example when name of
	 * an OrgUnit is changed.
	 * 
	 * @return tableViewer
	 */
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * returns selected item in ListViewer.
	 */
	private List<EObject> getSelectedItem(DoubleClickEvent event) {
		final List<EObject> selectedObjects = new ArrayList<EObject>();
		ISelection selection;
		if (event != null) {
			selection = event.getSelection();
		} else {
			selection = tableViewer.getSelection();
		}

		if (selection != null) {
			final IStructuredSelection structuredSelection = IStructuredSelection.class.cast(selection);
			for (final Object eObject : structuredSelection.toList()) {
				selectedObjects.add(EObject.class.cast(eObject));
			}
		}

		return selectedObjects;
	}

	/**
	 * @return the name of the tab.
	 */
	public String getTabName() {
		return tabName;
	}

	/**
	 * @param tabName set the name of current tab.
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	/**
	 * @return get the type of the current tab.
	 */
	public TabContent getTab() {
		return tab;
	}

	/**
	 * @param tab set the type of current tab.
	 */
	public void setTab(TabContent tab) {
		this.tab = tab;
	}

	/**
	 * @return get the AdminBroker.
	 */
	public AdminBroker getAdminBroker() {
		return adminBroker;
	}

	/**
	 * @param adminBroker set the AdminBroker.
	 */
	public void setAdminBroker(AdminBroker adminBroker) {
		this.adminBroker = adminBroker;
	}

	/**
	 * @return get the PropertiesForm.
	 */
	public PropertiesForm getForm() {
		return form;
	}

	/**
	 * @param form set the PropertiesForm.
	 */
	public void setForm(PropertiesForm form) {
		this.form = form;
	}

	/**
	 * @param tableViewer The tableViewer to set.
	 */
	public void setTableViewer(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

}
