package DTOs;

import java.util.ArrayList;
import java.util.List;

public class RoomSearchCriteriaDTO {
    public enum SortBy {
        NONE,
        PRICE_ASC,
        PRICE_DESC,
        RATING_DESC,
        CREATED_AT_DESC,
        AREA_ASC,
        AREA_DESC
    }

    private String keyword;
    private Double minPrice;
    private Double maxPrice;
    private List<Integer> amenityIds = new ArrayList<>();
    private SortBy sortBy = SortBy.NONE;
    private Boolean status;
    private Boolean availability;
    private Integer landlordId;

    public RoomSearchCriteriaDTO() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<Integer> getAmenityIds() {
        return amenityIds;
    }

    public void setAmenityIds(List<Integer> amenityIds) {
        this.amenityIds = amenityIds == null ? new ArrayList<>() : new ArrayList<>(amenityIds);
    }

    public SortBy getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy == null ? SortBy.NONE : sortBy;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public Integer getLandlordId() {
        return landlordId;
    }

    public void setLandlordId(Integer landlordId) {
        this.landlordId = landlordId;
    }

    public boolean hasAmenityFilter() {
        return amenityIds != null && !amenityIds.isEmpty();
    }
}
