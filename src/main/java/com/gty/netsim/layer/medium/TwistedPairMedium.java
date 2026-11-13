package com.gty.netsim.layer.medium;

import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;

public class TwistedPairMedium extends Medium {
    private final long delayNs;

    public TwistedPairMedium(Scheduler scheduler, long signalSpeedMps, long totalLengthM) {
        super(scheduler, signalSpeedMps, totalLengthM);
        this.delayNs = delayNsForLength(totalLengthM);
    }

    @Override
    protected long delayNsBetween(SignalPort from, SignalPort to) {
        return delayNs;
    }

    @Override
    protected SignalPackage attenuate(SignalPackage signal, SignalPort from, SignalPort to) {
        double lossPerHundredMeter = 2.0;
        double lossCoefficient = Math.pow(10.0, -lossPerHundredMeter / 20.0);

        double[] samples = signal.samples();

        double sumSq = 0.0;
        for (double s : samples) sumSq += s * s;
        double rms = Math.sqrt(sumSq / samples.length);

        double peakEstimate = rms * Math.sqrt(2);
        double sigma = Math.max(0.01 * peakEstimate, 1e-3);

        java.util.Random random = new java.util.Random();
        double[] attenuated = new double[samples.length];
        for (int i = 0; i < samples.length; i++) {
            double noise = random.nextGaussian() * sigma;
            attenuated[i] = samples[i] * lossCoefficient + noise;
        }
        return new SignalPackage(attenuated);
    }
}
