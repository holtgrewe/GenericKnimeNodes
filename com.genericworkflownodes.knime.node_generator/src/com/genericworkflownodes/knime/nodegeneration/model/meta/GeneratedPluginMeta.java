package com.genericworkflownodes.knime.nodegeneration.model.meta;

import java.security.InvalidParameterException;
import java.util.Properties;

import com.genericworkflownodes.knime.nodegeneration.model.directories.NodesSourceDirectory;

public class GeneratedPluginMeta extends PluginMeta {

	/**
	 * Returns the plugin name.
	 * <p>
	 * If no configuration could be found, the name is created based on the
	 * given package name. e.g. org.roettig.foo will result in foo
	 * 
	 * @param packageName
	 * @return
	 */
	private static String getPluginName(Properties props, String packageName) {
		String pluginname = props.getProperty("pluginName");
		if (pluginname != null && !pluginname.isEmpty()) {
			return pluginname;
		}

		int idx = packageName.lastIndexOf(".");
		if (idx == -1) {
			return packageName;
		}
		return packageName.substring(idx + 1);
	}

	/**
	 * Checks if the plugin name is valid.
	 * 
	 * @param obj
	 * @param id
	 */
	private static boolean isPluginNameValid(final String pluginName) {
		return pluginName != null && pluginName.matches("^\\w+$");
	}

	/**
	 * Returns the plugin version.
	 * 
	 * @param packageName
	 * @return
	 */
	private static String getPluginVersion(final Properties props) {
		return props.getProperty("pluginVersion");
	}

	/**
	 * Checks whether a given package version is valid.
	 * 
	 * @param pluginVersion
	 * @return
	 */
	private static boolean isPluginVersionValid(final String pluginVersion) {
		// TODO
		return true;
	}

	/**
	 * Returns the package name the generated plugin uses. (e.g.
	 * org.roettig.foo).
	 * 
	 * @param props
	 * @return
	 */
	private static String getPackageRoot(final Properties props) {
		return props.getProperty("pluginPackage");
	}

	/**
	 * Checks whether a given package name is valid.
	 * 
	 * @param packageName
	 * @param id
	 * @return true if package name is valid; false otherwise
	 */
	public static boolean isValidPackageRoot(final String packageName) {
		return packageName != null
				&& packageName
						.matches("^([A-Za-z_]{1}[A-Za-z0-9_]*(\\.[A-Za-z_]{1}[A-Za-z0-9_]*)*)$");
	}

	/**
	 * Returns the package name the generated plugin uses. (e.g.
	 * org.roettig.foo).
	 * 
	 * @param props
	 * @return
	 */
	private static String getNodeRepositoyPath(final Properties props) {
		return props.getProperty("nodeRepositoyRoot");
	}

	/**
	 * Checks whether a given package name is valid.
	 * 
	 * @param nodeRepositoryPath
	 * @param id
	 * @return true if package name is valid; false otherwise
	 */
	public static boolean isNodeRepositoyPathValid(
			final String nodeRepositoryPath) {
		// TODO
		return true;
	}

	private final String name;
	private final String nodeRepositoyPath;

	public GeneratedPluginMeta(NodesSourceDirectory sourceDirectory) {
		super(getPackageRoot(sourceDirectory.getProperties()),
				getPluginVersion(sourceDirectory.getProperties()));

		if (getId() == null || getId().isEmpty()) {
			throw new InvalidParameterException("No package name was specified");
		}
		if (!isValidPackageRoot(getPackageRoot())) {
			throw new InvalidParameterException("The given package name \""
					+ getPackageRoot() + "\" is invalid");
		}

		this.name = getPluginName(sourceDirectory.getProperties(),
				getPackageRoot());
		if (this.name == null || this.name.isEmpty()) {
			throw new InvalidParameterException("No plugin name was specified");
		}
		if (!isPluginNameValid(this.name)) {
			throw new InvalidParameterException("The plugin name \""
					+ this.name
					+ "\" must only contain alpha numeric characters");
		}

		if (getVersion() == null || getVersion().isEmpty()) {
			throw new InvalidParameterException(
					"No plugin version was specified");
		}
		if (!isPluginVersionValid(getVersion())) {
			throw new InvalidParameterException("The plugin version \""
					+ getVersion() + "\" is not valid");
		}

		this.nodeRepositoyPath = getNodeRepositoyPath(sourceDirectory
				.getProperties());
		if (this.nodeRepositoyPath == null || this.nodeRepositoyPath.isEmpty()) {
			throw new InvalidParameterException(
					"No path within the node repository was specified");
		}
		if (!isNodeRepositoyPathValid(getVersion())) {
			throw new InvalidParameterException("The node repository path \""
					+ this.nodeRepositoyPath + "\" is not valid");
		}
	}

	/**
	 * Gets the KNIME plugin's name.
	 * <p>
	 * e.g. KNIME Test
	 * 
	 * @return The plugin's name.
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Returns the KNIME plugin's root package name
	 * <p>
	 * e.g. de.fu_berlin.imp.seqan
	 * 
	 * @return The plugin's package root.
	 */
	public final String getPackageRoot() {
		return getId();
	}

	/**
	 * Returns the path within KNIME node repository.
	 * <p>
	 * e.g. community/foo/bar
	 * 
	 * @return The path where the tool should be registered in the tool
	 *         registry.
	 */
	public final String getNodeRepositoryRoot() {
		return this.nodeRepositoyPath;
	}

}
