package com.patikadev.Model;

import com.mysql.cj.protocol.Resultset;
import com.patikadev.Helper.DBConnector;
import com.patikadev.Helper.Helper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

public class User {
    private int id;
    private String name;
    private String uname;
    private String pass;
    private String type;

    public User() {
    }

    public User(int id, String name, String uname, String pass, String type) {
        this.id = id;
        this.name = name;
        this.uname = uname;
        this.pass = pass;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static ArrayList<User> getList() { // bir User listesi oluşturup onu ArrayList'e atmak için fonksiyon oluşturuyoruz.
        ArrayList<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user"; // sorgulama işlemi için mysql query'sini burada tanımlayıp executequery içinde çağırıyoruz.
        User obj; // while döngüsü içinde yeni değerler atamak için bir obje üretiyoruz.
        try {
            Statement st = DBConnector.getInstance().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                obj = new User();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
                userList.add(obj);
            }
            st.close();
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public static boolean add(String name, String uname, String pass, String type) {
        // kullanıcıları operator,educator ya da student olarak listeye eklemek için metot ekliyoruz.
        String query = "INSERT INTO user (name,uname,pass,type) VALUES (?,?,?,?) ";
        User findUser = User.getFetch(uname); // eğer kullanıcı ismi zaten varsa bu kullanıcı ekleyemeyiz.
        if (findUser != null) {
            Helper.showMessage("Bu kullanıcı adı daha önce alınmıştır. Lütfen yeni bir kullanıcı adı belirleyiniz!");
            return false;
        }
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setString(1, name);
            pr.setString(2, uname);
            pr.setString(3, pass);
            pr.setString(4, type);

            int response = pr.executeUpdate();
            if (response == -1) {
                Helper.showMessage("error"); // eğer kullanıcı adı daha önce alınmışsa kullanıcıyı eklemiyoruz ve hata mesajı veriyoruz.
            }
            return response != -1; // bu şekilde kullanıcının bilgilerini database'e göndererek ekleme işlemini yapıyoruz.
            // -1 değilse içi dolu demektir.
        } catch (SQLException e) {
            e.getMessage();
        }
        return true;
    }

    // veri tabanından tek bir veri çekme işlemine "fetch" denir.
    // aynı kullanıcı adıyla başka bir kişinin daha oluşturulmasını engellemek için metot oluşturuyoruz.
    public static User getFetch(String uname) {
        User obj = null;
        String query = "SELECT * FROM user WHERE uname = ?";
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setString(1, uname); // Username'i aynıysa
            ResultSet rs = pr.executeQuery();
            if (rs.next()) { // eğer bu kullanıcı adı hali hazırda varsa döngüye girmiyor ve eklenmiyor.
                obj = new User();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static User getFetch(String uname, String pass) { // login ekranı için
        User obj = null;
        String query = "SELECT * FROM user WHERE uname = ? AND pass = ?";
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setString(1, uname); // Username'i aynıysa
            pr.setString(2, pass);

            ResultSet rs = pr.executeQuery();
            if (rs.next()) { // eğer bu kullanıcı adı hali hazırda varsa döngüye girmiyor ve eklenmiyor.
                switch (rs.getString("type")) {
                    case "operator":
                        obj = new Operator();
                        break;
                    default:
                        obj = new User();
                }
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static User getFetch(int id) { // id için ayrıca bir fetch oluşturuyoruz.
        User obj = null;
        String query = "SELECT * FROM user WHERE id = ?";
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setInt(1, id);
            ResultSet rs = pr.executeQuery();
            if (rs.next()) {
                obj = new User();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static boolean delete(int id) { // silme işlemini yapmak için oluşturduğumuz metod.
        String query = "DELETE FROM user WHERE id=?"; // belirli id'de olanları silme işleminin sql sorgusu
        ArrayList<Course> courseList = Course.getListByUser(id); // o kullanıcının kurslarını da listeliyor.
        for (Course c : courseList) {
            Course.delete(c.getId());
        }

        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setInt(1, id);

            return pr.executeUpdate() != -1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public static boolean update(int id, String name, String uname, String pass, String type) {
        // tabloda manuel olarak değiştirdiğimiz değerlerin güncellenmesi için yazdığımız metod
        String query = "UPDATE user SET name=?,uname=?,pass=?,type=? WHERE id=?";
        User findUser = User.getFetch(uname); // eğer kullanıcı ismi zaten varsa bu kullanıcı ismini manuel olarak da ekleyemeyiz.
        if (findUser != null && findUser.getId() != id) {
            Helper.showMessage("Bu kullanıcı adı daha önce alınmıştır. Lütfen yeni bir kullanıcı adı belirleyiniz!");
            return false;
        }

        if (findUser != null && !Objects.equals(findUser.getType(), type)) {
            Helper.showMessage("Üyelik tipinizi daha sonra değiştiremezsiniz!");
            return false;
        }
        try {
            PreparedStatement pr = DBConnector.getInstance().prepareStatement(query);
            pr.setString(1, name);
            pr.setString(2, uname);
            pr.setString(3, pass);
            pr.setString(4, type);
            pr.setInt(5, id);
            return pr.executeUpdate() != -1;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public static ArrayList<User> searchUserList(String query) { // arama yap butonunun işlevselliğini oluşturduğumuz metot.
        ArrayList<User> userList = new ArrayList<>();
        User obj;
        try {
            Statement st = DBConnector.getInstance().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                obj = new User();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
                userList.add(obj);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public static String searchQuery(String name, String uname, String type) { // Arama yapma metodunu oluşturuyoruz.
        String query = "SELECT * FROM user WHERE uname LIKE '%{{uname}}%' AND name LIKE '%{{name}}%'";
        // Yukarıdaki query içinde kullandığımız "LIKE" arama yaparken belirli harfleri yazdığımızda geri kalanını otomatik tamamlaması için yazılır.
        query = query.replace("{{uname}}", uname);
        query = query.replace("{{name}}", name);
        if (!type.isEmpty()) {
            query += "AND type='{{type}}'";
            query = query.replace("{{type}}", type);
            System.out.println(query);
        }
        return query;
    }

    public static ArrayList<User> getListOnlyEducator() { // bu metod da bize sadece educator'ları getirir.
        ArrayList<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user WHERE type= 'educator'"; // sorgulama işlemi için mysql query'sini burada tanımlayıp executequery içinde çağırıyoruz.
        User obj; // while döngüsü içinde yeni değerler atamak için bir obje üretiyoruz.
        try {
            Statement st = DBConnector.getInstance().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                obj = new User();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUname(rs.getString("uname"));
                obj.setPass(rs.getString("pass"));
                obj.setType(rs.getString("type"));
                userList.add(obj);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }


}
