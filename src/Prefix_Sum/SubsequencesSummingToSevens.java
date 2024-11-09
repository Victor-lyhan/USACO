package Prefix_Sum;/* USACO 2016 January Contest, Silver
Problem 2. Subsequences Summing to Sevens

SAMPLE INPUT:
7
3
5
1
6
2
14
10
SAMPLE OUTPUT:
5
In this example, 5+1+6+2+14 = 28.

*/

import java.util.*;
import java.io.*;

public class SubsequencesSummingToSevens {
    public static void main(String[] args) throws FileNotFoundException{
        Scanner input = new Scanner(System.in);
        //Scanner input = new Scanner(new File("Prefix_Sum/div7.in"));
        int cowNum = input.nextInt();
        int[] cow = new int[cowNum + 1];
        cow[0] = 0;

        for(int i=1; i<=cowNum; i++){
            cow[i] = input.nextInt() + cow[i-1];
        }
//        for(int a: cow){
//            System.out.println(a);
//        }

        int maxGroup = -1;
        for(int i=0; i<cow.length; i++){
            int curMax = -1;
            int curRemainder = cow[i] % 7;
            for(int j=i; j<cow.length; j++){
                if(cow[j] % 7 == curRemainder){
                    int length = j - i;
                    curMax = Math.max(length, curMax);
                }
            }
            maxGroup = Math.max(curMax, maxGroup);
        }

        System.out.println(maxGroup);
//        PrintWriter a = new PrintWriter(new File("div7.out"));
//        a.println(maxGroup);
//        a.close();
    }
}
