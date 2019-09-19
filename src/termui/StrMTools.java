package termui;

import java.util.Arrays;

public class StrMTools {
	public static String setCharInString(String inputStr, int pos, char newchar) {
		char[] inputChars = inputStr.toCharArray();
		inputChars[pos] = newchar;
		return String.valueOf(inputChars);
	}
	
	public static int countCharsInString(String hayChars, char needleCh) {
		int cnt = 0;
		for(int n = 0; n < hayChars.length(); n++) {
			if(hayChars.charAt(n) == needleCh) {
				cnt++;
			}
		}
		return cnt;
	}
	
	public static String strRepeat(char c, int n) {
		String outStr = "";
		for(int i = 0; i < n; i++) {
			outStr += c;
		}
		return outStr;
	}
	
	public static int getCharIndex(char[] hayChars, char needleChar) {
		int index = -1;
		for(int n = 0; n < hayChars.length; n++) {
			if(needleChar == hayChars[n]) {
				return n;
			}
		}
		return index;
	}

	public static boolean charContain(char[] hayChars, char needleChar) {
		for(int n = 0; n < hayChars.length; n++) {
			if(needleChar == hayChars[n]) {
				return true;
			}
		}
		return false;
	}

	public static char[] deleteChar(char[] hayChars, int index) {
		if(index >= hayChars.length) {
			return hayChars;
		}
		char[] trunChars = new char[hayChars.length - 1];
		int increm = 0;
		for(int n = 0; n < hayChars.length; n++) {
			if(n == index) {
				continue;
			}
			trunChars[increm] = hayChars[n];
			increm++;
		}
		return trunChars;
	}

	public static String SortString(String hayChars) {
		char[] sortedChars = hayChars.toCharArray();
		Arrays.sort(sortedChars);
		return String.valueOf(sortedChars);
    }

}
