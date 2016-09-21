package at.justin.matlab.gui.mepr;


/** Created by Andreas Justin on 2016-09-20. */
public class MEPREntry {
    private String action = "";
    private String[] tags = new String[0];
    private String comment = "";

    public MEPREntry() {
    }

    public MEPREntry(String action, String[] tags, String comment) {
        this.action = action;
        this.tags = tags;
        this.comment = comment;
    }

    public MEPREntry(String action, String tags, String comment) {
        String[] tagsArray = tags.split("[;,]");
        for (int i = 0; i < tagsArray.length; i++) {
            int s = tagsArray[i].indexOf("Tags:");
            if (s > 0) {
                tagsArray[i] = tagsArray[i].substring(s + 5);
            }
            tagsArray[i] = tagsArray[i].trim();
        }
        this.action = action;
        this.tags = tagsArray;
        this.comment = comment;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
