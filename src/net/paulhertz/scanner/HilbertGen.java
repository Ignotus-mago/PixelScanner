package net.paulhertz.scanner;

import java.util.ArrayList;

public class HilbertGen extends PixelMapGen {
	private int w;
	private int h;
	private int len;
	private int[] pixelMap;
	private int[] sampleMap;
	private ArrayList<int[]> coords;

	public HilbertGen(int width, int height) {
		super(width, height);
	}

	@Override
	public String describe() {
		return "Creates a Hilbert curve over a square bitmap from upper left to upper right corner. Width and height must be the same power of 2.";
	}

	@Override
	public boolean validate(int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] generate() {
		// TODO Auto-generated method stub
		return null;
	}

}
