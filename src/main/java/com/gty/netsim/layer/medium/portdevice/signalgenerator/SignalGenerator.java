package com.gty.netsim.layer.medium.portdevice.signalgenerator;

import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;

public abstract class SignalGenerator extends SignalDevice {
    public SignalGenerator(SignalPort port) {
        super(port);
    }

    public abstract void generate(int duration);

    @Override
    public void send(SignalPackage signal) {
        transmit(signal);
    }

    @Override
    public void onSignal(SignalPackage signal, long delayNs) {
    }
}
