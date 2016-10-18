
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
		System.out.println("�Ƿ������չ��Y����  N����");
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
			case 1:// ��
				myPoly.partition();
				if (!myPoly.derivative(instruction.substring(5)))
					break;
				if (myPoly.boo && myPoly.str.indexOf("-") != -1)
					myPoly.result = myPoly.extendSim();
				else
					myPoly.result = myPoly.simplify(myPoly.arr);
				myPoly.getStr();
				break;
			case 2:// ��ֵ
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
			System.out.println("����ʱ��" + (endTime - startTime) + "ms");
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
	public boolean boo = false; // ��չ��־
	public String str; // �洢����ʽ
	public String simStr; // �����Ķ���ʽ
	String[] arr; // �洢����ʽ��ÿһ��
	public String result;

	public boolean set(String str) {
		// ��ʽƥ��ʱ������ʽ�洢����
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
			if (!this.expression()) // ��ʽ��ƥ��ʱ���������Ϣ
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
	public boolean expression() // ������ʽ���γ��Զ������ݽṹ
	{
		Pattern p;
		if (!boo)
			p = Pattern.compile("\\w+([*]\\w)*([+]\\w+([*]\\w)*)*");
		else
			p = Pattern.compile(
					"\\w+([\\^]\\d+)?(([*]\\w+)|([*]\\w+[\\^]\\d+))*([+-]\\w+([\\^]\\d+)?(([*]\\w+)|([*]\\w+[\\^]\\d+))*)*");
		Matcher m = p.matcher(str);
		// �����Ƿ�ƥ����
		return m.matches();
	}

	public void partition() {
		this.arr = this.str.split("[+-]");
		for (int i = 0; i < arr.length; i++)
			this.arr[i] = changeStrFormat(this.arr[i]);
	}

	public boolean derivative(String var) // ����,varָ����ʽ�еı���
	{
		if (str.indexOf(var) == -1) {
			System.out.println("error");
			return false;
		}
		for (int i = 0; i < arr.length; i++) {
			// ͳ�Ƹ����еı����ĸ���
			int count = countOfCh(arr[i], var);
			switch (count) {
			case 0: // �����в����б������󵼺�Ϊ0
				arr[i] = null;
				break;
			case 1: // �����а���һ���������󵼺������1����
				arr[i] = arr[i].replaceAll(var, "1");
				break;
			default: // ����2�����ϵı������ѵ�һ�������任��count
				String c = Integer.toString(count);
				arr[i] = arr[i].replaceFirst(var, c);
			}
		}
		return true;
	}

	public static int countOfCh(String myStr, String var) // ͳ��myStr��var���ֵĴ���
	{
		if (myStr == null || myStr.equals(""))
			return 0;
		int count = 0;
		// ����������չʱÿһ���и�������������֮����*�ָ�
		String[] varArr = myStr.split("\\*");

		for (String temp : varArr)
			if (temp.equals(var))
				count++;
		return count;
	}

	// �������ʽ
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
		ArrayList<Integer> list = new ArrayList<Integer>(); // ��¼�������������ڵ�λ��
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

	// ͨ������һ�������ֵintvalue�����ʽ�е�var�滻�ɾ����ֵ
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

	// �������ʽ��һ����磺��a*a��Ϊa^2����1*2��Ϊ2
	public static String simPoly(String myStr) {
		// ȷ�������к��ж��ٱ���������
		String[] arrlocal = myStr.split("\\*");
		Arrays.sort(arrlocal);
		int sum = 1; // �������������ֵĳ˻�
		String strTemp = arrlocal[0];
		String strReturn = new String();
		int count = 0; // �ַ����ֵĴ���
		for (String temp : arrlocal) {
			// �����������ֵĳ˻�
			if (isNum(temp)) {
				int a = Integer.valueOf(temp).intValue();
				if (a == 0)
					return null;
				sum = sum * a;
				strTemp = temp;
			} else // ͳ��ÿ���ַ����ֵĴ���
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
		// ������ָ�ɵ������ַ����� var ^ d����ʽ
		myStr = myStr.trim();
		String[] temp = myStr.split("\\*|\\s");
		for (int i = 0; i < temp.length; i++) {
			int index = temp[i].indexOf("^");
			if (index != -1) // ������var^d����ʽ
			{
				String[] a = temp[i].split("\\^");
				int numOfVar = Integer.parseInt(a[1]);
				temp[i] = a[0];
				for (int j = 1; j < numOfVar; j++) {
					temp[i] = temp[i] + "*" + a[0];
				}
			}
		}
		// �ϲ��õ�������^����
		myStr = temp[0];
		for (int i = 1; i < temp.length; i++)
			myStr = myStr + "*" + temp[i];
		return myStr;
	}

	// �ж�һ���ַ����Ƿ����ת��λΪ���֣�����������ʽ�ķ���
	public static boolean isNum(String s) {
		if (s == null)
			return false;
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public static int Oeder(String order) {
		// ��ָ��
		Pattern der = Pattern.compile("!d/d \\w+");
		// ����ָ��
		Pattern sim = Pattern.compile("!simplify( \\w+(=\\d+)?)+");
		// ��ָ���1������ָ���2�����򷵻�0
		if (der.matcher(order).matches())
			return 1;
		else if (sim.matcher(order).matches())
			return 2;
		else
			return 0;
	}
}
//add a new line at the end of the file
