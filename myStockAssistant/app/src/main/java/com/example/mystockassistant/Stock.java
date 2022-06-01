package com.example.mystockassistant;

public class Stock {
    private String stockSymbol = "";
    private String cName = "";
    private Double price = 0.0;
    private Double pChange = 0.0;
    private Double changePercent = 0.0;
    private String value = "Positive";

    public Stock() {
        this.stockSymbol = "None";
        this.cName = "None";
        this.price = 0.0;
        this.pChange = 0.0;
        this.changePercent = 0.0;
    }

    public Stock(String stockSymbol, String cName) {
        this.stockSymbol = stockSymbol;
        this.cName = cName;
        this.price = 0.0;
        this.pChange = 0.0;
        this.changePercent = 0.0;
    }

    public Stock(String stockSymbol, Double price, Double pChange, Double changePercent, String value) {
        this.stockSymbol = stockSymbol;
        this.price = price;
        this.pChange = pChange;
        this.changePercent = changePercent;
        this.value = value;
        this.cName = "Temp";
    }

    public Stock(String stockSymbol, String cName, Double price, Double pChange, Double changePercent, String value) {
        this.cName = cName;
        this.price = price;
        this.pChange = pChange;
        this.changePercent = changePercent;
        this.stockSymbol = stockSymbol;
        this.value = value;
    }

    public String getStockSymbol() {
        return this.stockSymbol;
    }

    public String getCName() {
        return this.cName;
    }

    public Double getPrice() {
        return this.price;
    }

    public Double getPChange() {
        return this.pChange;
    }

    public Double getChangePercent() {
        return this.changePercent;
    }

    public String getValue() {
        return this.value;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setpChange(Double pChange) {
        this.pChange = pChange;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    public void setValue(String str) {
        this.value = str;
    }

}
