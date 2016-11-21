/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offline4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import static offline4.Tree.buildTree;

/**
 *
 * @author masud
 */
class Data{
    int[]ara=new int[8];
    int Class;
    
    Data()
    {
        
    }
    Data(int Ara[],int Class)
    {
       this.ara=Ara;
       this.Class=Class;
    }
    
    void print()
    {
        for(int i=0;i<this.ara.length;i++){
//            System.out.print(this.ara[i]+" ");
        }
//        System.out.println(" --->"+this.Class);
    }
    int getValue(int index)
    {
        //System.out.println("Index "+index);
        return this.ara[index];
    }
    int getType(){
        return this.Class;
    }
}

class Models {

    Node node;
    double beta;

}
public class Offline4 {

    /**
     * @param args the command line arguments
     */
    
    static ArrayList<Data>dataSet;
    static ArrayList<Models>models=new ArrayList<>();
    static int learnDataSize;
    static ArrayList<Data> traindata = new ArrayList<>();
    static double[]weight;
    static double[]probability;
    static double entropy;
    static int Rounds=35;
    static int totaldata,trainSize,testSize,truePos=0,trueNeg=0,falsePos=0,falseNeg=0;
    static double recall, accuracy, precision;
    
    public static void getData() throws IOException{
        dataSet = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("assignment1_data_set.csv"));
        String line = br.readLine();
        while((line = br.readLine()) !=null){
             String[] b = line.split(",");
             int[]tempAra=new int[b.length-1];
             for(int i=0;i<b.length-1;i++){
                 tempAra[i]=Integer.valueOf(b[i]);
             }
             int Class=Integer.valueOf(b[b.length-1]);
             Data d =new Data(tempAra,Class);
             dataSet.add(d);
             //System.out.println();
        }
        br.close();
        for(int i=0;i<dataSet.size();i++)
        {
            Data ob =dataSet.get(i);
            //ob.print();
        }
        
    }
    
    static void shuffleData()
    {
        
        Random rand = new Random();
        double scale = rand.nextDouble();
        int it=0;
        while(it<totaldata)
        {
            double r = rand.nextDouble();
            if(r > scale)
            {
                int pos1 = rand.nextInt(totaldata);
                int pos2 = rand.nextInt(totaldata);
                Collections.swap(dataSet, pos1, pos2);
                //System.out.println("swapping");
            }
            it++;
        }
    }
    
    static double calculateEntropy(ArrayList<Data> traindata){
        int pos=0,neg=0;
        int i=0;
        while(i<traindata.size())
        {
            Data ob =traindata.get(i);
            //ob.print();
            //System.err.println("class "+ob.Class);
            if(ob.Class!=1) neg++;
            else pos++;
            i++;
        }
        //System.out.println(pos+" "+neg);
        double a = (double)pos/(double)traindata.size();
        double logA=log(a);
        if(a==0)logA=0;
        double b = (double)neg/(double)traindata.size();
        double logB=log(b);
        if(b==0)logB=0;
        
        double Entropy = -(a * (logA/log (2))) - (b * (logB/log (2)));
        return Entropy;
    }
    
    public static ArrayList<Data> getTrainData() throws IOException{
        ArrayList<Data> Learndata = new ArrayList<>();
        Random rand = new Random();
        learnDataSize=(int) (.7*trainSize);
        //System.out.println("Data Size "+learnDataSize);
        //return null;
        for(int I=0;I<learnDataSize;I++){
            double Sum=0.0;
            double r = rand.nextDouble();
            for(int J=0;J<probability.length;J++){
                Sum+=probability[J];
                //System.out.println("Sum "+probability[J]);
                if(r<=Sum){
                    Learndata.add(traindata.get(J));
                    break;
                }
            }
        }
        return Learndata;
        
    }
    
    static int getClass(Node n, Data ob)
   {
       if(n.leaf==1)
       {
           //System.out.println("Decision "+n.label);
           return n.label;
       }
       int index = n.attribute;
       //System.out.println("II "+index);
       int i=0;
       while(i<n.child.size())
       {
           Branch b = n.child.get(i);
           if(b.valueChosen==ob.getValue(index))
           {
               return getClass(b.node, ob);
           }
           i++;
       }
       return 0;
   }
    
    public static void Test(Node n){
        trueNeg=0;
        truePos=0;
        falseNeg=0;
        falsePos=0;
        int i=trainSize;
        while(i<dataSet.size())
        {
            Data ob = dataSet.get(i);
            int type = getClass(n, ob);

            if(ob.Class==type)
            {
                if(type==0)trueNeg++;
                else truePos++;
            }
            else
            {
                if(type==0)falseNeg++;
                else falsePos++;
            }
            i++;
        }
        accuracy = (double)(truePos+trueNeg)/(double)(truePos+trueNeg+falsePos+falseNeg);
        recall = (double)truePos/(double) (truePos+falseNeg);
        precision = (double)truePos/(double) (truePos+falsePos);
        
        //System.out.println("recall = "+recall+",  precision = "+precision+",  accuracy = "+accuracy);
    }
    
    public static double LearnTest(ArrayList<Data>testdata,Node n,double[] testProb){
        trueNeg=0;
        truePos=0;
        falseNeg=0;
        falsePos=0;
        int i=0;
        double Sum=0;
        //System.out.println("Dsize "+testdata.size());
        while(i<testdata.size())
        {
            Data ob = testdata.get(i);
            int type = getClass(n, ob);

            if(ob.Class!=type)
            {
                Sum+=testProb[i];
            }
            else truePos++;
            i++;
        }
//        accuracy = (double)(truePos+trueNeg)/(double)(truePos+trueNeg+falsePos+falseNeg);
//        recall = (double)truePos/(double) (truePos+falseNeg);
//        precision = (double)truePos/(double) (truePos+falsePos);
        
//        System.out.println("recall = "+recall+",  precision = "+precision+",  accuracy = "+accuracy);
        //System.out.println("correct "+truePos);
        return Sum;
    }
    
    public static void supervisedLearning() throws IOException{
        getData();
        totaldata = dataSet.size();
        testSize = (int)(.2*totaldata);
        trainSize = totaldata-testSize;
        //System.out.println("Test set = "+testSize+" Training set = "+trainSize);
        double trecall=0, taccuracy=0, tprecision=0;
        int numberOfIteration=10;
        for(int I=0;I<numberOfIteration;I++){
            shuffleData();
            ArrayList<Data> traindata = new ArrayList<>();
            int i=0;
            while(i<trainSize)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
                i++;
            }
            entropy=calculateEntropy(traindata);
            int attr[] = new int [8];
            for(i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            Test(n);
            trecall += recall;
            taccuracy += accuracy;
            tprecision += precision;
        }
        double avgRecall = trecall/(double)numberOfIteration;
        double avgAccuracy=taccuracy/(double)numberOfIteration;
        double avgPrecisions =tprecision/(double)numberOfIteration;
        double f1Score=2*((avgPrecisions*avgRecall)/(avgPrecisions+avgRecall));
        
//        System.out.println("Avg Recall = "+avgRecall);
//        System.out.println("Avg Precision = "+avgPrecisions);
        System.out.println("Avg Accuracy = "+avgAccuracy);
//        System.out.println("Avg F1-Score = "+f1Score);
    }
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        getData();
        int dataLen=dataSet.size();
        totaldata = dataSet.size();
        testSize = (int)(.2*totaldata);
        trainSize = totaldata-testSize;
        //System.out.println(dataLen);
        weight=new double[trainSize];
        for(int i=0;i<trainSize;i++){
            weight[i]=(1/(double)trainSize);
        }
        for(int i=0;i<weight.length;i++){
            //System.out.println(weight[i]);
        }
        probability=new double[trainSize];
        double Sum=0;
        for(int i=0;i<probability.length;i++){
            Sum+=weight[i];
        }
        for(int I=0;I<Rounds;I++){
            for(int i=0;i<probability.length;i++){
                probability[i]=weight[i]/Sum;
                
            }
            shuffleData();
            int i=0;
            while(i<trainSize)
            {
                Data d = dataSet.get(i);
                traindata.add(d);
                i++;
            }
            ArrayList<Data>learnData=new ArrayList<Data>();
            learnData=getTrainData();
            //System.out.println("Size "+learnData.size());
            /*ArrayList<Data>testdata=new ArrayList<Data>();
            ArrayList<Double>testProb=new ArrayList<Double>();
            for(int J=learnDataSize;J<dataSet.size();J++){
                Data d = dataSet.get(i);
                testdata.add(d);
                testProb.add(probability[J]);
            }*/
            
            entropy=calculateEntropy(learnData);
            //System.out.println("Entropy "+entropy);
            int attr[] = new int [8];
            for(i=0;i<8;i++)
                attr[i]=1;
            Node n =buildTree(traindata, attr);
            Models M=new Models();
            M.node=n;   
            double Error=LearnTest(traindata, n,probability);
            //Scanner sc = new Scanner(System.in);
            //int II = sc.nextInt();
            if(Error>0.5){
                Rounds=I-1;
                break;
            }
            double beta=(Error)/(1-Error);
            M.beta=beta;
            models.add(M);
            i=0;
            while(i<traindata.size()){
                Data ob = dataSet.get(i);
                int type = getClass(n, ob);

                if(ob.Class!=type){
                    weight[i]=weight[i]*beta;
                }
                i++;
            }
            traindata.clear();
        }
        
        int i=trainSize;
        int Correct=0,K=0;
        while(i<dataSet.size())
        {
            Data ob = dataSet.get(i);
            double posSum=0,negSum=0;
            int klass=0;
            for(int I=0;I<models.size();I++){
                int type = getClass(models.get(I).node, ob);
                if(type==1){
                    posSum+=log(1/models.get(I).beta);
                }
                else negSum+=log(1/models.get(I).beta);
            }
            if(posSum>negSum)klass=1;
            if(ob.Class==klass)Correct++;
            K++;
            i++;
        }
        accuracy = (double)(Correct)/(double)(K);
        //recall = (double)truePos/(double) (truePos+falseNeg);
        //precision = (double)truePos/(double) (truePos+falsePos);
        
        System.out.println("accuracy = "+accuracy);
        //supervisedLearning();
    }
    
}
