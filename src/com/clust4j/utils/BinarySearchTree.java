package com.clust4j.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BinarySearchTree<T extends Comparable<? super T>> implements java.io.Serializable {
	private static final long serialVersionUID = -478846685921837986L;
	
	/**
	 * Generate a new, default comparator
	 */
	private final static <T extends Comparable<? super T>> Comparator<T> defaultComparator() {
		return new Comparator<T>() {
			@Override public int compare(T o1, T o2) {
				return o1.compareTo(o2);
			}
		};
	}
	
	/**
	 * The comparator used for sorting, assigning left/right
	 */
	transient private Comparator<T> comparator;

	/**
	 * Stores the current root of the BST
	 */
	private BSTNode<T> root = null;
	
	
	
	
	/* ---------------------- CONSTRUCTORS ---------------------- */
	@SuppressWarnings("unchecked") public BinarySearchTree() {
		this((Comparator<T>) defaultComparator());
	}
	
	public BinarySearchTree(final Comparator<T> comparator) {
		this.comparator = comparator;
	}
	
	@SuppressWarnings("unchecked") public BinarySearchTree(T root) {
		this((Comparator<T>) defaultComparator(), root);
	}
	
	public BinarySearchTree(final Comparator<T> comparator, final T root) {
		this.root = new BSTNode<>(root, comparator);
		this.comparator = comparator;
	}
	
	
	

	
	/* ---------------------- METHODS ---------------------- */
	public void add(T t) {
		if(null == root)
			root = new BSTNode<>(t, comparator);
		else root.add(t);
		
		balance();
	}
	
	public void addAll(Collection<T> values) {
		List<T> vals;
		if(values instanceof List)
			vals = (List<T>)values;
		else vals = new ArrayList<T>(values);
		
		if(root != null)
			vals.addAll(root.values());
		
		balance(vals);
	}
	
	private void balance() {
		ArrayList<T> values = (ArrayList<T>) values();
		Collections.sort(values, comparator);
		
		// Now re-insert
		root = null;
		balanceRecursive(0, values.size(), values);
	}
	
	/**
	 * Rebalances the tree after a removal operation
	 * or after a batch add operation
	 * @param values
	 */
	private void balance(final List<T> values) {
		Collections.sort(values, comparator);
		
		// Re-insert
		root = null;
		balanceRecursive(0, values.size(), values);
	}
	
	private void balanceRecursive(int low, int high, List<T> coll){
	    if(low == high)
	        return;

	    int midpoint = (low + high)/2;
	    T insert = coll.get(midpoint);
	    
	    if(null == root)
	    	root = new BSTNode<>(insert, comparator);
	    else root.add(insert);

	    balanceRecursive(midpoint+1, high, coll);
	    balanceRecursive(low, midpoint, coll);  
	}
	
	public BSTNode<T> locate(T value) {
		return null == root ? null : root.locate(value);
	}
	
	/**
	 * Removes the first node whose value's compareTo()
	 * method equals zero with the provided value.
	 * @param value
	 * @return true if found and removed from tree
	 */
	public boolean remove(T value) {
		if(null == root)
			return false;
		
		ArrayList<T> values = (ArrayList<T>) values();
		final boolean b = values.remove(value);
		
		if(b)
			balance(values);
		return b;
	}
	
	public boolean removeAll(Collection<T> remove) {
		if(null == root)
			return false;
		
		ArrayList<T> values = (ArrayList<T>) root.values();
		final boolean b = values.removeAll(remove);
		
		if(b)
			balance(values);
		
		return b;
	}
	
	public int size() {
		return null == root ? 0 : root.size();
	}
	
	public BSTNode<T> root() {
		return root;
	}
	
	public Collection<T> values() {
		return null == root ? null : root.values();
	}
	
	
	
	
	
	
	/**
	 * Implements nearly all tree traversal logic. Since it 
	 * contains very sensitive references to potentially mutable
	 * datastructures, the decision was made to hide visibility
	 * to all but getter methods in this innerclass, and only allow access 
	 * to the values themselves. This is especially important when considered that a 
	 * tree balancing operation will likely reassign the root as a new
	 * BSTNode by value, likely destroying the integrety to a stored
	 * reference to BSTNode. This could cause unforeseen NullPointerExceptions
	 * on the client side.
	 * 
	 * @author Taylor G Smith
	 * 
	 * @param <T>
	 */
	final public static class BSTNode<T extends Comparable<? super T>> implements java.io.Serializable {
		private static final long serialVersionUID = -4243106326564743392L;
		
		private T value = null;
		private BSTNode<T> right = null;
		private BSTNode<T> left = null;
		
		transient private Comparator<T> comparator;
		
		private BSTNode(T t, final Comparator<T> comparator) {
			value = t;
			this.comparator = comparator;
		}
		
		private void add(T t) {
			addRecurse(this, t);
		}
		
		private static <T extends Comparable<? super T>> boolean addRecurse(BSTNode<T> root, T value) {
			final boolean left = root.goesLeft(value);
			if(left && !root.hasLeft()) {
				root.left = new BSTNode<>(value, root.comparator);
				return left;
			} else if(!left && !root.hasRight()) {
				root.right = new BSTNode<>(value, root.comparator);
				return left;
			}
			
			return addRecurse(left ? root.left : root.right, value);
		}
		
		public T getValue() {
			return value;
		}
		
		private boolean goesLeft(T incomingValue) {
			return comparator.compare(incomingValue, value) < 0;
		}
		
		public boolean hasLeft() {
			return left != null;
		}
		
		public boolean hasRight() {
			return right != null;
		}
		
		public BSTNode<T> leftChild() {
			return left;
		}
		
		/**
		 * Prunes all children from this node
		 */
		public void prune() {
			left = null;
			right = null;
		}
		
		public BSTNode<T> rightChild() {
			return right;
		}
		
		private BSTNode<T> locate(T value) {
			final int comparison = this.value.compareTo(value);
			if(comparison == 0) // this is the first node to have the value
				return this;
			if(comparison == -1)
				return hasLeft() ? left.locate(value) : null;
			return hasRight() ? right.locate(value) : null;
		}
		
		public int size() {
			int this_size = 1;
			if(hasLeft())
				this_size += left.size();
			if(hasRight())
				this_size += right.size();
			return this_size;
		}
		
		/**
		 * Collects the values of the tree in pre-order
		 * @return
		 */
		public Collection<T> values() {
			final ArrayList<T> values = new ArrayList<T>();
			return valuesRecurse(this, values);
		}
		
		private final static <T extends Comparable<? super T>> Collection<T> valuesRecurse(
				final BSTNode<T> root, 
				final Collection<T> coll) 
		{
			coll.add(root.value);
			if(root.hasLeft())
				valuesRecurse(root.left, coll);
			if(root.hasRight())
				valuesRecurse(root.right, coll);
			return coll;
		}
		
	}// End BSTNode
}
