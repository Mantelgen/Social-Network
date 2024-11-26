package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.UserService;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NotificationTableController implements Observer<FriendshipEntityChangeEvent> {

    @FXML
    TableView<Friendship> tableViewNotification;

    @FXML
    TableColumn<Friendship,String> tableViewNotificationColumnFirstName;
    @FXML
    TableColumn<Friendship,String> tableViewNotificationColumnLastName;

    @FXML
    TableColumn<Friendship,String> tableViewNotificationColumnStatus;
    Stage primaryStage;
    UserService service;
    FriendshipService friendService;
    User user;
    ObservableList<Friendship> modelNotification = FXCollections.observableArrayList();

    public void setUtilizatorService(UserService service, FriendshipService friendService, User user, Stage stage) {
        this.service = service;
        this.friendService = friendService;
        this.user = user;
        this.primaryStage = stage;
        friendService.addObserver(this);
        initModelNotification();
    }

    public void initialize() {
        tableViewNotificationColumnFirstName.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long id;
            if(friendship.getSecond().equals(user.getId()))
                id = friendship.getFirst();
            else
                id = friendship.getSecond();
            String userName =  service.findUser(id).getFirstName();
            return new SimpleStringProperty(userName);
        });
        tableViewNotificationColumnLastName.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            Long id;
            if(friendship.getSecond().equals(user.getId()))
                id = friendship.getFirst();
            else
                id = friendship.getSecond();
            String userName =  service.findUser(id).getLastName();
            return new SimpleStringProperty(userName);
        });
        tableViewNotificationColumnStatus.setCellValueFactory(cellData -> {
            Friendship friendship = cellData.getValue();
            String date = friendship.getStatus().toString();
            return new SimpleStringProperty(date);
        });
        tableViewNotification.setItems(modelNotification);

    }

    private void initModelNotification() {
        Iterable<Friendship> messages = friendService.getAllFriendships();
        List<Friendship> friends = StreamSupport.stream(messages.spliterator(), false)
                .filter(u->(u.getFirst().equals(user.getId())))
                .filter(u->(u.getStatus()==FriendshipStatus.PENDING))
                .collect(Collectors.toList());
        friends.forEach(System.out::println);
        modelNotification.setAll(friends);
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        Friendship friend=(Friendship) tableViewNotification.getSelectionModel().getSelectedItem();
        if (friend!=null) {
            Friendship fr = friendService.deleteFriendship(friend.getId());
        }
    }

    public void handleAcceptFriendship(ActionEvent actionEvent) {
        Friendship friend=(Friendship) tableViewNotification.getSelectionModel().getSelectedItem();
        if (friend!=null) {
            if(friend.getStatus()== FriendshipStatus.PENDING && friend.getFirst().equals(user.getId())) {
                Friendship fr = friendService.setStatus(friend.getId(),FriendshipStatus.ACCEPTED);
            }
            else{
                Alert message=new Alert(Alert.AlertType.ERROR);
                //message.initOwner();
                message.setTitle("Mesaj eroare");
                message.setContentText("Friendship was already handeled by this user!");
                message.showAndWait();
            }
        }
    }

    @Override
    public void update(FriendshipEntityChangeEvent utilizatorEntityChangeEvent) {

        initModelNotification();

    }
}
