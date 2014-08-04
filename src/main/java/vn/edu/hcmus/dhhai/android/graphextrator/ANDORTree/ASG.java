package vn.edu.hcmus.dhhai.android.graphextrator.ANDORTree;

import java.util.*;

public class ASG {

	public static <T> Set<Set<SwapOption<T>>> runASG(Node<T> alternatingTree) {

		Set<SwapOption<T>> optimalSig = new LinkedHashSet<SwapOption<T>>(0);

		// open set acts as a queue for BFS of solutions
		Set<Set<SwapOption<T>>> open = new LinkedHashSet<Set<SwapOption<T>>>();

		// closed set marks the visited solutions
		Set<Set<SwapOption<T>>> closed = new LinkedHashSet<Set<SwapOption<T>>>();

		// each solution is represented by a signature, which is an ordered set
		// of swap options (implemented using LinkedHashSet)
		open.add(optimalSig);

		Set<SwapOption<T>> currentSig;
		Iterator<Set<SwapOption<T>>> openIterator;

		while ((openIterator = open.iterator()) != null && openIterator.hasNext()) {

			currentSig = openIterator.next();
			openIterator.remove();

			closed.add(currentSig);

			List<SwapOption<T>> swapList 
					= ASG.computeSwapListPossibleDuplicate(alternatingTree, currentSig);

			if (swapList != null) {
				for (SwapOption swapOpt : swapList) {
					Set<SwapOption<T>> nextSolutionSig =
						new LinkedHashSet<SwapOption<T>>(currentSig);

					nextSolutionSig.add(swapOpt);

					if (!open.contains(nextSolutionSig) 
						&& !closed.contains(nextSolutionSig)) {
						open.add(nextSolutionSig);
					}
				}	
			}
		}

		return closed;
	}

	public static <T> List<SwapOption<T>> computeSwapList(
					Node<T> alternatingTree,
					Set<SwapOption<T>> curSolutionSignature) {
		
		List<SwapOption<T>> swapList;

		List<SwapOption<T>> optimalSolutionSwapList
			= alternatingTree.getOptimalSolutionSwapList();

		
		if (curSolutionSignature.isEmpty()) {
			// base case: SwapList for optimal solution
			return optimalSolutionSwapList;
		}
		// non-optimal solution
		else {
			
			/* Strategy for generating swap list from the current solution
				 (which is in form of a signature)
				
				SwapSet = {}
				Foreach SwapOption s_i in current SolutionSignature
					SwapList.add (s_j | (s_i, s_j) in relation R)

				SwapList = SwapSet \ curSolutionSignature 
					where \ is the set complement operation

				relation R:
				(1) (s_qi, s_rj) in R, if there is a path from v_i to v_r
				(2) (s_pq, s_rt) in R, if v_q = v_r, i.e. next swap option

				and there should be no 2 s_ij and s_st in SwapList that
					there is a path from v_s -> v_i

				for each swap option s_i, s_i can be the next swap option for
				solution S if:
					- s_i is not already applied in S
					- s_i does not invalidate any s_j in S
						s_i invalidates s_j in S if
							* s_i is a swap option in the subtree of s_j origin
							* s_j is a swap option in the subtree of s_i origin
			*/


			swapList = new ArrayList<SwapOption<T>>();
			Set<SwapOption<T>> swapSet 
				= new HashSet<SwapOption<T>>();
			
			// check the swap options available in the optimal solution
			for (SwapOption<T> candidateSwapOpt : optimalSolutionSwapList) {
				if (!curSolutionSignature.contains(candidateSwapOpt)) {
					// s_i is not already applied in S

					// forall s_j in S, s_j is not a swap option of the subtree
					// of s_i origin
					Set<SwapOption<T>> originNodeAllSwapOpts
						= new HashSet<SwapOption<T>>(candidateSwapOpt.getSourceNode().getAllSubSwapOptions());

					originNodeAllSwapOpts.retainAll(curSolutionSignature);

					if (originNodeAllSwapOpts.isEmpty()) {
						// forall s_j in S, s_i is not a swap option of the subtree
						// of s_j origin
						boolean isTrue = true;
						for (SwapOption<T> swapOpt : curSolutionSignature) {
							if (swapOpt
									.getSourceNode()
									.getAllSubSwapOptions()
									.contains(candidateSwapOpt)) {
								isTrue = false;
								break;
							}
						}

						if (isTrue) {
							swapSet.add(candidateSwapOpt);
						}
					}
				}
			}

			for (SwapOption<T> swapOpt : curSolutionSignature) {

				SwapOption<T> nextSwapOpt = swapOpt.getNextSwapOption();

				if (nextSwapOpt != null && curSolutionSignature.contains(nextSwapOpt))
					continue;

				// swap options that are enabled by this swapOpt
				// next swap options by definion (1)
				swapSet.addAll(swapOpt.getDestinationNode().getOptimalSolutionSwapList());

				// next swap option by definion (2)
				if (nextSwapOpt != null) {
					Set<SwapOption<T>> originNodeAllSwapOpts
						= new HashSet<SwapOption<T>>(nextSwapOpt.getSourceNode().getAllSubSwapOptions());

					originNodeAllSwapOpts.retainAll(curSolutionSignature);

					if (originNodeAllSwapOpts.isEmpty()) {

						swapSet.add(nextSwapOpt);
					}
				}
					
				// swap options that are disabled by this swapOpt
				swapSet.removeAll(swapOpt.getSourceNode().getAllSubSwapOptions());
			}

			swapList.addAll(swapSet);

			return swapList;
		}
	}

	public static <T> List<SwapOption<T>> computeSwapListPossibleDuplicate(
					Node<T> alternatingTree,
					Set<SwapOption<T>> curSolutionSignature) {
		
		List<SwapOption<T>> swapList;

		List<SwapOption<T>> optimalSolutionSwapList
			= alternatingTree.getOptimalSolutionSwapList();

		
		if (curSolutionSignature.isEmpty()) {
			// base case: SwapList for optimal solution
			return optimalSolutionSwapList;
		}
		// non-optimal solution
		else {

			swapList = new ArrayList<SwapOption<T>>();
			Set<SwapOption<T>> swapSet 
				= new HashSet<SwapOption<T>>(optimalSolutionSwapList);
			

			for (SwapOption<T> swapOpt : curSolutionSignature) {

				SwapOption<T> nextSwapOpt = swapOpt.getNextSwapOption();

				if (nextSwapOpt != null && curSolutionSignature.contains(nextSwapOpt))
					continue;

				// swap options that are enabled by this swapOpt
				// next swap options by definion (1)
				swapSet.addAll(swapOpt.getDestinationNode().getOptimalSolutionSwapList());

				// next swap option by definion (2)
				if (nextSwapOpt != null)
					swapSet.add(nextSwapOpt);

				// swap options that are disabled by this swapOpt
				swapSet.removeAll(swapOpt.getSourceNode().getAllSubSwapOptions());
			}

			swapList.addAll(swapSet);

			return swapList;
		}
	}
}