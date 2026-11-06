package com.gty.netsim.core;

import java.util.Objects;
import java.util.PriorityQueue;

/**
 * 离散事件调度器：整个仿真的唯一时间来源。
 *
 * <p>它只做三件事：登记事件、按 (时刻, 登记序号) 的顺序执行事件、把当前时间推进到
 * 正在执行的事件的时刻。时间不会凭空流逝——没有事件时仿真直接跳到下一个事件的时刻，
 * 因此 1 小时的网络行为可以在毫秒内跑完，且结果与机器性能无关。</p>
 *
 * <p>不变量：</p>
 * <ul>
 *   <li>{@code nowNs} 单调不减，且永远等于“最近一次执行的事件的时刻”（无事件时为 0）。</li>
 *   <li>任何时刻新登记的事件都必须满足 {@code timeNs >= nowNs}，否则抛
 *       {@link IllegalArgumentException}——把“时光倒流”当 bug 立刻暴露，而不是悄悄容忍。</li>
 *   <li>不允许在事件执行过程中重入 {@code run*}（会破坏堆的遍历语义），直接抛
 *       {@link IllegalStateException}。</li>
 * </ul>
 *
 * <p>线程模型：非线程安全，一次仿真只在单线程内使用。</p>
 */
public final class Scheduler implements SimClock {

    /** 可选时事件登记后返回的取消句柄。 */
    public static final class Handle {

        private final SimEvent event;

        private Handle(SimEvent event) {
            this.event = event;
        }

        /** @return true 表示取消成功；事件已执行或已取消过则返回 false */
        public boolean cancel() {
            return event.cancel();
        }
    }

    private final PriorityQueue<SimEvent> queue = new PriorityQueue<>();

    private long nowNs;
    private long nextSeq;
    private long firedCount;
    private boolean running;

    @Override
    public long nowNs() {
        return nowNs;
    }

    /**
     * 在绝对时刻 {@code timeNs} 执行 {@code action}。
     *
     * @throws IllegalArgumentException 时刻早于当前时间
     */
    public Handle scheduleAt(long timeNs, Runnable action) {
        Objects.requireNonNull(action, "action");
        if (timeNs < nowNs) {
            throw new IllegalArgumentException(
                    "不能把事件调度到过去: timeNs=" + timeNs + "ns, nowNs=" + nowNs + "ns");
        }
        SimEvent event = new SimEvent(timeNs, nextSeq++, action);
        queue.add(event);
        return new Handle(event);
    }

    /**
     * 在当前时间 {@code delayNs} 之后执行 {@code action}。层内代码应当优先用它，
     * 这样逻辑与绝对时间解耦（整条链路的时延参数改了，不需要重算绝对时刻）。
     */
    public Handle scheduleIn(long delayNs, Runnable action) {
        if (delayNs < 0) {
            throw new IllegalArgumentException("时延必须非负: delayNs=" + delayNs);
        }
        return scheduleAt(nowNs + delayNs, action);
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * 堆内尚未出堆的事件数，<b>包含</b>已取消但还没被清理的事件。
     * 需要精确待执行数量时用 {@link #runUntil} 的返回值判断。
     */
    public int pendingCount() {
        return queue.size();
    }

    /** 已执行的事件总数（被取消的不计）。 */
    public long firedCount() {
        return firedCount;
    }

    /** 执行下一个事件。 @return false 表示堆已空（或只剩已取消的事件） */
    public boolean runNext() {
        ensureNotRunning();
        return fireNext();
    }

    /**
     * 执行所有时刻 {@code <= horizonNs} 的事件，返回实际执行数量。
     * 执行过程中新登记、且仍在该范围内的事件也会被继续执行。
     *
     * <p>结束后 {@code nowNs} 停在最后一个被执行事件的时刻，而不是 {@code horizonNs}：
     * 时钟不空转，否则 {@code scheduleIn} 的相对时延会被“没发生任何事的一段空档”污染。</p>
     */
    public int runUntil(long horizonNs) {
        ensureNotRunning();
        int fired = 0;
        while (true) {
            SimEvent head = queue.peek();
            if (head == null || head.timeNs() > horizonNs) {
                break;
            }
            fireNext();
            fired++;
        }
        return fired;
    }

    /** 跑完全部事件，返回实际执行数量。 */
    public int runAll() {
        ensureNotRunning();
        int fired = 0;
        while (fireNext()) {
            fired++;
        }
        return fired;
    }

    private boolean fireNext() {
        while (true) {
            SimEvent event = queue.poll();
            if (event == null) {
                return false;
            }
            if (event.isCancelled()) {
                continue;
            }
            nowNs = event.timeNs();
            running = true;
            try {
                event.fire();
            } finally {
                running = false;
            }
            firedCount++;
            return true;
        }
    }

    private void ensureNotRunning() {
        if (running) {
            throw new IllegalStateException("调度器正在执行事件，不允许重入 runNext/runUntil/runAll");
        }
    }
}
