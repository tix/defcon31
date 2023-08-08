package com.starp.zoo.constant;

/**
 * @author Charles
 * @date 2018/12/18
 * @description :
 */
public enum NumberEnum {

    /**
     * 0
     */
    ZERO(0),
    /**
     * 1
     */
    ONE(1),
    /**
    *2
    */
    TWO(2),
    /**
     * 3
     */
    THREE(3),
    /**
     * 4
     */
    FOUR(4),
    /**
     * 5
     */
    FIVE(5),
    /**
     * 6
     */
    SIX(6),
    /**
     * 7
     */
    SEVEN(7),
    /**
     * 8
     */
    EIGHT(8),
    /**
     * 9
     */
    NIEN(9),
    /**
     * 10
     */
    TEN(10),
    /**
     * 11
     */
    ELEVEN(11),
    /**
     * 12
     */
    TWELVE(12),
    /**
     * 13
     */
    THIRTENN(13),
    /**
     * 15
     */
    FIFTEEN(15),
    /**
     * 16
     */
    SIX_TEEN(16),
    /**
     * 18
     */
    EIGHT_TEEN(18),
    /**
     * 20
     */
    TWENTY(20),

    TWENTY_FOUR(24),
    /**
     * 29
     */
    TWENTY_NINE(29),
    /**
     * 30
     */
    THIRTY(30),
    /**
     * 31
     */
    THIRTY_ONE(31),
    /**
     * 60
     */
    SIXTY(60),
    /**
     * 66
     */
    SIXTY_SIX(66),
    /**
     * 100
     */
    ONE_HUNDRED(100),
    /**
     * 101
     */
    ONE_HUNDRED_AND_ONE(101),
    /**
     * 105
     */
    ONE_HUNDRED_AND_FIVE(105),
    /**
     * 150
     */
    ONE_HUNDRED_AND_FIFTY(150),
    /**
     * 200
     */
    TWO_HUNDRED(200),
    /**
     * 201
     */
    TWO_HUNDRED_AND_ONE(201),
    /**
     * 214
     */
    TWO_HUNDRED_AND_FOURTEEN(214),
    /**
     * 255
     */
    TWO_HUNDRED_AND_FIVE_FIVE(255),
    /**
     * 256
     */
    TWO_HUNDRED_AND_FIVE_SIXTY(256),
    /**
     * 268
     */
    TWO_HUNDRED_AND_SIXTY_EIGHT(268),

    /**
     * 365
     */
    THREE_HUNDRED_AND_SIXTY_FIVE(365),
    /**
     * 400
     */
    FOUR_HUNDRED(400),
    /**
     * 407
     */
    FOUR_HUNDRED_AND_SEVEN(407),
    /**
     * 410
     */
    FOUR_HUNDRED_AND_TEN(410),
    /**
     * 500
     */
    FIVE_HUNDRED(500),
    /**
     * 502
     */
    FIVE_HUNDRED_AND_TWO(502),
    /**
     * 512
     */
    FIVE_HUNDRED_AND_ONE_TWO(512),
    /**
     * 1000
     */
    ONE_THOUSAND(1000),
    /**
     * 1001
     */
    ONE_THOUSAND_AND_ONE(1001),
    /**
     * 1004
     */
    ONE_THOUSAND_AND_FOUR(1004),
    /**
     * 1024
     */
    ONE_THOUSAND_AND_TWENTY_FOUR(1024),
    /**
     * 1027
     */
    ONE_THOUSAND_AND_TWENTY_SEVEN(1027),
    /**
     * 1800
     */
    ONE_THOUSAND_EIGHT_HUNDRED(1800),
    /**
     * 6379
     */
    SIX_THOUSAND_THREE_HUNDRED_AND_SEVENTY_NINE(6379),
    /**
     * ONE_DAY_MILLISECONDS
     */
    ONE_DAY_MILLISECONDS(86400000),

    /**
     * 1000000
     */
    ONE_MILION(1000000),

    /**
     * ONE_HOUR_MILLISECONDS
     */
    ONE_HOUR_MILLISECONDS(3600000);

    private int num;
    NumberEnum(int num){
        this.num = num;
    }

    public int getNum(){
        return num;
    }
}
