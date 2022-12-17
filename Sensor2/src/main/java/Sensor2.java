import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

public class Sensor2 {
    static int sensorID = Integer.parseInt(System.getenv("sensorID"));;
    static String valueType = "temperature";

    static public void main(String[] args){
        try {
            MqttClient broker = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId());
            broker.connect();
            while(true) {
                Thread.sleep(5000);
                MqttMessage message = createMqttMessage();
                broker.publish("mqttData", createMqttMessage());
                System.out.println("Daten an Adapter gesendet");
            }
        }catch(MqttException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public int generateTemperature(){
        int temperature = 0;
        Random temperatureGenerator = new Random();
        temperature = temperatureGenerator.nextInt(56)-15;
        return temperature;
    }
    static public MqttMessage createMqttMessage(){
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.GERMANY).format(new java.util.Date());
        String message = "";
        message += sensorID + ",";
        message += valueType + ",";
        message += generateTemperature() + ",";
        message += timestamp;
        MqttMessage newMessage = new MqttMessage(message.getBytes());

        return newMessage;
    }
}
