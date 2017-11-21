function txt = MEPV_DATE(txt)
% returns date string as yyyy-mm-dd format
%% VERSIONING
%             Author: Andreas Justin,
%      Creation date: 2014-06-11
%             Matlab: 8.3.0.532 (R2014a)
%  Required Products: -
%
%% REVISIONS
% V1.0 | 2014-06-11 | Andreas Justin    | Ersterstellung
%

txt = datestr(now,'yyyy-mm-dd');
