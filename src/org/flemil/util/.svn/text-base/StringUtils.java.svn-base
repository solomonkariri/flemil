package org.flemil.util;

import java.util.Vector;

public class StringUtils {
	public static String[] split(String mainString, String delim){ 
	 Vector splits=new Vector();
		String remainder=mainString;
		int index=remainder.indexOf(delim);
		while(index!=-1 && index<=remainder.length()){
			if(index!=0){
				splits.addElement(remainder.substring(0, index));
			}
			int newStart=index+delim.length();
			if(newStart==remainder.length()){
				remainder="";
				break;
			}
			remainder=remainder.substring(newStart);
			index=remainder.indexOf(delim);
		}
		if(!remainder.trim().equals("")){
			splits.addElement(remainder);
		}
		int size=splits.size();
		String[] res=new String[size];
		for(int i=0;i<size;i++){
			res[i]=splits.elementAt(i).toString();
		}
		return res;
	}
}
