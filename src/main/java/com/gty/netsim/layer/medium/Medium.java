package com.gty.netsim.layer.medium;

import com.gty.netsim.common.Units;
import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;

import java.util.ArrayList;
import java.util.List;

public abstract class Medium {
    // [改] 字段改为 private final，端口列表由 MediumPort 改为接口 SignalPort
    private final Scheduler scheduler;
    private final long signalSpeedMps;
    private final long totalLengthM;
    private final List<SignalPort> ports = new ArrayList<>();
    // [改] 原 static port_count 移到实例上，避免多个介质共享编号
    private int nextPortId;

    protected Medium(Scheduler scheduler, long signalSpeedMps, long totalLengthM) {
        this.scheduler = scheduler;
        this.signalSpeedMps = signalSpeedMps;
        this.totalLengthM = totalLengthM;
    }

    // [改] 原 getPort(SignalDevice) 删除，端口由 topology.MediumAssembler 创建后挂上来
    public final void attach(SignalPort port) {
        ports.add(port);
    }

    // [新增] 端口编号的唯一来源
    public final int nextPortId() {
        return nextPortId++;
    }

    // [改] 原 send() 同步调用对端；现在通过 Scheduler 登记延迟到达事件
    public final void transmit(SignalPort from, SignalPackage signal) {
        for (SignalPort to : ports) {
            if (to == from) {
                continue;
            }
            long delayNs = delayNsBetween(from, to);
            SignalPackage out = attenuate(signal, from, to);
            scheduler.scheduleIn(delayNs, () -> to.deliver(out, delayNs));
        }
    }

    // [改] 原 getDelay(MediumPort, MediumPort)
    protected abstract long delayNsBetween(SignalPort from, SignalPort to);

    // [改] 原 signalAfterLoss(SignalPackage, MediumPort, MediumPort)
    protected abstract SignalPackage attenuate(SignalPackage signal, SignalPort from, SignalPort to);

    // [新增] 子类算时延的公共公式
    protected final long delayNsForLength(long lengthM) {
        return Math.ceilDiv(Math.multiplyExact(lengthM, Units.NS_PER_S), signalSpeedMps);
    }

    protected final long totalLengthM() {
        return totalLengthM;
    }
}
