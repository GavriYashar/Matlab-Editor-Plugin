%% MEPRBEGIN
% returns the packagename of the class currently editing, or name of current m-File if it is no class
%
% V1.0 | 2015-04-30 | Andreas Justin      | Ersterstellung
%
% Tags: Util, Helper
%
%% FUNCHANDLEBEGIN
% wenn FUNCHANDLEBEGIN und FUNCHANDLEBEND außerhalb sind dann können funktionen aufgerufen werden
% die variablen $VAR1$ usw. werden dann durch den text welche die funktionhalde liefern ersetzt.
%$VAR1$ ${@()inputdlg('Aussagekräftiger Text1')}
%$VAR_2$ ${@()inputdlg('Aussagekräftiger Text2')}
%% FUNCHANDLEBEND
%% MEPREND
${THIS}
