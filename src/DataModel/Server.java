package DataModel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile boolean running = true;

    public Server(){
        this.waitingPeriod = new AtomicInteger(0);
        this.tasks = new LinkedBlockingDeque<>();
    }

    public void addTask(Task newTask){
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime());

        synchronized (this) {
            this.notify();
        }
    }




    @Override
    public void run() {

        try {
            while (running) {
                Task t = tasks.peek();
                if(t != null) {

                    Thread.sleep(1000);
                    t.setServiceTime(t.getServiceTime() - 1);
                    waitingPeriod.getAndDecrement();

                    if(t.getServiceTime() <= 0){
                        tasks.take();
                    }
                }
                else{
                    synchronized (this) {
                        if (tasks.isEmpty()) {
                            this.wait();
                        }
                    }                }
            }
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }


    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public void stop(){
        this.running = false;
        synchronized (this) {
            this.notify();
        }
    }
}
