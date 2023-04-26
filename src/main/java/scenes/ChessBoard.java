package scenes;

import graphics.Camera;
import input.Keyboard;
import scenes.chess.GameServer;
import scenes.pieces.Piece;
import scenes.pieces.Tile;
import ui.Text;
import ui.fonts.Font;
import static graphics.Graphics.setDefaultBackground;
import org.lwjgl.glfw.GLFW;

import util.Log;

public class ChessBoard extends Chess {

    // Drawable board
    public static Tile[][] board = new Tile[8][8];
    // Board data for networking
    public static char[][] boardData = new char[8][8];

    Tile currentSelectedTile = null;
    Tile futureSelectedTile = null;

    Text info;
    Text nextTurn;

    String ipText = "IP: " + GameServer.getIp();
    String turn = "Turn: "; //Tile.getTurn();

    public static boolean isServer = false;
    
    public void awake() {
        camera = new Camera();
        setDefaultBackground(100, 100, 100);

        Log.setLogLevel(Log.ALL);

        Piece.loadSprites("src/assets/images/pack.png");

        info = new Text(ipText, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, 10, 10);
        nextTurn = new Text(turn, new Font("src/assets/fonts/AnimeAce.ttf", 20, true), PRIMARY_LIGHT, 600, 10);

        isServer = server != null;

        if (isServer) {
            ipText = ipText + " as server.";
        } else {
            ipText = ipText + " as client.";
        }
        info.change(ipText);

        if (isServer) {
            boardData = createBoardData();
            Log.info("SERVER - Board created!");
        } else {
            Log.info("CLIENT - Waiting for board to be recieved from server!");
        }
    }

    public char[][] createBoardData () {
        return Tile.getStartingLayout();
    }

    private void serverUpdate () {
        // board is displayed with y rows first then x columns

    }

    public static void movePiece (int oldX, int oldY, int newX, int newY, char type) {
        // Log.debug("CLIENT - Moving piece from " + oldX + ", " + oldY + " to " + newX + ", " + newY);

        board[newX][newY].setPiece(board[oldX][oldY].getPiece());
        board[oldX][oldY].setPiece(null);
    }

    private void clientUpdate () {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (board[y][x] == null) continue;

                board[y][x].update();

                if(board[y][x].isPieceClicked() && currentSelectedTile != null) {
                    // If the currently selected tile is not null, then move the piece to the new tile because the user already clicked a tile before
                    // Log.p("Board " + x + ", " + y + " has been moved to new location!");
                    futureSelectedTile = board[y][x];

                    // Request the server to update the board
                    client.sendMove(currentSelectedTile, futureSelectedTile);

                    // // Reset the currently selected tile and the future selected tile
                    currentSelectedTile.setIsPieceClicked(false);
                    futureSelectedTile.setIsPieceClicked(false);

                    // // Reset the currently selected tile and the future selected tile
                    currentSelectedTile = null;
                    futureSelectedTile = null;
                }
                
                if(board[y][x].isPieceClicked() && currentSelectedTile == null && board[y][x].isOccupied()) {
                    // If the currently selected tile is null, then set the currently selected tile to the tile that was clicked
                    currentSelectedTile = board[y][x];
                    // Log.p("Board " + x + ", " + y + " has been selected to move!");
                }

                board[y][x].setIsPieceClicked(false);                
            }
        }
    }

    public void update () {
        // exit the chessboard by pressing escape
        if(Keyboard.getKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            Log.p("Client shutting down!");
            System.exit(0);
        }

        if (isServer) serverUpdate();
        
        clientUpdate();        
    }
}
