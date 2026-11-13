package com.gty.netsim.layer.medium.portdevice.signalgenerator;

import com.gty.netsim.common.Units;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;

public class SineWaveGenerator extends SignalGenerator {
    private final long frequency;
    private final long amplitude;

    public SineWaveGenerator(SignalPort port, long frequency, long amplitude) {
        super(port);
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    @Override
    public void generate(int duration) {
        double coefficient = (2 * Math.PI * frequency) / Units.NS_PER_S;
        double[] signalData = new double[duration];
        for (int i = 0; i < duration; i++) {
            signalData[i] = Math.sin(i * coefficient) * amplitude;
        }
        transmit(new SignalPackage(signalData));
    }
}
