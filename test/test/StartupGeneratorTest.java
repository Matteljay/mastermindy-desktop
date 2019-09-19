package test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import mmcore.StartupGenerator;

public class StartupGeneratorTest {
	
	private ArrayList<Integer> theSecret;

	@Test
	public void startupGeneratorTest() {
		theSecret = new ArrayList<Integer>();
		setInt(theSecret, 0, 1); showMatrix(2, theSecret);
		setInt(theSecret, 10, 11); showMatrix(12, theSecret);
		setInt(theSecret, 4, 5, 6, 7); showMatrix(11, theSecret);
		setInt(theSecret, 3, 1, 2, 0); showMatrix(10, theSecret);
		setInt(theSecret, 8, 7, 6, 5); showMatrix(9, theSecret);
		setInt(theSecret, 3, 1); showMatrix(2, theSecret);
		setInt(theSecret, 3, 1, 2, 0, 2, 3, 4, 5); showMatrix(1, theSecret);
		setInt(theSecret, 3, 1, 2, 0, 2, 3, 4, 5); showMatrix(0, theSecret);
		setInt(theSecret, 2, 3); showMatrix(1, theSecret);
		setInt(theSecret, 0, 1); showMatrix(2, theSecret);
		setInt(theSecret, 0); showMatrix(2, theSecret);
		setInt(theSecret, 3, 1, 2, 0); showMatrix(4, theSecret);
		setInt(theSecret, 0, 1, 2); showMatrix(6, theSecret);
		System.out.println("success.");
		assertTrue(true);
	}
	
	private void showMatrix(int assortPawns, ArrayList<Integer> theSecret) {
		System.out.printf("input: assortPawns: %d, theSecret: %s\n", assortPawns, theSecret.toString());
		StartupGenerator startupGenerator = new StartupGenerator(assortPawns, theSecret);
		for(ArrayList<Integer> feeler: startupGenerator) {
			System.out.println("output: startup-hint: " + feeler);
		}
		System.out.println();
	}

	private void setInt(ArrayList<Integer> arr, int... myints) {
		arr.clear();
		for(int i: myints) {
			arr.add(i);
		}
	}
}
