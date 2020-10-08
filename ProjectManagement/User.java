package ProjectManagement;

import RedBlack.RBTree;

public class User implements Comparable<User>,UserReport_  {
    public String name;
    public int consumed = 0;
    public int latest_consumption =0;

    User(String name){
        this.name=name;
    }
    @Override

    public int compareTo(User user) {
        return this.name.compareTo(user.name);
    }

    public String user(){
        return this.name;
    }

    public int consumed(){
        return this.consumed;
    }

    public static int comparator (User user2,User user1) {
        if (user1.consumed > user2.consumed)
            return 1;

        else if (user1.consumed == user2.consumed) {
            if (user1.latest_consumption < user2.latest_consumption)
                return 1;
            else if (user1.latest_consumption > user2.latest_consumption)
                return -1;
        }
        return -1;
    }
    public String toString(){
        return ("user name = "+this.name + " "+"user consumed = "+this.consumed);
    }


}
