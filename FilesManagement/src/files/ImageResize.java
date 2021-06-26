package files;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.imgscalr.Scalr;
import org.marvinproject.image.transform.scale.Scale;

//import com.luciad.imageio.webp.WebPWriteParam;

import marvin.image.MarvinImage;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

public class ImageResize {

	private static String path;
	
	public static void main(String[] args) throws IOException {
		new ImageResize().run();
	}

	private void run() throws IOException {
		// read file from class-path
		// From ClassLoader, all paths are "absolute" already - there's no context from which they could be relative. Therefore you don't need a leading slash.
		InputStream in = this.getClass().getClassLoader()
				.getResourceAsStream("lauterbrunnen.jpg");
		// From Class, the path is relative to the package of the class unless you include a leading slash, so if you don't want to use the current package, include a slash like this:
		//InputStream in = this.getClass().getResourceAsStream("/SomeTextFile.txt");
		
		// path for output files
		path = System.getProperty("user.dir").concat(File.separatorChar+"target"+File.separatorChar+"resized_pics");
		System.out.println(path);
		
		BufferedImage image = ImageIO.read(in); 
		
		resizeImage(image, 800, 600); // original pic: 2000x1328 ; 641 KB
		
		webP(image, 800, 600);
		
		/* My test (size no representa tamaño del fichero)
		java.awt.graphics2D: took ms: 74 | size: 1.920.000
		Image#getScaledInstance: took ms: 185 | size: 1.920.000
		Imgscalr: took ms: 161 | size: 1.699.200
		Thumbnailator: took ms: 196 | size: 5.097.600
		Marvin: took ms: 325 | size: 1.920.000
		
		 * Tutorial
	    java.awt.Graphics2D – 34ms
	    Image.getScaledInstance() – 235ms
	    Imgscalr – 143ms
	    Thumbnailator –  547ms
	    Marvin – 361ms
		*/
	}

	private void resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
		
		//JPG, BMP, JPEG, WBMP, PNG, and GIF.
		
		long init;
		
		// :::::::: Java
		
		// :::::::: java.awt.graphics2D

		init = System.currentTimeMillis();
	    BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB); // https://docs.oracle.com/javase/8/docs/api/java/awt/image/BufferedImage.html
	    Graphics2D graphics2D = resizedImage.createGraphics();
	    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); // https://docs.oracle.com/javase/tutorial/2d/advanced/quality.html
	    graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
	    graphics2D.dispose();
	    System.out.println("java.awt.graphics2D: took ms: " + (System.currentTimeMillis()-init) + " | size: " + (((long)resizedImage.getData().getDataBuffer().getSize())*4l));
	    ImageIO.write(resizedImage, "jpg", new File(path,"java_awt_g2d.jpg"));
	    
	    
	    // :::::::: Image#getScaledInstance
	    
	    init = System.currentTimeMillis();
	    BufferedImage resizedImage2 = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
	    Graphics2D graphics2D2 = resizedImage2.createGraphics(); //.getGraphics()
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT); // https://docs.oracle.com/javase/8/docs/api/java/awt/Image.html
	    graphics2D2.drawImage(resultingImage, 0, 0, null);
	    System.out.println("Image#getScaledInstance: took ms: " + (System.currentTimeMillis()-init) + " | size: " + (((long)resizedImage2.getData().getDataBuffer().getSize())*4l));
	    ImageIO.write(resizedImage2, "jpg", new File(path,"image_getscaledinstance.jpg"));
	    
	    
	    // :::::::: 3rd party
	    
	    // :::::::: Imgscalr (uses Graphics2D in the background)
	    // It's also possible to define additional resize properties that will provide us with logging or direct the library to do some color modifications on the image (make it lighter, darker, grayscale, and so on).
	    
	    init = System.currentTimeMillis();
	    //return Scalr.resize(originalImage, targetWidth); // This approach will keep the original image proportions and use default parameters – Method.AUTOMATIC and Mode.AUTOMATIC.
	    BufferedImage resizedImage3 = Scalr.resize(originalImage, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
	    System.out.println("Imgscalr: took ms: " + (System.currentTimeMillis()-init) + " | size: " + (((long)resizedImage3.getData().getDataBuffer().getSize())*4l));
	    ImageIO.write(resizedImage3, "jpg", new File(path,"imgscalr.jpg"));
	    
	    
	    // :::::::: Thumbnailator
	    
	    init = System.currentTimeMillis();
	    BufferedImage resizedImage4; 
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    Thumbnails.of(originalImage)
	        .size(targetWidth, targetHeight)
	        .outputFormat("JPEG")
	        .outputQuality(1)
	        .toOutputStream(outputStream);
	    byte[] data = outputStream.toByteArray();
	    ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
	    resizedImage4 = ImageIO.read(inputStream);
	    System.out.println("Thumbnailator: took ms: " + (System.currentTimeMillis()-init) + " | size: " + (((long)resizedImage4.getData().getDataBuffer().getSize())*4l));
	    ImageIO.write(resizedImage4, "jpg", new File(path,"thumbnailator.jpg"));
	    
	    //option for batch processing
	    /*Thumbnails.of(new File("path/to/directory").listFiles())
			    .size(300, 300)
			    .outputFormat("JPEG")
			    .outputQuality(0.80)
			    .toFiles(Rename.PREFIX_DOT_THUMBNAIL);*/
	    
	    
	    // :::::::: Marvin
	    // a handy tool for image manipulation and it offers a lot of useful basic (crop, rotate, skew, flip, scale) and advanced (blur, emboss, texturing) features.
	    
	    init = System.currentTimeMillis();
	    BufferedImage resizedImage5; 
	    MarvinImage image = new MarvinImage(originalImage);
	    Scale scale = new Scale();
	    scale.load();
	    scale.setAttribute("newWidth", targetWidth);
	    scale.setAttribute("newHeight", targetHeight);
	    scale.process(image.clone(), image, null, null, false);
	    resizedImage5 = image.getBufferedImageNoAlpha();
	    System.out.println("Marvin: took ms: " + (System.currentTimeMillis()-init) + " | size: " + (((long)resizedImage5.getData().getDataBuffer().getSize())*4l));
	    ImageIO.write(resizedImage5, "jpg", new File(path,"mavrin.jpg"));
	    
	}
	
	
	/** RESOURCES:
	    Backend:
	 	https://developers.google.com/speed/webp/
		https://developers.google.com/speed/webp/download
		https://developers.google.com/speed/webp/docs/precompiled
		https://formulae.brew.sh/formula/webp
		https://bitbucket.org/luciad/webp-imageio/src/default/
		https://github.com/nintha/webp-imageio-core (includes examples)
		Android:
		https://developer.android.com/studio/write/convert-webp
	 * */
	// Exception in thread "main" java.lang.UnsatisfiedLinkError: com.luciad.imageio.webp.WebPEncoderOptions.createConfig()J
	private void webP(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        
        // Obtain a WebP ImageWriter instance
        ImageWriter writer = ImageIO.getImageWritersByMIMEType("image/webp").next();

        // Configure the output on the ImageWriter
        writer.setOutput(new FileImageOutputStream(new File(path, "webp.webp")));

        // Configure encoding parameters
        //WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
        //writeParam.setCompressionMode(WebPWriteParam.MODE_DEFAULT);

        // Encode
        long init = System.currentTimeMillis();
        //writer.write(null, new IIOImage(originalImage, null, null), writeParam);
        System.out.println("WebP: took ms: " + (System.currentTimeMillis() - init));
	}

}
