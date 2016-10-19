# ProBro

File Browser application with some features for project management. 

This application basically is a file browser, featuring a file system tree and a table, as well as a details panel for file browsing. It allows to load deep info of the selected folder, which enables the user to have all folder sizes correct, which is very useful when cleaning up your hard disk.

For project administration of projects of any kind, there are some extra features:

In the projects view (See View Menu) and the user selects "select project info", the program scans the current folder recursively and tries to find a specific pattern of folders/files inside the subfolders which are defined as project structures in the ProBro XML configuration file.

You can define the project structure you typically use in this XML file (see class ProjectDefinition for details). The very use case for me was to scan for DAW projects made with ProTools and Logic Pro. I always have at least one of the folders named _MA (Masters), _MIX (Mixes), etc. in my audio projects, so these are defined as qualifiers for project folders. Also, the audio files (defined by file extensions) and session files (dito) are defined in XML, as well as different coloration for different file types, which can be used to show the existence of compressed audio files for example.

NOTE: The application currently is in alpha state, and not usable yet. When the project is in beta testing (with myself as the main beta tester), i will note that again in this README file. 
