/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vitalsignsgenerator;

/**
 *
 * @author tassio
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
     */

    /**
     *
     * @author Chrystinne
     */
    private static String driver = "org.postgresql.Driver";
    private static String user = "dev";
    private static String senha = "dev2017";
    private static String url = "jdbc:postgresql://139.82.24.122:5432/dev";

    public static void main(String[] args) throws SQLException {

    }

    public static Connection openDBConnection() {

        Connection conneccion = null;
        try {
            Class.forName(driver).newInstance();
            conneccion = DriverManager.getConnection(url, user, senha);
        } catch (Exception ex) {
            System.out.println("Nao foi possivel estabelecer conexao:" + driver + " url:" + url + " user:" + user + " pass:" + senha + "Error: " + ex);

        }
        return conneccion;
    }
}
