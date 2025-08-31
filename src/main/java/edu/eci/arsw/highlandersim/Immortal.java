package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Immortal extends Thread {

    private final ImmortalUpdateReportCallback updateCallback;
    private final AtomicInteger health;
    private final int defaultDamageValue;
    private final List<Immortal> immortalsPopulation;
    private final String name;
    private final Random r = new Random(System.currentTimeMillis());
    private volatile boolean paused = false;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    @Override
    public void run() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            isPaused();

            List<Immortal> aliveOpponents = immortalsPopulation.stream()
                    .filter(im -> im != this && im.getHealth() > 0)
                    .toList();

            if (aliveOpponents.isEmpty()) {
                updateCallback.processReport("The winner is " + this);
                this.interrupt();
            }

            Immortal opponent = aliveOpponents.get(r.nextInt(aliveOpponents.size()));
            this.fight(opponent);
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    public void fight(Immortal i2) {
        if (i2.getHealth() > 0) {
            i2.health.addAndGet(-defaultDamageValue);
            health.addAndGet(defaultDamageValue);
            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
            i2.interrupt();
        }
    }

    public void pause() {
        paused = true;
    }

    public synchronized void unpause() {
        paused = false;
        notifyAll();
    }

    private synchronized void isPaused() {
        while (paused) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public int getHealth() {return health.get(); }

    @Override
    public String toString() {
        return name + "[" + health + "]";
    }
}
