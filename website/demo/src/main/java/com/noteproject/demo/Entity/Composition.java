
package com.noteproject.demo.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compositions")
public class Composition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 255)
    private String composer;

    @Column(name = "time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime time;

    @Column(name = "note_value")
    private Integer noteValue;

    @Column(name = "num_note_values_per_measure")
    private Integer numNoteValuesPerMeasure;

    public Composition() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Integer getNoteValue() {
        return noteValue;
    }

    public void setNoteValue(Integer noteValue) {
        this.noteValue = noteValue;
    }

    public Integer getNumNoteValuesPerMeasure() {
        return numNoteValuesPerMeasure;
    }

    public void setNumNoteValuesPerMeasure(Integer numNoteValuesPerMeasure) {
        this.numNoteValuesPerMeasure = numNoteValuesPerMeasure;
    }
}
