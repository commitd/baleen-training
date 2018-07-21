package io.committed.training.baleenfootball.collectionreader;

import java.util.Date;
import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A serializable Java Bean representation of an article.
 */
public class Article {

  /** The pub date. */
  private Date pubDate;

  /** The source name. */
  private String sourceName;

  /** The source type. */
  private String sourceType;

  /** The source url. */
  // RSS url
  private String sourceUrl;

  /** The title. */
  private String title;

  /** The description. */
  private String description;

  /** The url. */
  private String url;

  /** The content. */
  private String content;

  /** The local filename. */
  private String localFilename;

  /**
   * Sets the local filename.
   *
   * @param localFilename the new local filename
   */
  public void setLocalFilename(final String localFilename) {
    this.localFilename = localFilename;
  }

  /**
   * Gets the local filename.
   *
   * @return the local filename
   */
  public String getLocalFilename() {
    return localFilename;
  }

  /**
   * Gets the pub date.
   *
   * @return the pub date
   */
  public Date getPubDate() {
    return pubDate;
  }

  /**
   * Sets the pub date.
   *
   * @param pubDate the new pub date
   */
  public void setPubDate(final Date pubDate) {
    this.pubDate = pubDate;
  }

  /**
   * Gets the source name.
   *
   * @return the source name
   */
  public String getSourceName() {
    return sourceName;
  }

  /**
   * Sets the source name.
   *
   * @param sourceName the new source name
   */
  public void setSourceName(final String sourceName) {
    this.sourceName = sourceName;
  }

  /**
   * Gets the source type.
   *
   * @return the source type
   */
  public String getSourceType() {
    return sourceType;
  }

  /**
   * Sets the source type.
   *
   * @param sourceType the new source type
   */
  public void setSourceType(final String sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * Gets the source url.
   *
   * @return the source url
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  /**
   * Sets the source url.
   *
   * @param sourceUrl the new source url
   */
  public void setSourceUrl(final String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Gets the url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the url.
   *
   * @param url the new url
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * Gets the content.
   *
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * Sets the content.
   *
   * @param content the new content
   */
  public void setContent(final String content) {
    this.content = content;
  }

  /**
   * Gets the summary.
   *
   * @return the summary
   */
  @JsonIgnore
  public Optional<String> getSummary() {
    return description != null && !description.isEmpty() ? Optional.of(description)
        : Optional.<String>empty();
  }

  /**
   * Gets the best source.
   *
   * @return the best source
   */
  @JsonIgnore
  public String getBestSource() {
    return url != null && !url.isEmpty() ? url : sourceUrl;
  }

  /**
   * Gets the best timestamp.
   *
   * @return the best timestamp
   */
  public long getBestTimestamp() {
    return pubDate != null ? pubDate.getTime() : System.currentTimeMillis();
  }
}
