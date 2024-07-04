package tfip.project.model;

public enum StripePriceId {
    
    FREE(0, "price_1PX2vX00X8yfGho6SkWxqXb0"),
    LITE(1, "price_1PX2wj00X8yfGho62vufqZlR"),
    STANDARD(2, "price_1PX2xD00X8yfGho6F9dG5kdf"),
    PREMIUM(3, "price_1PX2xk00X8yfGho6TmlaOpLO");

    private final int tier;
    private final String priceId;

    StripePriceId(int tier, String priceId) {
        this.tier = tier;
        this.priceId = priceId;
    }

    public int getTier() {
        return tier;
    }

    public String getPriceId() {
        return priceId;
    }

    public static StripePriceId fromTier(int tier) {
        for (StripePriceId memTier : values()) {
            if (memTier.getTier() == tier)
                return memTier;
        }
        throw new IllegalArgumentException("Invalid tier: " + tier);
    }

    public static StripePriceId fromPriceId(String priceId) {
        for (StripePriceId memPriceId : values()) {
            if (memPriceId.getPriceId().equals(priceId))
                return memPriceId;
        }
        throw new IllegalArgumentException("Invalid Price ID: " + priceId);
    }

}
