package com.hx.netty;

import java.awt.*;
import java.awt.event.*;
import java.util.UUID;

public class ClientFrame extends Frame {
    TextArea textArea;
    TextField textField;

    ChatClient chatClient;

    public ClientFrame(){
        textArea = new TextArea("",1,1,TextArea.SCROLLBARS_VERTICAL_ONLY);
        textArea.setSize(600,500);
        textField = new TextField();
        textField.setSize(600, 100);
        textField.setName("t_" + UUID.randomUUID());

        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        panel.add(textField,BorderLayout.SOUTH);

        Frame frame = new Frame("chat");
        frame.setSize(600, 600);
        frame.add(panel);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatClient.sendMsg(textField.getText());
                //textArea.append(textArea.getText() + textField.getText());
                textField.setText("");
            }
        });
    }

    public void connectToServer() throws Exception {
        this.chatClient = new ChatClient(this, "127.0.0.1", 8888);
        this.chatClient.connect();
    }

    public void updateTextArea(String msg) {
        this.textArea.append(msg + System.getProperty("line.separator"));
    }

    public static void main(String[] args) {
        ClientFrame clientFrame = new ClientFrame();
        try {
            clientFrame.connectToServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
