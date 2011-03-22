package org.flemil.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.flemil.util.ResourcesFactory;


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
//			e.printStackTrace();
		}
	}
	public static void loadTranslationsFromStream(InputStream is) throws IOException{
		currentTranslations=ResourcesFactory.loadProperites(is);
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