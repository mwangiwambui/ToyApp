package sample.Client;

import sample.Models.Message;
import sample.Models.Toy;
import sample.ClientProtocol;

import java.io.*;
import java.net.Socket;

public class SocketClient {
    private int serverPort;
    private String serverName;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private ClientProtocol clientProtocol;
    private ObjectOutputStream objectOutputStream;
    private OutputStream outputStream;

    public SocketClient(int port, String name, ClientProtocol clientProtocol){
        serverPort = port;
        serverName = name;
        this.clientProtocol = clientProtocol;
    }

    public void connectToServer(){
        Runnable serverTask = () -> {
            try {
                Socket clientSocket = new Socket(serverName,serverPort);
                System.out.println("SocketClient Connected...");
                clientProtocol.clientStateRadioButton.setSelected(true);
                clientProtocol.clientStateRadioButton.setDisable(false);
                inputStream = clientSocket.getInputStream();
                objectInputStream= new ObjectInputStream(inputStream);
                outputStream = clientSocket.getOutputStream();
                objectOutputStream = new ObjectOutputStream(outputStream);
                while (clientSocket.getInputStream()!=null){
                    Message message = (Message)objectInputStream.readObject();
                    clientProtocol.postMessage(message);
                }

            } catch (IOException e) {
                System.err.println("An error occurred");
                e.printStackTrace();
            }catch (ClassNotFoundException classNotFound){
                classNotFound.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    public void sendToy(Toy clientToy) throws IOException{
        System.out.println("SocketClient Sending ......:"+ clientToy.getName());
        objectOutputStream.writeObject(clientToy);
    }

    public void sendToyForm(Toy clientToy) throws IOException{
        clientProtocol.printToyObject(clientToy);
        System.out.println("SocketClient Sending ......:"+ clientToy.getName());
        objectOutputStream.writeObject(clientToy);

    }
}
