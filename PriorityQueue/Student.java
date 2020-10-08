package PriorityQueue;

public class Student implements Comparable<Student> {
    private String name;
    private Integer marks;

    public Student(String name, int marks) {
        this.name=name;
        this.marks=marks;
    }


    @Override
    public int compareTo(Student student) {
        return this.marks.compareTo(student.marks);
    }

    public String getName() {
        return this.name;
    }
    public String toString(){
        String str = "Student{name='"+this.name+"', marks="+this.marks+"}";
        return str;
    }
}
