package com.starp.zoo.entity.zoo;

/**
 * @author covey
 */
public class ResultTagModel {
    private String identification;

    private String tagName;

    private int  tagType ;

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagType() {
        return tagType;
    }

    public void setTagType(int tagType) {
        this.tagType = tagType;
    }

    public ResultTagModel(String identification, String tagName, int tagType) {
        this.identification = identification;
        this.tagName = tagName;
        this.tagType = tagType;
    }


    public ResultTagModel() {
    }
}
