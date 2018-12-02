package me.wieku.noteserv.database;

import java.util.List;

public interface NoteRepositoryCustom {
    Note getNewestNote(long noteId);
    List<Note> getHistory(long noteId);
    Note getNoteRevision(long noteId, int revision);
    void addNote(Note note);
    void updateNote(long noteId, Note note);
    void removeNote(long noteId);
}
