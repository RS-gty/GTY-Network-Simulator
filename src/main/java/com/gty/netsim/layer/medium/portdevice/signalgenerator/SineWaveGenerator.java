package com.gty.netsim.layer.medium.portdevice.signalgenerator;

import com.gty.netsim.common.Units;
import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.signal.SignalPackage;

import java.util.ArrayDeque;

public class SineWaveGenerator extends SignalGenerator {
    long frequency;
    long amplitude;

    public SineWaveGenerator(Medium medium, long frequency, long amplitude) {
        super(medium);
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    @Override
    public void generate(int duration) {
        ArrayDeque<Double> signal_data =  new ArrayDeque<>();
        double coefficient = (2 * Math.PI * frequency) / Units.NS_PER_S;
        for (int i = 0; i < duration; i++) {
            double data = Math.sin(i * coefficient) * amplitude;
            signal_data.addLast(data);
        }
        this.medium_port.sendSignal(new SignalPackage(signal_data));
    }




}
