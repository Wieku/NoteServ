package me.wieku.noteserv.view;

import java.time.LocalDateTime;

public class NoteWithId extends NewestNote {

    private String id;

    public NoteWithId(String id, String title, String content, LocalDateTime dateCreated, LocalDateTime dateModified) {
        super(title, content, dateCreated, dateModified);
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
