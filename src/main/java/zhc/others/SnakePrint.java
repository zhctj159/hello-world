package zhc.others;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;

public class SnakePrint {
	/** 分层标记 */
	private static BtreeNode xx = new BtreeNode(Integer.MAX_VALUE);
	/** 树根 */
	private static BtreeNode root;
	static {
		BtreeNode t01,t02,t11,t12;
		t01 = new BtreeNode(5);
		t02 = new BtreeNode(7);
		t11 = new BtreeNode(6);
		t11.left = t01;
		t11.right = t02;
		t01 = new BtreeNode(9);
		t02 = new BtreeNode(11);
		t12 = new BtreeNode(10);
		t12.left = t01;
		t12.right = t02;
		root = new BtreeNode(8);
		root.left = t11;
		root.right = t12;
	}
	
	public static void main(String[] args) {
//		print();
		quickSlowList();
	}
	
	
	
	public static void print() {
		MyQueue<BtreeNode> nodes = new MyQueue<BtreeNode>(new BtreeNode[1024]);
		MyStack<BtreeNode> stack = new MyStack<BtreeNode>(new BtreeNode[1024]);
		nodes.add(root);
		nodes.add(xx);
		boolean flag = true;
		while (!nodes.isEmpty()) {
			BtreeNode n = nodes.poll();
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
		Queue<BtreeNode> nodes = new LinkedBlockingQueue<BtreeNode>();
		Queue<BtreeNode> nodes2 = new LinkedBlockingQueue<BtreeNode>();
		Stack<BtreeNode> stack = new Stack<BtreeNode>();
		nodes.add(root);
		nodes.add(xx);
		boolean flag = true;
		while (!nodes.isEmpty()) {
			BtreeNode n = nodes.poll();
			if (flag) {
				nodes2.add(n);
			} else {
				stack.push(n);
			}
			if (n==xx) {
				if (!flag && !stack.empty()) {
					while (!flag && !stack.empty()) {
						BtreeNode nn = stack.pop();
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
			BtreeNode n = nodes2.poll();
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
	
	static class BtreeNode {
		BtreeNode left;
		BtreeNode right;
		int val;
		public BtreeNode(int val) {
			this.val = val;
		}
		
		@Override
		public String toString() {
			return super.toString()+"_"+val;
		}
	}

	/** 快慢表 */
	public static void quickSlowList() {
//		int[] arr = {0,4,517,6,519,520,8,523,526,529,21,533,22,534,27,540,543,547,554,46,49,50,562,51,564,53,565,54,56,569,60,61,573,574,64,579,580,69,582,584,73,586,75,587,589,592,80,82,86,600,89,90,602,608,610,100,102,615,104,616,617,106,108,620,621,622,110,115,628,116,629,631,632,122,127,130,644,646,134,135,648,649,651,143,658,659,154,156,670,671,672,162,163,681,682,683,685,686,176,693,183,695,699,700,701,702,705,195,718,206,207,723,725,728,218,733,741,232,233,234,236,750,240,761,763,765,255,768,259,262,264,778,780,270,784,273,275,276,277,279,282,794,797,285,288,289,802,291,298,810,299,301,820,309,821,828,320,323,324,838,841,331,844,333,335,336,849,339,851,854,343,857,860,348,862,866,355,357,869,870,871,872,363,364,876,878,369,881,883,886,374,889,891,893,894,895,896,387,899,901,392,393,907,399,913,403,919,408,920,410,413,925,927,416,420,932,933,421,425,941,950,439,953,443,956,445,958,448,449,450,962,963,968,457,460,975,463,976,977,465,466,468,981,470,471,472,984,473,989,478,479,992,993,486,487,999,492,497,499,507,509};
		int[] vals = {0,4,6,8,21,22,27,46,49,50,51,53,54,56,60,61,64,69,73,75,80,82,86,89,90,100,102,104,106,108,110,115,116,122,127,130,134,135,143,154,156,162,163,176,183,195,206,207,218,232,233,234,236,240,255,259,262,264,270,273,275,276,277,279,282,285,288,289,291,298,299,301,309,320,323,324,331,333,335,336,339,343,348,355,357,363,364,369,374,387,392,393,399,403,408,410,413,416,420,421,425,439,443,445,448,449,450,457,460,463,465,466,468,470,471,472,473,478,479,486,487,492,497,499,507,509,517,519,520,523,526,529,533,534,540,543,547,554,562,564,565,569,573,574,579,580,582,584,586,587,589,592,600,602,608,610,615,616,617,620,621,622,628,629,631,632,644,646,648,649,651,658,659,670,671,672,681,682,683,685,686,693,695,699,700,701,702,705,718,723,725,728,733,741,750,761,763,765,768,778,780,784,794,797,802,810,820,821,828,838,841,844,849,851,854,857,860,862,866,869,870,871,872,876,878,881,883,886,889,891,893,894,895,896,899,901,907,913,919,920,925,927,932,933,941,950,953,956,958,962,963,968,975,976,977,981,984,989,992,993,999};
		ListNode head = new ListNode(Integer.MIN_VALUE);
		ListNode tail = new ListNode(Integer.MAX_VALUE);
		ListNode p = null;
		p = head;
		for (int i = 0; i < vals.length; i++) {
			p.next[0] = new ListNode(vals[i]);
			p = p.next[0];
		}
		p.next[0] = tail;
		
		for (int i = 0; i < 3; i++) {
			p = head;
			while (p.next[i]!=null && p.next[i].next[i]!=null && p.next[i].next[i]!=tail) {
				p.next[i+1] = p.next[i].next[i];
				p = p.next[i+1];
			}
			System.out.println("xxxx");
			p.next[i+1] = tail;
		}
		//查找100
		p = head;
		int aa = 993;
		int count = 0;
		for (int i = 3; i >= 0; i--) {
			while (null!=p.next[i] && p.next[i].val < aa ) {
				p = p.next[i];
				count++;
			}
			System.out.println("count = "+count);
//			System.out.println(p.next[i].val);
//			if (p.next[i].val==aa) {
//				break;
//			}
		}
		System.out.println("count = "+count);
	}
	
	static class ListNode {
		ListNode[] next = new ListNode[10];
		int val;
		
		public ListNode(int val) {
			this.val = val;
		}
	}
}
