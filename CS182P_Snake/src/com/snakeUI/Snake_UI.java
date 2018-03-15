package com.snakeUI;

import javax.swing.JFrame;

public class Snake_UI extends javax.swing.JFrame {
    
    FontChooser f = new FontChooser();
    SnakePanel sp;
    
    public Snake_UI() {
        initSnakePanels();
        
        initComponents();
    }
    
    /**
     * Manual way of adding the SnakePanels or Board
     */
        
    public void initSnakePanels() {
        sp = new SnakePanel();
        this.setLayout(null);
        sp.setBounds(80, 70, sp.getbWidth(), sp.getbHeight());
        add(sp);
        
        SnakeOpponentPanel sop = new SnakeOpponentPanel();
        sop.setBounds(480, 70, sop.getbWidth(), sop.getbHeight());
        add(sop);
        pack();
        
        Thread player = new Thread(sp),
               opponent = new Thread(sop);
        
        player.setPriority(10);
        opponent.setPriority(9);
        player.start();
        opponent.start();
        
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        p1Label = new javax.swing.JLabel();
        p2Label = new javax.swing.JLabel();
        p2Score = new javax.swing.JLabel();
        p1Score = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Snake Game: Player Screen");
        setLocationByPlatform(true);
        setResizable(false);

        mainPanel.setBackground(new java.awt.Color(0, 33, 0));

        p1Label.setFont(f.returnFont("joystix monospace.ttf", 14f));
        p1Label.setForeground(new java.awt.Color(0, 238, 0));
        p1Label.setText("Player 1");

        p2Label.setFont(f.returnFont("joystix monospace.ttf", 14f));
        p2Label.setForeground(new java.awt.Color(0, 238, 0));
        p2Label.setText("Player 2");

        p2Score.setFont(f.returnFont("joystix monospace.ttf", 14f));
        p2Score.setForeground(new java.awt.Color(0, 238, 0));
        p2Score.setText("Score: ");

        p1Score.setFont(f.returnFont("joystix monospace.ttf", 14f));
        p1Score.setForeground(new java.awt.Color(0, 238, 0));
        p1Score.setText("Score: ");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(p1Label)
                    .addComponent(p1Score))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(p2Label)
                    .addComponent(p2Score))
                .addGap(170, 170, 170))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p1Label)
                    .addComponent(p2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 334, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(p1Score)
                    .addComponent(p2Score))
                .addGap(68, 68, 68))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Snake_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Snake_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Snake_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Snake_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Snake_UI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel p1Label;
    public javax.swing.JLabel p1Score;
    private javax.swing.JLabel p2Label;
    public javax.swing.JLabel p2Score;
    // End of variables declaration//GEN-END:variables
}
