import javax.swing.*;
import java.awt.*;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.geom.*;

public class Main extends JFrame implements ActionListener, MouseListener {
    public static void main(String[] args) {
        
    }

    public void actionPerformed(ActionEvent e) {

    }

    public void mouseExited(MouseEvent e) {System.out.println("exit");}
    public void mouseEntered(MouseEvent e) {System.out.println("enter");}
    public void mouseReleased(MouseEvent e) {System.out.println("release");}
    public void mousePressed(MouseEvent e) {System.out.println("press");}
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        System.out.println("click at "+mouseX+", "+mouseY);
        JDialog box = new JDialog(this);
        box.setBounds(400,400,170,20);
        box.toFront();
        box.setVisible(true);
        box.setTitle("something witty");
    }
}