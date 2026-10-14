package BuisnessLogic;

import DataModel.Server;
import DataModel.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers = new ArrayList<>();
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer){
        for(int i = 0; i< maxNoServers; i++)
        {
            Server s = new Server();
            servers.add(s);
            Thread thread = new Thread(s);
            thread.start();
        }
    }

    public void changeStrategy(SelectionPolicy policy){
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME){
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task t){

        strategy.addTask(servers, t);
    }

    public List<Server> getServers(){
        return servers;
    }


    public void stopAll() {
        for (Server s : servers) {
            s.stop();
        }
    }

}
