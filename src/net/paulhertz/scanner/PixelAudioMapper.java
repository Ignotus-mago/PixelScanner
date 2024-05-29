/**
 * 
 */
package net.paulhertz.scanner;

import processing.core.*;

/**
 * Parent class for child classes that map a 1D "signal" array to a 2D "image" array. 
 * 
 * 
 * DATA REPRESENTATION
 * 
 * For the sake of generality, the types for image and audio data are implemented outside this class. 
 * In Processing, PImage wraps image data. I have been using the minim library for audio, but the
 * built-in audio in Processing 4 is definitely an option. 
 * 
 * The image pixels are represented as an array of ints, and the audio samples are represented as 
 * an array of floats. Processing.core.PImage.pixels can supply an int array. Whatever audio class 
 * you use can supply the float array.
 * 
 * The image int array required to be a standard 24- or 32-bit RGB or RGBA bitmap image, in row major 
 * order, with (0,0) at upper left corner. The signal array is typically a floating point array of
 * values in the range [-1.0,1.0], a standard format for audio values. It is up to the implementation 
 * to devise methods for translating values from signal to image and vice versa: unless my default
 * methods suit your purpose, you should override the two transcode() methods. 
 * 
 *
 * Image
 *	 Width w, Height h
 *	 Index values {0..(w * h - 1)} (into pixel array)
 *	 Index to coordinate conversion for row major order with index i, width w, height h:
 *		i = x + w * y;
 *		x = i % w; y = floor(i/w);
 *	 Default data format: 24-bit RGB or 32-bit RGBA, for display from a bitmap to a computer monitor.
 *   RGBA includes an alpha channel A.
 *
 * Signal
 *	 Array with same cardinality as image data array {0..(w * h - 1)}
 *	 Default data format: floating point values in the range {-1.0..1.0}
 *
 * 
 * LOOKUP TABLES
 * 
 * At their most general, lookup tables or LUTs set up a one-to-one correspondence between two arrays
 * of the same cardinality, independent of the format of their data values. Every element in one array
 * corresponds to exactly one element in the other array. Starting from array A, for an element at index
 * A[i] we find the index of the corresponding element in array B at aToBLUT[i]. An element j in array B
 * has the index of its counterpart in array A at bToALUT[j]. 
 *
 * In PixelAudioMapper, we employ two LUTs, signalToImageLUT and imageToSignalLUT, to map elements in signal 
 * or image to the corresponding position in image or signal. 
 *
 *	signalToImageLUT: integer values over {0..(h * w - 1)} map a signal element index to a pixel array index
 *	imageToSignalLUT: integer values over (0..(h * w - 1)} map an image element index to a signal array index
 *
 * In signalToImageLUT, we can get the pixel index in the image for any index in the signal. 
 * In imageToSignalLUT, we can get index in the signal for any pixel index in the image. 
 *
 * Each array is the inverse of the other: for an array index i:
 *	
 *	signalToImageLUT[imageToSignalLUT[i]] == i;
 *	imageToSignalLUT[signalToImageLUT[i]] == i;
 *
 * Image data is always in row major order for PImage, our image data class. Signal values can be mapped 
 * to locations in the image in any arbitrary order, as long their coordinates traverse the entire image. 
 * A typical reordering might be a zigzag from upper left to lower right of an image, or a space-filling 
 * fractal, or even a randomly shuffled order. The coordinates are stored as indices (i = x + w * y)
 * into PImage.pixels[] in signalToImageLUT.
 * 
 * Once we know the “pixel index” for each value in the signal and have initialized signalToImageLUT, 
 * we can initialize imageToSignalLUT:
 *
 *	for (int i = 0; i < w * h - 1; i++) {
 *		imageToSignalLUT[signalToImageLUT[i]] = i;
 *	}
 *
 * Accordingly, we set up the PixelAudioMapper constructor to generate signalToImageLUT first and then generate 
 * imageToSignalLUT from signalToImageLUT.
 *  
 * Note that the same LUT could be used by multiple instances of PixelAudioMapper, as long as it is in effect
 * a static variable. The LUT for a 512 x 512 pixel Hilbert curve can be calculated once and copied to
 * or called by every PixelAudio instance that maps a signal to a 512 x 512 bitmap using a Hilbert curve. 
 * For this reason, we provide an interface, LUTGenerator, to implement LUT generation in a separate, shareable class.
 * 
 *
 * Mapping and Transcoding
 * 
 * We typically use the LUTs whenever we change the data in the signal or the image and want to write 
 * the new values to its counterpart, updating the appearance of the image or the sound of the audio signal. 
 * If the values in the arrays are in different formats, we will need to transcode the values from one 
 * format to the other. We have two methods, in pseudocode here:
 * 
 *	mapSigToImg		map signal values to the image: img[i] = transcode(sig[imgLUT[i]]);
 *	mapImgToSig		map image values to the signal: sig[i] = transcode(img[sigLUT[i]]);
 *
 * The img variable in the pseudocode corresponds to the pixel[] array of a PImage in our implementation. 
 * The sig variable corresponds to an array of floating point samples from an audio class. 
 * Implementation of the transcode() method is left to child instances of PixelArrayMapper.
 * 
 * In addition, we can write image or signal values directly, without using the LUTs. This operation transforms
 * the order of the pixel or signal values. 
 * 
 * 	writeImgToSig	write image values directly to the signal: sig[i] = transcode(img[i]);
 *	writeSigToImg	write signal values directly to the image: img[i] = transcode(sig[i]);	
 *
 * 
 * Reading and Writing Subarrays
 * 
 * When we want to work with subarrays of data from the signal or the image, it can be ordered either 
 * by the signal or image array order or by mapping with the corresponding LUT. In the case of images, 
 * we also have standard methods of reading and writing rectangular selections. We can define some 
 * methods to read and write data either in the order determined by the signal or by rectangular 
 * areas in the image. We’ll call the former methods pluck (read) and plant (write), and the latter 
 * peel (read) and stamp (write). The method signatures may be sufficient to determine the order of values 
 * when reading or writing, but it may clarify things to modify the name. 
 * 
 * When data to be written is not in the format of the target array, it will have to be transcoded first. 
 * In the following, intArray contains pixel data and floatArray contains signal data. 
 * Pos is an index into the signal, x and y are locations in the image. The length parameter is associated
 * with the array order of samples or pixels. The w and h parameters work with rectangular areas mapped 
 * from the image. When we call peel or stamp on signal data, length should be equal to w * h for 
 * a given rectangular area of the image.
 * 
 *	PLUCK
 *	copy in signal order	fromChannel			Return 		Notes
 *  --------------------------------------------------------------------------------------------------------------------------
 *	pluckPixels()			R, G, B, H, S, L	int[]		return RGB or channel as ints in signal order
 *	pluckPixelsFloat()		R, G, B, H, S, L	float[]		return transcoded RGB, HSL, or channel as floats in signal order
 *	pluckSamples()			n/a					float[] 	return float[] in signal order
 *	pluckSamplesInt()		n/a					int[]		transcode floats and return int[] in signal order
 *
 *	PLANT
 *	paste in signal order	values		toChannel			Notes
 *  --------------------------------------------------------------------------------------------------------------------------
 *  plantPixels()			int[]		R, G, B, H, S, L    paste to RGB by default, otherwise plant to channel
 * 							float[]		R, G, B, H, S, L    transcode and paste to selected channel or gray RGB
 *  plantSamples()			float[]		n/a				    default for float[]
 *  						int[]		n/a					transcode supplied int[]
 *  
 *	PEEL
 *	copy in image order		fromChannel			Return		Notes
 *  --------------------------------------------------------------------------------------------------------------------------
 *	peelPixels()			R, G, B, H, S, L	int[]		return a rectangular area of pixel values from image
 *	peelPixelsFloat()		R, G, B, H, S, L	float[]		transcode and return a rectangular area of pixel values as floats
 *	peelSamples()			n/a					float[]		default: return a rectangular area of signal values
 *	peelSamplesInt()		n/a					int[]		transcode and return a rectangular area of signal values as ints
 *
 *
 *	STAMP
 *	paste in image order	values		toChannel			Notes
 *  --------------------------------------------------------------------------------------------------------------------------
 *  stampPixels()			int[]		R, G, B, H, S, L    paste RGB or channel values to a rectangular area of the image
 * 	stampPixelsFloat()		float[]		R, G, B, H, S, L    transcode float[] to int[], defaults to gray
 *  stampSamples()			int[]		n/a		       		int[] always gets transcoded, paste values to a
 *  stampSamplesInt()		float[]		n/a					rectangular area of the signal
 *  
 *	
 *	int[]   pluckPixels(x, y, length, fromChannel)			source = image, ordered by signal, return int[]
 *	float[] pluckPixelsFloat(x, y, length, fromChannel)		source = image, ordered by signal, return transcoded float[]
 *	float[] pluckSamples(pos, length)						source = signal, ordered by signal, return float[]
 *	int[]	pluckSamplesInt(pos, length)					source = signal, ordered by signal, return transcoded int[]
 *
 *	plantPixels(x, y, int[], toChannel)						target = image, ordered by signal
 *	plantPixels(x, y, float[], toChannel)			        target = image, ordered by signal, transcode to int values
 *	plantSamples(pos, length, float[])					    target = signal, ordered by signal
 *	plantSamples(pos, length, int[])					    target = signal, ordered by signal, transcode to float values
 *	
 *	int[]	peelPixels(x, y, w, h)					        source = image, ordered by image, return int[]
 *	float[]	peelPixelsFloat(x, y, w, h)				    	source = image, ordered by image, return transcoded float[]
 *	float[]	peelSamples(pos, length);			    		source = signal, ordered by image, return float[]
 *	int[]	peelSamplesInt(pos, length);			    	source = signal, ordered by image, return transcoded int[]
 *
 *	stampPixels(x, y, w, h, int[], toChannel)				target = image, ordered by image
 *	stampPixelsFloat(x, y, w, h, float[], toChannel)		target = image, ordered by image, transcode to int values
 *	stampSamples(pos, length, float[])		    			target = signal, ordered by image
 *	stampSamplesInt(pos, length, int[])		    			target = signal, ordered by image, transcode to int values
 * 
 * 
 * Other Operations
 * 
 * The following are suggestions for methods that could be implemented in children of PixelArrayMapper.
 * 
 * Standard operations we can perform with the signal array:
 *   shiftLeft()		an array rotation where index values decrease and wrap around at the beginning
 *   shiftRight()		an array rotation where index values increase and wrap around at the end
 *   
 * Other operations that can be implemented by child classes:
 *	 audio synthesis operations
 * 	 phase shifting, amplitude modulation, etc. 
 *   FFT operations on both image and signal data
 *   pixel sorting, typically on image data
 *   blur, sharpen, etc.
 *   blending images
 *   mixing signals
 *   
 * As a rule, operations on the signal should be followed by writing to the image, and operations
 * on the image should be followed by writing to the signal. This will keep the values synchronized, 
 * even though they have different numerical formats. 
 * 
 * In most of the examples that accompany this library, audio data uses the Lightness channel of an 
 * HSL representation of the image's  RGB data, but this is by no means the only way of doing things. 
 * Using the Lightness channel  restricts audio data to 8 bits, apt for glitch esthetics, but noisy. 
 * It's also possible to  maintain high resolution data in the signal by processing image and audio 
 * data separately, and  writing audio data to the image but not in the other direction.
 * 
 * Finally, it bears mentioning that the image can be treated as simply an interface into an audio
 * buffer, where events such as mouse clicks or drawing and animation trigger audio events but do not
 * modify the audio buffer. Library examples will provide some suggestions for this strategy.
 * 
 * 
 */
public class PixelAudioMapper {
	// necessary instance variables
	/** The image for signal to image mapping. Note that the pixel values are accessed through the pixels[] array. */
	protected int[] img;
	/** image width */
	protected int w;
	/** image height */
	protected int h;
	/** pixel array and signal array length, equal to w * h */
	protected int len;
	/** The signal for signal to image mapping */
	protected float[] sig;
	/** Lookup table to go from the signal to the image: index values over {0..(h * w - 1)} 
	 * point to a corresponding index position in the image array img.pixels[] */
	protected int signalToImageLUT[];
	/** Lookup table to go from the image to the signal: index values over {0..(h * w - 1)} 
	 * point to a corresponding index position in the signal array sig[] */
	protected int imageToSignalLUT[];
	
	/** List of available color channels, "L" for lightness, since "B" for brightness is taken */
	public static enum ChannelNames {
		RGB, R, G, B, H, S, L;
	}

	

	/**
	 * Basic constructor for PixelAudio, sets up all variables. 
	 * @param w
	 * @param h
	 */
	public PixelAudioMapper(int w, int h, int[] img, float[] sig) {
		this.w = w;
		this.h = h;
		this.len = w * h;
		this.img = img;
		this.sig = sig;
		this.signalToImageLUT = this.generateSignalToImageLUT();
		this.generateImageToSignalLUT(signalToImageLUT);
	}

	
	//------------- Dimensions -------------//
	

	
	/** @return the width of the image */
	public int getWidth() {
		return this.w;
	}
	
	/** @return the height of the image */
	public int getHeight() {
		return this.h;
	}
	
	/** @return the length of the signal array (== length of image pixel array and the LUTs) */
	public int getSize() {
		return this.len;
	}
	
	/** @return a string representation of our data, possibly partial */
	public String toString() {
		return "";
	}
	

	//------------- Image and Signal -------------//

	
	/**
	 * @return 	the image associated with this instance 
	 */
	public int[] getImage() {
		return this.img;
	}

	/**
	 * Warning: the size of new image must conform to the size the current image and signal arrays.
	 * @param img	the image to set
	 */
	public void setImage(int[] img) {
		this.img = img;
	}

	/**
	 * @return 	the signal associated with this instance of PixelAudioMapper
	 */
	public float[] getSignal() {
		return this.sig;
	}
	
	/**
	 * Sets a new signal array.
	 * Warning: the size of the new signal array must conform to the size the current image and signal arrays.
	 * @param sig	the signal array to set
	 */
	public void setSignal(float[] sig) {
		this.sig = sig;
	}
	

	//------------- LUTs -------------//

	
	/** @return the lookup table that maps an index in the signal to the corresponding pixel index in the image. */
	public int[] getSignalToImageLUT() {
		return this.signalToImageLUT;
	}
	
	/**
	 * Sets a new lookup table for mapping signal to image. 
	 * Warning: The size of sigLUT must conform to the size the current image and signal arrays.
	 * @param sigLUT
	 */
	protected void setSignalToImageLUT(int[] sigLUT) {
		
	}
	
	/** @return the lookup table that maps pixel values in the image to the corresponding entry in the signal. */
	public int[] getImageToSignalLUT() {
		return this.imageToSignalLUT;
	}
	
	/**
	 * Sets a new lookup table for mapping image to signal.
	 * Warning: the size of imgLUT must conform to the size the current image and signal arrays.
	 * @param imgLUT
	 */
	protected void setImageToSignalLUT(int[] imgLUT) {
		
	}
	
	/**
	 * Generate signalToImageLUT. 
	 */
	public int[] generateSignalToImageLUT() {
		
	}
	
	/**
	 * Called when signalToImageLUT has been initialized to generate imageToSignalLUT
	 */
	public void generateImageToSignalLUT(int[] sigLUT) {
		for (int i = 0; i < w * h - 1; i++) {
			this.imageToSignalLUT[sigLUT[i]] = i;
		}		
	}
		
	
	
	
//	/**
//	 * Writes values from an array of pixel values to an array of audio samples using imageToSignalLUT 
//	 * to map array positions. Formats values as required by the implementation. In this library, 
//	 * we typically map 24-bit RGB pixel values to floating point audio values between -1.0 and 1.0.
//	 * 
//	 * @param pix		the pixel array from the mapped image
//	 * @param sig		the signal array, companion to the mapped image
//	 */
//	abstract void mapToSignal(int[] pix, float[] sig);
//
//	/**
//	 * Writes values from an array of floating point numbers ("audio") to an image's pixel array
//	 * using signalToImageLUT to map array positions. The floating point number are typically in the
//	 * range -1.0 to 1.0, and should be mapped to the interval 0 to 255. Example implementations
//	 * usually use the Lightness channel in the HSL color space, but there are many other possibilities.
//	 * 
//	 * @param sig		the signal array, companion to the mapped image
//	 * @param pix		the pixel array from the mapped image
//	 */
//	abstract void mapToImage(float[] sig, int[] pix);
//	
//	/**
//	 * shuffle the pixels in the image using the signalToImageLUT
//	 * @param     img
//	 * @return    a new PImage with pixels from img rearranged using signalToImageLUT
//	 */
//	abstract PImage remapImage(PImage img);
//	
//	/**
//	 * shuffle the pixels in the signal using the imageToSignalLUT
//	 * @param     sig
//	 * @return    a new array with values from sig rearranged using imageToSignalLUT
//	 */
//	abstract float[] remapSignal(float sig);
	
}




////------------- do we still need these methods from the previous interface? Maybe. Or maybe they belong in a utility class -------------//
//we should also have methods to generate each LUT from the other 
//
//
///** @return the type of this scanner, read-only */
//public PixelScanner.ScannerType getScannerType();
//
///** flip the order of the x coordinates */
//abstract void flipX();
//
///** flip the order of the y coordinates */
//abstract void flipY();
//
///** swap the x and y coordinates in the map */
//abstract void swapXY();
//
///** swap all x and y coordinates */
//abstract void swapCoords();
//
///** generate coords for indexMap */ 		// <--- might be a keeper 
//abstract void generateCoords();
//
////------------- | -------------//



