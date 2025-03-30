package view;

import java.util.ArrayList;
import java.util.Objects;

public class User {
    private String username;
    private ArrayList<Album> albums;
    
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
        // Create the default "Stock Images" album.
        Album defaultAlbum = new Album("Stock Images");
        defaultAlbum.addPhoto(new Photo("/app/icon.png")); 
        defaultAlbum.addPhoto(new Photo("/view/folder_icon.png"));
        defaultAlbum.addPhoto(new Photo("/view/background.jpeg"));
        this.albums.add(defaultAlbum);
    }

    
    public String getUsername() {
        return this.username;
    }
    
    public ArrayList<Album> getAlbums(){
        return this.albums;
    }
    
    public void deleteAlbum(Album album) {
        this.albums.remove(album);
    }
    
    @Override
    public String toString() {
        return this.username + " has " + albums.size() + " albums";
    }
    
    @Override
    public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof User)) return false;
         User user = (User) o;
         return Objects.equals(username, user.username);
    }
    
    @Override
    public int hashCode() {
         return Objects.hash(username);
    }
}
