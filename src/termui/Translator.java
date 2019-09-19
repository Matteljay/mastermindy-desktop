package termui;

import java.util.ArrayList;

import mmcore.HintStruct;

public class Translator {
	private static final String pawnArray = "RGBFYAMLNPOT"; // 12 colors
	
	public static String mmFeelerEncode(ArrayList<Integer> feelerArray) {
		String outStr = "";
		for(int feeler: feelerArray) {
			if(feeler < pawnArray.length()) {
				outStr += pawnArray.charAt(feeler);
			}
		}
		return outStr;
	}

	public static ArrayList<Integer> mmFeelerDecode(String feelerString) {
		ArrayList<Integer> feelerArray = new ArrayList<Integer>();
		for(char feel: feelerString.toCharArray()) {
			feelerArray.add(pawnArray.indexOf(feel));
		}
		return feelerArray;
	}

	public static String mmHintEncode(HintStruct hint) {
		return StrMTools.strRepeat('b', hint.numBlacks) +
				StrMTools.strRepeat('w', hint.numWhites) +
				StrMTools.strRepeat('-', hint.numBlanks);
	}
	
	public static String getPawnArray(int length) {
		if(length > 0) {
			return pawnArray.substring(0, length);
		}
		return pawnArray;
	}
}
