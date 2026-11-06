package com.gty.netsim.common;

/**
 * 物理常量。SI 单位、整数字面量、名字带单位后缀。
 * 这个类只放"物理世界给定、与任何场景无关"的数，不放协议参数。
 */
public final class Physics {
    public static final long LIGHT_SPEED_MPS = 299_792_458L;

    private Physics() {
    }

    public static long propagationDelayNs(long distanceMeters) {
        if (distanceMeters < 0) {
            throw new IllegalArgumentException("距离不能为负: " + distanceMeters);
        }
        return Math.ceilDiv(Math.multiplyExact(distanceMeters, Units.NS_PER_S), LIGHT_SPEED_MPS);
    }
}