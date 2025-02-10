package maerodrim.game.gui.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class GameCamera {
    private final OrthographicCamera camera;
    private final float zoomSpeed = 0.1f;
    private final float minZoom = 0.3f;
    private final float maxZoom = 1.0f;
    private final float moveSpeed = 1.5f;
    private float mapWidth, mapHeight;
    private float lastX, lastY;
    private boolean dragging = false;

    public GameCamera(float viewportWidth, float viewportHeight, float mapWidth, float mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        camera.update();

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                zoom(amountY);
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.MIDDLE) {
                    startDrag(screenX, screenY);
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.MIDDLE) {
                    stopDrag();
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (dragging) {
                    move(screenX, screenY);
                }
                return false;
            }
        });
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void zoom(float amount) {
        camera.zoom += amount * zoomSpeed;
        camera.zoom = Math.max(minZoom, Math.min(maxZoom, camera.zoom));
        camera.update();
        clampCamera();
    }

    private void startDrag(float screenX, float screenY) {
        dragging = true;
        lastX = screenX;
        lastY = screenY;
    }

    private void stopDrag() {
        dragging = false;
    }

    private void move(float screenX, float screenY) {
        float deltaX = (lastX - screenX) * moveSpeed * camera.zoom;
        float deltaY = (screenY - lastY) * moveSpeed * camera.zoom; // Инверсия Y
        camera.translate(deltaX, deltaY);
        camera.update();
        clampCamera();
        lastX = screenX;
        lastY = screenY;
    }

    private void clampCamera() {
        float halfWidth = camera.viewportWidth * camera.zoom / 2;
        float halfHeight = camera.viewportHeight * camera.zoom / 2;

        float minX = halfWidth;
        float maxX = mapWidth - halfWidth;
        float minY = halfHeight;
        float maxY = mapHeight - halfHeight;

        camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
        camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));

        camera.update();
    }
}
