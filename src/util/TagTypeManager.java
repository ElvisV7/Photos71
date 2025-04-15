package util;

import java.util.HashMap;
import java.util.Map;

/**
 * TagTypeManager is a simple utility class for managing allowed tag types.
 * 
 * The manager maintains a mapping of tag type names (as lower-case strings)
 * to a Boolean value indicating whether multiple instances of that tag type are allowed.
 * For example, a tag type like "location" might allow only one value (false), while
 * a type like "person" might allow multiple values (true).
 * 
 * @author Elvis Vasquez
 */
public class TagTypeManager {
    
    // Map of tag type to a boolean flag where:
    // true  = multiple tags allowed of that type.
    // false = only one tag allowed of that type.
    private Map<String, Boolean> allowedTagTypes;
    
    /**
     * Private constructor initializes the allowed tag types map.
     */
    private TagTypeManager() {
        allowedTagTypes = new HashMap<>();
        // Add default tag types:
        allowedTagTypes.put("location", false);  // Only one location tag allowed
        allowedTagTypes.put("person", true);       // Multiple person tags allowed
    }
    
    /**
     * Loads and returns an instance of TagTypeManager.
     * In a more advanced implementation, this might load configuration data from a file.
     *
     * @return a new TagTypeManager instance with default tag types.
     */
    public static TagTypeManager loadTagTypes() {
        return new TagTypeManager();
    }
    
    /**
     * Returns the allowed tag types.
     * The returned map has keys as tag type names and values as a flag indicating if multiple values are allowed.
     *
     * @return the map of allowed tag types.
     */
    public Map<String, Boolean> getAllowedTagTypes() {
        return allowedTagTypes;
    }
    
    /**
     * Determines if multiple tags of the specified type are allowed.
     * The lookup is done in a case-insensitive manner.
     *
     * @param tagType the tag type to check.
     * @return true if multiple tags of the specified type are allowed; false otherwise.
     */
    public boolean isMultipleAllowed(String tagType) {
        if (tagType == null) {
            return false;
        }
        // Use lower-case key to enforce case-insensitivity
        Boolean allowed = allowedTagTypes.get(tagType.toLowerCase());
        return (allowed != null) ? allowed : false;
    }
    
    /**
     * Adds a tag type to the allowed tag types map.
     * If the tag type already exists, its multiple-allowed status is updated.
     *
     * @param tagType the tag type name.
     * @param isMultiAllowed true if multiple instances of the tag are allowed, false if only one is allowed.
     */
    public void addTagType(String tagType, boolean isMultiAllowed) {
        if (tagType != null && !tagType.trim().isEmpty()) {
            allowedTagTypes.put(tagType.toLowerCase(), isMultiAllowed);
        }
    }
}
