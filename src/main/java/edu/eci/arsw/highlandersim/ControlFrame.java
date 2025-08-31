package edu.eci.arsw.highlandersim;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlFrame extends JFrame {
    private static final Logger logger = Logger.getLogger(ControlFrame.class.getName());


    private static final int DEFAULT_IMMORTAL_HEALTH = 100;
    private static final int DEFAULT_DAMAGE_VALUE = 10;

    private transient List<Immortal> immortals;

    private final JTextArea output;
    private final JLabel statisticsLabel;
    private final JScrollPane scrollPane;
    private final JTextField numOfImmortals;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ControlFrame frame = new ControlFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to launch ControlFrame", e);
            }
        });
    }

    /**
     * Create the frame.
     */
    public ControlFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 647, 248);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        final JButton btnStart = new JButton("Start");
        btnStart.addActionListener(e -> {
            immortals = setupInmortals();

            if (immortals != null) {
                for (Immortal im : immortals) {
                    im.start();
                }
            }

            btnStart.setEnabled(false);
        });
        toolBar.add(btnStart);

        JButton btnPauseAndCheck = getJButton();
        toolBar.add(btnPauseAndCheck);

        final JButton btnResume = new JButton("Resume");
        btnResume.addActionListener(e -> {
            for (Immortal im : immortals) {
                im.unpause();
            }
        });
        toolBar.add(btnResume);

        JLabel lblNumOfImmortals = new JLabel("num. of immortals:");
        toolBar.add(lblNumOfImmortals);

        numOfImmortals = new JTextField();
        numOfImmortals.setText("3");
        toolBar.add(numOfImmortals);
        numOfImmortals.setColumns(10);

        JButton btnStop = new JButton("STOP");
        btnStop.setForeground(Color.RED);
        toolBar.add(btnStop);

        scrollPane = new JScrollPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        output = new JTextArea();
        output.setEditable(false);
        scrollPane.setViewportView(output);
        
        
        statisticsLabel = new JLabel("Immortals total health:");
        contentPane.add(statisticsLabel, BorderLayout.SOUTH);

    }

    private JButton getJButton() {
        JButton btnPauseAndCheck = new JButton("Pause and check");
        btnPauseAndCheck.addActionListener(e -> {
            for (Immortal im : immortals) {
                im.pause();
            }

            int sum = 0;
            for (Immortal im : immortals) {
                sum += im.getHealth();
            }

            statisticsLabel.setText("<html>"+immortals+"<br>Health sum:"+ sum);

        });
        return btnPauseAndCheck;
    }

    public List<Immortal> setupInmortals() {

        ImmortalUpdateReportCallback ucb=new TextAreaUpdateReportCallback(output,scrollPane);
        
        try {
            int ni = Integer.parseInt(numOfImmortals.getText());

            List<Immortal> il = new LinkedList<>();

            for (int i = 0; i < ni; i++) {
                Immortal i1 = new Immortal("im" + i, il, DEFAULT_IMMORTAL_HEALTH, DEFAULT_DAMAGE_VALUE,ucb);
                il.add(i1);
            }
            return il;
        } catch (NumberFormatException e) {
            JOptionPane.showConfirmDialog(null, "Número inválido.");
            return Collections.emptyList();
        }

    }

}

class TextAreaUpdateReportCallback implements ImmortalUpdateReportCallback{

    JTextArea ta;
    JScrollPane jsp;

    public TextAreaUpdateReportCallback(JTextArea ta,JScrollPane jsp) {
        this.ta = ta;
        this.jsp=jsp;
    }       
    
    @Override
    public void processReport(String report) {
        ta.append(report);

        //move scrollbar to the bottom
        javax.swing.SwingUtilities.invokeLater(() -> {
            JScrollBar bar = jsp.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

    }
    
}