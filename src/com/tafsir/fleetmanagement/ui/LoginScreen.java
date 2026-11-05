package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;

import com.tafsir.fleetmanagement.logic.Authenticator;
import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.HeaderBar;
import com.tafsir.fleetmanagement.ui.components.LabeledField;
import com.tafsir.fleetmanagement.ui.components.PrimaryButton;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

// The login screen (UC-00): the gate in front of every other screen.
public class LoginScreen extends BorderPane {

    private final Authenticator authenticator = new Authenticator();
    private final Validator validator = new Validator();
    private final Runnable onSuccess;   

    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();   
    private final Label errorLabel = new Label();

    public LoginScreen(Runnable onSuccess) {
        this.onSuccess = onSuccess;
        setTop(new HeaderBar("FMAS \u2014 Login"));  
        setCenter(buildBackdrop());
        Platform.runLater(usernameField::requestFocus);   
    }

    private StackPane buildBackdrop() {
        Label title = new Label("FMAS");
        title.getStyleClass().add("login-title");
        Label subtitle = new Label("Fleet Management & Analytics System");
        subtitle.getStyleClass().add("login-subtitle");
        VBox.setMargin(subtitle, new Insets(0, 0, 10, 0));   

        usernameField.setPromptText("e.g. fleetmgr01");
        passwordField.setPromptText("Enter your password");
        
        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> attemptLogin());

        PrimaryButton loginButton = new PrimaryButton("LOG IN");
        loginButton.setMaxWidth(Double.MAX_VALUE);  
        loginButton.setOnAction(e -> attemptLogin());

        errorLabel.getStyleClass().add("login-error");
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setAlignment(Pos.CENTER);
        hideError();

        VBox card = new VBox(14, title, subtitle,
                new LabeledField("Username", usernameField),
                new LabeledField("Password", passwordField),
                loginButton, errorLabel);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(400);
        card.setMaxWidth(Region.USE_PREF_SIZE);    
        card.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane backdrop = new StackPane(card);   
        backdrop.getStyleClass().add("login-backdrop");
        return backdrop;
    }

    
    private void attemptLogin() {
        hideError();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!validator.requireFields(username, password)) {   
            showError("Username and password are required");
            return;
        }
        try {
            if (authenticator.isValidLogin(username, password)) {
                passwordField.clear();   
                onSuccess.run();         
            } else {
                showError("Invalid username or password");   
                passwordField.clear();
                passwordField.requestFocus();
            }
        } catch (SQLException e) {
            Dialogs.error("Could not log in", e.getMessage());   
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);   
    }
}