package me.wieku.noteserv.repository;

import me.wieku.noteserv.entities.Note;
import me.wieku.noteserv.entities.NoteRevision;

import java.util.List;
import java.util.UUID;

public interface NoteRepositoryCustom {

    List<Note> getAll();

    Note getNewestNote(UUID noteId);

    List<NoteRevision> getHistory(UUID noteId);

    NoteRevision getNoteRevision(UUID noteId, int revision);

    void addNote(Note note);

    boolean updateNote(UUID noteId, NoteRevision note);

    boolean removeNote(UUID noteId);
}
