PixelScanner is a Processing Library that provides an interface and various implementing classes for moving between 2D bitmaps and 1D arrays in pixel orders determined by space-filling curves such as a zigzag pattern, a Hilbert curve, and a Moore curve. It works the other way around, too: you can turn a 1D signal (including live or recorded audio) into a 2D image. I created it specifically to aid in a project that makes extensive use of Hilbert and Moore curves for creating a sort of _color organ_ where sine waves determine the mix of pixel values (i.e., colors).

PixelScanner is developed from the [**Processing Library Template**](https://github.com/processing/processing-library-template), traces of which will undoubtedly persist until I have everything edited. 

I am developing this library to aid in developing several small apps that move between 1D signals and 2D images in a consistent way. I figured keeping the code in a library would assure that. It will also re-educate me on how to create libraries for Processing with the new template. The experience will help me to plan the next release of [**IgnoCodeLib**](https://github.com/Ignotus-mago/IgnoCodeLib3), an extensive library for P3+ featured on the Processing Libraries Page at [processing.org](https://processing.org) and in the P3 distribution. 

For people who would like to experiment with methods for proceeding from 1D signals to 2D images in various interesting ways, this library should at least provide a place to get started. Here's a sample video (https://www.flickr.com/photos/ignotus/52132647816/) of my project for [150 Media Stream](https://150mediastream.com/). I expect the sample code to include the following as-yet-unreleased examples: 

- [**HilbertCompAnimation**](https://github.com/Ignotus-mago/Campos/tree/main/HilbertCompAnimation) Sine waves in different frequencies animated over a Hilbert or Moore curve, a specific example of animation over a space-filling curve. Now with a nice GUI, and JSON data storage for your favorite settings.
- [**PixelAudio**](https://github.com/Ignotus-mago/Campos/tree/main/PixelAudio) Recorded audio is mapped to a space-filling curve. You can trigger the audio by clicking anywhere in the bitmap, leaving a mark on the image. The selected audio can be passed to a signal processing chain.
- [**HilberIntervalAnimation**](https://github.com/Ignotus-mago/Campos/tree/main/HilbertIntervalAnimation) Animates sequences of pixels over a Hilbert, Moore, or other space-filling curve.
- [**Simplex3Clouds**](https://github.com/Ignotus-mago/Campos/tree/main/Simplex3Clouds) Scaled simplex noise generates animated clouds. The clouds change and are displaced vertically to give an illusion of flight. They can be used as source material for the other apps.


