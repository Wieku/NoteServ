package me.wieku.noteserv.database;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class NoteRepositoryImpl implements NoteRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Note getNewestNote(long noteId) {
        return null;
    }

    @Override
    public Note getNoteRevision(long noteId, long revision) {
        return null;
    }

    @Override
    @Transactional
    public void addNote(Note note) {
        em.persist(note);
        em.flush();
    }

    @Override
    public void updateNote(long noteId, Note note) {

    }

    @Override
    public void removeNote(long noteId) {

    }
}
