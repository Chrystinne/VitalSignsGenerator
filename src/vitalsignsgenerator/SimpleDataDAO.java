/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vitalsignsgenerator;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chrystinne
 */
public class SimpleDataDAO {
    

    ArrayList<SimpleData> listSimpleData = new ArrayList<SimpleData>();
    
    public SimpleDataDAO() {

    }

    
    public boolean inserir(SimpleData SimpleData) throws Exception {
        boolean erros = false;
         String sql2 = "INSERT INTO public.PATIENTDATA(            value, \"timestamp\", \"MONIT_idMonitoring\")            VALUES ('" + SimpleData.getValue() + "', '" + SimpleData.getTimestamp() + "', '" + SimpleData.getIdSensor() + "')";
  
        boolean sw = false;
        Connection con = null;
        Statement stmt = null;
        PreparedStatement pst = null;
        
        try {
            
            sw = ((con == null) || (con.isClosed()));
            con = (sw) ? ConexaoDB.openDBConnection() : con;

            
            //Insere novos valores na tabela PATIENTDATA
            PreparedStatement ps = con.prepareStatement(sql2);
            ps.executeUpdate();
           ps.close();
          
        } catch (Exception ex) {
            erros = true;
            System.out.println("Erro ao executar insert no bd. \n Error: " + ex);
        } finally {
            try {
                if (sw && con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (Exception ex) {
                erros = true;
                System.out.println("Erro ao executar insert no bd. \n Error: " + ex);
            }
        }

        if (erros) {
            System.out.println("Erro ao cadastrar dado de sensor.");
            return false;
        } else {
            System.out.println("Dado de sensor inserido com sucesso.");
            return true;
        }
    }
    
    
    public void listarSensores() {
        Iterator<SimpleData> it = listSimpleData.iterator();
        while (it.hasNext()) {
            SimpleData simpleData = (SimpleData) it.next();
            imprimeSensor(simpleData);
        }
    }

    public void imprimeSensor(SimpleData simpleData) {
        System.out.println("\nImprimindo dados do Sensor:");
        System.out.println("Simple data value: " + simpleData.getValue());

    }

    public ArrayList<SimpleData> getListSensores() {
        return listSimpleData;
    }

    public void setListSensores(ArrayList<SimpleData> listSimpleData) {
        this.listSimpleData = listSimpleData;
    }

}
