package com.gty.netsim.layer.medium.portdevice;

import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;
import com.gty.netsim.layer.medium.spi.SignalSink;

import java.util.Objects;

public abstract class SignalDevice implements SignalSink {
    protected final SignalPort port;

    public SignalDevice(SignalPort port) {
        this.port = Objects.requireNonNull(port, "port");
    }

    protected final void transmit(SignalPackage signal) {
        port.send(signal);
    }

    public abstract void send(SignalPackage signal);
}
