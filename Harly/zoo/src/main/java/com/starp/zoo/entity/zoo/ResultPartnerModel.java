package com.starp.zoo.entity.zoo;

/**
 * @author covey
 */
public class ResultPartnerModel {
    private String identification;

    private String partnerName;

    private String partnerId;

    public ResultPartnerModel(String identification, String partnerName, String partnerId) {
        this.identification = identification;
        this.partnerName = partnerName;
        this.partnerId = partnerId;
    }

    public ResultPartnerModel() {
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
