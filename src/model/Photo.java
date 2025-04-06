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
 * @author Elvis Vasquez
 */
public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String path;
    private Calendar date_taken;
    private String caption = ""; // Default caption is empty.
    private ArrayList<String> tags;  // Stores tags in "type:value" format.
    
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
     * Returns the list of tags associated with the photo.
     * 
     * @return an ArrayList of tags.
     */
    public ArrayList<String> getTags(){
        return this.tags;
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
     * Sets the tags for the photo.
     * 
     * @param tags an ArrayList of tags to be assigned.
     */
    public void setTags(ArrayList<String> tags) {
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
