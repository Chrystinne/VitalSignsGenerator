/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vitalsignsgenerator;


import java.sql.Timestamp;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chrystinne
 */
public class SimpleData {
    
    private int idSimpleData;
    private int idSensor;
    private Timestamp timestamp ;
    private String value;

    public SimpleData() {
    }

    public SimpleData(Timestamp timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getIdSimpleData() {
        return idSimpleData;
    }

    public void setIdSimpleData(int idSimpleData) {
        System.out.println("idSimpleData = "+ idSimpleData);
        this.idSimpleData = idSimpleData;
    }

    public int getIdSensor() {
        return idSensor;
    }

    public void setIdSensor(int idSensor) {
        this.idSensor = idSensor;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
    
}
