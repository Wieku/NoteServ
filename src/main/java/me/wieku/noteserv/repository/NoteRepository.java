package me.wieku.noteserv.repository;

import me.wieku.noteserv.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>, NoteRepositoryCustom {
}
