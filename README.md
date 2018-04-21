# PQ Tree
I accidentally re-invented the PQ-Tree:
https://en.wikipedia.org/wiki/PQ_tree

This implementation incrementally computes a PQ Tree.

## Input
Permutations of the same set of unique objects.
NOTE: "same" and "unique" are defined by .equals() method

This implementation requires that each permutation contains all the elements in the set.

Some PQ-Tree implementations allow the client to specify a permutation which only contains a subset of the elements.
This implementation does not.

## Result
A PQ Tree keeps track of all the groups of elements (or groups of groups) which always appear contiguously in the permutations.
It compresses grouping constraints to ensure the tree has the least amount of nodes possible. It accomplishes this compression by using two types of nodes:
- Unordered: A group of contiguous elements that can exist in any order.
- An unordered group encodes a single grouping constraint.
- Ordered: A group of elements that always exist sequentially
    - An ordered group encodes multiple grouping constraints.
    - For example, if abcd exist in an ordered group, we have the following 6 grouping requirements: ab, bc, cd, abc, bcd, abcd

NOTE: This implementation distinguishes 2 types of Ordered nodes:
- Sequential: The elements always appear in the same order
- Reversible: The elements always appear in the same (or the exact opposite) order

## Implementation
The new permutation is transformed into the new PQ Tree.

This is accomplished using a "smart label" system.
Each Node is responsible for finding its subNodes in the permutation. If all its subNodes are contiguous in the permutation, it can create a new Node with its same label. If not, it will identify any contiguous subsets of its subNodes and group those elements together if appropriate. It will then apply "smart labels" to all of these new groups and to all the lone subNodes that were not re-grouped. The parent Node must then deal with all these pieces and their labels.

The complex logic is contained within the Incorporators: UnOrderedIncorporator and OrderedIncorporator.

## Example

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

## Disclaimer
This is a old college project and does not reflect my professional coding standards.
