package com.ljy.java;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class prepareWindow extends Application {
    //MARK: UI relatives
    Stage stage = new Stage();
    Pane pane = new Pane();

    double xOffset, yOffset;

    //MARK: battle relatives
    static final int PLAYMODE_HUMAN =0;
    static final int PLAYMODE_MONSTER =1;
    static final int PLAYMODE_GOD =2;
    static final int PLAYMODE_TWOPLAYER =3;

    public void switchShow() throws Exception
    {
        loadComponents();
        start(stage);
    }

    private void SwitchWindows(int type)
    {
        battleWindow nextWin = new battleWindow();
        try {
            nextWin.switchShow(null,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(stage != null)
            stage.close();
    }

    private void loadComponents()
    {
        stage.setWidth(600);
        stage.setHeight(300);
        ImageView bg = new ImageView(Configs.SysIcons.get(Configs.INDEX_PREPAREGACKGROUND));
        bg.setFitWidth(600);
        bg.setFitHeight(300);
        pane.getChildren().add(bg);

        ImageView humanImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_HUMANHEAD));
        humanImg.setFitWidth(100);
        humanImg.setFitHeight(100);

        Label human = new Label("",humanImg);
        human.setMaxSize(150,150);
        human.setLayoutX(100);
        human.setLayoutY(80);
        human.setOnMouseClicked((MouseEvent e)->{ SwitchWindows(PLAYMODE_HUMAN); });
        human.setOnMouseEntered((MouseEvent e)->{ humanImg.setImage(Configs.SysIcons.get(Configs.INDEX_HUMANHEAD2)); });
        human.setOnMouseExited((MouseEvent e)->{ humanImg.setImage(Configs.SysIcons.get(Configs.INDEX_HUMANHEAD)); });
        pane.getChildren().add(human);

        ImageView monsterImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_MONSTERHEAD));
        monsterImg.setFitWidth(150);
        monsterImg.setFitHeight(150);

        Label monster = new Label("",monsterImg);
        monster.setMaxSize(150,150);
        monster.setLayoutX(400);
        monster.setLayoutY(80);
        monster.setOnMouseClicked((MouseEvent e)->{ SwitchWindows(PLAYMODE_MONSTER); });
        monster.setOnMouseEntered((MouseEvent e)->{ monsterImg.setImage(Configs.SysIcons.get(Configs.INDEX_MONSTERHEAD2)); });
        monster.setOnMouseExited((MouseEvent e)->{ monsterImg.setImage(Configs.SysIcons.get(Configs.INDEX_MONSTERHEAD)); });
        pane.getChildren().add(monster);

        ImageView tpmodeImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_TWOPLAYER));
        tpmodeImg.setFitWidth(150);
        tpmodeImg.setFitHeight(50);

        Label tpmode = new Label("",tpmodeImg);
        tpmode.setMaxSize(150,50);
        tpmode.setLayoutX(225);
        tpmode.setLayoutY(100);
        tpmode.setOnMouseEntered((MouseEvent e)->{ tpmodeImg.setImage(Configs.SysIcons.get(Configs.INDEX_TWOPLAYER2)); });
        tpmode.setOnMouseExited((MouseEvent e)->{ tpmodeImg.setImage(Configs.SysIcons.get(Configs.INDEX_TWOPLAYER)); });
        tpmode.setOnMouseClicked((MouseEvent e)->{ SwitchWindows(PLAYMODE_TWOPLAYER); });
        pane.getChildren().add(tpmode);

        ImageView automodeImg = new ImageView(Configs.SysIcons.get(Configs.INDEX_AUTOPLAY));
        automodeImg.setFitWidth(150);
        automodeImg.setFitHeight(50);

        Label automode = new Label("",automodeImg);
        automode.setMaxSize(150,50);
        automode.setLayoutX(225);
        automode.setLayoutY(200);
        automode.setOnMouseEntered((MouseEvent e)->{ automodeImg.setImage(Configs.SysIcons.get(Configs.INDEX_AUTOPLAY2)); });
        automode.setOnMouseExited((MouseEvent e)->{ automodeImg.setImage(Configs.SysIcons.get(Configs.INDEX_AUTOPLAY)); });
        automode.setOnMouseClicked((MouseEvent e)->{ SwitchWindows(PLAYMODE_GOD); });
        pane.getChildren().add(automode);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        pane.setOnMousePressed((MouseEvent e)->{
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        pane.setOnMouseDragged((MouseEvent e)->{
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });
        pane.setBackground(null);

        Scene scene = new Scene(pane, Configs.WIN_WIDTH,Configs.WIN_HEIGHT);
        scene.setFill(null);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.show();
    }
}
