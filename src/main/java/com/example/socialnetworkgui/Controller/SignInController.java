package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.HelloApplication;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.MessageService;
import com.example.socialnetworkgui.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

public class SignInController {
    UserService service;
    FriendshipService friendService;
    MessageService messageService;
    Long indexCurent;

    private Stage stage;
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField passwd;

    @FXML

    private TextField confirmPassword;

    @FXML
    private DatePicker birthday;

    public void setServices(UserService service, FriendshipService friendService,MessageService messageService,Stage stage) {
        this.service = service;
        this.friendService = friendService;
        this.stage = stage;
        this.messageService = messageService;
        loadGlobalIndex();
    }



    public void signUser(ActionEvent event) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String frsttName = firstName.getText();
        String lstName = lastName.getText();
        String email = this.email.getText();
        String password = this.passwd.getText();
        String confirmPassword = this.confirmPassword.getText();
        if (frsttName.isEmpty() || lstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            openErrorWindow("Invalid credentials");
        } else {
            if (password.equals(confirmPassword)) {
                LocalDate birthdayDate;
                if (this.birthday.getValue() == null) {
                    openErrorWindow("You must choose your birthday");
                } else {
                    birthdayDate = LocalDate.parse(this.birthday.getValue().toString(), formatter);
                    service.addUser(indexCurent, frsttName, lstName, birthdayDate, email, password);
                    indexCurent += 1;
                    saveCurentIndex();
                    User usr = new User(frsttName, lstName, birthdayDate, email, password);
                    usr.setId(indexCurent - 1);
                    stage.close();
                    openWindowUser(usr);
                }
            } else {
                openErrorWindow("Passwords do not match");
            }
        }
    }
    public void handleBack(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/LoginWindow.fxml"));
        AnchorPane userLayout = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();
        loginController.setLoginService(service,friendService,messageService,stage);
        stage.setScene(new Scene(userLayout));
        stage.setWidth(500);
        stage.setHeight(340);
        stage.setResizable(false);
    }

    private void openWindowUser(User user) throws IOException {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/UserView.fxml"));
        AnchorPane root = loader.load();

        Stage primaryStage = new Stage();
        UserController controller = loader.getController();
        controller.setUtilizatorService(service,friendService,messageService,user,primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Utilizator");
        primaryStage.setWidth(900);
        primaryStage.setHeight(520);
        primaryStage.show();

    }

    private void saveCurentIndex() {
        Path path = Paths.get("D:\\Facultate\\MAP\\SocialNetworkGUI\\src\\main\\resources\\com\\example\\socialnetworkgui\\globalIndex.txt");

        try {
            // Write the current global index to the file
            Files.writeString(path, indexCurent.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error saving current index to file" , e);
        }
    }

    private void loadGlobalIndex() {
        Path path = Paths.get("D:\\Facultate\\MAP\\SocialNetworkGUI\\src\\main\\resources\\com\\example\\socialnetworkgui\\globalIndex.txt");

        try {

            if (!Files.exists(path)) {
                Files.write(path, "0".getBytes());
                indexCurent = 0L;
            } else {

                String content = Files.readString(path).trim();
                indexCurent = Long.parseLong(content);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid data in file " , e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file", e);
        }
    }
    private void openErrorWindow(String error){
        Alert message=new Alert(Alert.AlertType.ERROR);
        //message.initOwner();
        message.setTitle("Mesaj eroare");
        message.setContentText(error);
        message.showAndWait();
    }
}
