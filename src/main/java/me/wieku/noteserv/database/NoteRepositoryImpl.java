package me.wieku.noteserv.database;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

public class NoteRepositoryImpl implements NoteRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Note getNewestNote(long noteId) {
        return em.find(Note.class, noteId);
    }

    @Override
    public List<Note> getHistory(long noteId) {
        List<Note> notes = new ArrayList<>();
        AuditReader reader = AuditReaderFactory.get(em);
        reader.getRevisions(Note.class, noteId).forEach(number ->
            notes.add(reader.find(Note.class, noteId, number))
        );
        return notes;
    }

    @Override
    public Note getNoteRevision(long noteId, int revision) {
        Note note = AuditReaderFactory.get(em).find(Note.class, noteId, revision);
        return note;
    }

    @Override
    @Transactional
    public void addNote(Note note) {
        em.persist(note);
        em.flush();
    }

    @Override
    @Transactional
    public void updateNote(long noteId, Note note) {
        Note mNote = em.find(Note.class, noteId);
        mNote.setTitle(note.getTitle());
        mNote.setContent(note.getContent());

        em.flush();
    }

    @Override
    public void removeNote(long noteId) {

    }
}
