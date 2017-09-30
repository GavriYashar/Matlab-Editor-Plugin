package at.mep.meta;

import at.mep.editor.tree.EAttributes;
import at.mep.util.TreeUtilsV2;

/**
 * Created by Andreas Justin on 2016-09-13.
 */
public abstract class Meta {
    protected String name = "";
    protected String description = "";
    protected String detailedDescription = "";
    protected boolean isHidden = false;

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

    public abstract void populate(EAttributes attribute, EMetaAccess access);

    public void populate(EAttributes attribute) {
        populate(attribute, attribute.getDefaultAccess());
    }
}
