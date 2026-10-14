package BuisnessLogic;

import DataModel.Server;
import DataModel.Task;

import java.util.List;

public interface Strategy {
    public void addTask(List<Server> servers, Task task);

}
