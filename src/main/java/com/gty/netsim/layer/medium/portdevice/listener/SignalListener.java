package com.gty.netsim.layer.medium.portdevice.listener;

import com.gty.netsim.layer.medium.Medium;
import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;

import java.util.ArrayDeque;

public class SignalListener extends SignalDevice {

    public SignalListener(Medium medium) {
        super(medium);
    }

    @Override
    public void send(SignalPackage signalPackage) {
        return;
    }

    @Override
    public void recieve(SignalPackage signalPackage) {
        ArrayDeque<Double> receivedSignalData = signalPackage.getSignalData();
        for (int i = 0; i < receivedSignalData.size(); i++) {
            double top = receivedSignalData.removeFirst();
            System.out.print(top + " ");
            if (i % 10 == 9){
                System.out.println();
            }
            receivedSignalData.addLast(top);
        }

    }


}
