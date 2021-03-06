/**
 * Copyright (c) 2012, Björn Kahlert.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.nodegeneration.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.genericworkflownodes.knime.config.INodeConfiguration;
import com.genericworkflownodes.knime.nodegeneration.NodeGenerator;
import com.genericworkflownodes.knime.nodegeneration.model.meta.GeneratedPluginMeta;
import com.genericworkflownodes.util.StringUtils;

/**
 * Abstraction of the PluginActivator template.
 * 
 * @author bkahlert, aiche
 */
public class PluginActivatorTemplate extends Template {

	private static String EXTERNAL_TOOL_CTOR_TEMPLATE = "new ExternalTool(\"%s\", \"%s\", \"%s\")";

	/**
	 * Constructor.
	 * 
	 * @param generatedPluginMeta
	 *            The name of the package, where the PluginActivator will be
	 *            located.
	 * @param configurations
	 *            A list of {@link INodeConfiguration}s contained in this
	 *            plugin.
	 * @throws IOException
	 *             Will be thrown if the access to the template file fails.
	 */
	public PluginActivatorTemplate(
			final GeneratedPluginMeta generatedPluginMeta,
			final List<INodeConfiguration> configurations) throws IOException {
		super(NodeGenerator.class
				.getResourceAsStream("templates/PluginActivator.template"));

		replace("__BASE__", generatedPluginMeta.getPackageRoot());
		replace("__PLUGIN_ID__", generatedPluginMeta.getId());
		replace("__PLUGIN_NAME__", generatedPluginMeta.getName());

		List<String> externalTools = new ArrayList<String>();
		for (INodeConfiguration config : configurations) {
			String nodeName = config.getName();
			String executableName = config.getName();
			if (config.getExecutableName() != null
					&& config.getExecutableName() != "") {
				executableName = config.getExecutableName();
			}

			externalTools.add(String.format(EXTERNAL_TOOL_CTOR_TEMPLATE,
					generatedPluginMeta.getId(), nodeName, executableName));
		}

		replace("__EXTERNAL_TOOLS__", StringUtils.join(externalTools, ", "));
	}
}
