package com.treelev.isimple.domain.ui.filter;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.treelev.isimple.domain.ui.Presentable;
import com.treelev.isimple.enumerable.item.DrinkCategory;
import com.treelev.isimple.enumerable.item.ProductType;
import com.treelev.isimple.utils.managers.ProxyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterItemData implements Parcelable {

    private String name;
    private boolean checked;

    public FilterItemData(String name) {
        this(name, false);
    }

    public FilterItemData(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }

    private FilterItemData(Parcel parcel) {
        this.name = parcel.readString();
        this.checked = parcel.readInt() > 0;
    }

    public String getName() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeInt(checked ? 1 : 0);
    }

    public static final Parcelable.Creator<FilterItemData> CREATOR = new Parcelable.Creator<FilterItemData>() {

        public FilterItemData createFromParcel(Parcel in) {
            return new FilterItemData(in);
        }

        public FilterItemData[] newArray(int size) {
            return new FilterItemData[size];
        }
    };


//    private static FilterItemData[] createFilterDataList(int categoryId, int filterId) {
//        return getCategoryFilterData(categoryId, filterId);
//    }
//
//    private List<FilterItemData> getCategoryFilterData(int categoryId, int filterId) {
//        switch (categoryId) {
//            case R.id.category_wine_butt:
//                return getWineFilterData(categoryId, filterId);
//            case R.id.category_spirits_butt:
//                return null;
//            case R.id.category_sparkling_butt:
//                return null;
//            case R.id.category_sake_butt:
//                return null;
//            case R.id.category_porto_heres_butt:
//                return null;
//            case R.id.category_water_butt:
//                return null;
//            default:
//                return null;
//        }
//    }

//    private List<FilterItemData> getWineFilterData(int categoryId, int filterId) {
//        switch (filterId) {
//            case 0:
//                return convertEnumDescToFilterData(Sweetness.getWineSweetness());
//            case 2:
//                break;
//            case 3:
//                return convertYearsToFilterData(categoryId);
//        }
//        return null;
//    }

    public static FilterItemData[] createFromPresentable(Presentable[] array) {
        if (array != null) {
            FilterItemData[] filterData = new FilterItemData[array.length];
            for (int i = 0; i < array.length; i++) {
                filterData[i] = new FilterItemData(array[i].getLabel());
            }
            return filterData;
        } else {
            return new FilterItemData[0];
        }
    }

    public static FilterItemData[] getAvailableManufacture(Context context, DrinkCategory category) {
        ProxyManager proxyManager = ProxyManager.getInstanse();
        List<String> manufactures = proxyManager.getManufactureByCategory(category);
        FilterItemData[] filterList = new FilterItemData[manufactures.size()];
        for (int i = 0; i < manufactures.size(); i++) {
            filterList[i] = new FilterItemData(manufactures.get(i));
        }
        return filterList;
    }

    public static FilterItemData[] getAvailableYears(Context context, DrinkCategory category) {
        ProxyManager proxyManager = ProxyManager.getInstanse();
        List<Integer> years = proxyManager.getYearsByCategory(category);
        FilterItemData[] filterList = new FilterItemData[years.size()];
        for (int i = 0; i < years.size(); i++) {
            filterList[i] = new FilterItemData(years.get(i).toString());
        }
        return filterList;
    }

    public static Map<String, FilterItemData[]> getAvailableCountryRegions(Context context, DrinkCategory category) {
        ProxyManager proxyManager = ProxyManager.getInstanse();
        return proxyManager.getRegionsByCategory(category);
    }

    public static Map<String, FilterItemData[]> getAvailableClassifications(Context context, DrinkCategory category) {
        ProxyManager proxyManager = ProxyManager.getInstanse();
        Map<ProductType, List<String>> classifications = proxyManager.getClassificationsByCategory(category);
        HashMap<String, FilterItemData[]> result = new HashMap<String, FilterItemData[]>();
        for (ProductType productType : classifications.keySet()) {
            List<String> r = classifications.get(productType);
            FilterItemData[] classificationsData;
            if (r != null) {
                classificationsData = new FilterItemData[r.size()];
                for (int i = 0; i < r.size(); i++) {
                    classificationsData[i] = new FilterItemData(r.get(i));
                }
            } else {
                classificationsData = new FilterItemData[0];
            }
            result.put(productType.getLabel(), classificationsData);
        }
        return result;
    }

}
