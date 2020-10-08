package ProjectManagement;

import java.util.ArrayList;

public class MinHeap {

    public ArrayList<Job> heap ;
    public int size;
    public int capacity;

    public MinHeap() {
        this.size = 0;
        heap = new ArrayList<>();
        heap.add(null);
    }

    private int parent(int currpos) {
        return currpos / 2;
    }

    private int rightchild(int currpos) {
        return (2 * currpos) + 1;
    }

    private int leftchild(int currpos){
        return (2 * currpos);
    }

    private void swap(int current, int parent) {
        Job temp;
        temp = heap.get(current);
        heap.set(current, heap.get(parent));
        heap.set(parent, temp);
    }

    public void insert(Job element) {
        if (this.size == 0) {
            this.size += 1;
            heap.add(size, element);
            return;
        } else {
            this.size += 1;
            heap.add(size, element);
            int temp = this.size;

            while (parent(temp) >= 1 && temp > 1 && heap.get(temp).comparearrive(heap.get(parent(temp)))<0) {
                swap(temp, parent(temp));
                temp = parent(temp);
            }
        }
    }

    public Job extractMax() {

        if(this.size==0){
            return null;
        }

        else{

            Job popped = heap.get(1);
            heap.set(1,heap.get(this.size));
            heap.remove(this.size);
            this.size-=1;
            int i=1;
            while(leftchild(i)<=this.size){
                if(rightchild(i)<=this.size){
                    if(heap.get(leftchild(i)).comparearrive(heap.get(rightchild(i)))<0){
                        if(heap.get(leftchild(i)).comparearrive(heap.get(i))<0){
                            swap(i,leftchild(i));
                            i=leftchild(i);
                        }
                        else{
                            break;
                        }
                    }
                    else{
                        if(heap.get(rightchild(i)).comparearrive(heap.get(i))<0){
                            swap(i,rightchild(i));
                            i=rightchild(i);
                        }
                        else{
                            break;
                        }
                    }
                }
                else{
                    if(heap.get(leftchild(i)).comparearrive(heap.get(i))<0){
                        swap(i,leftchild(i));
                        i=leftchild(i);
                    }
                    else
                        break;
                }
            }
            return popped;
        }
    }
}
