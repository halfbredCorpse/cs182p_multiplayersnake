import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import javax.swing.*;

public class BoardOpponent extends JPanel implements ActionListener {
     private final int B_WIDTH = 300,
                      B_HEIGHT = 300,
                      DOT_SIZE = 10,
                      ALL_DOTS = 900,
                      RAND_POS = 29,
                      DELAY = 140;
    
    private final int[] x = new int[ALL_DOTS],
                        y = new int[ALL_DOTS];
    
    private int dots, apple_x, apple_y, playerStatus;
    
    private boolean leftDirection = false,
                    rightDirection = true,
                    upDirection = false,
                    downDirection = false,
                    inGame = true;
    
    private Timer timer;
    private Image ball, apple, head;
    
    static Socket socket;
    static DataInputStream input;
    
    ////////////////////////////////////////////////////////////////////////////
    public BoardOpponent() {
        try {
            socket = new Socket("localhost", 4321);

            addKeyListener(new TAdapter());
            setBackground(Color.black);

            setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
            loadImages();
            initGame();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void loadImages() {
        ImageIcon imageDot = new ImageIcon("dot.png");
        ball = imageDot.getImage();
        
        ImageIcon imageApple = new ImageIcon("apple.png");
        apple = imageApple.getImage();
        
        ImageIcon imageHead = new ImageIcon("head.png");
        head = imageHead.getImage();
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void initGame() {
        try {
            String xArray, yArray,
                   xArr[], yArr[];
            
            input = new DataInputStream(socket.getInputStream());
            dots = Integer.parseInt(input.readUTF());
            
            xArray = input.readUTF();
            xArr = xArray.split("\t");
            
            yArray = input.readUTF();
            yArr = yArray.split("\t");
            
            for (int i = 0; i < xArr.length; i++) {
                x[i] = Integer.parseInt(xArr[i]);
                y[i] = Integer.parseInt(yArr[i]);
            }
            
            input.close();            
            locateApple();
        
            timer = new Timer(DELAY, this);
            timer.start();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }        
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        doDrawing(graphics);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void gameOver(Graphics graphics) {
        String message = "Game Over. You won!";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metric = getFontMetrics(small);
        
        graphics.setColor(Color.white);
        graphics.setFont(small);
        graphics.drawString(message, (B_WIDTH - metric.stringWidth(message))/2, B_HEIGHT/2);
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void checkCollision() {
        try {
            input = new DataInputStream(socket.getInputStream());
            if (input.readUTF().equals("-1"))
                inGame = false;
            
            input.close();
            
            if (!inGame)
                timer.stop();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private void locateApple() {
        try {
            input = new DataInputStream(socket.getInputStream());
            apple_x = Integer.parseInt(input.readUTF());
            apple_y = Integer.parseInt(input.readUTF());

            input.close();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }
        
        repaint();
    }
    ////////////////////////////////////////////////////////////////////////////    
    
    
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }
}
