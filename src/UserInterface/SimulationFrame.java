package UserInterface;

import BuisnessLogic.LogListener;
import BuisnessLogic.SelectionPolicy;
import BuisnessLogic.SimulationManager;

import javax.swing.*;
import java.awt.*;


public class SimulationFrame extends JFrame implements LogListener {

    private JTextArea resultArea;
    private JTextArea bottomArea;

    private final Color titleColor = new Color(32, 105, 82);
    private final Color resultsBackgroundColor = new Color(156, 197, 161);
    private final Color backgroundColor = new Color(220, 225, 222);
    private final Color btnColor = new Color(219, 234, 221);


    public SimulationFrame(){

        setTitle("My Application");
        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(backgroundColor);

        //3 areas
        JPanel topPanel = new JPanel( new GridLayout(7, 2, 10, 15));
        topPanel.setBackground(titleColor);
        topPanel.setPreferredSize(new Dimension(800, 250));
        add(topPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setBackground(resultsBackgroundColor);
        resultArea.setEditable(false);
        JScrollPane middleScroll = new JScrollPane(resultArea);
        add(middleScroll, BorderLayout.CENTER);

        bottomArea = new JTextArea(3, 50);
        bottomArea.setEditable(false);
        bottomArea.setBackground(btnColor);
        add(bottomArea, BorderLayout.SOUTH);


        //top area
        java.text.NumberFormat format = java.text.NumberFormat.getIntegerInstance();
        format.setGroupingUsed(false);

        JFormattedTextField timeLimitField = new JFormattedTextField(format);
        timeLimitField.setColumns(10);
        JLabel l1 = new JLabel("Time limit: ");
        l1.setForeground(Color.WHITE);
        topPanel.add(l1);
        topPanel.add(timeLimitField);

        JFormattedTextField maxProcessingTimeField = new JFormattedTextField(format);
        timeLimitField.setColumns(10);
        JLabel l2 = new JLabel("Maximum processing time: ");
        l2.setForeground(Color.WHITE);
        topPanel.add(l2);
        topPanel.add(maxProcessingTimeField);

        JFormattedTextField minProcessingTimeField = new JFormattedTextField(format);
        timeLimitField.setColumns(10);
        JLabel l3 = new JLabel("Minimum processing time: ");
        l3.setForeground(Color.WHITE);
        topPanel.add(l3);
        topPanel.add(minProcessingTimeField);

        JFormattedTextField nrServersField = new JFormattedTextField(format);
        timeLimitField.setColumns(10);
        JLabel l4 = new JLabel("Number of queues: ");
        l4.setForeground(Color.WHITE);
        topPanel.add(l4);
        topPanel.add(nrServersField);

        JFormattedTextField nrClientsField = new JFormattedTextField(format);
        timeLimitField.setColumns(10);
        JLabel l5 = new JLabel("Number of clients: ");
        l5.setForeground(Color.WHITE);
        topPanel.add(l5);
        topPanel.add(nrClientsField);

        String[] options = {"Shortest time", "Shortest queue"};
        JComboBox<String> selectionPolicyOptions = new JComboBox<>(options);
        selectionPolicyOptions.setBackground(btnColor);
        JLabel l6 = new JLabel("Selection policy:");
        l6.setForeground(Color.WHITE);
        topPanel.add(l6);
        topPanel.add(selectionPolicyOptions);

        JButton runSimulation = new JButton("Run simulation");
        runSimulation.setBackground(btnColor);
        topPanel.add(runSimulation);

        //run simulation logic

        runSimulation.addActionListener(e ->{

            resultArea.setText("");
            bottomArea.setText("");

            Object value;
            int timeLimit;
            int maxProcessingTime;
            int minProcessingTime;
            int nrServers;
            int nrClients;
            String selectedStrategy = (String)selectionPolicyOptions.getSelectedItem();

            value = timeLimitField.getValue();
            if(value instanceof Number){
                timeLimit = ((Number) value).intValue();
            }
            else {
                resultArea.setText("ERROR: Time limit field is empty!");
                return;
            }

            value = maxProcessingTimeField.getValue();
            if(value instanceof Number){
                maxProcessingTime = ((Number) value).intValue();
            }
            else {
                resultArea.setText("ERROR: Maximum processing time field is empty!");
                return;
            }

            value = minProcessingTimeField.getValue();
            if(value instanceof Number){
                minProcessingTime = ((Number) value).intValue();
            }
            else {
                resultArea.setText("ERROR: Minimum processing time field is empty!");
                return;
            }

            value = nrServersField.getValue();
            if(value instanceof Number){
                nrServers = ((Number) value).intValue();
            }
            else {
                resultArea.setText("ERROR: Number of servers field is empty!");
                return;
            }

            value = nrClientsField.getValue();
            if(value instanceof Number){
                nrClients = ((Number) value).intValue();
            }
            else {
                resultArea.setText("ERROR: Number of clients field is empty!");
                return;
            }

            runSim(timeLimit, maxProcessingTime, minProcessingTime, nrServers, nrClients, selectedStrategy);

        });

    }

    public void updateLog(String message){
        resultArea.append(message + "\n");
    }

    public void updateStats(String stats){

        bottomArea.setText(stats);
    }

    private void runSim(int timeLimit, int maxProcessingTime, int minProcessingTime, int nrServers, int nrClients, String selectedStrategy){

        SelectionPolicy policy;
        if(selectedStrategy.equals("Shortest time")){
            policy = SelectionPolicy.SHORTEST_TIME;
        }
        else{
            policy = SelectionPolicy.SHORTEST_QUEUE;
        }

        SimulationManager simulationManager = new SimulationManager(timeLimit, maxProcessingTime, minProcessingTime, nrServers, nrClients, policy);

        simulationManager.setListener(this);
        Thread simThread = new Thread(simulationManager);
        simThread.start();

    }

    public static void main(String[] args){
        SimulationFrame frame = new SimulationFrame();
        frame.setVisible(true);
    }


}
