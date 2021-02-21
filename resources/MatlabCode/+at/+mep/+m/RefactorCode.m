classdef RefactorCode
% One line brief description of the class or function
% 
%% DESCRIPTION
% 
%% VERSIONING
%             Author: Andreas Justin
%      Creation date: 2021-02-19
%             Matlab: 9.9, (R2020b)
%  Required Products: -
%
%% REVISONS
% V0.1 | 2021-02-19 | Andreas Justin        | first implementation
%
% See also 
%
%% EXAMPLES
%{

%}
%% --------------------------------------------------------------------------------------------
%% >|•| Methods
%% --|••| Public Static Methods
methods (Static = true)
    function searchAndReplaceInFolderInteractive(folder, expression, replace)
        arguments
            folder(1,1) file.Filename
            expression(1,1) string
            replace(1,1) string
        end
        searchRekursive = true;
        d = file.Filename.dirRegExp(folder, ".m$", searchRekursive);
        for ii = 1:numel(d)
            r = at.mep.m.ReplaceInteractive(d(ii), expression, replace);
            r.start()
        end
    end
    
    function searchAndReplaceInFolderAutomatic(folder, expression, replace)
        arguments
            folder(1,1) file.Filename
            expression(1,1) string
            replace(1,1) string
        end
        disp("------------------------------------------------------------")
        disp("Make sure everything is commited before you use this tool, there is no guarantee that it does what you want")
        disp("you must accept with typing 'JUST DO IT' (case sensitive)")
        disp(newline())
        
        in = input("If the selection is okay press type 'JUST DO IT': ", "s");
        if in ~= "JUST DO IT"
            disp("You chose wisely")
            return
        end
        
        searchRekursive = true;
        d = file.Filename.dirRegExp(folder, ".m$", searchRekursive);
        for ii = 1:numel(d)
            if d(ii).isdir()
                continue
            end
            str = file.util.ReadText.read(d(ii));
            strRep = regexprep(str, expression, replace);
            %{
                assignin("base","strLoaded",str)
                assignin("base","strReplaced",strRep)
                at.mep.workspace.WorkspaceWrapper.vardiff("strLoaded", "strReplaced")
            %}
            tfw = file.TextFileWriter(d(ii), "W", "UTF-8");
            tfw.fopen();
            tfw.writeTextString(strRep);
            tfw.fclose();
        end
    end
end     % public static methods

end     % classdef