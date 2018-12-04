package me.wieku.noteserv.database;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

public class NoteRepositoryImpl implements NoteRepositoryCustom {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<Note> getAll() {
        List<Note> notes = manager.createQuery("SELECT a FROM " + Note.class.getName() + " a").getResultList();
        return notes.stream().filter(note -> !note.isRemoved()).collect(Collectors.toList());
    }

    @Override
    public Note getNewestNote(long noteId) {
        Note note = manager.find(Note.class, noteId);
        return note.isRemoved() ? null : note;
    }

    @Override
    public List<NoteRevision> getHistory(long noteId) {
        return manager.find(Note.class, noteId).getRevisions();
    }

    @Override
    public NoteRevision getNoteRevision(long noteId, int revision) {
        Note note = manager.find(Note.class, noteId);
        if (note == null || note.isRemoved() || revision < 0 || revision >= note.getRevisions().size()) {
            return null;
        }
        return note.getRevisions().get(revision);
    }

    @Override
    @Transactional
    public void addNote(Note note) {
        manager.persist(note);
        manager.flush();
    }

    @Override
    @Transactional
    public boolean updateNote(long noteId, NoteRevision note) {
        Note mNote = manager.find(Note.class, noteId);

        if (mNote == null) {
            return false;
        }

        mNote.addRevision(note.getTitle(), note.getContent());
        manager.flush();
        return true;
    }

    @Override
    @Transactional
    public boolean removeNote(long noteId) {
        Note note = manager.find(Note.class, noteId);
        if (note == null || note.isRemoved()) {
            return false;
        }

        note.markAsRemoved();
        manager.flush();
        return true;
    }
}
