/*******************************************************************************
 * Copyright (c) 2008-2011 Chair for Applied Software Engineering,
 * Technische Universitaet Muenchen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * deser
 ******************************************************************************/
package org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.wizard;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.emfstore.internal.client.ui.Activator;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.CSVImportSource;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.ImportSource;
import org.eclipse.emf.emfstore.internal.client.ui.dialogs.admin.acimport.LdapImportSource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

/**
 * With this page the import source can be selected (via radio buttons).
 *
 * @author deser
 */
public class AcUserImportPageOne extends WizardPage {

	private ArrayList<ImportSource> list;

	/**
	 * Standard constructor.
	 */
	public AcUserImportPageOne() {
		super("Page One"); //$NON-NLS-1$
		setTitle(Messages.AcUserImportPageOne_ChooseImportSource);
		initSources();
	}

	private void initSources() {
		list = new ArrayList<ImportSource>();

		list.add(new CSVImportSource());
		list.add(new LdapImportSource());
	}

	/**
	 * @param parent
	 *            The parent Composite which gets filled here.
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// Set the specific help (performHelp() didn't work as expected).
		// See:
		// http://stackoverflow.com/questions/1012929/eclipse-contextual-help/1021718
		// http://dev.eclipse.org/newslists/news.eclipse.platform.ua/msg00359.html
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, Activator.PLUGIN_ID + ".help_import_wizard_page_one"); //$NON-NLS-1$

		final Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout(1, false));
		final Label label = new Label(composite, SWT.None);
		label.setText(Messages.AcUserImportPageOne_ChooseImportSource + "."); //$NON-NLS-1$

		final Button[] radios = new Button[list.size()];

		int i = 0;
		for (final ImportSource src : list) {
			radios[i] = new Button(composite, SWT.RADIO);
			radios[i].setText(src.getLabel());
			radios[i].setBounds(
				convertHorizontalDLUsToPixels(30),
				convertVerticalDLUsToPixels(5),
				convertHorizontalDLUsToPixels(200),
				convertVerticalDLUsToPixels(30));
			radios[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// do this only if item gets checked, not unchecked!
					final boolean isCurrentrlySelected = ((Button) e.getSource()).getSelection();
					if (isCurrentrlySelected) {
						final AcUserImportPageTwo nextPage = (AcUserImportPageTwo) getNextPage();
						final AcUserImportWizard wizard = (AcUserImportWizard) getWizard();
						wizard.getController().setImportSource(src);
						if (src.init(getShell())) {
							final ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(getShell());
							progressMonitorDialog.open();
							progressMonitorDialog.getProgressMonitor().beginTask(Messages.AcUserImportPageOne_Loading,
								IProgressMonitor.UNKNOWN);
							nextPage.init(src);
							progressMonitorDialog.close();
							setPageComplete(true);
							getContainer().showPage(getNextPage());
						}
					}
				}
			});
			i++;
		}

		setControl(composite);
		setPageComplete(false);
	}

}
