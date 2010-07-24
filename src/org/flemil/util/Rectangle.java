package org.flemil.util;

/**
 *Class that represents a rectangular or square area of display or anything else
 * of interest with a rectangular shape.
 * @author Solomon Kariri
 */
public class Rectangle 
{
	/**
	 * The x coordinate of the top left cornar of this Rectangle. Default is 0
	 */
    public int x;
    /**
     * The y coordinate of the to left corner of this Rectangle. Default is 0
     */
    public int y;
    /**
     * The width of this Rectangle in pixels. Default value is 1 to avoid division 
     * by 0 in calculations
     */
    public int width=1;
    /**
     * The height if this Rectangle in pixels.  Default value is 1 to avoid division 
     * by 0 in calculations
     */
    public int height=1;
    
    /**
     * Creates a new Rectangle instance with default values
     */
    public Rectangle()
    {
    	
    }
    /**
     * Creates a new Rectangle with the passed in properties
     * @param x the x coordinate for the top left corner of the generated Rectangle
     * @param y the y coordinate for the top left corner of the generated Rectangle
     * @param width the width in pixels of the generated Rectangle
     * @param height the height in pixels of the generated Rectangle
     */
    public Rectangle(int x,int y,int width,int height)
    {
    	this.x=x;
    	this.y=y;
    	this.width=width;
    	this.height=height;
    }
    public Rectangle(Rectangle rect) {
    	this(rect.x,rect.y,rect.width,rect.height);
	}
	/**
     * Checks whether the given coordinate falls within this Rectangles area 
     * leaving a margin of gap pixels on all sides of this Rectangle
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @param gap the gap/ margin in pixels to be left on all sides of the rectangle
     *  before the check
     * @return true if the coordinate is contained within this Rectangle and false otherwise
     */
    public boolean contains(int x,int y,int gap)
    {
        //check whether the x point falls within this rectangles xwise xtreems
        if(x>=(this.x+gap) && x<=this.x+this.width-(gap*2))
        {
            //if it does then check for the ywise xtreeems too
            if(y>=this.y+gap && y<=this.y+this.height-(gap*2))
            {
                //if it does then the point is contained within this rectangle
                return true;
            }
        }
        //if it doesnt then return false
        return false;
    }
    /**
     * Checks if the rectangle passed in the parameter intersects with this rectangle.
     * For the rectangle to be counted as intersecting with this rectangle, there must be
     * a rectangle of length and width at least one pixel so that the intersection rectangle
     * has no dimension with zero value in absolute.
     * @param rect the rectangle to be checked for intersection with this rectangle
     * @return true if it intersects and false otherwise
     */
    private boolean intersectsWith(Rectangle rect)
    {
        /*For this all we need to do is check that that a single point of the
        * rectangle is contained with a gap of at least one pixel. If it is then
        * there must be an intersection between the two
        */
        if(!(this.contains(rect.x, rect.y,0)||
                this.contains(rect.x, rect.y+rect.height,0)||
                this.contains(rect.x+rect.width, rect.y+rect.height,0)||
                this.contains(rect.x+rect.width, rect.y,0)))
        {
            if(!(rect.contains(this.x, this.y,0)||
                rect.contains(this.x, this.y+this.height,0)||
                rect.contains(this.x+this.width, this.y+this.height,0)||
                rect.contains(this.x+this.width, this.y,0)))
            {
            	if(!(this.x>rect.x && this.x+this.width<rect.x+rect.width &&
            			this.y<rect.y && this.y+this.height>rect.y+rect.height))
            	{
            		return (this.x<rect.x && this.x+this.width>rect.x+rect.width &&
            				this.y>rect.y && this.y+this.height<rect.y+rect.height);
            	}
            }
        }
        return true;
    }
    /**
     * Calculates the size and location of the intersection between this 
     * Rectangle and the one passed to it
     * @param rect the intersection of the two Rectangle. If not intersection occurs, this
     * method returns null.
     * @return the intersection of this Rectangle with the passed in Rectangle
     */
    public Rectangle calculateIntersection(Rectangle rect)
    {
        //first we check wheteher the two rects intersect, if not then we return
        //null
        if(!intersectsWith(rect))
        {
            return null;
        }
        //if it intersects then we check for the set of points that intersect
        //and calculate the intersection rect for return
        Rectangle intersect=new Rectangle();
        //boolean to indicate whether intersection was found in first parse
        boolean set=false;
        //First we check for the xwise intersection
        if(this.contains(rect.x, rect.y, 0)||
                this.contains(rect.x, rect.y+rect.height, 0))
        {
            set=true;
            intersect.x=rect.x;
            //if the other x end is also conatined we assign width else
            //we assin the deduction
            intersect.width=(this.x+this.width>rect.x+rect.width)?rect.width:
                (this.x+this.width)-intersect.x;
        }
        else if(this.contains(rect.x+rect.width, rect.y, 0)||
                this.contains(rect.x+rect.width, rect.y+rect.height, 0))
        {
            set=true;
            intersect.x=this.x;
            //since rects leftmost is not contained we simply calculate and assign the difference
            intersect.width=rect.x+rect.width-this.x;
        }

        //then check for the ywise intersection
        if(this.contains(rect.x, rect.y, 0)||
                this.contains(rect.x+rect.width, rect.y, 0))
        {
            set=true;
            intersect.y=rect.y;
            //if the other y end is also conatined we assign height to rects height else
            //we assin the deduction
            intersect.height=(this.y+this.height>rect.y+rect.height)?rect.height:
                (this.y+this.height)-rect.y;
        }
        else if(this.contains(rect.x, rect.y+rect.height, 0)||
                this.contains(rect.x+rect.width, rect.y+rect.height, 0))
        {
            set=true;
            intersect.y=this.y;
            //since rects topmost is not contained we simply calculate and assign the difference
            intersect.height=rect.y+rect.height-this.y;
        }
        //if it was not found we do a counter check on the other rect flemilar to this one
        if(set==false)
        {
        	if(this.x>rect.x && this.x+this.width<rect.x+rect.width &&
        			this.y<rect.y && this.y+this.height>rect.y+rect.height)
        	{
        		set=true;
        		intersect.x=this.x;
        		intersect.y=rect.y;
        		intersect.width=this.width;
        		intersect.height=rect.height;
        		return intersect;
        	}
        	else if(this.x<rect.x && this.x+this.width>rect.x+rect.width &&
    				this.y>rect.y && this.y+this.height<rect.y+rect.height)
        	{
        		set=true;
        		intersect.x=rect.x;
        		intersect.y=this.y;
        		intersect.width=rect.width;
        		intersect.height=this.height;
        		return intersect;
        	}
            return rect.calculateIntersection(this);
        }
        //if intersect has any domension with zero magnitude, then we rutrn null;
        if(intersect.width==0 || intersect.height==0)
        {
            return null;
        }
        //else we return the intersect rect
        return intersect;
    }
    /**
     * Checks whether the passed in Rectangle is equal to this Rectangle. for the  
     * two Rectangles to be considered equal they should have equal values for all 
     * the properties, that is equal x,y,width and height values 
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Rectangle)
        {
            Rectangle tmp2=(Rectangle)obj;
            return tmp2.x==this.x && tmp2.y==this.y &&
                    tmp2.width==this.width && tmp2.height==this.height;
        }
        return false;
    }
    /**
     * Returns a String representation of this Rectangle. The returned String has the format
     * '{x,y,width,height}' 
     */
    public String toString()
    {
    	return "{"+this.x+","+this.y+","+this.width+","+this.height+"}";
    }
}