package contiguityTree;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        basicTest();
        superTest();
    }
    
    private static void basicTest () {
    	System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Starting Basic Test");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();
        
        PQTree tree = new PQTree();
        System.out.println("Successfully made tree\n");
        
        Object[] permutation1 = new Object[] { "A", "B", "C", "D", "E" };
        Object[] permutation2 = new Object[] { "D", "A", "C", "B", "E" };
        Object[] permutation3 = new Object[] { "E", "D", "C", "B", "A" };
        Object[] permutation4 = new Object[] { "B", "E", "D", "A", "C" };
        Object[] permutation5 = new Object[] { "C", "A", "E", "B", "D" };

        observePermutation(tree, permutation1);
        observePermutation(tree, permutation2);
        observePermutation(tree, permutation3);
        observePermutation(tree, permutation4);
        observePermutation(tree, permutation5);

        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Basic Test Done");
        System.out.println("--------------------------------------------------------------------------------");
    }

    private static void observePermutation(PQTree ct, Object[] permutation){
        printPermutation(permutation);
        System.out.println();
        ct.absorbPermutation(permutation);
        System.out.println("Resulting Tree:");
        ct.print();     
    }
    
    private static void printPermutation (Object[] permutation){
        System.out.print("Observing Permutation: ");
        for(Object d : permutation) System.out.print(d + ", ");
        System.out.println();
    }
    
    
    //SUPER TEST DEMOS BROKEN UP... for Leo's sanity :)
    private static int [] a1 = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,33,34};
    private static int [] a2 = new int[] {13,14,15,11,12,17,18,16,21,28,23,26,24,27,25,29,30,31,19,20,33,34,32,5,6,4,9,7,10,8,2,3,1};
    private static int [] a3 = new int[] {18,16,17,30,31,19,20,21,24,25,26,27,28,23,29,34,32,33,7,8,9,10,3,1,2,6,4,5,11,12,13,14,15};
    
    private static int [] c1 = new int[] {101,102};
    private static int [] c2 = c1;
    private static int [] c3 = c1;
    
    private static int [] d1 = new int[] {301,302,303,304};
    private static int [] d2 = new int[] {302,304,301,303};
    private static int [] d3 = d1;
    
    private static int [] e1 = new int[] {401,402,403};
    private static int [] e2 = new int[] {403,402,401};
    private static int [] e3 = e1;
    
    private static int [] f1 = new int[] {501,502,503,504,505};
    private static int [] f2 = new int[] {502,503,501,505,504};
    private static int [] f3 = new int[] {503,501,502,504,505};
    
    //b node is more complex!
    private static int [] ba1 = new int[] {201,202,203,204,205,206,207,208,209,210};
    private static int [] ba2 = new int[] {209,210,204,207,206,205,208,202,203,201};
    private static int [] ba3 = new int[] {203,201,202,208,205,206,207,204,209,210};
    
    private static int [] bb1 = new int[] {211,212,213,214,215,216,217,218,219};
    private static int [] bb2 = new int[] {219,212,213,211,218,217,215,216,214};
    private static int [] bb3 = new int[] {216,214,215,217,218,219,213,211,212};
    
    private static int [] bc1 = new int[] {220,221};
    private static int [] bc2 = bc1;
    private static int [] bc3 = bc1;
    
    private static int [] be1 = new int[] {270,271,272};
    private static int [] be2 = new int[] {272,271,270};
    private static int [] be3 = be1;
    
    private static int [] bf1 = new int[] {273,274,275,276,277,278};
    private static int [] bf2 = new int[] {275,276,277,278,273,274};
    private static int [] bf3 = new int[] {277,278,273,274,275,276};
    
    private static int [] bda1 = new int[] {222,223,224,225,226,227,228,229,230,231,232,233};
    private static int [] bda2 = new int[] {231,232,233,226,225,228,227,230,229,223,224,222};
    private static int [] bda3 = new int[] {224,222,223,225,226,227,228,229,230,233,232,231};
    
    private static int [] bdb1 = new int[] {234,235,236};
    private static int [] bdb2 = new int[] {235,236,234};
    private static int [] bdb3 = new int[] {236,234,235};
    
    private static int [] bdc1 = new int[] {237,238,239,240,241,242};
    private static int [] bdc2 = new int[] {241,242,239,240,237,238};
    private static int [] bdc3 = new int[] {238,237,240,239,242,241};
    
    private static int [] bdd1 = new int[] {243,244,245,246,247,248,249,250,251,252,253,254,255,256};
    private static int [] bdd2 = new int[] {250,249,252,253,251,247,248,245,246,256,255,254,244,243};
    private static int [] bdd3 = new int[] {254,255,256,243,244,245,246,247,248,249,250,253,251,252};
    
    private static int [] bde1 = new int[] {257,258,259,260,261,262,263,264,265};
    private static int [] bde2 = new int[] {264,265,263,261,262,260,258,259,257};
    private static int [] bde3 = new int[] {259,257,258,262,260,261,265,263,264};
    
    private static int [] bdf1 = new int[] {266,267,268,269};
    private static int [] bdf2 = new int[] {267,269,266,268};
    private static int [] bdf3 = new int[] {266,267,268,269};
    
    private static void superTest () {
    	System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Starting Super Test");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println();

    	PQTree giant = new PQTree();
    	
    	accumulateAndSendPermsToTree(giant,a3,bf3,be3, bda3,bdb3,bdc3,bdd3,bde3,bdf3 ,bc3,bb3,ba3,c3,d3,e3,f3);
    	accumulateAndSendPermsToTree(giant,d2,ba2,bb2,bc2, bda2,bdb2,bdc2,bdd2,bde2,bdf2 ,be2,bf2,f2,a2,e2,c2);
    	accumulateAndSendPermsToTree(giant,a1,ba1,bb1,bc1, bda1,bdb1,bdc1,bdd1,bde1,bdf1 ,be1,bf1,c1,d1,e1,f1);
    	
    	System.out.println();
    	giant.print();

        System.out.println();
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Super Test Done");
        System.out.println("--------------------------------------------------------------------------------");
    }
    
    private static void accumulateAndSendPermsToTree (PQTree ct, int []... permParts){
    	int totalLength =0;
    	for (int [] d : permParts) {
    		totalLength+= d.length;
    	}
    	Integer [] accumulatedDemo = new Integer [totalLength];
    	int index=0;
    	for (int [] d : permParts) {
    		for (int j : d){
    			accumulatedDemo[index++] = j;
    		}
    	}
    	System.out.println("Observing Permutation: "+ Arrays.toString(accumulatedDemo));
    	ct.absorbPermutation(accumulatedDemo);
    }
}
