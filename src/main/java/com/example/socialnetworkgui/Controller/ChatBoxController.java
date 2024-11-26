package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Friendship;
import com.example.socialnetworkgui.Domain.Message;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Repository.Database.MessageDBRepository;
import com.example.socialnetworkgui.Repository.Database.UserDBRepository;
import com.example.socialnetworkgui.Service.MessageService;
import com.example.socialnetworkgui.Service.UserService;
import com.example.socialnetworkgui.Utils.Events.FriendshipEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Events.MessageEntityChangeEvent;
import com.example.socialnetworkgui.Utils.Observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.lang.model.type.ArrayType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChatBoxController implements Observer<MessageEntityChangeEvent> {
    private User to;
    private User from;
    private MessageService messageService;
    private UserService userService;
    ObservableList<Message> model = FXCollections.observableArrayList();
    Long indexCurent;

    @FXML
    public VBox chatZone;

    @FXML
    ScrollPane chatScrollPane;

    @FXML
    public TextField messageField;

    @FXML
    public Text chat;

    public void setService(User from, User to, MessageService messageService,UserService userService) {
        this.from = from;
        this.to = to;
        this.messageService = messageService;
        this.userService = userService;
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        chatScrollPane.setContent(chatZone);
        loadGlobalIndex();
        initModel();
    }

    public void initModel(){

        chat.setText("Chat: "+ to.getFirstName()+ " " + to.getLastName());
        Iterable<Message> messages = messageService.getAllMessages();

        List<Message> messageList = StreamSupport.stream(messages.spliterator(), false).
                                    filter(u->(u.getFrom().equals(from) && u.getTo().get(0).equals(to)) || (u.getTo().get(0).equals(from) && u.getFrom().equals(to))).
                                    sorted(Comparator.comparing(Message::getDate)).
                                    collect(Collectors.toList());
        chatZone.setPadding(new Insets(messageList.size()));
        chatZone.setSpacing(5);
        for(Message m : messageList){
            HBox message = new HBox();
            message.setAlignment(from.equals(m.getFrom()) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
            TextFlow textFlow = new TextFlow(new Text(m.getMessage()));
            textFlow.setPadding(new Insets(10));
            textFlow.setStyle("-fx-background-color: " + (from.equals(m.getFrom()) ? "#0084ff" : "#e4e6eb") + ";"
                    + "-fx-background-radius: 10;");
                ((Text) textFlow.getChildren().get(0)).setFill(from.equals(m.getFrom()) ? Color.WHITE : Color.BLACK);
                message.getChildren().add(textFlow);
                chatZone.getChildren().add(message);
            }
        chatZone.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });
        }
    public void handleSend(ActionEvent event) {
            String message = messageField.getText();
            indexCurent+=1;
            saveCurentIndex();
            messageService.addMessage(indexCurent,from,to,message,0);
            messageField.clear();
            HBox messageBox = new HBox();
            messageBox.setAlignment( Pos.CENTER_RIGHT  );
            TextFlow textFlow = new TextFlow(new Text(message));
            textFlow.setPadding(new Insets(10));
            textFlow.setStyle("-fx-background-color: #0084ff" + ";"
                + "-fx-background-radius: 10;");
            ((Text) textFlow.getChildren().get(0)).setFill( Color.WHITE );
             messageBox.getChildren().add(textFlow);
            chatZone.getChildren().add(messageBox);
        chatZone.heightProperty().addListener((observable, oldValue, newValue) -> {
            chatScrollPane.setVvalue(1.0);
        });
    }
    @Override
    public void update(MessageEntityChangeEvent event) {
            initModel();
    }
    private void saveCurentIndex() {
        Path path = Paths.get("D:\\Facultate\\MAP\\SocialNetworkGUI\\src\\main\\resources\\com\\example\\socialnetworkgui\\globalIndexMessage.txt");

        try {
            // Write the current global index to the file
            Files.writeString(path, indexCurent.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error saving current index to file" , e);
        }
    }

    private void loadGlobalIndex() {
        Path path = Paths.get("D:\\Facultate\\MAP\\SocialNetworkGUI\\src\\main\\resources\\com\\example\\socialnetworkgui\\globalIndexMessage.txt");

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
}
