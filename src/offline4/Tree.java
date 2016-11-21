/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offline4;

import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import static offline4.Offline4.entropy;

/**
 *
 * @author masud
 */
class Branch {

    Node node;
    int valueChosen;

}

class Node {

    int attribute;
    int label;
    ArrayList<Branch> child;
    int leaf;

    Node() {
        leaf = 0;
        child = new ArrayList<>();
    }

}

public class Tree {

    static Node buildTree(ArrayList<Data> samples, int attr[]) {
        Node tree = new Node();
        int index = getIndex(samples, attr);
        //System.out.println("Tree index "+index);
        tree.attribute = index;
        int i=1;
        while(i < 11) {
            Branch br = new Branch();
            br.valueChosen = i;

            ArrayList<Data> temp = getRangeData(samples, index, i);
            Node n = new Node();
            int label = getlabel(samples);
            n.label = label;
            n.leaf = 1;
            br.node = n;
            tree.child.add(br);
            i++;
        }
        return tree;
    }

    static int getlabel(ArrayList<Data> data) {
        int pos = 0, neg = 0;
        int i=0;
        while (i < data.size()) {
            Data ob = data.get(i);
            if (ob.Class !=1 ) {
                neg++;
            } else {
                pos++;
            }
            i++;
        }
        if (pos > neg) {
            return 1;
        } else {
            return 0;
        }
    }
    
    static double getEntropy(int Pos,int Neg,double count){
        double a = (double) Pos / count;
        double b = (double) Neg / count;
        if (a == 0) {
            a = 1;
        }
        if (b == 0) {
            b = 1;
        }
        double Entropy = -(a * (log(a) / log(2))) - (b * (log(b) / log(2)));
        return Entropy;
    }

    static int getIndex(ArrayList<Data> data, int attr[]) {
        int pos[][] = new int[8][11];
        int neg[][] = new int[8][11];
        
//        for(int i=0;i<8;i++)
//       {
//           for(int j=1;j<11;j++)
//           {
//               pos[i][j]=0;
//               neg[i][j]=0;
//           }
//       }
        int i=0;
        while(i<data.size()) {
            Data ob = data.get(i);
            if (ob.Class == 1) {
                for(int I=0;I<8;I++){
                    int temp=ob.ara[I];
                    pos[I][temp]++;
                }
            } 
            else {
                for(int I=0;I<8;I++){
                    int temp=ob.ara[I];
                    neg[I][temp]++;
                }
            }
            i++;
        }
        double Maximum = -99999.0;
        int index = 0;
        i=0;
        while(i<8) {
            if (attr[i] == 1) {
                double tempGain = 0;
                int j=0;
                while(j < 11) {
                    double count = pos[i][j] + neg[i][j], en = 0;
                    if (count!=0) {
                        en=getEntropy(pos[i][j],neg[i][j],count);
                        //System.out.println(count + "  "+ i);
                    }
                    tempGain = tempGain + en * count / (double) data.size();
                    j++;
                }
                tempGain = entropy - tempGain;
                //System.out.println("tempGain "+tempGain);
                if (Maximum < tempGain) {
                    Maximum = tempGain;
                    index = i;
                }
            }
            i++;
        }
        return index;
    }
    
    static ArrayList<Data> getRangeData(ArrayList<Data> data, int index, int value)
   {
       ArrayList<Data> temp = new ArrayList<>();
       int i=0;
       while(i<data.size())
       {
           Data ob = data.get(i);
           int in = ob.getValue(index);
           if(in!=value){
              //System.out.println("$ "+in+" "+i);
           }
           if(in == value){
               temp.add(ob);
               //System.out.println("$ "+in+" "+i);
           }
           i++;
       }
       return temp;
   }
}
