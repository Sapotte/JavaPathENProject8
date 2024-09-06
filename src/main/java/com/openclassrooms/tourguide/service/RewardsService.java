package com.openclassrooms.tourguide.service;

import com.openclassrooms.tourguide.dto.AttractionInfo;
import com.openclassrooms.tourguide.user.User;
import com.openclassrooms.tourguide.user.UserReward;
import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private final int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
    private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	private final ExecutorService executorService = Executors.newFixedThreadPool(15);
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}

	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	public void calculateRewardsForAllUsers(List<User> users) {
		try {
			List<CompletableFuture<Void>> futures = users.stream()
					.map(user -> CompletableFuture.runAsync(() -> calculateRewards(user), executorService))
					.toList();

			CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
		} finally {
			executorService.shutdown();
		}
	}
	
	public void calculateRewards(User user) {
		Set<VisitedLocation> userLocationsNotRewarded = user.getVisitedLocations()
				.stream()
				.filter(loc
						-> !user.getUserRewards().stream().map(UserReward::getVisitedLocation).toList().contains(loc)).collect(Collectors.toSet());

		Set<Attraction> attractionsNotRewarded = gpsUtil.getAttractions()
				.stream()
				.filter(attraction
						-> !user.getUserRewards().stream().map(UserReward::getAttraction).toList().contains(attraction)).collect(Collectors.toSet());
		Set<UserReward> newRewards = new HashSet<>();
		for(VisitedLocation visitedLocation : userLocationsNotRewarded) {
			for(Iterator<Attraction> iterator = attractionsNotRewarded.iterator(); iterator.hasNext();) {
				Attraction attraction = iterator.next();
					if(nearAttraction(visitedLocation, attraction)) {
						newRewards.add(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						iterator.remove();
					}
			}
		}
		newRewards.stream().filter(rew -> !user.getUserRewards().contains(rew)).forEach(user::addUserReward);
	}

	public AttractionInfo getAttractionInfo(Attraction attraction, VisitedLocation userLocation) {
		AttractionInfo attractionInfo = new AttractionInfo();
		attractionInfo.setName(attraction.attractionName);
		attractionInfo.setLocation(new Location(attraction.latitude, attraction.longitude));
		double distance = getDistance(attraction, userLocation.location);
		attractionInfo.setDistance(distance);
		int rewardPoints = rewardsCentral.getAttractionRewardPoints(attraction.attractionId, userLocation.userId);
		attractionInfo.setReward(rewardPoints);
		return attractionInfo;
	}

	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        int attractionProximityRange = 200;
        return !(getDistance(attraction, location) > attractionProximityRange);
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
	}

}
