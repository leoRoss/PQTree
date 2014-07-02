package contiguityTreeBuilder;

import java.util.List;
import contiguityTree.Task;

public class ContiguityTreeBuilder {
	private int[][] min, max, shiftedRange;
	private Task[] tasks;
	
	public void buildTree(List<Task> list1, List<Task> list2) {
		int size = list1.size();
		
		//the following arrays will be used as some sort of book-keeping to reduce our work :)
		tasks = new Task[size]; //copy of list 2
		min = new int[size][size];
		max = new int[size][size];
		shiftedRange = new int[size][size];  //range=max-min. 				when range[i][j]==i		    then l2.get(i..i+j) is contiguous. (not true for i=0)
											 //shiftedRange=range[i][j]-i	when shiftedRange[i][j]==0  ^            
											 //shiftedRange used for ease of debugging will not be used, simply for readability
		
		//let us initialize sum[0] and min[0] in such a way that (sum|min)[0][i] = the index in list1 where we can find the object list2.get(i)
		//ex: if l1 = A,B,C,D and l2 = A,C,D,B then max[0] = [0, 2, 3, 1] as does min[0]
		setUpBooks(list1,list2);
		buildBookkeeping();
		buildTree();
	}
	
	
	private void setUpBooks(List<Task> l1, List<Task> l2){
		int index=0;
		for (Task task : l2){
			int indexInL1 = l1.indexOf(task);
			min[0][index] = indexInL1;
			max[0][index] = indexInL1;
			tasks[index++] = task;
		}
	}
	
	private void buildBookkeeping( ){
		int size = min[0].length;
		
		//-1 for sRange[0][..] (not interested in contiguous groups of size 1)
		for(int j=0; j<size; j++){
			shiftedRange[0][j]=-1;
		}
				
		//build pyramid
		for (int i=1; i<size; i++){
			for (int j=0; j<=size-i-1; j++){
				min[i][j]=Math.min(min[i-1][j],min[i-1][j+1]);
				max[i][j]=Math.max(max[i-1][j],max[i-1][j+1]);
				shiftedRange[i][j]=max[i][j]-min[i][j]-i;
			}
		}
	}
	
	//after debugging, this can be moved inside buildBookkeeping
	private void buildTree(){
		int size = shiftedRange[0].length;
		//go through pyramid pyramid
		for (int i=1; i<size; i++){
			for (int j=0; j<=size-i-1; j++){
				if (shiftedRange[i][j]==0) buildNode(i,j);
			}
		}
	}
	
	private void buildNode(int i, int j) {
		for (int k = j; k<=j+i;){
			//TODO
		}
	}
	
	public void printBooks() {
		print2DIntArrays(min,max,shiftedRange);
	}
	
	public void printShiftedRange(){
		print2DIntArrays(shiftedRange);
	}
	
	private void print2DIntArrays(int[][]... arrays){
		int length = arrays[0].length;
		System.out.print(String.format("      :: "));
		for(int j=0; j<arrays[0][0].length; j++){
			System.out.print(String.format("%2d | ", arrays[0][0][j]));
		}
		System.out.println();
		System.out.println();
		for (int i=1; i<length; i++){
			for (int[][] array : arrays) {
				System.out.print(String.format("ROW%2d :: ", i));
				for(int j=0; j<array[0].length-i; j++){
					System.out.print(String.format("%2d | ", array[i][j]));
				}
				System.out.println();
			}
		}
	}


}
