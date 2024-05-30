package net.paulhertz.scanner;

import java.util.ArrayList;

public interface PixelMapGenINF {
	
	public String describe();
	
	public boolean validate(int width, int height);

	public int[] generate();
		
	public int getWidth();
	
	public int getHeight();
	
}
