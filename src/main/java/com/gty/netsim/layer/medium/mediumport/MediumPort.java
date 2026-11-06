package com.gty.netsim.layer.medium.mediumport;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;

import java.util.ArrayDeque;

public class MediumPort {
    private int id;
    private Medium medium;
    private SignalDevice signalDevice;

    public MediumPort(Medium medium, SignalDevice device, int id) {
        this.medium = medium;
        this.signalDevice = device;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public void sendSignal(SignalPackage signalPackage) {
        this.medium.send(this, signalPackage);
    }

    public void receiveSignal(SignalPackage signalPackage, long delay) {
        this.signalDevice.recieve(signalPackage);
    }
}
