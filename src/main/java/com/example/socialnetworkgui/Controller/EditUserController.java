package com.example.socialnetworkgui.Controller;

import com.example.socialnetworkgui.Domain.Exceptions.ValidationException;
import com.example.socialnetworkgui.Domain.User;
import com.example.socialnetworkgui.Service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditUserController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldBirthday;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;


    private UserService service;
    Stage dialogStage;
    User utilizator;

    @FXML
    private void initialize() {
    }

    public void setService(UserService service,  Stage stage, User u) {
        this.service = service;
        this.dialogStage=stage;
        this.utilizator =u;
        if (null != u) {
            setFields(u);
            textFieldId.setEditable(false);
        }
    }

    @FXML
    public void handleSave(){

        DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String id=textFieldId.getText();
        String firstNameText= textFieldFirstName.getText();
        String lastNameText= textFieldLastName.getText();
        LocalDate birthdayText=LocalDate.parse(textFieldBirthday.getText(),formatter);
        String emailText=textFieldEmail.getText();
        String passwordText=textFieldPassword.getText();

        User utilizator1=new User(firstNameText,lastNameText,birthdayText,emailText,passwordText);
        utilizator1.setId(Long.valueOf(id));
        if (null == this.utilizator)
            saveMessage(utilizator1);
        else
            updateMessage(utilizator1);
    }

    private void updateMessage(User m)
    {
        try {
            User r= this.service.updateUser(m.getId(),m.getFirstName(),m.getLastName(),m.getBirthDate(),m.getEmail(),m.getPassword());
            if (r==null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare user","Userul a fost modificat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }


    private void saveMessage(User m)
    {
        // TODO
        try {
            User r= this.service.addUser(m.getId(),m.getFirstName(),m.getLastName(),m.getBirthDate(),m.getEmail(),m.getPassword());
            if (r==null)
                dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"Slavare user","Mesajul a fost salvat");
        } catch (ValidationException e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();

    }

    private void clearFields() {
        textFieldId.setText("");
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldBirthday.setText("");
        textFieldEmail.setText("");

    }
    private void setFields(User u)
    {
        textFieldId.setText(u.getId().toString());
        textFieldFirstName.setText(u.getFirstName());
        textFieldLastName.setText(u.getLastName());
        textFieldBirthday.setText(u.getBirthDate().toString());
        textFieldEmail.setText(u.getEmail());


    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
