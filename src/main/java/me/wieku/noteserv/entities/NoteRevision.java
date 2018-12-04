package me.wieku.noteserv.entities;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "NoteRevision")
@Table(name = "notes_revisions")
public class NoteRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int revisionNumber;

    @CreationTimestamp
    private LocalDateTime timestamp;

    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "note_id")
    private Note note;

    public NoteRevision() {
    }

    public NoteRevision(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int number) {
        revisionNumber = number;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getRevisionDate() {
        return timestamp;
    }

}
