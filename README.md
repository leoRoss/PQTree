# PQ Tree
https://en.wikipedia.org/wiki/PQ_tree

A PQ Tree represents a family of permutations on a set of elements.
This PQ Tree implementation incrementally learns/computes/builds a PQ Tree from a series of example permutations.

As new permutations are presented to the PQ Tree, it will modify itself (if necessary) to ensure:
1. the new permutation is within the tree's family of permutations
2. the tree's family of permutations stays as small/strict as possible

At any given time, the PQ Tree will represent the strictest family of permutations to which all previously seen permutations belong.
In other words, the PQ Tree will encode all grouping constraints that have yet to be violated by an example permutations.

## Input
Permutations of the same set of unique objects.
NOTE: "same" and "unique" are defined by .equals() method

This implementation requires that each permutation contains all the elements in the set.

Some PQ-Tree implementations allow the client to specify a permutation which only contains a subset of the elements.
This implementation does not.

## Result
A PQ Tree keeps track of all the groups of elements (or groups of groups) which always appear contiguously in the permutations.
It compresses grouping constraints to ensure the tree has the least amount of nodes possible. It accomplishes this compression by using two types of nodes:
- Unordered: A group of contiguous elements that can exist in any order
    - An unordered group encodes a single grouping constraint
- Ordered: A group of elements that always exist sequentially
    - An ordered group encodes multiple grouping constraints
    - For example, if abcd exist in an ordered group, we have the following 6 grouping requirements: ab, bc, cd, abc, bcd, abcd

NOTE: This implementation distinguishes 2 types of Ordered nodes:
- Sequential: The elements always appear in the same order
- Reversible: The elements always appear in the same (or the exact opposite) order

## Simple Example

    Permutation 1 = A B C D E
    Permutation 2 = E B A C D

    The CT knows that so far...
        A and B are always contiguous, but can be in any order: {AB}
        C and D always appear in the same order: [CD]
        Moreover, {AB} and [CD] always appear in the same order: [ {AB}[CD] ]
        Finally, [ {AB}[CD] ] and E are always contiguous in any order: {  [ {AB}[CD] ]  E  }

    Permutation 3 =  A C B E D
        {AB} has been broken apart by C
        [CD] has been broken apart by B and E
        [ {AB}[CD] ] has been broken apart by E
        However, we still have some grouping constraints!
        Notice that A, B, and C all always contiguous, by appear in any order: {ABC}
        We end up with the following Tree: {{ABC} D E }
        
## Implementation Overview
The new permutation is transformed into the new PQ Tree. The previous PQ Tree encodes all of its grouping constraints onto the new permutation (unless the new permutation violates the constraint). During this process, the permutation will be transformed into a list of mini PQ-Trees. At the end of the process, all these trees will be unified into a single PQ Tree, which becomes the new tree.

This is accomplished bottom-up using a labeling system.

### Given
All nodes in the current PQ-Tree has a unique Label.

### Leaf Nodes
Each leaf node in the PQ-Tree will find its corresponding element in the new permutation (using object comparison .equals). It will copy its label onto the matching element.

From here on, we do not need to compare the actual objects in the permutations. We can simply use the labeling system.

### Inner Nodes
As we work our way up the tree, the nodes will need to find all their subNodes in the new permutation. Let us call the current node N. 

Given: All of N's subNodes have unique Labels.
Since we work bottom-up, all of N's subNodes have already finished labeling their correspoding elements in the new permutation. Thus, N can search the new permutation for nodes with the same labels as its subNodes.

#### Contiguous Subnodes
If all of N's subNodes are contiguous in the permutation, N will be able to group these subNodes together under a new node. It will first decide what type of node should be created. For example, if N was a Sequential node and its subNodes appear in the reverse order, it would simply create a Reversible node. If N was an Unordered node, it would re-group the subNodes in another Unordered ndoes.

However, sometimes a few subsets of the subnodes are still ordered. N must create a multi-level node:

    N:             Sequential [A B C J K L M X Y Z]
    New Permutation: ......    B A C L J M K X Y Z ..... other unrelated nodes ....
    
    New Node: .... Sequential [ Reversible{B A} C Unordered {L J M K} X Y Z ] ....
    
The resulting top-level node will be labeled the same as N. This will allow N's parent node, P, to easily find N in the new permutation once it is P's turn to incorparte itself into the permutation.

#### Fragmented Subnodes
If all the subNodes are NOT contiguous in the permutation, the node will not be able to re-build itself within the permutation. However, it must attempt to encode as much of its information as possible into the permutation. It accomplishes this by identifying subsets of its subNodes that can still be grouped together.
    
    N:    Sequential [A B C J K L M X Y Z]
    Permutation: .... B A C  ... L J M K ...  X Y ... Z ...
    
    New Nodes: ..... Sequential [ Reversible{B A} C ] ...  Unordered (L J M K) .... Sequential[X Y] .... Z ....

Each of the fragements (including the lone Z node) will be given a "Piece Label". A Piece Label signifies that these nodes are fragments. A Piece Label records the association with N's original label. 

    N:     Sequential [A B C  Reversible{D E F} V X Z]
    Permutation: ......    B A C E D V F X Z Y .....
    
    After the Reversible encorporates itself:
    Permutation: ......    B A C !Reversible{E D} V !F X Z .....
    
    If we pretend the Reversible was labeled R, we can re-write this as:
    N:    Sequential [A B C  R  V X Z] ... where R = Reversible{D E F}
    Permutation: .....     B A C R1 V R2 X Z ..... where R1 = Reversible{E D} and R2 = F, both fragements pieces of R
    
    Now, N will see that all of its subNodes (all of their pieces, if they were fragmented) are contiguous in the new permutation.
    N can regroup them all and deal with R's fragements:
    
    Sequential [ Reversible{B A} C  Unordered(R1 V R2) X Z]
    
    Replacing R1 and R2 labels with their underlying Nodes, we get:
    Sequential [ Reversible{B A} C  Unordered(Reversible{E D} V F) X Z]
    
The full logic is contained within the Incorporators: UnOrderedIncorporator and OrderedIncorporator.
    
#### Top Level Root Node
The root node is guarnteed to have all of its subNodes (or their pieces) appear contiguously in the new permutation. Thus, it will be able to group them into a single node. The new PQ-Tree has now been created.

## Disclaimer
This project does not reflect my professional coding standards.
