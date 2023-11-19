package com.patikadev.View;
import com.patikadev.Helper.*;
import com.patikadev.Model.Course;
import com.patikadev.Model.Operator;
import com.patikadev.Model.Patika;
import com.patikadev.Model.User;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class OperatorGUI extends JFrame {
    private JPanel wrapper;
    private JTabbedPane tab_operator;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JPanel pnl_userList;
    private JScrollPane scrl_userList;
    private JTable tbl_userList;
    private JPanel pnl_userForm;
    private JTextField fld_userName;
    private JTextField fld_userUname;
    private JPasswordField fld_userPass;
    private JComboBox cmb_userType;
    private JButton btn_userAdd;
    private JButton btn_userDelete;
    private JTextField fld_userId;
    private JTextField fld_src_userName;
    private JTextField fld_srcUname;
    private JComboBox cmb_src_userType;
    private JButton btn_src;
    private JPanel pnl_patikaList;
    private JScrollPane scrl_patikaList;
    private JTable tbl_patikaList;
    private JPanel pnl_patikaAdd;
    private JTextField fld_patikaName;
    private JButton btn_patikaAdd;
    private JPanel pnl_courseForm;
    private JScrollPane scrl_courseList;
    private JTable tbl_courseList;
    private JPanel pnl_courseAdd;
    private JTextField fld_courseName;
    private JTextField fld_courseLang;
    private JComboBox cmb_coursePatika;
    private JComboBox cmb_courseUser;
    private JButton btn_courseAdd;
    private DefaultTableModel mdl_userList;
    private Object[] row_userList;
    private DefaultTableModel mdl_patikaList;
    private Object[] row_patikaList;
    private final Operator operator;
    private JPopupMenu patikaMenu;
    private DefaultTableModel mdl_course_list;
    private Object[] row_course_list;

    public OperatorGUI(Operator operator){
        this.operator = operator;

        add(wrapper);
        setSize(1000, 500);
        setLocation(Helper.scCenter("x", getSize()), Helper.scCenter("y", getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        lbl_welcome.setText("Hoşgeldin " + operator.getName());

        // User List

        mdl_userList = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0)
                    return false;
                return super.isCellEditable(row, column);
            }
        };

        Object[] col_userList = {"Id", "Ad Soyad", "Kullanıcı Adı", "Şifre", "Üyelik Tipi"};
        mdl_userList.setColumnIdentifiers(col_userList);

        row_userList = new Object[col_userList.length];

        loadUserModel();

        tbl_userList.setModel(mdl_userList);
        tbl_userList.getTableHeader().setReorderingAllowed(false);

        tbl_userList.getSelectionModel().addListSelectionListener(e -> {
            try{
                String select_user_id = tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 0).toString();
                fld_userId.setText(select_user_id);
            } catch (Exception exception){

            }
        });

        tbl_userList.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE){
                int user_id = Integer.parseInt(tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 0).toString());
                String user_name = tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 1).toString();
                String user_uname = tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 2).toString();
                String user_pass = tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 3).toString();
                String user_type = tbl_userList.getValueAt(tbl_userList.getSelectedRow(), 4).toString();

                if (User.update(user_id, user_name, user_uname, user_pass, user_type)){
                    Helper.showMsg("done");
                    loadUserModel();
                    loadEducatorCombo();
                    loadCourseModel();
                }
            }
        });

        // Patika List

        patikaMenu = new JPopupMenu();
        JMenuItem updateMenu = new JMenuItem("Güncelle");
        JMenuItem deleteMenu = new JMenuItem("Sil");
        patikaMenu.add(updateMenu);
        patikaMenu.add(deleteMenu);

        updateMenu.addActionListener(e -> {
            int select_id = Integer.parseInt(tbl_patikaList.getValueAt(tbl_patikaList.getSelectedRow(), 0).toString());
            UpdatePatikaGUI updateGUI = new UpdatePatikaGUI(Patika.getFetch(select_id));
            updateGUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadPatikaModel();
                    loadPatikaCombo();
                    loadCourseModel();
                }
            });
        });

        deleteMenu.addActionListener(e -> {
            if (Helper.confirm("sure")){
                int select_id = Integer.parseInt(tbl_patikaList.getValueAt(tbl_patikaList.getSelectedRow(), 0).toString());
                if (Patika.delete(select_id)){
                    Helper.showMsg("done");
                    loadPatikaModel();
                    loadPatikaCombo();
                    loadCourseModel();
                }else{
                    Helper.showMsg("error");
                }
            }
        });

        mdl_patikaList = new DefaultTableModel();
        Object[] col_patika_list = {"ID", "Patika Adı"};
        mdl_patikaList.setColumnIdentifiers(col_patika_list);
        row_patikaList = new Object[col_patika_list.length];
        loadPatikaModel();

        tbl_patikaList.setModel(mdl_patikaList);
        tbl_patikaList.setComponentPopupMenu(patikaMenu);
        tbl_patikaList.getTableHeader().setReorderingAllowed(false);
        tbl_patikaList.getColumnModel().getColumn(0).setMaxWidth(75);

        tbl_patikaList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point point = e.getPoint();
                int selected_row = tbl_patikaList.rowAtPoint(point);
                tbl_patikaList.setRowSelectionInterval(selected_row, selected_row);
            }
        });

        // Course List

        mdl_course_list = new DefaultTableModel();
        Object[] col_courseList = {"ID", "Ders Adı", "Programlama Dili", "Patika", "Eğitmen"};
        mdl_course_list.setColumnIdentifiers(col_courseList);
        row_course_list = new Object[col_courseList.length];

        loadCourseModel();

        tbl_courseList.setModel(mdl_course_list);
        tbl_courseList.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_courseList.getTableHeader().setReorderingAllowed(false);

        loadPatikaCombo();
        loadEducatorCombo();

        // Buttons

        btn_userAdd.addActionListener(e -> {
            if (Helper.isFieldEmpty(fld_userName) || Helper.isFieldEmpty(fld_userUname) || Helper.isFieldEmpty(fld_userPass)){
                Helper.showMsg("fill");
            } else
            {
                String name = fld_userName.getText();
                String uname = fld_userUname.getText();
                String pass = fld_userPass.getText();
                String type = cmb_userType.getSelectedItem().toString();

                if (User.add(name, uname, pass, type)){
                    Helper.showMsg("done");

                    loadUserModel();
                    loadEducatorCombo();

                    fld_userPass.setText(null);
                    fld_userName.setText(null);
                    fld_userUname.setText(null);

                } else
                {
                    Helper.showMsg("error");
                }
            }
        });

        btn_userDelete.addActionListener(e -> {
            if (Helper.isFieldEmpty(fld_userId)){
                Helper.showMsg("fill");
            } else
            {
               if (Helper.confirm("sure")){
                   int fld_user_id = Integer.parseInt(fld_userId.getText());
                   if (User.delete(fld_user_id)){
                       Helper.showMsg("done");
                       loadUserModel();
                       loadEducatorCombo();
                       loadCourseModel();
                       fld_userId.setText(null);
                   }else {
                       Helper.showMsg("error");
                   }
               }
            }
        });

        btn_src.addActionListener(e -> {
            String name = fld_src_userName.getText();
            String uname = fld_srcUname.getText();
            String type = cmb_src_userType.getSelectedItem().toString();
            String query = User.searchQuery(name, uname, type);
            loadUserModel(User.searchUserList(query));
        });

        btn_logout.addActionListener(e -> {
            dispose();
            LoginGUI login = new LoginGUI();
        });

        btn_patikaAdd.addActionListener(e -> {
            if (Helper.isFieldEmpty(fld_patikaName)){
                Helper.showMsg("fill");
            } else {
                if (Patika.add(fld_patikaName.getText())){
                    Helper.showMsg("done");
                    loadPatikaModel();
                    loadPatikaCombo();
                    fld_patikaName.setText(null);
                }
                else
                {
                    Helper.showMsg("error");
                }
            }
        });

        btn_courseAdd.addActionListener(e -> {
            Item patikaItem = (Item) cmb_coursePatika.getSelectedItem();
            Item userItem = (Item) cmb_courseUser.getSelectedItem();
            if (Helper.isFieldEmpty(fld_courseName) || Helper.isFieldEmpty(fld_courseLang)){
                Helper.showMsg("fill");
            }else{
                if (Course.add(userItem.getKey(), patikaItem.getKey(), fld_courseName.getText(), fld_courseLang.getText())){
                    Helper.showMsg("done");
                    loadCourseModel();
                    loadPatikaCombo();
                    fld_courseLang.setText(null);
                    fld_courseName.setText(null);
                } else {
                    Helper.showMsg("error");
                }
            }
        });

    }

    // Loads

    private void loadCourseModel() {
        DefaultTableModel clearModel = (DefaultTableModel) tbl_courseList.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for (Course obj : Course.getList()){
            i=0;
            row_course_list[i++] = obj.getId();
            row_course_list[i++] = obj.getName();
            row_course_list[i++] = obj.getLang();
            row_course_list[i++] = obj.getPatika().getName();
            row_course_list[i++] = obj.getEducator().getName();

            mdl_course_list.addRow(row_course_list);
        }
    }

    private void loadPatikaModel() {
        DefaultTableModel clearModel = (DefaultTableModel) tbl_patikaList.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for (Patika obj : Patika.getList()){
            i=0;
            row_patikaList[i++] = obj.getId();
            row_patikaList[i++] = obj.getName();
            mdl_patikaList.addRow(row_patikaList);
        }
    }

    public void loadUserModel(){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_userList.getModel();
        clearModel.setRowCount(0);

        for (User obj : User.getList()){

            row_userList[0] = obj.getId();
            row_userList[1] = obj.getName();
            row_userList[2] = obj.getUname();
            row_userList[3] = obj.getPass();
            row_userList[4] = obj.getType();
            mdl_userList.addRow(row_userList);
        }
    }

    public void loadUserModel(ArrayList<User> list){
        DefaultTableModel clearModel = (DefaultTableModel) tbl_userList.getModel();
        clearModel.setRowCount(0);

        for (User obj : list){

            row_userList[0] = obj.getId();
            row_userList[1] = obj.getName();
            row_userList[2] = obj.getUname();
            row_userList[3] = obj.getPass();
            row_userList[4] = obj.getType();
            mdl_userList.addRow(row_userList);
        }
    }

    public void loadPatikaCombo(){
        cmb_coursePatika.removeAllItems();
        for (Patika obj : Patika.getList()){
            cmb_coursePatika.addItem(new Item(obj.getId(), obj.getName()));
        }
    }

    public void loadEducatorCombo(){
        cmb_courseUser.removeAllItems();
        for (User obj : User.getList()){
            if (obj.getType().equals("educator")){
                cmb_courseUser.addItem(new Item(obj.getId(), obj.getName()));
            }
        }
    }

    public static void main(String[] args) {
        Helper.setLayout();
        Operator op = new Operator();
        op.setId(1);
        op.setName("Mert Taş");
        op.setUname("mertcan");
        op.setPass("taskiran");
        op.setType("Operator");
        OperatorGUI opGUI = new OperatorGUI(op);
    }
}
