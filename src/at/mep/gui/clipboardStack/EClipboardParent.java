package at.mep.gui.clipboardStack;

/** Created by Gavri on 2017-04-29. */
public enum EClipboardParent {
    INVALID(-1),
    EDITOR(0),
    COMMAND(1);

    private final int parentType;

    EClipboardParent(int parentType) {
        this.parentType = parentType;
    }

    public int getParentType() {
        return this.parentType;
    }
}
