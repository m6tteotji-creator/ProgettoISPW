package com.example.onetour;

import com.example.onetour.config.AppConfig;
import com.example.onetour.enumeration.PersistenceMode;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        ChoiceDialog<PersistenceMode> dialog =
                new ChoiceDialog<>(PersistenceMode.DEMO, List.of(PersistenceMode.DEMO, PersistenceMode.CSV, PersistenceMode.JDBC));
        dialog.setTitle("OneTour - Select Mode");
        dialog.setHeaderText("Select application mode");
        dialog.setContentText("Mode:");

        Optional<PersistenceMode> choice = dialog.showAndWait();

        if (choice.isEmpty()) {
            Platform.exit();
            return;
        }

        AppConfig.getInstance().setPersistenceMode(choice.get());

        FXMLLoader loader = new FXMLLoader(MainFX.class.getResource("/fxml/navigatorBase.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("OneTour");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
