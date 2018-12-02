package me.wieku.noteserv.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>, NoteRepositoryCustom {
}
