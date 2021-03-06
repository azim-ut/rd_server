package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetPassword extends JFrame implements ActionListener {
    JButton submit;
    JPanel panel;
    JTextField text1, text2;
    String value1, value2;
    JLabel label, label1, label2;

    public SetPassword() {
        label1 = new JLabel();
        label1.setText("Set Password");
        text1 = new JTextField(15);
        label = new JLabel();
        this.setLayout(new BorderLayout());
        submit = new JButton("Submit");
        panel = new JPanel(new GridLayout(2, 1));
        panel.add(label1);
        panel.add(text1);
        panel.add(label);
        panel.add(submit);
        add(panel, BorderLayout.CENTER);
        submit.addActionListener(this);
        setTitle("Setting password for client");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        value1 = text1.getText();
        dispose();
        new InitConnection(Constants.PORT_READ, value1);
    }

    public String getValue1() {
        return value1;
    }

    public static void main(String[] args) {
        SetPassword frame = new SetPassword();
        frame.setSize(3000, 80);
        frame.setLocation(500, 300);
        frame.setVisible(true);
    }

}
