package com.wangwenchao.gameworld;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.wangwenchao.gameobjects.Bird;
import com.wangwenchao.gameobjects.ScrollHandler;
import com.wangwenchao.zbHelpers.AssetLoader;

public class GameWorld {

	private Bird bird;
	private ScrollHandler scroller;
	private Rectangle ground;
	
	private int score = 0;
    private float runTime = 0;
	
	private GameState currentState;
	private GameRenderer renderer;
		
	public int midPointY;
	
	public enum GameState {
		MENU, READY, RUNNING, GAMEOVER, HIGHSCORE
	}

	public GameWorld(int midPointY) {
		currentState = GameState.MENU;
		this.midPointY = midPointY;
		bird = new Bird(33, midPointY - 5, 17, 12);
		scroller = new ScrollHandler(this, midPointY + 66);
		ground = new Rectangle(0, midPointY + 66, 136, 11);
	}

	public void updateRunning(float delta) {

		if (delta > .15f) {
            delta = .15f;
        }

        bird.update(delta);
        scroller.update(delta);

        if (scroller.collides(bird) && bird.isAlive()) {
            scroller.stop();
            bird.die();
            AssetLoader.dead.play();
            renderer.prepareTransition(255, 255, 255, .3f);
            AssetLoader.fall.play();
        }

        if (Intersector.overlaps(bird.getBoundingCircle(), ground)) {
        	if (bird.isAlive()) {
				AssetLoader.dead.play();
				renderer.prepareTransition(255, 255, 255, .3f);
				bird.die();
			}
        	
        	scroller.stop();
            bird.die();
            bird.decelerate();
            currentState = GameState.GAMEOVER;
            
            if (score > AssetLoader.getHighScore()) {
                AssetLoader.setHighScore(score);
                currentState = GameState.HIGHSCORE;
            }
        }
	}
	
	public void update(float delta) {

		runTime += delta;
		
		switch (currentState) {
		case READY:
		case MENU:
			updateReady(delta);
			break;

		case RUNNING:
			updateRunning(delta);
			break;
		default:
			break;
		}

    }

    private void updateReady(float delta) {
    	bird.updateReady(runTime);
        scroller.updateReady(delta);
    }

	public Bird getBird() {
		return bird;
	}

	public ScrollHandler getScroller() {
		return scroller;
	}
	
	public int getScore() {
	    return score;
	}
	
	public int getMidPointY() {
        return midPointY;
    }

	public void addScore(int increment) {
	    score += increment;
	}

	public boolean isReady() {
        return currentState == GameState.READY;
    }
	
	public void ready() {
        currentState = GameState.READY;
        renderer.prepareTransition(0, 0, 0, 1f);
    }
	
    public void start() {
        currentState = GameState.RUNNING;
    }

    public void restart() {
        score = 0;
        bird.onRestart(midPointY - 5);
        scroller.onRestart();
        ready();
    }

    public boolean isGameOver() {
        return currentState == GameState.GAMEOVER;
    }
    
    public boolean isHighScore() {
        return currentState == GameState.HIGHSCORE;
    }
    
	public boolean isRunning() {
        return currentState == GameState.RUNNING;
    }
	
	public boolean isMenu() {
        return currentState == GameState.MENU;
    }
	
	public void setRenderer(GameRenderer renderer) {
		this.renderer = renderer;
	}
	
}
