package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Service.FriendshipService;
import com.example.socialnetworkgui.Service.UserService;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.control.Button;

public class FriendshipController implements Observer<FriendshipEntityChangeEvent> {
    UserService userService;
    FriendshipService friendshipService;
    ObservableList<Friendship> model = FXCollections.observableArrayList();


    @Override
    public void update(FriendshipEntityChangeEvent event) {
            ;
    }
    private void openNewWindow() {
        // Creează un nou `Stage` pentru fereastra secundară
        Stage newWindow = new Stage();

        // Setează proprietățile ferestrei secundare
        newWindow.setTitle("New Window");
        newWindow.initModality(Modality.APPLICATION_MODAL); // Blochează interacțiunea cu fereastra principală până ce se închide cea secundară

        // Creează un layout și scena pentru fereastra secundară
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(new Button("Hello from new window!"));
        Scene secondScene = new Scene(secondaryLayout, 250, 150);

        // Setează scena pe noul `Stage` și afișeaz-o
        newWindow.setScene(secondScene);
        newWindow.show();
    }
}
