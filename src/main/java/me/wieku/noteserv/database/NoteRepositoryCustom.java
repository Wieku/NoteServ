package me.wieku.noteserv.database;

public interface NoteRepositoryCustom {
    Note getNewestNote(long noteId);
    Note getNoteRevision(long noteId, long revision);
    void addNote(Note note);
    void updateNote(long noteId, Note note);
    void removeNote(long noteId);
}
