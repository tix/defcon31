package com.starp.zoo.entity.zoo;

/**
 * @author covey
 * @date 2019/11/19
 * @description :接收查询结果
 */
public class ComparativeModdel {

    private String offerId;

    private  long count;


    public ComparativeModdel(String offerId, long count) {
        this.offerId = offerId;
        this.count = count;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
