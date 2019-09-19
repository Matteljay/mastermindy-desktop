package mmcore;

public class HintStruct {
	public int numBlacks;
	public int numWhites;
	public int numBlanks;
	
	public HintStruct() {
		numBlacks = numWhites = numBlanks = 0;
	}
	
	public void set(int blacks, int whites, int blanks) {
		numBlacks = blacks;
		numWhites = whites;
		numBlanks = blanks;
	}
	
	@Override
	public boolean equals(Object obj) { // for unit testing
		if(obj instanceof HintStruct) {
			HintStruct h = ((HintStruct) obj);
			return (h.numBlacks == numBlacks && h.numWhites == numWhites && h.numBlanks == numBlanks);
		}
		return super.equals(obj);
	}
	@Override
	public String toString() {
		return "[" + numBlacks + ", " + numWhites + ", " + numBlanks + "]";
		//return super.toString();
	}
}
