package model;

import java.io.Serializable;
import java.util.Objects;


/**
 * Represents the tag object that can be attached to photos
 * Each tag as a type and a value
 * This class implements three extra functions besides the getters and setters for the object
 * equals() and hashCode() overrides the original built in function to implement case-sensitive comparisons to prevent duplicate tags
 * toString() makes it much easier to print our tags in display.
 * 
 * @author Tyler Gehringer
 */

public class Tag implements Serializable{
	private static final long serialVersionUID = 1L;
	private String tagType;
	private String tagValue;
	
	public Tag(String tagType, String tagValue) {
		this.tagType = tagType.trim();
		this.tagValue = tagValue.trim();
	}
	
	public String getTagType() {
		return tagType;
	}
	
	public String getTagValue() {
		return tagValue;
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if (!(o instanceof Tag)) return false;
		Tag tag = (Tag) o;
		
		return tagType.equalsIgnoreCase(tag.tagType) && tagValue.equalsIgnoreCase(tag.tagValue);
	}
	
	public int hashCode() {
		return Objects.hash(tagType.toLowerCase(), tagValue.toLowerCase());
	}
	
	public String toString() {
		return tagType + ":" + tagValue;
	}
}
