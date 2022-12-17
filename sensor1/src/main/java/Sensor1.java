

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class Sensor1{

    static DatagramSocket socket;
    static byte[] rcvData = new byte[512];
    static byte[] sendData = new byte[512];
    static InetAddress ipAdr;
    static String metaData;
    static String dstAdr;
    static String srcAdr;
    static String messageID;
    static String messageType;

    static String valueType = "humidity";
    static String humidity;

    static String timeStamp;
    static String port;



    static int sensorID = Integer.parseInt(System.getenv("sensorID"));


    public static void openSocket() throws SocketException {
        System.out.println("socket will be opened");
        socket = new DatagramSocket(6969);
        System.out.println("socket was opened");
    }

    public void closeSocket(){
        socket.close();
    }

    public static void receiveDataFunc() throws IOException {
        // System.out.println("data will be recieved");
        DatagramPacket receivedPacket =  new DatagramPacket(rcvData, rcvData.length);

        socket.receive(receivedPacket);
        ipAdr = receivedPacket.getAddress();
        metaData = new String(receivedPacket.getData());

        String tmp;
        String[] metaDataList = metaData.split(",");
        srcAdr = metaDataList[0]; //Ip Adress from Sensor
        dstAdr = metaDataList[1]; // IP Adress from Gateway
        port = metaDataList[2]; //port from Gateway
        messageID = metaDataList[3];
        messageType = "Humidity";

        Random r1 = new Random();
        humidity = String.valueOf(r1.nextInt(91) + 10);
        //System.out.println("Current Humidity: " + humidity);
        //   System.out.println("data was recieved");
    }
    public static void sendDataFunc() throws IOException {
        // System.out.println("data will be send");

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.GERMANY).format(new java.util.Date());
        String sentence = srcAdr.toUpperCase() + "," + dstAdr.toUpperCase() + "," + String.valueOf(port) + "," +
                String.valueOf(messageID) + "," + sensorID + "," + messageType + "," + humidity + "," + timeStamp + ",";
        sendData = sentence.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAdr, Integer.parseInt(port));
        socket.send(sendPacket);
        // System.out.println("data was send");
    }

    public static void generateData(){

    }

    public static void main(String args[]){
        try{
            openSocket();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        while(true){
            try{
                receiveDataFunc();
                sendDataFunc();
            }
            catch (IOException e){
                System.out.println(e.getMessage());
            }
        }
    }
}
