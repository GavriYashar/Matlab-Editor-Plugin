package at.mep.meta;

import at.mep.editor.tree.EAttributePropertyMethod;
import at.mep.util.TreeUtilsV2;
import com.mathworks.widgets.text.mcode.MTree;

import java.util.List;

/**
 * Created by Andreas Justin on 2016-09-13.
 */
public abstract class Meta {
    protected String name = "";
    protected String description = "";
    protected String detailedDescription = "";
    protected boolean isHidden = false;
    private List<MTree.Node> attributes;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void populate(TreeUtilsV2.AttributeHolder attributeHolder) {
        populate(attributeHolder.getAttribute(), attributeHolder.getAccess());
    }

    public abstract void populate(EAttributePropertyMethod attribute, EMetaAccess access);

    public void populate(EAttributePropertyMethod attribute) {
        populate(attribute, attribute.getDefaultAccess());
    }
}
