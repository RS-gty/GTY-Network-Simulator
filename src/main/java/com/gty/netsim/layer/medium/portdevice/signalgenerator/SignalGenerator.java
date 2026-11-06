package com.gty.netsim.layer.medium.portdevice.signalgenerator;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.mediumport.MediumPort;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;

public abstract class SignalGenerator extends SignalDevice {
    public SignalGenerator(Medium medium) {
        super(medium);
    }

    public abstract void generate(int duration);

    @Override
    public void send(SignalPackage signalPackage) {
        this.medium_port.sendSignal(signalPackage);
    }

    @Override
    public void recieve(SignalPackage signalPackage) {
        return;
    }
}
