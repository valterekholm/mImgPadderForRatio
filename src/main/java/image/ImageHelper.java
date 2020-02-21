package image;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import settings.Settings;

public class ImageHelper {

	private Settings _settings;

	public ImageHelper(Settings s) {
		_settings = s;
	}

	public static boolean hasFolderAnyPicture(File[] folderContent) {
		long count = Arrays
				.asList(folderContent).stream().filter(f -> f.getName().endsWith("png") || f.getName().endsWith("jpg")
						|| f.getName().endsWith("gif") || f.getName().endsWith("tif") || f.getName().endsWith("bmp")
						|| f.getName().endsWith("jpeg"))
				.count();
		System.out.println("img count: " + count);
		return count > 0;
	}

	public boolean hasFolderAnyPicture_NonStatic(File[] folderContent) {
		long count = Arrays
				.asList(folderContent).stream().filter(f -> f.getName().endsWith("png") || f.getName().endsWith("jpg")
						|| f.getName().endsWith("gif") || f.getName().endsWith("tif") || f.getName().endsWith("bmp")
						|| f.getName().endsWith("jpeg"))
				.count();
		System.out.println("img count: " + count);
		return count > 0;
	}

	public static List<File> getPictureFilesFromFolder(File[] folderContent) {
		if (!hasFolderAnyPicture(folderContent)) {
			return null;
		}

		List<File> images = Arrays
				.asList(folderContent).stream().filter(f -> f.getName().endsWith("png") || f.getName().endsWith("jpg")
						|| f.getName().endsWith("gif") || f.getName().endsWith("tif") || f.getName().endsWith("bmp")
						|| f.getName().endsWith("jpeg"))
				.collect(Collectors.toList());
		// System.out.println("List size: " + images.size());
		return images;
	}

	public List<File> getPictureFilesFromFolder_NonStatic(File[] folderContent) {
		if (!hasFolderAnyPicture(folderContent)) {
			return null;
		}

		List<File> images = Arrays
				.asList(folderContent).stream().filter(f -> f.getName().endsWith("png") || f.getName().endsWith("jpg")
						|| f.getName().endsWith("gif") || f.getName().endsWith("tif") || f.getName().endsWith("bmp")
						|| f.getName().endsWith("jpeg"))
				.collect(Collectors.toList());
		// System.out.println("List size: " + images.size());
		return images;
	}

	public static boolean hasFolderAnyPictureWithExceedingRatio(String path, double ratioX, double ratioY,
			boolean checkBothOrientation) {
		System.out.println("hasFolderAnyPictureWithExceedingRatio " + checkBothOrientation);
		List<File> imgFiles = getPictureFilesFromFolder(listFromDirectory(path));
		List<ImageWithRatio> foundImages = new ArrayList<ImageWithRatio>();
		boolean doAgain = checkBothOrientation;
		int doneCount = 0;
		double ratioLimitKvot = 0;// can be switched(?)

		for (File f : imgFiles) {
			try {
				foundImages.add(new ImageWithRatio(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Have loaded images: " + foundImages.size());
		do {
			System.out.println("Do again: " + doAgain + ", count: " + doneCount);
			ratioLimitKvot = ratioX / ratioY;
			System.out.println("ratioLimit: " + ratioLimitKvot);
			for (ImageWithRatio i : foundImages) {

				if (ratioLimitKvot < 1 && i.getRatio() < ratioLimitKvot) {
					// is under
					System.out.println("Under : " + i.getName() + " " + i.getRatio());
					return true;
				} else if (ratioLimitKvot > 1 && i.getRatio() > ratioLimitKvot) {
					System.out.println("Over : " + i.getName() + " " + i.getRatio());
					return true;
				} else {
					System.out.println("Found normal ratio: " + i.getRatio() + ", " + i.getName());
				}
			}

			doneCount++;
			double temp = ratioX;
			ratioX = ratioY;
			ratioY = temp;

			System.out.println("Har bytt plats...");

			System.out.println("-------------------one lap------------------- " + doneCount);
		} while (doAgain && doneCount < 2);

		System.out.println("All image sizes are normal");
		return false;
	}

	public boolean hasFolderAnyPictureWithExceedingRatio_NonStatic(String path, double ratioX, double ratioY,
			boolean checkBothOrientation, StringBuilder errorReport) {
		System.out.println("hasFolderAnyPictureWithExceedingRatio " + checkBothOrientation);

		List<File> imgFiles = getPictureFilesFromFolder(listFromDirectory(path));
		List<Dimension> dimensions = new ArrayList<Dimension>();

		getDimensionsFromFiles(imgFiles, errorReport);

		if (imgFiles.size() != dimensions.size()) {
			System.out.println("Fel, olika storlek");
			return false;
		}

		boolean doAgain = checkBothOrientation;
		int doneCount = 0;
		double ratioLimitKvot = 0;// can be switched(?)
		
		double imgRatio;

		do {
			System.out.println("Do again: " + doAgain + ", count: " + doneCount);
			ratioLimitKvot = ratioX / ratioY;
			System.out.println("ratioLimit: " + ratioLimitKvot);
			int counter = 0;
			for (Dimension d : dimensions) {
				imgRatio = getRatioFromDimension(d);
				
				if (ratioLimitKvot < 1 && imgRatio < ratioLimitKvot) {
					System.out.println("Under : " + imgFiles.get(counter).getName() + " " + imgRatio);
					return true;
				} else if (ratioLimitKvot > 1 && imgRatio > ratioLimitKvot) {
					System.out.println("Over : " + imgFiles.get(counter).getName() + " " + imgRatio);
					return true;
				} else {
					System.out.println(
							"Found normal ratio: " + imgRatio + ", " + imgFiles.get(counter).getName());
				}
				counter++;
			}

			doneCount++;
			double temp = ratioX;
			ratioX = ratioY;
			ratioY = temp;

			System.out.println("Har bytt plats...");

			System.out.println("-------------------one lap------------------- " + doneCount);
		} while (doAgain && doneCount < 2);

		System.out.println("All image sizes are normal");
		return false;
	}

	private void getDimensionsFromFiles(List<File> imgFiles, List<Dimension> dimensions, StringBuilder errorReport) {
		for (File f : imgFiles) {
			dimensions.add(getImageDimension(f));
		}
	}

	private List<Dimension> getDimensionsFromFiles(List<File> imgFiles, StringBuilder errorReport) {
		List<Dimension> dimensions = new ArrayList<Dimension>();
		List<String> errorFiles = new ArrayList<String>();

		Dimension tempD;
		for (File f : imgFiles) {
			tempD = new Dimension();
			try {
				tempD = getImageDimension(f);
				dimensions.add(tempD);
			} catch (Exception ex) {
				errorFiles.add(f.getName());
			}

		}
		if (errorFiles.size() == 1) {
			// errorReport.append("There was a problem with file " + errorFiles.get(0) + "
			// it can't be used");
			errorReport.append("Det fanns problem med filen " + errorFiles.get(0) + " den kan inte användas");
		} else if (errorFiles.size() > 1) {
			errorReport.append("Det fanns problem med filerna ");
			for (String fileN : errorFiles) {
				errorReport.append(fileN + " ");
			}
			errorReport.append(", de kan inte användas");
		}
		return dimensions;
	}


	public List<File> listImagesAndRatiosAndIfExceedingRatioLimit_NonStatic(String path, double ratioX, double ratioY,
			boolean checkBothOrientation, StringBuilder errorReport) {
		System.out.println("listImagesAndRatiosAndIfExceedingRatioLimit_NonStatic, limit from " + ratioX + ":" + ratioY
				+ " = " + ratioX / ratioY);
		//TODO: bilder som g�rs bredare m�ste bli en pixel predare... el s�
		List<File> imgFiles = getPictureFilesFromFolder(listFromDirectory(path));

		// List<ImageWithRatio> foundImages = new ArrayList<ImageWithRatio>();
		boolean doAgain = checkBothOrientation;
		int doneCount = 0;//for each axis checked (can be max 2)
		double ratioLimitKvot = 0;// can be switched(?)
		int errorCount = 0;

		/**
		 * To show what kinds of offsize was found
		 */
		List<ImageOrientation> foundOffSizes = new ArrayList<>();
		List<File> foundOffSized = new ArrayList<>();
		List<Dimension> dimensions = new ArrayList<>();

		dimensions = getDimensionsFromFiles(imgFiles, errorReport);

		ImageOrientation foundOrient;

		if (imgFiles.size() != dimensions.size()) {
			System.out.println("Fel, olika antal i arraylist");
			return null;
		}

		// System.out.println("Have loaded images: " + foundImages.size());
		do {
			// Declare the ratio limit
			System.out.println("Do again: " + doAgain + ", count: " + doneCount);
			if (ratioX > ratioY) {
				System.out.println("Looking for images with excessive width (compared to height)");
				foundOrient = ImageOrientation.horizontal;
			} else if (ratioY > ratioX) {
				System.out.println("Looking for images with excessive height (compared to width)");
				foundOrient = ImageOrientation.vertical;
			} else {
				// typ 1:1
				foundOrient = ImageOrientation.square;
			}

			ratioLimitKvot = ratioX / ratioY;
			System.out.println("ratioLimit: " + ratioLimitKvot);
			int counter = 0;//for each image
			// Go through the dimensions
			for (Dimension d : dimensions) {
				if (d != null) { //TODO: exclude that 'image' from any further handling
					// break;//TODO: show messsage
					System.out.println("Using dimension: " + d);
					/*
					 * if(doneCount==1) { ratiox = 1 / i.getRatio(); } else { ratiox = i.getRatio();
					 * }
					 */
					double foundRatio = 0;

					try {
						foundRatio = getRatioFromDimension(d);
					} catch (Exception ex) {
						errorCount++;
						errorReport.append(imgFiles.get(counter).getName() + " - fel, ta bort filen! ");
					}

					// catch(Exception ex) {
					// System.out.println(ex.getMessage());
					// errorsReport.append("Det uppstod fel med filen " +
					// imgFiles.get(counter).getName() + ", dess dimension var " + d);
					// throw(new IIOException("Error with image " + imgFiles.get(counter).getName()
					// + ", dimension was " + d));
					// A dimension of null - must be a broken file...
					// }

					if (ratioLimitKvot < 1 && foundRatio < ratioLimitKvot) {
						// is under
						System.out.println(imgFiles.get(counter).getName() + "\t" + foundRatio + "\tExceeding");

						foundOffSizes.add(ImageOrientation.vertical);
						System.out.println("Detta var " + foundOffSizes.get(foundOffSizes.size() - 1));
						try {
							foundOffSized.add(imgFiles.get(counter));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}
						// return true;
					} else if (ratioLimitKvot > 1 && foundRatio > ratioLimitKvot) {
						System.out.println(imgFiles.get(counter).getName() + "\t" + foundRatio + "\tExceeding");
						foundOffSizes.add(ImageOrientation.horizontal);
						System.out.println("Detta var " + foundOffSizes.get(foundOffSizes.size() - 1));
						try {
							foundOffSized.add(imgFiles.get(counter));
							System.out.println(imgFiles.get(counter));
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
							System.out.println(imgFiles.get(counter));
						}
						// return true;
					} else if (foundOrient == ImageOrientation.square) {

						if (d.height != d.width) {
							foundOffSizes.add(ImageOrientation.square);
							foundOffSized.add(imgFiles.get(counter));
							// prevent further checking
							doAgain = false;
						}
					} else {
						System.out.println(imgFiles.get(counter).getName() + "\t" + foundRatio);
					}


					counter++;




					System.out.println("-------------------one image------------------- " + doneCount);
				}
				else {
					errorReport.append(imgFiles.get(counter).getName() + " - fel! ");
				}
			}
			// switch x:y to be able to check opposite dimension
			double temp = ratioX;
			ratioX = ratioY;
			ratioY = temp;

			System.out.println("Har bytt plats p� axis");
			
			doneCount++;
			System.out.println("doAgain: " + doAgain + ", doneCount: " + doneCount);

		} while (doAgain && doneCount < 2);
		System.out.println("foundOffSizes.size: " + foundOffSizes.size());
		// return false;
		return foundOffSized;
	}

	public static File[] listFromDirectory(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				// System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				// System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		return listOfFiles;
	}

	public File[] listFromDirectory_NonStatic(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				// System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				// System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

		return listOfFiles;
	}

	public static void save1(JPanel p, String fileType, String filename) {
		BufferedImage i = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) i.getGraphics();

		p.paint(g2);

		try {
			ImageIO.write(i, fileType, new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save1_NonStatic(JPanel p, String fileType, String filename) {
		BufferedImage i = new BufferedImage(p.getWidth(), p.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) i.getGraphics();

		p.paint(g2);

		try {
			ImageIO.write(i, fileType, new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param i              - the image that might need correction
	 * @param ratioX         - the supplied value, that is to be read together with
	 *                       ratioY, forming a ratio
	 * @param ratioY         - the supplied value, that is to be read together with
	 *                       ratioX, forming a ratio
	 * @param assertBothAxis - true if the derived ratio is to set limit for both
	 *                       orientations/axis
	 */
	public static boolean save2(ImageWithRatio i, double ratioX, double ratioY, boolean assertBothAxis) {// TODO:
																											// correct
																											// to be
																											// like
																											// save2_NonStatic
		System.out.println(
				"save 2 with i " + i + ", rX: " + ratioX + ", rY: " + ratioY + ", assertBothAxis: " + assertBothAxis);
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		Canvas c = new Canvas();
		double limit = ratioX / ratioY;
		double newWidth = 0;
		double newHeight = 0;

		int startX = 0, startY = 0;// for placing image

		if (limit < 1 && i.getRatio() < limit) {
			// standing ratio, e.g. 2:3
			// Must add horizontally
			newWidth = (i.height * limit);
			System.out.println("want new width 1");
		} else if (limit > 1 && i.getRatio() > limit) {
			// laid ratio, e.g. 3:2
			// must add vertically
			newHeight = (i.width * (1.0 / limit));
			System.out.println("want new height 1");
		}
		if (assertBothAxis && newWidth == 0 && newHeight == 0) {// want to enable inverted ratio and not yet found image
																// exceeding
			// switch axis
			double temp = ratioX;
			ratioX = ratioY;
			ratioY = temp;
			limit = ratioX / ratioY;

			if (limit < 1 && i.getRatio() < limit) {
				newWidth = i.height * limit;
				System.out.println("want new width 2");
			} else if (limit > 1 && i.getRatio() > limit) {
				newHeight = i.width * (1.0 / limit);
				System.out.println("want new height 2");
			}

		}

		System.out.println("Want this, newWidth: " + newWidth + ", newHeight: " + newHeight);
		if (newWidth > 0) {
			f.setSize((int) newWidth, i.height);
			p.setSize((int) newWidth, i.height);
			c.setSize((int) newWidth, i.height);
			startX = (int) (newWidth - i.width) / 2;
		} else if (newHeight > 0) {
			f.setSize(i.width, (int) newHeight);
			p.setSize(i.width, (int) newHeight);
			c.setSize(i.width, (int) newHeight);
			startY = (int) ((int) newHeight - (int) i.height) / 2;
			System.out.println("startY: " + startY);
		}

		// Raster r = i.image.getData();
		JLabel lbl = new JLabel(new ImageIcon(i.image));
		p.add(lbl);
		f.add(p);
		// f.show(); //fungerar

		// save
		BufferedImage copy = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) copy.getGraphics();
		g2.setColor(Color.white);
		g2.fillRect(0, 0, f.getWidth(), f.getHeight());
		g2.drawImage(i.image, startX, startY, null);

		String lastExt = getFileExtensionName(i.getName()); // if filesystem with no filename ext is to be targeted, use
															// FilenameUtils.getExtension from Apache Commons IO
		String bareName = getNameWithoutExtension(i.getName());
		String newName = bareName + "_padded." + lastExt;

		File test = new File(newName);
		if (test.exists()) {
			test.delete();
		}

		boolean success = false;

		try {
			System.out.println("Will try saving " + newName + ", of type " + lastExt);
			ImageIO.write(copy, lastExt, new File(newName));
			success = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		File file = new File(newName);
		if (file.exists()) {
			System.out.println("File " + file.getName() + " exists");
		} else {
			System.out.println("File " + file.getName() + " not found");
		}

		return success;
	}

	public boolean save2_NonStatic(File file, Settings settings) throws IOException {
		_settings = settings;// TODO: remove?
		double ratioX = settings.getRatioX(), ratioY = settings.getRatioY();
		boolean assertBothAxis = settings.isAssertBothAxis();
		System.out.println("save 2 with file " + file + ", rX: " + ratioX + ", rY: " + ratioY + ", assertBothAxis: "
				+ assertBothAxis);
		JFrame f = new JFrame();
		JPanel p = new JPanel();
		Canvas c = new Canvas();
		double limit = ratioX / ratioY;
		double newWidth = 0;
		double newHeight = 0;

		int startX = 0, startY = 0;// for placing image

		Dimension d = getImageDimension(file);
		double imageRatio = getRatioFromDimension(d);

		// checking given ratio
		if (limit < 1 && imageRatio < limit) {
			// standing ratio, e.g. 2:3
			// Must add horizontally

			newWidth = Math.ceil(d.height * limit);
			System.out.println("want new width 1");
		} else if (limit > 1 && imageRatio > limit) {
			// laid ratio, e.g. 3:2
			// must add vertically
			newHeight = Math.ceil(d.width * (1.0 / limit));
			System.out.println("want new height 1");
		} else if (limit == 1 && imageRatio != 1) {
			System.out.println("square ratio, make shortest side same as longest side");
			if (d.width > d.height) {
				newHeight = d.getWidth();
			} else {
				newWidth = d.getHeight();
			}
		}

		if (assertBothAxis && newWidth == 0 && newHeight == 0) {// want to enable inverted (switched) ratio and not yet
																// found image exceeding
			// switch axis of ratio
			double temp = ratioX;
			ratioX = ratioY;
			ratioY = temp;
			limit = ratioX / ratioY;

			// Try again, other axis

			if (limit < 1 && getRatioFromDimension(d) < limit) {
				newWidth = Math.round(d.height * limit);
				System.out.println("want new width 2");
			} else if (limit > 1 && getRatioFromDimension(d) > limit) {
				newHeight = Math.round(d.width * (1.0 / limit));
				System.out.println("want new height 2");
			}
		}

		System.out.println("Want this, newWidth: " + newWidth + ", newHeight: " + newHeight);
		if (newWidth > 0) {
			f.setSize((int) newWidth, d.height);
			p.setSize((int) newWidth, d.height);
			c.setSize((int) newWidth, d.height);
			startX = (int) (newWidth - d.width) / 2;
		} else if (newHeight > 0) {
			f.setSize(d.width, (int) newHeight);
			p.setSize(d.width, (int) newHeight);
			c.setSize(d.width, (int) newHeight);
			startY = (int) ((int) newHeight - (int) d.height) / 2;
			System.out.println("startY: " + startY);
		} else {

			return false;
		}

		ImageWithRatio i = new ImageWithRatio(file);

		// Raster r = i.image.getData();
		JLabel lbl = new JLabel(new ImageIcon(i.image));
		p.add(lbl);
		f.add(p);
		// f.show(); //fungerar


		
		
        // Determine if the GraphicsDevice supports translucency.
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        //If translucent windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
            System.err.println(
                "Translucency is not supported");
                //System.exit(0);
        }
        else {
        	System.out.println("Translucency is supported");
        }
		
		// g2.setColor(getColorOfEdge1(i.image)); //use it to pick color from image
		//test av function
		Color bg1 = getColorOfEdge1(i.image);
		System.out.println("Got background color, " + bg1 + ", alpha " + bg1.getAlpha());
		

		
		
		// save new image with new dimensions
		BufferedImage copy;
		Graphics2D g2;
		
		
		if(bg1.getAlpha() == 0) {//TODO: get a transparent bg on new image
			//completely transparent
			copy = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_ARGB);//TYPE_INT_RGB
			g2 = copy.createGraphics();//(Graphics2D) copy.getGraphics();
			System.out.println("Background is transparent!!!!!!!!!!!!!!!");
			g2.setColor(bg1);
			
			
			
			//int rule = AlphaComposite.; //tested XOR, SRC_OVER(transp foregr), SRC_OUT, SRC_IN, SRC_ATOP, DST_OVER, DST_OUT, DST_IN, DST_ATOP, DST, SRC, SRC_OVER
			//Composite comp = AlphaComposite.getInstance(rule , .5f);//test med 0.5 f�r transparens
			//g2.setComposite(comp);
		}
		else {
			copy = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
			g2 = copy.createGraphics();
			g2.setColor(_settings.getBackgroundColor());
		}
		//Make a background color with the color set by g2.setColor()		
		g2.fillRect(0, 0, f.getWidth(), f.getHeight());
		g2.drawImage(i.image, startX, startY, null);


		String newName = i.getName();
		String lastExt = getFileExtensionName(i.getName()); // if filesystem with no filename ext is to be targeted, use
															// FilenameUtils.getExtension from Apache Commons IO

		System.out.println("Settings isReplaceOriginalImage: " + _settings.isReplaceOriginalImage());
		if (!_settings.isReplaceOriginalImage()) {
			String bareName = getNameWithoutExtension(i.getName());
			newName = bareName + _settings.getSuffix() + "." + lastExt;
		}

		File test = new File(newName);
		if (test.exists()) {
			test.delete();
		}

		boolean success = false;

		try {
			System.out.println("Will try saving " + newName + ", of type " + lastExt);

			if (lastExt.equalsIgnoreCase("JPG")) {
				System.out.println("JPEG - set high quality .9");
				JPEGImageWriteParam jpegParams;
				jpegParams = new JPEGImageWriteParam(null);
				jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				jpegParams.setCompressionQuality(.9f);

				final ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
				// specifies where the jpg image has to be written
				// FileImageOutputStream ios = (FileImageOutputStream)
				// ImageIO.createImageOutputStream(newName);

				FileImageOutputStream outStream = new FileImageOutputStream(new File(newName));
				writer.setOutput(outStream);

				// writes the file with given compression level
				// from your JPEGImageWriteParam instance
				try {
					writer.write(null, new IIOImage(copy, null, null), jpegParams);
				} finally {
					writer.dispose();
					outStream.flush();
					outStream.close();
				}
			} else {
				ImageIO.write(copy, lastExt, new File(newName));
			}

			success = true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		File fil = new File(newName);
		if (fil.exists()) {
			System.out.println("File " + fil.getName() + " exists");
		} else {
			System.out.println("File " + fil.getName() + " not found");
			success = false;
		}

		System.out.println("Ska returnera " + success);
		return success;
	}

	static String getFileExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}

	static String getFileExtension(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf);
	}

	/**
	 * Get file name extension without dot
	 * 
	 * @param name - filename
	 * @return
	 */
	static String getFileExtensionName(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return ""; // empty extension
		}
		return name.substring(lastIndexOf + 1);
	}

	static String getNameWithoutExtension(String name) {
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return name;
		}
		return name.substring(0, lastIndexOf);
	}

	static double roundTo2Decimals(double val) {
		return (double) Math.round(val * 100) / 100;
	}

	static Dimension getImageDimension(File file) {
		try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
			final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				try {
					reader.setInput(in);
					return new Dimension(reader.getWidth(0), reader.getHeight(0));
				} catch (Exception ex) {
					return null;
				} finally {
					reader.dispose();
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			return null;
		}
		return null;
	}

	static double getRatioFromDimension(Dimension d) {
		if (d == null) {
			throw new NullPointerException();
		}
		return (double) d.width / (double) d.height;
	}

	/**
	 * Get the edge pixels from an image as a single array, starting from top-left,
	 * going top top-right, then to bottom-right ...
	 * 
	 * @param img
	 * @return
	 */
	static int[] getEdgePixels(BufferedImage img) {
		System.out.println("getEdgePixels med img " + img + ", " + img.isAlphaPremultiplied());
		int width = img.getWidth();
		int height = img.getHeight();
		boolean hasAlpha = img.getAlphaRaster() != null;//TODO: make faster
		
		System.out.println("width " + width + " height " + height);

		int edgeLength = width * 2 + height * 2 - 4;

		int[] edge = new int[edgeLength];
		// top

		//int strideBase = (width - 1) * 3;
		//int rem = 4 - (strideBase % 4);
		//int stride = strideBase + rem;

		// img.getRGB(0, 0, width-1, 1, edge, 0, stride);
		int x, y, count = 0;

		for (x = 0; x < width-1; x++, count++) {
			int pix = img.getRGB(x, 0);
			Color mycolor = new Color(pix, hasAlpha);
			edge[count] = pix;// top
			System.out.print(mycolor + " " + mycolor.getAlpha() + " ");
		}

		for (y = 0; y < height - 1; y++, count++) {// right
			edge[count] = img.getRGB(x, y);
		}

		for (x = width - 1; x > 0; x--, count++) {// bottom
			edge[count] = img.getRGB(x, y);
		}

		for (; y > 0; y--, count++) {
			edge[count] = img.getRGB(x, y);
		}
		System.out.println("Copied to edge, edgeLength = " + edgeLength + ", count = " + count);
		return edge;
	}

	static Color getColorOfEdge1(BufferedImage i) {
		System.out.println("getColorOfEdge1");
		
		boolean hasAlpha = i.getAlphaRaster() != null;//TODO: make faster
		int[] edge = getEdgePixels(i);
		int firstPixel = edge[0];
		Color found = new Color(firstPixel, hasAlpha);
		System.out.println("firstPixel: " + found);
		return found;
	}

	static int RGBToInt(int r, int g, int b) {
		int rgb = r;
		rgb = (rgb << 8) + g;
		rgb = (rgb << 8) + b;
		return rgb;
	}

	/**
	 * Calculate difference between two colors, here just based on RGB-values, not
	 * on human eye perception
	 * 
	 * @param color1
	 * @param color2
	 * @return
	 */
	static double getRGBDistance(Color color1, Color color2) {
		double d;
		color1 = RGBAToRGB_bgWhite(color1);
		color2 = RGBAToRGB_bgWhite(color2);// needed?

		int r1 = color1.getRed(), r2 = color2.getRed();
		int g1 = color1.getGreen(), g2 = color2.getGreen();
		int b1 = color1.getBlue(), b2 = color2.getBlue();
		d = Math.sqrt((r2 - r1) ^ 2 + (g2 - g1) ^ 2 + (b2 - b1) ^ 2);
		return d;
	}

	/**
	 * To get a rgb value without alpha-value, assuming bg-color is white
	 * 
	 * @param color
	 * @return
	 */
	static Color RGBAToRGB_bgWhite(Color color) {
		int r, g, b;
		Color bgColor = new Color(255, 255, 255);// white
		r = color.getRed() * color.getAlpha() + bgColor.getRed() * (255 - color.getAlpha());
		g = color.getGreen() * color.getAlpha() + bgColor.getGreen() * (255 - color.getAlpha());
		b = color.getBlue() * color.getAlpha() + bgColor.getBlue() * (255 - color.getAlpha());
		Color result = new Color(r, g, b);
		return result;
		// https://stackoverflow.com/questions/6486002/transform-rgba-with-underlying-color-into-rgb
	}
}
