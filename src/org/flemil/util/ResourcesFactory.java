package org.flemil.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class ResourcesFactory {
	public static Hashtable loadProperites(InputStream is) throws IOException{
		Hashtable translations=new Hashtable();
    	byte []buffer=new byte[1024];
    	int read=0;
    	ByteArrayOutputStream baos=new ByteArrayOutputStream();
    	while((read=is.read(buffer))>0){
    		baos.write(buffer, 0, read);
    		while(true){
    			String testString=new String(baos.toByteArray(),"UTF-8");
        		while(testString.startsWith("\n")){
        			if(testString.length()>1){
        				testString=testString.substring(1);
        			}
        			else{
        				testString="";
        			}
        		}
        		int newlinePoint=testString.indexOf("\n");
        		if(newlinePoint==-1){
        			baos=new ByteArrayOutputStream();
        			baos.write(testString.getBytes("UTF-8"));
        			break;
        		}
        		String currentInput=testString.substring(0,newlinePoint);
        		String remnantString="";
        		if(newlinePoint<testString.length()-1){
        			remnantString=testString.substring(newlinePoint+1);
        		}
        		if(!currentInput.startsWith("#")){
        			int splitPoint=currentInput.indexOf("=");
        			if(splitPoint!=-1){
            			String property=currentInput.substring(0,splitPoint);
        				String value=currentInput.substring(splitPoint+1);
        				if(value.indexOf("\\n")!=-1){
        					StringBuffer buff=new StringBuffer();
        					String[] splits=StringUtils.split(value, "\\n");
        					for(int i=0;i<splits.length-1;i++){
        						buff.append(splits[i]);
        						buff.append("\n");
        					}
        					buff.append(splits[splits.length-1]);
        					value=buff.toString();
        				}
        				translations.put(property, value);
        			}
        		}
        		baos=new ByteArrayOutputStream();
        		baos.write(remnantString.getBytes("UTF-8"));
    		}
    	}
    	while(true){
			String testString=new String(baos.toByteArray(),"UTF-8");
    		while(testString.startsWith("\n")){
    			if(testString.length()>1){
    				testString=testString.substring(1);
    			}
    			else{
    				testString="";
    			}
    		}
    		int newlinePoint=testString.indexOf("\n");
    		String currentInput="";
    		String remnantString="";
    		if(newlinePoint!=-1){
    			currentInput=testString.substring(0,newlinePoint);
        		if(newlinePoint>-1 && newlinePoint!=testString.length()-1){
        			remnantString=testString.substring(newlinePoint+1);
        		}
    		}
    		else{
    			currentInput=testString;
    		}
    		if(!currentInput.startsWith("#")){
    			int splitPoint=currentInput.indexOf("=");
    			if(splitPoint!=-1){
        			String property=currentInput.substring(0,splitPoint);
    				String value=currentInput.substring(splitPoint+1);
    				if(value.indexOf("\\n")!=-1){
    					StringBuffer buff=new StringBuffer();
    					String[] splits=StringUtils.split(value, "\\n");
    					for(int i=0;i<splits.length-1;i++){
    						buff.append(splits[i]);
    						buff.append("\n");
    					}
    					buff.append(splits[splits.length-1]);
    					value=buff.toString();
    				}
    				translations.put(property, value);
    			}
    		}
    		if(remnantString.length()==0){
    			break;
    		}
    		baos=new ByteArrayOutputStream();
    		baos.write(remnantString.getBytes("UTF-8"));
		}
    	return translations;
	}
}
