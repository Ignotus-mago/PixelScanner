package net.paulhertz.scanner;

import java.util.ArrayList;
import java.util.Arrays;

public class DiagonalZigzagGen extends PixelMapGen {
	private int w;
	private int h;
	private int len;
	int[] pixelMap;
	int[] sampleMap;
	ArrayList<int[]> coords;

	
	public DiagonalZigzagGen(int width, int height) {
		super(width, height);
	}
	
	
	
	@Override
	public String describe() {
		return "Diagonal zigzag map starting at (0,0). Moves first to (0,1) then proceeeds diagonally from edge to edge. "
				+ "Any width and height greater than 2 are valid for the constructor DiagonalZigzagGen(int width, int height).";
	}

	/**
	 * Always returns true, no restrictions on width and height. 
	 */
	@Override
	public boolean validate(int width, int height) {
		// any width and height > 2 will work
		if (width < 2 || height < 2) {
			System.out.println("width and height must be greater than 1.");
			return false;
		}
		return true;
	}

	/**
	 * Initialize this.coords and this.pixelMap.
	 * @return  this.pixelMap, the value for PixelAudioMapper.signalToImageLUT. 
	 */
	@Override
	public int[] generate() {
		int p = 0;
		int i = 0;
		this.pixelMap = new int[this.h * this.w];
		this.sampleMap = new int[this.h * this.w];
		this.coords = this.generateCoordinates();
		for (int[] loc : this.coords) {
			p = loc[0] + loc[1] * w;
			this.pixelMap[i++] = p;
		}
		for (i = 0; i < w * h - 1; i++) {
			this.sampleMap[this.pixelMap[i]] = i;
		}		
		return pixelMap;
	}

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
	
	protected ArrayList<int[]> generateCoordinates() {
		this.coords = this.generateZigzagDiagonalCoordinates(this.w, this.h);
		return this.coords;
	}
	
	
	/**
	 * @param width
	 * @param height
	 * @return 
	 */
	private ArrayList<int[]> generateZigzagDiagonalCoordinates(int width, int height) {
		ArrayList<int[]> coordinates = new ArrayList<>();
		int x = 0, y = 0;
		boolean movingUp = false;
		while (x < width && y < height) {
			coordinates.add(new int[]{x, y});
			if (movingUp) {                  	// movingUp is true, diagonal step is x++, y--
				if (x == width - 1) {          	// we hit the right edge
					y++;                        // move down 1 
					movingUp = false;           // flip the diagonal direction
				} 
				else if (y == 0) {             	// we hit the top edge
					x++;                        // move right 1 
					movingUp = false;           // flip the diagonal direction
				} 
				else {                          // diagonal step
					x++;                        // move right 1
					y--;                        // move up 1
				}
			} 
			else {                            	// movingUp is false,  diagonal step is x--, y++
				if (y == height - 1) {          // we hit the bottom edge
					x++;                        // move right 1
					movingUp = true;            // flip the diagonal direction
				} 
				else if (x == 0) {              // we hit the left edge
					y++;                        // move down 1
					movingUp = true;            // flip the diagonal direction
				} 
				else {                          // diagonal step
					x--;                        // move left 1
					y++;                        // move down 1
				}
			}
		}
		return coordinates;
	}
	
	
}
