
/**
 * Created by HYC on 2016/9/13.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class polynomialDer {
	public static void main(String[] args) {
		poly myPoly = new poly();
		Scanner input = new Scanner(System.in);
		System.out.println("是否采用扩展？Y－是  N－否");
		String extendOrder = input.nextLine();
		if (extendOrder.equals("Y") || extendOrder.equals("y"))
			myPoly.boo = true;
		boolean setflag = true;
		while (setflag) {
			System.out.println("Enter a polynomial:");
			String str = input.nextLine();
			setflag = !myPoly.set(str);
		}
		boolean flag = true;
		while (flag) {
			System.out.println("Please input instruction:");
			String instruction = input.nextLine();
			int command = poly.Oeder(instruction);
			long startTime = System.currentTimeMillis();
			switch (command) {
			case 1:// 求导
				myPoly.partition();
				if (!myPoly.derivative(instruction.substring(5)))
					break;
				if (myPoly.boo && myPoly.str.indexOf("-") != -1)
					myPoly.result = myPoly.extendSim();
				else
					myPoly.result = myPoly.simplify(myPoly.arr);
				myPoly.getStr();
				break;
			case 2:// 求值
				String[] var = instruction.split(" ");
				for (int i = 1; i < var.length; i++) {
					if (var[i].indexOf("=") != -1) {
						String[] temp = var[i].split("=");
						myPoly.specific(temp[0], temp[1]);
					}
				}
				if (myPoly.boo && myPoly.simStr.indexOf("-") != -1)
					myPoly.result = myPoly.extendSim();
				else
					myPoly.result = myPoly.simplify(myPoly.arr);
				myPoly.getStr();
				break;
			default:
				System.out.println("error");
			}
			long endTime = System.currentTimeMillis();
			System.out.println("运行时间" + (endTime - startTime) + "ms");
			System.out.println("continue or give up?(Y/N):");
			String temp = input.nextLine();
			if (temp.equals("n") || temp.equals("N"))
				break;
		}
		input.close();
		System.out.println("end");
	}
}

class poly {
	public boolean boo = false; // 扩展标志
	public String str; // 存储多项式
	public String simStr; // 化简后的多项式
	String[] arr; // 存储多项式的每一项
	public String result;

	public boolean set(String str) {
		// 格式匹配时将多项式存储起来
		if (boo) {
			this.arr = str.split("[+-]");
			for (int i = 0; i < arr.length; i++) {
				arr[i] = arr[i].trim();
				arr[i] = arr[i].replaceAll("\\s", "*");
				Pattern p = Pattern.compile("\\w+([\\^]\\d+)?(([*]\\w+)|([*]\\w+[\\^]\\d+))*");
				Matcher m = p.matcher(arr[i]);
				if (!m.matches())
					return false;
				arr[i] = changeStrFormat(arr[i]);
			}
			this.simStr = str;
			this.str = str;
			return true;
		} else {
			this.str = str;
			this.simStr = str;
			if (!this.expression()) // 格式不匹配时输出错误信息
			{
				System.out.println("error");
				return false;
			}
			return true;
		}
	}

	public void getStr() {
		// System.out.println(str);
		System.out.println(result);
	}

	// public void getSimStr()
	// {
	// System.out.println(simStr);
	// }
	public boolean expression() // 处理表达式，形成自定义数据结构
	{
		Pattern p;
		if (!boo)
			p = Pattern.compile("\\w+([*]\\w)*([+]\\w+([*]\\w)*)*");
		else
			p = Pattern.compile(
					"\\w+([\\^]\\d+)?(([*]\\w+)|([*]\\w+[\\^]\\d+))*([+-]\\w+([\\^]\\d+)?(([*]\\w+)|([*]\\w+[\\^]\\d+))*)*");
		Matcher m = p.matcher(str);
		// 返回是否匹配标记
		return m.matches();
	}

	public void partition() {
		this.arr = this.str.split("[+-]");
		for (int i = 0; i < arr.length; i++)
			this.arr[i] = changeStrFormat(this.arr[i]);
	}

	public boolean derivative(String var) // 求导数,var指多项式中的变量
	{
		if (str.indexOf(var) == -1) {
			System.out.println("error");
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			// 统计该项中的变量的个数
			int count = countOfCh(arr[i], var);
			switch (count) {
			case 0: // 该项中不含有变量，求导后为0
				arr[i] = null;
				break;
			case 1: // 该项中包含一个变量，求导后变量用1代替
				arr[i] = arr[i].replaceAll(var, "1");
				break;
			default: // 包含2个以上的变量，把第一个变量变换成count
				String c = Integer.toString(count);
				arr[i] = arr[i].replaceFirst(var, c);
			}
		}
		return true;
	}

	public static int countOfCh(String myStr, String var) // 统计myStr中var出现的次数
	{
		if (myStr == null || myStr.equals(""))
			return 0;
		int count = 0;
		// 当不进行扩展时每一项中各个变量或数字之间用*分割
		String[] varArr = myStr.split("\\*");

		for (String temp : varArr)
			if (temp.equals(var))
				count++;
		return count;
	}

	// 化简多项式
	public String simplify(String[] arr) {
		String myStr;
		for (int i = 0; i < arr.length; i++)
			if (arr[i] != null)
				arr[i] = simPoly(arr[i]);
		int sum = 0;
		// System.out.println(Arrays.toString(arr));
		for (String temp : arr)
			if (isNum(temp))
				sum = Integer.valueOf(temp).intValue() + sum;
		myStr = sum == 0 ? "" : String.valueOf(sum);
		for (String temp : arr)
			if (temp != null && !isNum(temp))
				myStr = myStr + "+" + temp;
		myStr = sum == 0 ? myStr.substring(1) : myStr;
		// System.out.println(myStr);
		return myStr;
	}

	public String extendSim() {
		ArrayList<Integer> list = new ArrayList<Integer>(); // 记录所有正负号所在的位置
		int index = str.indexOf("-");
		int min = -1;
		int pcount = 0, scount = 0;
		if (index != 0) {
			list.add(1);
			pcount++;
		}
		do {
			int a = str.indexOf("+", ++min);
			int b = str.indexOf("-", ++min);
			if (a == -1 && b == -1)
				break;
			else if (a == -1)
				min = b;
			else if (b == -1)
				min = a;
			else
				min = a < b ? a : b;
			if (a == min) {
				list.add(1);
				pcount++;
			} else
				list.add(0);
		} while (min > -1 && min < str.length());
		scount = list.size() - pcount;
		String[] parr = new String[pcount];
		String[] sarr = new String[scount];
		int m = 0, n = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == 0) {
				sarr[n] = this.arr[i];
				n++;
			} else {
				parr[m] = this.arr[i];
				m++;
			}
		}
		// System.out.println(Arrays.toString(parr));
		// System.out.println(Arrays.toString(sarr));
		// return simplify(parr) + "-(" + simplify(sarr) + ")";
		return simplify(parr) + "-" + simplify(sarr).replaceAll("\\+", "-");
	}

	// 通过传入一个具体的值intvalue将表达式中的var替换成具体的值
	public void specific(String var, String intvalue) {
		if (intvalue != null && intvalue != "")
			simStr = simStr.replace(var, intvalue);
		arr = simStr.split("[+-]");
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr[i].trim();
			arr[i] = arr[i].replaceAll("\\s", "*");
			arr[i] = changeStrFormat(arr[i]);
		}
		// arr = simStr.split("\\-");
	}

	// 化简多项式的一项，例如：将a*a变为a^2，将1*2变为2
	public static String simPoly(String myStr) {
		// 确定该项中含有多少变量或数字
		String[] arrlocal = myStr.split("\\*");
		Arrays.sort(arrlocal);
		int sum = 1; // 该项中所有数字的乘积
		String strTemp = arrlocal[0];
		String strReturn = new String();
		int count = 0; // 字符出现的次数
		for (String temp : arrlocal) {
			// 计算所有数字的乘积
			if (isNum(temp)) {
				int a = Integer.valueOf(temp).intValue();
				if (a == 0)
					return null;
				sum = sum * a;
				strTemp = temp;
			} else // 统计每种字符出现的次数
			{
				if (strTemp.equals(temp))
					count++;
				else {
					if (count == 1)
						strReturn = strReturn + "*" + strTemp;
					else if (count > 1)
						strReturn = strReturn + "*" + strTemp + "^" + String.valueOf(count);
					strTemp = temp;
					count = 1;
				}
			}
		}
		if (count == 1)
			strReturn = strReturn + "*" + strTemp;
		else if (count > 1)
			strReturn = strReturn + "*" + strTemp + "^" + String.valueOf(count);
		if (strReturn.isEmpty())
			return String.valueOf(sum);
		else {
			if (sum == 1)
				return strReturn.substring(1);
			else
				return String.valueOf(sum) + strReturn;
		}
	}

	public static String changeStrFormat(String myStr) {
		// 将该项分割成单个的字符或者 var ^ d的形式
		myStr = myStr.trim();
		String[] temp = myStr.split("\\*|\\s");
		for (int i = 0; i < temp.length; i++) {
			int index = temp[i].indexOf("^");
			if (index != -1) // 出现了var^d的形式
			{
				String[] a = temp[i].split("\\^");
				int numOfVar = Integer.parseInt(a[1]);
				temp[i] = a[0];
				for (int j = 1; j < numOfVar; j++) {
					temp[i] = temp[i] + "*" + a[0];
				}
			}
		}
		// 合并得到不含有^的项
		myStr = temp[0];
		for (int i = 1; i < temp.length; i++)
			myStr = myStr + "*" + temp[i];
		return myStr;
	}

	// 判断一个字符串是否可以转化位为数字，采用正则表达式的方法
	public static boolean isNum(String s) {
		if (s == null)
			return false;
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public static int Oeder(String order) {
		// 求导指令
		Pattern der = Pattern.compile("!d/d \\w+");
		// 化简指令
		Pattern sim = Pattern.compile("!simplify( \\w+(=\\d+)?)+");
		// 求导指令返回1，化简指令返回2，否则返回0
		if (der.matcher(order).matches())
			return 1;
		else if (sim.matcher(order).matches())
			return 2;
		else
			return 0;
	}
}
//add a new line at the end of the file
