package circleCloud;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CircleAnimation2 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("3D Circle Orbit Animation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setResizable(false);

            CircleOrbitPanel2 panel = new CircleOrbitPanel2();
            frame.add(panel);
            frame.setVisible(true);
            new Thread(panel::startAnimation).start();
        });
    }
}

class CircleOrbitPanel2 extends JPanel {
	
    private final List<Circle> circles;
    private final int centerX, centerY;
    private final int circleCount = 50;
    private int speedCounter = 0;
    private boolean faster = true;
    public double SPEED = 0.02;

    public CircleOrbitPanel2() {
        this.setBackground(Color.BLACK);
        this.centerX = 400;
        this.centerY = 400;
        this.circles = new ArrayList<>();
        
        // Initialize circles with random angles and heights
        for (int i = 0; i < circleCount; i++) {
            double angle = Math.random() * 2 * Math.PI;
            int initialHeight = centerY + (int) (Math.random() * 400 - 200); // Random height variation
            circles.add(new Circle(angle, initialHeight));
        }
    }

    public void startAnimation() {
        while (true) {
            for (Circle circle : circles) {
                circle.update();
            }
            repaint();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        speedCounter++;
        if (faster) {
        	SPEED = SPEED - 0.0041;
        } else {
        	SPEED = SPEED + 0.004;
        }
        if (speedCounter == 25) {
        	speedCounter=0;
        	if (faster) {
        		faster = false;
        	} else {
        		faster = true;
        	}
        }

        for (Circle circle : circles) {
        	circle.SPEED = SPEED;
            int size = (int) circle.getSize();
            int x = (int) (centerX + circle.getX() - size / 2);
            int y = (int) (circle.getY() - size / 2);
            g2d.setColor(circle.getColor());
            g2d.fillOval(x, y, size, size);
        }
    }

    private class Circle {
        private static final double MAX_RADIUS = 300;
        public double SPEED = 0.02;

        private static final int BASE_SIZE = 25;
        private static final int HEIGHT_CHANGE_DELAY = 360;

        private double angle;
        private int orbitHeight;
        private int heightChangeCounter;

        public Circle(double initialAngle, int initialHeight) {
            this.angle = initialAngle;
            this.orbitHeight = initialHeight;
            this.heightChangeCounter = 0;
        }

        public void update() {
            angle += SPEED;
            if (angle > 2 * Math.PI) {
                angle -= 2 * Math.PI;
                heightChangeCounter++;
                if (heightChangeCounter >= HEIGHT_CHANGE_DELAY / (2 * Math.PI / SPEED)) {
                    changeHeight();
                    heightChangeCounter = 0;
                }
            }
        
        }

        public double getX() {
            double radius = MAX_RADIUS * Math.cos(angle);
            double x = centerX + radius;

            // Ensure the x-coordinate is within bounds
            if (x - BASE_SIZE / 2 < 0 || x + BASE_SIZE / 2 > getWidth()) {
                radius = Math.min(radius, getWidth() / 2 - BASE_SIZE / 2);
                radius = Math.max(radius, -getWidth() / 2 + BASE_SIZE / 2);
            }

            return radius;
        }

        public double getY() {
            // The entire orbit's vertical position depends on orbitHeight
            double orbitY = MAX_RADIUS * Math.sin(angle) * 0.5; // Simulates 3D perspective
            double y = orbitHeight + orbitY;

            // Ensure the y-coordinate is within bounds
            if (y - BASE_SIZE / 2 < 0 || y + BASE_SIZE / 2 > getHeight()) {
                orbitHeight = Math.min(orbitHeight, getHeight() - BASE_SIZE / 2);
                orbitHeight = Math.max(orbitHeight, BASE_SIZE / 2);
            }

            return y;
        }

        public double getSize() {
            return BASE_SIZE * (1 - Math.abs(Math.sin(angle)) * 0.3); // Simulates size change
        }

        public Color getColor() {
            // Brightness now changes with the angle. The color is brightest at 0 and 2π, and darkest at π.
            float brightness = (float) (Math.cos(angle - Math.PI*0.5) * 0.3 + 0.7); // This makes the color darken at 180° (π)
            return new Color(
                (int) (brightness * 135),
                (int) (brightness * 206),
                (int) (brightness * 235));
        }
        private void changeHeight() {
            int direction = Math.random() > 0.5 ? 1 : -1;
            int newHeight = orbitHeight + direction * (50 + (int) (Math.random() * 100));

            // Ensure the new height is within bounds
            orbitHeight = Math.max(BASE_SIZE, Math.min(newHeight, getHeight() - BASE_SIZE));
        }
    }
}
