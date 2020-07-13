classdef (Sealed) MEP < handle
% Class to install newest version of MEP
%
%% VERSIONING
%             Author: Andreas Justin, MO MLT BG EN SDS TD-E
%           Copyright (C) Tomo-Tec, 2016 All Rights Reserved
%      Creation date: 2016-09-23
%             Matlab: 9.1, (R2016b Prerelease)
%  Required Products: -
%
%% REVISIONS
% V1.0 | 2016-09-23 | Andreas Justin      | Ersterstellung
%
% See also
%% --------------------------------------------------------------------------------------------
properties (Constant)
    % search patterns
    mepPatPreReleases = "MEP_\d+[a-z]"
    mepPatReleases = "MEP_[\d\.]+"

    % current version
    mepVer = "MEP_1.35"
    
    % link to both jar files
    matconsolectl = "D:/MEP/matconsolectl-4.5.0.jar"
    mep = "D:/MEP/" + MEPInstaller.mepVer + ".jar"
    
    % will manipulate javaclasspath.txt and add both jar files
    allowStatic = true;
end

methods (Access = private)
    function obj = MEP()
        import at.mep.m.*;
        
        % check if there are multiple MEP entries
        jcp = javaclasspath("-all");
        idx = MEP.regexStr(jcp, mepPatReleases);
        mepV = jcp(idx);
        if numel(mepV) < 1
            return;
        end
        if numel(mepV) > 1
            warning("MEPInstall multiple MEPs in javaclasspath.txt. a manual fix is required")
            jcpt = MEP.getJavaClassPathTxtFile();
            winopen(string(jcpt.getAbsolutePath()))
        end
    end
end

methods (Static = true, Access = private)
    function addJars()
        import at.mep.m.*;
        
        % dynamic: doc Dynamic Path
        %          web(fullfile(docroot, 'matlab/matlab_external/dynamic-path.html'))
        %  static: doc Static Path
        %          web(fullfile(docroot, 'matlab/matlab_external/static-path.html'))
        
        % add matconsolectl dynamically
        if ~MEP.isMatconsolectlOnStaticPath()
            javaaddpath(MEP.matconsolectl);
        end
        
        % add MEP dynamically
        if ~MEP.isMEPOnStaticPath()
            javaaddpath(MEP.mep)
            
            % add both jars to static bath for next startup
            if MEP.allowStatic
                jcpt = MEP.getJavaClassPathTxtFile();
                if ~MEP.isMatconsolectlOnStaticPath()
                    at.mep.installer.Install.appendJCPT(jcpt, MEP.matconsolectl);
                end
                at.mep.installer.Install.appendJCPT(jcpt, MEP.mep);
            end
        end
    end
end

% These methods can be changed to fit your need
methods (Static = true)
    function profileUser = getProfileUser()
        % custom user profile return as string
        profileUser = "D:/MEP/user.properties";
    end
    
    function profileDefault = getProfileDefault()
        % default user return as string
        profileDefault = "D:/MEP/default.properties";
    end
    
    function addCustomCallbacks()
        % custom user methods
        % web("https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Setup#creating-custom-key-press-callbacks-in-editor", "-browser")
        import at.mep.util.*;
        import at.mep.editor.*;

        ctrl = true;
        shift = true;
        alt = true;

        % e.g.:
        % ks = KeyStrokeUtil.getKeyStroke(java.awt.event.KeyEvent.VK_C, ~ctrl, ~shift,  alt, false);
        % EditorApp.addMatlabCallback('MEPC_ks', ks, 'MEPC_ks');
    end
    
    function jcpt = getJavaClassPathTxtFile()
        % location of javaclasspath.txt as java.io.File
        % web("https://github.com/GavriYashar/Matlab-Editor-Plugin/wiki/Setup#manual-installation", "-browser")
        jcpt = fullfile(prefdir, "javaclasspath.txt");
        jcpt = java.io.File(jcpt);
    end
end

methods (Static = true)
    function instance = getInstance()
       persistent localObj
       if isempty(localObj) || ~isvalid(localObj)
           localObj = at.mep.m.MEP();
       end
       instance = localObj;
    end

    function install()
        MEP.addJars();
        fprintf("\nMEP Version: %s\n", string(at.mep.installer.Install.getVersion()));
    end

    function updateJavaclasspathtxtNewMEP()
        % MEP für den nächsten start mit neuester version ausstatten
        try
            if MEPInstaller.isMEPOnStaticPath() && ~any(util.regexCell(javaclasspath('-static'), MEPInstaller.mepVer))
                jcpt = MEPInstaller.getJavaClassPathTxtFile();
                
                a = javaclasspath('-static');
                a = a{util.regexCell(a, 'MEP_')};
                v = regexp(a,'(?<=MEP_)\d+','match','once');
                v2 = regexp(a,'(?<=MEP_\d+)[a-z]','match','once');
                
                oldIsPreRelease = ~isempty(v2);
                newIsPreRelease = ~isempty(regexp(MEPInstaller.mep,'(?<=MEP_\d+)[a-z]','match','once'));
                doOld = oldIsPreRelease && newIsPreRelease;
                if (doOld && ~isempty(v) && str2double(v) > 1635) ...
                        || (str2double(v) == 1635 && v2 > 'b')
                    at.mep.util.FileUtils.replaceFileLine(jcpt, MEPInstaller.mepPatPreReleases, MEPInstaller.mep);
                elseif (~doOld && isempty(v2) && str2double(v) >= 1)
                    at.mep.util.FileUtils.replaceFileLine(jcpt, MEPInstaller.mepPatReleases, MEPInstaller.mep);
                end
            end
        catch err
            warning('DATools:error','Error in updateing MEP-Tools!\n%s',err.getReport)
        end
    end

    function bool = start()
        import at.mep.m.*;
        bool = false;
        try
            profileUser = MEP.getProfileUser();
            profileDefault = MEP.getProfileDefault();
            at.mep.Start.start(profileUser, profileDefault);
            bool = true;
        catch e
            e.getReport()
        end
    end
    
    function bool = isMatconsolectlOnStaticPath()
        import at.mep.m.*;
        
        bool = any(MEP.regexStr(string(javaclasspath('-static')), "matconsolectl"));
    end
    
    function bool = isMEPOnStaticPath()
        import at.mep.m.*;
        
        bool = any(MEP.regexStr(javaclasspath('-static'), MEPInstaller.mepPatReleases));
    end

    function bool = currentVerLessThanVer(v)
        cv = char(at.mep.installer.Install.getVersion());
        bool = MEPInstaller.verStr2num(cv) < MEPInstaller.verStr2num(v);
    end
    
    function vNum = verStr2num(vStr)
        vStr = regexprep(vStr, '_','.');
        vStr = regexp(vStr, '\d+\.\d+', 'match', 'once');
        vNum = str2double(vStr);
    end
end


% copy paste code of my personal source
methods (Static = true)
    function idx = regexStr(str, expr, ignorecase)
        narginchk(2,3)
        if ~isstring(str)
            % util.Error.INVALID_ARGUMENT.throw("str must be a matlab string isa " + class(str));
            error("str must be a matlab string isa " + class(str));
        elseif isempty(str)
            % util.Error.INVALID_ARGUMENT.throw("str must not be empty");
            error("str must not be empty");
        end
        if ~isstring(expr)
            % util.Error.INVALID_ARGUMENT.throw("str must be a matlab string isa " + class(expr));
            error("str must be a matlab string isa " + class(expr));
        elseif isempty(expr)
            % util.Error.INVALID_ARGUMENT.throw("str must not be empty");
            error("str must not be empty");
        elseif numel(expr) > 1
            % util.Error.INVALID_ARGUMENT.throw("expression can only be scalar");
            error("expression can only be scalar");
        end
        if nargin < 3 || isempty(ignorecase)
            ignorecase = false;
        end
        if ~(ismember(ignorecase, [true, false]) || ismember(ignorecase, [0,1]))
            % util.Error.INVALID_ARGUMENT.throw("ignorecase must be boolean");
            error("ignorecase must be boolean");
        elseif numel(ignorecase) > 1
            % util.Error.INVALID_ARGUMENT.throw("ignorecase can only be scalar");
            error("ignorecase can only be scalar");
        end
        ignorecase = iif(ignorecase, "ignorecase", "matchcase");
        res = regexp(str, expr, ignorecase, "once");
        if ~iscell(res)
            res = {res};
        end
        idx = ~cellfun('isempty', res);
    end
    
    function out = iif(trueFalse, outTrue, outFalse)
        if trueFalse
            out = outTrue;
        else
            out = outFalse;
        end
    end
end

end % classdef