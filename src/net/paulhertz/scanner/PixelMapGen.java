package net.paulhertz.scanner;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * Abstract class for handling LUT generation for PixelAudioMapper. PixelAudioMapper is designed to be independent of any specific mapping
 * between its audio and pixel arrays. It can handle any mapping via its LUTs. Keeping the LUT generation class outside PixelAudioMapper 
 * removes any dependencies on the particular mapping. 
 * 
 * 
 * 
 * NOTES
 * 
 * The PixelAudioMapper class handles the combinatorial math for mapping between two arrays whose elements are in one-to-one correspondence
 * but in different orders. This class, PixelMapGen, generates the mapping between the two arrays. PixelAudioMapper, as its name suggests,
 * considers one array to be floating point audio samples and other to be RGBA integer pixel data, but of course the relationship is 
 * completely arbitrary as far as the mapping goes. The mapping was given its own class precisely because it is generalizable, though 
 * PixelMapGen does assume that the cardinality of its arrays can be factored by width and height. 
 * 
 * One of the big questions for me to resolve just now is how tightly bound an instance of PixelMapGen should be to an instance of PixelAudioMapper. 
 * -- We want PixelMapGen to be reusable, i.e., not so tightly bound to PixelAudioMapper as to be inaccessible to other code. 
 * 	  - This suggests that PixelMapGen is initialized outside PixelAudioMapper then and passed to it. 
 * 	  - PixelAudioMapper relies on PixelMapGen to generate lookup tables (LUTs) that map an audio sample array to a pixel array.
 *    - Once PixelAudioMapper has initialized its LUTs, PixelMapGen can be passed to another PixelAudioMapper. 
 * So, should PixelMapGen act like a singleton, where the width and height values and the LUTs never vary, after they are generated? That's the second question.
 * -- Take into account that real-time multi-media performance is a goal of this library, so speedups are welcome
 *    - This is a major reason for using LUTs and arrays of coordinates, rather then math to update our arrays, map clicks on the image to audio offsets, etc.
 *    - LUTs also advance the potential for using OpenGL shaders to run the imaging routines, at some later time.
 *    - We also use threading to make response to timed and time-dependent more responsive. 
 *    - Threading is used when playing audio samples: mouse-clicks are used to find positions in an audio buffer
 * -- It seems to make more sense in a multi-threaded environment *not* to share resources so as not to arrive at conflicts.
 * 	  - Processing overhead in my applications is very low and PixelMapGen instances are only read from, not written to.
 * 	  - Nevertheless, it probably makes sense for each PixelAudioMapper to have its own copy of required LUTs, rather than calling a PixelMapGen instance.
 * 	  - PixelMapGen provides methods to copy its resources, which only need to be calculated once, and they can be obtained at application initialization.
 * 	  - We use more memory by replicating LUTs, but in current architectures that is hardly a problem. In any case, we can also call PixelMapGen if we prefer.
 * -- In real-time performance situations, initialing all required resources for the performance up front is desirable, anyhow. 
 * -- One more question: do we want a no-argument constructor for PixelMapGen? (answer tomorrow)
 * 
 * CONCLUSION
 * 
 * Create a PixelMapGen instance with assigned width and height, and LUTs created by the generate() processes. 
 * Initialize a PixelAudioMapper instance with the PixelMapGen instance, using copies of PixelMapGen resources.
 * Decouple PixelAudioMapper instances from PixelMapGen in all later method calls. 
 * But don't enforce this usage pattern with class structure, just make it the recommended practice (and see where that gets you).
 *    
 * 
 */
public abstract class PixelMapGen {
	private int w;
	private int h;
	private int len;
	int[] pixelMap;
	int[] sampleMap;
	ArrayList<int[]> coords;
	public final static String description = "Declare the description variable in your class and describe your PixelMapGen.";

	
	
	/**
	 * Constructor for classes that extend PixelMapGen. You will need to create you own constructor
	 * for your class, but it can just call super(width, height) if everything it does can be handled
	 * in your generate() method. Note that generate() is call on the last line of this constructor, 
	 * so if you need additional initializations or arguments for your class....
	 * 
	 * @param width
	 * @param height
	 */
	public PixelMapGen(int width, int height) {
		// TODO throw an exception instead? This is not the usual way of handling errors in Processing.
		if (!validate(w,h)) {
			System.out.println("Error: Validation failed");
			return;
		}
		this.w = width;
		this.h = height;
		this.len = h * w;
		this.generate();
	}
	
	
	
	/* ---------------- USER MUST SUPPLY THESE METHODS ---------------- */
	/* describe(), validate(width, height), generate() */
	
	
	/**
	 * @return 	A String describing the mapping generated by your class and any initialization requirements. 
	 */
	public abstract String describe();

	
	/**
	 * @param 	width
	 * @param 	height
	 * @return	true if the width and height parameters are valid for creating a mapping with this generator, 
	 * 			otherwise, false.
	 */
	public abstract boolean validate(int width, int height);

	/**
	 * Initialization method for coordinates (this.coords), signalToImageLUT (this.pixelMap), and 
	 * imageToSignalLUT (this.sampleMap) used by PixelAudioMapper classes.
	 * You must initialize this.coords, this.pixelMap, and this.sampleMap within generate() 
	 * or other methods that it calls: See DiagonalZigzagGen for an example. 
	 * @return  this.pixelMap, the value for PixelAudioMapper.signalToImageLUT. 
	 */
	public abstract int[] generate();
	
		
	/* ------------------------------ GETTERS AND NO SETTERS ------------------------------ */
	/* For the most part, we don't want to alter variables once they have been initialized. */

	
	
	/**
	 * @return 	Width of the bitmap associated with this PixelMapGen. 
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * @return 	Height of the bitmap associated with this PixelMapGen. 
	 */
	public int getHeight() {
		return h;
	}
	
	/**
	 * @return 	Size (width * height) of the bitmap associated with this PixelMapGen. 
	 */
	public int getSize() {
		return len;
	}
	
	/**
	 * @return	pixelMap value created by the generate() method.  
	 */
	public int[] getPixelMap() {
		return this.pixelMap;
	}
	
	public int[] getPixelMapCopy() {
		return Arrays.copyOf(pixelMap, len);
	}
	
	public int[] getSampleMap() {
		return this.sampleMap;
	}
	
	public int[] getSampleMapCopy() {
		return Arrays.copyOf(sampleMap, len);
	}

	public ArrayList<int[]> getCoordinates() {
		return this.coords;
	}
	
	public ArrayList<int[]> getCoordinatesCopy() {
		ArrayList<int[]> coordsCopy = new ArrayList<int[]>(len);
		for (int[] coord: this.coords) {
			coordsCopy.add(coord);
		}
		return coordsCopy;
	}	
		

}
