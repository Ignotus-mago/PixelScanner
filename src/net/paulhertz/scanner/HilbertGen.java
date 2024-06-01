package net.paulhertz.scanner;

public class HilbertGen implements PixelMapGenINF {

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

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

}
