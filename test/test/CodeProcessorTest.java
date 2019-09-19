package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import mmcore.CodeProcessor;
import mmcore.HintStruct;

public class CodeProcessorTest {

	private CodeProcessor codeProcessor;
	private ArrayList<Integer> theSecret;
	private ArrayList<Integer> feeler;
	private HintStruct hint;
	
	@Test
	public void computeHintTest() {
		theSecret = new ArrayList<Integer>();
		codeProcessor = new CodeProcessor();
		feeler = new ArrayList<Integer>();
		hint = new HintStruct();
		
		/*
		for(int i = 0; i < 10; i++) {
			theSecret = codeProcessor.newRandomSecret(12, 12, false);
			System.out.println(theSecret);
		}
		*/
		
		
		setInt(theSecret, 0, 3, 4, 4);
		setInt(feeler, 0, 3, 4, 5);	hint.set(3, 0, 1); myAssert();
		setInt(feeler, 0, 3, 5, 5);	hint.set(2, 0, 2); myAssert();
		setInt(feeler, 0, 0, 0, 0);	hint.set(1, 0, 3); myAssert();
		setInt(feeler, 5, 0, 0, 0);	hint.set(0, 1, 3); myAssert();
		setInt(feeler, 4, 4, 4, 4);	hint.set(2, 0, 2); myAssert();

		setInt(theSecret, 1, 2, 1, 2);
		setInt(feeler, 1, 1, 1, 1);	hint.set(2, 0, 2); myAssert();
		setInt(feeler, 2, 1, 2, 1);	hint.set(0, 4, 0); myAssert();

		setInt(theSecret, 7, 4, 0, 6);
		setInt(feeler, 6, 6, 6, 4);	hint.set(0, 2, 2); myAssert();
		setInt(feeler, 4, 7, 0, 0);	hint.set(1, 2, 1); myAssert();
		setInt(feeler, 4, 7, 0, 0, 6);	hint.set(1, 3, 0); myAssert();
		setInt(feeler, 2, 1, 1, 1, 6, 0, 7, 7);	hint.set(0, 3, 1); myAssert();
		setInt(feeler, 2, 1, 1, 1, 6, 0, 7, 6);	hint.set(0, 3, 1); myAssert();
		setInt(feeler, 4, 7, 0, 0, 1, 2);	hint.set(1, 2, 1); myAssert();
		setInt(feeler, 4, 1, 7);	hint.set(0, 2, 2); myAssert();
		setInt(feeler, 4);	hint.set(0, 1, 3); myAssert();
		setInt(feeler, 2, 1);	hint.set(0, 0, 4); myAssert();
		setInt(feeler, 7, 4, 0, 6, 7);	hint.set(4, 0, 0); myAssert();
		setInt(feeler, 7, -1, -2, 4, 7);	hint.set(1, 1, 2); myAssert();
		
		setInt(theSecret, 1, 2, 0, 4);
		setInt(feeler, 5, 5, 5, 5, 4, 0, 2, 1);	hint.set(0, 4, 0); myAssert();
		
		setInt(theSecret, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4);
		setInt(   feeler, 3, 3, 1, 4);	hint.set(1, 3, 6); myAssert();
		
		setInt(theSecret, 2, 1, 2, 2);
		setInt(   feeler, 1, 1, 2, 2);	hint.set(3, 0, 1); myAssert();
		
		setInt(theSecret, 3, 2, 3, 4);
		setInt(   feeler, 1, 2, 3, 6);	hint.set(2, 0, 2); myAssert();
		
		setInt(theSecret, 1, 2, 1, 3);
		setInt(   feeler, 4, 2, 1, 6);	hint.set(2, 0, 2); myAssert();
		
		setInt(theSecret, 1, 1, 2, 3);
		setInt(   feeler, 4, 1, 5, 6);	hint.set(1, 0, 3); myAssert();
		
		setInt(theSecret, 1, 1, 2, 3, 3, 4);
		setInt(   feeler, 1, 1, 3, 3, 2, 4);	hint.set(4, 2, 0); myAssert();
		
		setInt(theSecret, 1, 1, 2, 3, 3, 4);
		setInt(   feeler, 1, 3, 1, 3, 2, 4);	hint.set(3, 3, 0); myAssert();

	}
	
	private void myAssert() {
		HintStruct compHint = codeProcessor.computeHint(theSecret, feeler);
		//System.out.println("secret: " + theSecret + " feeler: " + feeler + " hint: " + hint + " computed: " + compHint);
		assertEquals(hint, compHint);
	}

	private void setInt(ArrayList<Integer> arr, int... myints) {
		arr.clear();
		for(int i: myints) {
			arr.add(i);
		}
	}
}
