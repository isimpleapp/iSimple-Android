package com.treelev.isimple.domain.db;

import com.treelev.isimple.enumerable.item.*;

import java.io.Serializable;

public class Item implements Serializable {

    public final static String UI_TAG_ID = "_id";
    public final static String UI_TAG_NAME = "name";
    public final static String UI_TAG_LOCALIZATION_NAME = "localized_name";
    public final static String UI_TAG_PRICE = "price";
    public final static String UI_TAG_DRINK_CATEGORY = "drink_category";
    public final static String UI_TAG_VOLUME = "volume";
    public final static String UI_TAG_IMAGE = "image";
    public final static String UI_TAG_DRINK_ID = "drink_id";

    private String itemID;
    private String drinkID;
    private String name;
    private String localizedName;
    private String manufacturer;
    private String localizedManufacturer;
    private Float price;
    private Float priceMarkup;
    private String country;
    private String region;
    private String barcode;
    private DrinkCategory drinkCategory;
    private ItemColor color;
    private String style;
    private Sweetness sweetness;
    private Integer year;
    private Float volume;
    private ProductType productType;
    private String drinkType;
    private String classification;
    private String alcohol;
    private String bottleHiResolutionImageFilename;
    private String bottleLowResolutionImageFilename;
    private String styleDescription;
    private String appelation;
    private String servingTempMin;
    private String servingTempMax;
    private String tasteQualities;
    private String vintageReport;
    private String agingProcess;
    private String productionProcess;
    private String interestingFacts;
    private String labelHistory;
    private String gastronomy;
    private String vineyard;
    private String grapesUsed;
    private String rating;
    private Float quantity;
    private Integer leftOvers;
    private Boolean isFavourite = false;

    public static String[] getUITags() {
        return new String[] { UI_TAG_NAME, UI_TAG_LOCALIZATION_NAME, UI_TAG_VOLUME, UI_TAG_PRICE, UI_TAG_DRINK_CATEGORY };
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getDrinkID() {
        return drinkID;
    }

    public void setDrinkID(String drinkID) {
        this.drinkID = drinkID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getLocalizedManufacturer() {
        return localizedManufacturer;
    }

    public void setLocalizedManufacturer(String localizedManufacturer) {
        this.localizedManufacturer = localizedManufacturer;
    }

    public Float getPrice() {
        return price ;
    }


    public void setPrice(Float price) {
        this.price = price;
    }

    public boolean hasPrice(){
        return price != null;
    }

    public Float getPriceMarkup() {
        return priceMarkup;
    }

    public void setPriceMarkup(Float priceMarkup) {
        this.priceMarkup = priceMarkup;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public DrinkCategory getDrinkCategory() {
        if (drinkCategory != null) {
            return drinkCategory;
        } else {
            return DrinkCategory.OTHER;
        }
    }

    public void setDrinkCategory(DrinkCategory drinkCategory) {
        this.drinkCategory = drinkCategory;
    }

    public ItemColor getColor() {
        if (color != null) {
            return color;
        } else {
            return ItemColor.OTHER;
        }
    }

    public void setColor(ItemColor color) {
        this.color = color;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Sweetness getSweetness() {
        if (sweetness != null) {
            return sweetness;
        } else {
            return Sweetness.OTHER;
        }
    }

    public void setSweetness(Sweetness sweetness) {
        this.sweetness = sweetness;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public boolean hasYear(){
        return year != null && year != 0;
    }

    public Float getVolume() {
        return volume;
    }

    public void setVolume(Float volume) {
        this.volume = volume;
    }

    public String getDrinkType() {
        return drinkType;
    }

    public void setDrinkType(String drinkType) {
        this.drinkType = drinkType;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public boolean hasAlcohol(){
        return alcohol != null && !alcohol.trim().equals("0") && !alcohol.equals("0.0");
    }

    public String getBottleHiResolutionImageFilename() {
        return bottleHiResolutionImageFilename;
    }

    public void setBottleHiResolutionImageFilename(String bottleHiResolutionImageFilename) {
        this.bottleHiResolutionImageFilename = bottleHiResolutionImageFilename;
    }

    public String getBottleLowResolutionImageFilename() {
        return bottleLowResolutionImageFilename;
    }

    public void setBottleLowResolutionImageFilename(String bottleLowResolutionImageFilename) {
        this.bottleLowResolutionImageFilename = bottleLowResolutionImageFilename;
    }

    public String getStyleDescription() {
        return styleDescription;
    }

    public void setStyleDescription(String styleDescription) {
        this.styleDescription = styleDescription;
    }

    public String getAppelation() {
        return appelation;
    }

    public void setAppelation(String appelation) {
        this.appelation = appelation;
    }

    public String getServingTempMin() {
        return servingTempMin;
    }

    public void setServingTempMin(String servingTempMin) {
        this.servingTempMin = servingTempMin;
    }

    public String getServingTempMax() {
        return servingTempMax;
    }

    public void setServingTempMax(String servingTempMax) {
        this.servingTempMax = servingTempMax;
    }

    public String getTasteQualities() {
        return tasteQualities;
    }

    public void setTasteQualities(String tasteQualities) {
        this.tasteQualities = tasteQualities;
    }

    public String getVintageReport() {
        return vintageReport;
    }

    public void setVintageReport(String vintageReport) {
        this.vintageReport = vintageReport;
    }

    public String getAgingProcess() {
        return agingProcess;
    }

    public void setAgingProcess(String agingProcess) {
        this.agingProcess = agingProcess;
    }

    public String getProductionProcess() {
        return productionProcess;
    }

    public void setProductionProcess(String productionProcess) {
        this.productionProcess = productionProcess;
    }

    public String getInterestingFacts() {
        return interestingFacts;
    }

    public void setInterestingFacts(String interestingFacts) {
        this.interestingFacts = interestingFacts;
    }

    public String getLabelHistory() {
        return labelHistory;
    }

    public void setLabelHistory(String labelHistory) {
        this.labelHistory = labelHistory;
    }

    public String getGastronomy() {
        return gastronomy;
    }

    public void setGastronomy(String gastronomy) {
        this.gastronomy = gastronomy;
    }

    public String getVineyard() {
        return vineyard;
    }

    public void setVineyard(String vineyard) {
        this.vineyard = vineyard;
    }

    public String getGrapesUsed() {
        return grapesUsed;
    }

    public void setGrapesUsed(String grapesUsed) {
        this.grapesUsed = grapesUsed;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public Boolean getFavourite() {
        return isFavourite;
    }

    public void setFavourite(Boolean favourite) {
        isFavourite= favourite;
    }

    public int getLeftOvers(){
        return leftOvers != null ? leftOvers : 0;
    }
}
