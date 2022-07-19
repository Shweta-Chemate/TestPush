package com.cisco.cx.training.models;

import com.cisco.cx.training.app.entities.NewLearningContentEntity;
import com.cisco.cx.training.constants.Constants;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class LearningContentItem {

  public LearningContentItem(NewLearningContentEntity entity) {
    this.id = entity.getId();
    this.learningType = entity.getLearningType();
    this.title = entity.getTitle();
    this.description = entity.getDescription();
    this.sortByDate =
        entity.getSortByDate() != null ? entity.getSortByDate().toInstant().toString() : null;
    this.status = entity.getStatus();
    this.registrationUrl = entity.getRegistrationUrl();
    this.presenterName = entity.getPresenterName();
    this.recordingUrl = entity.getRecordingUrl();
    this.duration = entity.getDuration();
    this.region = entity.getRegion();
    this.piw_architecture = entity.getPiw_architecture();
    this.piw_technology = entity.getPiw_technology();
    this.piw_sub_technology = entity.getPiw_sub_technology();
    this.piw_score = entity.getPiw_score();
    this.language = entity.getLanguage();
    this.piw_timezone = entity.getPiw_timezone();
    this.piw_ppt_url = entity.getPiw_ppt_url();
    this.sequence = entity.getSequence();
    this.published_date =
        entity.getPublished_date() != null
            ? entity.getPublished_date().toInstant().toString()
            : null;
    this.created_date =
        entity.getCreated_date() != null ? entity.getCreated_date().toInstant().toString() : null;
    this.updated_date =
        entity.getUpdated_date() != null ? entity.getCreated_date().toInstant().toString() : null;
    this.modulecount = entity.getModulecount();
    this.learning_map = entity.getLearningMap();
    this.roles = entity.getRoles();
    this.technology = entity.getTechnology();
    this.contentType = entity.getContentType();
    this.link = entity.getLink();
    this.archetype = entity.getArchetype();
    this.avgRatingPercentage = entity.getAvgRatingPercentage();
    this.totalCompletions = entity.getTotalCompletions();
    this.votesPercentage = entity.getVotesPercentage();
    this.link_title = entity.getLink_title();
    this.link_description = entity.getLink_descrption();
    populateSuccessTipsData();
  }

  private String id;

  private String learningType;

  private String title;

  private String description;

  private String sessionStartDate;

  private String sortByDate;

  private String status;

  private String regTimestamp;

  private Boolean bookmark;

  private String registrationUrl;

  private String presenterName;

  private String recordingUrl;

  private String duration;

  private String region;

  private String piw_architecture;

  private String piw_technology;

  private String piw_sub_technology;

  private Integer piw_score;

  private String language;

  private String piw_timezone;

  private String piw_ppt_url;

  private String sequence;

  private String published_date;

  private String created_date;

  private String updated_date;

  private String modulecount;

  private String learning_map;

  private String roles;

  private String technology;

  private String contentType;

  private String link;

  private String archetype;

  private String bookmarkTimeStamp;

  private Integer avgRatingPercentage;

  private Integer totalCompletions;

  private Integer votesPercentage;

  private String link_title;

  private String link_description;

  private List<SuccessTipsAttachment> successTipsVideos;

  private List<SuccessTipsAttachment> successTipsFiles;

  private void populateSuccessTipsData() {
    if (Constants.SUCCESSTIPS.equalsIgnoreCase(this.learningType)) {
      List<SuccessTipsAttachment> videoAttachments = new ArrayList<>();
      List<SuccessTipsAttachment> fileAttachments = new ArrayList<>();
      String[] asset_types = new String[0];
      String[] asset_links = new String[0];
      if (StringUtils.isNotBlank(this.contentType)) {
        asset_types = this.contentType.split(",");
      }
      if (StringUtils.isNotBlank(this.link)) {
        asset_links = this.link.split(",");
      }
      String[] asset_description = new String[0];
      if (StringUtils.isNotBlank(link_description)) {
        asset_description = this.link_description.split(":");
      }
      String[] asset_titles = new String[0];
      if (StringUtils.isNotBlank(link_title)) {
        asset_titles = this.link_title.split(":");
      }
      for (int i = 0; i < asset_types.length; i++) {
        SuccessTipsAttachment successTipAttac = new SuccessTipsAttachment();
        successTipAttac.setAttachmentType(asset_types[i]);
        if (i < asset_links.length) {
          successTipAttac.setUrl(asset_links[i]);
        } else {
          successTipAttac.setUrl("");
        }
        if (asset_description.length == 0) {
          successTipAttac.setUrlDescription("");
        } else {
          if ((i + 1) > asset_description.length) {
            successTipAttac.setUrlDescription("");
          } else {
            successTipAttac.setUrlDescription(asset_description[i]);
          }
        }
        if (i < asset_titles.length) {
          successTipAttac.setUrlTitle(asset_titles[i]);
        } else {
          successTipAttac.setUrlTitle("");
        }
        if (asset_types[i].equalsIgnoreCase(Constants.SUCCESS_TIPS_VIDEO)) {
          videoAttachments.add(successTipAttac);
        } else {
          fileAttachments.add(successTipAttac);
        }
      }
      if (!videoAttachments.isEmpty()) {
        this.setSuccessTipsVideos(videoAttachments);
      }
      if (!fileAttachments.isEmpty()) {
        this.setSuccessTipsFiles(fileAttachments);
      }
    }
  }
}
