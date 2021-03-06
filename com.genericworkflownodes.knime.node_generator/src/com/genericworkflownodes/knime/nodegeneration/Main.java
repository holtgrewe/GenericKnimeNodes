package com.genericworkflownodes.knime.nodegeneration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.genericworkflownodes.knime.nodegeneration.NodeGenerator.NodeGeneratorException;

public class Main {

	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(NodeGenerator.class
			.getCanonicalName());

	/**
	 * 
	 * @param args
	 *            <li>#1: directory in which the plugin's sources reside;</li>
	 *            <li>#2: directory to where to put the plugins (base + payload
	 *            fragments)</li>
	 *            <p>
	 *            Note: The built plugin will neither be compiled nor be
	 *            packaged to a jar file.
	 */
	public static void main(String[] args) throws IOException {
		File srcDir = new File((args.length > 0) ? args[0] : ".")
				.getAbsoluteFile().getCanonicalFile();
		File buildDir = (args.length > 1) ? new File(args[1]).getAbsoluteFile()
				.getCanonicalFile() : null;
		if (buildDir != null) {
			buildDir.mkdirs();
		}

		try {
			NodeGenerator nodeGenerator = new NodeGenerator(srcDir, buildDir);
			nodeGenerator.generate();
		} catch (NodeGeneratorException e) {
			e.printStackTrace();
		}
	}
}
