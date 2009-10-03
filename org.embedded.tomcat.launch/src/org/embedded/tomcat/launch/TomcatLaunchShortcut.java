package org.embedded.tomcat.launch;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class TomcatLaunchShortcut implements ILaunchShortcut  {

	//private static final String JDK16_CONTAINER_ID = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jdk1.6";
	/**
	 * implements {@link ILaunchShortcut}
	 */
	public void launch(ISelection selection, String mode) {
		if (selection != null && selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IProject targetProject = null;
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IProject) {
				targetProject = (IProject) obj;
			} else if (obj instanceof IResource) {
				targetProject = ((IResource) obj).getProject();
			} else if (obj instanceof IAdaptable) {
				targetProject = (IProject) ((IAdaptable) obj).getAdapter(IProject.class);
			}

			if (targetProject != null) {
				launchApplication(targetProject, mode);
			}
		}
	}

	/**
	 * implements {@link ILaunchShortcut}
	 */
	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IProject targetProject = ((IFileEditorInput) input).getFile().getProject();
			launchApplication(targetProject, mode);
		}
	}

	/**
	 * build and launch for air application.
	 * @param targetProject
	 */
	private void launchApplication(IProject targetProject, String mode) {
		try {
			ILaunchConfiguration config = getLaunchConfiguration(targetProject, mode);
			DebugUITools.launch(config, mode);
		} catch (Exception ex) {
			Activator.logException(ex);
		}
	}

	/**
	 * create and save for air launch configuration.
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private ILaunchConfiguration getLaunchConfiguration(IProject project, String main) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] configs = manager.getLaunchConfigurations();

		for (int i = 0; i < configs.length; i++) {
			String value = configs[i].getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			if (value.equals(project.getName())) {
				return configs[i];
			}
		}

		ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, manager
				.generateUniqueLaunchConfigurationNameFrom(project.getName()));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.embedded.tomcat.starter.TomcatStarter");
		//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, JDK16_CONTAINER_ID);
		//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Xmx512m -DtomcatConfig="+findConfigFile(project).getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, findConfigFile(project).getName());
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);

		return wc.doSave();
	}

	private File findConfigFile(IProject project) {
		String[] prefix = {"xml"};
		File result = null;
		File file = null;
		Iterator<File> iterator = FileUtils.listFiles(new File(project.getLocation().makeAbsolute().toOSString()), prefix, false).iterator();
		while (iterator.hasNext()) {
			file = iterator.next();
			try {
				String content = FileUtils.readFileToString(file);
				if (StringUtils.contains(content, "<tomcat-config>")) {
					result = file; 
				}
			} catch (IOException e) {
				Activator.logException(e);
			}
		}
		/**if (result==null){
			RuntimeException exception = new RuntimeException("Keine valide Tomcat Konfigurationsdatei (z.B. tomcat-config.xml)");
			openAlertDialog("Keine valide Tomcat Konfigurationsdatei (z.B. tomcat-config.xml)");
			throw exception;
		}*/
		return result;
	}
	
	public static void openAlertDialog(String message){
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(),SWT.NULL|SWT.ICON_ERROR);
		box.setMessage(message);
		box.setText("Fehler:");
		box.open();
	}
	
}
