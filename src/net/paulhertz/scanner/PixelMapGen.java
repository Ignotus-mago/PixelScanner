package net.paulhertz.scanner;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PixelMapGen implements PixelMapGenINF {
	private int w;
	private int h;
	private int len;
	int[] pixelMap;
	int[] sampleMap;
	ArrayList<int[]> coords;

	
	public PixelMapGen(int width, int height) {
		// TODO throw an exception instead? This is not the usual way of handling errors in Processing.
		if (!validate(w,h)) {
			System.out.println("Error: Validation failed");
			return;
		}
		this.w = width;
		this.h = height;
		this.len = h * w;
	}
	
	/* ---------------- USER MUST SUPPLY THESE METHODS ---------------- */
	/* describe(), validate(width, height), generate() */
	
	
	@Override
	public abstract String describe();

	@Override
	public abstract boolean validate(int width, int height);

	/**
	 * Initialize this.coords, this.pixelMap, and this.sampleMap.
	 * @return  this.pixelMap, the value for PixelAudioMapper.signalToImageLUT. 
	 */
	@Override
	public abstract int[] generate();
	
		
	/* ------------------------------ GETTERS ADN NO SETTERS ------------------------------ */
	/* For the most part, we don't want to alter variables once they have been initialized. */

	
	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public int getHeight() {
		return h;
	}
	
	public int getSize() {
		return len;
	}
	
	public int[] getPixelMap() {
		if (this.pixelMap == null) {
			return this.generate();
		}
		return this.pixelMap;
	}
	
	public int[] getSampleMap() {
		if (this.sampleMap == null) {
			this.generate();
		}
		return this.sampleMap;
	}
	
	public int[] getPixelMapCopy() {
		if (this.pixelMap == null) {
			return this.generate();
		}
		return Arrays.copyOf(pixelMap, len);
	}
	
	public int[] getSampleMapCopy() {
		if (this.sampleMap == null) {
			this.generate();
		}
		return Arrays.copyOf(sampleMap, len);
	}

	public ArrayList<int[]> getCoordinates() {
		if (this.coords == null) {
			this.generate();
		}
		return this.coords;
	}
	
	/**
	 * @return 	An ArrayList<int[]> of bitmap coordinates in the order the signal mapping would visit them. 
	 * 			It is designed to be inaccessible outside its package, but you can have it call a public method
	 * 			that passes it the necessary values. See DiagonalZigzagGen for an example. 
	 * 
	 */
	protected abstract ArrayList<int[]> generateCoordinates();
	

}
