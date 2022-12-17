





import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


//TimeUnit.SECONDS.sleep(1);


class IoT {
    int serverPort = 1337;

    static int rttCounter = 0;
    static InetAddress serverAdr;

    static {
        try {
            serverAdr = InetAddress.getByName("server");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    static int sensorCount = Integer.parseInt(System.getenv("numberOfSensors"));


    static int messageId = 0;
    static DatagramSocket clientSocket = null;
    static DatagramSocket mqttSocket = null;
    static String messageTypeForSensor = "post";
    static final int sensorPort = 4242;
    static final String GatewayIPAdr;
    static int messageIDForTCP = 0;
    static String sentence = "";



    static {
        try {
            GatewayIPAdr = InetAddress.getByName("iotgateway").toString().split("/")[1];
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    IoT() throws UnknownHostException {
    }

    public static void main(String args[]) throws Exception {

        clientSocket = new DatagramSocket(6969);

        try {
            while (true) {
                for(int i = 0; i < sensorCount; i++) {
                        String datagramFromSensor = "";
                        datagramFromSensor = sendDataToSensors(InetAddress.getByName("sensor" + i), rttCounter, 6969);
                        TCPThread serverCommunication = new TCPThread(datagramFromSensor);
                        serverCommunication.start();}
                for(int i = 0; i < 4; i++) {
                        String datagramFromMQTTSensor = "";
                        datagramFromMQTTSensor = sendDataToSensors(InetAddress.getByName("adapter"), rttCounter, 6969);
                        TCPThread serverCommunication = new TCPThread(datagramFromMQTTSensor);
                        serverCommunication.start();
                    }
                    Thread.sleep(10000);
                }

        } catch (SocketException e) {
            System.out.println(e.getMessage());

        } catch (IOException a) {
            System.out.println(a.getMessage());
        }
        clientSocket.close();
    }

    private static synchronized String sendDataToSensors(InetAddress dstIPAdr, int rttCounter, int port) throws IOException, InterruptedException {
        byte[] sendData = new byte[512];
        byte[] receiveData = new byte[512];

        String sentence = GatewayIPAdr + "," + dstIPAdr + "," + "6969" + "," + String.valueOf(messageId++) + "," + messageTypeForSensor + ",";

        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dstIPAdr, port);


        clientSocket.send(sendPacket);
        System.out.println("Packet was send to Sensor: " + dstIPAdr);
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.setSoTimeout(20000);
        clientSocket.receive(receivePacket);

        if(receivePacket.toString().equals("ERROR")){
            return "";
        }

        String modifiedSentence = new String(receivePacket.getData());
        String[] modifiedSentenceArr = modifiedSentence.split(",");
        modifiedSentence = "";

        for (int i = 0; i < modifiedSentenceArr.length - 1; i++)
            modifiedSentence = modifiedSentence + modifiedSentenceArr[i] + ",";

        System.out.println("Data from Sensor:" + modifiedSentence);


        String receivePacketString = new String(receivePacket.getData());
        String[] messageArray = receivePacketString.split(",");
        String completeMessage = "";

        for (int i = 4; i < messageArray.length - 1; i++) {
            completeMessage += messageArray[i] + ",";
        }

        return completeMessage;
    }


    // Zwei Punkte werden gesucht im Raum. Der Abstand dieser Punkte ist auch Abhängig von der Distanz
    public void receiveMqttData(){

    }


    public static String createMessage(String host, String message) {
        String httpFormat =
                "POST / HTTP/1.1\n" +
                        "Host:" + host + "\n" +
                        "User-Agent: iot_gate_way_group_4\n" +
                        "Accept: Yes\n" +
                        "Content-Length:" + message.length() + "\n" +
                        "Content-Type:text/plain\n\n" +
                        message + "\n";

        return httpFormat;
    }
}
