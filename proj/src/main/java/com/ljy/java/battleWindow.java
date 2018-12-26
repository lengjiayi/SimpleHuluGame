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
import javafx.scene.shape.Ellipse;
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
    Ellipse BlockMask;
    StepHint hint;
    Label save;
    Label close;
    EndMask endMask;
    double xOffset, yOffset;

    //MARK: battle relatives
    battleManager manager;
    String loadfile;
    boolean monster = false;
    boolean autofight = false;
    boolean twoplayer = false;


    public void switchShow(String loadfile, int playtype) throws Exception
    {
        this.loadfile = loadfile;
        if(loadfile==null){
            switch(playtype)
            {
                case prepareWindow.PLAYMODE_HUMAN:
                    monster = false;
                    break;
                case prepareWindow.PLAYMODE_MONSTER:
                    monster = true;
                    break;
                case prepareWindow.PLAYMODE_GOD:
                    autofight = true;
                    break;
                case prepareWindow.PLAYMODE_TWOPLAYER:
                    twoplayer = true;
                    break;
            }
        }
        loadComponents();
        start(stage);
    }

    private void loadComponents()
    {
        ImageView bg = new ImageView(Configs.SysIcons.get(Configs.INDEX_BACKGROUND));
        bg.setFitWidth(Configs.WIN_WIDTH);
        bg.setFitHeight(Configs.WIN_HEIGHT);
        pane.getChildren().add(bg);

        ImageView battlefield = new ImageView(Configs.SysIcons.get(Configs.INDEX_BATTLEFIELD));
        battlefield.setFitWidth(Configs.B_SIZE*Configs.B_WNUM);
        battlefield.setFitHeight(Configs.B_SIZE*Configs.B_HNUM);
        battlefield.setLayoutX(Configs.LEFT_MARGIN);
        battlefield.setLayoutY(Configs.TOP_MARAGIN);
        pane.getChildren().add(battlefield);

        sbar = new SelectionBar();
        sbar.setLayoutX(-Configs.SBAR_WIDTH);
        sbar.setLayoutY((Configs.WIN_HEIGHT-Configs.SBAR_HEIGHT)/2.0);

        cibar = new CInfoBar();
        cibar.setLayoutX(Configs.LEFT_MARGIN + Configs.B_SIZE*5 - Configs.CIBAR_WIDTH/2.0);
        cibar.setLayoutY(Configs.WIN_HEIGHT);

        BlockMask = new Ellipse(Configs.B_SIZE, Configs.B_SIZE/2);
        BlockMask.setFill(new Color(0.8,0.8,0.8,0.6));
        pane.getChildren().add(BlockMask);

        canvas = new Canvas(Configs.WIN_WIDTH, Configs.WIN_HEIGHT);
        manager = new battleManager(this, loadfile, monster, autofight, twoplayer);
        sbar.bmanager = manager;

        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        pane.getChildren().add(canvas);
/*
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
*/

        hint = new StepHint();
        hint.setLayoutX(Configs.LEFT_MARGIN + Configs.B_SIZE*4);
        hint.setLayoutY(Configs.TOP_MARAGIN/2);
        if(monster)
            hint.set(2, monster);
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
            //MARK: 将高亮块进行透视映射
            for(int j=0;j<Configs.B_HNUM;j++)
                for(int i=0;i<Configs.B_WNUM;i++)
                {
                    Block block = Configs.IsInBlock(e.getX(), e.getY(), i, j);
                    if(block != null)
                    {
                        BlockMask.setRadiusX(block.twidth/8 + block.bwidth*3/8);
                        BlockMask.setRadiusY(block.height/4);
                        BlockMask.setCenterX(block.tx*3/4 + block.bx/4 + block.twidth/8 + block.bwidth*3/8);
                        BlockMask.setCenterY(block.y + block.height*3/4);
                        cibar.reSet(manager.newAction(new iPoint(i,j), battleManager.ACTION_MOVEABOVE));
                        return;
                    }
                }
        });
        pane.setOnMouseClicked((MouseEvent e)-> {
            iPoint vloc = virtualField.rpTovp(e.getX(), e.getY());
            for(int j=0;j<Configs.B_HNUM;j++)
                for(int i=0;i<Configs.B_WNUM;i++)
                {
                    Block block = Configs.IsInBlock(e.getX(), e.getY(), i, j);
                    if(block != null)
                    {
                        sbar.reSet(manager.newAction(new iPoint(i,j), battleManager.ACTION_CLICKED));
                        return;
                    }
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
    private boolean monster;
    public StepHint()
    {
        c1 = new Circle(Configs.HINT_RADUIS,Color.color(0.9,0.2,0.3));
        c1.setCenterX(Configs.HINT_RADUIS);
        c1.setCenterY(Configs.HINT_RADUIS);
        c2 = new Circle(Configs.HINT_RADUIS,Color.color(0.9,0.2,0.3));
        c2.setCenterX(Configs.HINT_PADDING + Configs.HINT_RADUIS);
        c2.setCenterY(Configs.HINT_RADUIS);
        c3 = new Circle(Configs.HINT_RADUIS,Color.color(0.9,0.2,0.3));
        c3.setCenterX(Configs.HINT_PADDING + Configs.HINT_PADDING + Configs.HINT_RADUIS);
        c3.setCenterY(Configs.HINT_RADUIS);
        getChildren().add(c1);
        getChildren().add(c2);
        getChildren().add(c3);
    }
    public void set(int value, boolean monster)
    {
        this.monster = monster;
        Platform.runLater(()->{
            if(monster)
            {
                c1.setFill(Color.BLACK);
                c2.setFill(Color.BLACK);
                c3.setFill(Color.BLACK);
            }
            else
            {
                c1.setFill(Color.color(0.9,0.2,0.3));
                c2.setFill(Color.color(0.9,0.2,0.3));
                c3.setFill(Color.color(0.9,0.2,0.3));
            }
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
        rect.setFill(Color.color(1.0,1.0,1.0, 0.8));
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

