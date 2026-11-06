package com.gty.netsim.core;

import java.util.Objects;

/**
 * 调度器内部的事件节点：到达时刻 + 登记序号 + 待执行动作。
 *
 * <p>{@code compareTo} 先比时刻、再比序号，使任意两个事件的先后关系唯一确定：
 * 同一时刻的多个事件按登记顺序执行（FIFO）。这是仿真结果可复现的前提——
 * 若同一时刻的事件顺序不确定，依赖它的误码、重传时序都会变得不可复现。</p>
 *
 * <p>只有 {@link Scheduler} 能创建它；调用方拿到的是 {@link Scheduler.Handle}。</p>
 */
final class SimEvent implements Comparable<SimEvent> {

    private final long timeNs;
    private final long seq;
    private final Runnable action;

    private boolean cancelled;
    private boolean fired;

    SimEvent(long timeNs, long seq, Runnable action) {
        this.timeNs = timeNs;
        this.seq = seq;
        this.action = Objects.requireNonNull(action, "action");
    }

    long timeNs() {
        return timeNs;
    }

    boolean isCancelled() {
        return cancelled;
    }

    /**
     * 只打标记，不从堆里删除。堆的删除是 O(n)，而仿真里“登记了就取消”的事件占比通常不高，
     * 惰性删除（出堆时跳过）在复杂度上更划算。
     *
     * @return true 表示此次取消生效；已执行或已取消过则返回 false
     */
    boolean cancel() {
        if (fired || cancelled) {
            return false;
        }
        cancelled = true;
        return true;
    }

    void fire() {
        fired = true;
        action.run();
    }

    @Override
    public int compareTo(SimEvent other) {
        int byTime = Long.compare(timeNs, other.timeNs);
        return byTime != 0 ? byTime : Long.compare(seq, other.seq);
    }

    @Override
    public String toString() {
        return "SimEvent{t=" + timeNs + "ns, seq=" + seq + (cancelled ? ", cancelled" : "") + "}";
    }
}
