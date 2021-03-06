/*
  Copyright 2006 by Sean Luke and George Mason University
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package sim.portrayal3d.simple;

import sim.portrayal3d.*;
import javax.media.j3d.*;
import sim.portrayal.*;
import java.awt.*;

/**
   A simple portrayal for displaying Shape3D objects.  You can find Shape3D objects,
   or CompressedGeometry objects (which you can make into a Shape3D in its constructor)
   all over the web.
   
   <p><b>Important note: CompressedGeometry cannot have any appearances set: it ignores all of them
   and only uses what's defined in the geometry itself. That's Java3D for you.</b>
   
   <p>Some examples (be sure to <tt>import javax.media.j3d.*; import com.sun.j3d.utils.geometry.*; import java.awt.Font.*;</tt>)
   
   <ul>
   <li>A seagull comes with MASON: <tt>new Shape3DPortrayal3D(new Shape3D(new sim.app.crowd3d.GullCG()));</tt>
   <li>A box with six colored sides: <tt>new Shape3DPortrayal3D(new ColorCube());</tt>
   <li>Some 3D text: <tt>new Shape3DPortrayal3D(new Shape3D(new Text3D(new Font3D(new Font("SansSerif", Font.PLAIN, 9), new FontExtrusion()), "Hello, World!")));</tt>
   </ul>
*/


public class Shape3DPortrayal3D extends PrimitivePortrayal3D
    {
    /** Constructs a Shape3DPortrayal3D with the given shape and a default (flat opaque white) appearance. */
    public Shape3DPortrayal3D(Shape3D shape)
        {
        this(shape,Color.white);
        }

    /** Constructs a Shape3DPortrayal3D  with the given shape and a flat opaque appearance of the given color. */
    public Shape3DPortrayal3D(Shape3D shape, Color color)
        {
        this(shape,appearanceForColor(color));
        }

    /** Constructs a Shape3DPortrayal3D with the given shape and (opaque) image. */
    public Shape3DPortrayal3D(Shape3D shape, Image image)
        {
        this(shape,appearanceForImage(image,true));
        }

    /** Constructs a Shape3DPortrayal3D with the given shape and appearance. */
    public Shape3DPortrayal3D(Shape3D shape, Appearance appearance)
        {
        this.appearance = appearance;
        shape = (Shape3D)(shape.cloneTree(true));  // force a true copy
                
        Geometry g = shape.getGeometry();
        if (g instanceof CompressedGeometry)
            ((CompressedGeometry)g).setCapability(CompressedGeometry.ALLOW_GEOMETRY_READ);

        setShape3DFlags(shape);
        shape.setAppearance(appearance);
        group = shape;
        }

    /** Constructs a Shape3DPortrayal3D with the given geometry and a default (flat opaque white) appearance. */
    public Shape3DPortrayal3D(Geometry geometry)
        {
        this(geometry,Color.white);
        }

    /** Constructs a Shape3DPortrayal3D  with the given geometry and a flat opaque appearance of the given color. */
    public Shape3DPortrayal3D(Geometry geometry, Color color)
        {
        this(geometry,appearanceForColor(color));
        }

    /** Constructs a Shape3DPortrayal3D with the given geometry and (opaque) image. */
    public Shape3DPortrayal3D(Geometry geometry, Image image)
        {
        this(geometry,appearanceForImage(image,true));
        }

    /** Constructs a Shape3DPortrayal3D with the given geometry and appearance. */
    public Shape3DPortrayal3D(Geometry geometry, Appearance appearance)
        {
        this(new Shape3D(geometry), appearance);
        }
              
    protected int numShapes() { return 1; }
        
    // we always just return the shape no matter what
    protected Shape3D getShape(TransformGroup j3dModel, int shapeNumber)
        {
        Node n = j3dModel;
        while(n instanceof TransformGroup)
            n = ((TransformGroup)n).getChild(0);
        Shape3D p = (Shape3D)n;
        return p;
        }
    }
