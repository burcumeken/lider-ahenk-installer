package tr.org.liderahenk.installer.lider.wizard;

import java.util.LinkedList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.wizard.pages.KarafAccessPage;
import tr.org.liderahenk.installer.lider.wizard.pages.KarafConfirmPage;
import tr.org.liderahenk.installer.lider.wizard.pages.KarafSetupMethodPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LdapAccessPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LdapConfirmPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LdapSetupMethodPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LiderComponentSelectionPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LiderDistributionSelectionPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LiderLocationOfComponentsPage;
import tr.org.liderahenk.installer.lider.wizard.pages.MariaDBConfirmPage;
import tr.org.liderahenk.installer.lider.wizard.pages.MariaDBInstallationStatus;
import tr.org.liderahenk.installer.lider.wizard.pages.MariaDBSetupMethodPage;
import tr.org.liderahenk.installer.lider.wizard.pages.MariaDbAccessPage;

public class LiderSetupWizard extends Wizard {

	/**
	 * The instance which holds all the configuration variables throughout the
	 * setup process. It is shared by all wizard pages.
	 */
	private LiderSetupConfig config = new LiderSetupConfig();
	
	/**
	 * This wizard's list of pages (element type: <code>IWizardPage</code>).
	 */
	private LinkedList<IWizardPage> pages = new LinkedList<IWizardPage>();
	
	/**
	 * Setup wizard pages.
	 * 
	 * Other pages will be added dynamically according to user action!
	 */
	LiderComponentSelectionPage compSelect = new LiderComponentSelectionPage(config);
	LiderLocationOfComponentsPage locatOfComps = new LiderLocationOfComponentsPage(config);
	// TODO do we need that? If not delete
	LiderDistributionSelectionPage fourthPage = new LiderDistributionSelectionPage(config); //TODO DELETE
	
	MariaDbAccessPage mariaAccess = new MariaDbAccessPage(config); 
	MariaDBSetupMethodPage mariaSetup = new MariaDBSetupMethodPage(config);
	MariaDBConfirmPage mariaConfirm = new MariaDBConfirmPage(config);
	MariaDBInstallationStatus mariaStatus = new MariaDBInstallationStatus(config);
	
	LdapAccessPage ldapAccess = new LdapAccessPage(config);
	LdapSetupMethodPage ldapSetup = new LdapSetupMethodPage(config);
	LdapConfirmPage ldapConfirm = new LdapConfirmPage(config);
	
	KarafAccessPage karafAccess = new KarafAccessPage(config);
	KarafSetupMethodPage karafSetup = new KarafSetupMethodPage(config);
	KarafConfirmPage karafConfirm = new KarafConfirmPage(config);
	@Override
	public void addPages() {
		// Add first page as default, so the wizard can show it on startup
		addPage(compSelect);
		addPage(locatOfComps);
		
		addPage(mariaAccess);
		addPage(mariaSetup);
		addPage(mariaConfirm);
		addPage(mariaStatus);
		
		addPage(ldapAccess);
		addPage(ldapSetup);
		addPage(ldapConfirm);
		
		addPage(karafAccess);
		addPage(karafSetup);
		addPage(karafConfirm);

		// set this to true to override needsPreviousAndNextButtons() method
		setForcePreviousAndNextButtons(true);
	}

	/**
	 * Adds a new page to this wizard. The page is inserted at the end of the
	 * page list.
	 * 
	 * @param page
	 *            the new page
	 */
	public void addPage(IWizardPage page) {
		pages.add(page);
		page.setWizard(this);
	}

	/**
	 * Inserts a new page to this wizard at the specified position.
	 * 
	 * @param page
	 *            the new page
	 */
	public void addPage(IWizardPage page, int index) {
		pages.add(index, page);
		page.setWizard(this);
	}
	
	public LinkedList<IWizardPage> getPagesList() {
		return pages;
	}
	
    /**
     * The <code>Wizard</code> implementation of this <code>IWizard</code>
     * method creates all the pages controls using
     * <code>IDialogPage.createControl</code>. Subclasses should reimplement
     * this method if they want to delay creating one or more of the pages
     * lazily. The framework ensures that the contents of a page will be created
     * before attempting to show it.
     */
    @Override
	public void createPageControls(Composite pageContainer) {
        // the default behavior is to create all the pages controls
        for (int i = 0; i < pages.size(); i++) {
            IWizardPage page = pages.get(i);
            page.createControl(pageContainer);
			// page is responsible for ensuring the created control is
			// accessible
            // via getControl.
			Assert.isNotNull(
					page.getControl(),
					"getControl() of wizard page returns null. Did you call setControl() in your wizard page?"); //$NON-NLS-1$
        }
    }
    
    /**
     * The <code>Wizard</code> implementation of this <code>IWizard</code>
     * method disposes all the pages controls using
     * <code>DialogPage.dispose</code>. Subclasses should extend this method
     * if the wizard instance maintains addition SWT resource that need to be
     * disposed.
     */
    @Override
	public void dispose() {
    	super.dispose();
        // notify pages
        for (int i = 0; i < pages.size(); i++) {
			try {
	            pages.get(i).dispose();
			} catch (Exception e) {
				Status status = new Status(IStatus.ERROR, Policy.JFACE, IStatus.ERROR, e.getMessage(), e);
				Policy.getLog().log(status);
			}
        }
    }
    
    /*
     * (non-Javadoc) Method declared on IWizard. The default behavior is to
     * return the page that was added to this wizard after the given page.
     */
    @Override
	public IWizardPage getNextPage(IWizardPage page) {
        int index = pages.indexOf(page);
        if (index == pages.size() - 1 || index == -1) {
			// last page or page not found
            return null;
		}
        return pages.get(index + 1);
    }
    
    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    @Override
	public IWizardPage getPage(String name) {
        for (int i = 0; i < pages.size(); i++) {
            IWizardPage page = pages.get(i);
            String pageName = page.getName();
            if (pageName.equals(name)) {
				return page;
			}
        }
        return null;
    }
    
    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    @Override
	public int getPageCount() {
        return pages.size();
    }

    /*
     * (non-Javadoc) Method declared on IWizard.
     */
    @Override
	public IWizardPage[] getPages() {
        return pages.toArray(new IWizardPage[pages.size()]);
    }

    /*
     * (non-Javadoc) Method declared on IWizard. The default behavior is to
     * return the page that was added to this wizard before the given page.
     */
    @Override
	public IWizardPage getPreviousPage(IWizardPage page) {
        int index = pages.indexOf(page);
        if (index == 0 || index == -1) {
			// first page or page not found
            return null;
		} 
		return pages.get(index - 1);
    }
    
    /*
     * (non-Javadoc) Method declared on IWizard. By default this is the first
     * page inserted into the wizard.
     */
    @Override
	public IWizardPage getStartingPage() {
        if (pages.size() == 0) {
			return null;
		}
        return pages.get(0);
    }
    
	@Override
	public boolean performFinish() {
		// TODO
		return false;
	}

	@Override
	public boolean canFinish() {
		
		// If current page is the last page of this wizard
		// then enable finish button.
		if (getContainer().getCurrentPage().getName()
				.equals(this.getPagesList().getLast().getName())) {
			return true;
		}
		
		return false;
	}

}