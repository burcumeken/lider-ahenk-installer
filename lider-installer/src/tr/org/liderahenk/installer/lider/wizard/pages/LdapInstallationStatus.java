package tr.org.liderahenk.installer.lider.wizard.pages;

import java.io.File;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.i18n.Messages;
import tr.org.pardus.mys.liderahenksetup.constants.InstallMethod;
import tr.org.pardus.mys.liderahenksetup.utils.PropertyReader;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;
import tr.org.pardus.mys.liderahenksetup.utils.setup.SetupUtils;

public class LdapInstallationStatus extends WizardPage implements ILdapPage {

	private LiderSetupConfig config;

	private ProgressBar progressBar;
	private Text txtLogConsole;

	boolean isInstallationFinished = false;

	public LdapInstallationStatus(LiderSetupConfig config) {
		super(LdapInstallationStatus.class.getName(), Messages.getString("LIDER_INSTALLATION"), null);
		setDescription("3.4 " + Messages.getString("LDAP_DB_INSTALLATION_METHOD") + " - "
				+ Messages.getString("LDAP_SETUP_METHOD_DESC"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = GUIHelper.createComposite(parent, 1);
		setControl(container);

		txtLogConsole = GUIHelper.createText(container, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(container, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);
		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		// progressGd.widthHint = 780;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {
		// Start LDAP installation here.
		// To prevent triggering installation again
		// (i.e. when clicked "next" after installation finished),
		// set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished) {

			final Display display = Display.getCurrent();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {

					printMessage("Initializing installation...");
					setProgressBar(10);

					printMessage("Setting up parameters for database password.");
					final String[] debconfValues = generateDebconfValues();
					setProgressBar(20);

					if (config.getLdapInstallMethod() == InstallMethod.APT_GET) {
						SetupUtils.installPackageNoninteractively(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(),
								config.getLdapPackageName(), null, debconfValues);
					} else if (config.getLdapInstallMethod() == InstallMethod.PROVIDED_DEB) {
						File deb = new File(config.getLdapDebFileName());
						SetupUtils.installPackageNonInteractively(config.getLdapIp(), config.getLdapAccessUsername(),
								config.getLdapAccessPasswd(), config.getLdapPort(), config.getLdapAccessKeyPath(), deb,
								debconfValues);
					} else {
						printMessage("Invalid installation method. Installation cancelled.");
					}
					// TODO handle failed installation attempts!

					setProgressBar(100);
					isInstallationFinished = true;
					setPageComplete(isInstallationFinished);
				}

				/**
				 * Prints log message to the log console widget
				 * 
				 * @param message
				 */
				private void printMessage(final String message) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
									? txtLogConsole.getText() + "\n" : "") + message);
						}
					});
				}

				/**
				 * Sets progress bar selection
				 * 
				 * @param selection
				 */
				private void setProgressBar(final int selection) {
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							progressBar.setSelection(selection);
						}
					});
				}
			};

			Thread thread = new Thread(runnable);
			thread.start();
		}

		return super.getNextPage();
	}

	/**
	 * Generates debconf values for database root password
	 * 
	 * @return
	 */
	public String[] generateDebconfValues() {
		String debconfPwd = PropertyReader.property("ldap.debconf.password1") + " " + config.getLdapRootPassword();
		String debconfPwdAgain = PropertyReader.property("ldap.debconf.password2") + " " + config.getLdapRootPassword();
		String debconfAdminPwd = PropertyReader.property("slapd slapd/internal/adminpw password") + " "
				+ config.getLdapRootPassword();
		String debconfGeneratedPwd = PropertyReader.property("slapd slapd/internal/generated_adminpw password") + " "
				+ config.getLdapRootPassword();
		return new String[] { debconfPwd, debconfPwdAgain, debconfAdminPwd, debconfGeneratedPwd };
	}

	@Override
	public IWizardPage getPreviousPage() {
		/**
		 * Do not allow to go back from this page.
		 */
		return null;
	}

}
