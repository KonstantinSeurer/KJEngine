package com.realityinteractive.imageio.tga;

/*
 * TGAImageReader.java
 * Copyright (c) 2003 Reality Interactive, Inc.  
 *   See bottom of file for license and warranty information.
 * Created on Sep 26, 2003
 */

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class TGAImageReader extends ImageReader {

	private ImageInputStream inputStream;

	private TGAHeader header;

	public TGAImageReader(final ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	@Override
	public void setInput(final Object input, final boolean seekForwardOnly, final boolean ignoreMetadata) {
		super.setInput(input, seekForwardOnly, ignoreMetadata);

		if (input == null) {
			inputStream = null;
			header = null;
		}

		if (input instanceof ImageInputStream) {
			inputStream = (ImageInputStream) input;

			inputStream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
		} else {
			throw new IllegalArgumentException("Only ImageInputStreams are accepted."); // FIXME: localize
		}
	}

	private synchronized TGAHeader getHeader() throws IOException {
		if (header != null)
			return header;
		if (inputStream == null)
			throw new IllegalStateException("There is no ImageInputStream from which the header can be read."); // FIXME: localize

		header = new TGAHeader(inputStream);

		return header;
	}

	private void checkImageIndex(final int imageIndex) {
		if (imageIndex != 0)
			throw new IndexOutOfBoundsException("Image index out of bounds (" + imageIndex + " != 0)."); // FIXME: localize
	}

	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(final int imageIndex) throws IOException {
		checkImageIndex(imageIndex);

		final TGAHeader header = getHeader();

		final ImageTypeSpecifier imageTypeSpecifier;
		switch (header.getImageType()) {
		case TGAConstants.COLOR_MAP:
		case TGAConstants.RLE_COLOR_MAP:
		case TGAConstants.TRUE_COLOR:
		case TGAConstants.RLE_TRUE_COLOR: {
			final boolean hasAlpha = header.getSamplesPerPixel() == 4;
			final int[] bandOffset;
			if (hasAlpha)
				bandOffset = new int[] { 2, 1, 0, 3 };// BGRA
			else
				bandOffset = new int[] { 2, 1, 0 };// BGR
			final ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
			imageTypeSpecifier = ImageTypeSpecifier.createInterleaved(rgb, bandOffset, DataBuffer.TYPE_BYTE, hasAlpha, false /* not pre-multiplied by an alpha */);

			break;
		}

		case TGAConstants.MONO:
		case TGAConstants.RLE_MONO:
			throw new IllegalArgumentException("Monochrome image type not supported.");

		case TGAConstants.NO_IMAGE:
		default:
			throw new IllegalArgumentException("The image type is not known."); // FIXME: localize
		}

		final List<ImageTypeSpecifier> imageSpecifiers = new ArrayList<ImageTypeSpecifier>();
		imageSpecifiers.add(imageTypeSpecifier);

		return imageSpecifiers.iterator();
	}

	@Override
	public int getNumImages(final boolean allowSearch) throws IOException {
		// NOTE: 1 is returned regardless if a search is allowed or not
		return 1;
	}

	@Override
	public IIOMetadata getStreamMetadata() throws IOException {
		return null;
	}

	@Override
	public IIOMetadata getImageMetadata(final int imageIndex) throws IOException {
		return null;
	}

	@Override
	public int getHeight(final int imageIndex) throws IOException {
		checkImageIndex(imageIndex);

		return getHeader().getHeight();
	}

	@Override
	public int getWidth(final int imageIndex) throws IOException {
		checkImageIndex(imageIndex);

		return getHeader().getWidth();
	}

	@Override
	public BufferedImage read(final int imageIndex, final ImageReadParam param) throws IOException {
		// NOTE: this will implicitly ensure that the imageIndex is valid
		final Iterator<ImageTypeSpecifier> imageTypes = getImageTypes(imageIndex);
		if (!imageTypes.hasNext()) {
			throw new IOException("Unsupported Image Type");
		}

		final TGAHeader header = getHeader();

		checkImageReadParam(param, header);

		final int width = header.getWidth();
		final int height = header.getHeight();

		final int[] colorMap = readColorMap(header);

		// TODO: read the color map
		inputStream.seek(header.getPixelDataOffset());

		final BufferedImage image = getDestination(param, imageTypes, width, height);
		final WritableRaster imageRaster = image.getRaster();

		final int numberOfImageBands = image.getSampleModel().getNumBands();
		checkReadParamBandSettings(param, header.getSamplesPerPixel(), numberOfImageBands);

		final int[] destinationBands;
		if (param != null) {
			destinationBands = param.getDestinationBands();
		} else {
			destinationBands = null;
		}

		final boolean hasAlpha = image.getColorModel().hasAlpha();
		final int numberOfComponents = image.getColorModel().getNumComponents();

		final WritableRaster raster = imageRaster.createWritableChild(0, 0, width, height, 0, 0, destinationBands);

		if (header.getBitsPerPixel() == 16 && hasAlpha) {
			throw new UnsupportedOperationException("This decoder does not support 1 bit alpha for 16 bit images.");
		}
		final int bytesPerPixel = (header.getBitsPerPixel() + 7) / 8;

		final byte[] resultData = ((DataBufferByte) raster.getDataBuffer()).getData(); // CHECK: is this valid / acceptible?
		int index = 0;
		int runLength = 0;
		boolean readPixel = true;
		boolean isRaw = false;

		byte red = 0;
		byte green = 0;
		byte blue = 0;
		byte alpha = (byte) 0xFF;

		final int minBufferSize = 8192 * 3;
		final ByteBuffer inputBuffer = ByteBuffer.allocate(minBufferSize);
		inputBuffer.order(ByteOrder.LITTLE_ENDIAN);
		inputBuffer.limit(0);

		final byte[] packedPixelbuffer = new byte[4];

		// TODO: break out the case of 32 bit non-RLE as it can be read
		// directly and 24 bit non-RLE as it can be read simply. If
		// subsampling and ROI's are implemented then selection must be
		// done per pixel for RLE otherwise it's possible to miss the
		// repetition count field.

		// TODO: account for TGAHeader.firstColorMapEntryIndex

		// TODO: this should be destinationROI.height (right?)
		for (int y = 0; y < height; y++) {
			if (header.isBottomToTop())
				index = (height - y) - 1;
			else /* is top-to-bottom */
				index = y;

			// TODO: this doesn't take into account the destination size or bands
			index *= width * numberOfComponents;

			// TODO: this should be destinationROI.width (right?)
			// NOTE: *if* destinations are used the RLE will break as this will
			// cause the repetition count field to be missed.
			for (int x = 0; x < width; x++) {
				if (header.isCompressed()) {
					if (runLength > 0) {
						// NOTE: a pixel is only read from the input if the
						// packet was raw. If it was a run length packet
						// then the previous (current) pixel is used.
						runLength--;
						readPixel = isRaw;
					} else {
						if (checkFillBuffer(inputStream, inputBuffer, bytesPerPixel + 1))
							return image;

						runLength = inputBuffer.get() & 0xFF; // unsigned

						isRaw = ((runLength & 0x80) == 0); // bit 7 == 0 -> raw; bit 7 == 1 -> runlength

						if (!isRaw)
							runLength -= 0x80;
						readPixel = true;
					}
				}

				// NOTE: only don't read when in a run length packet
				if (readPixel) {
					checkFillBuffer(inputStream, inputBuffer, bytesPerPixel);

					// NOTE: the alpha must have a default value since it is
					// not guaranteed to be present for each pixel read
					switch (bytesPerPixel) {
					case 1:
					default: {
						final int data = inputBuffer.get() & 0xFF; // unsigned

						if (header.hasColorMap()) {
							// CHECK: do sanity bounds check?
							final int packedPixel = colorMap[data];
							red = (byte) packedPixel;
							green = (byte) (packedPixel >>> 8);
							blue = (byte) (packedPixel >>> 16);
							alpha = (byte) (packedPixel >>> 24);
						} else {
							red = green = blue = (byte) data;
						}

						break;
					}

					case 2: {
						final int data = inputBuffer.getShort() & 0xFFFF;

						red = (byte) ((data >>> 10) & 0x1F);
						green = (byte) ((data >>> 5) & 0x1F);
						blue = (byte) (data & 0x1F);

						red = (byte) ((red << 3) + (red >>> 2));
						green = (byte) ((green << 3) + (green >>> 2));
						blue = (byte) ((blue << 3) + (blue >>> 2));
						break;
					}

					case 3: {
						inputBuffer.get(packedPixelbuffer, 0, 3);

						red = packedPixelbuffer[2];
						green = packedPixelbuffer[1];
						blue = packedPixelbuffer[0];
						break;
					}

					case 4: {
						inputBuffer.get(packedPixelbuffer, 0, 4);

						red = packedPixelbuffer[2];
						green = packedPixelbuffer[1];
						blue = packedPixelbuffer[0];
						alpha = packedPixelbuffer[3];
						break;
					}
					}
				}

				resultData[index + 0] = blue;
				resultData[index + 1] = green;
				resultData[index + 2] = red;
				index += 3;
				if (hasAlpha) {
					resultData[index] = alpha;
					index++;
				}

				// TODO: the right-to-left switch
			}
		}
		
		return image;
	}

	private boolean checkFillBuffer(ImageInputStream input, ByteBuffer buffer, int minRemaining) throws IOException {
		final int remaining = buffer.remaining();
		if (remaining < minRemaining) {
			final int bytesLoaded;
			if (remaining != 0) {
				buffer.get(buffer.array(), 0, remaining);
				bytesLoaded = input.read(buffer.array(), remaining, buffer.capacity() - remaining);
			} else {
				bytesLoaded = input.read(buffer.array());
			}
			if (bytesLoaded == -1)
				return true;
			buffer.position(0);
			buffer.limit(remaining + bytesLoaded);
		}
		return false;
	}

	private int[] readColorMap(final TGAHeader header) throws IOException {
		if (!header.hasColorMap())
			return null;
		inputStream.seek(header.getColorMapDataOffset());
		final int numberOfColors = header.getColorMapLength();
		final int bitsPerEntry = header.getBitsPerColorMapEntry();
		// CHECK: why is tge explicit +1 needed here ?!?
		final int[] colorMap = new int[numberOfColors + 1];

		final byte[] buffer = new byte[4];

		for (int i = 0; i < numberOfColors; i++) {
			int red = 0, green = 0, blue = 0;
			switch (bitsPerEntry) {
			case 8:
			default: {
				final int data = inputStream.readByte() & 0xFF; // unsigned
				red = green = blue = data;

				break;
			}

			case 15:
			case 16: {
				final int data = inputStream.readShort() & 0xFFFF; // unsigned

				red = (byte) ((data >>> 10) & 0x1F);
				green = (byte) ((data >>> 5) & 0x1F);
				blue = (byte) (data & 0x1F);

				red = (byte) ((red << 3) + (red >>> 2));
				green = (byte) ((green << 3) + (green >>> 2));
				blue = (byte) ((blue << 3) + (blue >>> 2));

				break;
			}

			case 24:
			case 32:
				// CHECK: is there an alpha?!?
				inputStream.read(buffer, 0, 3);

				blue = buffer[0] & 0xFF;
				green = buffer[1] & 0xFF;
				red = buffer[2] & 0xFF;

				break;
			}

			colorMap[i] = (red << 0) | (green << 8) | (blue << 16);
		}

		return colorMap;
	}

	private void checkImageReadParam(final ImageReadParam param, final TGAHeader header) throws IOException {
		if (param != null) {
			final int width = header.getWidth();
			final int height = header.getHeight();

			final Rectangle sourceROI = param.getSourceRegion();
			if ((sourceROI != null) && ((sourceROI.x != 0) || (sourceROI.y != 0) || (sourceROI.width != width) || (sourceROI.height != height))) {
				throw new IOException("The source region of interest is not the default."); // FIXME: localize
			}

			final Rectangle destinationROI = param.getSourceRegion();
			if ((destinationROI != null) && ((destinationROI.x != 0) || (destinationROI.y != 0) || (destinationROI.width != width) || (destinationROI.height != height))) {
				throw new IOException("The destination region of interest is not the default."); // FIXME: localize
			}

			if ((param.getSourceXSubsampling() != 1) || (param.getSourceYSubsampling() != 1)) {
				throw new IOException("Source sub-sampling is not supported."); // FIXME: localize
			}
		}
	}
}
// =============================================================================
/*
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */