
package com.debug;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.geometry.Triangle;
import com.geometry.Vertex;
import com.model.Mesh;
import com.model.STLParser;

/**
 * A simple Swing component that renders an STL mesh and provides several
 * debugging modes useful for 3D printing.  Users can rotate, zoom and pan
 * the mesh with the mouse.  Optional overlays include per‐triangle normal
 * vectors, axis guides, the mesh bounding box and highlighting of inverted
 * normals (faces pointing "inward").  A hover mode may also be enabled to
 * display the coordinates of the triangle under the cursor along with its
 * normal vector.
 *
 * <p>The implementation strives to keep methods short and focused on a single
 * responsibility.  Complex drawing logic is factored into helper methods and
 * all public API is documented.  See {@link #show(Mesh, String)} for a
 * convenient way to open a frame with UI controls.</p>
 *
 * @author Zach Brinton and GPT 5.4
 * @version 4-18-26
 */
public class MeshViewer extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Mesh mesh;

    private float rotX = 0.4f;
    private float rotY = 0.4f;
    private float zoom = 400f;
    
    private int offsetX = 0;
    private int offsetY = 0;
    /**
     * Precomputed center of the mesh used as the pivot for rotations.  The
     * center is the midpoint of the mesh's axis‐aligned bounding box.
     */
    private final Vertex meshCenter;
    /**
     * Corners of the mesh's axis aligned bounding box.  Used for drawing
     * the bounding box overlay.
     */
    private final Vertex[] boundingBoxCorners;
    /**
     * The largest dimension of the mesh's bounding box.  Used to scale
     * normal vectors and axis lines to a reasonable visible length.
     */
    private final float maxDimension;
    /**
     * Precomputed scale factor for drawing normals.  This is a small
     * fraction of {@link #maxDimension} so that normals are visible but
     * unobtrusive.
     */
    private final float normalScale;

    private int lastMouseX;
    private int lastMouseY;
    private boolean primaryDragging = false;
    private boolean middleDragging = false; 

    // ------------------------------------------------------------------------
    // Debugging toggles.  These flags are mutated via UI controls created
    // in {@link #show(Mesh, String)}.  The defaults leave all overlays
    // disabled so the viewer behaves like a simple mesh viewer.
    /** Whether to draw a line representing the normal vector of each triangle. */
    private boolean showNormals = false;
    /** Whether to show a tooltip containing the vertices and normal of the triangle under the cursor. */
    private boolean showHoverInfo = false;
    /** Whether to draw the axis aligned bounding box around the mesh. */
    private boolean showBoundingBox = false;
    /** Whether to draw small axis guides (X, Y, Z) anchored at the mesh center. */
    private boolean showAxes = false;
    /** Whether to highlight triangles with inverted normals (negative Z in object space). */
    private boolean highlightInverted = false;

    /** The triangle currently under the cursor when hover information is enabled. */
    private Triangle hoveredTriangle = null;

    public MeshViewer(Mesh mesh) {
        this.mesh = mesh;
        
        // Compute the axis aligned bounding box of the mesh.  This is used
        // to determine the rotation pivot (meshCenter), define the corners
        // for the bounding box overlay and compute the maximum dimension.
        float minX = Float.POSITIVE_INFINITY, maxX = Float.NEGATIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;
        for (Triangle tri : mesh.triangles()) {
            for (Vertex v : new Vertex[]{tri.v1, tri.v2, tri.v3}) {
                if (v.x < minX) minX = v.x;
                if (v.x > maxX) maxX = v.x;
                if (v.y < minY) minY = v.y;
                if (v.y > maxY) maxY = v.y;
                if (v.z < minZ) minZ = v.z;
                if (v.z > maxZ) maxZ = v.z;
            }
        }
        // Pivot at the center of the bounding box for intuitive rotation.
        meshCenter = new Vertex((minX + maxX) / 2f,
                                (minY + maxY) / 2f,
                                (minZ + maxZ) / 2f);
        // Precompute the eight corners of the bounding box for drawing.
        boundingBoxCorners = new Vertex[] {
                new Vertex(minX, minY, minZ), new Vertex(maxX, minY, minZ),
                new Vertex(maxX, maxY, minZ), new Vertex(minX, maxY, minZ),
                new Vertex(minX, minY, maxZ), new Vertex(maxX, minY, maxZ),
                new Vertex(maxX, maxY, maxZ), new Vertex(minX, maxY, maxZ)
        };
        // Determine the largest side of the bounding box to scale normals and axes.
        float sizeX = maxX - minX;
        float sizeY = maxY - minY;
        float sizeZ = maxZ - minZ;
        maxDimension = Math.max(sizeX, Math.max(sizeY, sizeZ));
        // Normal scale is a small portion of the maximum dimension so that
        // normals remain visible without dominating the drawing.  The value
        // can be tuned if needed.
        normalScale = maxDimension * 0.05f;

        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    middleDragging = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    middleDragging = false;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!middleDragging) return;

                offsetX += e.getX() - lastMouseX;
                offsetY += e.getY() - lastMouseY;

                lastMouseX = e.getX();
                lastMouseY = e.getY();
                
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Only start primary (rotation) dragging on left mouse button.
                if (e.getButton() == MouseEvent.BUTTON1) {
                    primaryDragging = true;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    primaryDragging = false;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Rotate only while the left button is pressed.
                if (!primaryDragging) return;
                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;
                rotY += dx * 0.01f;
                rotX += dy * 0.01f;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                repaint();
            }
        });

        // Track mouse movement for hover information.  When hover info mode is
        // enabled, the triangle under the cursor will be detected and its
        // vertices and normal will be displayed as a tooltip.
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (showHoverInfo) {
                    Triangle tri = findTriangleAt(e.getX(), e.getY());
                    if (tri != hoveredTriangle) {
                        hoveredTriangle = tri;
                        if (tri != null) {
                            setToolTipText(formatHoverText(tri));
                        } else {
                            setToolTipText(null);
                        }
                    }
                } else {
                    // Disable tooltips when hover info is disabled
                    if (hoveredTriangle != null) {
                        hoveredTriangle = null;
                        setToolTipText(null);
                    }
                }
            }
        });

        addMouseWheelListener(e -> {
            zoom *= (float) Math.pow(1.1, -e.getPreciseWheelRotation());
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = getWidth();
        int h = getHeight();

        // Draw each triangle with optional highlighting and normal display.
        for (Triangle tri : mesh.triangles()) {
            drawTriangle(g2, tri, w, h);
        }
        // Optionally draw bounding box overlay.
        if (showBoundingBox) {
            drawBoundingBox(g2, w, h);
        }
        // Optionally draw axis guides.
        if (showAxes) {
            drawAxes(g2, w, h);
        }
    }
    
    private Vertex rotate(Vertex v) {
        float x = v.x - meshCenter.x;
        float y = v.y - meshCenter.y;
        float z = v.z - meshCenter.z;

        float cosX = (float) Math.cos(rotX);
        float sinX = (float) Math.sin(rotX);
        float y1 = y * cosX - z * sinX;
        float z1 = y * sinX + z * cosX;

        float cosY = (float) Math.cos(rotY);
        float sinY = (float) Math.sin(rotY);
        float x2 = x * cosY + z1 * sinY;
        float z2 = -x * sinY + z1 * cosY;

        return new Vertex(x2, y1, z2);
    }

    private Point project(Vertex v, int width, int height) {
        float cameraDist = 800f;
        float scale = zoom / (cameraDist + v.z);

        int sx = (int) (width / 2f + v.x * scale);
        int sy = (int) (height / 2f - v.y * scale);

        return new Point(sx, sy);
    }

    /**
     * Rotate a direction vector by the current rotation without translating
     * around the mesh center.  Normals and axis directions should be passed
     * through this method so that only their orientation changes.
     *
     * @param v the direction vector in object coordinates
     * @return the rotated direction vector
     */
    private Vertex rotateDirection(Vertex v) {
        float x = v.x;
        float y = v.y;
        float z = v.z;
        // rotate around X
        float cosX = (float) Math.cos(rotX);
        float sinX = (float) Math.sin(rotX);
        float y1 = y * cosX - z * sinX;
        float z1 = y * sinX + z * cosX;
        // rotate around Y
        float cosY = (float) Math.cos(rotY);
        float sinY = (float) Math.sin(rotY);
        float x2 = x * cosY + z1 * sinY;
        float z2 = -x * sinY + z1 * cosY;
        return new Vertex(x2, y1, z2);
    }

    /**
     * Draw a single triangle.  This method handles projecting vertices,
     * applying pan offset, optional highlighting of inverted normals and
     * drawing the triangle's normal if enabled.  Keeping this logic in
     * a helper method keeps {@link #paintComponent(Graphics)} concise.
     *
     * @param g2 the graphics context
     * @param tri the triangle to draw
     * @param w component width
     * @param h component height
     */
    private void drawTriangle(Graphics2D g2, Triangle tri, int w, int h) {
        // Compute screen coordinates for each vertex
        Point p1 = project(rotate(tri.v1), w, h);
        Point p2 = project(rotate(tri.v2), w, h);
        Point p3 = project(rotate(tri.v3), w, h);
        // Apply pan offset
        p1.translate(offsetX, offsetY);
        p2.translate(offsetX, offsetY);
        p3.translate(offsetX, offsetY);
        // Highlight inverted normals if requested
        if (highlightInverted) {
            Vertex dir = rotateDirection(tri.normal);
            if (dir.z < 0) {
                // Create a polygon and fill with semi-transparent red
                int[] xs = {p1.x, p2.x, p3.x};
                int[] ys = {p1.y, p2.y, p3.y};
                g2.setColor(new Color(255, 0, 0, 64));
                g2.fillPolygon(xs, ys, 3);
            }
        }
        // Draw triangle edges
        g2.setColor(Color.BLACK);
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        g2.drawLine(p2.x, p2.y, p3.x, p3.y);
        g2.drawLine(p3.x, p3.y, p1.x, p1.y);
        // Draw normal if enabled
        if (showNormals) {
            drawNormal(g2, tri, w, h);
        }
    }

    /**
     * Draw the normal vector for the given triangle.  The normal is drawn as
     * a line originating at the triangle's centroid and extending in the
     * direction of the normal.  The length of the line is scaled by
     * {@link #normalScale}.
     *
     * @param g2 the graphics context
     * @param tri the triangle whose normal to draw
     * @param w component width
     * @param h component height
     */
    private void drawNormal(Graphics2D g2, Triangle tri, int w, int h) {
        // Compute the centroid of the triangle in object space
        Vertex centroid = new Vertex(
                (tri.v1.x + tri.v2.x + tri.v3.x) / 3f,
                (tri.v1.y + tri.v2.y + tri.v3.y) / 3f,
                (tri.v1.z + tri.v2.z + tri.v3.z) / 3f);
        // Rotate the centroid and the normal direction
        Vertex centroidRotated = rotate(centroid);
        Vertex dirRotated = rotateDirection(tri.normal);
        // Create end point by adding scaled normal direction
        Vertex end = new Vertex(
                centroidRotated.x + dirRotated.x * normalScale,
                centroidRotated.y + dirRotated.y * normalScale,
                centroidRotated.z + dirRotated.z * normalScale);
        // Project to screen coordinates and apply pan
        Point pCentroid = project(centroidRotated, w, h);
        Point pEnd = project(end, w, h);
        pCentroid.translate(offsetX, offsetY);
        pEnd.translate(offsetX, offsetY);
        // Draw the normal line in blue
        g2.setColor(Color.MAGENTA);
        g2.drawLine(pCentroid.x, pCentroid.y, pEnd.x, pEnd.y);
    }

    /**
     * Draw the axis aligned bounding box around the mesh.  The box edges
     * are drawn in a semi-transparent blue so they do not obscure the mesh.
     *
     * @param g2 the graphics context
     * @param w component width
     * @param h component height
     */
    private void drawBoundingBox(Graphics2D g2, int w, int h) {
        // Pairs of indices defining the twelve box edges
        int[][] edges = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };
        g2.setColor(new Color(0, 0, 255, 128));
        for (int[] edge : edges) {
            Vertex v1 = boundingBoxCorners[edge[0]];
            Vertex v2 = boundingBoxCorners[edge[1]];
            Point p1 = project(rotate(v1), w, h);
            Point p2 = project(rotate(v2), w, h);
            p1.translate(offsetX, offsetY);
            p2.translate(offsetX, offsetY);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }

    /**
     * Draw the X, Y and Z axes centred at the mesh's centre.  Axis lines are
     * drawn in red, green and blue respectively.  The length is based on
     * half of {@link #maxDimension} so that the axes are proportional to the
     * model size.
     *
     * @param g2 the graphics context
     * @param w component width
     * @param h component height
     */
    private void drawAxes(Graphics2D g2, int w, int h) {
        float axisLength = maxDimension * 0.5f;
        // Origin at mesh centre after rotation is (0,0,0)
        Point origin = project(new Vertex(0, 0, 0), w, h);
        origin.translate(offsetX, offsetY);
        // X axis
        Vertex dirX = rotateDirection(new Vertex(axisLength, 0, 0));
        Point endX = project(dirX, w, h);
        endX.translate(offsetX, offsetY);
        g2.setColor(Color.RED);
        g2.drawLine(origin.x, origin.y, endX.x, endX.y);
        // Y axis
        Vertex dirY = rotateDirection(new Vertex(0, axisLength, 0));
        Point endY = project(dirY, w, h);
        endY.translate(offsetX, offsetY);
        g2.setColor(Color.GREEN);
        g2.drawLine(origin.x, origin.y, endY.x, endY.y);
        // Z axis
        Vertex dirZ = rotateDirection(new Vertex(0, 0, axisLength));
        Point endZ = project(dirZ, w, h);
        endZ.translate(offsetX, offsetY);
        g2.setColor(Color.BLUE);
        g2.drawLine(origin.x, origin.y, endZ.x, endZ.y);
    }

    /**
     * Determine whether the given screen point lies inside the triangle defined
     * by the three screen points a, b and c.  Uses barycentric coordinates.
     *
     * @param p the point to test
     * @param a first triangle vertex
     * @param b second triangle vertex
     * @param c third triangle vertex
     * @return true if p is inside or on the edge of the triangle
     */
    private boolean pointInTriangle(Point p, Point a, Point b, Point c) {
        double denom = ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y));
        if (denom == 0) return false; // degenerate triangle
        double alpha = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / denom;
        double beta = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / denom;
        double gamma = 1.0 - alpha - beta;
        return alpha >= 0 && beta >= 0 && gamma >= 0;
    }

    /**
     * Find the first triangle under the given mouse coordinates.  The search
     * iterates over all mesh triangles and projects them to screen
     * coordinates using the current rotation and pan.  The first triangle
     * containing the point is returned.  This method is used to implement
     * hover information.
     *
     * @param mx mouse x coordinate
     * @param my mouse y coordinate
     * @return the triangle under the mouse or {@code null} if none found
     */
    private Triangle findTriangleAt(int mx, int my) {
        int w = getWidth();
        int h = getHeight();
        Point p = new Point(mx, my);
        for (Triangle tri : mesh.triangles()) {
            Point p1 = project(rotate(tri.v1), w, h);
            Point p2 = project(rotate(tri.v2), w, h);
            Point p3 = project(rotate(tri.v3), w, h);
            p1.translate(offsetX, offsetY);
            p2.translate(offsetX, offsetY);
            p3.translate(offsetX, offsetY);
            if (pointInTriangle(p, p1, p2, p3)) {
                return tri;
            }
        }
        return null;
    }

    /**
     * Format a triangle's vertices and normal into an HTML tooltip string.  The
     * normal vector and vertex coordinates are limited to three decimal
     * places for readability.  HTML is used so that line breaks render
     * correctly in Swing tooltips.
     *
     * @param tri the triangle to format
     * @return an HTML formatted string describing the triangle
     */
    private String formatHoverText(Triangle tri) {
        String fmt = "<html>Normal: (%.3f, %.3f, %.3f)<br/>"
                + "v1: (%.3f, %.3f, %.3f)<br/>"
                + "v2: (%.3f, %.3f, %.3f)<br/>"
                + "v3: (%.3f, %.3f, %.3f)</html>";
        return String.format(fmt,
                tri.normal.x, tri.normal.y, tri.normal.z,
                tri.v1.x, tri.v1.y, tri.v1.z,
                tri.v2.x, tri.v2.y, tri.v2.z,
                tri.v3.x, tri.v3.y, tri.v3.z);
    }
    
    public static void show(Mesh mesh) {
    	show(mesh, "Mesh Viewer");
    }

    public static void show(Mesh mesh, String title) {
        // Construct a frame and viewer
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        MeshViewer viewer = new MeshViewer(mesh);
        // Create UI controls for debugging modes
        javax.swing.JPanel controls = new javax.swing.JPanel();
        javax.swing.JCheckBox normalsBox = new javax.swing.JCheckBox("Show Normals");
        javax.swing.JCheckBox hoverBox = new javax.swing.JCheckBox("Hover Info");
        javax.swing.JCheckBox bboxBox = new javax.swing.JCheckBox("Bounding Box");
        javax.swing.JCheckBox axesBox = new javax.swing.JCheckBox("Axes");
        javax.swing.JCheckBox invertedBox = new javax.swing.JCheckBox("Highlight Inverted");
        controls.add(normalsBox);
        controls.add(hoverBox);
        controls.add(bboxBox);
        controls.add(axesBox);
        controls.add(invertedBox);
        // Bind checkboxes to viewer flags
        normalsBox.addActionListener(e -> {
            viewer.showNormals = normalsBox.isSelected();
            viewer.repaint();
        });
        hoverBox.addActionListener(e -> {
            viewer.showHoverInfo = hoverBox.isSelected();
            // Disable any existing tooltip when turning off
            if (!viewer.showHoverInfo) {
                viewer.hoveredTriangle = null;
                viewer.setToolTipText(null);
            }
        });
        bboxBox.addActionListener(e -> {
            viewer.showBoundingBox = bboxBox.isSelected();
            viewer.repaint();
        });
        axesBox.addActionListener(e -> {
            viewer.showAxes = axesBox.isSelected();
            viewer.repaint();
        });
        invertedBox.addActionListener(e -> {
            viewer.highlightInverted = invertedBox.isSelected();
            viewer.repaint();
        });
        // Layout the frame: viewer in center, controls at bottom
        java.awt.BorderLayout layout = new java.awt.BorderLayout();
        frame.getContentPane().setLayout(layout);
        frame.add(viewer, java.awt.BorderLayout.CENTER);
        frame.add(controls, java.awt.BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    public static void show(File file) throws IOException {
        Mesh mesh = STLParser.parse(file);
        // Delegate to the primary show method so that UI controls are added
        show(mesh, "Mesh Viewer");
    }
}
