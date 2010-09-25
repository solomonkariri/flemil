package org.flemil.util;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * Class that provides utility methods such as resizing for manipulating images
 * to be used for display as well as creation of images to be used for gradients in the
 * applications UI generation.
 * 
 * @author Solomon Kariri
 */
public class ImageFactory
{
	/**
	 * used to denote a shading style where the light seems to be coming from the top of a
	 * generated gradient texture
	 */
    public static final byte LIGHT_TOP=0;
    /**
	 * used to denote a shading style where the light seems to be coming from the bottom of a
	 * generated gradient texture
	 */
    public static final byte LIGHT_BOTTOM=1;
    /**
	 * used to denote a shading style where the light seems to be coming from the front of a
	 * generated gradient texture
	 */
    public static final byte LIGHT_FRONT=2;
    /**
	 * used to denote a shading style where the light seems to be coming from behind the
	 * generated gradient texture
	 */
    public static final byte LIGHT_BEHIND=3;
    //Declare the CRC table
    private int crcTable[]=null;
    public ImageFactory()
    {
    }
    /**
    * Scales a image passed in as a parameter and returns a new Image with
    * the specified preferred size and transformation. The transformations
    * have to be one of the transformation constantsde fined in class Sprite,
    * for example, Sprite.TRANS_MIRROR. This method preserves transparency in
    * a scaled Image. It can be used to scale Immutable images that have for
    * example being loaded from the resources or an input stream while
    * preserving its tranparency or alpha channel information.
    * @param srcImg the source image to be scaled
    * @param newWidth scaled image's width
    * @param newHeight scaled image's height
    * @param transform the transformation to be applied to the image
    * @return the scaled image
    */

	public Image scaleImage(Image srcImg,int newWidth,
		int newHeight,int transform)
	{
        //Apply the transform to the original image first
        Image mirroredImg=Image.createImage(srcImg,0,0,srcImg.getWidth(),
				srcImg.getHeight(),transform);
		if(mirroredImg.getWidth()==newWidth &&
			mirroredImg.getHeight()==newHeight)
		{
			return mirroredImg;
		}
		Image newImage=mirroredImg;
		//capture src image dimensions
		int width=mirroredImg.getWidth();
		int height=mirroredImg.getHeight();
		int rgbData[]=new int[newWidth*newHeight];
		int track=0;
		for(int i=0;i<newHeight;i++)
		{
			for(int j=0;j<newWidth;j++)
			{
				int []tempstr=new int[1];
				mirroredImg.getRGB(tempstr, 0, 1, (j*width)/newWidth,
						(i*height)/newHeight, 1, 1);
				rgbData[track++]=tempstr[0];
			}
		}
		try
		{
			newImage=Image.createRGBImage(rgbData, newWidth, newHeight, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return newImage;
	}
    /**
     * Creates an Image 1pixel wide and with the specified height with the
     * provided properties. The image is created with a base color specified.
     * The Image is creeated with an opacity that varies over the whole height
     * of the image. The opacity starts at the top and ends at the bottom but
     * can be reversed by setting the reverseOpacity parameter to true.
     * @param width the width of the generated image
     * @param height the height of the Image
     * @param color the base color of the Image
     * @param startOpacity the opacity of the starting point. Should be in the 
     * range 0 to 255 inclusive. 0 for total transparency and 255 for total opacity
     * @param endOpacity the opacity of the end point. Should be in the 
     * range 0 to 255 inclusive. 0 for total transparency and 255 for total opacity
     * @param style the style for the opacity. This is the parameter that is used to 
     * denote the lighting to be used and should be one of the values 
     * ImageFactory.LIGHT_TOP,ImageFactory.LIGHT_BOTTOM, ImageFactory.FRONT, ImageFactory.LIGHT_BEHIND 
     * @param shading states whether shading should take place for this gradient.
     * opacity at the bottom instead of top.
     * @param curvature the amount of curvature to apply when generating a gradient with round edges.
     * To generate a texture with no round edges, pass a value of 0 for this parameter. The curvature
     * is applied only for the left side of the image and its upon you to do a mirror transformation
     * @return the created Image
     */
    public Image createTextureImage(int width,int height,int color,
            int startOpacity, int endOpacity, int style,boolean shading,int curvature)
    {
        //Initialize the CRC table only if this method is invoked
        //Free up memory fast
        Runtime.getRuntime().gc();
        Image testImage=Image.createImage(width, height);
        Graphics g=testImage.getGraphics();
        g.setColor(0xff0000);
        g.fillRoundRect(0, 0, testImage.getWidth(), testImage.getHeight(), 
        		curvature, 
        		curvature);
        Image testImg=Image.createImage(1,1);
        Graphics gTst=testImg.getGraphics();
        gTst.setColor(0xff0000);
        gTst.fillRect(0, 0, 1, 1);
        int []testOp=new int[1];
        testImg.getRGB(testOp, 0, 1, 0, 0, 1, 1);
        /*
            //Calculate the size in bytes of the data for this Image
            int size=8;//Add the first signature bytes for PNG image
            //Creating a chunk
            -Create first 4 bytes for the unsigned integer giving the
         *number of bytes in the chunk data field.
         *-Create a sequence of 4 bytes defining the chunk type resticted
         *to the values 65 to 90 and 97 to 122 for A-Z and a-z.Bit 5(32)
         *in every byte is used as a property bit. Bit 5 is 0 for uppercase
         *and 1 for lowercase. These bits are automatically set accordingly
         *depemding on the case of the letter used in the chunk name. Use
         *uppercase for public and compulsory chunks.
         *The IHDR 4 bytes consist of the values 73 72 68 82, The data byte
         *sequence for this chunk have the following functions
         *Width               4 bytes
         *Height              4 bytes
         *Bit depth           1 byte --valid values are 1, 2, 4, 8, and 16 (8 or 16)
         *Colour type         1 byte --valid values are 0, 2, 3, 4, and 6 (6)
         *Compression method  1 byte --valid values 0 only for this spec.
         *Filter method       1 byte --valid values 0,1,2,3,4 (0)indicates no filtering
         *Interlace method    1 byte --valid values 0 no interlace 1 adam7 (0).
         *
         *The IDAT 4 bytes consist of the values 73 68 65 84, This chunk is the
         *one that contains the actual image data. Its first 4 bytes should hence
         *be set to represent the size of the data in the image.
         *
         *The IEND 4 bytes consist of the values 73 69 78 68. This chunks data
         *field is usually empty.
         *-Create a byte sequence for the data necessary for the chunk length
         *if any.
         *-Create a 4 byte CRC value calculated on the chunk data and chunk type
         *fields using this algorithm. In PNG the 32bit CRC is initialized
         *with all 1s. This seems to take a lot of time. Le me try out without it
         *
            //Create the IHDR chunk for an image with width 120 by 120 pixels
            size+=4;//The integer for the size of the IHDR chunk.
            //The chunk has 13 bytes as specified in the comments above
            size+=4;//The IHDR chunk type field
            size+=8;//Image width and Image height int values 
            size+=5;//bitdepth,color type,compression,filter type,interlace bytes
            size+=4;//integer for the IHDR CRC value

            size+=4;//The length int for the srgb chunk
            size+=4;//the srgb chunk type field
            size+=1;//the data byte for the srgb chunk
            size+=4;//The CRC for the srgb chunk
            //Actual display data chunks starts here.

            size+=4;//The IDAT chunk length integer
            size+=4;//The IDAT chunk type bytes
            size+=3;//The three special bytes for IDAT
            size+=4;//The 4 bytes for length and length transpose
            size+=(height*4)+height;//Every pixel in the image height plus the one
            size+=4;//The Adler integer for the IDAT chunk
            //byte that comes in for every row of the image
         *
         * All the above additions summed together and performed in a single 
         * operation to provide efficient utilization of processor.
         *
            //IEND chunk
            size+=4;//IEND chunk length integer
            size+=4;//IEND 4 byte type field
            size+=4;//IEND CRC integer
         */
        int size=81;//3 bytes added due to the data chunk
        size+=height*width*4+height;//Every pixel in the image height
        byte[]data=new byte[size];
        //Fill in the Image data
        //Intreger to keep track of current index
        int track=0;
        data[0]=(byte)137;
        data[1]=(byte)80;
        data[2]=(byte)78;
        data[3]=(byte)71;
        data[4]=(byte)13;
        data[5]=(byte)10;
        data[6]=(byte)26;
        data[7]=(byte)10;
        //Update track
        track=7;
        byte []temp=getByteArrayFromInt(13);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        data[track+1]=(byte)73;
        data[track+2]=(byte)72;
        data[track+3]=(byte)68;
        data[track+4]=(byte)82;
        //Update track
        track+=4;
        //Add Image width bytes to image
        temp=getByteArrayFromInt(width);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add Image height bytes
        temp=getByteArrayFromInt(height);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add the bitdepth etc bytes
        data[track+1]=(byte)8;
        data[track+2]=(byte)6;
        data[track+3]=(byte)0;
        data[track+4]=(byte)0;
        data[track+5]=(byte)0;
        //Update track
        track+=5;
        //Calculate CRC for the IHDR chunk
        temp=getByteArrayFromInt(
        		getCRC(data, track-16, 17));
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        /* Done with the IHDR chunk*/
        //Deal with the srgb chunk
        //Add the length int for the srbgb shunk
        temp=getByteArrayFromInt(0x00000001);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add the 4 type bytes
        data[track+1]=(byte)115;
        data[track+2]=(byte)82;
        data[track+3]=(byte)71;
        data[track+4]=(byte)66;
        data[track+5]=(byte)0;//The chunk data byte
        //Update track
        track+=5;
        //Add CRC for the srgb chunk
        temp=getByteArrayFromInt(
        		getCRC(data, track-4, 5));
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Deal with IDAT chunk
        //add the length int for the idat chunk
        int length=height*width*4+height+11;
        temp=getByteArrayFromInt(length);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add the type bytes
        data[track+1]=(byte)73;
        data[track+2]=(byte)68;
        data[track+3]=(byte)65;
        data[track+4]=(byte)84;
        //Update track
        track+=4;
        //Add the actual Image data
        //Add the three unique bytes for idat chunk
        data[track+1]=(byte)8;
        data[track+2]=(byte)29;
        data[track+3]=(byte)1;
        //Update track
        track+=3;
        //Add the data length chararcter and its complement
        char len=(char)(height*width*4+height);
        char lencomp=(char)(len^0xffff);
        data[track+1]=(byte)(len&0x00ff);
        data[track+2]=(byte)((len&0xff00)>>>8);
        data[track+3]=(byte)(lencomp&0x00ff);
        data[track+4]=(byte)((lencomp&0xff00)>>>8);
        //Update track
        track+=4;
        //Set the actual apperance info depending on parameters

        int red=(color>>>16)&0x000000ff;
        int green=(color>>>8)&0x000000ff;
        int blue=color&0x000000ff;
        for(int i=0;i<height;i++)
        {
        	data[track+1]=(byte)0x0000000;//Add the filter byte
        	track+=1;
        	for(int p=0;p<width;p++)
        	{
        		int []tempstr=new int[1];
        		testImage.getRGB(tempstr, 0, 1, p,
        				i, 1, 1);
        		switch(style)
        		{
        		case LIGHT_TOP:
        		{
        			data[track+1]=(byte)(shading?((height-i)*red/height):red);//Red
        			data[track+2]=(byte)(shading?((height-i)*green/height):green);//Green
        			data[track+3]=(byte)(shading?((height-i)*blue/height):green);//Blue
        			data[track+4]=(byte)(tempstr[0]==testOp[0]?(((startOpacity>=endOpacity?startOpacity-
        					((startOpacity-endOpacity)*i)/height:startOpacity+
        					((endOpacity-startOpacity)*i)/height))):0);//Alpha
        			//Update track
        			track+=4;
        			break;
        		}
        		case LIGHT_BOTTOM:
        		{
        			data[track+1]=(byte)(shading?((i)*red/height):red);//Red
        			data[track+2]=(byte)(shading?((i)*green/height):green);//Green
        			data[track+3]=(byte)(shading?((i)*blue/height):green);//Blue
        			data[track+4]=(byte)(tempstr[0]==testOp[0]?(((startOpacity>=endOpacity?startOpacity-
        					((startOpacity-endOpacity)*i)/height:startOpacity+
        					((endOpacity-startOpacity)*i)/height))):0);//Alpha
        			//Update track
        			track+=4;
        			break;
        		}
        		case LIGHT_BEHIND:
        		{
        			data[track+1]=(byte)(shading?(i<=height/2?((height/2-i)*red*2/height):((i-height/2)*red*2/height)):red);//Red
        			data[track+2]=(byte)(shading?(i<=height/2?((height/2-i)*green*2/height):((i-height/2)*green*2/height)):green);//Green
        			data[track+3]=(byte)(shading?(i<=height/2?((height/2-i)*blue*2/height):((i-height/2)*blue*2/height)):blue);;//Blue
        			data[track+4]=(byte)(tempstr[0]==testOp[0]?(((startOpacity>=endOpacity?startOpacity-
        					((startOpacity-endOpacity)*i)/height:startOpacity+
        					((endOpacity-startOpacity)*i)/height))):0);//Alpha
        			//Update track
        			track+=4;
        			break;
        		}
        		case LIGHT_FRONT:
        		{
        			data[track+1]=(byte)(shading?(i<=height/2?((i)*red*2/height):((height-i)*red*2/height)):red);//Red
        			data[track+2]=(byte)(shading?(i<=height/2?((i)*green*2/height):((height-i)*green*2/height)):green);//Green
        			data[track+3]=(byte)(shading?(i<=height/2?((i)*blue*2/height):((height-i)*blue*2/height)):blue);//Blue
        			data[track+4]=(byte)(tempstr[0]==testOp[0]?(((startOpacity>=endOpacity?startOpacity-
        					((startOpacity-endOpacity)*i)/height:startOpacity+
        					((endOpacity-startOpacity)*i)/height))):0);//Alpha
        			//Update track
        			track+=4;
        			break;
        		}
        		default:
        		{
        			throw new IllegalArgumentException("Invalid style for Image specified");
        		}
        		}
        	}
        }
        //Add the adler value
        temp=getByteArrayFromInt(
        		getAdler(data, track-((height*width*4+height)-1), (height*width*4+height)));
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add CRC for IDAT chunk
        temp=getByteArrayFromInt(
        		getCRC(data, track-(height*width*4+height+14), height*width*4+height+15));
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        
        //Check for license

        //Deal with IEND chunk
        temp=getByteArrayFromInt(0x00000000);
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //Update track
        track+=4;
        //Add the 4 type bytes
        data[track+1]=(byte)73;
        data[track+2]=(byte)69;
        data[track+3]=(byte)78;
        data[track+4]=(byte)68;
        //Update track
        track+=4;
        //no data to add
        //Add CRC for the IEND chunk
        temp=getByteArrayFromInt(
        		getCRC(data, track-3, 4));
        for(int i=track+1;i<track+5;i++)
        {
        	data[i]=temp[i-(track+1)];
        }
        //free the crc table from memory
        crcTable=null;
        Runtime.getRuntime().gc();
        return Image.createImage(data, 0, size);
    }
    public synchronized int getCRC(byte []data,int start,int length)
    {
    	if(crcTable==null){
    		crcTable=new int[256];
            int c=0;
            int n=0,k=0;
            for(n=0;n<256;n++)
            {
                c=n;
                for(k=0;k<8;k++)
                {
                    if((c&1)!=0)
                        c=0xedb88320^(c>>>1);
                    else
                        c=c>>>1;
                }
                crcTable[n]=c;
            }
    	}
        int crc=0xffffffff;
        for(int n=start;n<start+length;n++)
        {
            crc=crcTable[(crc^data[n])&0xff]^(crc>>>8);
        }
        return crc^0xffffffff;
    }
    public byte[]getByteArrayFromInt(int value)
    {
        byte[] result=new byte[4];
        result[0]=(byte)(value>>>24);
        result[1]=(byte)(value>>>16);
        result[2]=(byte)(value>>>8);
        result[3]=(byte)value;
        return result;
    }
    private int getAdler(byte[] data,int start,int length)
    {
        final int BASE=65521;
        int s1=0x00000001;
        int s2=0x00000000;
        for(int n=start;n<length+start;n++)
        {
            s1=(s1+(((int)data[n])&0x000000ff))%BASE;
            s2=(s1+s2)%BASE;
        }
        return (s2<<16) + s1;
    }
}
