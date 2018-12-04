package me.wieku.noteserv.database;

import java.util.List;

public interface NoteRepositoryCustom {

    List<Note> getAll();

    Note getNewestNote(long noteId);

    List<NoteRevision> getHistory(long noteId);

    NoteRevision getNoteRevision(long noteId, int revision);

    void addNote(Note note);

    boolean updateNote(long noteId, NoteRevision note);

    boolean removeNote(long noteId);
}
