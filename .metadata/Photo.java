package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

/**
 * Represents a photo in the application.
 * A photo is associated with a file path, a date taken (using the file's last modified date),
 * an optional caption, and a list of tags.
 * 
 * @author Elvis Vasquez & Tyler Gehringer
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String path;
    private Calendar date_taken;
    private String caption = ""; // Default caption is empty.
    private ArrayList<Tag> tags;  // Stores tags in "type:value" format.
    
    /**
     * Constructs a Photo object given its file path.
     * The last modified time of the file is used as the photo's date.
     * 
     * @param path the file path of the photo.
     */
    public Photo(String path) {
        this.path = path;
        File file = new File(path);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        cal.set(Calendar.MILLISECOND, 0);
        this.date_taken = cal;
        this.tags = new ArrayList<>();  // Initialize the tags list.
    }
    
    /**
     * Returns the file path of the photo.
     * 
     * @return the path as a String.
     */
    public String getPath() {
        return this.path;
    }
    
    /**
     * Returns the date the photo was taken.
     * This is based on the last modified date of the file.
     * 
     * @return a Calendar object representing the date.
     */
    public Calendar getDate() {
        return this.date_taken;
    }
    
    /**
     * Returns the photo's caption.
     * 
     * @return the caption as a String.
     */
    public String getCaption() {
        return caption;
    }
    
    /**
     * Sets the photo's caption.
     * 
     * @param caption the new caption.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /**
     * Returns the list of tags associated with the photo.
     * 
     * @return an ArrayList of tags.
     */
    public ArrayList<Tag> getTags(){
        return tags;
    }
    
    public ArrayList<String> getTagsAsString(){
        ArrayList<String> tagStrings = new ArrayList<>();
        for (Tag tag : tags) {
            tagStrings.add(tag.toString());
        }
        return tagStrings;
    }
    
    /**
     * Adds a tag to the photo using the provided TagTypeManager to enforce constraints.
     * @param tag the Tag object to add.
     * @param tagTypeManager the manager that enforces single or multi-value rules.
     * @return true if added, false otherwise.
     */
    public boolean addTag(Tag tag, util.TagTypeManager tagTypeManager) {
        if (tags.contains(tag)) {
            System.out.println("Tag " + tag + " is already present.");
            return false;
        }
        // Enforce single-value rule if applicable.
        if (!tagTypeManager.isMultipleAllowed(tag.getTagType())) {
            for (Tag t : tags) {
                if (t.getTagType().equalsIgnoreCase(tag.getTagType())) {
                    System.out.println("Only one tag allowed for '" + tag.getTagType() + "'.");
                    return false;
                }
            }
        }
        tags.add(tag);
        System.out.println("Tag " + tag + " added.");
        return true;
    }
    
    /**
     * Removes a tag from the photo.
     * @param tag the Tag object to remove.
     * @return true if found and removed, false otherwise.
     */
    public boolean removeTag(Tag tag) {
        if (tags.contains(tag)) {
            tags.remove(tag);
            System.out.println("Tag " + tag + " removed.");
            return true;
        } else {
            System.out.println("Tag " + tag + " not found.");
            return false;
        }
    }
    
    
    /**
     * Sets the tags for the photo.
     * 
     * @param tags an ArrayList of tags to be assigned.
     */
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }
    
    @Override
    public String toString() {
        return this.path + " was taken on " + this.date_taken.getTime();
    }
    
    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Photo)) return false;
         Photo photo = (Photo) o;
         return Objects.equals(path, photo.path);
    }
    
    @Override
    public int hashCode() {
         return Objects.hash(path);
    }
}
