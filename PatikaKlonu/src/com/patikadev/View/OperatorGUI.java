package com.patikadev.View;

import com.patikadev.Helper.Config;
import com.patikadev.Helper.Helper;
import com.patikadev.Helper.Item;
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
    private JPanel pnl_top;
    private JButton btn_logout;
    private JPanel pnl_user_list;
    private JScrollPane scrl_user_list;
    private JTable tbl_user_list; // tablolar modellerle çalışır.
    private JPanel pnl_user_form;
    private JTextField fld_user_name;
    private JTextField fld_user_uname;
    private JPasswordField fld_user_pass;
    private JComboBox cmb_user_type;
    private JButton btn_user_add;
    private JTextField fld_user_id;
    private JButton btn_user_delete;
    private JTextField fld_sh_user_name;
    private JLabel lbl_user_name;
    private JTextField fld_sh_user_uname;
    private JComboBox cmb_sh_user_type;
    private JButton btn_user_sh;
    private JPanel pnl_patika_list;
    private JScrollPane scrl_patika_list;
    private JTable tbl_patika_list;
    private JPanel pnl_patika_add;
    private JTextField fld_patika_name;
    private JButton btn_patika_add;
    private JPanel pnl_course_list;
    private JScrollPane scrl_course_list;
    private JTable tbl_course_list;
    private JPanel pnl_course_add;
    private JTextField fld_course_name;
    private JTextField fld_course_lang;
    private JComboBox cmb_course_patika;
    private JComboBox cmb_course_user;
    private JButton btn_course_add;
    private DefaultTableModel mdl_user_list; //o yüzden model tanımlıyoruz.
    private Object[] row_user_list;
    private DefaultTableModel mdl_patika_list; // dersleri listelemek için bir model oluşturuyoruz.
    private Object[] row_patika_list;
    private JPopupMenu patikaMenu; // Pop-up'lar için.
    private DefaultTableModel mdl_course_list; // kursları listelemek için bir model oluşturuyoruz.
    private Object[] row_course_list;
    private final Operator operator;

    public OperatorGUI(Operator operator) {
        this.operator = operator;
        add(wrapper); // wrapper ekliyoruz.
        setSize(1000, 500); // genişliği ve yüksekliği belirliyoruz.
        // Ekranın ortasını alıyoruz.
        int x = Helper.screenCenterPoint("x", getSize());
        int y = Helper.screenCenterPoint("y", getSize());
        setLocation(x, y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        // giriş yapan kullanıcının adını alıyoruz.
        lbl_welcome.setText("Hoş geldin: " + operator.getName());

        // UserList - Başlangıç
        // modelUserList: kullanıcıları listelemek için bir model oluşturuyoruz.
        mdl_user_list = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) { // bu metod sayesinde bizim tablodaki değerlerimiz elle girilemez olur.
                    return false;
                }
                return super.isCellEditable(row, column);
            }
        };

        Object[] col_user_list = {"ID", "Ad Soyad", "Kullanıcı Adı", "Şifre", "Üyelik Tipi"}; // sütunlarımı isimlendirdim.
        mdl_user_list.setColumnIdentifiers(col_user_list); // oluşturduğum objeyi modelimin sütunu yapıyorum.
        row_user_list = new Object[col_user_list.length];
        loadUserModel(); // veri tabanından çektiğimiz verileri tabloya aktaran metodu çağırıyoruz.
        tbl_user_list.setModel(mdl_user_list); // kendi objemin içine listemi yerleştiriyorum.
        tbl_user_list.getTableHeader().setReorderingAllowed(false); // listemdeki objelerin sıralamasının değiştirilme özelliğini kapatıyorum.

        tbl_user_list.getSelectionModel().addListSelectionListener(e -> { // kullanıcı silerken ID değerimiz elle girilmesin
            try {
                // listener sayesinde tıkladığımız sütunun ID'si ID satırında gözüksün ve onu silebileyim diye oluşturduğumuz listener.
                String selected_user_id = tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 0).toString(); // bu işlem 3.video 55.dakika
                fld_user_id.setText(selected_user_id); // listede tıkladığımız kişinin ID'si ID bölümünde otomatik gözükür.
            } catch (Exception exception) {

            }
        });

        // tabloda manuel yaptığımız değişiklikler kaydolsun diye listener ekliyoruz.
        tbl_user_list.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int user_id = Integer.parseInt(tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 0).toString());
                String user_name = (tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 1).toString());
                String user_uname = (tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 2).toString());
                String user_pass = (tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 3).toString());
                String user_type = (tbl_user_list.getValueAt(tbl_user_list.getSelectedRow(), 4).toString());

                if (User.update(user_id, user_name, user_uname, user_pass, user_type)) {
                    Helper.showMessage("done");
                }
                loadUserModel();
                loadEducatorCombo();
                loadCourseModel();
            }
        });

        // ## UserList - Bitiş

        patikaMenu = new JPopupMenu(); // pop-up menü için nesne üretiyoruz.
        JMenuItem updateMenu = new JMenuItem("Güncelle");
        JMenuItem deleteMenu = new JMenuItem("Sil");
        patikaMenu.add(updateMenu);
        patikaMenu.add(deleteMenu);

        // patika menüsü içinde tıklandığında oluşacak hareket.
        updateMenu.addActionListener(e -> { // seçilen satırın(row) id'sini getirmesi(maviye dönmesi) için alttaki metodu yazıyoruz.
            int select_id = Integer.parseInt(tbl_patika_list.getValueAt(tbl_patika_list.getSelectedRow(), 0).toString());
            // veri tabanında hangi değeri seçti?
            UpdatePatikaGUI updateGUI = new UpdatePatikaGUI(Patika.getFetch(select_id));

            // güncellemek istediğimiz değeri sayfayı yenileyerek tabloya ekleyen listener.
            updateGUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadPatikaModel(); // bir değer güncellediğimizde kendini güncelleyerek o değeri değiştirir.
                    loadPatikaCombo(); // combo içindeki değerleri güncellemesi için.
                    loadCourseModel();
                }
            });
        });

        deleteMenu.addActionListener(e -> { // Patika sekmesinde silme işlemi için oluşturduğumuz listener.
            if (Helper.confirm("sure")) {
                int select_id = Integer.parseInt(tbl_patika_list.getValueAt(tbl_patika_list.getSelectedRow(), 0).toString());
                if (Patika.delete(select_id)) {
                    Helper.showMessage("done");
                    loadPatikaModel();
                    loadPatikaCombo();
                    loadCourseModel();
                } else {
                    Helper.showMessage("error");
                }
            }
        });

        // PatikaList - Başlangıç
        // Ders listesinin tablosunu oluşturuyoruz.
        mdl_patika_list = new DefaultTableModel();
        Object[] col_patika_list = {"ID", "Patika Adı"};
        mdl_patika_list.setColumnIdentifiers(col_patika_list);
        row_patika_list = new Object[col_patika_list.length];
        loadPatikaModel();

        tbl_patika_list.setComponentPopupMenu(patikaMenu); // pop-up menüyü tablomuza attık.

        tbl_patika_list.setModel(mdl_patika_list); // ders liste modelimizi tablo içine setliyoruz.
        tbl_patika_list.getTableHeader().setReorderingAllowed(false); // tabloda ID ile name bölmesinin yerini manuel olarak değiştirememek için.
        tbl_patika_list.getColumnModel().getColumn(0).setMaxWidth(75);  // Patika içinde ID kolonu çok uzundu onu biraz kısalttık.

        tbl_patika_list.addMouseListener(new MouseAdapter() { // patika listesi içinde mouse'un tıklandığı satırın güncellenmesi veya silinmesi işlemi için.
            @Override
            public void mousePressed(MouseEvent e) { // bu metotla patika listesi içinde sağ tıkladığımız yer seçili hale gelip mavi olacak.
                Point point = e.getPoint();
                int selected_row = tbl_patika_list.rowAtPoint(point);
                tbl_patika_list.setRowSelectionInterval(selected_row, selected_row);
            }
        });
        // ## PatikaList - Bitiş

        // CourseList - Başlangıç
        mdl_course_list = new DefaultTableModel();
        Object[] col_course_list = {"ID", "Ders Adı", "Programlama Dili", "Patika", "Eğitmen"};
        mdl_course_list.setColumnIdentifiers(col_course_list);
        row_course_list = new Object[col_course_list.length];
        loadCourseModel();

        tbl_course_list.setModel((mdl_course_list));
        tbl_course_list.getTableHeader().setReorderingAllowed(false); // kolonlar kaydırılamaz.
        tbl_course_list.getColumnModel().getColumn(0).setMaxWidth(75); // kolon uzunluğu en fazla 75.

        loadPatikaCombo(); // combo metodumuzu çağırıyoruz.
        loadEducatorCombo(); // combo metodumuzu çağırıyoruz.

        /*
        cmb_course_patika.addItem(new Item(1,"1.Eleman")); // Dersler kısmında combo boxlarım içinde patika bilgilerini getirme.
        cmb_course_patika.addItem(new Item(2,"2.Eleman"));
        cmb_course_patika.addItem(new Item(3,"3.Eleman")); // aşağıda bunun metodunu yazacağız loadPatikaCombo() ismiyle.
         */
        // ## CourseList - Bitiş


        // butona tıklandığında alacağı aksiyon için otomatik metod.
        btn_user_add.addActionListener(e -> {
            if (Helper.isFieldEmpty(fld_user_name) || Helper.isFieldEmpty(fld_user_uname) || Helper.isFieldEmpty(fld_user_pass)) {
                // Kullanıcı alanları boş bıraktığında hata mesajı alması için yazdığımız metodu çağırıp içine parametrelerimizi veriyoruz.
                // JOptionPane.showMessageDialog(null, "Tüm alanların doldurulması zorunludur!", "Hata", JOptionPane.INFORMATION_MESSAGE);
                // Bir üstteki gibi de yazabiliriz ama birkaç yerde kullanacağımız için metod oluşturup onu çağırıyoruz.
                Helper.showMessage("fill");
            } else { // eğer işlem başarılıysa "done" mesajı gösterimini sağlarız.
                String name = fld_user_name.getText();
                String uname = fld_user_uname.getText();
                String pass = fld_user_pass.getText();
                String type = cmb_user_type.getSelectedItem().toString();
                if (User.add(name, uname, pass, type)) {
                    Helper.showMessage("done");
                    loadUserModel(); // veri tabanındaki kullanıcı tabloya eklendi.
                    loadEducatorCombo(); // combo list'ten eğitmenleri de getiriyor.

                    // kullanıcıyı ekleme işlemi bittikten sonra inputları boşaltmak için null değeri set'liyoruz.
                    fld_user_name.setText(null);
                    fld_user_uname.setText(null);
                    fld_user_pass.setText(null);
                }
            }
        });

        // Sil butonunun action listener'ı
        btn_user_delete.addActionListener(e -> {
            if (Helper.isFieldEmpty(fld_user_id)) {
                Helper.showMessage("fill");
            } else { // silme işlemi yaptığında emin misin diye soran metodu çağırdığımız koşul.
                if (Helper.confirm("sure")) {
                    int user_id = Integer.parseInt(fld_user_id.getText()); // id değerimizi integer bir değere dönüştürdük.
                    if (User.delete(user_id)) {
                        Helper.showMessage("done");
                        loadUserModel();
                        loadEducatorCombo();
                        loadCourseModel();
                        fld_user_id.setText(null); // id değerini eklemeden sonra kutucukta boşaltmak için.
                    } else {
                        Helper.showMessage("error");
                    }
                }
            }
        });

        btn_user_sh.addActionListener(e -> { // Arama yap butonunun action listener'ı
            String name = fld_sh_user_name.getText();
            System.out.println(name);
            String uname = fld_sh_user_uname.getText();
            System.out.println(uname);
            String type = cmb_sh_user_type.getSelectedItem().toString();
            System.out.println(type);
            String query = User.searchQuery(name, uname, type);
            ArrayList<User> searchUser = User.searchUserList(query);
            loadUserModel(searchUser); // bu metoddan 2 tane var. ArrayList olanı çağırdık burada. Arama işlemi için.

        });

        btn_logout.addActionListener(e -> { // çıkış yap butonunun işlevselliğini sağlıyoruz.
            dispose(); // dispose çağrıldığı frame'i kapatır.
            LoginGUI login = new LoginGUI();
        });

        btn_patika_add.addActionListener(e -> { // Patika ders ekleme butonu listener'ı
            if (Helper.isFieldEmpty(fld_patika_name)) {
                Helper.showMessage("fill");
            } else {
                if (Patika.add(fld_patika_name.getText())) {
                    Helper.showMessage("done");
                    loadPatikaModel();
                    loadPatikaCombo();
                    fld_patika_name.setText(null);
                } else {
                    Helper.showMessage("error");
                }
            }
        });

        btn_course_add.addActionListener(e -> { // dersler kısmında ders eklemenin listener'ı
            Item patikaItem = (Item) cmb_course_patika.getSelectedItem();
            Item userItem = (Item) cmb_course_user.getSelectedItem();
            if (Helper.isFieldEmpty(fld_course_name) || Helper.isFieldEmpty(fld_course_lang)) {
                Helper.showMessage("fill");
            } else {
                if (Course.add(userItem.getKey(), patikaItem.getKey(), fld_course_name.getText(), fld_course_lang.getText())) {
                    Helper.showMessage("done");
                    loadCourseModel();
                    fld_course_lang.setText(null); // dersler kısmında ekleme yaptığımızda kutucukları boşaltır.
                    fld_course_name.setText(null); // dersler kısmında ekleme yaptığımızda kutucukları boşaltır.
                } else {
                    Helper.showMessage("error");
                }

            }
        });
    }

    private void loadCourseModel() { // dersleri listeye eklemek için oluşturduğumuz metot
        DefaultTableModel clearModel = (DefaultTableModel) tbl_course_list.getModel();
        clearModel.setRowCount(0);
        for (Course obj : Course.getList()) {
            int i = 0;
            row_course_list[i++] = obj.getId();
            row_course_list[i++] = obj.getName();
            row_course_list[i++] = obj.getLang();
            row_course_list[i++] = obj.getPatika().getName();
            row_course_list[i++] = obj.getEducator().getName();
            mdl_course_list.addRow((row_course_list));
        }
    }

    private void loadPatikaModel() { // patika derslerini listeye eklemek için oluşturduğumuz metot
        DefaultTableModel clearModel = (DefaultTableModel) tbl_patika_list.getModel();
        clearModel.setRowCount(0);
        for (Patika obj : Patika.getList()) {
            int i = 0;
            row_patika_list[i++] = obj.getId();
            row_patika_list[i++] = obj.getName();
            mdl_patika_list.addRow(row_patika_list);
        }
    }

    public void loadUserModel() { // eklediğimiz kullanıcıyı listeye bastırmak için oluşturduğumuz metod.
        // Tablomuzu her defasında silip eklenen yeni kullanıcıyla birlikte tekrar yazdırma işlemi yapıyoruz
        DefaultTableModel clearModel = (DefaultTableModel) tbl_user_list.getModel();
        clearModel.setRowCount(0); // tablodaki tüm değerleri sıfırlar.
        // veritabanından çektiğimiz bilgileri bu tabloya aktarmak için;
        for (User obj : User.getList()) {
            int i = 0;
            row_user_list[i++] = obj.getId();
            row_user_list[i++] = obj.getName();
            row_user_list[i++] = obj.getUname();
            row_user_list[i++] = obj.getPass();
            row_user_list[i++] = obj.getType();
            mdl_user_list.addRow(row_user_list);
        }
    }

    public void loadUserModel(ArrayList<User> list) { // arama kısmında kullanıcıları getirmek için overloading metot yazdık
        // üstteki metoda benzer, farkları çok az. Sadece listemiz değişti.
        DefaultTableModel clearModel = (DefaultTableModel) tbl_user_list.getModel();
        clearModel.setRowCount(0); // tablodaki tüm değerleri sıfırlar.
        // veritabanından çektiğimiz bilgileri bu tabloya aktarmak için;
        for (User obj : list) {
            int i = 0;
            row_user_list[i++] = obj.getId();
            row_user_list[i++] = obj.getName();
            row_user_list[i++] = obj.getUname();
            row_user_list[i++] = obj.getPass();
            row_user_list[i++] = obj.getType();
            mdl_user_list.addRow(row_user_list);
        }
    }

    public void loadPatikaCombo() { // Dersler kısmında combo boxlarım içinde patika bilgilerini getirme.
        cmb_course_patika.removeAllItems(); // combo box içindeki tüm verileri siliyorum.
        for (Patika obj : Patika.getList()) {
            cmb_course_patika.addItem(new Item(obj.getId(), obj.getName()));
        }
    }

    public void loadEducatorCombo() { // Dersler kısmında combo boxlarım içinde öğretmen bilgilerini getirme.
        cmb_course_user.removeAllItems(); // combo box içindeki tüm verileri siliyorum.
        for (User obj : User.getListOnlyEducator()) { // User içinde OnlyEducator metodu oluşturmuştuk onu çağırdık.
            cmb_course_user.addItem(new Item(obj.getId(), obj.getName()));
        }
    }


    public static void main(String[] args) {
        Helper.setLayout();
        Operator op = new Operator();
        op.setId(1);
        op.setName("Erdi Salgın");
        op.setPass("1234");
        op.setType("operator");
        op.setUname("erdi");


        OperatorGUI opGUI = new OperatorGUI(op);
    }
}
