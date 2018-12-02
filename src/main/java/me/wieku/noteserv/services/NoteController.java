package me.wieku.noteserv.services;

import me.wieku.noteserv.database.Note;
import me.wieku.noteserv.database.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
