package com.example.myapplication.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;




@Entity(tableName = "excursions")
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    private int excursionID;
    private String excursionName;
    private double excursionPrice;
    private int vacationID;

    public Excursion(int excursionID, String excursionName, double excursionPrice, int vacationID) {
        this.excursionID = excursionID;
        this.excursionName = excursionName;
        this.excursionPrice = excursionPrice;
        this.vacationID = vacationID;
    }

    public int getExcursionID() {
        return excursionID;
    }

    public void setExcursionID(int excursionID) {
        this.excursionID = excursionID;
    }

    public String getExcursionName() {
        return excursionName;
    }

    public void setExcursionName(String excursionName) {
        this.excursionName = excursionName;
    }

    public double getExcursionPrice() {
        return excursionPrice;
    }

    public void setExcursionPrice(double excursionPrice) {
        this.excursionPrice = excursionPrice;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }


    }

