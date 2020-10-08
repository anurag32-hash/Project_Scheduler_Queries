package ProjectManagement;


import RedBlack.RBTree;

public class Project implements Comparable<Project> {
   public String name;
    public int priority,budget;

    Project(String name,int priority,int budget){
        this.name=name;
        this.priority=priority;
        this.budget=budget;
    }
    public int compareTo (Project p){
        return this.name.compareTo(p.name);
    }

}
