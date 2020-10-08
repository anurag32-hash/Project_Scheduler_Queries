package ProjectManagement;


import PriorityQueue.MaxHeap;
import RadixTrie.RadixNode;
import RadixTrie.RadixTree;
import RedBlack.RBTree;
import RedBlack.RedBlackNode;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;


public class Scheduler_Driver extends Thread implements SchedulerInterface {
    RBTree<Project,Job> rbtree = new RBTree<>();
    MaxHeap<Job> jobheap = new MaxHeap<>();
    MinHeap minHeap = new MinHeap();
    MaxHeap<Job> priormaxheap = new MaxHeap<>();
    RadixTree<Project> projecttrie = new RadixTree<>();
    RadixTree<User> usertrie = new RadixTree();
    RadixTree<Job> totaljobs = new RadixTree<>();
    ArrayList<Job> queuelist = new ArrayList<>();
    ArrayList<User> userlist = new ArrayList<>();
    ArrayList<Job> totaljoblist = new ArrayList<>();
    ArrayList<JobReport_> temp = new ArrayList<>();

    int time=0;
    int timestap=0;
    int completed_jobs=0;


    public static void main(String[] args) throws IOException {

        Scheduler_Driver scheduler_driver = new Scheduler_Driver();
        File file;
        if (args.length == 0) {
            URL url = Scheduler_Driver.class.getResource("INP");
            file = new File(url.getPath());
        } else {
            file = new File(args[0]);
        }

        scheduler_driver.execute(file);
    }

    public void execute(File commandFile) throws IOException {


        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(commandFile));

            String st;
            while ((st = br.readLine()) != null) {
                String[] cmd = st.split(" ");
                if (cmd.length == 0) {
                    System.err.println("Error parsing: " + st);
                    return;
                }
                String project_name, user_name;
                Integer start_time, end_time;

                long qstart_time, qend_time;

                switch (cmd[0]) {
                    case "PROJECT":
                        handle_project(cmd);
                        break;
                    case "JOB":
                        handle_job(cmd);
                        break;
                    case "USER":
                        handle_user(cmd[1]);
                        break;
                    case "QUERY":
                        handle_query(cmd[1]);
                        break;
                    case "": // HANDLE EMPTY LINE
                        handle_empty_line();
                        break;
                    case "ADD":
                        handle_add(cmd);
                        break;
                    //--------- New Queries
                    case "NEW_PROJECT":
                    case "NEW_USER":
                    case "NEW_PROJECTUSER":
                    case "NEW_PRIORITY":
                        timed_report(cmd);
                        break;
                    case "NEW_TOP":
                        qstart_time = System.nanoTime();
                        timed_top_consumer(Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                        System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    case "NEW_FLUSH":
                        qstart_time = System.nanoTime();
                        timed_flush( Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                        System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    default:
                        System.err.println("Unknown command: " + cmd[0]);
                }

            }


            run_to_completion();
            print_stats();

        } catch (FileNotFoundException e) {
            System.err.println("Input file Not found. " + commandFile.getAbsolutePath());
        } catch (NullPointerException ne) {
            ne.printStackTrace();

        }
    }

    @Override
    public ArrayList<JobReport_> timed_report(String[] cmd) {
        long qstart_time, qend_time;
        ArrayList<JobReport_> res = null;
        switch (cmd[0]) {
            case "NEW_PROJECT":
                qstart_time = System.nanoTime();
                res = handle_new_project(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
            case "NEW_USER":
                qstart_time = System.nanoTime();
                res = handle_new_user(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));

                break;
            case "NEW_PROJECTUSER":
                qstart_time = System.nanoTime();
                res = handle_new_projectuser(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
            case "NEW_PRIORITY":
                qstart_time = System.nanoTime();
                res = handle_new_priority(cmd[1]);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
        }

        return res;
    }

    @Override
    public ArrayList<UserReport_> timed_top_consumer(int top) {
        ArrayList<UserReport_> arr = new ArrayList<>();
        userlist.sort(User::comparator);
        if(userlist.size() >= top) {
            for (int i = 0; i < top; i++) {
                arr.add(userlist.get(i));
            }
        }
        else{
            arr.addAll(userlist);
        }
        return arr;
    }


    @Override
    public void timed_flush(int waittime) {
        temp.clear();
        Job job = minHeap.extractMax();
        while(time - job.arriving_time >= waittime){
            if(job.jobstatus.equals("COMPLETED")){
                 job = minHeap.extractMax();
                 if(job==null)
                     break;
                 else
                    continue;
            }
            else if(job.jobstatus.equals("REQUESTED") && job.runtime > job.project.budget){
                temp.add(job);
                job = minHeap.extractMax();
                if(job==null)
                    break;
                else
                    continue;
            }
            else if(job.jobstatus.equals("REQUESTED") && job.runtime <= job.project.budget){
                priormaxheap.insert(job);
                job = minHeap.extractMax();
                if(job==null)
                    break;
                else
                    continue;
            }
        }
        for(int i=0;i<temp.size();i++){
            minHeap.insert((Job) temp.get(i));
        }
        while(priormaxheap.size != 0){
            Job job1 = priormaxheap.extractMax();
            RadixNode<Project> project = projecttrie.search(job1.project.name);
            RadixNode<User> user = usertrie.search(job1.user);
            //System.out.println("Executing: "+job1.name+" from: "+project.value.name);
            job1.jobstatus="COMPLETED";
            this.time=this.time+job1.runtime;
            job1.end_time=this.time;
            project.value.budget=project.value.budget-job1.runtime;
            queuelist.add(job1);
            user.value.consumed+=job1.runtime;
            user.value.latest_consumption = job1.end_time;
        }
    }


    private ArrayList<JobReport_> handle_new_priority(String s) {
        temp.clear();

        for(int i=0;i<totaljoblist.size();i++){
            if(totaljoblist.get(i).jobstatus.equals("REQUESTED") && totaljoblist.get(i).priority>=Integer.parseInt(s))
                temp.add(totaljoblist.get(i));
        }
        return temp;
    }


    private ArrayList<JobReport_> handle_new_projectuser(String[] cmd) {
        temp.clear();
        ArrayList<Job> arr = new ArrayList<>(10000);

        for(int i=0;i<totaljoblist.size();i++){
            if(totaljoblist.get(i).project.name.compareTo(cmd[1])==0 && totaljoblist.get(i).user.compareTo(cmd[2])==0 && totaljoblist.get(i).arriving_time >= Integer.parseInt(cmd[3]) && totaljoblist.get(i).arriving_time <= Integer.parseInt(cmd[4])){
                if(totaljoblist.get(i).jobstatus.compareTo("COMPLETED")==0)
                    arr.add(totaljoblist.get(i));
                else
                    temp.add(totaljoblist.get(i));
            }
        }
        arr.sort(Job::comparator);
        for(int i=arr.size()-1;i>=0;i--){
            temp.add(0, arr.get(i));
        }
        return temp;
    }

    private ArrayList<JobReport_> handle_new_user(String[] cmd) {
        temp.clear();

        for(int i=0;i<totaljoblist.size();i++){
            if(totaljoblist.get(i).user.compareTo(cmd[1])==0 && totaljoblist.get(i).arriving_time >= Integer.parseInt(cmd[2]) && totaljoblist.get(i).arriving_time <= Integer.parseInt(cmd[3])){
               temp.add(totaljoblist.get(i));
            }
        }
        return temp;
    }


    private ArrayList<JobReport_> handle_new_project(String[] cmd) {
        temp.clear();

        for(int i=0;i<totaljoblist.size();i++){
            if(totaljoblist.get(i).project.name.compareTo(cmd[1])==0 && totaljoblist.get(i).arriving_time >= Integer.parseInt(cmd[2]) && totaljoblist.get(i).arriving_time <= Integer.parseInt(cmd[3])){
                temp.add(totaljoblist.get(i));
            }
        }
        return temp;
    }



    public void schedule() {
        execute_a_job();
    }

    public void handle_empty_line() {
        System.out.println("Running code");
        System.out.println("Remaining jobs: "+jobheap.size);
        while(jobheap.size!=0){
            Job job = jobheap.extractMax();
            if (job.jobstatus.compareTo("COMPLETED")==0) {
                continue;
            }
            RadixNode<Project> project = projecttrie.search(job.project.name);
            RadixNode<User> user = usertrie.search(job.user);
            if(job.runtime  <=  project.value.budget){
                System.out.println("Executing: "+job.name+" from: "+project.value.name);
                job.jobstatus="COMPLETED";
                this.time=this.time+job.runtime;
                job.end_time=this.time;
                project.value.budget=project.value.budget-job.runtime;
                System.out.println("Project: "+project.value.name+" budget remaining: "+project.value.budget);
                queuelist.add(job);
                user.value.consumed+=job.runtime;
                user.value.latest_consumption = job.end_time;
                completed_jobs+=1;
                break;
            }
            else{
                rbtree.insert(project.value,job);
                System.out.println("Executing: "+job.name+" from: "+project.value.name);
                System.out.println("Un-sufficient budget.");
                job.jobstatus="REQUESTED";
                queuelist.add(job);
            }
        }
        System.out.println("Execution cycle completed");
    }

    public void handle_project(String[] cmd) {
        System.out.println("Creating project");
        Project project = new Project(cmd[1],Integer.parseInt(cmd[2]),Integer.parseInt(cmd[3]));
        projecttrie.insert(cmd[1],project);
    }
    public void timed_handle_project(String[] cmd) {
        Project project = new Project(cmd[1],Integer.parseInt(cmd[2]),Integer.parseInt(cmd[3]));
        projecttrie.insert(cmd[1],project);
    }

    public void handle_user(String name) {
        System.out.println("Creating user");
        User user = new User(name);
        usertrie.insert(name,user);
        userlist.add(user);
    }
    public void timed_handle_user(String name) {
        User user = new User(name);
        usertrie.insert(name,user);
        userlist.add(user);
    }

    public void handle_job(String[] cmd) {
        System.out.println("Creating job");
        RadixNode<Project> temp = projecttrie.search(cmd[2]);
        RadixNode<User> temp1 = usertrie.search((cmd[3]));
        if(temp==null)
            System.out.println("No such project exists. "+cmd[2]);
        else if(temp1==null)
            System.out.println("No such user exists: "+cmd[3]);
        if(temp != null && temp1 !=null) {
            int priority = temp.getValue().priority;
            timestap+=1;
            Job job = new Job(cmd[1], temp.value, priority, cmd[3], Integer.parseInt(cmd[4]),timestap,time);
            jobheap.insert(job);
            totaljobs.insert(job.name,job);
            totaljoblist.add(job);
            minHeap.insert(job);
        }
    }

    public void timed_handle_job(String[] cmd) {
        RadixNode<Project> temp = projecttrie.search(cmd[2]);
        RadixNode<User> temp1 = usertrie.search((cmd[3]));
        if(temp==null)
            return;
        else if(temp1==null)
            return;
        else  {
            int priority = temp.getValue().priority;
            timestap+=1;
            Job job = new Job(cmd[1], temp.value, priority, cmd[3], Integer.parseInt(cmd[4]),timestap,time);
            jobheap.insert(job);
            totaljobs.insert(job.name,job);
            totaljoblist.add(job);
            minHeap.insert(job);
        }
    }

    public void handle_query(String key) {
        System.out.println("Querying");
        RadixNode<Job> job = totaljobs.search(key);
        if(job==null)
            System.out.println(key + ": NO SUCH JOB");
        else{
            if(job.value.jobstatus.compareTo("COMPLETED")==0)
                System.out.println(key + ": "+job.value.jobstatus);
            else
                System.out.println(key + ": "+"NOT FINISHED");
        }

    }

    public void handle_add(String[] cmd) {
        System.out.println("ADDING Budget");
        RadixNode<Project> project = projecttrie.search(cmd[1]);
        if(project==null) {
            System.out.println("No such projects exists. "+ cmd[1]);
            return;
        }
        else {
            project.getValue().budget = project.getValue().budget+Integer.parseInt(cmd[2]);
            RedBlackNode<Project,Job> r = rbtree.search(project.getValue());
            if(r.value==null){
                return;
            }
            int j = r.value.size();
            for(int i=0;i<j;i++){
                jobheap.insert(r.value.get(i));
                queuelist.remove(r.value.get(i));
            }
            r.value.clear();
        }
    }


    public void execute_a_job() {

    }

    public void run_to_completion() {
        while (jobheap.size != 0) {
            System.out.println("Running code");
            System.out.println("Remaining jobs: " + jobheap.size);
            Job job = jobheap.extractMax();
            if (job.jobstatus.compareTo("COMPLETED") == 0)
                continue;

            RadixNode<Project> project = projecttrie.search(job.project.name);
            RadixNode<User> user = usertrie.search(job.user);
            if (project.value.budget < job.runtime) {
                System.out.println("Executing: " + job.name + " from: " + project.value.name);
                System.out.println("Un-sufficient budget.");
                job.jobstatus = "REQUESTED";
                queuelist.add(job);
                continue;
            }
            else {
                System.out.println("Executing: " + job.name + " from: " + project.value.name);
                job.jobstatus = "COMPLETED";
                this.time = this.time + job.runtime;
                job.end_time = this.time;
                project.value.budget = project.value.budget - job.runtime;
                System.out.println("Project: " + project.value.name + " budget remaining: " + project.value.budget);
                queuelist.add(job);
                user.value.consumed += job.runtime;
                user.value.latest_consumption = job.end_time;
                completed_jobs += 1;
                System.out.println("System execution completed");
            }
        }
    }

    public void timed_run_to_completion() {
        while (jobheap.size != 0) {

            Job job = jobheap.extractMax();

            if(job.jobstatus.compareTo("COMPLETED")==0)
                continue;

            RadixNode<Project> project = projecttrie.search(job.project.name);
            RadixNode<User> user = usertrie.search(job.user);
            if (project.value.budget < job.runtime) {

                job.jobstatus = "REQUESTED";
                queuelist.add(job);
                continue;
            }
            else {

                job.jobstatus = "COMPLETED";
                this.time = this.time + job.runtime;
                job.end_time = this.time;
                project.value.budget = project.value.budget - job.runtime;
                queuelist.add(job);
                user.value.consumed += job.runtime;
                user.value.latest_consumption = job.end_time;
                completed_jobs += 1;
            }
        }
    }

    public void print_stats() {
        System.out.println("--------------STATS---------------");
        System.out.println("Total jobs done: "+completed_jobs);
        for(int i=0;i<queuelist.size();i++){
            if(queuelist.get(i).jobstatus.compareTo("COMPLETED")==0) {
                Job job = queuelist.get(i);
                System.out.println("Job{user='" + job.user + "', project='" + job.project.name + "', jobstatus=" + job.jobstatus + ", execution_time=" + job.runtime + ", end_time=" + job.end_time + ", name='" + job.name + "'}");
            }
        }
        System.out.println("------------------------");
        System.out.println("Unfinished jobs: ");
        int uncompleted_jobs=0;
        for(int i=0;i<queuelist.size();i++){
            if(queuelist.get(i).jobstatus.compareTo("REQUESTED")==0) {
                uncompleted_jobs+=1;
                Job job = queuelist.get(i);
                System.out.println("Job{user='" + job.user + "', project='" + job.project.name + "', jobstatus=" + job.jobstatus + ", execution_time=" + job.runtime + ", end_time=null" + ", name='" + job.name + "'}");
            }
        }
        System.out.println("Total unfinished jobs: "+uncompleted_jobs);
        System.out.println("--------------STATS DONE---------------");
    }
}
