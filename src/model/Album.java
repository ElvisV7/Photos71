package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a photo album that contains a collection of photos.
 * Each album has a name and a list of photos.
 * 
 * @author Elvis Vasquez
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private ArrayList<Photo> photos;
    
    /**
     * Constructs a new Album with the given name.
     * 
     * @param name the album name
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Returns the name of this album.
     * 
     * @return the album name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns the list of photos in this album.
     * 
     * @return the ArrayList of Photo objects
     */
    public ArrayList<Photo> getPhotos(){
        return this.photos;
    }
    
    /**
     * Adds a photo to this album.
     * 
     * @param photo the Photo to add
     */
    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }
    
    /**
     * Changes the album's name.
     * 
     * @param name the new album name
     */
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
