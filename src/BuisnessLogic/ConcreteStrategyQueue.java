package BuisnessLogic;

import DataModel.Server;
import DataModel.Task;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task task){
        int min = servers.get(0).getTasks().size();
        Server minServer = servers.get(0);
        for(int i = 1; i < servers.size(); i++)
        {
            if(servers.get(i).getTasks().size() < min){
                minServer = servers.get(i);
                min = servers.get(i).getTasks().size();
            }
        }

        minServer.addTask(task);

    }
}
