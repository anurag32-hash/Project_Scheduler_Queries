package RedBlack;

import java.util.ArrayList;

public class RBTree<T extends Comparable, E> implements RBTreeInterface<T, E>  {
    public RedBlackNode<T,E> root;
   public int rbtreesize;


    public RBTree(){
        this.root=null;
        this.rbtreesize=0;
    }

    public boolean isRed (RedBlackNode<T,E> x){
        if(x==null)
            return false;
        else{
            if(x.color.compareTo("red")==0)
                return true;
            return false;
        }
    }
    public int size(RedBlackNode<T,E> x){
        if(x==null)
            return 0;
        return x.size;
    }
    public int size(){
        return size(root);
    }
    public void flipcolors(RedBlackNode h) {
        if(h.color.equals("black"))
            h.color="red";
        else h.color="black";

        if(h.right.color.equals("black"))
            h.right.color="red";
        else
            h.right.color="black";

        if(h.left.color.equals("black"))
            h.left.color="red";
        else
            h.left.color="black";
    }

    @Override
    public void insert(T key, E value) {
        root=reccinsert(root,key,value);
        root.color="black";
    }
    public RedBlackNode<T,E> reccinsert(RedBlackNode<T,E> node,T key,E value){
        
        if (node == null)
            return new RedBlackNode<T, E>(key, value);
        if (key.compareTo(node.key) == 0)
        {
            node.getValues().add(value);
            return node;
        }
        if (key.compareTo(node.key) < 0)
            node.left = reccinsert(node.left, key, value);
        if (key.compareTo(node.key) > 0)
            node.right = reccinsert(node.right, key, value);

        //To balance the tree
        if (isRed(node.right) && isRed(node.right.left))
        {
            if (isRed(node.left))
                flipcolors(node);
            else
            {
                node.right = rightrotation(node.right);
                node = leftrotation(node);
            }
        }
        if (isRed(node.left) && isRed(node.left.right))
        {
            if (isRed(node.right))
                flipcolors(node);
            else
            {
                node.left = leftrotation(node.left);
                node = rightrotation(node);
            }
        }
        if (isRed(node.right) && isRed(node.right.right))
        {
            if (isRed(node.left))
                flipcolors(node);
            else
                node = leftrotation(node);
        }
        if (isRed(node.left) && isRed(node.left.left))
        {
            if (isRed(node.right))
                flipcolors(node);
            else
                node = rightrotation(node);
        }


        node.size = size(node.left) + size(node.right) + 1;

        return node;
    }



    private RedBlackNode<T, E> rightrotation (RedBlackNode<T,E> h){
        RedBlackNode<T,E> x = h.left;
        h.left=x.right;
        x.right=h;
        x.color=x.right.color;
        x.right.color="red";
        x.size=h.size;
        h.size=size(h.left)+size(h.right)+1;
        return x;
    }


    private RedBlackNode<T,E> leftrotation (RedBlackNode<T,E> h) {
        RedBlackNode<T,E> x = h.right;
        h.right=x.left;
        x.left=h;
        x.color=x.left.color;
        x.left.color="red";
        x.size=h.size;
        h.size=size(h.left)+size(h.right)+1;
        return x;
    }

    private void swapcolors (RedBlackNode<T,E> node1,RedBlackNode<T,E> node2){
        String str = node1.color;
        node1.color = node2.color;
        node2.color = str;
    }

    public void display (RedBlackNode<T,E> root){
        if(root==null)
            return;
        display(root.left);
        System.out.print(root.toString()+" ");
        System.out.print(root.color+" ");
        for(int i=0;i<root.getValues().size();i++){
            System.out.print(root.getValues().get(i)+" ");
        }
        System.out.println("");
        display(root.right);
    }

    @Override
    public RedBlackNode<T, E> search(T key) {
        RedBlackNode<T,E> temp = root;
        RedBlackNode<T,E> dnode = new RedBlackNode<>(key);
        while (true) {
            if (temp == null) {
                return new RedBlackNode<>(null,null);
            } else if (dnode.compareTo(temp) == 0) {
                return temp;
            } else if (dnode.compareTo(temp) > 0) {
                temp = temp.right;
            } else if (dnode.compareTo(temp) < 0) {
                temp = temp.left;
            }
        }
    }

    public RedBlackNode<T,E> search1 (T key){
        RedBlackNode<T,E> temp = root;
        RedBlackNode<T,E> dnode = new RedBlackNode<>(key);
        while(true){
            if(temp==null)
                return null;
            else if(dnode.compareTo(temp)==0)
                return temp;
           else if(dnode.compareTo(temp)>0)
               temp=temp.right;
           while(dnode.compareTo(temp)<0){
               if(temp.left == null)
                   return temp;
               temp=temp.left;
               if(dnode.compareTo(temp)<0)
                   continue;
               else if(temp==null){
                   temp=temp.parent;
                   return temp;
               }
               else if(dnode.compareTo(temp)>0){
                   temp=temp.parent;
                   return temp;
               }
            }

        }
    }

    public ArrayList<E> reccsearch (T t1,T t2){
        ArrayList<E> arr;
        arr = reccsearch1(root,t1,t2);
        return arr;
    }

    public ArrayList<E> reccsearch1 (RedBlackNode<T,E> node, T t1, T t2){
        ArrayList<E> arr = new ArrayList<>();

        if(node != null) {

             if (t1.compareTo(node.key) <= 0)
                arr.addAll(reccsearch1(node.left, t1, t2));
              if (t1.compareTo(node.key) <= 0 && t2.compareTo(node.key) >= 0)
                arr.addAll(node.value);
              if (t2.compareTo(node.key) >= 0)
                arr.addAll(reccsearch1(node.right, t1, t2));
        }
        return arr;
    }
}