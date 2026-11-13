package com.gty.netsim.layer.medium.spi;

import com.gty.netsim.layer.medium.signal.SignalPackage;

@FunctionalInterface
public interface SignalSink {

    void onSignal(SignalPackage signal, long delayNs);
}
