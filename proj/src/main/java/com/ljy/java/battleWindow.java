package com.ljy.java;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.font.GraphicComponent;

import java.io.File;

public class battleWindow extends Application{
    //MARK: UI relatives
    Stage stage = new Stage();
    Pane pane = new Pane();
    Canvas canvas;
    SelectionBar sbar;
    CInfoBar cibar;
    Rectangle BlockMask;
    StepHint hint;
    Label save;
    Label close;
    EndMask endMask;
    double xOffset, yOffset;

    //MARK: battle relatives
    battleManager manager;
    String loadfile;


    public void switchShow(String loadfile) throws Exception
    {
        this.loadfile = loadfile;
        loadComponents();
        start(stage);
    }

    private void loadComponents()
    {
        ImageView bg = new ImageView(Configs.SysIcons.get(Configs.INDEX_BACKGROUND));
        bg.setFitWidth(Configs.WIN_WIDTH);
        bg.setFitHeight(Configs.WIN_HEIGHT);
        pane.getChildren().add(bg);

        sbar = new SelectionBar();
        sbar.setLayoutX(-Configs.SBAR_WIDTH);
        sbar.setLayoutY((Configs.WIN_HEIGHT-Configs.SBAR_HEIGHT)/2.0);

        cibar = new CInfoBar();
        cibar.setLayoutX(Configs.LEFT_MARGIN + Configs.B_SIZE*5 - Configs.CIBAR_WIDTH/2.0);
        cibar.setLayoutY(Configs.WIN_HEIGHT);

        BlockMask = new Rectangle(Configs.B_SIZE,Configs.B_SIZE);
        BlockMask.setArcHeight(10);
        BlockMask.setArcWidth(10);
        BlockMask.setFill(new Color(0.8,0.8,0.8,0.6));
        pane.getChildren().add(BlockMask);

        canvas = new Canvas(Configs.WIN_WIDTH, Configs.WIN_HEIGHT);
        manager = new battleManager(this, loadfile);
        sbar.bmanager = manager;

        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        pane.getChildren().add(canvas);

        ImageView cabin = new ImageView(Configs.SysIcons.get(Configs.INDEX_HUMANCABIN));
        cabin.setFitWidth(Configs.B_SIZE);
        cabin.setFitHeight(Configs.B_SIZE);
        cabin.setLayoutX(virtualField.vxTorx(-1));
        cabin.setLayoutY(virtualField.vyTory(3));
        pane.getChildren().add(cabin);

        ImageView cave = new ImageView(Configs.SysIcons.get(Configs.INDEX_MONSTERCAVE));
        cave.setFitWidth(Configs.B_SIZE);
        cave.setFitHeight(Configs.B_SIZE);
        cave.setLayoutX(virtualField.vxTorx(10));
        cave.setLayoutY(virtualField.vyTory(3));
        pane.getChildren().add(cave);


        hint = new StepHint();
        hint.setLayoutX(Configs.LEFT_MARGIN + Configs.B_SIZE*4);
        hint.setLayoutY(Configs.TOP_MARAGIN/2);
        pane.getChildren().add(hint);

        ImageView saveimg = new ImageView(Configs.SysIcons.get(Configs.INDEX_SAVE));
        saveimg.setFitWidth(Configs.B_SIZE/2.0);
        saveimg.setFitHeight(Configs.B_SIZE/2.0);
        save = new Label("", saveimg);
        save.setLayoutX(Configs.WIN_WIDTH - Configs.B_SIZE*1.5);
        save.setOnMouseClicked((MouseEvent)->{
            FileChooser chooser = new FileChooser();
            File file = chooser.showSaveDialog(stage);
            if(file!=null)
                manager.savestack.saveToFile(file.getPath()+".xml");
        });
        pane.getChildren().add(save);

        ImageView closeimg = new ImageView(Configs.SysIcons.get(Configs.INDEX_CLOSE));
        closeimg.setFitWidth(Configs.B_SIZE/2.0);
        closeimg.setFitHeight(Configs.B_SIZE/2.0);
        close = new Label("",closeimg);
        close.setLayoutX(Configs.WIN_WIDTH - Configs.B_SIZE/2.0);
        close.setOnMouseClicked((MouseEvent e)->{ System.exit(0); });
        close.setOnMouseEntered((MouseEvent e)->{ closeimg.setImage(Configs.SysIcons.get(Configs.INDEX_DCLOSE)); });
        close.setOnMouseExited((MouseEvent e)->{ closeimg.setImage(Configs.SysIcons.get(Configs.INDEX_CLOSE)); });
        pane.getChildren().add(close);

        endMask = new EndMask();
        endMask.setVisible(false);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        pane.setOnMouseMoved((MouseEvent e)->{
            iPoint vloc = virtualField.rpTovp(e.getX(), e.getY());
            if(vloc.x<0) vloc.x=0;
            if(vloc.x>=virtualField.width) vloc.x=virtualField.width-1;
            if(vloc.y<0) vloc.y=0;
            if(vloc.y>=virtualField.height) vloc.y=virtualField.height-1;
            iPoint rloc = virtualField.vpTorp(vloc.x, vloc.y);
            BlockMask.setLayoutX(rloc.x);
            BlockMask.setLayoutY(rloc.y);
            vloc = virtualField.rpTovp(e.getX(), e.getY());
            if (vloc.x < 0 || vloc.x >= virtualField.width || vloc.y < 0 || vloc.y >= virtualField.height)
                return;
            cibar.reSet(manager.newAction(vloc, battleManager.ACTION_MOVEABOVE));
        });
        pane.setOnMouseClicked((MouseEvent e)-> {
            iPoint vloc = virtualField.rpTovp(e.getX(), e.getY());
            if (vloc.x < 0 || vloc.x >= virtualField.width || vloc.y < 0 || vloc.y >= virtualField.height)
            {
                //TODO: unselected
            }
            else
            {
                sbar.reSet(manager.newAction(vloc, battleManager.ACTION_CLICKED));
            }
        });
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

        pane.getChildren().add(sbar);
        pane.getChildren().add(cibar);
        pane.getChildren().add(endMask);
    }
}

/** 显示剩余步数*/
class StepHint extends Pane{
    Circle c1;
    Circle c2;
    Circle c3;
    public StepHint()
    {
        c1 = new Circle(Configs.HINT_RADUIS,Color.color(0.1,0.8,0.8));
        c1.setCenterX(Configs.HINT_RADUIS);
        c1.setCenterY(Configs.HINT_RADUIS);
        c2 = new Circle(Configs.HINT_RADUIS,Color.color(0.1,0.8,0.8));
        c2.setCenterX(Configs.HINT_PADDING + Configs.HINT_RADUIS);
        c2.setCenterY(Configs.HINT_RADUIS);
        c3 = new Circle(Configs.HINT_RADUIS,Color.color(0.1,0.8,0.8));
        c3.setCenterX(Configs.HINT_PADDING + Configs.HINT_PADDING + Configs.HINT_RADUIS);
        c3.setCenterY(Configs.HINT_RADUIS);
        getChildren().add(c1);
        getChildren().add(c2);
        getChildren().add(c3);
    }
    public void set(int value)
    {
        Platform.runLater(()->{
            switch (value)
            {
                case 0:
                    c1.setVisible(false);
                    c2.setVisible(false);
                    c3.setVisible(false);
                    break;
                case 1:
                    c1.setVisible(true);
                    c2.setVisible(false);
                    c3.setVisible(false);
                    break;
                case 2:
                    c1.setVisible(true);
                    c2.setVisible(true);
                    c3.setVisible(false);
                    break;
                case 3:
                    c1.setVisible(true);
                    c2.setVisible(true);
                    c3.setVisible(true);
                    break;
                default:
                    System.out.println("hint count error");
            }
        });
    }
}

/** 游戏结束提示画面*/
class EndMask extends Pane
{
    ImageView winner;
    Label save;
    Label close;

    public EndMask()
    {
        Rectangle rect = new Rectangle(Configs.WIN_WIDTH, Configs.WIN_HEIGHT);
        rect.setFill(Color.color(0,0,0,0.5));
        rect.setStroke(null);
        getChildren().add(rect);

        ImageView endText = new ImageView(Configs.SysIcons.get(Configs.INDEX_END));
        endText.setFitWidth(Configs.WIN_HEIGHT/1.5);
        endText.setFitHeight(Configs.WIN_HEIGHT/3.0);
        endText.setX(Configs.WIN_WIDTH/2.0 - Configs.WIN_HEIGHT/3.0);
        endText.setY(Configs.WIN_HEIGHT/5.0);
        getChildren().add(endText);

        winner = new ImageView(Configs.SysIcons.get(Configs.INDEX_HUMANHEAD));
        winner.setFitWidth(Configs.WIN_HEIGHT/5.0);
        winner.setFitHeight(Configs.WIN_HEIGHT/5.0);
        winner.setX(Configs.WIN_WIDTH/2.0 - Configs.WIN_HEIGHT/10.0);
        winner.setY(Configs.WIN_HEIGHT*3/5.0);
        getChildren().add(winner);

        ImageView closeimg = new ImageView(Configs.SysIcons.get(Configs.INDEX_CLOSE));
        closeimg.setFitWidth(Configs.B_SIZE/2.0);
        closeimg.setFitHeight(Configs.B_SIZE/2.0);
        close = new Label("",closeimg);
        close.setLayoutX(Configs.WIN_WIDTH - Configs.B_SIZE/2.0);
        close.setOnMouseClicked((MouseEvent e)->{ System.exit(0); });
        close.setOnMouseEntered((MouseEvent e)->{ closeimg.setImage(Configs.SysIcons.get(Configs.INDEX_DCLOSE)); });
        close.setOnMouseExited((MouseEvent e)->{ closeimg.setImage(Configs.SysIcons.get(Configs.INDEX_CLOSE)); });
        getChildren().add(close);

    }

    /**
     * 指定胜利方
     * @param monster 是否为妖怪胜利
     */
    public void whoWin(boolean monster)
    {
        if(monster)
            winner.setImage(Configs.SysIcons.get(Configs.INDEX_MONSTERHEAD));
        else
            winner.setImage(Configs.SysIcons.get(Configs.INDEX_HUMANHEAD));
    }
}

