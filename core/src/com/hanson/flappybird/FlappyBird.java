package com.hanson.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    BitmapFont font;
    private SpriteBatch batch;
    private Texture background, bird, bottomTube, topTube, gameOver;
    ShapeRenderer shapeRenderer;
    private TextureAtlas birdAtlas;
    private Animation<TextureAtlas.AtlasRegion> animation;
    private float timePassed = 0;
    private float birdY = 0;
    private TextureAtlas.AtlasRegion currentFrame;
    private float velocity = 0;
    private float gap = 500;
    private float gravity = 2;
    private float maxTubeOffset;
    private int gameState = 0;
    private Random rng;
    private float tubeVelocity = 8;
    private int tubeNum = 4;
    private float[] tubeX = new float[tubeNum];
    private float[] tubeOffset = new float[tubeNum];
    private float tubeDistance;
    private int score = 0;
    private int scoreTube = 0;
    Circle birdCircle;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;


    @Override
    public void create() {
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(11);
        batch = new SpriteBatch();
        gameOver = new Texture("gameover.png");
        background = new Texture("bg.png");
        bird = new Texture("bird.png");
        bottomTube = new Texture("bottomtube.png");
        topTube = new Texture("toptube.png");
        birdAtlas = new TextureAtlas(Gdx.files.internal("shooter.atlas"));
        animation = new Animation<TextureAtlas.AtlasRegion>(1 / 8f, birdAtlas.getRegions());
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        rng = new Random();
        tubeDistance = Gdx.graphics.getWidth() * 3 / 4;
        shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        topTubeRectangles = new Rectangle[tubeNum];
        bottomTubeRectangles = new Rectangle[tubeNum];
        beginGame();
    }

    public void beginGame() {
        birdY = Gdx.graphics.getHeight() / 2 - bird.getHeight() / 2;

        for (int i = 0; i < tubeNum; i++) {
            tubeOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * tubeDistance;

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            // Checks if tube is passed the middle of the screen.
            if (tubeX[scoreTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                // 0 <= scoreTube < tubeNum
                if (scoreTube < tubeNum - 1) scoreTube++;
                else scoreTube = 0;
            }

            if (Gdx.input.isTouched()) {
                velocity = -30;

            }

            for (int i = 0; i < tubeNum; i++) {
                if (tubeX[i] < -bottomTube.getWidth()) {
                    tubeX[i] += tubeNum * tubeDistance;
                    tubeOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {
                    tubeX[i] -= tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);
                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            }

            if (birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            }
            else gameState = 2;

        } else if (gameState == 0) {
            if (Gdx.input.isTouched()) {
                gameState = 1;
            }
        }

        else {
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

            if (Gdx.input.isTouched()) {
                gameState = 1;
                beginGame();
                score = 0;
                scoreTube = 0;
                velocity = 0;
            }


        }

        timePassed += Gdx.graphics.getDeltaTime();
        currentFrame = animation.getKeyFrame(timePassed, true);
        batch.draw(currentFrame, Gdx.graphics.getWidth() / 2 - currentFrame.getRegionWidth() / 2, birdY);
        font.draw(batch, String.valueOf(score), 50, 200);
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + bird.getHeight() / 2, bird.getWidth() / 2);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.RED);
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for (int i = 0; i < tubeNum; i++) {
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
                    Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
                gameState = 2;
                Gdx.app.log("Collision", "Hit!");
            }

        }

//        shapeRenderer.end();


    }


    @Override
    public void dispose() {
        batch.dispose();
        birdAtlas.dispose();
    }
}
