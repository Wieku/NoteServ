package me.wieku.noteserv.services;

import me.wieku.noteserv.database.Note;
import me.wieku.noteserv.database.NoteRepository;
import me.wieku.noteserv.database.NoteRevision;
import me.wieku.noteserv.view.NewestNote;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository repository;

    private Hashids hashids = new Hashids("67rbg8567GNFY", 4);

    @RequestMapping(value = "/notes", method = RequestMethod.POST)
    public ResponseEntity<Object> createNote(@RequestParam(value = "title") String title, @RequestParam(value = "content") String content, UriComponentsBuilder b) {
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.badRequest().build();
        }
        Note note = new Note(title, content);
        repository.addNote(note);

        UriComponents c = b.path("/notes/{id}").buildAndExpand(hashids.encode(note.getId()));

        return ResponseEntity.created(c.toUri()).build();
    }

    @RequestMapping(value = "/notes/{noteId}", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateNote(@PathVariable String noteId, @RequestParam(value = "title") String title, @RequestParam(value = "content") String content) {
        long[] decoded;
        if (title.length() == 0 || content.length() == 0 || noteId == null || (decoded = hashids.decode(noteId)).length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        NoteRevision note = new NoteRevision(title, content);
        boolean ok = repository.updateNote(decoded[0], note);

        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/notes/{noteId}/history", method = RequestMethod.GET)
    public ResponseEntity<Object> getHistory(@PathVariable String noteId) {
        long[] decoded;
        if (noteId == null || (decoded = hashids.decode(noteId)).length == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<NoteRevision> notes = repository.getHistory(decoded[0]);

        return ResponseEntity.ok(notes);
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

    @RequestMapping(value = {"/notes/{noteId}", "/notes/{noteId}/{revisionId}"}, method = RequestMethod.GET)
    public ResponseEntity<Object> getNote(@PathVariable String noteId, @PathVariable(required = false) Integer revisionId) {
        long[] decoded;
        if (noteId == null || (decoded = hashids.decode(noteId)).length == 0) {
            return ResponseEntity.notFound().build();
        }

        if (revisionId == null) {
            Note note = repository.getNewestNote(decoded[0]);
            if (note == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(new NewestNote(note.getTitle(), note.getContent(), note.getCreationDate(), note.getModificationDate()));
            }
        } else {
            NoteRevision revision = repository.getNoteRevision(decoded[0], revisionId);
            if (revision == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(revision);
            }
        }
    }

}
