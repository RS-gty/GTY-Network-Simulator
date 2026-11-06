package com.gty.netsim.layer.medium;

import com.gty.netsim.common.Units;
import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.mediumport.MediumPort;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;

import java.util.ArrayDeque;

public class TwistedPairMedium extends Medium {
    static int port_count = 0;
    long delay = 0;

    public TwistedPairMedium(Scheduler scheduler, long signal_speed, long total_length){
        super(scheduler, signal_speed, total_length);
        this.delay = Math.ceilDiv(Math.multiplyExact(total_length, Units.NS_PER_S), signal_speed);
    }

    @Override
    public MediumPort getPort(SignalDevice signalDevice){
        MediumPort port = new MediumPort(this, signalDevice, port_count++);
        this.signal_ports.add(port);
        return port;
    }

    @Override
    long getDelay(MediumPort port, MediumPort other_port) {
        return this.delay;
    }

    @Override
    SignalPackage signalAfterLoss(SignalPackage signalPackage, MediumPort port, MediumPort other_port) {
        // TODO: 衰减与频率相关，这里采用典型值 2.0dB/100m
        double lossPerHundredMeter = 2.0;

        ArrayDeque<Double> newSignalData = new ArrayDeque<>();
        ArrayDeque<Double> signalData = signalPackage.getSignalData();

        double lossCoefficient = Math.pow(10.0, -lossPerHundredMeter / 20.0);

        for (int i = 0; i < signalData.size(); i++){
            double topData = signalData.removeFirst();
            newSignalData.addLast(topData * lossCoefficient);
            signalData.addLast(topData);
        }

        return new SignalPackage(newSignalData);
    }

}
