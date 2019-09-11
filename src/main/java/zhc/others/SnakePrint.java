package zhc.others;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class SnakePrint {
	/** 分层标记 */
	private static BTreeNode xx = new BTreeNode(Integer.MAX_VALUE);
	/** 树根 */
	private static BTreeNode root;
	static {
		BTreeNode t01,t02,t11,t12;
		t01 = new BTreeNode(5);
		t02 = new BTreeNode(7);
		t11 = new BTreeNode(6);
		t11.left = t01;
		t11.right = t02;
		t01 = new BTreeNode(9);
		t02 = new BTreeNode(11);
		t12 = new BTreeNode(10);
		t12.left = t01;
		t12.right = t02;
		root = new BTreeNode(8);
		root.left = t11;
		root.right = t12;
	}
	
	public static void main(String[] args) {
		print();
	}
	
	public static void print() {
		MyQueue<BTreeNode> nodes = new MyQueue<BTreeNode>(new BTreeNode[1024]);
		MyStack<BTreeNode> stack = new MyStack<BTreeNode>(new BTreeNode[1024]);
		nodes.add(root);
		nodes.add(xx);
		boolean flag = true;
		while (!nodes.isEmpty()) {
			BTreeNode n = nodes.poll();
			if (n==xx) {
				while (!flag && !stack.isEmpty()) {
					System.out.print(stack.pop().val+" ");
				}
				
				if (!nodes.isEmpty()) {
					System.out.println();
					nodes.add(xx);
					flag = !flag;
				}
			} else {
				if (flag) {
					System.out.print(n.val+" ");	//flag为true直接顺序打印
				} else {
					stack.push(n);					//flag为false的入栈待遇到换行标志再出栈打印
				}
				if (null!=n.left) {		//左孩子入栈
					nodes.add(n.left);
				}
				if (null!=n.right) {	//右孩子入栈
					nodes.add(n.right);
				}
			}
		}
	}
	
	public static void print1() {
		Queue<BTreeNode> nodes = new LinkedBlockingQueue<BTreeNode>();
		Queue<BTreeNode> nodes2 = new LinkedBlockingQueue<BTreeNode>();
		Stack<BTreeNode> stack = new Stack<BTreeNode>();
		nodes.add(root);
		nodes.add(xx);
		boolean flag = true;
		while (!nodes.isEmpty()) {
			BTreeNode n = nodes.poll();
			if (flag) {
				nodes2.add(n);
			} else {
				stack.push(n);
			}
			if (n==xx) {
				if (!flag && !stack.empty()) {
					while (!flag && !stack.empty()) {
						BTreeNode nn = stack.pop();
						if (nn!=xx) {
							nodes2.add(nn);
						}
					}
					nodes2.add(xx);
				}
				
				if (!nodes.isEmpty()) {
					nodes.add(xx);
					flag = !flag;
				}
				continue;
			}
			if (null!=n.left) {
				nodes.add(n.left);
			}
			if (null!=n.right) {
				nodes.add(n.right);
			}
		}
		while (!nodes2.isEmpty()) {
			BTreeNode n = nodes2.poll();
			if (n==xx) {
				System.out.println();
				continue;
			}
			System.out.print(n.val+" ");
		}
	}
	
	static class MyQueue<T> {
		T[] q;
		int head = 0;
		int tail = 0;
		int capacity = 1024;
		
		public MyQueue(T[] q) {
			if (null==q || q.length<1024) {
				throw new RuntimeException("queue capacity must not low with 1024");
			}
			this.q = q;
			this.capacity = q.length;
		}
		
		public void add(T obj) {
			push(obj);
		}
		
		public void push(T obj) {
			if (isFull()) {
				throw new RuntimeException("queue is full");
			}
			q[tail] = obj;
			tail = (tail+1)%capacity;
		}
		
		public T poll() {
			if (isEmpty()) {
				throw new RuntimeException("queue is empty");
			}
			T t = q[head];
			head = (head+1)%capacity;
			return t;
		}
		
		public boolean isEmpty() {
			return tail==head;
		}
		
		public boolean isFull() {
			return (tail+1)%capacity == head;
		}
	}
	
	static class MyStack<T> {
		T[] s;
		/** 栈顶 */
		int pp = 0;
		int capacity = 1024;
		public MyStack(T[] s) {
			if (null==s || s.length<1024) {
				throw new RuntimeException("stack capacity must not low with 1024");
			}
			this.s = s;
			this.capacity = s.length;
		}
		
		public void push(T obj) {
			if (isFull()) {
				throw new RuntimeException("stack is full");
			}
			s[pp++] = obj;
		}
		
		public T pop() {
			if (isEmpty()) {
				throw new RuntimeException("stack is empty");
			}
			return s[--pp];
		}
		
		public boolean isEmpty() {
			return pp==0;
		}
		
		public boolean isFull() {
			return pp==capacity;
		}
	}
	
	static class BTreeNode {
		BTreeNode left;
		BTreeNode right;
		int val;
		public BTreeNode(int val) {
			this.val = val;
		}
		
		@Override
		public String toString() {
			return super.toString()+"_"+val;
		}
	}
}
