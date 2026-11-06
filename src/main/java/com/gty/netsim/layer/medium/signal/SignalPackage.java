package com.gty.netsim.layer.medium.signal;

import java.util.ArrayDeque;

public class SignalPackage {
    ArrayDeque<Double> signalData;

    public SignalPackage() {
        signalData = new ArrayDeque<>();
    }

    public SignalPackage(ArrayDeque<Double> signal_data) {
        this.signalData = signal_data;
    }

    public double current_signal(){
        return signalData.getFirst();
    }

    public ArrayDeque<Double> getSignalData() {
        return signalData;
    }
}
