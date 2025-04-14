package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

public class PhotoService {

    /**
     * Returns a new Image using the proper URL.
     * If the path is already a file URL (i.e. starts with "file:"), it is parsed using URL.
     */
	public static Image loadImage(String path) throws FileNotFoundException {
	    try {
	        if (path.startsWith("file:")) {
	            // Create a properly encoded URL
	            String encodedPath = path.replace(" ", "%20");
	            return new Image(encodedPath);
	        } else {
	            File file = new File(path);
	            if (file.exists()) {
	                return new Image(file.toURI().toString());
	            } else {
	                throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new RuntimeException("Error loading image: " + path, e);
	    }
	}
    
    /**
     * Filters the given list of photos by a date range.
     */
    public static List<Photo> filterByDateRange(List<Photo> photos, java.time.LocalDate start, java.time.LocalDate end) {
        return photos.stream().filter(photo -> {
            java.time.LocalDate photoDate = photo.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            return !photoDate.isBefore(start) && !photoDate.isAfter(end);
        }).collect(Collectors.toList());
    }
    
    /**
     * Filters the given list of photos by a tag.
     * Checks each photo's Tag objects for a match.
     *
     * @param photos the list of photos.
     * @param tagType the tag type.
     * @param tagValue the tag value.
     * @return a filtered list of photos.
     */
    public static List<Photo> filterByTag(List<Photo> photos, String tagType, String tagValue) {
        return photos.stream()
                .filter(photo -> photo.getTags().stream()
                        .anyMatch(tag -> tag.getTagType().equalsIgnoreCase(tagType) &&
                        		tag.getTagValue().equalsIgnoreCase(tagValue)
                        		))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns a URL string suitable for an Image given a path.
     * For paths starting with "/data/", it assumes the file is located in the external "data" folder.
     */
    public static String getImageURL(String path) {
        if (path.startsWith("/data/")) {
            String baseDir = System.getProperty("user.dir") + File.separator + "data";
            String fileName = path.substring(6); // remove "/data/"
            File file = new File(baseDir, fileName);
            if (file.exists()) {
                return file.toURI().toString();
            } else {
                throw new RuntimeException("File not found: " + file.getAbsolutePath());
            }
        } else if (path.startsWith("/")) {
            URL resourceUrl = PhotoService.class.getResource(path);
            if (resourceUrl != null) {
                return resourceUrl.toExternalForm();
            } else {
                throw new RuntimeException("Resource not found: " + path);
            }
        } else {
            return "file:" + path;
        }
    }
}
  
