
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub Tomczuk
 */

     public class Main extends JPanel {

    private static final long serialVersionUID = 1L;
    private BufferedImage img;
    
   
    private ppm k;

    public Main() {

        k = new ppm();
        loader();
        JFrame window = new JFrame("Frame");
        JMenuBar menuBar = new JMenuBar();
        JMenu editormenu = new JMenu("Binaryzacja");
        JMenuItem customThreshold = new JMenuItem("Ręczna");

        customThreshold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String name;
                int value;
                name = JOptionPane.showInputDialog(window,
                        "Podaj wartość :", 0);
                System.out.println(name);
                value = Integer.parseInt(name);
                System.out.println(value);
                if(value>0 && value<=100){
                    Binarization.customThreshold(img,value);                   
                }         
               
                window.pack();
                window.repaint();
            }
        });
        
     
        JMenuItem percentBlackSelect = new JMenuItem("Procentowa selekcja czarnego");
        percentBlackSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name;
                double value;
                name = JOptionPane.showInputDialog(window,
                        "Podaj wartość procentową:", 0.0);
                System.out.println(name);
                value = Double.parseDouble(name);
                System.out.println(value);
                if(value>0.0 && value<=1.0){
                    Binarization.percentBlackSelection(img,value);                   
                }         
                
                window.pack();
                window.repaint();
            }
        });
        
        JMenuItem meanIterativeSelection = new JMenuItem("Selekcja iteratywna średniej");
        meanIterativeSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
               Binarization.meanIterativeSelection(img);
                
                window.pack();
                window.repaint();
            }
        });
        
        JMenuItem entropySelection = new JMenuItem("Selekcja entropii");
        entropySelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Binarization.entropySelection(img);
                
                window.pack();
                window.repaint();
            }
        });
        JMenuItem minimumError = new JMenuItem("Błąd Minimalny");
        minimumError.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Binarization.minimumError(img);
              
                window.pack();
                window.repaint();
            }
        });
        JMenuItem fuzzyMinimumError = new JMenuItem("Metoda rozmytego błędu minimalnego");
        fuzzyMinimumError.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Binarization.fuzzyMinimumError(img);
                window.pack();
                window.repaint();
            }
   


            
        });
        JMenuItem reset = new JMenuItem("Resetuj obraz");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               loader();
               img = k.ppmi;
               window.pack();
               window.repaint();
            }
        });

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        menuBar.add(editormenu);
        editormenu.add(customThreshold);        
        editormenu.add(percentBlackSelect);
        editormenu.add(meanIterativeSelection);
        editormenu.add(entropySelection);
        editormenu.add(minimumError);
        editormenu.add(fuzzyMinimumError);
        editormenu.add(reset);
        //window.add(menuBar);
        window.setJMenuBar(menuBar);
        window.add(this);
        img = k.ppmi;

        window.setPreferredSize(new Dimension(k.width + 15, k.height + 60));
        window.pack();

        System.out.println("Done");

    }

    public static void main(String[] args) throws IOException {
        new Main();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(img, 0, 0, null);
    }
    public  void loader(){
        try {						// tutaj wpisujemy plik ppm ktory chcemy wczytac
            k.load("lena_384.ppm");// pm3
            //k.load("etud.ppm");//pm6			
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

