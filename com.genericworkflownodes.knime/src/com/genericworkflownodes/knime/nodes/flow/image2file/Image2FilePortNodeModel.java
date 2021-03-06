/**
 * Copyright (c) 2013, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.flow.image2file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.data.DataCell;
import org.knime.core.data.image.ImageContent;
import org.knime.core.data.image.ImageValue;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.image.ImagePortObject;

import com.genericworkflownodes.util.FileStashFactory;
import com.genericworkflownodes.util.FileStashProperties;
import com.genericworkflownodes.util.IFileStash;

/**
 * This is the model implementation of Image2FilePort. Converts an Image Port to
 * a File port by saving it as either png or svg.
 * 
 * @author GenericKnimeNodes
 */
public class Image2FilePortNodeModel extends NodeModel {

	private static final NodeLogger LOGGER = NodeLogger
			.getLogger(Image2FilePortNodeModel.class);

	/**
	 * We assume that we always write png files. We need to check in future if
	 * we can change this.
	 */
	private static final String IMAGE_FILE_EXTENSION = "png";

	// the logger instance
	private static final NodeLogger logger = NodeLogger
			.getLogger(Image2FilePortNodeModel.class);

	/**
	 * Static method that provides the incoming {@link PortType}s.
	 * 
	 * @return The incoming {@link PortType}s of this node.
	 */
	private static PortType[] getIncomingPorts() {
		return new PortType[] { ImagePortObject.TYPE };
	}

	/**
	 * Static method that provides the outgoing {@link PortType}s.
	 * 
	 * @return The outgoing {@link PortType}s of this node.
	 */
	private static PortType[] getOutgoingPorts() {
		return new PortType[] { URIPortObject.TYPE };
	}

	private IFileStash fileStash;

	/**
	 * Constructor for the node model.
	 */
	protected Image2FilePortNodeModel() {
		super(getIncomingPorts(), getOutgoingPorts());
		this.fileStash = FileStashFactory.createTemporary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObject[] execute(final PortObject[] inObjects,
			final ExecutionContext exec) throws Exception {

		ImagePortObject imageObj = (ImagePortObject) inObjects[0];
		DataCell imageCellDC = imageObj.toDataCell();

		if (!(imageCellDC instanceof ImageValue)) {
			throw new InvalidSettingsException("Image object does not produce"
					+ " valid image object but " + imageCellDC == null ? null
					: imageCellDC.getClass().getName());
		}

		ImageValue v = (ImageValue) imageCellDC;
		ImageContent content = v.getImageContent();

		// check if the extension matches our PNG assumption
		checkExtension(v);
		// write the file to stash
		File outFile = writeImageFile(content);

		List<URIContent> outUri = new ArrayList<URIContent>();
		outUri.add(new URIContent(outFile.toURI(), IMAGE_FILE_EXTENSION));
		URIPortObject outPort = new URIPortObject(outUri);
		return new PortObject[] { outPort };
	}

	private File writeImageFile(ImageContent content) throws IOException,
			FileNotFoundException {
		File outFile = this.fileStash.getFile(
				Image2FilePortNodeModel.class.getSimpleName(),
				IMAGE_FILE_EXTENSION);
		logger.debug("Created output file " + outFile.getAbsolutePath());

		final FileOutputStream out = new FileOutputStream(outFile);
		try {
			content.save(out);
		} finally {
			out.close();
		}
		return outFile;
	}

	private void checkExtension(ImageValue v) throws InvalidSettingsException {
		final String imageExtension = v.getImageExtension();
		if (!IMAGE_FILE_EXTENSION.equals(imageExtension)) {
			throw new InvalidSettingsException(
					"The Image2FilePortNode can only handle PNG images.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		try {
			this.fileStash.deleteAllFiles();
		} catch (IOException e) {
			LOGGER.error("Error cleaning " + IFileStash.class.getSimpleName(),
					e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
			throws InvalidSettingsException {

		// maybe we can check in future if we get a png file
		// ImagePortObjectSpec imgSpec = (ImagePortObjectSpec) inSpecs[0];

		URIPortObjectSpec outSpec = new URIPortObjectSpec(IMAGE_FILE_EXTENSION);
		return new PortObjectSpec[] { outSpec };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
			throws InvalidSettingsException {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		File file = FileStashProperties.readLocation(internDir);
		if (file != null) {
			this.fileStash = FileStashFactory.createPersistent(file);
		} else {
			// leave temporary file stash
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
			final ExecutionMonitor exec) throws IOException,
			CanceledExecutionException {
		FileStashProperties.saveLocation(this.fileStash, internDir);
	}

}
