package me.wieku.noteserv.view;

import java.time.LocalDateTime;
import java.util.UUID;

public class NoteWithId extends NewestNote {

    private UUID id;

    public NoteWithId(UUID id, String title, String content, LocalDateTime dateCreated, LocalDateTime dateModified) {
        super(title, content, dateCreated, dateModified);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

}
