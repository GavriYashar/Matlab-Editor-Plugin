package at.mep.mepr;

/** Created by Andreas Justin on 2016-09-28. */
public enum MEPRActionE {
    VIEWER(0),
    QUICKSEARCH(1),
    COMMAND(2);

    private int action;

    MEPRActionE(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }
}
