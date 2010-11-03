package org.flemil.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;




public class RMSUtil {
	private static final String RMS_NAME="FLEMIL7896";
	private static final String STYLE_RMS="FLE_STY";
	private static final String STYLE_INDEX="FLE_STY_IND";
	public synchronized static boolean saveStyle(Style style,String name,String id,String owner)
	{
		try { 
			RecordStore libStore=RecordStore.openRecordStore(RMSUtil.RMS_NAME, true,RecordStore.AUTHMODE_ANY,true);
			int count=libStore.getNumRecords();
			boolean found=false;
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_INDEX))
				{
					found=true;
					try
					{
						String read=dis.readUTF();
						while(read!=null)
						{
							if(read.equals(id))
							{
								deleteStyle(id);
								saveStyle(style, name, id, owner);
								return true;
							}
							read=dis.readUTF();
						}
					}catch(EOFException eof){break;}
				}
				dis.close();
			}
			if(!found)
			{
				ByteArrayOutputStream baaos=new ByteArrayOutputStream();
				DataOutputStream dos=new DataOutputStream(baaos);
				dos.writeUTF(RMSUtil.STYLE_INDEX);
				dos.writeUTF(id);
				dos.writeUTF(name);
				dos.writeUTF(owner);
				byte []dat=baaos.toByteArray();
				libStore.addRecord(dat, 0, dat.length);
				dos.close();
				baaos.close();
				baaos=new ByteArrayOutputStream();
				dos=new DataOutputStream(baaos);
				dos.writeUTF(RMSUtil.STYLE_RMS);
				dos.writeUTF(id);
				dos.write(style.toByteArray());
				dat=baaos.toByteArray();
				libStore.addRecord(dat, 0, dat.length);
				libStore.closeRecordStore();
				baaos.close();
				dos.close();
				return true;
			}
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_INDEX))
				{
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					DataOutputStream dos=new DataOutputStream(baos);
					dos.write(data);
					dos.writeUTF(id);
					dos.writeUTF(name);
					dos.writeUTF(owner);
					byte []dat=baos.toByteArray();
					libStore.setRecord(i, dat, 0, dat.length);
					break;
				}
			}
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_RMS))
				{
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					DataOutputStream dos=new DataOutputStream(baos);
					dos.write(data);
					dos.writeUTF(id);
					dos.write(style.toByteArray());
					byte []dat=baos.toByteArray();
					libStore.setRecord(i, dat, 0, dat.length);
				}
			}
			libStore.closeRecordStore();
			return true;
		}
		catch (IOException e) {
			return false;
		}catch (RecordStoreFullException e) {
			return false;
		} catch (RecordStoreNotFoundException e) {
			return false;
		} catch (RecordStoreException e) {
			return false;
		}
	}
	public synchronized static Vector getSavedStyleNames()
	{
		Vector saved=new Vector();
		try {
			RecordStore libStore=RecordStore.openRecordStore(RMSUtil.RMS_NAME, true,RecordStore.AUTHMODE_ANY,true);
			int count=libStore.getNumRecords();
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_INDEX))
				{
					String read=dis.readUTF();
					while(read!=null)
					{
						saved.addElement(read);
						saved.addElement(dis.readUTF());
						saved.addElement(dis.readUTF());
						read=dis.readUTF();
					}
				}
				dis.close();
			}
			libStore.closeRecordStore();
		}
		catch (IOException e) {
		}catch (RecordStoreFullException e) {
		} catch (RecordStoreNotFoundException e) {
		} catch (RecordStoreException e) {
		}
		return saved;
	}
	public synchronized static Style getStyle(String id)
	{
		Style style=null;
		try {
			RecordStore libStore=RecordStore.openRecordStore(RMSUtil.RMS_NAME, true,RecordStore.AUTHMODE_ANY,true);
			int count=libStore.getNumRecords();
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_RMS))
				{
					String read=dis.readUTF();
					while(!read.equals(id))
					{
						Style.getDefault().loadFromByteStream(dis);
						read=dis.readUTF();
					}
					style=Style.getDefault().loadFromByteStream(dis);
				}
			}
			libStore.closeRecordStore();
		}
		catch (IOException e) {
		}catch (RecordStoreFullException e) {
		} catch (RecordStoreNotFoundException e) {
		} catch (RecordStoreException e) {
		}
		return style;
	}
	public synchronized static boolean deleteStyle(String id)
	{
		try {
			RecordStore libStore=RecordStore.openRecordStore(RMSUtil.RMS_NAME, true,RecordStore.AUTHMODE_ANY,true);
			int count=libStore.getNumRecords();
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_INDEX))
				{
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					DataOutputStream dos=new DataOutputStream(baos);
					dos.writeUTF(RMSUtil.STYLE_INDEX);
					try
					{
						String read=dis.readUTF();
						while(read!=null)
						{
							if(!read.equals(id))
							{
								dos.writeUTF(read);
								dos.writeUTF(dis.readUTF());
								dos.writeUTF(dis.readUTF());
							}
							else
							{
								dis.readUTF();
								dis.readUTF();
							}
							read=dis.readUTF();
						}
					}
					catch (EOFException e) {
						byte[] dat=baos.toByteArray();
						libStore.setRecord(i, dat, 0, dat.length);
					}
				}
				dis.close();
			}
			for(int i=1;i<count+1;i++)
			{
				byte[] data=libStore.getRecord(i);
				DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
				String rmsName=dis.readUTF();
				if(rmsName.equals(RMSUtil.STYLE_RMS))
				{
					ByteArrayOutputStream baos=new ByteArrayOutputStream();
					DataOutputStream dos=new DataOutputStream(baos);
					dos.writeUTF(RMSUtil.STYLE_RMS);
					try
					{
						String read=dis.readUTF();
						while(read!=null)
						{
							if(!read.equals(id))
							{
								dos.writeUTF(read);
								dos.write(Style.getDefault().loadFromByteStream(dis).toByteArray());
							}
							else
							{
								Style.getDefault().loadFromByteStream(dis);
							}
							read=dis.readUTF();
						}
					}catch(EOFException eofe){
						byte[] dat=baos.toByteArray();
						libStore.setRecord(i, dat, 0, dat.length);
					}
				}
			}
			libStore.closeRecordStore();
		}
		catch (IOException e) {return false;
		}catch (RecordStoreFullException e) {
			return false;
		} catch (RecordStoreNotFoundException e) {
			return false;
		} catch (RecordStoreException e) {
			return false;
		}
		return true;
	}
}
