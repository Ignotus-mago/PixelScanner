package net.paulhertz.scanner;

public class BitmapTransformations {
	
	
	// ------------- STATIC METHODS USING INDEX REMAPPING OF PIXELS ------------- //
	
	
	public static void rotate90Clockwise(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = height - 1 - y;
                int newY = x;
                int j = newX + newY * height;
                newPixels[j] = pixels[i];
            }
        }
    }

    public static void rotate90Counterclockwise(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = y;
                int newY = width - 1 - x;
                int j = newX + newY * height;
                newPixels[j] = pixels[i];
            }
        }
    }

    public static void rotate180(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = width - 1 - x;
                int newY = height - 1 - y;
                int j = newX + newY * width;
                newPixels[j] = pixels[i];
            }
        }
    }
    
    public static void reflecVertically(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width; // Index in the original array
                int newX = width - 1 - x; // Calculate the reflected x-coordinate
                int newY = y;
                int j = newX + newY * width; // Index in the new array
                newPixels[j] = pixels[i]; // Copy the pixel value to the new array
            }
        }
    }

        public static void reflectHorizontally(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = width - 1 - x;
                int newY = y;
                int j = newX + newY * width;
                newPixels[j] = pixels[i];
            }
        }
    }

    public static void reflectDiagonallyPrimary(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = y;
                int newY = x;
                int j = newX + newY * height;
                newPixels[j] = pixels[i];
            }
        }
    }
    
    public static void reflectDiagonallySecondary(int[] pixels, int[] newPixels, int width, int height) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = x + y * width;
                int newX = height - 1 - y;
                int newY = width - 1 - x;
                int j = newX + newY * height;
                newPixels[j] = pixels[i];
            }
        }
    }

}
