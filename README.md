I've stopped developing this plugin due to Matlabs plan to implement a new UI. 
FYI: Currently the code will not be updated, any Issues, known or unknown are *features*. If you however find this plugin still usefull be informed, that I do not visit GitHub often.


## Welcome to the Matlab-Editor-Plugin [wiki](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki)!

In short, it will mess around with Matlab's Editor.

Last supported version: [**R2021a**](https://github.com/GavriYashar/Matlab-Editor-Plugin/issues/159#issuecomment-1026671085)

### Edit Code
* **[ClipboardStack](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#clipboard-stack)**: opened via <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>V</kbd>, stores the previous 10 strings copied to clipboard from Matlab.
* **[Duplicate line](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#duplicate-or-remove-current-line)**:  <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>D</kbd>
* **[Delete line](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#duplicate-or-remove-current-line)**: <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>Y</kbd>
* **[Moving current lines up](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#moving-current-lines-up-or-down)**: <kbd>ALT</kbd> + <kbd>SHIFT</kbd> + <kbd>ARROW UP</kbd>
* **[Moving current lines down](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#moving-current-lines-up-or-down)**: <kbd>ALT</kbd> + <kbd>SHIFT</kbd> + <kbd>ARROW DOWN</kbd>
* **[LiveTemplatesViewer](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Live-Templates)**: <kbd>ALT</kbd> + <kbd>INSERT</kbd> view all created livetemplates
* **[Local History V1.35](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#local-history)**: <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>H</kbd> view local history of file

### Navigation
* **[Navigation History](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#navigation-history)**: using the Mouse Forward and Backward button to navigate through location history
* **[Auto-Detail-Viewer](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#auto-detail-viewer--switch-current-folder)**: updates the detailviewer of matlab if active editor has changed
* **[Auto-Switch-Current-Folder](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#auto-detail-viewer--switch-current-folder)**: changes currentfolder if active editor has changed
* **[FileStructure](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#file-structure)**: opened via <kbd>CTRL</kbd> + <kbd>F12</kbd>, searching through functions and sections
* **[BookmarkViewer](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#bookmarks)**: opened via <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>F2</kbd> View all bookmarks in every opened and closed editor. Stored and restored after closing and reopening an editor or Matlab
* **[Recently Closed Editor](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#recently-closed-editor)**: <kbd>CTRL</kbd> + <kbd>SHIFT</kbd> + <kbd>T</kbd>

### Other
* **[KeyPressListener](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Setup#creating-custom-key-press-callbacks-in-editor-optional)**: executes custom Matlab functions on keypress passed in by the user beforehand
* **[Execute Current Line](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#execute-current-lines)**: <kbd>SHIFT</kbd> + <kbd>F9</kbd>
* **[VarDiff](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features#vardiff)**: Compares two Variables in Workspace
* Shortcuts can be changed in `*.properties` file
* Windows are dockable
* **[SectionRunner](https://github.com/GavriYashar/Matlab-Editor-Plugin/blob/master/resources/MatlabCode/%2Bat/%2Bmep/%2Bm/SectionRunner.m)** to run or jump to specific section in a given script.

## Setup
[Follow this link](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Setup)

## Features
[Follow this link](https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Features)

## Why?
I use Matlab on a daily basis (well from Monday to Friday). The lack of a proper IDE motivates me enough to write this "plug-in".
Since this is a __hobby side project__, written in a language i don't use on a daily basis, updates, bug fixes, enhancements and the coding style may... IS not as i would like it to be. I started writing this plugin back in 2013 in [Matlab](https://de.mathworks.com/matlabcentral/fileexchange/41099-extend-matlab-editors-callback?s_tid=prof_contriblnk) for me personally and my colleagues. In 2015 i startet writing the plugin in java. I made it available for the community in the hope that more people would find it somewhat useful. 

## contact
gavriyashargithub@gmail.com
