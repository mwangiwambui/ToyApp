package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import sample.Client.SocketClient;
import sample.Models.Message;
import sample.Models.Toy;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class ClientProtocol implements Initializable {

    @FXML
    public JFXTextField portNumber;

    @FXML
    public JFXTextField serverName;

    @FXML
    public JFXButton btnConnect;

    @FXML
    public JFXTextArea messageBoard;

    @FXML
    public JFXTextField toyField;

    @FXML
    public JFXButton sendMessage;

    @FXML
    public JFXRadioButton clientStateRadioButton;


    private SocketClient socketClient;

    public void connectToServer(ActionEvent actionEvent){
        if(!portNumber.getText().isEmpty() && !serverName.getText().isEmpty()){
            socketClient = new SocketClient(Integer.parseInt(portNumber.getText()),serverName.getText(),this);
            socketClient.connectToServer();

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All Fields Required!");
            alert.showAndWait();

        }

    }

    public void sendToy(ActionEvent actionEvent){
        if(!toyField.getText().isEmpty()){
            String message = toyField.getText().trim();
            Toy clientToy = new Toy();
            clientToy.setCustomMessage(message);

            if (message.contains("Thank you") || message.contains("Thanks")) {
                UUID uuid = UUID.randomUUID();
                message= message.concat("-"+uuid.toString());
                clientToy.setName(message);

            }
            try{
                messageBoard.appendText("|"+Util.Companion.getCurrentDateTime()+"| "+"SocketClient : "+ message +"\n");
                socketClient.sendToy(clientToy);
                toyField.clear();
            }catch (IOException exception){
                exception.printStackTrace();
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Server name required!",ButtonType.CLOSE,ButtonType.CANCEL);
            alert.showAndWait();

        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnConnect.setOnAction(this::connectToServer);
        sendMessage.setOnAction(this::sendToy);

    }

    public void postMessage(Message message){
        messageBoard.appendText("|"+Util.Companion.getCurrentDateTime()+"| "+message.getMessage()+"\n");
    }

    @FXML
    public void openForm(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Toy.fxml"));
        try {
            Parent root= fxmlLoader.load();
            TFormController tFormController = fxmlLoader.<TFormController>getController();
            tFormController.setClient(socketClient);
            Stage stage = new Stage();
            stage.setTitle("Toy Form");
            stage.setScene(new Scene(root,600,450));
            stage.setResizable(false);
            stage.show();
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }

    public void printToyObject(Toy toyObject){
        messageBoard.appendText("|"+Util.Companion.getCurrentDateTime()+"|"+" SocketClient : "+Util.Companion.printToyObject(toyObject)+"\n");
    }
}
