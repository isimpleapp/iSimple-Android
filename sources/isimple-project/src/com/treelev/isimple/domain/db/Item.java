package com.treelev.isimple.domain.db;

import com.treelev.isimple.enumerable.item.Color;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.Style;
import com.treelev.isimple.enumerable.item.Sweetness;

import java.io.Serializable;

public class Item implements Serializable {

    public final static String UI_TAG_ID = "_id";
    public final static String UI_TAG_NAME = "name";
    public final static String UI_TAG_LOCALIZATION_NAME = "localized_name";
    public final static String UI_TAG_PRICE = "price";
    public final static String UI_TAG_DRINK_CATEGORY = "drink_category";
    public final static String UI_TAG_VOLUME = "volume";
    public final static String UI_TAG_IMAGE = "image";

    private String itemID;
    private String drinkID;
    private String name;
    private String localizedName;
    private String manufacturer;
    private String localizedManufacturer;
    private String price;
    private String priceMarkup;
    private String country;
    private String region;
    private String barcode;
    private DrinkCategory drinkCategory;
    private Color color;
    private Style style;
    private Sweetness sweetness;
    private String year;
    private String volume;
    private String drinkType;
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

    public static String[] getUITags() {
        return new String[] { UI_TAG_IMAGE, UI_TAG_NAME, UI_TAG_LOCALIZATION_NAME, UI_TAG_VOLUME, UI_TAG_PRICE, UI_TAG_DRINK_CATEGORY };
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceMarkup() {
        return priceMarkup;
    }

    public void setPriceMarkup(String priceMarkup) {
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
            return DrinkCategory.UNKNOWN;
        }
    }

    public void setDrinkCategory(DrinkCategory drinkCategory) {
        this.drinkCategory = drinkCategory;
    }

    public Color getColor() {
        if (color != null) {
            return color;
        } else {
            return Color.UNKNOWN;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Style getStyle() {
        if (style != null) {
            return style;
        } else {
            return Style.UNKNOWN;
        }
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Sweetness getSweetness() {
        if (sweetness != null) {
            return sweetness;
        } else {
            return Sweetness.UNKNOWN;
        }
    }

    public void setSweetness(Sweetness sweetness) {
        this.sweetness = sweetness;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
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

//    private Item(Parcel in) {
//        itemID = in.readString();
//        drinkID = in.readString();
//        name = in.readString();
//        localizedName = in.readString();
//        manufacturer = in.readString();
//        localizedManufacturer = in.readString();
//        price = in.readString();
//        priceMarkup = in.readString();
//        country = in.readString();
//        region = in.readString();
//        barcode = in.readString();
//        drinkCategory = in.readParcelable(DrinkCategory.class.getClassLoader());
//        color = in.readParcelable(Color.class.getClassLoader());
//        style = in.readParcelable(Style.class.getClassLoader());
//        sweetness = in.readParcelable(Sweetness.class.getClassLoader());
//        year = in.readString();
//        volume = in.readString();
//        drinkType = in.readString();
//        alcohol = in.readString();
//        bottleHiResolutionImageFilename = in.readString();
//        bottleLowResolutionImageFilename = in.readString();
//        styleDescription = in.readString();
//        appelation = in.readString();
//        servingTempMin = in.readString();
//        servingTempMax = in.readString();
//        tasteQualities = in.readString();
//        vintageReport = in.readString();
//        agingProcess = in.readString();
//        productionProcess = in.readString();
//        interestingFacts = in.readString();
//        labelHistory = in.readString();
//        gastronomy = in.readString();
//        vineyard = in.readString();
//        grapesUsed = in.readString();
//        rating = in.readString();
//    }
//
//    public static final Parcelable.Creator<Item> CREATOR =
//            new Parcelable.Creator<Item>() {
//
//                @Override
//                public Item createFromParcel(Parcel source) {
//                    return new Item(source);
//                }
//
//                @Override
//                public Item[] newArray(int size) {
//                    return new Item[size];
//                }
//
//            };
//
//
//    @Override
//    public int describeContents() {
//        return 0;  //To change body of implemented methods use File | Settings | File Templates.
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int flags) {
//        parcel.writeString(name);
//        parcel.writeString(localizedName);
//        parcel.writeString(manufacturer);
//        parcel.writeString(localizedManufacturer);
//        parcel.writeString(price);
//        parcel.writeString(priceMarkup);
//        parcel.writeString(country);
//        parcel.writeString(region);
//        parcel.writeString(barcode);
//        parcel.writeParcelable(drinkCategory, flags);
//        parcel.writeParcelable(color, flags);
//        parcel.writeParcelable(style, flags);
//        parcel.writeParcelable(sweetness, flags);
////        if(drinkCategory != null) {
////            parcel.writeString(drinkCategory.getDescription());
////        } else {
////            parcel.writeString(null);
////        }
////        if(color != null) {
////            parcel.writeString(color.getDescription());
////        } else {
////            parcel.writeString(null);
////        }
////        if(style != null) {
////            parcel.writeString(style.getDescription());
////        } else {
////            parcel.writeString(null);
////        }
////        if(sweetness != null) {
////            parcel.writeString(sweetness.getDescription());
////        } else {
////            parcel.writeString(null);
////        }
//        parcel.writeString(year);
//        parcel.writeString(volume);
//        parcel.writeString(drinkType);
//        parcel.writeString(alcohol);
//        parcel.writeString(bottleHiResolutionImageFilename);
//        parcel.writeString(bottleLowResolutionImageFilename);
//        parcel.writeString(styleDescription);
//        parcel.writeString(appelation);
//        parcel.writeString(servingTempMin);
//        parcel.writeString(servingTempMax);
//        parcel.writeString(tasteQualities);
//        parcel.writeString(vintageReport);
//        parcel.writeString(agingProcess);
//        parcel.writeString(productionProcess);
//        parcel.writeString(interestingFacts);
//        parcel.writeString(labelHistory);
//        parcel.writeString(gastronomy);
//        parcel.writeString(vineyard);
//        parcel.writeString(grapesUsed);
//        parcel.writeString(rating);
//    }
}
