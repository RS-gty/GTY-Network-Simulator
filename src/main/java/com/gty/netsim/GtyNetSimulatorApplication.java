package com.gty.netsim;

import com.gty.netsim.app.ui.SignalPlotPanel;
import com.gty.netsim.common.Physics;
import com.gty.netsim.core.Scheduler;
import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.TwistedPairMedium;
import com.gty.netsim.layer.medium.portdevice.listener.SignalListener;
import com.gty.netsim.layer.medium.portdevice.signalgenerator.SignalGenerator;
import com.gty.netsim.layer.medium.portdevice.signalgenerator.SineWaveGenerator;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.topology.MediumAssembler;

import javax.swing.*;
import java.awt.*;

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

        // [改] 双绞线中的信号速度约为 0.68c
        long signalSpeedMps = Math.round(0.68 * Physics.LIGHT_SPEED_MPS);
        Medium medium = new TwistedPairMedium(scheduler, signalSpeedMps, 100);
        // [改] 设备改由装配器创建并挂到介质上，不再 new ...(medium)
        SignalGenerator generator = MediumAssembler.attach(medium,
                port -> new SineWaveGenerator(port, 400000, 1));
        SignalListener listener = MediumAssembler.attach(medium,
                port -> new SignalListener(port));

        long start = System.currentTimeMillis();
        generator.generate(2500);
        long end = System.currentTimeMillis();
        long diffMs = end - start;

        System.out.println("Generation time: " + diffMs + "ms");

        // [改] 推进仿真：信号到达是一个延迟事件
        System.out.println(scheduler.pendingCount());
        scheduler.runAll();
        System.out.println("最后一帧信号到达时刻: " + listener.lastSignalNs() + "ns");
        System.out.println("仿真结束时刻: " + scheduler.nowNs() + "ns");

        SignalPackage sp = listener.recentSignalPackage();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("信号追踪 - 衰减后");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new SignalPlotPanel(sp.samples(), "衰减后波形", Color.BLUE));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
