package com.cisco.cx.training.models;

import com.fasterxml.jackson.annotation.*;

import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookmarkRequestSchema {

    @JsonIgnore
    public boolean isNotBlank() {
        return StringUtils.isNotBlank(title);
    }

    @ApiModelProperty(notes = "Unique Identifier of the selected Success Talk")
    private String id;
    
    @ApiModelProperty(notes = "Title", example = "New Customer Experience Specialization overview")
    private String title;


    @ApiModelProperty(notes = "is Bookmarked", example = "true | false")
    private boolean bookmark = false;

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }
    
    public String getId() {
    	this.id = String.valueOf(this.hashCode());
    	return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}