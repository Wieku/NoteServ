package me.wieku.noteserv.services;

import me.wieku.noteserv.database.Note;
import me.wieku.noteserv.database.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class NoteController {

    @Autowired
    private NoteRepository repository;

    @RequestMapping("/create")
    public ResponseEntity<Object> createNote(@RequestParam(value="title") String title, @RequestParam(value="content") String content) {
        if (title.length() == 0 || content.length() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Note note = new Note(title, content);
        repository.addNote(note);
        return ResponseEntity.ok(note.getId());
    }

    @RequestMapping("/update/{noteId}")
    public ResponseEntity<Object> updateNote(@PathVariable Long noteId, @RequestParam(value="title") String title, @RequestParam(value="content") String content) {
        if (title.length() == 0 || content.length() == 0 || noteId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Note note = new Note(title, content);
        repository.updateNote(noteId, note);
        return ResponseEntity.ok(null);
    }

    @RequestMapping("/history/{noteId}")
    public ResponseEntity<Object> getHistory(@PathVariable Long noteId) {
        if (noteId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        List<Note> notes = repository.getHistory(noteId);

        return ResponseEntity.ok(notes);
    }

    @RequestMapping({"/get/{noteId}", "/get/{noteId}/{revisionId}"})
    public ResponseEntity<Object> getNote(@PathVariable Long noteId, @PathVariable(required = false) Integer revisionId) {
        if (noteId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Note note;

        if (revisionId == null) {
            note = repository.getNewestNote(noteId);
        } else {
            note = repository.getNoteRevision(noteId, revisionId);
        }

        return ResponseEntity.ok(note);
    }

}
