package com.gty.netsim.layer.medium.mediumport;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;
import com.gty.netsim.layer.medium.spi.SignalSink;

import java.util.Objects;

public class MediumPort implements SignalPort {
    private final int id;
    private final Medium medium;
    private SignalSink sink;

    public MediumPort(Medium medium, int id) {
        this.medium = Objects.requireNonNull(medium, "medium");
        this.id = id;
    }

    public void bindSink(SignalSink sink) {
        if (this.sink != null) {
            throw new IllegalStateException("端口 " + id + " 已绑定设备");
        }
        this.sink = Objects.requireNonNull(sink, "sink");
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void send(SignalPackage signal) {
        medium.transmit(this, signal);
    }

    @Override
    public void deliver(SignalPackage signal, long delayNs) {
        if (sink == null) {
            throw new IllegalStateException("端口 " + id + " 尚未绑定设备");
        }
        sink.onSignal(signal, delayNs);
    }
}
