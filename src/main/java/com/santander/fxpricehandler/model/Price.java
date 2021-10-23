package com.santander.fxpricehandler.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Price implements Serializable {

    private long id;

    private String instrumentName;

    private double bid;

    private double ask;

    private LocalDateTime timestamp;

    public Price(long id, String instrumentName, double bid, double ask, LocalDateTime timestamp) {
        this.id = id;
        this.instrumentName = instrumentName;
        this.bid = bid;
        this.ask = ask;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public double getBid() {
        return bid;
    }

    public double getAsk() {
        return ask;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Price{" +
                "id=" + id +
                ", instrumentName='" + instrumentName + '\'' +
                ", bid=" + bid +
                ", ask=" + ask +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return id == price.id &&
                Double.compare(price.bid, bid) == 0 &&
                Double.compare(price.ask, ask) == 0 &&
                Objects.equals(instrumentName, price.instrumentName) &&
                Objects.equals(timestamp, price.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, instrumentName, bid, ask, timestamp);
    }
}
