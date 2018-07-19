/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.EventQueue;
import java.text.DecimalFormat;

/**
 *
 * @author wyq
 */
public class Test {
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                double dmin = 1.0;
                double dmax = 2389.874;
                DecimalFormat df = new DecimalFormat("#.0");
                df.setMaximumFractionDigits(6);
                String smin = df.format(dmin);
                String smax = df.format(dmax);
                System.out.println(smin);
                System.out.println(smax);
            }
        });
    }
}
