package com.patikadev.View;

import javax.swing.*;
import com.patikadev.Helper.Config;
import com.patikadev.Helper.DBConnector;
import com.patikadev.Helper.Helper;
import com.patikadev.Model.User;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class SignInGUI extends JFrame{
    private JPanel wrapper;
    private JTextField fld_email;
    private JTextField fld_uname;
    private JTextField fld_ad;
    private JTextField fld_soyad;
    private JPasswordField fld_pass;
    private JButton btn_kayit;

    public SignInGUI() {
        add(wrapper);
        setSize(750,500);
        setLocation(Helper.scCenter("x",getSize()),Helper.scCenter("y",getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setResizable(false);
        setVisible(true);

        btn_kayit.addActionListener(e -> {
            String email = fld_email.getText();
            String uname = fld_uname.getText();
            String ad = fld_ad.getText();
            String soyad = fld_soyad.getText();
            String pass = fld_pass.getText();

            String name = ad + " " + soyad;
            if (Helper.isFieldEmpty(fld_email) || Helper.isFieldEmpty(fld_uname) || Helper.isFieldEmpty(fld_ad) || Helper.isFieldEmpty(fld_soyad) || Helper.isFieldEmpty(fld_pass)){
                Helper.showMsg("fill");
            }else{
                if (isEmailValid(email)){
                    addNewStudent(name,uname,pass);
                    LoginGUI loginGUI = new LoginGUI();
                    dispose();
                }
            }
        });
    }

    private boolean isEmailValid(String mail){
        if (mail.contains("@gmail.com") || mail.contains("@hotmail.com")){
            return true;
        }else {
            Helper.showMsg("Lütfen geçerli bir e-posta girin.");
            return false;
        }
    }

    private boolean addNewStudent(String name, String uname, String pass){
        String query = "INSERT INTO user (name,uname,pass,type) VALUES (?,?,?,?)";
        User findUser = User.getFetch(uname);
        if (findUser != null){
            Helper.showMsg("Bu kullancı adı alınmış. Lütfen farklı bir kullanıcı giriniz.");
            return false;
        }
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setString(1,name);
            pr.setString(2,uname);
            pr.setString(3,pass);
            pr.setObject(4,"student", Types.OTHER);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }

}
