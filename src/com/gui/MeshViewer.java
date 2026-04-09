package com.gui;

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
 * 
 * @author Zach Brinton and GPT 5.4
 * @version 4-9-26
 */
public class MeshViewer extends JPanel {
	private static final long serialVersionUID = 1L;

	private final Mesh mesh;

    private float rotX = 0.4f;
    private float rotY = 0.4f;
    private float zoom = 400f;

    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging = false;

    public MeshViewer(Mesh mesh) {
        this.mesh = mesh;
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!dragging) return;

                int dx = e.getX() - lastMouseX;
                int dy = e.getY() - lastMouseY;

                rotY += dx * 0.01f;
                rotX += dy * 0.01f;

                lastMouseX = e.getX();
                lastMouseY = e.getY();

                repaint();
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

        g2.setColor(Color.BLACK);

        for (Triangle tri : mesh.triangles()) {
            Point p1 = project(rotate(tri.v1), w, h);
            Point p2 = project(rotate(tri.v2), w, h);
            Point p3 = project(rotate(tri.v3), w, h);

            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            g2.drawLine(p2.x, p2.y, p3.x, p3.y);
            g2.drawLine(p3.x, p3.y, p1.x, p1.y);
        }
    }

    private Vertex rotate(Vertex v) {
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

    private Point project(Vertex v, int width, int height) {
        float cameraDist = 800f;
        float scale = zoom / (cameraDist + v.z);

        int sx = (int) (width / 2f + v.x * scale);
        int sy = (int) (height / 2f - v.y * scale);

        return new Point(sx, sy);
    }

    public static void show(Mesh mesh) {
        JFrame frame = new JFrame("Mesh Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new MeshViewer(mesh));
        frame.setVisible(true);
    }
    
    public static void show(File file) throws IOException {
    	Mesh mesh = STLParser.parse(file);
    	
        JFrame frame = new JFrame("Mesh Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new MeshViewer(mesh));
        frame.setVisible(true);
    }
}