package mmcore;

import java.io.*;
import java.util.HashMap;

public final class SimpleSettings {
	
	private static final String fileName = "settings.ser";
	private static HashMap<String, String> map;
	private static String filePath;
	
	public static void init() {
		map = new HashMap<String, String>();
		map.put("numPawns", "4");
		map.put("assortPawns", "6");
		map.put("maxTurns", "12");
		map.put("gameTime", "0");
		map.put("turnTime", "0");
		map.put("allowCopies", "1");
		map.put("startupHints", "0");
		// GUI stuff below
		map.put("windowSize", "300;512");
		map.put("windowState", "normal");
		genPath();
		tryFetchFromPath();
	}
	
	private static void genPath() {
		String slash = System.getProperty("file.separator");
		String fileDir = System.getProperty("user.home") + slash + ".config" + slash + "mastermindy";
		new File(fileDir).mkdirs();
		filePath = fileDir + slash + fileName;
	}
	
	private static void tryFetchFromPath() {
		try {
	        FileInputStream fileIn = new FileInputStream(filePath);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        HashMap<?, ?> rawmap = (HashMap<?, ?>) in.readObject();
	        in.close();
	        fileIn.close();
	        updateMapValues(rawmap);
		} catch (FileNotFoundException e) {
	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}
	private static void updateMapValues(HashMap<?, ?> rawmap) {
		String rawKey;
		for(Object rawKObj: rawmap.keySet()) { // loop over all keys from rawmap
			rawKey = (String) rawKObj;
			if(map.containsKey(rawKey)) {
				map.put(rawKey, (String) rawmap.get(rawKey));
			}
		}
	}
	
	public static void store(String key, String value) {
		map.put(key, value);
		try {
	        FileOutputStream fileOut = new FileOutputStream(filePath);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(map);
	        out.close();
	        fileOut.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	}
	
	public static String get(String key) {
		return map.get(key);
	}
	
	public static int getInt(String key) {
		int ret = 0;
		try {
			ret = Integer.parseInt(map.get(key)); 
		} catch (NumberFormatException e) {}
		return ret;
	}
	
	public static boolean getBool(String key) {
		return !map.get(key).equals("0");
	}

}
