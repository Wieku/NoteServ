package me.wieku.noteserv.database;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Note")
@Table(name = "notes")
@EntityListeners(AuditingEntityListener.class)
public class Note {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    @OrderBy("revision_number ASC")
    private List<NoteRevision> revisions = new ArrayList<>();

    public Note(){}

    public Note(String title, String content) {
        addRevision(title, content);
    }

    public NoteRevision addRevision(String title, String content) {
        NoteRevision revision = new NoteRevision(title, content);
        revision.setRevisionNumber(revisions.size());
        revision.setNote(this);
        revisions.add(revision);
        return revision;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getModificationDate() {
        return revisions.get(revisions.size()-1).getRevisionDate();
    }

    public String getTitle() {
        return revisions.get(revisions.size()-1).getTitle();
    }

    public String getContent() {
        return revisions.get(revisions.size()-1).getContent();
    }

    public List<NoteRevision> getRevisions() {
        return revisions;
    }

}
