package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a user of the Photo application.
 * A user has a username and a list of albums.
 * 
 * <p>Note: You may choose to automatically add a default album (such as "Stock Images")
 * to each user in this constructor if desired.
 * </p>
 *
 * @author Elvis Vasquez
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private ArrayList<Album> albums;
    
    /**
     * Constructs a new user with the given username.
     * By default, the user has an empty list of albums.
     *
     * @param username the username of the user
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        // Uncomment the following lines if you want every new user to have a default album.
        // Album defaultAlbum = new Album("Stock Images");
        // this.albums.add(defaultAlbum);
    }
    
    /**
     * Returns the username of this user.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }
    
    /**
     * Returns the list of albums belonging to this user.
     *
     * @return the list of albums
     */
    public ArrayList<Album> getAlbums(){
        return this.albums;
    }
    
    /**
     * Removes the specified album from the user's album list.
     *
     * @param album the album to delete
     */
    public void deleteAlbum(Album album) {
        this.albums.remove(album);
    }
    
    /**
     * Returns a string representation of the user.
     *
     * @return a string in the format "username has X albums"
     */
    @Override
    public String toString() {
        return this.username + " has " + albums.size() + " albums";
    }
    
    /**
     * Checks if this user is equal to another object.
     * Two users are considered equal if their usernames are equal.
     *
     * @param o the object to compare with
     * @return true if the usernames are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof User)) return false;
         User user = (User) o;
         return Objects.equals(username, user.username);
    }
    
    /**
     * Returns the hash code for this user, based on the username.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
         return Objects.hash(username);
    }
}
