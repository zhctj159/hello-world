package zhc.others;

public class SortTest {
	public static void main(String[] args) {
		int[] a = {6,2,7,3,8,1,9};
//		selectSort(a);
//		insertSort(a);
//		quickSort(a, 0, a.length-1);
		bitmapSort2(a);
		print(a);
	}
	
	public static void bitmapSort2(int[] a) {
		//计算待排序数组取值范围
		int max=a[0],min=a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i]>max) {
				max=a[i];
			}
			if (a[i]<min) {
				min=a[i];
			}
		}
		//初始化位图数组
		int[] bm2 = new int[max-min+1];
		for (int i = 0; i < bm2.length; i++) {
			bm2[i] = 0;
		}
		//待排序数组存入位图数组
		for (int i = 0; i < a.length; i++) {
			bm2[a[i]-min]++;	//存在值重复的情况
		}
		//构建结果
		int k = 0;
		for (int i = 0; i < bm2.length; i++) {
			for (int j = 0; j < bm2[i]; j++) {
				a[k++] = i+min;
			}
		}
	}
	
	public static void bitmapSort(int[] a) {
//		int max,min;
//		int[] bitmap = new int[1024*1024*1024];
	}
	
	public static void heapSort(int[] a) {
		
	}
	
	public static void selectSort(int[] a) {
		for (int i = 0; i < a.length-1; i++) {
			int minpos = i;
			for (int j = i+1; j < a.length; j++) {
				if (a[minpos]>a[j]) {
					minpos = j;
				}
			}
			if (minpos!=i) {
				int t = a[i];
				a[i] = a[minpos];
				a[minpos] = t;
			}
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
	
	public static void insertSort(int[] a) {
		int j;
        for(int i=1;i<a.length;i++){
            int temp = a[i];
            for(j=i-1;j>=0 && a[j]>temp;j--){
                a[j+1] = a[j];
            }
            a[j+1] = temp;
        }
	}
	
	public static void quickSort(int[] a, int low, int high) {
		if (low>high) {
			return;
		}
		int i = low;
		int j = high;
		int k = a[low];
		while (i<j) {
			while (i<j && a[j]>k) j--;	//从右向左找到第一个小于k的
			while (i<j && a[i]<=k) i++;	//从左向右找到第一个大于k的
			if (i<j) {		//交换，小的交换到左边，大的交换到右边
				int t = a[i];
				a[i] = a[j];
				a[j] = t;
			}
		}
		int t = a[i];
		a[i] = a[low];
		a[low] = t;
		
		quickSort(a, low, i-1);
		quickSort(a, i+1, high);
	}
	
	public static void print(int[] a) {
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]+" ");
		}
		System.out.println();
	}
}
