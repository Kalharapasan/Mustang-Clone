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
    private String carImg; // path to image file
    private int categoryID;
    private double rating;

    public Car(int carID, String carName, String carModel, String year, String generation,
               String engineType, String horsepower, String transmission, String color,
               String carImg, int categoryID, double rating) {
        this.carID = carID;
        this.carName = carName;
        this.carModel = carModel;
        this.year = year;
        this.generation = generation;
        this.engineType = engineType;
        this.horsepower = horsepower;
        this.transmission = transmission;
        this.color = color;
        this.carImg = carImg;
        this.categoryID = categoryID;
        this.rating = rating;
    }

    // Getters and setters
    public int getCarID() { return carID; }
    public void setCarID(int carID) { this.carID = carID; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getGeneration() { return generation; }
    public void setGeneration(String generation) { this.generation = generation; }

    public String getEngineType() { return engineType; }
    public void setEngineType(String engineType) { this.engineType = engineType; }

    public String getHorsepower() { return horsepower; }
    public void setHorsepower(String horsepower) { this.horsepower = horsepower; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCarImg() { return carImg; }
    public void setCarImg(String carImg) { this.carImg = carImg; }

    public int getCategoryID() { return categoryID; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}
