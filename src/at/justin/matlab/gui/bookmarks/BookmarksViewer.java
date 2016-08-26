package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.gui.components.JTextFieldSearch;
import at.justin.matlab.gui.components.UndecoratedFrame;

import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class BookmarksViewer extends UndecoratedFrame {
    private static BookmarksViewer INSTANCE;
    private static Dimension dimension = new Dimension(600,400);

    private BookmarksViewer() {
        setLayout();
    }

    public static BookmarksViewer getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new BookmarksViewer();
        return INSTANCE;
    }

    private void setLayout() {
        setResizable(true);
        setSize(dimension);
        rootPane.setLayout(new GridBagLayout());

        addSearchBar();
        addToolBar();
        addViewPanel();
    }


    private void addSearchBar() {
        JTextFieldSearch jtfs = new JTextFieldSearch(20);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        rootPane.add(jtfs,gbc);
    }

    private void addToolBar() {

    }

    private void addViewPanel() {

    }

}
