package com.mygdx.game.screen;

import static com.mygdx.game.utils.Constants.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.audio.AudioManager;
import com.mygdx.game.components.ComponentObserver;
import com.mygdx.game.entities.Entity;
import com.mygdx.game.entities.EntityFactory;
import com.mygdx.game.manager.ResourceManager;
import com.mygdx.game.map.GameStage;
import com.mygdx.game.utils.Joystick;

import java.util.ArrayList;

public class GameScreen extends BaseScreen implements ComponentObserver {
    private final float cameraViewport = 6;
    private final Vector2 screenSize;
    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer = new Box2DDebugRenderer();
    protected OrthogonalTiledMapRenderer mapRenderer;

    private ArrayList<GameStage> gameStages = new ArrayList<>();
    private Joystick joystick;
    private Entity player;

    //room cache
    private Texture floor;
    private Texture walls;

    public GameScreen(MyGdxGame gdxGame, Batch batch) {
        super(gdxGame, batch);

        createWorld();
        box2DDebugRenderer.SHAPE_STATIC.set(0, 0, 255, 255);
        box2DDebugRenderer.SHAPE_AWAKE.set(0, 0, 255, 255);

        camera = new OrthographicCamera();
        screenSize = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, screenSize.x / cameraViewport, screenSize.y / cameraViewport);


        GameStage stage = new GameStage(world);
        gameStages.add(stage);
        mapRenderer = new OrthogonalTiledMapRenderer(stage.getCurrentRoom().getMap());

        joystick = new Joystick(200, 200, 50);
        player = EntityFactory.getInstance().getEntity(EntityFactory.EntityType.PLAYER, world);
        onRoomChanged();
    }

    @Override
    public void onNotify(String value, ComponentEvent event) {

    }

    @Override
    public void show() {

    }

    private void update() {
        world.step(1 / 60f, 6, 2);
        camera.position.set(new Vector3(128, 64, 0));
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        mapRenderer.setView(camera);
    }

    @Override
    public void render(float delta) {
        this.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();

        batch.draw(floor, -16, -16);
        batch.draw(walls, -32, -32);

        batch.end();

        player.update(batch, delta);

        mapRenderer.render();

        joystick.render(game.getBatch());


        box2DDebugRenderer.render(world, camera.combined.scl(PPM));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        joystick.start(screenX, screenY);

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        joystick.dragged(screenX, screenY);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        joystick.end();

        return true;
    }

    public void onRoomChanged() {
        floor = gameStages.get(0).getCurrentRoom().getFloor();
        walls = gameStages.get(0).getCurrentRoom().getWalls();
    }

    private void createWorld() {
        world = new World(new Vector2(0, 0), false);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

    }
}