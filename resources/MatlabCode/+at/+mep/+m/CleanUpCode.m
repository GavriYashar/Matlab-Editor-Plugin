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
        arguments
            mfile(1,1) string = string(getLongName(at.mep.editor.EditorWrapper.getActiveEditor()))
            ignoreLineOnlySpace(1,1) logical = true
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
    function spaceEqSign(mfile, numBefore, numAfter)
        arguments
            mfile(1,1) string   = string(getLongName(at.mep.editor.EditorWrapper.getActiveEditor()))
            numBefore(1,1) double = 1
            numAfter(1,1) double = 1
        end
        mfile = java.io.File(mfile);
        if ~mfile.isFile
            return
        end
        % at.mep.debug.Debug.assignObjectsToMatlab()
        % mt = at.mep.editor.tree.MTreeNode.construct(mTree);
        % mt.printTree()
        editor = at.mep.editor.EditorWrapper.openEditor(mfile);
        mTree = at.mep.editor.EditorWrapper.getMTree(editor);
        nodeEQ = javaMethod("valueOf", "com.mathworks.widgets.text.mcode.MTree$NodeType", "EQUALS");
        nodeETC = javaMethod("valueOf", "com.mathworks.widgets.text.mcode.MTree$NodeType", "ETC");
        nodeJavaNull = javaMethod("valueOf", "com.mathworks.widgets.text.mcode.MTree$NodeType", "JAVA_NULL_NODE");
        % methodsview(mTree) shows that an MTree.NodeType[] is expected
        mTreeEQ = toArray(mTree.findAsList([nodeEQ, nodeETC]));
        
        startPos1 = at.mep.editor.EditorWrapper.getSelectionPositionStart(editor);
        endPos1 = at.mep.editor.EditorWrapper.getSelectionPositionEnd(editor);
        for ii = numel(mTreeEQ):-1:1
            startNode = mTreeEQ(ii).getParent().getLeft().getLeft();
            endNode = mTreeEQ(ii).getParent().getLeft().getRight();
            if startNode.getType() == nodeJavaNull || endNode.getType() == nodeJavaNull
                continue
            end
            startPos = startNode.getPosition()-1;
            endPos = endNode.getPosition() + 1;
            txtPre = string(at.mep.editor.EditorWrapper.getText(editor, startPos, endPos));
            % once ... ignores any equals sign afterwards
            txtPost = regexprep(txtPre, "\s*=\s*", " = ", "once");
            if txtPre == txtPost
                continue
            end
            at.mep.editor.EditorWrapper.setSelectionPosition(editor, startPos, endPos);
            % fprintf("\n\t von: " + strrep(txtPre, "\", "\\"));
            % fprintf("\n\t nach: " + strrep(txtPost, "\", "\\") + newline());
            % fprintf(strrep(txtPost, "\", "\\") + newline());
            at.mep.editor.EditorWrapper.setSelectedTxt(editor, txtPost);
        end
        at.mep.editor.EditorWrapper.setSelectionPosition(editor, startPos1, endPos1);
    end
end     % public static methods
end
