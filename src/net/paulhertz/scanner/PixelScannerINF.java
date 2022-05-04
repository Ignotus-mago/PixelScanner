/**
 * Interface for classes that scan the screen coordinates of an array of pixels 
 * in orders other than the usual left to right and top to bottom (x,y) scanlines.
 * Scans create one-dimensional arrays that are processed (perhaps as audio signals) 
 * and written back to the screen. 
 *
 */
package net.paulhertz.scanner;

/**
 * @author Ignotus_Mago
 *
 */
public interface PixelScannerINF {

	/** flip the order of the x cooordinates */
	abstract void flipX();

	/** flip the order of the y cooordinates */
	abstract void flipY();

	/** swap the x and y coordinates in the map */
	abstract void swapXY();

	/** swap all x and y coordinates */
	abstract void swapCoords();

	/** generate coords for indexMap */
	abstract void generateCoords();


	/**
	 * @param pix   an array of pixels
	 * @param w     width of the image represented by the array of pixels
	 * @param h     height of the image represented by the array of pixels
	 * @param x     x-coordinate of the location in the image to scan
	 * @param y     y-coordinate of the location in the image to scan
	 * @return      an array in the order determined by the scan
	 */
	abstract int[] pluck(int[] pix, int w, int h, int x, int y);

	/**
	 * @param pix      an array of pixels
	 * @param sprout   an array of d * d pixels to write to the array of pixels
	 * @param w        width of the image represented by the array of pixels
	 * @param h        height of the image represented by the array of pixels
	 * @param x        x-coordinate of the location in the image to write to
	 * @param y        y-coordinate of the location in the image to write to
	 */
	abstract void plant(int[] pix, int[] sprout, int w, int h, int x, int y);

	/** return a string representation of our data, possibly partial */
	abstract String toString();

	/** return a number representing recursion depth. If not applicable, return -1 */
	abstract int getDepth();

	/** return the width of the pixel array mapped in this PixelScannerINF instance */
	abstract int getBlockWidth();  

	/** return the height of the pixel array mapped in this PixelScannerINF instance */
	abstract int getBlockHeight();  

	/** return the number of pixel values mapped in this PixelScannerINF instance */
	abstract int getSize();

	/** return the index of a point in the mapped representation */ 
	abstract int lookup(int x, int y);

	/** return the x coordinate at a specified index in the map */
	abstract int xcoord(int pos);

	/** return the y coordinate at a specified index in the map */
	abstract int ycoord(int pos);

}
