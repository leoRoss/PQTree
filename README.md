#ContiguityTree


##Input

A Contiguity Tree (CT) takes as input demonstrations, one at a time.

Demonstation: a sequence of unique actions (Objects).

Limitation: all of the demonstrations must include the same set objects



##Result

A Contiguity Tree keeps track of all the groups of Objects (or groups of groups of Objects) which always appear contiguously in the demos

EX:
      
      Demo 1 = A B C D E
      
      Demo 2 = E B A C D
      
      The CT knows that so far...
      
            A and B are always contiguous (next to each other), but can be in any order: {AB}
      
            C and D always appear in the same order: [CD]
      
            Moreover, {AB} and [CD] always appear in the same order: [ {AB}[CD] ]
      
            Finally, [ {AB}[CD] ] and E are always contiguous: {  [ {AB}[CD] ]  E  }

Note: This is all done dynamically!

Visit my [website](http://www.lrossignacmilon.com/projects/ContiguityTree/main.php) for further explanation and examples.


Interested in the implementation?
Starting by reading my webpage above, then read ContiguityTree.java, glance at Group.java, read UnorderedIncorporator.java and finally read OrderedIncorporator.java (the most complex process)
