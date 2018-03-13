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

public class Board extends JPanel implements ActionListener {
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
    
    static ServerSocket serverSocket;
    static Socket socket;
    static DataOutputStream output;
    
    ////////////////////////////////////////////////////////////////////////////
    public Board() {    
        try {
            serverSocket = new ServerSocket(4321);
            socket = serverSocket.accept();
            output = new DataOutputStream(socket.getOutputStream());

            addKeyListener(new TAdapter());
            setBackground(Color.black);
            setFocusable(true);

            setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
            loadImages();
            initGame();
        }
        catch (Exception e) {
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
            String xArray = "", 
                   yArray = "";
            output = new DataOutputStream(socket.getOutputStream());
        
            dots = 3;

            for (int i = 0; i < dots; i++) {
                x[i] = 50 - i*10;
                xArray += Integer.toString(x[i]) + "\t";
                
                y[i] = 50;
                yArray += Integer.toString(y[i]) + "\t";
            }

            output.writeUTF(Integer.toString(dots));
            output.flush();
            
            output.writeUTF(xArray);
            output.flush();
            
            output.writeUTF(yArray);
            output.flush();
            
            output.close();
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
        String message = "Game Over. You lost!";
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
            output = new DataOutputStream(socket.getOutputStream());
            
            for (int i = dots; i > 0; i--) {
                if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                    inGame = false;
                    output.writeUTF("-1");
                    output.flush();
                }        
            }

            if (((y[0] >= B_HEIGHT) | (y[0] < 0)) | ((x[0] >= B_WIDTH) | (x[0] < 0))) {
                inGame = false;
                output.writeUTF("-1");
                output.flush();
            }
                
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
            output = new DataOutputStream(socket.getOutputStream());
            
            int r = (int)(Math.random() * RAND_POS);
            apple_x = r * DOT_SIZE;
            output.writeUTF(Integer.toString(apple_x));
            output.flush();

            r = (int)(Math.random() * RAND_POS);
            apple_y = r * DOT_SIZE;
            output.writeUTF(Integer.toString(apple_y));
            output.flush();
            
            output.close();
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
            checkApple();     //////////////////////////////////////////////////
            checkCollision(); //////////////////////////////////////////////////
            move();           //////////////////////////////////////////////////
        }
        
        repaint();
    }
    ////////////////////////////////////////////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            try {
                output = new DataOutputStream(socket.getOutputStream());
                
                if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;

                    output.writeUTF("1");
                    output.flush();
                }

                if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;

                    output.writeUTF("3");
                    output.flush();
                }

                if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                    upDirection = true;
                    leftDirection = false;
                    rightDirection = false;

                    output.writeUTF("2");
                    output.flush();
                }

                if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                    downDirection = true;
                    leftDirection = false;
                    rightDirection = false;

                    output.writeUTF("4");
                    output.flush();
                }
                
                output.close();
            }
            catch (IOException evt) {
                JOptionPane.showMessageDialog(null, evt);
            }                
        }
    }
}
