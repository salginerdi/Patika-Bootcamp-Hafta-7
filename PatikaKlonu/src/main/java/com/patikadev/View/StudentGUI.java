package com.patikadev.View;

import com.patikadev.Helper.Config;
import com.patikadev.Helper.Helper;
import com.patikadev.Model.Patika;
import com.patikadev.Model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class StudentGUI extends JFrame{
    private JPanel wrapper;
    private JButton btn_logout;
    private JTable tbl_studentList;
    private JLabel lbl_student_welcome;
    private DefaultTableModel mdl_patika_list;
    private Object[] row_patika_list;

    public StudentGUI(Student student){
        add(wrapper);
        setSize(1000, 500);
        setLocation(Helper.scCenter("x", getSize()), Helper.scCenter("y", getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        lbl_student_welcome.setText("Hoşgeldiniz, " + student.getName());
        mdl_patika_list = new DefaultTableModel();
        loadPatikaModel();
        tbl_studentList.setModel(mdl_patika_list);
        tbl_studentList.getTableHeader().setReorderingAllowed(false);

        tbl_studentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int selected_row = tbl_studentList.rowAtPoint(point);
                int selected_column = tbl_studentList.columnAtPoint(point);
                tbl_studentList.setRowSelectionInterval(selected_column,selected_row);
                dispose();
                StudentPatikaGUI studentPatikaGUI = new StudentPatikaGUI((String) tbl_studentList.getValueAt(selected_row,selected_column),student.getId());
            }
        });


        btn_logout.addActionListener(e -> {
            dispose();
            LoginGUI loginGUI = new LoginGUI();
        });
    }

    private void loadPatikaModel(){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_studentList.getModel();
        clearModel.setRowCount(0);
        int i;
        for (Patika obj : Patika.getList()){
            i = 0;
            row_patika_list[i++] = obj.getName();
            mdl_patika_list.addRow(row_patika_list);
        }
    }

}
