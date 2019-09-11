package zhc.others;

public class SortTest2 {
	
	public static void main(String[] args) {
		int[] a = {6,2,4,3,7,1,9};
		print(a);
//		quickSort(a, 0, a.length-1);
//		selectSort(a);
//		insertSort(a);
		insertSort2(a);
		print(a);
		
//		int[] b = {1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20};
//		binFind(b, 0, b.length-1, 13);
	}
	
	
	public static boolean binFind(int[] a, int s, int e, int x) {
		if (s>e) {
//			System.out.println("NO");
			return false;
		}
		int mid = (s+e)/2;
		if (a[mid]>x) {
//			System.out.printf("binFind(a,%d,%d,%d)\n",s,(mid-1),x);
			return binFind(a, s, mid-1, x);
		} else if (a[mid]<x) {
//			System.out.printf("binFind(a,%d,%d,%d)\n",(mid+1),e,x);
			return binFind(a, mid+1, e, x);
		} else {
//			System.out.println("OK: "+mid);
			return true;
		}
	}
	
	//二分插入排序
	public static void insertSort2(int[] a) {
		for (int i = 1; i < a.length; i++) {
			int t = a[i];
			int left = 0;
			int right = i-1;
			while (left<=right) {
				int mid = (left+right)/2;
				if (t<a[mid]) {
					right = mid-1;
				} else {
					left = mid+1;
				}
			}
			for (int j = i-1; j >= left; j--) {
				a[j+1] = a[j];
			}
			if (left!=i) {
				a[left] = t;
			}
		}
	}
	
	//简单插入排序
	public static void insertSort(int[] a) {
		for (int i = 1; i < a.length; i++) {
			int t = a[i];
			int k = i;
			for (int j = i-1; j >= 0; j--) {
				if (a[j]>t) {
					a[j+1] = a[j];
					k = j;
				} else {
					break;
				}
			}
			a[k] = t;
		}
	}
	
	public static void selectSort(int[] a) {
		for (int i = 0; i < a.length-1; i++) {
			int k = i;
			for (int j = i+1; j < a.length; j++) {
				if (a[j]<a[k]) {
					k = j;
				}
			}
			int t = a[i];
			a[i] = a[k];
			a[k] = t;
		}
	}
	
	public static void quickSort(int[] a, int low, int heigh) {
		if (low>=heigh) {
			return;
		}
		int i = low;
		int j = heigh;
		while (i<j) {
			while (i<j && a[low]<a[j]) {
				j--;
			}
			while (i<j && a[low]>a[i]) {
				i++;
			}
			int t = a[i];
			a[i] = a[j];
			a[j] = t;
		}
		int t = a[i];
		a[i] = a[low];
		a[low] = t;
		
		quickSort(a, low, i-1);
		quickSort(a, i+1, heigh);
	}
	
	public static void print(int[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]+" ");
		}
		System.out.println();
	}
	
	
}
