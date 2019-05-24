# Dashboard analyzer

Dashboard analyzer is a Java application which provides tools for processing and analyzing of screenshots of dashboard user interfaces. It was created as part of my dissertation at Brno University of Technology: http://www.fit.vutbr.cz/~ihynek/dashboards/

![dashapp_draw](https://user-images.githubusercontent.com/1479229/58312655-0cbe0d80-7e0c-11e9-8ac2-26de3fea2576.png)
![dashapp_metrics](https://user-images.githubusercontent.com/1479229/58312656-0cbe0d80-7e0c-11e9-8c26-57e98092f466.png)
![dashapp_segmentation](https://user-images.githubusercontent.com/1479229/58312657-0cbe0d80-7e0c-11e9-8186-e326bc93402d.png)
![dashapp_download](https://user-images.githubusercontent.com/1479229/58312654-0cbe0d80-7e0c-11e9-9210-6420679d5071.png)

It provides the following functionality:
- loads images (from file or url)
- detects (automatically/manually using the interactive editor) regions which represent visually dominant objects (with respect to Gestalt laws / subjective perception of the user)
- performs image operations and filters (gray-scale, posterization, thresholding, Hough-transform, etc.), show image histograms
- performs pixel-based metrics analyzing UI characteristics (colorfulness, pixel-based balance, symmetry, etc...)
- performs object-based metrics analyzing UI characteristics (Ngo's metrics of aesthetics and their modifications)
- processes folders containing various UI descriptions of regions (analysis of subjective perception of users, generating heatmaps, etc.)

For more information, see the project wiki: https://github.com/Jirka/dash/wiki

## Running and building the application

### Prerequisites

- Java >=1.8
- git
- Eclipse IDE for Java Developers (http://www.eclipse.org/downloads/packages/release)

TODO: convert to maven projects

### Import and running the application

1. Clone the repository:
  - Eclipse IDE: Window > Perspective > Open Perspective > Git > Clone Git repository (in Git Repositories View)
  - or: 'git clone https://github.com/Jirka/dash.git' command
  - or: download and unpack zip file manually
2. Import projects into Eclipse IDE:
  - File > Import > General > Existing projects into workspace > select all projects without the 'dash' root project
  - wait until projects are successfully  builded
3. Run the application:
  - run DashApp.java file located in the projects **dashapp.rel.public** (Right click on the file in Package explorer view > Run as > Java application);
    - note that there are other runnable realeases -- e. g., **dashapp.rel.*** used for specific purposes and demonstrations of the application
  
### Build .jar executable file

1. Export a .jar file using Eclipse IDE:
  - open export dialog: File > Export > Java > Runnable JAR file
  - select launch configuration (the DashAppFile from previous section)
  - specify Export destination using File browser (e.g.: home/user/Desktop/dashapp.jar)
  - check 'Package required libraries into generated JAR'
  - select Finish
2. Run the generated .jar file:
  - Windows: double click on .jar file
  - Linux: run 'java -jar dashapp.jar' (do not forget to set permissions: 'chmod +x dashapp.jar')

## Usage

See the project wiki: https://github.com/Jirka/dash/wiki
