package com.cisco.cx.training.models;
import com.cisco.cx.training.util.HasId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookmarkResponseSchema extends BookmarkRequestSchema implements HasId {

    @Generated("javax.util.UUID.randomUUID")
    @ApiModelProperty(notes = "Unique Identifier for bookmark request", example = "00000000-0000-0000-0000-000000000000")
    private String bookmarkRequestId = UUID.randomUUID().toString();

    @ApiModelProperty(notes = "Created Timestamp Epoch", example = "1500000000000")
    private Long created;

    @ApiModelProperty(notes = "Updated Timestamp Epoch", example = "1500000000000")
    private Long updated = System.currentTimeMillis();
    
    private String email;

    public String getBookmarkRequestId() {
        return bookmarkRequestId;
    }

    public void setBookmarkRequestId(String bookmarkRequestId) {
        this.bookmarkRequestId = bookmarkRequestId;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
    @JsonIgnore
    public String getDocId() {
        return this.bookmarkRequestId;
    }

    @Override
    public void setDocId(String id) {
        this.bookmarkRequestId = id;
    }
}
