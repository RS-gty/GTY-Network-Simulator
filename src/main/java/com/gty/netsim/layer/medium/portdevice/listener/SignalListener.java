package com.gty.netsim.layer.medium.portdevice.listener;

import com.gty.netsim.layer.medium.portdevice.SignalDevice;
import com.gty.netsim.layer.medium.signal.SignalPackage;
import com.gty.netsim.layer.medium.spi.SignalPort;

public class SignalListener extends SignalDevice {
    private long lastSignalNs = -1;
    private SignalPackage recentSignalPackage = null;

    public SignalListener(SignalPort port) {
        super(port);
    }

    @Override
    public void send(SignalPackage signal) {
    }

    // [改] 原 recieve(SignalPackage)；遍历不再旋转共享队列
    @Override
    public void onSignal(SignalPackage signal, long delayNs) {
        lastSignalNs = delayNs;
        double[] samples = signal.samples();
        for (int i = 0; i < samples.length; i++) {
            System.out.print(samples[i] + " ");
            if (i % 10 == 9) {
                System.out.println();
            }
        }
        System.out.println();
        this.recentSignalPackage = signal;
    }

    public long lastSignalNs() {
        return lastSignalNs;
    }

    public SignalPackage recentSignalPackage() {
        return recentSignalPackage;
    }
}
