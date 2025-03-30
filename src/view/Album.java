package view;

import java.util.ArrayList;
import java.util.Objects;

public class Album {
    private String name;
    private ArrayList<Photo> photos;
    
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    public String getName() {
        return this.name;
    }
    
    public ArrayList<Photo> getPhotos(){
        return this.photos;
    }
    
    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }
    
    public void changeName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name + " has " + photos.size() + " photos";
    }
    
    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof Album)) return false;
         Album album = (Album) o;
         return Objects.equals(name, album.name);
    }
    
    @Override
    public int hashCode() {
         return Objects.hash(name);
    }
}
