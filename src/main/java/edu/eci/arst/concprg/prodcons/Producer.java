/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Producer extends Thread {
    Logger logger = Logger.getLogger(getClass().getName());
    private Queue<Integer> queue = null;

    private int dataSeed = 0;
    private Random rand=null;

    public Producer(Queue<Integer> queue) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            dataSeed = dataSeed + rand.nextInt(100);
            logger.log(Level.INFO, "Producer added {0}", dataSeed);
            queue.offer(dataSeed);
        }, 0, 500, TimeUnit.MILLISECONDS);
    }
}
