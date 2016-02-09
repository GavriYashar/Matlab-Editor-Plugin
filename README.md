Welcome to the Matlab-Editor-Plugin wiki!

# What is it?
For now it justs adds a KeyReleasedListener to the Matlab-Editor which executes custom matlab functions passed in by the user

# Setup
1. you need Matlab, at least 2014a (i only tried 2014a)
2. you need to download the matlabcontrol package here [https://code.google.com/archive/p/matlabcontrol/](https://code.google.com/archive/p/matlabcontrol/)
3. create a Matlab function <MyKeyReleaseFunction>
    ```Matlab
    function out = MyKeyReleaseFunction(keyEvent)
    if nargin == 0; return; end
    % this is necessary, the first thing happen is trying find out whether 
    % the passed function is a valid matlab function or not
    ctrlFlag = evnt.isControlDown;
    shiftFlag = evnt.isShiftDown;
    altFlag = evnt.isAltDown;
    ctrlShiftFlag = ctrlFlag && shiftFlag && ~altFlag;
    ctrlShiftAltFlag = ctrlFlag && shiftFlag && altFlag;
    ctrlOnlyFlag = ctrlFlag && ~shiftFlag && ~altFlag;
    altOnlyFlag = ~ctrlFlag && ~shiftFlag && altFlag;`
    
    if altOnlyFlag && evnt.getKeyCode == evnt.VK_INSERT
        fprintf('ALT + INSERT\n')
    end
```

4. start Matlab and run following
    ```Matlab
    clear classes,     clc
    javaaddpath('<path>\matlabcontrol-4.1.0.jar')
    javaaddpath('<path>\matlab-editor-plugin_01.jar')
    at.justin.matlab.Settings.loadSettings(<path>/CustomProps.props',...
                                          <path>/DefaultProps.props')
    ea = at.justin.matlab.EditorApp.getInstance();
    ea.setCallbacks();
    ea.addMatlabCallback('MyKeyReleaseFunction');
```









 
