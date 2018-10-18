# Dashboard analyzer

Dashboard analyzer is an Java application which provides tools for processing and analyzing of screenshots of dashboard user interfaces. It was created as part of my dissertation in Brno University of Technology: http://www.fit.vutbr.cz/~ihynek/dashboards/

It provides following functionality:
- loads images (from file or url)
- detects (automatically/manually using the interactive editor) regions which represent visually dominant objects (with respect to Gestalt laws / subjective perception of the user)
- performs image operations and filters (gray-scale, posterization, thresholding, Hough-transform, etc.), show image histograms
- performs pixel-based metrics analyzing UI characteristics (colorfulness, pixel-based balance, symmetry, etc...)
- performs object-based metrics analyzing UI characteristics (Ngo's metrics of aesthetics and their modifications)
- processes folders containing various UI descriptions of regions (analysis of subjective perception of users, generating heatmaps, etc.)

For more information, see the project wiki: https://github.com/Jirka/dash/wiki

## Running and building application

### Prerequisites

- Java >=1.8
- git
- Eclipse IDE for Java Developers (http://www.eclipse.org/downloads/packages/release)

TODO: convert to maven projects

### Import and running application

1. Clone the repository:
  - Eclipse IDE: Window > Perspective > Open Perspective > Git > Clone Git repository (in Git Repositories View)
  - or: 'git clone https://github.com/Jirka/dash.git' command
  - or: download and unpack zip file manually
2. Import projects into Eclipse IDE:
  - File > Import > General > Existing projects into workspace > select all projects without the 'dash' root project
  - wait until projects are successfully  builded
3. Run the application:
  - run DashApp.java file located in the projects **dashapp.rel.public** (Right click on the file in Package explorer view > Run as > Java application)
  
### Build .jar executable file

1. Export .jar file using Eclipse IDE:
  - open export dialog: File > Export > Java > Runnable JAR file
  - select launch configuration (the DashAppFile from previous section)
  - specify Export destination using File browser (e.g.: home/user/Desktop/dashapp.jar)
  - check 'Package required libraries into generated JAR'
  - select Finish
2. Run generated .jar file:
  - Windows: Double click on .jar file
  - Linux: 'java -jar dashapp.jar' (do not forget to set permissions: 'chmod +x dashapp.jar')

## Usage

See the project wiki: https://github.com/Jirka/dash/wiki
