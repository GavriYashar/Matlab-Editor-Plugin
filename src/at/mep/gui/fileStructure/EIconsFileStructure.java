package at.mep.gui.fileStructure;

import at.mep.prefs.EIconSetting;
import at.mep.prefs.Settings;
import at.mep.util.EIconDecorator;
import at.mep.util.IconUtil;
import com.mathworks.common.icons.FileTypeIcon;
import com.mathworks.common.icons.ProjectIcon;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public enum EIconsFileStructure {
    MFILE(FileTypeIcon.M.getIcon(), FileTypeIcon.M.getIcon(), FileTypeIcon.M.getIcon()),
    CELL(ProjectIcon.CELL.getIcon(), ProjectIcon.CELL.getIcon(), ProjectIcon.CELL.getIcon()),
    
    CLASS("FileStructureSymbols/intellij/classTypeJavaClass.png", "FileStructureSymbols/eclipse/class_obj.png", FileTypeIcon.M_CLASS.getIcon()),
    METHOD("FileStructureSymbols/intellij/method.png", "FileStructureSymbols/eclipse/methpub_obj.png", ProjectIcon.FUNCTION.getIcon()),
    PROPERTY("FileStructureSymbols/intellij/field.png", "FileStructureSymbols/eclipse/field_public_obj.png", ProjectIcon.PROPERTY.getIcon()),

    DECORATOR_INVALID("FileStructureSymbols/intellij/public.png", "FileStructureSymbols/eclipse/methpub_obj.png", ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon()),
    DECORATOR_META("FileStructureSymbols/intellij/private.png", "FileStructureSymbols/eclipse/methpri_obj.png", ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon()),
    DECORATOR_PUBLIC("FileStructureSymbols/intellij/public.png", "FileStructureSymbols/eclipse/methpub_obj.png", ProjectIcon.PUBLIC_OVERLAY_11x11.getIcon()),
    DECORATOR_PRIVATE("FileStructureSymbols/intellij/private.png", "FileStructureSymbols/eclipse/methpri_obj.png", ProjectIcon.PRIVATE_OVERLAY_11x11.getIcon()),
    DECORATOR_PROTECTED("FileStructureSymbols/intellij/protected.png", "FileStructureSymbols/eclipse/methpro_obj.png", ProjectIcon.PROTECTED_OVERLAY_11x11.getIcon()),
    DECORATOR_STATIC("FileStructureSymbols/intellij/staticMark.png", "FileStructureSymbols/eclipse/static_co.png", ProjectIcon.PROTECTED_OVERLAY_11x11.getIcon());

    private final ImageIcon iconIJ;
    private final ImageIcon iconE;
    private final ImageIcon iconM;

    EIconsFileStructure(String filenameIJ, String filenameE, Icon matlab) {
        this.iconIJ = new ImageIcon(EIconsFileStructure.class.getResource("/icons/" + filenameIJ));
        this.iconE = new ImageIcon(EIconsFileStructure.class.getResource("/icons/" + filenameE));
        this.iconM = (ImageIcon) matlab;
    }

    EIconsFileStructure(Icon iconIJ, Icon iconE, Icon iconM) {
        this.iconIJ = (ImageIcon) iconIJ;
        this.iconE = (ImageIcon) iconE;
        this.iconM = (ImageIcon) iconM;
    }

    public Icon getIcon(EIconSetting setting) {
        int initCap = 0;
        return getIcon(setting, new ArrayList<Icon>(initCap), new ArrayList<Color>(initCap), new ArrayList<EIconDecorator>(initCap));
    }

    public Icon getIcon() {
        return getIcon(Settings.getFSIconSet());
    }

    public Icon getIcon(EIconSetting setting, @NotNull java.util.List<Icon> decorators, java.util.List<Color> decoratorColors, java.util.List<EIconDecorator> decoratorPositions) {
        Icon icon;
        switch (setting) {
            case INTELLIJ: {
                icon = this.iconIJ;
                break;
            }
            case ECLIPSE: {
                icon = this.iconE;
                break;
            }
            case MATLAB: {
                icon = this.iconM;
                break;
            }
            case DEFAULT: {
                // run through; next default:
            }
            default: {
                icon = this.iconIJ;
            }
        }

        if (decorators.size() > 0) {
            for (int i = 0; i < decorators.size(); i++) {
                if (decoratorColors.size() > 0 && decoratorColors.get(i) != null) {
                    decorators.set(i, IconUtil.color(decorators.get(i), decoratorColors.get(i)));
                }
                icon = IconUtil.decorateIcon(icon, decorators.get(i), decoratorPositions.get(i));
            }
        }
        return icon;
    }
}
