package me.wieku.noteserv.controllers;

import me.wieku.noteserv.entities.Note;
import me.wieku.noteserv.repository.NoteRepository;
import me.wieku.noteserv.entities.NoteRevision;
import me.wieku.noteserv.view.NewestNote;
import me.wieku.noteserv.view.NoteWithId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository repository;

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    public ResponseEntity<Object> createNote(@RequestParam(value = "title") String title, @RequestParam(value = "content") String content, UriComponentsBuilder builder) {
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Note note = new Note(title, content);
        repository.addNote(note);

        UriComponents components = builder.path("/notes/{id}").buildAndExpand(note.getId());
        return ResponseEntity.created(components.toUri()).build();
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public ResponseEntity<Object> getNotes() {
        List<NoteWithId> notes = repository.getAll().stream().map(note -> new NoteWithId(note.getId(), note.getTitle(), note.getContent(), note.getCreationDate(), note.getModificationDate())).collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getNote(@PathVariable UUID noteId) {
        Note note = repository.getNewestNote(noteId);
        return note == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(new NewestNote(note.getTitle(), note.getContent(), note.getCreationDate(), note.getModificationDate()));
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateNote(@PathVariable UUID noteId, @RequestParam(value = "title") String title, @RequestParam(value = "content") String content) {
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        boolean ok = repository.updateNote(noteId, new NoteRevision(title, content));
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/notes/{noteId}/{revisionId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getNote(@PathVariable UUID noteId, @PathVariable Integer revisionId) {
        NoteRevision revision = repository.getNoteRevision(/*decoded[0]*/noteId, revisionId);
        return revision == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(revision);
    }

    @RequestMapping(value = "/notes/{noteId}/history", method = RequestMethod.GET)
    public ResponseEntity<Object> getHistory(@PathVariable UUID noteId) {
        List<NoteRevision> notes = repository.getHistory(/*decoded[0]*/noteId);
        return notes == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(notes);
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable UUID noteId) {
        boolean ok = repository.removeNote(noteId);
        return ok ? ResponseEntity.ok(null) : ResponseEntity.notFound().build();
    }
}
