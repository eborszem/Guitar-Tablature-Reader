package com.noteproject.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chords")
public class Chord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "measure_id", nullable = false)
    private Integer measureId;

    @Column(name = "low_e_string")
    private String lowEString;

    @Column(name = "a_string")
    private String aString;

    @Column(name = "d_string")
    private String dString;

    @Column(name = "g_string")
    private String gString;

    @Column(name = "b_string")
    private String bString;

    @Column(name = "high_e_string")
    private String highEString;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "chord_number")
    private Integer chordNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMeasureId() {
        return measureId;
    }

    public void setMeasureId(Integer measureId) {
        this.measureId = measureId;
    }

    public String getLowEString() {
        return lowEString;
    }

    public void setLowEString(String lowEString) {
        this.lowEString = lowEString;
    }

    public String getAString() {
        return aString;
    }

    public void setAString(String aString) {
        this.aString = aString;
    }

    public String getDString() {
        return dString;
    }

    public void setDString(String dString) {
        this.dString = dString;
    }

    public String getGString() {
        return gString;
    }

    public void setGString(String gString) {
        this.gString = gString;
    }

    public String getBString() {
        return bString;
    }

    public void setBString(String bString) {
        this.bString = bString;
    }

    public String getHighEString() {
        return highEString;
    }

    public void setHighEString(String highEString) {
        this.highEString = highEString;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getChordNumber() {
        return chordNumber;
    }

    public void setChordNumber(Integer chordNumber) {
        this.chordNumber = chordNumber;
    }
}
