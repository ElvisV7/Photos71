package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class Photo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String path;
    private Calendar date_taken;
    private String caption = ""; // default caption
    // add tags here
    private ArrayList<String> tags;

    public Photo(String path) {
        this.path = path;
        File file = new File(path);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(file.lastModified());
        cal.set(Calendar.MILLISECOND, 0);
        this.date_taken = cal;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public Calendar getDate() {
        return this.date_taken;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public ArrayList<String> getTags(){
    	return this.tags;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /* Complete:                    V this could be changed for a programmer version of Tag (a class) */
    public void setTags(ArrayList<String> tags) {
    	// Complete the implementation of tags
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
