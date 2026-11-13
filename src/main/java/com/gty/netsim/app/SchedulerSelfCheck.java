package com.gty.netsim.app;

import java.util.ArrayList;
import java.util.List;

import com.gty.netsim.core.Scheduler;

/**
 * v0.1 自检：不依赖 JUnit（当前工程没有构建工具），直接用 main 跑断言。
 * 全部通过时退出码 0；任一失败抛 AssertionError，退出码 1。
 *
 * <p>运行方式（IDEA 里右键 Run 即可，或命令行见 README 说明）：
 * {@code java main.java.com.gty.netsim.app.SchedulerSelfCheck}</p>
 */
public final class SchedulerSelfCheck {

    private static int passed;
    private static int failed;

    public static void main(String[] args) {
        timeOrder();
        fifoAtSameInstant();
        relativeScheduling();
        clockStopsAtLastEvent();
        chainedScheduling();
        cancellation();
        rejectPastTime();
        rejectReentrancy();
        report();
    }

    private static void timeOrder() {
        Scheduler scheduler = new Scheduler();
        List<Integer> order = new ArrayList<>();
        scheduler.scheduleAt(30, () -> order.add(30));
        scheduler.scheduleAt(10, () -> order.add(10));
        scheduler.scheduleAt(20, () -> order.add(20));

        int fired = scheduler.runAll();
        check("乱序登记按时刻执行", fired == 3 && order.equals(List.of(10, 20, 30)));
        check("时刻顺序执行后 nowNs 停在 30", scheduler.nowNs() == 30);
    }

    private static void fifoAtSameInstant() {
        Scheduler scheduler = new Scheduler();
        List<Integer> order = new ArrayList<>();
        scheduler.scheduleAt(5, () -> order.add(1));
        scheduler.scheduleAt(5, () -> order.add(2));
        scheduler.scheduleAt(5, () -> order.add(3));

        scheduler.runAll();
        check("同一时刻按登记顺序执行", order.equals(List.of(1, 2, 3)));
    }

    private static void relativeScheduling() {
        Scheduler scheduler = new Scheduler();
        List<Long> seen = new ArrayList<>();
        scheduler.scheduleIn(100, () -> seen.add(scheduler.nowNs()));

        scheduler.runAll();
        check("scheduleIn(100) 在 100ns 触发", seen.equals(List.of(100L)));
    }

    private static void clockStopsAtLastEvent() {
        Scheduler scheduler = new Scheduler();
        scheduler.scheduleAt(50, () -> { });

        int fired = scheduler.runUntil(10_000);
        check("runUntil(10000) 只执行了 1 个事件", fired == 1);
        check("时钟停在 50ns 而非 10000ns", scheduler.nowNs() == 50);
    }

    private static void chainedScheduling() {
        Scheduler scheduler = new Scheduler();
        List<Long> seen = new ArrayList<>();
        scheduler.scheduleAt(10, () -> {
            seen.add(scheduler.nowNs());
            scheduler.scheduleIn(5, () -> seen.add(scheduler.nowNs()));
        });

        scheduler.runUntil(12);
        check("horizon 外的新事件留到下次", seen.equals(List.of(10L)) && scheduler.nowNs() == 10);

        scheduler.runAll();
        check("链式事件在 15ns 执行", seen.equals(List.of(10L, 15L)) && scheduler.nowNs() == 15);
    }

    private static void cancellation() {
        Scheduler scheduler = new Scheduler();
        List<Integer> order = new ArrayList<>();
        scheduler.scheduleAt(10, () -> order.add(1));
        Scheduler.Handle handle = scheduler.scheduleAt(20, () -> order.add(2));
        scheduler.scheduleAt(30, () -> order.add(3));

        check("首次取消返回 true", handle.cancel());
        check("重复取消返回 false", !handle.cancel());
        scheduler.runAll();
        check("被取消的事件不执行", order.equals(List.of(1, 3)));
        check("firedCount 不含已取消事件", scheduler.firedCount() == 2);
    }

    private static void rejectPastTime() {
        Scheduler scheduler = new Scheduler();
        scheduler.scheduleAt(50, () -> { });
        scheduler.runAll();

        boolean threw = false;
        try {
            scheduler.scheduleAt(49, () -> { });
        } catch (IllegalArgumentException expected) {
            threw = true;
        }
        check("调度到过去抛 IllegalArgumentException", threw && scheduler.nowNs() == 50);

        boolean negativeDelay = false;
        try {
            scheduler.scheduleIn(-1, () -> { });
        } catch (IllegalArgumentException expected) {
            negativeDelay = true;
        }
        check("负时延抛 IllegalArgumentException", negativeDelay);
    }

    private static void rejectReentrancy() {
        Scheduler scheduler = new Scheduler();
        boolean[] threw = {false};
        scheduler.scheduleAt(10, () -> {
            try {
                scheduler.runNext();
            } catch (IllegalStateException expected) {
                threw[0] = true;
            }
        });

        scheduler.runAll();
        check("事件内重入 runNext 被拒绝", threw[0]);
    }

    private static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    private static void report() {
        System.out.println();
        System.out.println("通过 " + passed + " 项，失败 " + failed + " 项");
        if (failed > 0) {
            throw new AssertionError(failed + " 项自检未通过");
        }
    }
}
