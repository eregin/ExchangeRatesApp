package com.example.app.Entities;

import java.util.Date;

public class PointGraph {
    private Double value;
    private Date date;

    public PointGraph() {
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
