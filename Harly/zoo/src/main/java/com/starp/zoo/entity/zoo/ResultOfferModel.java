package com.starp.zoo.entity.zoo;

/**
 * @author covey
 */
public class ResultOfferModel {
    private String identification;

    private String offerName;

    public ResultOfferModel(String identification, String offerName) {
        this.identification = identification;
        this.offerName = offerName;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }
}
