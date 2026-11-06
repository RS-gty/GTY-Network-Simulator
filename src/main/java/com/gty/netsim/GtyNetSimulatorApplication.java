package com.gty.netsim;

import com.gty.netsim.common.Physics;
import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.TwistedPairMedium;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.portdevice.listener.SignalListener;
import com.gty.netsim.layer.medium.portdevice.signalgenerator.SignalGenerator;
import com.gty.netsim.layer.medium.portdevice.signalgenerator.SineWaveGenerator;

public class GtyNetSimulatorApplication {
    public static void main(String[] args){
        System.out.println("Gty Net Simulator Application");
        Scheduler scheduler = new Scheduler();
        /*
        scheduler.scheduleAt(10, () -> {System.out.println(1);});
        scheduler.scheduleAt(20, () -> {System.out.println(2);});
        scheduler.scheduleAt(30, () -> {System.out.println(3);});
        scheduler.scheduleAt(40, () -> {System.out.println(4);});
        scheduler.scheduleAt(50, () -> {System.out.println(5);});
        scheduler.scheduleAt(60, () -> {System.out.println(6);});
        scheduler.scheduleAt(70, () -> {System.out.println(7);});
        scheduler.scheduleAt(80, () -> {System.out.println(8);});
        scheduler.scheduleAt(90, () -> {System.out.println(9);});
        scheduler.scheduleAt(100, () -> {System.out.println(10);});

        scheduler.runUntil(35);
        */

        Medium medium = new TwistedPairMedium(scheduler, Physics.LIGHT_SPEED_MPS, 100);
        SignalGenerator generator = new SineWaveGenerator(medium, 500000, 1); // 400kHz, 1V
        SignalDevice listener = new SignalListener(medium);
        long start = System.currentTimeMillis();
        generator.generate(2010);
        long end = System.currentTimeMillis();
        long diffMs = end - start;

        System.out.println("Generation time: " + diffMs + "ms");


    }
}
