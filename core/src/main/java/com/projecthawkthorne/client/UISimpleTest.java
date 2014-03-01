package com.projecthawkthorne.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.projecthawkthorne.client.display.Assets;
import com.projecthawkthorne.content.GameKeys;

public class UISimpleTest {
        Skin skin;
        Stage stage;
        SpriteBatch batch;
		private Button jumpButton;
		private TextureRegion upArrowTexture = new TextureRegion(Assets.loadTexture("controls/uparrow.png"));
		private TextureRegion downArrowTexture = new TextureRegion(Assets.loadTexture("controls/downarrow.png"));
		private TextureRegion leftArrowTexture = new TextureRegion(Assets.loadTexture("controls/leftarrow.png"));
		private TextureRegion rightArrowTexture = new TextureRegion(Assets.loadTexture("controls/rightarrow.png"));
		private TextureRegion jumpTexture = new TextureRegion(Assets.loadTexture("controls/jumpbutton.png"));
		private TextButton upButton;
		private TextButton rightButton;
		private TextButton leftButton;
		private TextButton downButton;


        public void create () {
                batch = new SpriteBatch();
                stage = new Stage();
                Gdx.input.setInputProcessor(stage);

                // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
                // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
                skin = new Skin();

				Texture map = Assets.loadTexture("bboxTexture.png");
				TextureRegion texture = new TextureRegion(map);
				skin.add("white", texture);

                // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
                TextButtonStyle jumpButtonStyle = new TextButtonStyle();
                jumpButtonStyle.up = skin.newDrawable("white", 0.8f, 0.8f, 0.8f, 0.5f);
                jumpButtonStyle.down = skin.newDrawable("white", 0, 1, 0, 0.5f);
                jumpButtonStyle.over = skin.newDrawable("white", 0, 0.8f, 0, 0.5f);
                BitmapFont font = Assets.getFont();
                font.setColor(0, 0, 0, 0.5f);
                font.setScale(4, 4);
                jumpButtonStyle.font = font;
                skin.add("default", jumpButtonStyle);

                // Create a table that fills the screen. Everything else will go inside this table.
                Table dirTable = new Table();
                Table jumpTable = new Table();

                // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
                jumpButton = new TextButton("JUMP",skin);
                upButton = new TextButton("UP",skin);
                downButton = new TextButton("DOWN",skin);
                leftButton = new TextButton("LEFT",skin);
                rightButton = new TextButton("RIGHT",skin);

            	dirTable.add();
                dirTable.add(upButton);
                dirTable.row();
                dirTable.add(leftButton);
                dirTable.add();
                dirTable.add(rightButton);
                dirTable.row();
                dirTable.add();
                dirTable.add(downButton);
                dirTable.align(Align.bottom|Align.left);
                
                dirTable.setFillParent(true);
                dirTable.debug();
                
                jumpTable.add(jumpButton);
                jumpTable.align(Align.bottom|Align.right);
                jumpTable.setFillParent(true);
                jumpTable.debug();
                
                stage.addActor(dirTable);
                stage.addActor(jumpTable);

        }

        public void render () {
                stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
                stage.draw();
                Table.drawDebug(stage);
        }

        public void resize (int width, int height) {
                stage.setViewport(width, height, false);
        }

        public void dispose () {
                stage.dispose();
                skin.dispose();
        }

		public void update(long dt) {
			// TODO Auto-generated method stub
			
		}
		
		public boolean getIsAndroidKeyDown(GameKeys gk) {
			
			boolean result = false;
			if(!Gdx.input.isTouched()){
				return false;
			}
			
			switch (gk) {
			case JUMP:
				result = jumpButton.isOver();
				break;
			case DOWN:
				result = downButton.isOver();
				break;
			case LEFT:
				result = leftButton.isOver();
				break;
			case RIGHT:
				result = rightButton.isOver();
				break;
			case UP:
				result = upButton.isOver();
				break;
			default:
				break;
			}
			return result;
		}
}
