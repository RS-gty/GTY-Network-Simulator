package com.gty.netsim.app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SignalPlotPanel extends JPanel {

    private final double[] samples;
    private final String title;
    private final Color lineColor;

    public SignalPlotPanel(double[] samples, String title, Color lineColor) {
        this.samples = samples;
        this.title = title;
        this.lineColor = lineColor;
        setPreferredSize(new Dimension(900, 400));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int padL = 60, padR = 20, padT = 40, padB = 40;
        int plotW = w - padL - padR;
        int plotH = h - padT - padB;

        double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
        for (double v : samples) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }
        if (max - min < 1e-12) { max = min + 1; }

        g2.setColor(new Color(230, 230, 230));
        for (int i = 0; i <= 4; i++) {
            int y = padT + plotH * i / 4;
            g2.drawLine(padL, y, padL + plotW, y);
        }
        g2.setColor(Color.DARK_GRAY);
        g2.drawRect(padL, padT, plotW, plotH);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        for (int i = 0; i <= 4; i++) {
            double val = max - (max - min) * i / 4;
            int y = padT + plotH * i / 4;
            String label = String.format("%.3f", val);
            g2.drawString(label, padL - 55, y + 4);
        }

        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(1.5f));
        int n = samples.length;
        int prevX = -1, prevY = -1;
        for (int i = 0; i < n; i++) {
            int x = padL + (int) ((double) i / (n - 1) * plotW);
            double norm = (samples[i] - min) / (max - min);
            int y = padT + (int) ((1.0 - norm) * plotH);
            if (prevX >= 0) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (w - fm.stringWidth(title)) / 2, 25);
    }

    public void savePng(String path) throws Exception {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        paintComponent(g2);
        g2.dispose();
        ImageIO.write(img, "png", new File(path));
    }
}