package ProjectManagement;

public class Job  implements Comparable<Job>,JobReport_  {
    public String name,user;
    public String jobstatus = "REQUESTED";
    public int end_time=0,arriving_time;
    public Project project;
    public int priority,runtime,timestap;


    public Job(String name,Project project,int priority,String user,int runtime,int timestap,int arriving_time){
        this.name=name;
        this.project=project;
        this.priority=priority;
        this.user=user;
        this.runtime=runtime;
        this.timestap=timestap;
        this.arriving_time = arriving_time;
    }
    @Override
    public int compareTo(Job job) {
        if(this.priority != job.priority){
            return this.priority - job.priority;
        }
        else
            return job.timestap - this.timestap;
    }



    public String user(){
        return this.user;
    }
    public String project_name(){
        return this.project.name;
    }
    public int budget (){
        return this.runtime;
    }
    public int arrival_time(){
        return this.arriving_time;
    }
    public int completion_time(){
        return this.end_time;
    }
    public String getstatus(){
        return this.jobstatus;
    }

    public static int comparator (Job job1,Job job2) {
        if (job1.end_time > job2.end_time)
            return 1;

        else if (job1.end_time == job2.end_time) {
            if (job1.arriving_time > job2.arriving_time)
                return 1;
            else if (job1.end_time < job2.end_time)
                return -1;
        }
        return -1;
    }
    public int comparearrive (Job job){
        if(this.arriving_time != job.arriving_time){
            return this.arriving_time - job.arriving_time;
        }
        else
            return job.timestap - this.timestap;
    }
    public String toString (){
        return ("Job{user='"+this.user+"', project='"+this.project.name+"', jobstatus="+this.jobstatus+", execution_time="+this.runtime+", end_time="+this.end_time+", name='"+this.name+"'}"+"arrival_time = "+this.arriving_time);
    }
}