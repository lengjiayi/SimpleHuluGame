package com.ljy.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class Main extends Application {
    Stage stage = null;
    Pane pane = new Pane();
    double xOffset, yOffset;
    @Override
    public void init() throws Exception {
        super.init();
        virtualField.initial();
        Configs.initialize();
        ViewBundle.initialize();
        if(!HintGenerater.GenerateHintDoc())
            System.out.println("生成属性文件失败");
        loadComponents();
    }

    private void loadComponents()
    {
        ImageView bg = new ImageView(Configs.SysIcons.get(Configs.INDEX_PREBACKGROUND));
        pane.getChildren().add(bg);

        ImageView startImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_START));
        startImg.setFitHeight(100);
        startImg.setFitWidth(200);
        Label start = new Label("",startImg);
        start.setMaxSize(200,100);
        start.setLayoutX(300);
        start.setLayoutY(300);
        start.setOnMouseClicked((MouseEvent e)->{ SwitchWindows(null); });

        ImageView loadImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_LOAD));
        loadImg.setFitHeight(100);
        loadImg.setFitWidth(200);
        Label load = new Label("",loadImg);
        load.setMaxSize(200,100);
        load.setLayoutX(300);
        load.setLayoutY(400);
        load.setOnMouseClicked((MouseEvent)-> {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Xml Files", "*.xml"));
            File file = chooser.showOpenDialog(stage);
            if(file!=null)
            {
                SwitchWindows(file.getPath());
            }
        });
        ImageView exitImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_CLOSE));
        exitImg.setFitWidth(50);
        exitImg.setFitHeight(50);
        Label exit = new Label("",exitImg);
        exit.setMaxSize(50,50);
        exit.setLayoutX(550);
        exit.setLayoutY(50);
        exit.setOnMouseClicked((MouseEvent e)->{ Platform.exit(); });
        exit.setOnMouseEntered((MouseEvent e)->{ exitImg.setImage(Configs.SysIcons.get(Configs.INDEX_DCLOSE)); });
        exit.setOnMouseExited((MouseEvent e)->{ exitImg.setImage(Configs.SysIcons.get(Configs.INDEX_CLOSE)); });

        pane.getChildren().add(start);
        pane.getChildren().add(load);
        pane.getChildren().add(exit);
    }

    private void SwitchWindows(String loadfile)
    {
        if(loadfile == null)
        {
            prepareWindow nextWin = new prepareWindow();
            try {
                nextWin.switchShow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stage != null)
                stage.close();
        }
        else {
            battleWindow nextWin = new battleWindow();
            try {
                nextWin.switchShow(loadfile,0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (stage != null)
                stage.close();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        pane.setOnMousePressed((MouseEvent e)->{
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        pane.setOnMouseDragged((MouseEvent e)->{
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });
        pane.setBackground(null);
        Scene scene = new Scene(pane, 600,600);
        scene.setFill(null);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
