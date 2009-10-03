package org.embedded.tomcat.launch;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.ui.IEditorPart;

public class TomcatLaunchShortcut implements ILaunchShortcut  {

	//private static final String JDK16_CONTAINER_ID = "org.eclipse.jdt.launching.JRE_CONTAINER/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/jdk1.6";
	/**
	 * implements {@link ILaunchShortcut}
	 */
	public void launch(ISelection selection, String mode) {
		if (selection != null && selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IProject targetProject = null;
			IResource configFile = null;
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof IProject) {
				targetProject = (IProject) obj;
			} else if (obj instanceof IResource) {
				targetProject = ((IResource) obj).getProject();
				configFile = (IFile) obj;
			}

			if (targetProject != null) {
				launchApplication(targetProject, mode, configFile);
			}
		}
	}

	/**
	 * implements {@link ILaunchShortcut}
	 */
	public void launch(IEditorPart editor, String mode) {	}

	/**
	 * build and launch for air application.
	 * @param targetProject
	 */
	private void launchApplication(IProject targetProject, String mode, IResource configFile) {
		try {
			ILaunchConfiguration config = getLaunchConfiguration(targetProject, mode, configFile);
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
	private ILaunchConfiguration getLaunchConfiguration(IProject project, String main, IResource configFile) throws CoreException {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] configs = manager.getLaunchConfigurations();

		for (int i = 0; i < configs.length; i++) {
			String value = configs[i].getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			if (value.equals(project.getName())) {
				configs[i].delete();
			}
		}

		ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);

		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, manager
				.generateUniqueLaunchConfigurationNameFrom(project.getName()));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.embedded.tomcat.starter.TomcatStarter");
		//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, JDK16_CONTAINER_ID);
		//wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-Xmx512m -DtomcatConfig="+findConfigFile(project).getName());
		if (configFile!=null){
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, configFile.getName());
		}
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);

		return wc.doSave();
	}

	
}
