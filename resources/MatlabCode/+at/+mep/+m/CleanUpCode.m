classdef CleanUpCode
%% DESCRIPTION
% collection of code cleanup tools
%% VERSIONING
%             Author: Andreas Justin
%      Creation date: 2019-01-30
%             Matlab: 9.6, (R2019a)
%  Required Products: https://github.com/GavriYashar/Matlab-Utilities
%
%% REVISONS
% V0.1 | 2019-01-30 | Andreas Justin      | first implementation
%
% See also
%
%% EXAMPLES
%{

    mfile = "C:\sds\tools\DA\MatlabM\Tools\Matlab-Editor-Plugin\MEP\+at\+mep\+m\CleanUpCode.m"
    at.mep.m.CleanUpCode.removeTrailingSpace(mfile, true)

%}
%% --------------------------------------------------------------------------------------------
    
%% >|•| Methods
%% --|••| Public Static Methods
methods (Static = true)
    function removeTrailingSpace(mfile, ignoreLineOnlySpace)
        % removes trailing whitespace from given $mfile
        %               mfile ... 1x1 String
        %                          Absolute path to .m file
        % ignoreLineOnlySpace ... 1x1 logical
        %                          true - ignores lines with only space (DEFAULT)
        %                          false - removes spaces from line even if line only has spaces
        narginchk(1,2)
        if nargin < 2 || isempty(ignoreLineOnlySpace)
            ignoreLineOnlySpace = true;
        end
        mfile = java.io.File(mfile);
        if ~mfile.isFile
            return
        end
        strArr = at.mep.util.FileUtils.readFileToStringList(mfile, []);
        strs = string(strArr.toArray);
        strsCleaned = util.String.trimEnd(strs);
        
        if ignoreLineOnlySpace
            idx = util.regexStr(strs, "^ +$");
            strsCleaned(idx) = strs(idx);
        end
        
        at.mep.util.FileUtils.writeFileText(mfile, strsCleaned.join(newline()));
    end
end     % public static methods

end
