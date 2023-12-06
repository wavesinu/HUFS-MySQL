import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class ConnectTest {
    public static void main(String[] args)
            throws SQLException, IOException {
        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            String host = "localhost:3306/";
            String db = "companydb";
            String user = "root";
            String password = getPassword();

            try (Connection con = DriverManager.getConnection("jdbc:mysql://" + host + db +
                                                              "?useSSL=false&serverTimezone=Asia/Seoul", user, password)) {
                try (Statement stmt = con.createStatement()) {
                    stmt.execute("CREATE TEMPORARY TABLE tempssn (ssn CHAR(9), level INT) ENGINE=MEMORY");
                }

                String initialSSN = readEntry("Enter a ssn: ");
                try (PreparedStatement pstmt = con.prepareStatement("INSERT INTO tempssn (ssn, level) VALUES (?, 0)")) {
                    pstmt.setString(1, initialSSN);
                    pstmt.executeUpdate();
                }

                int currentLevel = 0;
                boolean found;
                do {
                    found = false;
                    String query = "SELECT e.Ssn, t.level " +
                                   "FROM e INNER JOIN tempssn t ON e.Superssn = t.ssn" +
                                   "WHERE t.level = "
                                   + currentLevel;

                    try (Statement stmt = con.createStatement();
                         ResultSet rs = stmt.executeQuery(query)) {

                        while (rs.next()) {
                            found = true;

                            String subordinateSSN = rs.getString("Ssn");
                            int subordinateLevel = rs.getInt("level") + 1;

                            System.out.println(subordinateSSN + " at level " + subordinateLevel);

                            try (PreparedStatement pstmt = con.prepareStatement
                                    ("INSERT INTO tempssn (ssn, level) VALUES (?, ?)")) {
                                pstmt.setString(1, subordinateSSN);
                                pstmt.setInt(2, subordinateLevel);
                                pstmt.executeUpdate();
                            }
                        }
                    }
                    currentLevel++;
                } while (found);

                try (Statement stmt = con.createStatement()) {
                    stmt.execute("DROP TEMPORARY TABLE IF EXISTS tempssn");
                } catch (SQLException ex) {
                    System.out.println("SQLException: " + ex.getMessage());
                }
            }
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