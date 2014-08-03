package contiguityTree;

import java.util.ArrayList;
import java.util.List;

public class ContiguityTreeBuilder {
	private int[][] min, max;
	private Task[] list2tasks;
	
	/*
	 * This function takes in two lists of Tasks and returns a Contiguity Tree
	 * The lists must be composed of the same set of unique Tasks 
	 * We dynamically compute all contiguous groups
	 */
	public void buildContiguityTree(List<Task> list1, List<Task> list2) {
		
		int size = list1.size();
		if (size!=list2.size()) throw new Error("Lists must be of the same length when building a Contiguity Tree");
		
		//the following arrays will be used for book-keeping
		list2tasks = new Task[size]; 
		min = new int[size][size];
		max = new int[size][size];        
		
		/*We initialize max[0][..] and min[0][..] s.t. [0][i] = list1.indexOf( list2.get(i) )
		* We dynamically compute the remaining max s.t. max[i][j]=Math.max(max[i-1][j],max[i-1][j+1])
		* We dynamically compute the remaining min s.t. max[i][j]=Math.min(min[i-1][j],min[i-1][j+1])
		* Let range = max-min
		* Iff range[i][i] == i then l2.get(i..i+j) is contiguous. (not true for i=0)
		*
		* Example:
		* list1 = A,B,C,D,E,F
		* list2 = E,F,A,C,D,B 
		* 																
		* max[0] = [4, 5, 0, 2, 3, 1]  min[0] = [4, 5, 0, 2, 3, 1]             [E, F, A, C, D, B]
		* max[1] = [5, 5, 2, 3, 3]     min[1] = [4, 0, 0, 2, 1]    max-min-i = [0, 4, 1, 0, 1]    // the two 0 signify that groups EF and CD are both contiguous in the two lists
		* max[2] = [5, 5, 3, 3]        min[2] = [0, 0, 0, 1]       max-min-i = [3, 3, 1, 0]       // 0 signifies that CDB are contiguous in the two lists
		* max[3] = [5, 5, 3]           min[3] = [0, 0, 0]          max-min-i = [2, 2, 0]          // 0 signifies that ACDB are contiguous in the two lists
		* max[4] = [5, 5]              min[4] = [0, 0]             max-min-i = [1, 1]             // neither EFACD or FACDB are contiguous in the two lists
		* max[5] = [5]                 min[5] = [0]                max-min-i = [0]                // of course, EFACDB is a contiguous group in the two lists
		*
		* Why? This makes sense because we want the range of a group to be equal to its size. 
		* A group 7, 9, 6, 8 has a range of 4 and a size of 4. However, 7, 10, 6, 8 has a range of 5 and a size of 4.
		*/

		setUpBooks(list1,list2);
		
		dynamicallyFindContiguousGroups();
	}
	
	//Copy list2 into an list2tasks
	//Initialize min[0][..] and max[0][..]
	private void setUpBooks(List<Task> l1, List<Task> l2){
		int index=0;
		for (Task task : l2){
			int indexInL1 = l1.indexOf(task);
			if (indexInL1==-1) throw new Error ("Lists must contain the same set of unique elements when building a Contiguity Tree");
			min[0][index] = indexInL1;
			max[0][index] = indexInL1;
			list2tasks[index++] = task;
		}
	}
	
	//dynamically compute range
	//when range == i, we have found a contiguous group
	private void dynamicallyFindContiguousGroups( ){
		int size = min[0].length;
		
		//build pyramid
		for (int i=1; i<size; i++){
			for (int j=0; j<=size-i-1; j++){
				min[i][j]=Math.min(min[i-1][j],min[i-1][j+1]);
				max[i][j]=Math.max(max[i-1][j],max[i-1][j+1]);
				if (max[i][j]-min[i][j]==i) processContiguousGroup(j,j+i);
			}
		}
	}
	
	/*Not all contiguous groups merit to become a Task in the final Contiguity Tree
	* For example, assume list1 = ABCD && list2 = ABCD
	* Initially, list2tasks = 0 1 2 3.
	* 
	* We first dynamically discover group AB. We turn that into a task OrderedGroup#5: <AB>
	* Now, list2tasks = 5 5 2 3.
	* 
	* Next, we see that BC is also a contiguous group. 
	* However, since B has already been coupled with A via Task#5, we cannot "steal" B into a new task. 
	* 
	* Next, we see that CD is also a contiguous group. 
	* Since neither C or D are in still in their own tasks, we turn that into a task OrderedGroup#6 <CD>.
	* Now, list2tasks = 5 5 6 6.
	* 
	* We find, but ignore, groups ABC and BCD since C is already coupled with D and B is coupled with A.
	* 
	* Finally, we find group ABCD. Since forming task OrderedGroup#7 <<AB><CD>> does not involve breaking apart Task#5 or #6, we proceed.
	* Now, list2tasks = 7 7 7 7.
	* This ContiguityTree legitimately represents all of the contiguous groups. 
	* However, when we create Task #7, the OrderedGroup is smart enough to know it should remove unnecessary children (other OrderedGroups).
	* Thus, Task#7 = <ABCD>. Task#5 and Task #6 have been absorbed by #7, as the information they represented is encoded in Task#7.
	* 
	* The final Contiguity Tree is an Ordered Group <ABCD>.
	*/
	private void processContiguousGroup(int start, int end) {
		
		ArrayList<Task> tasksInL2Order = new ArrayList<Task>();
		ArrayList<Integer> L1IndexOfTasksInL2Order = new ArrayList<Integer>();
		
		/*As described above, when building a Task, we must ensure that we are not breaking up any existing tasks
		* While we do this, we can go ahead and find out if the task is Ordered(reversible?) or Unordered
		* We accomplish this by tracking the tasks we would like to combine into a new task
		* 	and by tracking the index of these tasks with respect to list1
		* 	For example, if the tasks (when traversed w respect to list2) have decreasing indices with respect to list1,
		* 		then we know the tasks are in the opposite order 
		*/
		
		//check first edge to ensure we are not breaking apart a pre-existing task
		if (!list2tasks[start].equals(list2tasks[start+list2tasks[start].absoluteSize()-1])) return;
		
		//record each subtask and their index with respect to list 1
		int k =start;
		while(k<=end){
			tasksInL2Order.add(list2tasks[k]);
			L1IndexOfTasksInL2Order.add(min[0][k]);
			k+=list2tasks[k].absoluteSize();
		}
		
		//check last edge to ensure we are not breaking apart a pre-existing task
		if (k!=end+1) return;
		
		//determine the GroupType based on L1IndexOfTasksInL2Order's numeric ordering
		int direction = determineDirection(L1IndexOfTasksInL2Order);
		
		buildNode(start, end, tasksInL2Order, direction);
	}
	
	//return 1 if the tasks are in the same order
	//return -1 if in opposite order (reversed)
	//return 0 if scrambled
	private int determineDirection(ArrayList<Integer> order){
		int initialDirection = order.get(1)-order.get(0);
		for (int i=order.size()-1; i>=2; i--){
			int tempDirection = order.get(i)-order.get(i-1);
			if (initialDirection*tempDirection<0) return 0; 
		}
		return (int) Math.signum(initialDirection);
	}
	
	private void buildNode(int start, int end, ArrayList<Task> tasksInL2Order, int direction){
		Task node;
		if (direction==1){ //increasing task indices. ex: 3, 4, 7, 11
			node = new OrderedGroup(new Label(), null, tasksInL2Order, false); //false bc task order is not reversible
		}
		else if (direction==-1){  //decreasing task indices. ex: 11, 7, 4, 3
			node = new OrderedGroup(new Label(), null, tasksInL2Order, true); //true bc task order is reversible
		}
		else if (direction==0){  //fluctuating task indices. ex: 11, 4, 7, 3
			node = new UnOrderedGroup(new Label(), null, tasksInL2Order);
		}
		else {throw new Error("Unsupported direction passed into buildNode()");}
		
		for (int i=start; i<=end; i++) {
			list2tasks[i]= node;
		}
	}
}
