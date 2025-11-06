package com.example.mustang_clone;

public class Car {
    private int carID;
    private String carName;
    private String carModel;
    private String year;
    private String generation;
    private String engineType;
    private String horsepower;
    private String transmission;
    private String color;
    private String img;
    private int categoryID;
    private double rating;

    public Car(int carID, String carName, String carModel, String year, String generation,
               String engineType, String horsepower, String transmission, String color,
               String img, int categoryID, double rating) {
        this.carID = carID;
        this.carName = carName;
        this.carModel = carModel;
        this.year = year;
        this.generation = generation;
        this.engineType = engineType;
        this.horsepower = horsepower;
        this.transmission = transmission;
        this.color = color;
        this.img = img;
        this.categoryID = categoryID;
        this.rating = rating;
    }

    public Car() {} // optional empty constructor

    public int getCarID() { return carID; }
    public String getCarName() { return carName; }
    public String getCarModel() { return carModel; }
    public String getYear() { return year; }
    public String getGeneration() { return generation; }
    public String getEngineType() { return engineType; }
    public String getHorsepower() { return horsepower; }
    public String getTransmission() { return transmission; }
    public String getColor() { return color; }
    public String getImg() { return img; }
    public int getCategoryID() { return categoryID; }
    public double getRating() { return rating; }

    public void setImg(String img) { this.img = img; }
}
