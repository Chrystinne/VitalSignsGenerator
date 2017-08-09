package vitalsignsgenerator;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

/**
 *
 * @author Chrystinne
 */
public class SaveArduinoOutputRemotely implements SerialPortEventListener {

    SerialPort serialPort;
    SimpleDataDAO simpleDataDAO = new SimpleDataDAO();

    private ArrayList<String> pulseValues = new ArrayList<String>();
    private ArrayList<String> spo2Values = new ArrayList<String>();
    private ArrayList<String> temperatureValues = new ArrayList<String>();
    private ArrayList<String> positionValues = new ArrayList<String>();

    private int repetitions = 0;
    private int numRepetions = 40;
    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
        //	"/dev/tty.usbserial-A9007UX1", // Mac OS X
        //      "/dev/ttyACM0", // Raspberry Pi
        //	"/dev/ttyUSB0", // Linux
        "COM65", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader converting the
     * bytes into characters making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    //private static final int DATA_RATE = 115200;
    private static final int DATA_RATE = 115200;

    public void initialize() {
        System.out.println("PatientDataFromArduinoProducer 0");

        // the next line is for Raspberry Pi and 
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        System.setProperty("gnu.io.rxtx.SerialPorts", "COM65");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port. This will prevent
     * port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();

            System.out.println("close!");
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        SimpleData data = null;
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {

            try {
                String inputLine = input.readLine();

                System.out.println(inputLine);

                String sensors[] = inputLine.split(";");

                for (int i = 0; i < sensors.length; i++) {

                    String values[] = sensors[i].split(":");

                    data = new SimpleData();
                    Timestamp stamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
                    // Date date = new Date(stamp.getTime());
                    data.setTimestamp(stamp);
                    //timestamp sendo setado aqui, como valor fixo

                    data.setIdSensor(Integer.parseInt(values[0]));
                    //simula variacao da posicao na decima leitura

                    data.setValue(values[1]);

                    switch ((i + 1) % 4) {
                        case 0:
                            positionValues.add(data.getValue());
                            break;
                        case 1:
                            temperatureValues.add(data.getValue());
                            break;
                        case 2:
                            pulseValues.add(data.getValue());
                            break;
                        case 3: {
                            spo2Values.add(data.getValue());
                        }
                        break;
                    }

                    repetitions++;

                    if (repetitions == numRepetions - 1) {
                        String simulacao = simulaVariacao(data);
                        if (simulacao != null) {
                            data.setValue(simulacao);
                            System.out.println("Gerando variacao simulada: " + simulacao);
                        }

                        repetitions = 0;

                        System.out.println("Limpando buffer");

                        limpaListas();
                    }

                    simpleDataDAO.inserir(data);
                }

            } catch (Exception e) {

                System.err.println(e.toString());
            }

        }

    }

    public void limpaListas() {
        pulseValues.clear();
        temperatureValues.clear();
        spo2Values.clear();
        positionValues.clear();
    }

    public String simulaVariacao(SimpleData simpleData) {
        String variation = null;
        switch (simpleData.getIdSensor() % 4) {
            case 0: {
                if (!variou(positionValues)) {
                    variation = "right position";
                }
                positionValues.add(variation);
                break;
            }
            case 1:
                if (!variou(temperatureValues)) {
                    variation = "22.22";
                }
                temperatureValues.add(variation);
                break;

            case 2:
                if (!variou(pulseValues)) {
                    variation = "88";
                }
                pulseValues.add(variation);
                break;
            case 3:
                if (!variou(spo2Values)) {
                    variation = "95";
                }
                spo2Values.add(variation);
                break;

        }
        return variation;
    }

    public static void main(String[] args) throws InterruptedException {

        SaveArduinoOutputRemotely main = new SaveArduinoOutputRemotely();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ie) {
                }
            }
        };
        t.start();
        System.out.println("Started");

    }

    private boolean variou(ArrayList<String> sensorValues) {
        String firstValue = null;
        boolean variou = false;
        if (!sensorValues.isEmpty()) {
            firstValue = sensorValues.get(0);
        } else {
            System.out.println("lista de sensores esta vazia");
            return false;
        }

        for (int i = 1; i < sensorValues.size(); i++) {
            if (!sensorValues.get(i).equals(firstValue)) {
                variou = true;
            }
        }
        return variou;
    }

    public void imprime() {

        System.out.println(" lenght pulse: " + pulseValues.size());
        System.out.println(" lenght spo2: " + spo2Values.size());
        System.out.println(" lenght temperature: " + temperatureValues.size());
        System.out.println(" lenght position: " + positionValues.size());
        for (int i = 0; i < pulseValues.size(); i++) {
            System.out.println(" pulseValues: " + pulseValues.get(i));
        }
        for (int i = 0; i < spo2Values.size(); i++) {
            System.out.println(" spo2Values: " + spo2Values.get(i));
        }
        for (int i = 0; i < temperatureValues.size(); i++) {
            System.out.println(" temperatureValues: " + temperatureValues.get(i));
        }
        for (int i = 0; i < positionValues.size(); i++) {
            System.out.println(" positionValues: " + positionValues.get(i));
        }

    }

}
