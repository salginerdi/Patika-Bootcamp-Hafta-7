package com.patikadev.View;

import com.patikadev.Helper.Config;
import com.patikadev.Helper.DBConnector;
import com.patikadev.Model.Course;
import com.patikadev.Helper.Helper;
import com.patikadev.Model.CourseContent;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContentGUI extends JFrame{
    private JPanel wrapper;
    private JLabel lbl_course_name;
    private JTable tbl_content;
    private JButton btn_back;
    private JButton btn_content_delete;
    private JEditorPane pnl_content_title;
    private JEditorPane pnl_content_desc;
    private JEditorPane pnl_content_link;
    private JButton btn_contentAdd;
    private Course course;
    private String courseName;
    private DefaultTableModel mdl_content_list;
    private Object[] row_content_list;

    public ContentGUI(String courseName){
        this.courseName = courseName;
        this.course = getSelectedCourse(this.courseName);
        add(wrapper);
        setSize(1000,750);
        setLocation(Helper.scCenter("x",getSize()) , Helper.scCenter("y",getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);
        lbl_course_name.setText(this.course.getName());

        mdl_content_list = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0){
                    return false;
                }
                return super.isCellEditable(row,column);
            }
        };

        Object[] col_content_list = {"ID", "Başlık","Açıklama","Youtube Link","Quiz Soruları"," course_id"};
        mdl_content_list.setColumnIdentifiers(col_content_list);
        row_content_list = new Object[col_content_list.length];
        tbl_content.setModel(mdl_content_list);
        tbl_content.getTableHeader().setReorderingAllowed(false);
        tbl_content.getColumnModel().getColumn(0).setMaxWidth(70);
        loadContentModel();


        btn_contentAdd.addActionListener(e -> {
            if (Helper.isFieldEmpty(pnl_content_title) || Helper.isFieldEmpty(pnl_content_desc) || Helper.isFieldEmpty(pnl_content_link)){
                Helper.showMsg("fill");
            }else {
                String title = pnl_content_title.getText();
                String description = pnl_content_desc.getText();
                String link = pnl_content_link.getText();
                int course_id = course.getId();
                if (CourseContent.add(title,description,link,course_id)){
                    Helper.showMsg("done");
                    loadContentModel();
                    pnl_content_title.setText(null);
                    pnl_content_desc.setText(null);
                    pnl_content_link.setText(null);
                }
            }
        });

        btn_back.addActionListener(e -> {
            if (Helper.confirm("sure")){
                int course_id = (int) tbl_content.getValueAt(tbl_content.getSelectedRow(),0);
                if (CourseContent.delete(course_id)){
                    Helper.showMsg("done");
                    loadContentModel();
                }else {
                    Helper.showMsg("error");
                }
            }
        });

    }

    public static Course getSelectedCourse(String course_name){
        String query = "SELECT * FROM course WHERE course.name=?";
        Course obj = null;
        try {
            PreparedStatement ps = DBConnector.getInstance().prepareStatement(query);
            ps.setString(1,course_name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                int id = rs.getInt("id");
                int user_id = rs.getInt("user_id");
                int patika_id = rs.getInt("patika_id");
                String name = rs.getString("name");
                String lang = rs.getString("lang");
                obj = new Course(id,user_id,patika_id,name,lang);}
            return obj;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void loadContentModel(){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_content.getModel();
        clearModel.setRowCount(0);
        int i;
        int id = this.course.getId();
        for (CourseContent obj: CourseContent.getList(id)) {
            i = 0;
            row_content_list[i++] = obj.getId();
            row_content_list[i++] = obj.getTitle();
            row_content_list[i++] = obj.getDescription();
            row_content_list[i++] = obj.getyLink();
            row_content_list[i++] = obj.getQuizQuestion();
            row_content_list[i++] = obj.getCourse_id();
            mdl_content_list.addRow(row_content_list);
        }
    }
    public void loadContentModel(ArrayList<CourseContent> list){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_content.getModel();
        clearModel.setRowCount(0);
        int i;
        for (CourseContent obj: list) {
            i = 0;
            row_content_list[i++] = obj.getId();
            row_content_list[i++] = obj.getTitle();
            row_content_list[i++] = obj.getDescription();
            row_content_list[i++] = obj.getyLink();
            row_content_list[i++] = obj.getQuizQuestion();
            row_content_list[i++] = obj.getCourse_id();
            mdl_content_list.addRow(row_content_list);
        }
    }

}
