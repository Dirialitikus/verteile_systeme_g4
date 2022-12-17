import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Adapter implements MqttCallback {
    static Adapter adapter;
    static int port = 6969;
   static int messageID = 0;
    static DatagramSocket clientSocket;
    IMqttMessageListener iMqttMessageListener;

    InetAddress dstAddress;
    static ArrayList<String> messageBuffer = new ArrayList<>();

    public Adapter() throws SocketException {
    }

    public void subscribeToMQTTChannel() throws MqttException, InterruptedException {
        MqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId());
        MqttConnectOptions connOptions = new MqttConnectOptions();
        connOptions.setCleanSession(true);
        connOptions.setAutomaticReconnect(true);
        //client.setCallback(this);
        iMqttMessageListener = new IMqttMessageListener() {
            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String message = mqttMessage.toString();
                messageBuffer.add(message);
            }
        };


        client.connect(connOptions);
        Thread.sleep(2000);
        client.subscribe("mqttData", iMqttMessageListener);
        System.out.println("Subscribed to Broker");
    }
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {


    }
    static public DatagramPacket createDatagramPacket(String message) throws UnknownHostException {
        String srcAdr, dstAdr;
        InetAddress src = InetAddress.getByName("adapter");
        srcAdr = src.toString().split("/")[1];

        InetAddress dst = InetAddress.getByName("iotgateway");
        dstAdr = dst.toString().split("/")[1];

        String sentence = srcAdr.toUpperCase() + "," + dstAdr.toUpperCase() + "," + port + "," +
                messageID++ + "," + message + ",";

        byte[] byteMessage = new byte[512];
        byteMessage = sentence.getBytes();
        DatagramPacket sendData = new DatagramPacket(byteMessage, byteMessage.length, dst, 6969);


        return sendData;

    }

    static public DatagramPacket createErrorMessage() throws UnknownHostException {
        String error = "ERROR";
        byte[] byteMessage = new byte[512];
        byteMessage = error.getBytes();
        InetAddress dst = InetAddress.getByName("iotgateway");
        System.out.println("Gateway? " + dst);
        DatagramPacket errorPacket = new DatagramPacket(byteMessage, byteMessage.length, dst, 6969);

        return errorPacket;
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }

    static public void main(String[] args) throws IOException, MqttException, InterruptedException {

        clientSocket = new DatagramSocket(6969);
        adapter = new Adapter();
        adapter.subscribeToMQTTChannel();
        byte[] byteArray = new byte[512];

        DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length);
        Thread.sleep(5000);

        while(true) {
                clientSocket.receive(packet);
                DatagramPacket newDat = createDatagramPacket(messageBuffer.get(0));
                messageBuffer.remove(0);
                clientSocket.send(newDat);
                System.out.println("Erfolgreich umgewandelt und abgesendet");
        }
    }
}
