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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import java.io.*;
import java.net.*;

public class SnakePanel extends javax.swing.JPanel implements ActionListener, Runnable {
    private final int B_WIDTH = 300,
                      B_HEIGHT = 300,
                      DOT_SIZE = 10,
                      ALL_DOTS = 900,
                      RAND_POS = 29,
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
    private String keyString;
    
    private SnakePanel sp;
    private ControlAdapter controller;
    
    private DatagramSocket datagramSocket;
    private DatagramPacket packet;
    private InetAddress ipadd;
    
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
    public SnakePanel() {
        initComponents(); 
    }
    
    public void run() {
        controller = new ControlAdapter();
        
        addKeyListener(controller);
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
            datagramSocket = new DatagramSocket();
            try {
                ipadd = InetAddress.getLocalHost();
            }
            catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            
            String xArray = "", 
                   yArray = "";
            
            dots = 3;
            packet = new DatagramPacket(Integer.toString(dots).getBytes(), Integer.toString(dots).length(), ipadd, 4321);
            datagramSocket.send(packet);
            
            for (int i = 0; i < dots; i++) {
                x[i] = 50 - i * 10;
                xArray += Integer.toString(x[i]) + "\t";

                y[i] = 50;
                yArray += Integer.toString(y[i]) + "\t";
            }

            packet = new DatagramPacket(xArray.getBytes(), xArray.length(), ipadd, 4321);
            datagramSocket.send(packet);
            
            packet = new DatagramPacket(yArray.getBytes(), yArray.length(), ipadd, 4321);
            datagramSocket.send(packet);
            
            locateApple();

            timer = new Timer(DELAY, this);
            timer.start();
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
        sp = new SnakePanel();
        this.removeAll();
        add(sp);
        this.revalidate();
        this.repaint();
        sp.requestFocus();
    }

    private void gameOver(Graphics g) {
        String msg = "Game Over. You LOST!";
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
        
        keyString = "";
        
        try {
            packet = new DatagramPacket(keyString.getBytes(), keyString.length(), ipadd, 4321);
            datagramSocket.send(packet);
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void checkCollision() {
        try {
            for (int z = dots; z > 0; z--) {
                if ((z > 4) && (x[0] == x[z]) && (y[0] == y[z])) {
                    inGame = false;
                    
                    packet = new DatagramPacket("-1".getBytes(),"-1".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                }
            }

            if ((y[0] >= B_HEIGHT) | (y[0] < 0) | (x[0] >= B_WIDTH) | (x[0] < 0)) {
                inGame = false;
                
                packet = new DatagramPacket("-1".getBytes(), "-1".length(), ipadd, 4321);
                datagramSocket.send(packet);
            }

            if (inGame) {
                packet = new DatagramPacket("0".getBytes(), "0".length(), ipadd, 4321);
                datagramSocket.send(packet);
            }
                
            else
                timer.stop();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(null, e);
        }   
    }

    private void locateApple() {
        try {
            int r = (int) (Math.random() * RAND_POS);
            apple_x = ((r * DOT_SIZE));
            
            packet = new DatagramPacket(Integer.toString(apple_x).getBytes(), Integer.toString(apple_x).length(), ipadd, 4321);
            datagramSocket.send(packet);
            
            r = (int) (Math.random() * RAND_POS);
            apple_y = ((r * DOT_SIZE));
            
            packet = new DatagramPacket(Integer.toString(apple_y).getBytes(), Integer.toString(apple_y).length(), ipadd, 4321);
            datagramSocket.send(packet);
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
            move();
        }

        repaint();
    }

    private class ControlAdapter extends KeyAdapter implements Runnable {
        public void run() {
            /*
            try {
                do {
                    packet = new DatagramPacket("".getBytes(), "".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                    
                } while (inGame);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog(null, e);
            }
            */
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            try {
                if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                    leftDirection = true;
                    upDirection = false;
                    downDirection = false;
                    
                    keyString = "1";
                    
                    
                    packet = new DatagramPacket("1".getBytes(), "1".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                    
                }

                if  ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                    rightDirection = true;
                    upDirection = false;
                    downDirection = false;
                    
                    keyString = "3";
                    
                    
                    packet = new DatagramPacket("3".getBytes(), "3".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                    
                }

                if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                    upDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                    
                    keyString = "2";
                    
                    
                    packet = new DatagramPacket("2".getBytes(), "2".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                    
                }

                if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                    downDirection = true;
                    rightDirection = false;
                    leftDirection = false;
                    
                   keyString = "4";
                    
                    
                    packet = new DatagramPacket("4".getBytes(), "4".length(), ipadd, 4321);
                    datagramSocket.send(packet);
                    
                }   
            }
            catch (IOException evt) {
                JOptionPane.showMessageDialog(null, evt);
            }            
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
