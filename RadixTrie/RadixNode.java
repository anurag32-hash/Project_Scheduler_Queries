package RadixTrie;

import java.util.ArrayList;

public class RadixNode<T> {
    public RadixNode<T> [] children = new RadixNode[128];
    public boolean isword ;
    public T value;
    public String key;

    public RadixNode(){
        this.value=null;
        this.isword=false;
        this.key="";
    }
    public void setValue (T value){
        this.value = value;
    }

    public T getValue (){
        return this.value;
    }

    public void setKey (String key){
        this.key=key;
    }

    public String getKey(){
        return this.key;
    }

    public void setIsword(boolean datanode){
        this.isword=datanode;
    }

    public boolean getIsword(){
        return this.isword;
    }

    public RadixNode [] getChildren(){
        return this.children;
    }

    public void setChildren(RadixNode<T>[] children ){
        for (int i=0; i<128; i++){
            this.children[i] = children[i];
        }
    }

    public int getNumberOfMatchingCharacters(String key){
        int numberofmatchingcharacters = 0;
        while(numberofmatchingcharacters < key.length() && numberofmatchingcharacters < this.getKey().length()){
            if(key.charAt(numberofmatchingcharacters) != this.getKey().charAt(numberofmatchingcharacters)){
                break;
            }
            numberofmatchingcharacters++;
        }
        return numberofmatchingcharacters;
    }



}
