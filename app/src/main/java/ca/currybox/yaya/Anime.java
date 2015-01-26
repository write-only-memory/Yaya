package ca.currybox.yaya;

import java.io.Serializable;

/**
 * Created by write-only-memory on 10/3/2014.
 */
public class Anime implements Serializable {

    private static final long serialVersionUID = 1L;
    private String title;
    private String synonyms;
    private String custom_synonyms;
    private int episodes;
    private int watched;
    private int status;
    private int updated;
    private int id;
    private String dateFinished;
    private String dateStarted;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public String getCustom_synonyms() {
        return custom_synonyms;
    }

    public void setCustom_synonyms(String synonyms) {
        this.custom_synonyms = synonyms;
    }

    public int getEpisodes() {
        return episodes;
    }

    public void setEpisodes(int episodes) {
        this.episodes = episodes;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(String date) {
        dateFinished = date;
    }

    public String getDateStarted() {
        return dateStarted;
    }

    public void setDateStarted(String date) {
        dateStarted = date;
    }

    public void copy(Anime show) {
        this.title = show.title;
        this.synonyms = show.synonyms;
        this.episodes = show.episodes;
        this.watched = show.watched;
        this.status = show.status;
        this.updated = show.updated;
        this.id = show.id;
        this.dateStarted = show.dateStarted;
        this.dateFinished = show.dateFinished;
    }
}
