package com.example.socialnetworkgui;

import com.example.socialnetworkgui.Controller.LoginController;
import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.Message;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Domain.Validators.FriendshipValidator;
import com.example.socialnetworkgui.Domain.Validators.MessageValidator;
import com.example.socialnetworkgui.Domain.Validators.UserValidator;
import com.example.socialnetworkgui.Repository.Database.FriendshipDBRepository;
import com.example.socialnetworkgui.Repository.Database.MessageDBRepository;
import com.example.socialnetworkgui.Repository.Database.UserDBRepository;
import com.example.socialnetworkgui.Repository.Repository;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.MessageService;
import com.example.socialnetworkgui.Service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HelloApplication extends Application {

    UserService userService;
    FriendshipService friendshipService;
    MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");



        System.out.println("Reading data from file");
        String username="postgres";
        String pasword="cosmin2004";
        String url="jdbc:postgresql://localhost:5433/SocialApp";
        Repository<Long, User> utilizatorRepository =
                new UserDBRepository( new UserValidator(),url,username, pasword);

        Repository<Tuple<Long,Long>,Friendship> friendshipRepository = new FriendshipDBRepository(new FriendshipValidator(),url,username,pasword);
        Repository<Long, Message> messageRepository = new MessageDBRepository(new MessageValidator(),url,username,pasword,utilizatorRepository);
        userService =new UserService(utilizatorRepository,friendshipRepository);
        friendshipService = new FriendshipService(utilizatorRepository,friendshipRepository);
        messageService = new MessageService(utilizatorRepository,messageRepository);

        /*userService.addUser(1L,"Mircea", "Lucesc", LocalDate.parse("11/10/2012", formatter), "lucescumircea10@gmail.com", "12345");
        userService.addUser(2L,"George", "Becali", LocalDate.parse("10/12/2010", formatter), "georgebecali10@yahoo.com", "123456");
        userService.addUser(3L,"Nicolaie", "Trepan", LocalDate.parse("13/05/2001", formatter), "nicolaietrepan10@gamil.com", "1234567");
        userService.addUser(4L,"Ion", "Vasile", LocalDate.parse("12/03/2004", formatter), "vasileion10@yahoo.com", "2345678");
        userService.addUser(5L,"Cristian", "Veres", LocalDate.parse("11/08/2013", formatter), "verescristi10@yahoo.com", "123498");
        userService.addUser(6L,"Mihaela", "Popescu", LocalDate.parse("10/03/2013", formatter), "popescumihaela10@yahoo.com", "123490");
        userService.addUser(7L,"Maria", "Tanase", LocalDate.parse("12/01/2011", formatter), "tanasemaria10@gmail.com", "1234234");
        userService.addUser(8L,"Ionela", "Eremia", LocalDate.parse("10/06/2007", formatter), "eremiaionela10@yahoo.com", "1234678");
        userService.addUser(9L,"Adela", "Popescu", LocalDate.parse("11/08/2001", formatter), "popescuadela10@gmail.com", "1234345");
        userService.addUser(10L,"Ion", "Popescu", LocalDate.parse("12/03/2011", formatter), "popescuion10@yahoo.com", "1234");
        */
        initView(primaryStage);
        primaryStage.setWidth(500);
        primaryStage.setHeight(340);
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        // FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(getClass().getResource("com/example/seminar6/views/utilizator-view.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/utilizator-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/LoginWindow.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.setScene(new Scene(userLayout));

        LoginController loginController = fxmlLoader.getController();
        loginController.setLoginService(userService,friendshipService,messageService,primaryStage);

    }
}