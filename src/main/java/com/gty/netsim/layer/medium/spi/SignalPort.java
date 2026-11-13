package com.gty.netsim.layer.medium.spi;

import com.gty.netsim.layer.medium.signal.SignalPackage;

public interface SignalPort {

    int id();

    void send(SignalPackage signal);

    void deliver(SignalPackage signal, long delayNs);
}
