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

        //final ArrayList<Sprite> laserList = new ArrayList<>();
        final Sprite laser = new Sprite();
        laser.setImage("laser.png");

        final double[] lastNanoTime = {System.nanoTime()};

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

                if (input.contains("Z"))
                    laser.setPosition(400, 540);
                laser.addVelocity(0, -1);
                //laserList.add(laser);

                ship.update(elapsedTime);
                laser.update(elapsedTime);

                // Clear the canvas
                gc.clearRect(0, 0, 800, 600);
                //Render
                gc.drawImage(background, 0, 0);
                ship.render(gc);
                laser.render(gc);



            }
        }.start();

        stage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}