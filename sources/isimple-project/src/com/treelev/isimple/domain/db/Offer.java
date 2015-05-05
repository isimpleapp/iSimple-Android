
package com.treelev.isimple.domain.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Offer {

    private long id;
    private String name;
    private String url;
    private int expired;
    private String image;
    private String image1200;
    private String imagehdpi;
    private String image2x;
    private String imageipad;
    private String imageipad2x;
    private String description;
    private int prioritized;
    private List<Long> itemsList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isExpired() {
        if (expired == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void setExpired(int expired) {
        this.expired = expired;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage1200() {
        return image1200;
    }

    public void setImage1200(String image1200) {
        this.image1200 = image1200;
    }

    public String getImagehdpi() {
        return imagehdpi;
    }

    public void setImagehdpi(String imagehdpi) {
        this.imagehdpi = imagehdpi;
    }

    public String getImage2x() {
        return image2x;
    }

    public void setImage2x(String image2x) {
        this.image2x = image2x;
    }

    public String getImageipad() {
        return imageipad;
    }

    public void setImageipad(String imageipad) {
        this.imageipad = imageipad;
    }

    public String getImageipad2x() {
        return imageipad2x;
    }

    public void setImageipad2x(String imageipad2x) {
        this.imageipad2x = imageipad2x;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrioritized() {
        return prioritized;
    }

    public void setPrioritized(int prioritized) {
        this.prioritized = prioritized;
    }

    public List<Long> getItemsList() {
        return itemsList;
    }
    
    public void setItemsList(List<Long> itemsList) {
        this.itemsList = itemsList;
    }

    public void setItemsList(String itemsList) {
        this.itemsList = new ArrayList<Long>();
        List<String> itemsStringsList = Arrays.asList(itemsList.split(","));
        for (String itemId : itemsStringsList) {
            this.itemsList.add(Long.valueOf(itemId));
        }
    }

}
