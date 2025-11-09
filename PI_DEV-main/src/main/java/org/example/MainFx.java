package org.example;

import Services.EvenementService;
import Services.ParticipantEvenementService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainFx extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML file (adjust path to match your FXML location)

        //  Parent root = FXMLLoader.load(getClass().getResource("/DashboardR.fxml"));
      // Parent root = FXMLLoader.load(getClass().getResource("/Coach.fxml"));
       // Parent root = FXMLLoader.load(getClass().getResource("/invest.fxml"));
        // Parent root = FXMLLoader.load(getClass().getResource("/creaters.fxml"));
         Parent root = FXMLLoader.load(getClass().getResource("/SigninUp.fxml"));


        //  Parent root = FXMLLoader.load(getClass().getResource("/Events.fxml"));
        // Set up the scene and stage
        Scene scene = new Scene(root);
        primaryStage.setTitle("Coachini");
        primaryStage.setScene(scene);
        primaryStage.show();

        //trigger
//
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(() -> {
//            System.out.println("[DEBUG] Running scheduled task...");
//
//            EvenementService.updateExpiredEvents();
//            EvenementService.updateEtatWhenFull();
//            ParticipantEvenementService.cancelExpiredPendingPayments();
//        }, 0, 1, TimeUnit.MINUTES);
    }

}
