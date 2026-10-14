package BuisnessLogic;

import DataModel.Server;
import DataModel.Task;
import UserInterface.SimulationFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class SimulationManager implements Runnable{
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> tasks = new ArrayList<>();

    public int timeLimit;
    public int maxProcessingTime;
    public int minProcessingTime;
    public int numberOfServers;
    public int numberOfClients;
    public SelectionPolicy selectionPolicy;

    public int totalWaitingTime = 0;
    public int totalServiceTime = 0;
    public int peakHour = -1;
    public int maxNrClients = -1;

    private LogListener listener;

    public void setListener(LogListener listener){
        this.listener = listener;
    }

    public void generateRandomTasks(){
        for(int i = 1; i <= numberOfClients; i++)
        {
            int serviceTime = (int)(Math.random() * (maxProcessingTime - minProcessingTime + 1)) + minProcessingTime;
            int arrivalTime = (int)(Math.random() * (timeLimit - serviceTime + 1));
            Task t = new Task(i, arrivalTime, serviceTime);
            tasks.add(t);
        }

        Collections.sort(tasks);
    }

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int numberOfServers, int numberOfClients, SelectionPolicy selectionPolicy){

        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.selectionPolicy = selectionPolicy;

        this.scheduler = new Scheduler(numberOfServers, numberOfClients);
        generateRandomTasks();
        this.scheduler.changeStrategy(selectionPolicy);

    }

    @Override
    public void run(){
        int currentTime = 0;

        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("output.txt"))) {
            while (currentTime <= timeLimit) {
                int nrClients = 0;

                Iterator<Task> iterator = tasks.iterator();

                while(iterator.hasNext()){
                    Task t = iterator.next();
                    if (t.getArrivalTime() == currentTime) {
                        scheduler.dispatchTask(t);
                        iterator.remove();
                        totalServiceTime += t.getServiceTime();
                    }
                }

                for(Server s: scheduler.getServers())
                {   if(s.getTasks().size() > 0)
                    {
                        totalWaitingTime += s.getTasks().size() - 1;
                        nrClients += s.getTasks().size();
                    }

                }

                if(nrClients > maxNrClients)
                {
                    peakHour = currentTime;
                    maxNrClients = nrClients;
                }

                String logText =  logFile(currentTime, tasks);
                writer.println(logText);

                if(listener != null){
                    listener.updateLog(logText);
                }

                currentTime++;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            double avgWaitingTime = (double) totalWaitingTime / numberOfClients;
            double avgServiceTime = (double) totalServiceTime / numberOfClients;

            StringBuilder stats = new StringBuilder();
            stats.append("Average waiting time: " + String.format("%.2f", avgWaitingTime) + "\nAverage service time: " + String.format("%.2f", avgServiceTime) + "\nPeak hour: " + peakHour + "\n");

            writer.println(stats);

            if(listener != null){
                listener.updateStats(stats.toString());
            }

            scheduler.stopAll();

        }catch(java.io.IOException e){
            e.printStackTrace();
        }



    }

    public String logFile(int currentTime,  List<Task> waitingClients){

        StringBuilder logText = new StringBuilder();

        logText.append("Time " + currentTime + "\n");
        logText.append("Waiting clients: ");

        for(Task t: waitingClients)
        {
            logText.append("(" + t.getId() + " ," + t.getArrivalTime() + ", " + t.getServiceTime() + "), ");

        }
        logText.append("\n");

        List<Server> servers = scheduler.getServers();

        for(int i = 0; i < servers.size(); i++)
        {
            logText.append("Queue " + (i + 1) + ": ");
            if(servers.get(i).getTasks().isEmpty())
            {
                logText.append("closed\n");
            }
            else{
                for(Task t : servers.get(i).getTasks()){
                    logText.append("(" + t.getId() + " ," + t.getArrivalTime() + ", " + t.getServiceTime() + "), ");
                }
                logText.append("\n");

            }

        }

        return logText.toString();

    }

    public static void main(String[] args){
        SimulationManager sim = new SimulationManager(200, 3, 9, 20, 1000, SelectionPolicy.SHORTEST_TIME);
        Thread t = new Thread(sim);
        t.start();
    }
}
