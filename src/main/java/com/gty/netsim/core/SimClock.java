package com.gty.netsim.core;

/**
 * 仿真时钟的只读视图。
 *
 * <p>各层只需要知道“现在几点”，不应该拿到推进时间的能力，
 * 因此把读时间抽成接口，由 {@link Scheduler} 实现。</p>
 */
public interface SimClock {

    /** 当前仿真时间，单位纳秒（ns）。仿真开始时为 0。 */
    long nowNs();
}
