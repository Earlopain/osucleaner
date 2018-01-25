This program will clean up your osu folder. It shrinked my song folder size by almost 50%.

Please close osu while running thi program so you don't have to reprocess your beatmaps

Different mapsets:    15,451    Time to execute: 87s (on an ssd)

Files before:         356,673   Size before:     137GB

Files after:          93,458    Size after:      68.29GB
    
Difference:           263,215   Difference:      68.71GB

To use this tool either download the js file and execute it with nodejs or use the exe supplied in the rar archive.

If you want to compile it yourself:

1. Download java jdk from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html)
2. Add java to your path variables
3. Clone this repository
4. Run javac *.java
5. Run jar cfe osu!cleaner.jar Main *.class