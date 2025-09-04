/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class Consumer extends Thread{
    Logger logger = Logger.getLogger(getClass().getName());

    
    private final LinkedBlockingQueue<Integer> queue;
    
    
    public Consumer(LinkedBlockingQueue<Integer> queue){
        this.queue = queue;
    }
    
    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            int elem = 0;
            try {
                elem = queue.take();
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Interrupted!", e);
                Thread.currentThread().interrupt();
            }
            logger.log(Level.INFO, "Consumer consumes {0}", elem);
        }, 0, 2, TimeUnit.SECONDS);
    }
}
