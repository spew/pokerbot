
package org.poker.irc.espn;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class Headline {

    @Expose
    private String headline;
    @Expose
    private List<String> keywords = new ArrayList<String>();
    @Expose
    private String lastModified;
    @Expose
    private Boolean premium;
    @Expose
    private String mobileStory;
    @Expose
    private Links links;
    @Expose
    private String shortLinkText;
    @Expose
    private String type;
    @Expose
    private List<Related> related = new ArrayList<Related>();
    @Expose
    private Integer id;
    @Expose
    private String story;
    @Expose
    private String title;
    @Expose
    private String linkText;
    @Expose
    private String byline;
    @Expose
    private String source;
    @Expose
    private String description;
    @Expose
    private List<Image> images = new ArrayList<Image>();
    @Expose
    private List<Category> categories = new ArrayList<Category>();
    @Expose
    private String published;
    @Expose
    private List<Video> video = new ArrayList<Video>();

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getMobileStory() {
        return mobileStory;
    }

    public void setMobileStory(String mobileStory) {
        this.mobileStory = mobileStory;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getShortLinkText() {
        return shortLinkText;
    }

    public void setShortLinkText(String shortLinkText) {
        this.shortLinkText = shortLinkText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Related> getRelated() {
        return related;
    }

    public void setRelated(List<Related> related) {
        this.related = related;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public List<Video> getVideo() {
        return video;
    }

    public void setVideo(List<Video> video) {
        this.video = video;
    }

}
