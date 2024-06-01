package net.paulhertz.scanner;

import java.util.ArrayList;

/**
 * A mapping generator for two arrays with the same number of elements (same cardinality), 
 * used specifically for mapping 1D audio signals to 2D bitmaps. 
 * See the PixelAudioMapper class for details of the mapping. 
 * See the abstract PixMapGen class for a parent class.
 * See DiagonalZigzagGen for an example class. 
 */
public interface PixelMapGenINF {
	
	/**
	 * @return	A String describing the sort of mapping created by the generate() method and requirements for width and height, 
	 * 			along with any other requirements for initialization. 
	 */
	public String describe();
	
	/**
	 * @param width		the width in pixels of the bitmap supported by this mapping generator.
	 * @param height
	 * @return
	 */
	public boolean validate(int width, int height);

	/**
	 * The core method for the user to write. 
	 * @return the mapping from the 1D array (audio signal) to the 2D array (bitmap).
	 */
	public int[] generate();
		
	/**
	 * @return	the width of the bitmap array.
	 */
	public int getWidth();
	
	/**
	 * @return	the height of the bitmap array.
	 */
	public int getHeight();
	
}
