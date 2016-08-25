package at.justin.matlab.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum Icons {
    // 0-16 pixels
    CHECK_10("check_10.png"),
    SEARCH_15("search_15.png"),
    TOOLBAR_DELETE_16("toolbar_delete_16.png");

    private final ImageIcon icon;

    Icons(String filename) {
        this.icon = new ImageIcon(Icons.class.getResource("/icons/" + filename));
    }

    public Icon getIcon() {
        return this.icon;
    }

}
