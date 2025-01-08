package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.Tuple;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.HelloApplication;
import com.example.socialnetworkgui.Repository.Database.Paging.FriendshipDTO;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.MessageService;
import com.example.socialnetworkgui.Service.UserService;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Events.UserEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import javafx.scene.image.ImageView;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<FriendshipEntityChangeEvent> {
    UserService service;
    FriendshipService friendService;
    MessageService messageService;
    User user;
    ObservableList<Friendship> model = FXCollections.observableArrayList();



    @FXML
    Text username;
    @FXML
    TableView<Friendship> tableView;


    @FXML
    TableColumn<Friendship,String> tableColumnFirstName;
    @FXML
    TableColumn<Friendship,String> tableColumnLastName;
    @FXML
    TableColumn<Friendship,String> tableColumnDate;
    @FXML
    TableColumn<Friendship,String> tableColumnStatus;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    TextField userFind;

    @FXML
    private Label labelPage;

    @FXML
    ImageView notificationBell;

    Stage primaryStage;
    Stage notificationStage;
    Stage chatStage;
    long count;

    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private FriendshipDTO filter = new FriendshipDTO();

    public void setUtilizatorService(UserService service, FriendshipService friendService,MessageService messageService,User user,Stage primaryStage ) {
        this.service = service;
        this.friendService = friendService;
        this.messageService = messageService;
        this.user = user;
        this.primaryStage = primaryStage;
        this.notificationStage = new Stage();
        notificationStage.initOwner(primaryStage);
        chatStage = new Stage();
        chatStage.initOwner(primaryStage);
        friendService.addObserver(this);
        username.setText("User: "+user.getEmail());
        count=0;
        initModel();
    }

    @FXML
    public void initialize() {

        tableColumnFirstName.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long id;
            if(friendship.getSecond().equals(user.getId()))
                id = friendship.getFirst();
            else
                id = friendship.getSecond();
            String userName =  service.findUser(id).getFirstName();
            return new SimpleStringProperty(userName);
        });
        tableColumnLastName.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long id;
            if(friendship.getSecond().equals(user.getId()))
                id = friendship.getFirst();
            else
                id = friendship.getSecond();
            String userName =  service.findUser(id).getLastName();
            return new SimpleStringProperty(userName);
        });
        tableColumnDate.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long id;
            if(friendship.getSecond().equals(user.getId()))
                id = friendship.getFirst();
            else
                id = friendship.getSecond();
            LocalDate date =  service.findUser(id).getBirthDate();
            return new SimpleStringProperty(date.toString());
        });
        tableColumnStatus.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            String date = friendship.getStringDate();
            return new SimpleStringProperty(date);
        });
        tableView.setItems(model);


    }

    private void initModel() {
        filter.setUser(Optional.ofNullable(user.getId()));
        filter.setUser2(Optional.ofNullable(user.getId()));
        filter.setStatus(Optional.ofNullable(FriendshipStatus.ACCEPTED));
        Pageable pageable = new Pageable(currentPage, pageSize);
        Page<Friendship> page = friendService.findAllOnPage(pageable,filter);
        totalNumberOfElements = page.getTotalNumberOfElements();
        int totalNumberOfPages = totalNumberOfElements / pageSize;
        if (totalNumberOfElements % pageSize != 0)
            totalNumberOfPages++;
        labelPage.setText("Page " + (currentPage + 1) + " of " + totalNumberOfPages);
        List<Friendship> messages = friendService.findActualFriendships(user,page,FriendshipStatus.ACCEPTED);

        model.setAll(messages);
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable(currentPage + 1 == totalNumberOfPages);
        count=friendService.numberOfPending(user);
        if (count!=0){
            Image image = new Image(getClass().getResourceAsStream("../images/notification-bell-alert.png"));
            notificationBell.setImage(image);


        }else{
            Image image = new Image(getClass().getResourceAsStream("../images/notification-bell-normal.png"));
            notificationBell.setImage(image);
        }
    }

    @Override
    public void update(FriendshipEntityChangeEvent utilizatorEntityChangeEvent) {

        initModel();

    }

    public void handleAddFriend(ActionEvent actionEvent) {
        String email = userFind.getText();
        if(email.isEmpty()) {
            Alert message=new Alert(Alert.AlertType.ERROR);
            //message.initOwner();
            message.setTitle("Mesaj eroare");
            message.setContentText("We don't have a valid email");
            message.showAndWait();
        }
        else{
            Optional<User> usr = service.getUserByMail(email);

            usr.ifPresent(value -> friendService.addFriendship(new Tuple<>(value.getId(), user.getId())));
        }

    }

    public void handleDeleteUser(ActionEvent actionEvent) {
       Friendship friend=(Friendship) tableView.getSelectionModel().getSelectedItem();
       if (friend!=null) {
           Friendship fr = friendService.deleteFriendship(friend.getId());
       }
        int remainingElements = totalNumberOfElements - 1; // One element was deleted
        int totalPages = (remainingElements + pageSize - 1) / pageSize; // Calculate total pages after deletion

        // If the current page is empty, move to the previous page if not already on the first page
        if (currentPage + 1 > totalPages && currentPage > 0) {
            currentPage--;
        }

        initModel();
    }

    public void handleNotificationAlert(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/PendingTable.fxml"));

        AnchorPane userLayout = fxmlLoader.load();
        NotificationTableController controller = fxmlLoader.getController();
        controller.setUtilizatorService(service,friendService,user,primaryStage);

        notificationStage.setScene(new Scene(userLayout));
        notificationStage.setWidth(370);
        notificationStage.setHeight(440);
        notificationStage.setResizable(false);
        notificationStage.show();


    }
    public void handleChat(ActionEvent actionEvent) throws IOException{
        Friendship friend=(Friendship) tableView.getSelectionModel().getSelectedItem();
        if(friend==null) {
            Alert message = new Alert(Alert.AlertType.ERROR);
        }
        else{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../views/ChatBox.fxml"));
        AnchorPane chatLayout = fxmlLoader.load();
        ChatBoxController controller = fxmlLoader.getController();
        User to = null;
        if(user.equals(service.findUser(friend.getSecond())))
             to = service.findUser(friend.getFirst());
        else
             to = service.findUser(friend.getSecond());
        User from = user;
        controller.setService(from,to,messageService,service);
        chatStage.setScene(new Scene(chatLayout));
        chatStage.setWidth(370);
        chatStage.setHeight(440);
        chatStage.setResizable(false);
        chatStage.show();}
    }


    public void handleLogOutUser(ActionEvent actionEvent) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("views/LoginWindow.fxml"));
        AnchorPane userLayout = fxmlLoader.load();
        primaryStage.close();
        notificationStage.close();
        LoginController loginController = fxmlLoader.getController();
        loginController.setLoginService(service,friendService,messageService,primaryStage);
        primaryStage.setScene(new Scene(userLayout));
        primaryStage.setWidth(500);
        primaryStage.setHeight(340);
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public void handleAddPredefinedUser(ActionEvent actionEvent) {
        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        service.addUser(1L,"Ionut","Mircea",LocalDate.parse("2023-10-23",formatter),"ionutmircea@gmail.com","1234");
        service.addUser(2L, "Ana", "Popescu", LocalDate.parse("2023-10-23",formatter), "anapopescu@gmail.com", "abcd");
        service.addUser(3L, "Mihai", "Ionescu", LocalDate.parse("2023-10-23",formatter), "mihaiionescu@gmail.com", "pass123");
        service.addUser(4L, "Maria", "Georgescu", LocalDate.parse("2023-10-23",formatter), "mariageorgescu@gmail.com", "pw3456");
        service.addUser(5L, "Alin", "Vasilescu", LocalDate.parse("2023-10-23",formatter), "alinvasilescu@gmail.com", "123abc");
        service.addUser(6L, "Diana", "Stoian", LocalDate.parse("2023-10-23",formatter), "dianastoian@gmail.com", "diana5678");
        service.addUser(7L, "Vlad", "Popa", LocalDate.parse("2023-10-23",formatter), "vladpopa@gmail.com", "vl@d789");
        service.addUser(8L, "Elena", "Marinescu", LocalDate.parse("2023-10-23",formatter), "elenamarinescu@gmail.com", "elena123");
        service.addUser(9L, "Radu", "Dumitru", LocalDate.parse("2023-10-23",formatter), "radudumitru@gmail.com", "pass9876");
        service.addUser(10L, "Laura", "Preda", LocalDate.parse("2023-10-23",formatter), "laurapreda@gmail.com", "pass4321");
        service.addUser(11L, "Cristian", "Stan", LocalDate.parse("2023-10-23",formatter), "cristianstan@gmail.com", "cristian1234");
        service.addUser(12L, "Adina", "Munteanu", LocalDate.parse("2023-10-23",formatter), "adinamunteanu@gmail.com", "adinapass");


    }
    public void handleNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }
}
