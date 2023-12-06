package com.imegga.suitcase;

public class Item {
    String id;
    private String name;
    private String description;
    private String price;
    private String imageUrl;
    private boolean isPurchased;
    private boolean isTagged;
    private double latTag;
    private double lonTag;

    public Item() {
    }
    public Item(String name, String description, String price, String imageUrl, boolean isPurchased) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPurchased = isPurchased;
    }

    public Item(String name, String description, String price, String imageUrl, boolean isPurchased, boolean isTagged,
                double latTag, double lonTag) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.isPurchased = isPurchased;
        this.isTagged = isTagged;
        this.latTag = latTag;
        this.lonTag = lonTag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }

    public boolean isTagged() {
        return isTagged;
    }

    public void setTagged(boolean tagged) {
        isTagged = tagged;
    }

    public double getLatTag() {
        return latTag;
    }

    public void setLatTag(double latTag) {
        this.latTag = latTag;
    }

    public double getLonTag() {
        return lonTag;
    }

    public void setLonTag(double lonTag) {
        this.lonTag = lonTag;
    }
}
