package me.wieku.noteserv.services;

import me.wieku.noteserv.database.Note;
import me.wieku.noteserv.database.NoteRepository;
import me.wieku.noteserv.database.NoteRevision;
import me.wieku.noteserv.view.NewestNote;
import me.wieku.noteserv.view.NoteWithId;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository repository;

    private Hashids hashids = new Hashids("67rbg8567GNFY", 4);

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    public ResponseEntity<Object> createNote(@RequestParam(value = "title") String title, @RequestParam(value = "content") String content, UriComponentsBuilder builder) {
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        Note note = new Note(title, content);
        repository.addNote(note);

        UriComponents components = builder.path("/notes/{id}").buildAndExpand(hashids.encode(note.getId()));
        return ResponseEntity.created(components.toUri()).build();
    }

    @RequestMapping(value = "/notes", method = RequestMethod.GET)
    public ResponseEntity<Object> getNotes() {
        List<NoteWithId> notes = repository.getAll().stream().map(note -> new NoteWithId(hashids.encode(note.getId()), note.getTitle(), note.getContent(), note.getCreationDate(), note.getModificationDate())).collect(Collectors.toList());
        return ResponseEntity.ok(notes);
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getNote(@PathVariable String noteId) {
        long[] decoded = hashids.decode(noteId);
        if (decoded.length == 0) {
            return ResponseEntity.notFound().build();
        }

        Note note = repository.getNewestNote(decoded[0]);
        return note == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(new NewestNote(note.getTitle(), note.getContent(), note.getCreationDate(), note.getModificationDate()));
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateNote(@PathVariable String noteId, @RequestParam(value = "title") String title, @RequestParam(value = "content") String content) {
        long[] decoded = hashids.decode(noteId);
        if (decoded.length == 0) {
            return ResponseEntity.notFound().build();
        }
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.badRequest().build();
        }

        boolean ok = repository.updateNote(decoded[0], new NoteRevision(title, content));
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/notes/{noteId}/{revisionId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getNote(@PathVariable String noteId, @PathVariable Integer revisionId) {
        long[] decoded = hashids.decode(noteId);
        if (decoded.length == 0) {
            return ResponseEntity.notFound().build();
        }

        NoteRevision revision = repository.getNoteRevision(decoded[0], revisionId);
        return revision == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(revision);
    }

    @RequestMapping(value = "/notes/{noteId}/history", method = RequestMethod.GET)
    public ResponseEntity<Object> getHistory(@PathVariable String noteId) {
        long[] decoded = hashids.decode(noteId);
        if (decoded.length == 0) {
            return ResponseEntity.notFound().build();
        }

        List<NoteRevision> notes = repository.getHistory(decoded[0]);
        return notes == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(notes);
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable String noteId) {
        long[] decoded;
        if (noteId == null || (decoded = hashids.decode(noteId)).length == 0) {
            return ResponseEntity.notFound().build();
        }

        boolean ok = repository.removeNote(decoded[0]);

        return ok ? ResponseEntity.ok(null) : ResponseEntity.notFound().build();
    }
}
