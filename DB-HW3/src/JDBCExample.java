import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.io.*;

public class JDBCExample {
    public static void main(String[] args)
            throws SQLException, IOException {
        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            String host = "dm.hufs.ac.kr:3306/";
            String db = "s202103316db";
            String user = "s202103316";
            String password = getPassword();
            Connection con = DriverManager.getConnection("jdbc:mysql://"
                                                         + host + db +
                                                         "?useSSL=false&serverTimezone=Asia/Seoul", user, password);

            // Perform query using PreparedStatement
            // Write code here

        } catch (SQLException ex) {
            System.out.println("SQLException" + ex);
        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
        }
    }

    private static String getPassword() {
        final String password, message = "Enter password";
        if (System.console() == null) {
            final JPasswordField pf = new JPasswordField();
            password = JOptionPane.showConfirmDialog(null, pf, message,
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION ?
                    new String(pf.getPassword()) : "";
        } else
            password = new String(System.console().readPassword("%s> ", message));

        return password;
    }

    // ReadEntry function -- to read input string
    private static String readEntry(String prompt) {
        try {
            StringBuffer buffer = new StringBuffer();
            System.out.print(prompt);
            System.out.flush();
            int c = System.in.read();
            while (c != '\n' && c != -1) {
                buffer.append((char) c);
                c = System.in.read();
            }
            return buffer.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
}