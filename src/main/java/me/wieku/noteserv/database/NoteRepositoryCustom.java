package me.wieku.noteserv.database;

import java.util.List;

public interface NoteRepositoryCustom {
    Note getNewestNote(long noteId);

    List<NoteRevision> getHistory(long noteId);

    NoteRevision getNoteRevision(long noteId, int revision);

    void addNote(Note note);

    void updateNote(long noteId, NoteRevision note);

    void removeNote(long noteId);
}
