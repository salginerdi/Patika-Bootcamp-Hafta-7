package com.patikadev.View;

import com.patikadev.Helper.Config;
import com.patikadev.Helper.DBConnector;
import com.patikadev.Model.Course;
import com.patikadev.Model.Student;
import com.patikadev.Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.patikadev.Helper.Helper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentPatikaGUI extends JFrame{
    private JPanel wrapper;
    private JButton btn_back;
    private JButton btn_patikaGiris;
    private JTable tbl_studentPatikaList;
    private JLabel lbl_welcome_patika;
    private final JButton btn_studentPatikaGiris = new JButton("Patikaya Gir");
    private String patikaName;
    private int user_id;
    private int patika_id;
    private DefaultTableModel mdl_course_list;
    private Object[] row_course_list;

    public StudentPatikaGUI(String patikaName,int user_id){
        this.patikaName = patikaName;
        patika_id = User.getPatikaID(patikaName);
        this.user_id = user_id;
        add(wrapper);
        setSize(1000,500);
        int x = Helper.scCenter("x",getSize());
        int y = Helper.scCenter("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);
        lbl_welcome_patika.setText(this.patikaName);

        mdl_course_list = new DefaultTableModel();
        Object[] col_courseList = {"Dersler",""};
        mdl_course_list.setColumnIdentifiers(col_courseList);
        row_course_list = new Object[col_courseList.length];
        loadCourseModel();
        tbl_studentPatikaList.setModel(mdl_course_list);
        tbl_studentPatikaList.getTableHeader().setReorderingAllowed(false);

        tbl_studentPatikaList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int selected_row = tbl_studentPatikaList.rowAtPoint(point);
                int selected_column = tbl_studentPatikaList.columnAtPoint(point);
                tbl_studentPatikaList.setRowSelectionInterval(selected_column,selected_row);
                int isJoined = checkJoin(user_id,patika_id);
                if ((isJoined != 0)){
                    StudentCourseGUI studentCourseGUI = new StudentCourseGUI((String) tbl_studentPatikaList.getValueAt(selected_row,selected_column));
                    dispose();
                }else {
                    Helper.showMsg("Ders içeriklerini görmek için bir patikaya katıl !");
                }
            }
        });

        btn_back.addActionListener(e -> {
            Student student = Student.getFetch(user_id);
            StudentGUI studentGUI = new StudentGUI((student));
            dispose();
        });

        btn_patikaGiris.addActionListener(e -> {
            int isJoined = checkJoin(user_id,patika_id);
            if ((isJoined == 0)){
                if (joinPatika(user_id,patika_id)){
                    btn_patikaGiris.setText("Patikaya katıldın ! İyi çalışmalar...");
                }
            }
        });
    }

    private void loadCourseModel(){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_studentPatikaList.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Course obj: Course.getList()){
            if (obj.getPatika_id() == patika_id) {
                i = 0;
                row_course_list[i++] = obj.getName();
                mdl_course_list.addRow(row_course_list);
            }
        }
        int isJoined = checkJoin(user_id,patika_id);
        if ((isJoined != 0)){
            btn_patikaGiris.setText("Patikaya katıldın !");
        }
    }

    private boolean joinPatika(int user_id,int patika_id){
        String query = "INSERT INTO patika_giris (user_id,patika_id) VALUES (?,?)";
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setInt(1,user_id);
            pr.setInt(2,patika_id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int checkJoin(int user_id,int patikaID){
        String query = "SELECT patika_id FROM patika_giris WHERE user_id = " + user_id + " AND  patika_id = " + patikaID;
        try {
            Statement st = DBConnector.getInstance().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                return rs.getInt("patika_id");
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return 0;
    }

}
