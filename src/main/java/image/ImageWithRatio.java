package image;

import java.awt.Dimension;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;


public class ImageWithRatio {

	BufferedImage image;
	/**
	 * x-length divided by y-
	 */
	double ratio;
	int width;
	int height;
	String name;
	
	public ImageWithRatio(String pathToImage) throws IOException {
		this(new File(pathToImage));
	}
	
	public ImageWithRatio(File imgFile) throws IOException {
		super();
		System.out.println("Contructing ImageWithRatio from " + imgFile.getName());
		name = imgFile.getName();
		try {
			image = ImageIO.read(imgFile);
			height = image.getHeight();
			width = image.getWidth();
			ratio = (double) width / (double) height;
		}
		catch(IOException ex) {
			Dimension d = new Dimension();
			try(ImageInputStream in = ImageIO.createImageInputStream(imgFile)){
			    final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
			    if (readers.hasNext()) {
			        ImageReader reader = readers.next();
			        try {
			            reader.setInput(in);
			            
			            String format = reader.getFormatName();
			            System.out.println("format: " + format);
			            
			            try {
							d = new Dimension(reader.getWidth(0), reader.getHeight(0));
							
							//read data?
							//name = name + " " + d.toString();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        } finally {
			            reader.dispose();
			        }
			    }
			    
			    //test
			    byte[] imageInBytes;
			    //image = 
			}
			System.out.println("Had an exception: " + ex.getMessage());
			height = d.height;
			width = d.width;
			ratio = (double) width / (double) height;
		}

		
		
	}


	
	public boolean isStanding() {
		return ratio < 1;
	}
	
	public ImageOrientation getOrientation() {
		if (isStanding()) return ImageOrientation.vertical;
		else if (ratio == 1) return ImageOrientation.square;
		else return ImageOrientation.horizontal;
	}
	
	public double getRatio() {
		return ratio;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "ImageWithRatio [image=" + image + ", ratio=" + ratio + ", width=" + width + ", height=" + height
				+ ", name=" + name + "]";
	}
	
	
}
