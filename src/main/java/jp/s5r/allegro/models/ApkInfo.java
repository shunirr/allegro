package jp.s5r.allegro.models;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

import java.net.URI;
import java.util.Date;

@JsonModel(decamelize = true)
public class ApkInfo {
  @JsonKey
  private String title;

  @JsonKey
  private String uri;

//  @JsonKey
  private Date lastModified;

  @JsonKey
  private long size;

  public ApkInfo() {
  }

  public ApkInfo(final String aTitle,
                 final String aUri,
                 final Date aLastModified,
                 final long aSize) {
    setTitle(aTitle);
    setUri(aUri);
    setLastModified(aLastModified);
    setSize(aSize);
  }

  @Deprecated
  public ApkInfo(final String aTitle,
                 final URI aUri,
                 final long aSize,
                 final Date aLastModified) {
    setTitle(aTitle);
    setUri(aUri.toString());
    setSize(aSize);
    setLastModified(aLastModified);
  }

  public String getTitle() {
    if (title == null) {
      return "";
    }
    return title;
  }

  public void setTitle(final String aTitle) {
    title = aTitle;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(final String aUri) {
    uri = aUri;
  }

  public Date getLastModified() {
    if (lastModified == null) {
      return new Date();
    }
    return lastModified;
  }

  public void setLastModified(final Date aLastModified) {
    lastModified = aLastModified;
  }

  public long getSize() {
    return size;
  }

  public void setSize(final long aSize) {
    size = aSize;
  }
}
