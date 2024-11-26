package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.HelloApplication;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.MessageService;
import com.example.socialnetworkgui.Service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    UserService service;
    FriendshipService friendService;
    MessageService messageService;
    private Stage primaryStage;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @FXML
    private Button signinButton;

    //Long globalIndex;

    public void setLoginService(UserService service, FriendshipService friendService,MessageService messageService, Stage primaryStage) {
        this.service = service;
        this.friendService = friendService;
        this.primaryStage = primaryStage; // Assign the login stage here
        this.messageService = messageService;
    }

    public void handleLogin(ActionEvent actionEvent) throws IOException {
        String email = username.getText();
        String pass= password.getText();
        if(email.isEmpty()|| pass.isEmpty()){
            MessageAlert.showMessage(primaryStage, Alert.AlertType.ERROR,"ERROR!","Invalid credentials!");
        }
        else{
            Optional<User> user = service.getUserByMail(email);
            if(user.isPresent()){
                User usr=user.get();
                if(usr.getPassword().equals(pass)){
                    primaryStage.close();
                    openWindowLogin(usr);
                }
                else{
                    MessageAlert.showMessage(primaryStage, Alert.AlertType.ERROR,"ERROR!","Passwords do not match!");
                    username.clear();
                    password.clear();
                }
            }else{
                MessageAlert.showMessage(primaryStage, Alert.AlertType.ERROR,"ERROR!","User does not exist! You can try signing in!");
                username.clear();
                password.clear();
            }
        }

    }



    private void openWindowLogin(User user) throws IOException {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/UserView.fxml"));
        AnchorPane root = loader.load();

        UserController controller = loader.getController();
        controller.setUtilizatorService(service,friendService,messageService,user,primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Utilizator");
        primaryStage.setWidth(820);
        primaryStage.setHeight(500);
        primaryStage.setResizable(false);
        primaryStage.show();
        if(controller.count!=0){
            MessageAlert.showMessage(primaryStage, Alert.AlertType.INFORMATION,"Notificare!","Ai primit " + controller.count + " cereri!");
        }

    }

    public void handleSignin(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../views/UserSignIn.fxml"));
        AnchorPane root = loader.load();
        SignInController controller = loader.getController();
        controller.setServices(service,friendService,messageService,primaryStage);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Sign In");
        primaryStage.setWidth(500);
        primaryStage.setHeight(340);
        primaryStage.setResizable(false);
        primaryStage.show();

    }

}
