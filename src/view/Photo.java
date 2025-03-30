package view;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;

public class Photo {
    private String path;
    private Calendar date_taken;
    
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
