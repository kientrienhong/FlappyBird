package com.trienhk.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameover;
	Texture[] birds;
	Texture[] tube;
	float gap =400;
	float maxTubeOffset;
	Random randomGenergator;
	Circle birdCircle ;
	BitmapFont font;
	float tubeVelocity = 4;

	int score;
	int scoringTube = 0;
	int flapState = 0;
	float birdY = 0;
	float velocity = 0;

	int gameState = 0;
	float gravity = 2;
	int numberOfTubes  = 4;
	float distanceBetweenTubes;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	Rectangle[] bottomTubeRectangles;
	Rectangle[] topTubeRectangles;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		tube = new Texture[2];
		gameover = new Texture("gameover.png");
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		tube[0] = new Texture("toptube.png");
		tube[1] = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap/2 - 100;
		randomGenergator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;
		birdCircle = new Circle();
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		topTubeRectangles = new Rectangle[numberOfTubes];
		score = 0;
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		startGame();
	}

	public void startGame(){
		birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++){
			tubeOffset[i] = (randomGenergator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			tubeX[i] = Gdx.graphics.getWidth() / 2 - tube[0].getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();

		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				if (scoringTube < numberOfTubes  - 1){
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			if (Gdx.input.justTouched()){
				velocity = -20;

			}

			for (int i = 0; i < numberOfTubes; i++) {


				if (tubeX[i] < - tube[0].getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
				} else {
					tubeX[i] -= tubeVelocity;

				}

				batch.draw(tube[0], tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(tube[1], tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - tube[0].getHeight() + tubeOffset[i]);

				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], tube[0].getWidth(), tube[0].getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - tube[1].getHeight() + tubeOffset[i], tube[1].getWidth(), tube[1].getHeight());
			}

			if (birdY > 0) {
				velocity += gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}
        } else if (gameState == 0){

            if (Gdx.input.justTouched()){
                gameState = 1;
            }
        } else if (gameState == 2){
			batch.draw(gameover, Gdx.graphics.getWidth() / 2 - gameover.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameover.getHeight() / 2);

			if (Gdx.input.justTouched()){
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0; 
			}
		}

		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
		batch.end();
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++){
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], tube[0].getWidth(), tube[0].getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - tube[1].getHeight() + tubeOffset[i], tube[1].getWidth(), tube[1].getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){

				gameState = 2;

			}
		}
//		shapeRenderer.end();
	}

}
