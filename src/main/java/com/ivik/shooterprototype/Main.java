package com.ivik.shooterprototype;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.RED;

/**
 * Created by Eigenaar on 17-3-2016.
 */
public class Main extends Application{


    public void start (Stage stage){

        stage.setTitle("Prototype");

        Group root = new Group();
        Scene scene = new Scene( root );
        stage.setScene(scene);

        Canvas canvas = new Canvas( 800, 600);
        root.getChildren().add( canvas );

        final HashSet<String> input = new HashSet<>();

        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent keyEvent) {
                        String code = keyEvent.getCode().toString();
                        if (!input.contains(code));
                        input.add(code);
                    }
                }
        );

        scene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent keyEvent)
                    {
                        String code = keyEvent.getCode().toString();
                        input.remove( code );
                    }
                });

        final GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill( BLACK );
        gc.setStroke( RED );

        final Image background = new Image("background.png");

        final Sprite ship = new Sprite();
        ship.setImage("ship.png");
        ship.setPosition(400, 540);

        final ArrayList<Sprite> enemaList = new ArrayList<>();
        for (int i = 0; i < 15; i++)
        {
            Sprite enema = new Sprite();
            enema.setImage("enema.png");
            double px = 800 * Math.random();
            double py = -400 * Math.random();
            enema.setPosition(px,py);
            enema.setVelocity(0, 100);
            enemaList.add(enema);
        }

        final ArrayList<Sprite> laserList = new ArrayList<>();

        final double[] lastNanoTime = {System.nanoTime()};
        final long[] lastShoot = {System.currentTimeMillis()};
        final long threshold = 500;

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                // calculate time since last update.
                double elapsedTime = (currentNanoTime - lastNanoTime[0]) / 1000000000.0;
                lastNanoTime[0] = currentNanoTime;

                // game logic
                ship.setVelocity(0,0);
                if (input.contains("LEFT"))
                    ship.addVelocity(-200,0);
                if (input.contains("RIGHT"))
                    ship.addVelocity(200,0);
                if (input.contains("UP"))
                    ship.addVelocity(0,-200);
                if (input.contains("DOWN"))
                    ship.addVelocity(0,200);

                if (input.contains("Z")) {
                    long now = System.currentTimeMillis();
                    if (now - lastShoot[0] > threshold){
                        Sprite laser = new Sprite();
                        laser.setImage("laser.png");
                        laser.setPosition(ship.positionX + 10, ship.positionY - 15);
                        laser.addVelocity(0, -250);
                        laserList.add(laser);
                        lastShoot[0] = now;
                    }
                }

                ship.update(elapsedTime);

                for (Sprite enema : enemaList)
                        enema.update(elapsedTime);

                for (Sprite laser : laserList )
                        laser.update(elapsedTime);


                // Clear the canvas
                gc.clearRect(0, 0, 800, 600);
                //Render
                gc.drawImage(background, 0, 0);
                ship.render(gc);

                for(Sprite enema : enemaList)
                enema.render(gc);

                for (Sprite laser : laserList)
                    laser.render(gc);

                //collision detection
                for (int i = 0; i < enemaList.size() ; i++){
                    for (int j = 0; j < laserList.size() ; j++){
                        if (enemaList.get(i).intersects(laserList.get(j))){
                            enemaList.remove(i);
                            laserList.remove(j);
                        }
                    }
                }



            }
        }.start();

        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}