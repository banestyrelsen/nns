package com.stk.nns.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.stk.nns.input.PathingTestInputProcessor;
import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;
import com.stk.nns.map.TileIndex;

import java.util.Map;

public class Pathing extends ApplicationAdapter {
    GameBoard gameBoard;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    PathingTestInputProcessor inputProcessor;

    Color color_solid = Color.RED;
    Color color_empty = Color.BLACK;
    Color color_path = Color.YELLOW;

    boolean pathStartSet = false;
    Tile pathStart = null;
    Tile pathEnd = null;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        gameBoard = new GameBoard("maps/map0.map");



        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 2.0f;
        camera.update();
        inputProcessor = new PathingTestInputProcessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);
        inputProcessor.setGameBoard(gameBoard);
        inputProcessor.setPathing(this);

    }

    public PathingTestInputProcessor getInputProcessor() {
        return inputProcessor;
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        super.dispose();
    }

    @Override
    public void render() {
        camera.update();
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        for (Map.Entry<TileIndex, Tile> entry :  gameBoard.getTiles().entrySet()) {
            Tile tile = entry.getValue();
            if (tile.getValue() == GameBoard.SOLID) {
                /*                shapeRenderer.point(tile.getPosition().x, tile.getPosition().y, 0);*/
                drawShape(tile.getPosition().x, tile.getPosition().y, Game.TILESIZE, Game.TILESIZE, color_solid);
/*                shapeRenderer.setColor(color_solid);
                shapeRenderer.rect(tile.getPosition().x, tile.getPosition().y, Game.TILESIZE, Game.TILESIZE);*/
/*                shapeRenderer.box(tile.getPosition().x+1, tile.getPosition().y+1, 0, Game.TILESIZE-1, Game.TILESIZE-1, 0);*/
                /*               System.out.println(tile.getPosition().x +Game.TILESIZE) + " , " + tile.getPosition().y);*/
/*                drawShape(tile.getPosition().x, tile.getPosition().y, tile.getPosition().x +Game.TILESIZE ,
                        tile.getPosition().y + Game.TILESIZE, color_solid);*/


/*            } else if (tile.getValue() == FOOD) {
                batch.draw(foodTile, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == SNAKE) {
                batch.draw(textureSnakeBody, tile.getPosition().x, tile.getPosition().y);
            } else if (tile.getValue() == SNAKE_HEAD) {
                batch.draw(tileHead, tile.getPosition().x, tile.getPosition().y);*/
            }

        }
        shapeRenderer.end();
        drawGrid();
        highlightGridOnMouseOver();



        drawPath();

    }

    private void drawGrid() {
        // Draw grid
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (int x = 0; x < GameBoard.BOARD_WIDTH_PIXELS; x += Game.TILESIZE) {
            shapeRenderer.line(x, 0, x, GameBoard.BOARD_HEIGHT_PIXELS, Color.GRAY, Color.GRAY);
        }
        for (int y = 0; y < GameBoard.BOARD_HEIGHT_PIXELS; y += Game.TILESIZE) {
            shapeRenderer.line(0, y, GameBoard.BOARD_WIDTH_PIXELS, y, Color.GRAY, Color.GRAY);
        }
        shapeRenderer.end();

    }

    private void highlightGridOnMouseOver() {
        // Highlight
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Tile mouseTile = gameBoard.getTileAtPosition(inputProcessor.getMousePosition());
        if (null != mouseTile ) {
            drawShape(mouseTile.getPosition().x, mouseTile.getPosition().y, Game.TILESIZE, Game.TILESIZE, Color.GREEN);
/*            shapeRenderer.rect();*/
/*            if (mouseTile.getValue() == GameBoard.EMPTY) {

            } else if (mouseTile.getValue() == GameBoard.SOLID) {
                shapeRenderer.setColor(color_empty);
            }*/
/*            shapeRenderer.line(mouseTile.getPosition().x, mouseTile.getPosition().y, mouseTile.getPosition().x + Game.TILESIZE, mouseTile.getPosition().y + Game.TILESIZE, Color.WHITE, Color.WHITE);*/
/*            shapeRenderer.box(mouseTile.getPosition().x, mouseTile.getPosition().y, 0, Game.TILESIZE, Game.TILESIZE, 0);*/
        }
        shapeRenderer.end();
    }

    private void drawShape(float x, float y, float width, float height, Color color) {
        shapeRenderer.rect(x, y, width, height, color, color, color, color);
    }

    public void update() {

    }

    private void drawPath() {
        if (pathStart != null) {
            if (pathEnd != null) {
                drawLine(pathStart.getPosition(), pathEnd.getPosition(), Game.TILESIZE / 2, Color.WHITE);
            } else {
                drawLine(pathStart.getPosition(), inputProcessor.getMousePosition(), Game.TILESIZE / 2, Color.WHITE);
/*                Tile mouseTile = gameBoard.getTileAtPosition(inputProcessor.getMousePosition());*/
/*                if (mouseTile != null) {
                    drawLine(pathStart.getPosition(), mouseTile.getPosition(), Game.TILESIZE / 2, Color.WHITE);
                }*/
            }

        }

    }

    private void drawLine(Vector2 a, Vector2 b, int pixelOffset, Color color) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(
                a.x + pixelOffset,
                a.y + pixelOffset,
                b.x + pixelOffset,
                b.y + pixelOffset,
                color, color);
        shapeRenderer.end();
    }

    public void setPathPosition(Tile tile) {
        if (pathStart != null && pathEnd != null) {
            pathStart = tile;
            pathEnd = null;
        } else if (pathStart == null) {
            pathStart = tile;
            pathEnd = null;
        } else {
            this.pathEnd = tile;
        }
    }
}
