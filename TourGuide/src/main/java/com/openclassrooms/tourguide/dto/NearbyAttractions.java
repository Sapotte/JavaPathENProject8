package com.openclassrooms.tourguide.dto;

import gpsUtil.location.Location;

import java.util.List;
import java.util.Objects;

public class NearbyAttractions {
    private Location userLocation;
    List<AttractionInfo> nearbyAttractionsInfo;

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public List<AttractionInfo> getNearbyAttractionsInfo() {
        return nearbyAttractionsInfo;
    }

    public void setNearbyAttractionsInfo(List<AttractionInfo> nearbyAttractionsInfo) {
        this.nearbyAttractionsInfo = nearbyAttractionsInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyAttractions that = (NearbyAttractions) o;
        return Objects.equals(userLocation, that.userLocation) && Objects.equals(nearbyAttractionsInfo, that.nearbyAttractionsInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userLocation, nearbyAttractionsInfo);
    }
}
