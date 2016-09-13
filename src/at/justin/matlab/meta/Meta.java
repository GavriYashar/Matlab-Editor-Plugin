package at.justin.matlab.meta;

/**
 * Created by Andreas Justin on 2016-09-13.
 */
public class Meta {
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
}
