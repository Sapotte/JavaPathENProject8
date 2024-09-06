package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;

import java.util.Objects;

public class AttractionInfo {
    private String name;
    private Location location;
    private Double distance;
    private int reward;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttractionInfo that = (AttractionInfo) o;
        return reward == that.reward && Objects.equals(name, that.name) && Objects.equals(location, that.location) && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, location, distance, reward);
    }
}
