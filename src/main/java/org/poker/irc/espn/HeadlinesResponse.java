
package org.poker.irc.espn;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("com.googlecode.jsonschema2pojo")
public class HeadlinesResponse {

    @Expose
    private String timestamp;
    @Expose
    private Integer resultsOffset;
    @Expose
    private String status;
    @Expose
    private Integer resultsLimit;
    @Expose
    private Integer resultsCount;
    @Expose
    private List<Headline> headlines = new ArrayList<Headline>();

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getResultsOffset() {
        return resultsOffset;
    }

    public void setResultsOffset(Integer resultsOffset) {
        this.resultsOffset = resultsOffset;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResultsLimit() {
        return resultsLimit;
    }

    public void setResultsLimit(Integer resultsLimit) {
        this.resultsLimit = resultsLimit;
    }

    public Integer getResultsCount() {
        return resultsCount;
    }

    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    public List<Headline> getHeadlines() {
        return headlines;
    }

    public void setHeadlines(List<Headline> headlines) {
        this.headlines = headlines;
    }

}
