package contiguityTreeBuilder;

import java.util.List;

public class ContiguityTreeBuilder <T> {
	private int[][] min, max, shiftedRange;
	
	public void buildTree(List<T> list1, List<T> list2) {
		int size = list1.size();
		
		//the following arrays will be used as some sort of book-keeping to reduce our work :)
		min = new int[size][size];
		max = new int[size][size];
		shiftedRange = new int[size][size];  //range=max-min. 				when range[i][j]==i		    then l2.get(i..i+j) is contiguous. (not true for i=0)
											 //shiftedRange=range[i][j]-i	when shiftedRange[i][j]==0  ^            
											 //shiftedRange used for ease of debugging will not be used, simply for readability
		
		//let us initialize sum[0] and min[0] in such a way that (sum|min)[0][i] = the index in list1 where we can find the object list2.get(i)
		//ex: if l1 = A,B,C,D and l2 = A,C,D,B then max[0] = [0, 2, 3, 1] as does min[0]
		indexLists(list1,list2,min,max);
		buildBookkeeping(min,max,shiftedRange);
		
	}
	
	public void printBooks() {
		print2DIntArrays(min,max,shiftedRange);
	}
	
	public void printShiftedRange(){
		print2DIntArrays(shiftedRange);
	}
	
	private void indexLists(List<T> l1, List<T> l2, int[][] mini, int[][] maxi){
		for (int index=l2.size()-1; index>=0; index--){
			int indexInL1 = l1.indexOf(l2.get(index));
			mini[0][index] = indexInL1;
			maxi[0][index] = indexInL1;
		}
	}
	
	private void buildBookkeeping(int[][] mini, int[][]maxi, int[][] sRange ){
		int size = mini[0].length;
		for (int i=1; i<size; i++){
			for (int j=0; j<=size-j; j++){
				mini[i][j]=Math.min(mini[i-1][j],mini[i-1][j+1]);
				maxi[i][j]=Math.max(maxi[i-1][j],maxi[i-1][j+1]);
				sRange[i][j]=maxi[i][j]-mini[i][j]-i;
			}
		}
		//0 out sRange[0][..]
		for(int j=0; j<size; j++){
			sRange[0][j]=-1;
		}
	}
	
	private void print2DIntArrays(int[][]... arrays){
		int length = arrays[0].length;
		for (int i=0; i<length; i++){
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
