package projectAI;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class main_project implements Initializable {
    @FXML   AnchorPane p_map;
    @FXML   ComboBox source;
    @FXML   ComboBox destination;
    @FXML   ComboBox algorithm;

    @FXML   Button button1;
    @FXML   TextArea path;
    @FXML   TextArea time_complexity;
    @FXML   TextArea space_complexity;
    cities[] selectc = new cities[2];
    LinkedList<path>[] d;
    cities[] Cities_p = new cities[29];

    ArrayList<cities> visited = new ArrayList<>();
    PriorityQueue<cities> queueq;
    ArrayList<String> Algorithms;
    int Use = 0;

    @Override
    public void initialize(URL r, ResourceBundle resourceBundle)
    {
        try
        {

//            UseAreal.setSelected(true);
            this.readFile();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void readFile() throws IOException
    {
        //initialize adjacency list
        d = new LinkedList[29];
        queueq = new PriorityQueue<>();
        for (int i = 0; i < d.length; i++)
        {
            d[i] = new LinkedList<>();
        }
        //Read city file,parse it and fill the cities array
        File cityFile = new File("cities.txt");
        BufferedReader br = new BufferedReader(new FileReader(cityFile));
        String line;
        int counter = 0;

        while ((line = br.readLine()) != null)
        {
            String[] info = line.split("#");
            String cityName = info[0];

            double cityYAxis = Double.parseDouble(info[2]);
            double cityXAxis = Double.parseDouble(info[1]);
            String arabicname = info[3];
            cities currentCities = new cities(counter, cityName, cityXAxis, cityYAxis,arabicname);
            Cities_p[counter] = currentCities;
            queueq.add(currentCities);
            counter++;
        }

        File adjFile = new File("pathcities.txt");
        BufferedReader adjBr = new BufferedReader(new FileReader(adjFile));
        String adjLine;
        while ((adjLine = adjBr.readLine()) != null)
        {
            String[] info = adjLine.split("#");
            System.out.println(info[0]);
            cities init = tofind_city(info[0]);
            cities adj = tofind_city(info[1]);
            double distance = Double.parseDouble(info[2]);
            path r = new path(adj, distance);
            d[init.getId()].add(r);
        }
        this.point_map();
    }
    public boolean DLS(cities source, cities target, int limit)
    {
        visited.add(source);
        if(source.equals(target))
            return true;
        if(limit <= 0)
            return false;
        for(int i = 0; i < d[source.getId()].size(); i++)
        {
            cities newCities = d[source.getId()].get(i).c;
            if(!visited.contains(newCities))
            {
                newCities.predecessor = source;
                if (DLS(d[source.getId()].get(i).c, target, limit - 1) == true)
                    return true;
            }
        }
        return false;
    }
    public void IDS(cities s, cities g, int d)
    {
        for(int i = 0; i <= d; i++)
        {
            visited.clear();
            if(DLS(s, g, i) == true) {
                this.p_method2(g, s, i);
                return;
            }
        }
        System.out.println("Not Found");
        path.setText("Not Found!");
    }
    public void BFS_Search(cities s, cities g) throws Exception {
        queueq = new PriorityQueue<cities>();
        visited.clear();
        queueq.add(s);
        visited.add(s);
        if(s.equals(g))
        {
            this.path.setText("Source and goal are equal");
            return;
        }
        this.space_complexity.setText("");
        this.time_complexity.setText("");
        while(!queueq.isEmpty())
        {
            cities newCities = queueq.remove();
            if(newCities.equals(g))
            {
                this.p_mehod(g, s);
                return;
            }
            for(int i = 0; i < d[newCities.getId()].size(); i++) {
                cities adj = d[newCities.getId()].get(i).c;
                if (!visited.contains(adj))
                {
                    queueq.add(adj);
                    visited.add(adj);
                    adj.predecessor = newCities;
                }
            }
        }
        throw new Exception("UnExpected error occurred!");
    }
    public void A_Start(cities s, cities g) {
        queueq = new PriorityQueue<cities>();
        visited.clear();
        s.setFinal_c(0.0 + this.Walk(s, g));
        s.setR_COST(0.0);
        queueq.add(s);
        int time=0;
        int fringeSize= queueq.size();
        while (!queueq.isEmpty()) {
            cities newMin = queueq.remove();
            visited.add(newMin);
            time++;
            if (newMin.getName_A().equals(g.getName_A())) {
                this.printAStartSearchPath(newMin, s,time,fringeSize);
                return;
            }
            for (int i = 0; i < d[newMin.getId()].size(); i++) {
                cities adj = d[newMin.getId()].get(i).c;
                adj.setR_COST(0.0);
                double gN = d[newMin.getId()].get(i).distance + newMin.getR_COST();
                adj.setR_COST(gN);
                double h1= this.Heuristic(adj, g);
                double h2 = this.Walk(adj, g);
                double hN = h1 + h2;
                double fN = gN + hN;
                adj.setFinal_c(fN);
                adj.setHofn(hN);
                time++;
                if (!visited.contains(adj)) {
                    queueq.add(adj);
                    adj.predecessor = newMin;
                    fringeSize=Integer.max(fringeSize, queueq.size());
                }
            }
        }
    }

    public double Walk(cities city1, cities city2) {
        double x1 = city1.getX_POINT();
        double y1 = city1.getY_POINT();
        double x2 = city2.getX_POINT();
        double y2 = city2.getY_POINT();
        double s = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        double goal = city1.getR_COST()+ city2.getR_COST();
        double h = (goal + s)/2;
        return h *0.50;
    }
    public double Heuristic(cities city1, cities city2) {
        double x1 = city1.getX_POINT();
        double y1 = city1.getY_POINT();
        double x2 = city2.getX_POINT();
        double y2 = city2.getY_POINT();
        double h = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        return h;
    }

    public void p_method2(cities g, cities s, int d)
    {
        LinkedList<cities> path = new LinkedList<>();
        path.add(g);
        double distance = 0.0;
        while(!g.getName_A().equals(s.getName_A()))
        {
            path.add(g.predecessor);
            LinkedList<path> r = this.d[g.getId()];
            for(int i = 0; i < r.size(); i++)
            {
                if(r.get(i).c.getName_A().equals(g.predecessor.getName_A()))
                {
                    distance+=r.get(i).distance;
                    break;
                }
            }
            g = g.predecessor;
        }
        int time = path.size();
        for (int i = 0; i < path.size() - 1; i++) {
            Line p = new Line();
            p.setStartX(path.get(i).getX_POINT());
            p.setStartY(path.get(i).getY_POINT());
            p.setEndX(path.get(i + 1).getX_POINT());
            p.setEndY(path.get(i + 1).getY_POINT());
            p.setStrokeWidth(3);
            p.setStroke(Color.BLACK);
            p.setOpacity(0.8);
            p.setId("path");
            Circle c = new Circle(path.get(i).getX_POINT(), path.get(i).getY_POINT(), 4);
            c.setFill(Color.RED);
            c.setId("pathCircle");
            p_map.getChildren().addAll(p, c);
        }
        this.path.appendText("Total distance: " +distance + " KM\n");
        this.time_complexity.setText(String.valueOf(time));
        this.space_complexity.setText("Path found after depth: "+String.valueOf(d));
        for (int i = path.size() - 1; i >= 0; i--) {
            this.path.appendText(path.get(i).getName() + " " +
                    "-" + path.get(i).getName_A() + " _ " + "\n");
        }
    }

    public void printAStartSearchPath(cities goal, cities source, int timeValue, int spaceValue)
    {
        double cost = Math.round(goal.getR_COST() * 100.0) / 100.0;
        path.appendText("total distance ==> " + cost + " KM\n");
        this.time_complexity.setText(String.valueOf(timeValue));
        this.space_complexity.setText(String.valueOf(spaceValue));
        LinkedList<cities> path = new LinkedList<cities>();
        path.add(goal);
        while (!goal.getName_A().equals(source.getName_A())) {
            path.add(goal.predecessor);
            goal = goal.predecessor;
        }
        for (int i = 0; i < path.size() - 1; i++) {

            Line partialPath = new Line();
            partialPath.setStartX(path.get(i).getX_POINT());
            partialPath.setStartY(path.get(i).getY_POINT());
            partialPath.setEndX(path.get(i + 1).getX_POINT());
            partialPath.setEndY(path.get(i + 1).getY_POINT());
            partialPath.setStrokeWidth(3);
            partialPath.setStroke(Color.rgb(0,0,0));
            partialPath.setOpacity(0.8);
            partialPath.setId("path");
            Circle c = new Circle(path.get(i).getX_POINT(), path.get(i).getY_POINT(), 4);
            c.setFill(Color.AQUA);
            c.setId("pathCircle");
            p_map.getChildren().addAll(partialPath, c);
        }
        for (int i = path.size() - 1; i >= 0; i--) {
            this.path.appendText(path.get(i).getName() + " - " + path.get(i).getName_A() + " - " + "\n");
        }

    }

//delet all old value to new used
    public void clear()
    {//clear all information
        path.setText("");
        source.setValue(null);
        destination.setValue(null);
        algorithm.setValue(null);
        time_complexity.clear();
        space_complexity.clear();
        Use = 0;
        selectc[0] = null;
        selectc[1] = null;
        Set<Node> prevPath = p_map.lookupAll("#path");
        for (Node p : prevPath) {
            Line path = (Line) p;
            p_map.getChildren().remove(path);
        }
        Set<Node> prevPathCircle = p_map.lookupAll("#pathCircle");
        for (Node p : prevPathCircle) {
            Circle path = (Circle) p;
            p_map.getChildren().remove(path);
        }
        clearCircles();
    }
    public void exit()
    {
        System.exit(0);
    }
    public void clearCircles()
    {
        for (int i = 0; i < p_map.getChildren().size(); i++)
        {
            if(p_map.getChildren().get(i) instanceof Circle)
            {
                Circle dot = (Circle) p_map.getChildren().get(i);
                dot.setFill(Color.BLACK);
                dot.setRadius(5);
            }
        }
    }
    //put the point in map
    public void point_map()
    {
        for(int i = 0; i< Cities_p.length; i++)
        {
            //use x y axis to set point
            Circle point = new Circle(Cities_p[i].getX_POINT(), (Cities_p[i].getY_POINT()), 4);
            //set color to black
            point.setFill(Color.BLACK);
            //clean space
            point.setId(Cities_p[i].getName().replaceAll(" ", ""));
            //add point in map
            p_map.getChildren().add(point);
//add name of destination in map
            destination.getItems().addAll(Cities_p[i].getName_A());
            //add name of source in map
            source.getItems().addAll(Cities_p[i].getName_A());
        }
        //add action
        source.setOnAction(e -> {
            cities s1 = tofind_city(String.valueOf(source.getValue()));
            this.f_Select(s1, e);
        });

        destination.setOnAction(e -> {
            cities s2 = tofind_city(String.valueOf(destination.getValue()));
            this.f_Select(s2, e);
        });
         //ArrayList
        Algorithms = new ArrayList<>();
        Algorithms.add("A*");
        Algorithms.add("IDS");
        Algorithms.add("BFS");
        //add to button
        algorithm.getItems().addAll(Algorithms);
//do action //show methode
        algorithm.setOnAction(e -> {
            this.c_algorithm();
        });
    }
    //find city
    public cities tofind_city(String cityName) {
        //looop
        for(cities palestincity : Cities_p){
            //compare
            if (palestincity.getName_A().equals(cityName.trim())) {
                return palestincity;
            }
        }
        //else
        return null;
    }
    //find city and put it in red color
    public void f_Select(cities cities, Event event)
    {
        if (((ComboBox) event.getSource()).getId().equals("source"))
        {
            Set<Node> c_select = p_map.lookupAll("#selectedSource");
            //loop
            for (Node c : c_select) {
                //object circle
                Circle circle = (Circle) c;
                //put in black
                circle.setFill(Color.BLACK);
                //put raduise
                circle.setRadius(4);
                //clean space
                circle.setId(selectc[0].getName().replaceAll(" ", ""));
            }
            if (cities == null)
                return;
            //node
            Set<Node> s = p_map.lookupAll("#" + cities.getName().replaceAll(" ", ""));
            //loop
            for (Node c : s) {
                //object circle
                Circle ci = (Circle) c;
                //put in red
                ci.setFill(Color.RED);
                //put raduise
                ci.setRadius(4);
                ci.setId("selectedSource");
            }
            //put first city in selectc(sorce)
            selectc[0] = cities;
        } else if (((ComboBox) event.getSource()).getId().equals("destination"))
        {
            Set<Node> selec_C = p_map.lookupAll("#selectedDestination");

            //loop
            for (Node c : selec_C)
            {
                //object circle
                Circle circle = (Circle) c;
                //put it black
                circle.setFill(Color.BLACK);
                //put raduise
                circle.setRadius(5);
                //clean space
                circle.setId(selectc[1].getName().replaceAll(" ", ""));
            }
            if (cities == null)
                return;

            Set<Node> selected = p_map.lookupAll("#" + cities.getName().replaceAll(" ", ""));
            //loop
            for (Node c : selected)
            {
                //object circle
                Circle ci = (Circle) c;
                ////put it red
                ci.setFill(Color.RED);
                //put raduise
                ci.setRadius(5);
                ci.setId("selectedDestination");
            }
            //put goaal in array
            selectc[1] = cities;
        }
    }
    //function to choose method
    public void c_algorithm()
    {
        //if user not choose
        if(algorithm.getValue() == null)
        {
            //then the value for use 0
            Use = 0;
            return;
        }
        //if user choose a*
        if(algorithm.getValue().equals("A*"))
        {
            //then the value for use 1
            Use = 1;
            return;
        }
        //if user choose ids
        if(algorithm.getValue().equals("IDS"))
        {
            //then the value for use 2
            Use = 2;
            return;
        }
        //if user choose bfs
        if(algorithm.getValue().equals("BFS"))
        {
            //then the value for use 3
            Use = 3;
            return;
        }

    }
    public void choose()
    {
        if(Use == 0)
        {
            this.path.setText("choose algorithm");
            return;
        }
        if(selectc[0]==null|| selectc[1]==null)
        {
            this.path.setText("choose cities");
            return;
        }

        if(selectc[0].equals(selectc[1]))
        {
            this.path.setText("error!Goal and source must be different!");
            return;
        }
        this.path.setText("");
        for (int i = 0; i < Cities_p.length; i++)
        {
            Cities_p[i].setCost(Double.POSITIVE_INFINITY);
        }
        Set<Node> prevPath = p_map.lookupAll("#path");
        for (Node p : prevPath)
        {
            Line path = (Line) p;
            p_map.getChildren().remove(path);
        }
        Set<Node> prevPathCircle = p_map.lookupAll("#pathCircle");
        for (Node p : prevPathCircle)
        {
            Circle path = (Circle) p;
            p_map.getChildren().remove(path);
        }
        if(Use == 1)
            this.A_Start(selectc[0], selectc[1]);
        if(Use == 2)
            this.IDS(selectc[0], selectc[1], 10);
        if(Use == 3) {
            try {
                this.BFS_Search(selectc[0], selectc[1]);
            } catch (Exception e) {}
        }
    }
    public void p_mehod(cities g, cities s)
    {
        LinkedList<cities> path = new LinkedList<>();
        path.add(g);
        double distance = 0.0;
        while(!g.getName_A().equals(s.getName_A()))
        {
            path.add(g.predecessor);
            LinkedList<path> r = d[g.getId()];
            for(int i = 0; i < r.size(); i++)
            {
                if(r.get(i).c.getName_A().equals(g.predecessor.getName_A()))
                {
                    distance+=r.get(i).distance;
                    break;
                }
            }
            g = g.predecessor;
        }
        int time = path.size();
        for (int i = 0; i < path.size() - 1; i++) {
            Line p = new Line();
            p.setStartX(path.get(i).getX_POINT());
            p.setStartY(path.get(i).getY_POINT());
            p.setEndX(path.get(i + 1).getX_POINT());
            p.setEndY(path.get(i + 1).getY_POINT());
            p.setStrokeWidth(3);
            p.setStroke(Color.BLACK);
            p.setOpacity(0.8);
            p.setId("path");
            Circle c = new Circle(path.get(i).getX_POINT(), path.get(i).getY_POINT(), 4);
            c.setFill(Color.RED);
            c.setId("pathCircle");
            p_map.getChildren().addAll(p, c);
        }
        this.path.appendText("Total distance: " +distance + " KM\n");
        this.time_complexity.setText(String.valueOf(time));
        for (int i = path.size() - 1; i >= 0; i--) {
            this.path.appendText(path.get(i).getName() + " - " + path.get(i).getName_A() + " - " + "\n");
        }
    }

}