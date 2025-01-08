package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.FriendshipStatus;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Repository.Database.Paging.FriendshipDTO;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.UserService;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import com.example.socialnetworkgui.Utils.paging.Page;
import com.example.socialnetworkgui.Utils.paging.Pageable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
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
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Label labelPage;
    private FriendshipDTO filter = new FriendshipDTO();
    private int pageSize = 3;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

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
        filter.setUser(Optional.ofNullable(user.getId()));
        filter.setStatus(Optional.of(FriendshipStatus.PENDING));
        Pageable pageable = new Pageable(currentPage, pageSize);
        Page<Friendship> page = friendService.findAllOnPage(pageable,filter);
        totalNumberOfElements = page.getTotalNumberOfElements();
        int totalNumberOfPages = totalNumberOfElements / pageSize;
        if (totalNumberOfElements % pageSize != 0)
            totalNumberOfPages++;
        labelPage.setText("Page " + (currentPage + 1) + " of " + totalNumberOfPages);
        List<Friendship> messages = friendService.findActualFriendships(user,page,FriendshipStatus.PENDING);

        modelNotification.setAll(messages);
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable(currentPage + 1 == totalNumberOfPages);
    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        Friendship friend=(Friendship) tableViewNotification.getSelectionModel().getSelectedItem();
        if (friend!=null) {
            Friendship fr = friendService.deleteFriendship(friend.getId());
        }
        int remainingElements = totalNumberOfElements - 1;
        int totalPages = (remainingElements + pageSize - 1) / pageSize;
        if (currentPage + 1 > totalPages && currentPage > 0) {
            currentPage--;
        }

        initModelNotification();
    }

    public void handleAcceptFriendship(ActionEvent actionEvent) {
        Friendship friend=(Friendship) tableViewNotification.getSelectionModel().getSelectedItem();
        Friendship fr = friendService.setStatus(friend.getId(),FriendshipStatus.ACCEPTED);
    }

    @Override
    public void update(FriendshipEntityChangeEvent utilizatorEntityChangeEvent) {

        initModelNotification();

    }
    public void handleNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModelNotification();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModelNotification();
    }
}
