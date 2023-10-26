package com.patikadev.Helper;

import javax.swing.*;
import java.awt.*;

public class Helper {
    public static void setLayout() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }

    public static int screenCenterPoint(String axis, Dimension size) {
        int point;
        switch (axis) {
            case "x":
                point = (Toolkit.getDefaultToolkit().getScreenSize().width - size.width) / 2;
                break;
            case "y":
                point = (Toolkit.getDefaultToolkit().getScreenSize().height - size.height) / 2;
                break;
            default:
                point = 0;
        }
        return point;
    }

    public static boolean isFieldEmpty(JTextField field) { // kullanıcının alanları boş bırakıp bırakmadığını sorgulayan metod
        return field.getText().trim().isEmpty();
    }

    public static void showMessage(String str) { // kullanıcının alanları boş bırakması halinde alacağı mesajları için yazılan metod
        optionPageLangTR(); // buton değişikliğimiz
        String msg;
        String title;
        switch (str) {
            case "fill":
                msg = "Tüm alanların doldurulması zorunludur!";
                title = "Hata!";
                break;
            case "done":
                msg = "İşlem Başarılı!";
                title = "Sonuç";
                break;
            case "error":
                msg = "Hata Oluştu!";
                title = "Hata!";
            default:
                msg = str;
                title = "Mesaj";
        }
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(String str) { // silme işlemi yapmak istediğinde emin misin? diye soran metot.
        optionPageLangTR();
        String msg;
        switch (str) {
            case "sure":
                msg = "Bu işlemi yapmak istediğinize emin misini?";
                break;
            default:
                msg = str;
        }
        return JOptionPane.showConfirmDialog(null, msg, "Son kararınız mı?", JOptionPane.YES_NO_OPTION) == 0;
    }

    public static void optionPageLangTR() { // butondaki yazıyı değiştirmek için oluşturduğumuz metod
        UIManager.put("OptionPane.okButtonText", "Tamam"); // butonda yazan "OK" ifadesini "Tamam" olarak değiştirdik.
        UIManager.put("OptionPane.yesButtonText", "Evet"); // silme işlemi sonrası butonda yazan "YES" ifadesini "EVET" olarak değiştirdik.
        UIManager.put("OptionPane.noButtonText", "Hayır"); // silme işlemi sonrası butonda yazan "NO" ifadesini "HAYIR" olarak değiştirdik.
    }
}
