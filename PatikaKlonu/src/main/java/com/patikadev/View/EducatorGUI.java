package com.patikadev.View;

import com.patikadev.Helper.Config;
import com.patikadev.Helper.Helper;
import com.patikadev.Helper.Item;
import com.patikadev.Model.Course;
import com.patikadev.Model.CourseContent;
import com.patikadev.Model.Educator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EducatorGUI extends JFrame{
    private JPanel wrapper;
    private JPanel topw;
    private JPanel bottomw;
    private JButton btn_logout;
    private JTabbedPane tabbedPane1;
    private JTable tbl_course_list;
    private JComboBox cmb_contentCourse;
    private JComboBox cmb_contentTitle;
    private JEditorPane pnl_contentQuiz;
    private JButton btn_quizAdd;
    private JLabel lbl_welcome;
    private final Educator educator;
    private DefaultTableModel mdl_course_list;
    private Object[] row_course_list;
    private int course_id;


    public EducatorGUI(Educator educator){
        this.educator = educator;
        add(wrapper);
        setSize(1000, 500);
        setLocation(Helper.scCenter("x", getSize()), Helper.scCenter("y", getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);
        lbl_welcome.setText("Hoşgeldiniz, " + educator.getName());

        btn_logout.addActionListener(e -> {
            dispose();
            LoginGUI loginGUI = new LoginGUI();
        });

        btn_quizAdd.addActionListener(e -> {
            String title = cmb_contentTitle.getSelectedItem().toString();
            String quizText = pnl_contentQuiz.getText();
            if(CourseContent.addQuiz(title,quizText)){
                Helper.showMsg("done");
                pnl_contentQuiz.setText(null);
            }else {
                Helper.showMsg("error");
                pnl_contentQuiz.setText(null);
            }
        });

        cmb_contentCourse.addActionListener(e -> {
            loadContentTitleCombo();
        });

        mdl_course_list = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0)
                    return false;
                return super.isCellEditable(row, column);
            }
        };

        Object[] col_course_list = {"Ders Adı", "Patika"};
        mdl_course_list.setColumnIdentifiers(col_course_list);
        row_course_list = new Object[col_course_list.length];
        loadEducatorModel();
        tbl_course_list.setModel(mdl_course_list);
        tbl_course_list.getTableHeader().setReorderingAllowed(false);
        loadContentCourseCombo();
        loadContentTitleCombo();
        tbl_course_list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Point point = e.getPoint();
                    int selected_row = tbl_course_list.rowAtPoint(point);
                    int selected_column = tbl_course_list.columnAtPoint(point);
                    tbl_course_list.setRowSelectionInterval(selected_column, selected_row);
                    dispose();
                    ContentGUI contentGUI = new ContentGUI((String) tbl_course_list.getValueAt(selected_row, selected_column));
                }catch (IllegalArgumentException exception){
                    exception.getStackTrace();
                }
            }
        });

    }

    public void loadEducatorModel(){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_course_list.getModel();
        clearModel.setRowCount(0);
        int i;
        for(Course obj :  Course.getList()){
            i = 0;
            if (obj.getEducator().getName().equals(educator.getName())){
                row_course_list[i++] = obj.getName();
                row_course_list[i++] = obj.getPatika().getName();
                mdl_course_list.addRow(row_course_list);
            }
        }
    }

    public void loadContentCourseCombo(){
        cmb_contentCourse.removeAllItems();
        for(Course obj :  Course.getList()){
            if (obj.getEducator().getName().equals(educator.getName())){
                cmb_contentCourse.addItem(new Item(obj.getId(), obj.getName()));
            }
        }
    }

    public void loadContentTitleCombo(){
        cmb_contentTitle.removeAllItems();
        String title = cmb_contentCourse.getSelectedItem().toString();
        int id = CourseContent.getCourseID(title);
        for (CourseContent obj: CourseContent.getList(id)){
            cmb_contentTitle.addItem(new Item(obj.getId(),obj.getTitle()));
        }
    }

}
