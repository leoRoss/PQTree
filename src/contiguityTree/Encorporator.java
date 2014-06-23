package contiguityTree;

import java.util.List;
import java.util.SortedSet;

public class Encorporator {
	
	//We need to know the demo to change, the subTasks to find, ordered?, reversible?
	public static List<Task> encorporate (List<Task> demo, Group group) {
		//TODO: well... encorporate!
		SortedSet<Piece> peices = Splitter.split(demo, group);
		
		group.setNumberOfPeices(peices.size());
		
		if (peices.size()>1) {
			for (Piece peice : peices) {
				Task subTask = Resolver.resolve(peice);
				demo = ToolBox.replaceInDemo(demo, subTask, peice);
				subTask.setLabel(new PieceLabel(group.getLabel()));
			}
		}
		
		else {
			demo = ToolBox.replaceInDemo(demo, subTask, peices[0]);
			subTask.setLabel(group.getLabel());
		}
		
		return demo;
	}
	
	
	
	
	private class Splitter {

	}
	
	private class Resolver {

	}
	
	private class Piece {
		int start, end;
		//ordered so largest start is first in sorted set
		//allows for indecies to remain valid as we edit demo from end to start
	}
	
	private class ToolBox {
		
	}
}
