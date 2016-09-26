function txt = MEPV_AUTHORLONG(txt)
% returns name of currently logged in user
%% VERSIONING
%             Author: Andreas Justin
%      Creation date: 2014-06-11
%             Matlab: 8.3.0.532 (R2014a)
%  Required Products: -
%
%% REVISIONS
% V1.0 | 2014-06-12 | Andreas Justin    | Ersterstellung
%
% See also

expr = '\$\{AUTHORLONG\}';
if ~isempty(regexp(txt,expr,'once'))
    txt = regexprep(txt, expr, getenv('username'));
end
