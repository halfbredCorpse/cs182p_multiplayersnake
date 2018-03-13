package com.snakeUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;

public class SnakeOpponentPanel extends javax.swing.JPanel implements ActionListener, Runnable {
    private final int B_WIDTH = 300,
                      B_HEIGHT = 300,
                      DOT_SIZE = 10,
                      ALL_DOTS = 900,
                      DELAY = 100;

    private final int x[] = new int[ALL_DOTS],
                      y[] = new int[ALL_DOTS];

    private int dots, apple_x, apple_y;

    private boolean leftDirection = false,
                    rightDirection = true,
                    upDirection = false,
                    downDirection = false,
                    inGame = true;

    private Timer timer;
    private Image ball, apple, head;
    
    private SnakeOpponentPanel sop;

    private String key;
    
    private DatagramSocket datagramSocket;
    private DatagramPacket packet;
    private final int BYTE_SIZE = 1024;
    private byte byteBuffer[] = new byte[BYTE_SIZE];
    
    /**
     *
     * @return B_WIDTH
     */
    public int getbWidth() {
        return B_WIDTH;
    }
    
    /**
     *
     * @return B_HEIGHT
     */
    public int getbHeight() {
        return B_HEIGHT;
    }
    
    /**
     * Creates new form SnakePanel
     */
    public SnakeOpponentPanel() {
        initComponents();
        
        
    }    
    
    public void run() {
        setFocusable(true);
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();  
    }
    
    private void loadImages() {
        ImageIcon iid = new ImageIcon("dot.png");
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon("apple.png");
        apple = iia.getImage();

        ImageIcon iih = new ImageIcon("dot.png");
        head = iih.getImage();
    }    
    
    private void initGame() {
        try {
            datagramSocket = new DatagramSocket(4321);
            packet = new DatagramPacket(byteBuffer, BYTE_SIZE);
            
            String xArray, yArray,
                   xArr[], yArr[];
            
            //dots = Integer.parseInt(input.readUTF());
            datagramSocket.receive(packet);
            dots = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));

            //xArray = input.readUTF();
            datagramSocket.receive(packet);
            xArray = new String(packet.getData(), 0, packet.getLength());
            xArr = xArray.split("\t");
            
            //yArray = input.readUTF();
            datagramSocket.receive(packet);
            yArray = new String(packet.getData(), 0, packet.getLength());
            yArr = yArray.split("\t");
            
            for (int i = 0; i < xArr.length; i++) {
                if (xArr[i].equals(""))
                    x[i] = 0;
                else
                    x[i] = Integer.parseInt(xArr[i]);
                
                if (yArr[i].equals(""))
                    y[i] = 0;
                else
                    y[i] = Integer.parseInt(yArr[i]);
            }

            locateApple();

            timer = new Timer(DELAY, this);
            timer.start();
            
            //new CheckMove().start();

        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }
    
    private void doDrawing(Graphics g) {
        if (inGame) {
            g.drawImage(apple, apple_x, apple_y, this);

            for (int z = 0; z < dots; z++) {
                if (z == 0) {
                    g.drawImage(head, x[z], y[z], this);
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }

            Toolkit.getDefaultToolkit().sync();
        }
        else {
            gameOver(g);
            JButton btn = new JButton("Play Again");
            btn.setSize(100, 30);
            btn.setLocation(new Point((B_WIDTH-100) / 2, (B_HEIGHT+50) / 2));
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newGame();
                }
            });
            this.add(btn);
        }        
    }
    
    private void newGame() {
        sop = new SnakeOpponentPanel();
        this.removeAll();
        add(sop);
        this.revalidate();
        this.repaint();
        sop.requestFocus();
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over. You WON!";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);   
        g.setColor(Color.black);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    }

    private void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            locateApple();
        }
    }

    private void move() {
        for (int z = dots; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection)
            x[0] -= DOT_SIZE;

        if (rightDirection)
            x[0] += DOT_SIZE;

        if (upDirection) 
            y[0] -= DOT_SIZE;

        if (downDirection)
            y[0] += DOT_SIZE;
    }

    private void checkCollision() {
        try {
            datagramSocket.receive(packet);
            String check = new String(packet.getData(), 0, packet.getLength());
                    
            if (check.equals("0"))
                inGame = true;
            else if (check.equals("-1"))
                inGame = false;
            
            if (!inGame)
                timer.stop();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void locateApple() {
        try {
            datagramSocket.receive(packet);
            apple_x = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
            
            datagramSocket.receive(packet);
            apple_y = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
        }
        catch (IOException e) { 
            JOptionPane.showMessageDialog(null, e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (inGame) {
            checkApple();
            checkCollision();
            checkMove();
            move();
        }

        repaint();
    }
    
    public class CheckMove extends Thread {
        public void run() {
            try {
                while (inGame) {
                    //key = input.readUTF();
                    
                    datagramSocket.receive(packet);
                    key = new String(packet.getData(), 0, packet.getLength());
                    
                    if ((key.equals("1")) && (!rightDirection)) {
                        leftDirection = true;
                        upDirection = false;
                        downDirection = false;
                    }

                    if ((key.equals("3")) && (!leftDirection)) {
                        rightDirection = true;
                        upDirection = false;
                        downDirection = false;
                    }

                    if ((key.equals("2")) && (!downDirection)) {
                        upDirection = true;
                        rightDirection = false;
                        leftDirection = false;
                    }

                    if ((key.equals("4")) && (!upDirection)) {
                        downDirection = true;
                        rightDirection = false;
                        leftDirection = false;
                    } 
                }
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(null, e);    
            }   
        }
    }
    
    public void checkMove() {
        try {    
            //key = input.readUTF();

            datagramSocket.receive(packet);
            key = new String(packet.getData(), 0, packet.getLength());

            if ((key.equals("1")) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key.equals("3")) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key.equals("2")) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key.equals("4")) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            } 
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);    
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        setLayout(new java.awt.GridLayout(1, 0));
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
