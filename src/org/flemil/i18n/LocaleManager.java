package org.flemil.i18n;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;


public class LocaleManager {
	private static Hashtable currentTranslations;
	public static final byte RTOL=0;
	public static final byte LTOR=1;
	private static final String TEXT_DIRE_STR="flemil.textdirection";
	private static byte textDirection=LocaleManager.LTOR;
	
	static{
		InputStream defaultLangStream=new LocaleManager().getClass().getResourceAsStream("/i18n_default.txt");
		if(defaultLangStream==null){
			defaultLangStream=new LocaleManager().getClass().getResourceAsStream("/i18n_template.txt");
		}
		try {
			loadTranslationsFromStream(defaultLangStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static Hashtable loadTranslations(InputStream is)throws IOException{
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
	public static void loadTranslationsFromStream(InputStream is) throws IOException{
		currentTranslations=loadTranslations(is);
		String textDirection=(String)currentTranslations.get(LocaleManager.TEXT_DIRE_STR);
		if(textDirection==null){
			LocaleManager.textDirection=LocaleManager.LTOR;
		}
		else{
			if(textDirection.toLowerCase().trim().equals("lr")){
				LocaleManager.textDirection=LocaleManager.LTOR;
			}
			else{
				LocaleManager.textDirection=LocaleManager.RTOL;
			}
		}
	}
	
	public static byte getTextDirection(){
		return textDirection;
	}
	public static String getTranslation(String identifier){
		if(currentTranslations.get(identifier)!=null){
			String value=currentTranslations.get(identifier).toString();
			return value;
		}
		else{
			throw new IllegalArgumentException("No localization Found for Identifier:" +
					" "+identifier);
		}
	}
	public static String getTranslation(String identifier,String param){
		return getTranslation(identifier, new String[]{param});
	}
	public static String getTranslation(String identifier,String[] params){
		if(currentTranslations.get(identifier)!=null){
			String value=currentTranslations.get(identifier).toString();
			StringBuffer result=new StringBuffer();
			int currentParam=0;
			Vector values=new Vector();
			Vector keys=new Vector();
			for(int i=0;i<value.length();i++){
				if(value.charAt(i)!='\\'){
					if(value.charAt(i)!='{'){
						result.append(value.charAt(i));
					}
					else{
						if(i>0 && value.charAt(i-1)=='\\'){
							int count=0;
							while(value.charAt(i-count-1)=='\\'){
								count++;
							}
							if(count%2!=0){
								result.append(value.charAt(i));
							}
							else{
								if(currentParam<params.length){
									//replace the parameter
									int incr=value.substring(i).indexOf("}");
									if(incr!=-1){
										i+=incr;
										values.addElement(params[currentParam++]);
										keys.addElement(result.toString());
										result=new StringBuffer();
									}
									else{
										result.append(value.charAt(i));
									}
								}
								else{
									result.append(value.charAt(i));
								}
							}
						}
						else{
							if(currentParam<params.length){
								//replace the parameter
								int incr=value.substring(i).indexOf("}");
								if(incr!=-1){
									i+=incr;
									values.addElement(params[currentParam++]);
									keys.addElement(result.toString());
									result=new StringBuffer();
								}
								else{
									result.append(value.charAt(i));
								}
							}
							else{
								result.append(value.charAt(i));
							}
						}
					}
				}
				else{
					if(i>0 && value.charAt(i-1)=='\\'){
						int count=0;
						while(value.charAt(i-count-1)=='\\'){
							count++;
						}
						if(count%2!=0){
							result.append('\\');
							values.addElement("");
							keys.addElement(result.toString());
							result=new StringBuffer();
							String tmp1=value.substring(0, i-1);
							String tmp2=value.substring(i+1);
							value=tmp1+tmp2;
							i-=2;
							continue;
						}
						else{
							result.append(value.charAt(i));
						}
					}
				}
			}
			values.addElement(result.toString());
			StringBuffer resultBuff=new StringBuffer();
			for(int i=0;i<keys.size();i++){
				resultBuff.append(keys.elementAt(i).toString());
				resultBuff.append(values.elementAt(i).toString());
			}
			resultBuff.append(values.elementAt(values.size()-1).toString());
			return resultBuff.toString();
		} 
		else{
			throw new IllegalArgumentException("No localization Found for Identifier:" +
					" "+identifier);
		}
	}
}