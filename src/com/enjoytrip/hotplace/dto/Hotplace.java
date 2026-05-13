package com.enjoytrip.hotplace.dto;

public class Hotplace {

    private int    hotplaceId;
    private String userId;
    private String title;
    private String visitDate;
    private String placeType;
    private String description;
    private double latitude;
    private double longitude;
    private String imagePath;
    private String createdAt;

    public Hotplace() {}

    public Hotplace(String userId, String title, String visitDate,
                    String placeType, String description,
                    double latitude, double longitude, String imagePath) {
        this.userId      = userId;
        this.title       = title;
        this.visitDate   = visitDate;
        this.placeType   = placeType;
        this.description = description;
        this.latitude    = latitude;
        this.longitude   = longitude;
        this.imagePath   = imagePath;
    }

    public int getHotplaceId()                   { return hotplaceId; }
    public void setHotplaceId(int hotplaceId)    { this.hotplaceId = hotplaceId; }

    public String getUserId()                    { return userId; }
    public void setUserId(String userId)         { this.userId = userId; }

    public String getTitle()                     { return title; }
    public void setTitle(String title)           { this.title = title; }

    public String getVisitDate()                 { return visitDate; }
    public void setVisitDate(String visitDate)   { this.visitDate = visitDate; }

    public String getPlaceType()                 { return placeType; }
    public void setPlaceType(String placeType)   { this.placeType = placeType; }

    public String getDescription()               { return description; }
    public void setDescription(String description){ this.description = description; }

    public double getLatitude()                  { return latitude; }
    public void setLatitude(double latitude)     { this.latitude = latitude; }

    public double getLongitude()                 { return longitude; }
    public void setLongitude(double longitude)   { this.longitude = longitude; }

    public String getImagePath()                 { return imagePath; }
    public void setImagePath(String imagePath)   { this.imagePath = imagePath; }

    public String getCreatedAt()                 { return createdAt; }
    public void setCreatedAt(String createdAt)   { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Hotplace{hotplaceId=" + hotplaceId + ", userId='" + userId +
               "', title='" + title + "'}";
    }
}
