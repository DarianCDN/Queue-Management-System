package BuisnessLogic;

import DataModel.Server;
import DataModel.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{


    @Override
    public void addTask(List<Server> servers, Task task){

        Server minServer = servers.getFirst();

        int min = minServer.getWaitingPeriod().get();

        for(int i = 1; i < servers.size(); i++)
        {
            int serverTime = servers.get(i).getWaitingPeriod().get();
            if(serverTime < min){
                minServer = servers.get(i);
                min = serverTime;
            }
        }

        minServer.addTask(task);

    }
}
