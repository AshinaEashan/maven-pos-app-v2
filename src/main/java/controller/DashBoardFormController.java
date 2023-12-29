package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashBoardFormController {

    public Label dateLabel;
    public Label timeLabel;
    public BorderPane DashboardPane;

    public void initialize(){
        genTime();
        genDate();
    }
    private void genTime(){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.ZERO,
                actionEvent -> timeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")))
        ), new KeyFrame(Duration.seconds(1)));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }
    private void genDate() {
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dateLabel.setText(formattedDate);

    }
    public void customerButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) DashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/CustomerForm.fxml"))));
            stage.setTitle("Customer Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void orderButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) DashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrdersForm.fxml"))));
            stage.setTitle("Orders Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void itemButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) DashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ItemForm.fxml"))));
            stage.setTitle("Item Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void placeOrderButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) DashboardPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/PlaceOrderForm.fxml"))));
            stage.setTitle("Place Order Form");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
