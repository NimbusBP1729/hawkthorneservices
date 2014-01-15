package com.projecthawkthorne.gamestate;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.projecthawkthorne.client.HawkthorneParentGame;
import com.projecthawkthorne.content.GameKeys;
import com.projecthawkthorne.content.Player;
import com.projecthawkthorne.content.UUID;
import com.projecthawkthorne.content.nodes.Door;
import com.projecthawkthorne.content.nodes.Node;

public abstract class Gamestate implements Screen{
	protected static HawkthorneParentGame context;
	private Set<Player> players = new HashSet<Player>();

	public abstract String getName();

	final void addPlayer(Player player) {
		if (players.contains(player)) {
			Gdx.app.error(
					"player adding error",
					"level '" + this.getName() + "' already contains "
							+ player.getId() + ":" + player.getUsername());
		} else {
			players.add(player);
		}
	}

	final boolean removePlayer(Player p) {
		return players.remove(p);
	}

	public final Set<Player> getPlayers() {
		return players;
	}

	public Door getDoor(String doorName) {
		throw new UnsupportedOperationException("this class:("
				+ this.getClass().getName() + ") has no doors");
	}

	public Map<UUID, Node> getNodeMap() {
		throw new UnsupportedOperationException("this class:("
				+ this.getClass().getName() + ") has no nodes");
	}

	public abstract void draw(SpriteBatch batch);

	public static final void setContext(HawkthorneParentGame game) {
		context = game;
	}
	
	public static boolean getIsAndroidKeyDown(GameKeys gk) {
		boolean result;
		boolean isFirstInRegion = false;
		boolean isSecondInRegion = false;
		boolean isFirstTouched = Gdx.input.isTouched();
		boolean isSecondTouched = Gdx.input.isTouched(1);
		int firstTouchX = Gdx.input.getX();
		int firstTouchY = Gdx.input.getY();
		int secondTouchX = Gdx.input.getX(1);
		int secondTouchY = Gdx.input.getY(1);
		int height = Gdx.graphics.getHeight();
		int width = Gdx.graphics.getWidth();

		switch (gk) {
		case ATTACK:
			break;
		case DOWN:
			isFirstInRegion = firstTouchY > 2 * height / 3;
			isSecondInRegion = secondTouchY > 2 * height / 3;
			break;
		case INTERACT:
			break;
		case JUMP:
			isFirstInRegion = firstTouchX > width / 3
					&& firstTouchY > height / 3 && firstTouchX < 2 * width / 3
					&& firstTouchY < 2 * height / 3;
			isSecondInRegion = secondTouchX > width / 3
					&& secondTouchY > height / 3
					&& secondTouchX < 2 * width / 3
					&& secondTouchY < 2 * height / 3;
			break;
		case LEFT:
			isFirstInRegion = firstTouchX < width / 3;
			isSecondInRegion = secondTouchX < width / 3;
			break;
		case RIGHT:
			isFirstInRegion = firstTouchX > 2 * width / 3;
			isSecondInRegion = secondTouchX > 2 * width / 3;
			break;
		case SELECT:
			break;
		case START:
			break;
		case UP:
			isFirstInRegion = firstTouchY < height / 3;
			isSecondInRegion = secondTouchY < height / 3;
			break;
		default:
			break;

		}
		result = (isFirstTouched && isFirstInRegion)
				|| (isSecondTouched && isSecondInRegion);
		return result;
	}


}
