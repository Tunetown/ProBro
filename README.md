# ProBro - The Project Browser

Project Browser is a file system explorer/browser application with some features for project management, built upon a simple Java file browser implemented in Swing. 

This application basically is a file browser, featuring a file system tree and a folder content table where you can select and display your file system, as well as a details panel for a selected file/folder. It additionally allows to load deep info of the selected folder with a button click, which enables the user to have all folder sizes determined recursively, which is very useful when cleaning up your hard disk for example.

For project administration of projects of any kind, there are some extra features:

In the projects view (see View menu), after the user selected a folder in the tree and then clicked the button "select project info", the program scans the currently selected folder recursively and tries to find a specific pattern of folders/files which are defined as project structures in the ProBro XML configuration file. These are shown in the table in projects view.

You can define the project structure you typically use with a XML file, which is initially contained with the application with a default project structure. If you want to define your own project structure(s), you can copy the file somewhere and alter it like you wish. The definition can then be loaded via the Projects menu. On Mac OS X, you find the file inside the ProBro.app package, under ProBro.app/Contents/Java/ProBroDefaultProjectDef.xml, on all other platforms, you have downloaded the file anyway during installation. Details on the syntax are documented below in this file.   

The use case for me (and the reason this application has been developed) was to scan for DAW music projects made with ProTools and Logic Pro, and count the contained mix or master audio files there. I always have at least one of the folders named _MA (Masters), _MIX (Mixes), etc. in my audio projects, so these are defined as qualifiers for folders being detected as project folders. Also, the audio files and session file formats recognized are defined in XML, as well as different colors for different file types etc., which can be used to show the existence of compressed audio files for example. Also, the folders which have NOT been recognized as projects and are not contained in a project can be shown with the projects leftovers view (see Projects menu). 

The given default XML configuration is my personal one, however, you can easily define your own project structures also for completely different kinds of projects like programming, webdesign, photography for example.  

## Features

- Basic file browsing (file browser view)
- Loading / visualizing the real size of any files/folders in the system
- Project overview of selected folder, generated by a XML definition of project structure and the files on the system (no temporary data is stored anywhere)
- Compressing selected items with the command line ZIP tool (not available in Windows yet), this can be done for multiple folders in parallel, also showing the progress in a task bar on the right side, which also can terminate the ZIP processes on double click. This helps archiving projects for example.
- Determination of the year of the project, by either extracting this from the project folder name, or by the earliest existing file in one of the project qualifying folders (a.k.a. project targets, see below)
- Project leftovers view, showing all folders which have NOT been detected (qualified) as projects and which are not part of a project themselves 

## Compatibility and Installation

Minimum required Java Runtime Environment Version: 1.8

#### Mac OS X
For Mac OS X (>= 10.8.5), a genuine DMG installer is available, see the project folder ProBro/release/macosx/bundles/. It also contains the project definition XML file, which is automatically loaded on program start.

#### Windows, Linux, ...
For all other platforms, the .jar file can be used (path: ProBro/release/macosx/ProBro.jar. Sorry, no .exe has been created yet... However, the application has been tested on Windows 7 and works flawlessly, except for the ZIP functionality which is only available on Unix-like systems.
Also, you will need the default project definition file, which should be downloaded from ProBro/release/macosx/ProBroDefaultProjectDef.xml and copied beneath the jar file.

## Further Details

#### Project Definition (XML)
Here is an example definition file like the one included in the repository:

```xml 
<?xml version="1.0" encoding="UTF-8"?>
<projectdefinition name="Tunetown Projects">
	<projectproperty header="_MA">
		<folder qualifying="true">_MA</folder>
		<file qualifying="true" bgcolor="0;230;230" tabletext="ZIP">_MA.zip</file>
		
		<extension recursive="true">wav</extension>
		<extension recursive="true">aif</extension>
		<extension recursive="true">aiff</extension>
		<extension recursive="true">sd2</extension>
		<extension recursive="true" bgcolor="255;160;0">mp3</extension>
		<extension recursive="true" bgcolor="255;160;0">wma</extension>
	</projectproperty>
	<projectproperty header="_MIX">
		<folder qualifying="true">_EM</folder>
		<folder qualifying="true">_MIX</folder>
		<folder qualifying="true">_MX</folder>
		<file qualifying="true" bgcolor="0;230;230" tabletext="ZIP">_EM.zip</file>
		<file qualifying="true" bgcolor="0;230;230" tabletext="ZIP">_MIX.zip</file>
		<file qualifying="true" bgcolor="0;230;230" tabletext="ZIP">_MX.zip</file>
		<extension recursive="true">wav</extension>
		<extension recursive="true">aif</extension>
		<extension recursive="true">aiff</extension>
		<extension recursive="true">sd2</extension>
		<extension recursive="true" bgcolor="255;160;0">mp3</extension>
		<extension recursive="true" bgcolor="255;160;0">wma</extension>
	</projectproperty>
	<projectproperty header="_PMA">
		<folder qualifying="true">_PMA</folder>
		<file qualifying="true"  bgcolor="0;230;230" tabletext="ZIP">_PMA.zip</file>  
		<extension recursive="true">wav</extension>
		<extension recursive="true">aif</extension>
		<extension recursive="true">aiff</extension>
		<extension recursive="true">sd2</extension>
		<extension recursive="true" bgcolor="255;160;0">mp3</extension>
		<extension recursive="true" bgcolor="255;160;0">wma</extension>
	</projectproperty>
	<projectproperty header="_PMX">
		<folder qualifying="true">_PMX</folder>
		<folder qualifying="true">_RUFF</folder>
		<file qualifying="true" tabletext="ZIP" bgcolor="0;230;230">_PMX.zip</file>
		<file qualifying="true" tabletext="ZIP" bgcolor="0;230;230">_RUFF.zip</file>
		<extension recursive="true">wav</extension>
		<extension recursive="true">aif</extension>
		<extension recursive="true">aiff</extension>
		<extension recursive="true">sd2</extension>
		<extension recursive="true" bgcolor="255;160;0">mp3</extension>
		<extension recursive="true" bgcolor="255;160;0">wma</extension>
	</projectproperty>
	<projectproperty header="Sessions">
		<extension recursive="true">logic</extension>
		<extension recursive="true">lso</extension>
		<extension recursive="true" bgcolor="200;200;200">ptf</extension>
		<extension recursive="true" bgcolor="200;200;200">pts</extension>
		<extension recursive="true" bgcolor="200;200;200">ptx</extension>
	</projectproperty>
	<projectproperty header="Audiofiles">
		<extension recursive="true">wav</extension>
		<extension recursive="true">aif</extension>
		<extension recursive="true">aiff</extension>
		<extension recursive="true">sd2</extension>
		<extension recursive="true" bgcolor="255;160;0">mp3</extension>
		<extension recursive="true" bgcolor="255;160;0">wma</extension>
	</projectproperty>
	<ignore>
		<extension>logic</extension>
		<extension>bak</extension>
		<extension>wb3</extension>
	</ignore>
</projectdefinition>
```

The project definition XML file must have the following structure, defined here by the XML tags. The main tag is **projectdefinition**, it has to be defined in the document. The first definition is parsed, all others are ignored.

#### Project Definition XML Syntax Reference

###### Main Tag **projectdefinition**
Defines a project definition, and contains all information about the definition.

Attributes:
- *name*: Name of the project definition (free text)

Can contain:
- Multiple tags **projectproperty** (at least one has to be defined) 
- One tag **ignore** (only one can be defined, all further ones are ignored)

###### Tag <projectproperty>

###### Tag **ignore**












It has to contain a main tag named "projectdefinition"
	- Attributes:
		- name: Name of the definition (not used currently)
		
- Inside this tag, there can be several "projectproperty" tags. Each of these will be evaluated for
  every file or folder inside a potential project folder. Also, each one will have a separate column in
  the projects view table, by default showing 0 if the property has been found or "-" if not (see below for details).
	- Attributes:
 		- header: Must be set to show the property in the table as a column.

- Inside each of these project properties, the following tags are parsed:
  - "folder": This defines a folder name. If this folder is found directly as child of a folder, the
              property is seen as found, and its target will be set to the found folder.
				- Attributes:
 					- qualifying: If set to true, the existence of the folder qualifies the project folder to 
 	               really be recognized as a project.
 *                      - bgColor: Defines the base back color for table cells, if the property has been found. Syntax: "r;g;b"
 *                      - tableText: This text, if set, is shown if the property has been found. If not specified,
 *                                   the column will show the amount of matching files, see "extension" tag.
 *                                   
 *   	- "file": This defines a file name. If this file is found directly as child of a folder, the
 *                property is seen as found, and its target will be set to the found folder.
 *                - Attributes: same as for property "folder"
 *                
 *      - "extension": These extensions define the file (or folder) extensions which will be counted in one of these cases:
 *                     1. If a folder tag exists, all defined extensions will be searched here, and the count is shown in the table (if no tableText is defined)
 *                     2. If a file tag exists, this is  
 *                     3. If no file or folder tags exist, files matching these extensions will be searched and counted directly in the project folder.
 *                     - Attributes:
 *                  		- qualifying: If set to true, the existence of a file of this type qualifies the project folder to 
 *                          	          really be recognized as a project.
 *                      	- bgColor: Defines the back color for table cells, if the extension has been found. The real back color
 *                                     will be between the base color (if set by the hitting folder tag) or the java standard table background color,
 *                                     the exact color being defined by the percentage of files with the extension. Syntax: "r;g;b"
 *                          - recursive: If set to true, files will be searched deeply inside the target (see "extension" tag description above), if not set,
 *                                       only first level files and folders will be scanned.
 * 
 * - Also, there can be a "ignore" tag. Inside this, you can specify "extension" tags, which will be ignored as project folders. This
 *   can be handy because also the leftovers list ignores these...and for example, Logic creates folders as sessions, so these
 *   would show up in the leftovers view.


# Release Note

The application currently is in beta state. There could likely be some bugs, if you come across any issues, please let me know. 
