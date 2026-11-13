package com.gty.netsim.layer.medium.signal;

public record SignalPackage(double[] samples) {

    public SignalPackage {
        samples = samples.clone();
    }

    public double currentSignal() {
        return samples[0];
    }

    public int size() {
        return samples.length;
    }

    @Override
    public double[] samples() {
        return samples.clone();
    }
}
