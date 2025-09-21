package com.noteproject.demo.Model;

import java.sql.Timestamp;
import java.util.List;

public class Composition {
    private List<Measure> measures;
    private String title;
    private int id;
    private Timestamp timestamp;
    private String composer;
    private long userId;

    public Composition(int id, String title, String composer, List<Measure> measures, Timestamp timestamp, long userId) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.timestamp = timestamp;
        this.userId = userId;
        this.measures = measures;
    }

    public Composition(int id, String title, String composer, Timestamp timestamp, long userId) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public List<Measure> getMeasures() {
        return this.measures;
    }

    public void setMeasure(List<Measure> measures) {
        this.measures = measures;
    }

    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public String getComposer() {
        return this.composer;
    }
    
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return this.userId;
    }
}
