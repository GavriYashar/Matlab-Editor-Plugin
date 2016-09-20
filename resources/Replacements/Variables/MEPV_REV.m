function txt = MEPV_REV(txt)
% Revision
%% EXAMPLES
%{

txt = '${REV(3.4)}'
text = MESRV_REV(txt)

% returns the following
%     text =
%     % V3.4 | 2014-06-16 | Andreas Justin      | Commentary
%     %

txt = '${REV}'
text = MESRV_REV(txt)
% returns the following
%     text =
%     % V#.# | 2014-06-16 | Andreas Justin      | Commentary
%     %

%}
%% REVISIONS
% V1.0 | 2014-06-12 | Andreas Justin      | Creation
%
% See also

expr = '\$\{(REV)\(?(\d*\.?\d*)\)?}';
m = regexp(txt,expr,'tokens','once');
if ~isempty(m) && ~isempty(m{1})
    refNum = '#.#';
    commentary = 'Commentary';
    if ~isempty(m{2});
        refNum = m{2};
        if strcmp(refNum,'1.0'); commentary = 'Creation'; end
    end
    fieldAuthor = blanks(19);
    nameAuthor = getenv('username');
    fieldAuthor(1:numel(nameAuthor)) = nameAuthor;
    newText = sprintf('%% V%s | %s | %s | %s\n%%', refNum, datestr(now,'yyyy-mm-dd'), fieldAuthor, commentary);
    txt = regexprep(txt, expr, newText);
end
