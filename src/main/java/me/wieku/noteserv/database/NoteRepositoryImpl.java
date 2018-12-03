package me.wieku.noteserv.database;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Note getNewestNote(long noteId) {
        Note note = em.find(Note.class, noteId);
        return note.isRemoved() ? null : note;
    }

    @Override
    public List<NoteRevision> getHistory(long noteId) {
        return em.find(Note.class, noteId).getRevisions();
    }

    @Override
    public NoteRevision getNoteRevision(long noteId, int revision) {
        Note note = em.find(Note.class, noteId);
        if (note == null || note.isRemoved() || revision < 0 || revision >= note.getRevisions().size()) {
            return null;
        }
        return note.getRevisions().get(revision);
    }

    @Override
    @Transactional
    public void addNote(Note note) {
        em.persist(note);
        em.flush();
    }

    @Override
    @Transactional
    public void updateNote(long noteId, NoteRevision note) {
        Note mNote = em.find(Note.class, noteId);
        mNote.addRevision(note.getTitle(), note.getContent());
        em.flush();
    }

    @Override
    @Transactional
    public void removeNote(long noteId) {
        em.find(Note.class, noteId).markAsRemoved();
        em.flush();
    }
}
