package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.image.Image;

public class PhotoService {

    /**
     * Returns a new Image using the proper URL.
     * If the path is a resource path (starting with "/"), then it uses the resource.
     * Otherwise, it assumes it is a file path.
     */
    public static Image loadImage(String path) throws FileNotFoundException {
        if (path.startsWith("/")) {
            URL resourceUrl = PhotoService.class.getResource(path);
            if (resourceUrl != null) {
                return new Image(resourceUrl.toExternalForm());
            } else {
                throw new FileNotFoundException("Resource not found: " + path);
            }
        } else {
            return new Image(new FileInputStream(new File(path)));
        }
    }

    /**
     * Filters the given list of photos by a date range.
     */
    public static List<Photo> filterByDateRange(List<Photo> photos, LocalDate start, LocalDate end) {
        return photos.stream().filter(photo -> {
            LocalDate photoDate = photo.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            return !photoDate.isBefore(start) && !photoDate.isAfter(end);
        }).collect(Collectors.toList());
    }
    
    /**
     * Filters the given list of photos by a tag (in the format "tagType:tagValue").
     */
    public static List<Photo> filterByTag(List<Photo> photos, String tagType, String tagValue) {
        String key = tagType + ":" + tagValue;
        return photos.stream()
                .filter(photo -> photo.getTags().stream()
                        .anyMatch(tag -> tag.equalsIgnoreCase(key)))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns a URL string suitable for an Image given a path.
     */
    public static String getImageURL(String path) {
        if (path.startsWith("/")) {
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
