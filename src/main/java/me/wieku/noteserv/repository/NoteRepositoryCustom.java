package me.wieku.noteserv.repository;

import me.wieku.noteserv.entities.Note;
import me.wieku.noteserv.entities.NoteRevision;

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
