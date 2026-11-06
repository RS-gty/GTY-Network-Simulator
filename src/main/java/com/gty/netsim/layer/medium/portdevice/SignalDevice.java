package com.gty.netsim.layer.medium.portdevice;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.mediumport.MediumPort;
import com.gty.netsim.layer.medium.signal.SignalPackage;

public abstract class SignalDevice {
    protected MediumPort medium_port;
    public SignalDevice(Medium medium) {
        this.medium_port = medium.getPort(this);
    }

    public abstract void send(SignalPackage signalPackage);

    public abstract void recieve(SignalPackage signalPackage);
}
