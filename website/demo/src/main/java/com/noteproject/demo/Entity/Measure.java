package com.noteproject.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "measures")
public class Measure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composition_id", nullable = false)
    private Composition composition;

    @Column(name = "measure_number", nullable = false)
    private Integer measureNumber;

    public Measure() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Composition getComposition() {
        return composition;
    }

    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    public Integer getMeasureNumber() {
        return measureNumber;
    }

    public void setMeasureNumber(Integer measureNumber) {
        this.measureNumber = measureNumber;
    }
}
