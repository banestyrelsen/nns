package com.stk.nns.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.stk.nns.input.PathingTestInputProcessor;
import com.stk.nns.map.GameBoard;
import com.stk.nns.map.Tile;
import com.stk.nns.map.TileIndex;
import com.stk.nns.pathfinding.astar.AStar;

import java.util.List;
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

    List<Tile> path;
    AStar aStar;
    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        gameBoard = new GameBoard("maps/map0.map");


        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        System.out.println("camera.position: " + camera.position.x + "m " + camera.position.y + " , " + camera.position.z);
        /*        camera.translate(new Vector3(GameBoard.BOARD_WIDTH_PIXELS / 2f, GameBoard.BOARD_HEIGHT_PIXELS / 2f, 0f));*/
        /*camera.zoom = 2.0f;*/
        camera.position.set(new Vector3(GameBoard.BOARD_WIDTH_PIXELS / 2f, GameBoard.BOARD_HEIGHT_PIXELS / 2f, 0f));
        camera.update();
        inputProcessor = new PathingTestInputProcessor(camera);
        Gdx.input.setInputProcessor(inputProcessor);
        inputProcessor.setGameBoard(gameBoard);
        inputProcessor.setPathing(this);

        aStar = new AStar();
        path = aStar.getPath(gameBoard.getGrid()[1][1], gameBoard.getGrid()[4][2], gameBoard);

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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        Tile[][] grid = gameBoard.getGrid();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                Tile tile = grid[x][y];
                if (tile.getValue() == GameBoard.SOLID) {
                    drawShape(tile.getPosition().x, tile.getPosition().y, Game.TILESIZE, Game.TILESIZE, color_solid);
                }
            }
        }


        shapeRenderer.end();
        drawGrid();
        drawRoute();
        highlightGridOnMouseOver();

/*        drawPath();*/

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
        if (null != mouseTile) {
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
        if (pathStart != null && pathStart.getValue() != GameBoard.SOLID) {
            if (pathEnd != null && pathEnd.getValue() != GameBoard.SOLID) {
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

    private void drawRoute() {
        if (pathStart != null && pathStart.getValue() != GameBoard.SOLID && pathEnd == null) {
            drawLine(pathStart.getPosition(), inputProcessor.getMousePosition(), Game.TILESIZE / 2, Color.WHITE);
        }
        if (path != null && path.size() > 1) {
            for (int i = 0; i < path.size()-1; i++) {
                drawLine(path.get(i).getPosition(), path.get(i+1).getPosition(), Game.TILESIZE / 2, Color.WHITE);
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
            path = aStar.getPath(pathStart, pathEnd, gameBoard);
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.position.set(width / 2f, height / 2f, 0); //by default camera position on (0,0,0)
    }

    public void updatePath() {
        if (pathStart != null && pathEnd != null) {
            path = aStar.getPath(pathStart, pathEnd, gameBoard);
        }
    }
}
