package com.gty.netsim.layer.medium;

import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.mediumport.MediumPort;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;

import java.util.ArrayList;

public abstract class Medium {
    long signal_speed = 0;
    long total_length = 0;
    ArrayList<MediumPort> signal_ports = new ArrayList<>();
    Scheduler scheduler;

    public Medium(Scheduler scheduler, long signal_speed, long total_length) {
        this.scheduler = scheduler;
        this.signal_speed = signal_speed;
        this.total_length = total_length;
    }

    public abstract MediumPort getPort(SignalDevice signalDevice);
    abstract long getDelay(MediumPort port, MediumPort other_port);
    abstract SignalPackage signalAfterLoss(SignalPackage signalPackage, MediumPort port, MediumPort other_port);

    public void send(MediumPort port, SignalPackage signal){
        for (MediumPort other_port : signal_ports){
            if (other_port.equals(port)){continue;}
            other_port.receiveSignal(signalAfterLoss(signal, port, other_port), getDelay(port, other_port));
        }
    }
}
