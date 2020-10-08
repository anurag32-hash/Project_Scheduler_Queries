package RadixTrie;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class RadixTree<T> {
    public RadixNode<T> root;
    public long size;

    public RadixTree() {
        root = new RadixNode<>();
        size = 0;
    }

    public void insert(String key, T value) {
        insert(key, root, value);
        size++;
    }

    public void insert(String key, RadixNode node, T value) {

        int numberofmatchingcharacters = node.getNumberOfMatchingCharacters(key);

        if (node.getKey().equals("") || numberofmatchingcharacters == 0 || numberofmatchingcharacters < key.length() && numberofmatchingcharacters >= node.getKey().length()) {
            //System.out.println("in first if");
            String newKey = key.substring(numberofmatchingcharacters);
            if (node.getChildren()[newKey.charAt(0)] == null) {
                RadixNode<T> n = new RadixNode<>();
                n.setKey(newKey);
                n.setValue(value);
                n.setIsword(true);
                node.getChildren()[newKey.charAt(0)] = n;
            } else {
                insert(newKey, node.getChildren()[newKey.charAt(0)], value);
            }
        } else if (numberofmatchingcharacters == key.length() && numberofmatchingcharacters == node.getKey().length()) {
            //System.out.println("match if else found");
            node.setIsword(true);
            node.setValue(value);
        } else if (numberofmatchingcharacters > 0 && numberofmatchingcharacters < node.getKey().length()) {
            //System.out.println("going in splitting else if");
            RadixNode<T> n1 = new RadixNode<>();
            n1.setKey(node.getKey().substring(numberofmatchingcharacters));
            n1.setValue((T) node.getValue());
            n1.setIsword(node.getIsword());
            n1.setChildren(node.getChildren());

            node.setKey(key.substring(0, numberofmatchingcharacters));
            node.setIsword(false);
            node.setChildren(new RadixNode[128]);
            node.getChildren()[n1.getKey().charAt(0)] = n1;

            if (numberofmatchingcharacters < key.length()) {
                RadixNode<T> n2 = new RadixNode<>();
                n2.setKey(key.substring(numberofmatchingcharacters));
                n2.setIsword(true);
                n2.setValue(value);
                node.getChildren()[n2.getKey().charAt(0)] = n2;
            } else {
                node.setValue(value);
                node.setIsword(true);
            }
        } else {
            // System.out.println("going in else");
            RadixNode<T> d = new RadixNode<>();
            d.setKey(node.getKey().substring(numberofmatchingcharacters));
            d.setChildren(node.getChildren());
            d.setIsword(node.getIsword());
            d.setValue((T) node.getValue());

            node.setKey(key);
            node.setValue(value);
            node.setIsword(true);
            node.setChildren(new RadixNode[128]);
            node.getChildren()[d.getKey().charAt(0)] = d;
        }
    }

    public RadixNode<T> search(String word) {
        if(root!=null)
            return reccsearch(word,root);
        else
            return null;
    }
    public RadixNode<T> reccsearch(String word,RadixNode node){
        int matches = node.getNumberOfMatchingCharacters(word);

        if(matches == word.length() && matches == node.getKey().length()){
            if(node.getIsword() == true)
                return  node;
        }
        else if (node.getKey().equals("") || (matches < word.length() && matches >= node.getKey().length())){
            String newKey = word.substring(matches);
            node = node.getChildren()[newKey.charAt(0)];
            if(node==null)
                return null;
            else
               return reccsearch(newKey,node);
        }
        return null;
    }

    public void print(){

        int level=1;
        String str="";
        if(this.root==null)
            return;

        Queue<RadixNode<T>> q = new LinkedList<>();
        RadixNode<T> trav=this.root;
        for(int i=0; i<128;i++){
            if(trav.children[i]!=null){
                q.offer(trav.children[i]);
            }
        }
        q.offer(null);
        System.out.println("Printing Trie");
        System.out.print("Level "+level+": ");
        while(!q.isEmpty()){
            trav  = q.poll();
            if(trav!=null){
                str = str.concat(trav.getKey()+",");
                for (int i = 0; i < 128; i++) {
                    if (trav.children[i] != null) {
                        q.offer(trav.children[i]);
                    }
                }
            }
            else{
                if(!q.isEmpty()){


                    System.out.println(str);

                    str="";
                    level+=1;
                    System.out.print("Level "+level+": ");
                    q.offer(null);
                }
                else {


                    System.out.println(str);

                    //System.out.println(sorted.charAt(sorted.length()-1));
                    str="";
                    level+=1;
                    System.out.println("Level "+level+": ");
                    System.out.println("-------------");
                }
            }
        }

    }

}

