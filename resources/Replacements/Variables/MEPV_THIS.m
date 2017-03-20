function txt = MEPV_TIME(txt)
% returns the package name of the class currently editing, or name of current m-File if it is no class
%% VERSIONING
%             Author: Andreas Justin
%      Creation date: 2014-08-05
%             Matlab: 8.3.0.532 (R2014a)
%  Required Products: -
%
%% REVISIONS
% V1.0 | 2015-04-30 | Andreas Justin      | Ersterstellung
%
% See also
expr = '\$\{(THIS)\}';
if ~isempty(regexp(txt,expr,'once'))
    txt = char(at.mep.editor.EditorWrapper.getFullQualifiedClass());
end
