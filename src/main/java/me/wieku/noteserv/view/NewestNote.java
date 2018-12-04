package me.wieku.noteserv.view;

import java.time.LocalDateTime;

public class NewestNote {
    private String title;
    private String content;
    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public NewestNote(String title, String content, LocalDateTime dateCreated, LocalDateTime dateModified) {
        this.title = title;
        this.content = content;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }
}
