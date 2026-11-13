package com.gty.netsim.topology;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.mediumport.MediumPort;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.spi.SignalPort;

import java.util.function.Function;

// [新增] 组合根：唯一创建端口、并完成 端口 ↔ 设备 双向连接的地方
public final class MediumAssembler {
    private MediumAssembler() {
    }

    // [新增] 先造端口 → 造设备 → 绑定 → 最后挂到介质上，避免半成品被广播到
    public static <T extends SignalDevice> T attach(Medium medium, Function<SignalPort, T> factory) {
        MediumPort port = new MediumPort(medium, medium.nextPortId());
        T device = factory.apply(port);
        port.bindSink(device);
        medium.attach(port);
        return device;
    }
}
