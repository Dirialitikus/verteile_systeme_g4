import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Coordinator implements crudService.Iface {
    int numberOfDatabases = 2;
    static crudService.Processor processor = new crudService.Processor(new Coordinator());
    @Override
    public void create(Dataset newDataset) throws TException, UnknownHostException {

        boolean isCommitable = true;
        boolean tmpBoolean = true;
        for (int i = 1; i < numberOfDatabases + 1; i++) {

            TTransport transport;
            transport = new TSocket(InetAddress.getByName("db" + i).toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            CrudPcaService.Client client = new CrudPcaService.Client(protocol);

            tmpBoolean = client.prepare(newDataset);

            if (tmpBoolean == false) {
                isCommitable = false;
            }
            System.out.println("Prepare was true on" + InetAddress.getByName("db" + i).toString().split("/")[1]);

            transport.close();
        }

            for (int i = 1; i < numberOfDatabases + 1; i++) {
                TTransport transport;
                transport = new TSocket(InetAddress.getByName("db" + i).toString().split("/")[1], 9090);
                transport.open();

                TProtocol protocol = new TBinaryProtocol(transport);
                CrudPcaService.Client client = new CrudPcaService.Client(protocol);

                if(isCommitable == true) {

                    client.commit(newDataset.primaryKey);
                    System.out.println("Dataset was commited on " + InetAddress.getByName("db" + i).toString().split("/")[1]);
                }
                else {
                    client.abort(newDataset.primaryKey);
                }
                transport.close();
            }

    }
    @Override
    public Dataset read(int primaryKey) throws TException, UnknownHostException {
        for (int i = 1; i < numberOfDatabases + 1; i++) {

            TTransport transport;
            transport = new TSocket(InetAddress.getByName("db" + i).toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            CrudPcaService.Client client = new CrudPcaService.Client(protocol);

            Dataset getDataset = client.read(primaryKey);
            transport.close();
            if(getDataset != null){
                return getDataset;
            }

        }
        return null;
    }

    @Override
    public void update(Dataset updatedDataSet) throws TException, UnknownHostException {
        for (int i = 1; i < numberOfDatabases + 1; i++) {

            TTransport transport;
            transport = new TSocket(InetAddress.getByName("db" + i).toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            CrudPcaService.Client client = new CrudPcaService.Client(protocol);

            client.update(updatedDataSet);
            transport.close();
        }
    }

    @Override
    public void remove(int primaryKey) throws TException, UnknownHostException {
        for (int i = 1; i < numberOfDatabases + 1; i++) {

            TTransport transport;
            transport = new TSocket(InetAddress.getByName("db" + i).toString().split("/")[1], 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            CrudPcaService.Client client = new CrudPcaService.Client(protocol);

            client.remove(primaryKey);
            transport.close();

        }
    }

   public static void main(String[] args){
       TServer server;
       TServerTransport serverTransport;
       try {
           serverTransport = new TServerSocket(9090);
           server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
           server.serve();
       }catch(TTransportException e){
           e.printStackTrace();
       }
    }
}
