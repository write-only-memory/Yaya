package ca.currybox.yaya;

/**
 * Created by write_only_memory on 10/3/2014.
 */
public class Anime {

    private String title;
    private String synonyms;
    private int episodes;
    private int watched;
    private int status;
    private int updated;
    private int id;
    private String dateFinished;

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

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDateFinished()
    {
        return dateFinished;
    }

    public void setDateFinished(String date)
    {
        dateFinished = date;
    }
}
