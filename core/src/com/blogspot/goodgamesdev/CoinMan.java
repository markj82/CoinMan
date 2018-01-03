package com.blogspot.goodgamesdev;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;

	// saving high score in memory
	Preferences savingGameScore;

	//any visual image, we need to define Texture
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.35f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	BitmapFont font;
	BitmapFont highScoreFont;
	Texture dizzy;
	BitmapFont tapToPlay;

	int score = 0;
	int gameState = 0;

	Random random;

	//declaring sound:
	Sound coinSound;
	Sound bombSound;
	Sound grenade;
	Sound jump;
	Music music;

	Integer maxResult = 0;

	//HIGH SCORE
	ArrayList<Integer> highscore = new ArrayList<Integer>();

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount; //proper spacing between the coins

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background1.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("boybomb.png");

		random = new Random();

		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		tapToPlay = new BitmapFont();
		tapToPlay.setColor(Color.BLUE);
		tapToPlay.getData().setScale(6);

		highScoreFont = new BitmapFont();
		highScoreFont.setColor(Color.WHITE);
		highScoreFont.getData().setScale(10);

		coinSound = Gdx.audio.newSound(Gdx.files.internal("coin.mp3"));
		bombSound = Gdx.audio.newSound(Gdx.files.internal("bomb.mp3"));
		grenade = Gdx.audio.newSound(Gdx.files.internal("grenade.mp3"));
		jump = Gdx.audio.newSound(Gdx.files.internal("jump.mp3"));
		music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
		music.setVolume(0.5f);

	}

	public void makeCoin() {
		//random number between 0 and 1:
		float height = random.nextFloat() * Gdx.graphics.getHeight(); //if 0 it goes to the bottom of screen
																	// if 1 goes to the top of height, or somewhere between
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb() {
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());

	}


			// render method - constantly repeating loop, over and over again...
	@Override
	public void render () { //tu jest tak naprawde cala mechanika gry, w metodzie render
		batch.begin(); //poczatek wszystkiego

		//wypelnienie calego ekranu w urzadzeniu obrazkiem z background, czyli bg.png
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1) {

			music.play();
			music.isLooping();
			//GAME IS LIVE

			// BOMBS every 200 loops, we making 1 bomb
			if (bombCount < 200) {
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}
			//and now we drawing bombs on the screen
			bombRectangles.clear();
			for (int i = 0; i < bombXs.size(); i++) {
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) -7); //  -8 is speed, faster than coin -4 speed
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));

			}


			//COINS   every 100 loops, me making 1 coin
			if(coinCount < 100) {
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}
			//drawing coins on the screen

			coinRectangles.clear();
			for (int i = 0; i < coinXs.size(); i++) {
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) -4);
				// 									where                     and how big
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}


			// when user touch screen, true
			if (Gdx.input.justTouched()) {

				jump.play();
				velocity = -10;

			}

			if (pause < 8) {
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity = velocity + gravity;
			manY -= velocity;

			if (manY <= 105) {
				manY = 105;
			}

		} else if (gameState == 0) {
			tapToPlay.draw(batch, "Tap to play", 100, 450);
			// waiting to start
			if(Gdx.input.justTouched()) {
				gameState = 1;
			}


		} else if (gameState == 2) {

			//GAME OVER

			if(Gdx.input.justTouched()) {


				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount = 0;
			}


		}

		if (gameState == 2) {
			//dizzy guy
			batch.draw(dizzy, Gdx.graphics.getWidth() / 4 - man[manState].getWidth() / 2, manY);


		} else {
			//normal guy
			batch.draw(man[manState], Gdx.graphics.getWidth() / 4 - man[manState].getWidth() / 2, manY);
		}


		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 4 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for (int i = 0; i < coinRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
				Gdx.app.log("Coin", "collisiooon!!");
				score++;
				highscore.add(score);
				maxResult = Collections.max(highscore);
				coinSound.play();

				coinRectangles.remove(i); //ludzik i moneta jest jakas chwile razem, wiec score zwiekszal by sie caly czas, te 4 linijki temu zapobiegaja
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
			// hitting a bomb
		for (int i = 0; i < bombRectangles.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {

				Gdx.app.log("Bomb", "collisiooon!!");

				bombRectangles.remove(i);
				bombXs.remove(i);
				bombYs.remove(i);
				bombSound.play(); //dzieki tym linijom kodu, dzwiek jest zagrany TYLKO RAZ
				gameState = 2;
				break;
			}
		}



		font.draw(batch, String.valueOf(score), 100, 1150); // 100 i 200 is bottom left of screen
		highScoreFont.draw(batch, String.valueOf(maxResult), 550, 1150);

		batch.end(); //koniec gry
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
