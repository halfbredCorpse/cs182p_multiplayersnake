import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    private final int B_WIDTH = 300,
                      B_HEIGHT = 300,
                      DOT_SIZE = 10,
                      ALL_DOTS = 900,
                      RAND_POS = 29,
                      DELAY = 140;
    
    private final int[] x = new int[ALL_DOTS],
                        y = new int[ALL_DOTS];
    
    private int dots, apple_x, apple_y;
    
    private boolean leftDirection = false,
                    rightDirection = true,
                    upDirection = false,
                    downDirection = false,
                    inGame = true;
    
    private Timer timer;
    private Image ball, apple, head;
    
    public Board() {
        initComponents();
        
        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);
        
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Board().setVisible(true);
            }
        });
    }
    
    private void loadImages() {
        ImageIcon imageDot = new ImageIcon("dot.png");
        ball = imageDot.getImage();
        
        ImageIcon imageApple = new ImageIcon("apple.png");
        apple = imageApple.getImage();
        
        ImageIcon imageHead = new ImageIcon("head.png");
        head = imageHead.getImage();
    }
    
    private void initGame() {
        dots = 3;
        
        for (int i = 0; i < dots; i++) {
            x[i] = 50 - i*10;
            y[i] = 50;
        }
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        doDrawing(graphics);
    }
    
    public void doDrawing(Graphics graphics) {
        if (inGame) {
            graphics.drawImage(apple, apple_x, apple_y, this);
            
            for (int i = 0; i < dots;  i++) {
                if (i == 0)
                    graphics.drawImage(head, x[i], y[i], this);
                else
                    graphics.drawImage(ball, x[i], y[i], this);
            }
            
            Toolkit.getDefaultToolkit().sync();
        }
        else
            gameOver(graphics);
    }
    
    private void gameOver(Graphics graphics) {
        String message = "Game Over";
        Font small = new Font("Hellvetica", Font.BOLD, 14);
        FontMetrics metric = getFontMetrics(small);
        
        graphics.setColor(Color.white);
        graphics.setFont(small);
        graphics.drawString(message, (B_WIDTH - metric.stringWidth(message))/2, B_HEIGHT/2);
    }
    
    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
        }
    }
    
    private void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        if (leftDirection)
            x[0] -= DOT_SIZE;
        
        if (rightDirection)
            x[0] += DOT_SIZE;
        
        if (upDirection)
            y[0] += DOT_SIZE;
        
        if (downDirection)
            y[0] -= DOT_SIZE;
    }
    
    private void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i]))
                inGame = false;
        }
        
        if (((y[0] >= B_HEIGHT) | (y[0] < 0)) | ((x[0] >= B_WIDTH) | (x[0] < 0)))
            inGame = false;
        
        if (!inGame)
            timer.stop();
    }
    
    private void locateApple() {
        int r = 
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
