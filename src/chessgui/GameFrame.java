package chessgui;

import chessgame.*;
import chessutil.FileReader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Main JFrame for the project. Handles transferring user input between the
 * various menus and the GameController as well as adding or removing menus so
 * that the user can choose piece color, game mode and so on.
 *
 * @author John Polus
 */
public class GameFrame extends JFrame implements ActionListener, PropertyChangeListener
{

    private static final int FRAME_HEIGHT = 750;
    private static final int FRAME_WIDTH = 750;

    public static final String WINDOW_TITLE = "John Polus's Chess Game";

    public static final String KEEP_BOARD_TITLE = "Keep current board setup?";
    public static final String KEEP_BOARD_MSG = "Do you want to start playing "
            + "with the shown board setup or start a new game?";
    public static final String ILLEGAL_BOARD_POS = "Board configuration illegal";

    public static final String GAME_OVER_TITLE = "Game Over!";
    public static final String GAME_WON_MSG = "You have won!";
    public static final String GAME_TIED_MSG = "It's a tie";
    public static final String GAME_LOST_MSG = "You have lost!";

    private class MyMouseAdapter extends MouseAdapter
    {

        /**
         * Handles the mousePressed event for the component to which this
         * adapter is attached. If the mouse is pressed on the checkerboard,
         * then the appropriate methods are called on the game controller.
         *
         * @param e mouse event that was passed to this component
         */
        @Override
        public void mousePressed(MouseEvent e)
        {
            e = SwingUtilities.convertMouseEvent(null, e, gamePanel);
            int x = e.getX() - gamePanel.myBoard.getX();
            int y = e.getY() - gamePanel.myBoard.getY();
            if (x < Checkerboard.BOARD_WIDTH && y < Checkerboard.BOARD_HEIGHT)
            {
                int a = x / Checkerboard.SQUARE_WIDTH;
                int b = y / Checkerboard.SQUARE_HEIGHT;
                switch (modeMenu.getMode())
                {
                    case SET_UP:
                        doSetUp(e, a, b);
                        break;
                    case SINGLE:
                    case VERSUS:
                        doGamePlay(e, a, b);
                        break;
                }
            }
        }

        /**
         * Handles the mouseWheelMoved event for the component to which this
         * adapter is attached. Cycles through the types and colors for the
         * pieceToAdd.
         *
         * @param e mouseWheelEvent that is passed to this component
         */
        @Override
        public void mouseWheelMoved(MouseWheelEvent e)
        {
            ChessPiece pieceToAdd = determinePieceToAdd();
            int clicks = e.getWheelRotation();

            if (!e.isShiftDown()) // change piece type
            {
                ChessPiece newPiece = clicks > 0 ? getNextPieceType(pieceToAdd) : getPrevPieceType(pieceToAdd);
                pieceMenu.setPiece(newPiece);

            }
            else // change piece color
            {
                if (clicks != 0)
                {
                    if (colorMenu.getColor() == null)
                    {
                        colorMenu.setColor(ChessPiece.Color.WHITE);
                    }
                    else
                    {
                        colorMenu.flipColor();
                    }
                }
            }
            pieceToAdd = determinePieceToAdd();
            lbl_pieceToAdd.setText(pieceToAdd.toString());
        }

        private ChessPiece getPrevPieceType(ChessPiece oldPiece)
        {
            if (oldPiece == null)
            {
                return new King();
            }
            switch (oldPiece.oneLetterIdentifier())
            {
                case "":
                    return new King();
                case "K":
                    return new Queen();
                case "Q":
                    return new Bishop();
                case "B":
                    return new Knight();
                case "N":
                    return new Rook();
                default:
                    return new Pawn();
            }
        }

        private ChessPiece getNextPieceType(ChessPiece oldPiece)
        {
            if (oldPiece == null)
            {
                return new King();
            }

            switch (oldPiece.oneLetterIdentifier())
            {
                case "Q":
                    return new King();
                case "B":
                    return new Queen();
                case "N":
                    return new Bishop();
                case "R":
                    return new Knight();
                case "":
                    return new Rook();
                default:
                    return new Pawn();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            gamePanel.mouseMoved(e);
        }
    }

    public static int __count = 0;

    private ModeMenu modeMenu;
    private GamePanel gamePanel;
    private ColorMenu colorMenu;
    private PieceMenu pieceMenu;
    private FileMenu fileMenu;
    private JMenuBar menuBar;

    public GameController controller;
    public ChessPiece.Color humanPlayer;
    private JLabel lbl_pieceToAdd;
    private Scoreboard scoreboard;

    private BlockingQueue<GameRequest> tasks; // tasks that need to be done on the processing thread
    private BlockingQueue<GameRequest> responses; // responses to processing tasks
    private Timer checkForResponseTimer;
    private boolean boardEnabled = true;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                GameFrame gf = new GameFrame();
                gf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gf.setVisible(true);
            }

        });
    }

    public GameFrame()
    {
        super(WINDOW_TITLE);
        initComponents();
        initGameServer();
        humanPlayer = ChessPiece.Color.WHITE;

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void initMouseListener()
    {
        MouseAdapter m = new MyMouseAdapter();
        addMouseListener(m);
        addMouseWheelListener(m);
        addMouseMotionListener(m);
    }

    private void initComponents()
    {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        modeMenu = new ModeMenu();
        colorMenu = new ColorMenu();
        pieceMenu = new PieceMenu();
        fileMenu = new FileMenu();

        menuBar.add(fileMenu);
        menuBar.add(modeMenu);
        menuBar.add(colorMenu);
        menuBar.add(pieceMenu);

        pieceMenu.addPropertyChangeListener(this);
        modeMenu.addPropertyChangeListener(this);
        colorMenu.addPropertyChangeListener(this);
        fileMenu.addPropertyChangeListener(this);

        scoreboard = new Scoreboard(modeMenu.getMode());
        gamePanel = new GamePanel(scoreboard);
        add(gamePanel, BorderLayout.NORTH);

        lbl_pieceToAdd = new JLabel("");
        add(lbl_pieceToAdd);

        initMouseListener();
    }

    private void initGameServer()
    {
        // set up GUI's response timer
        checkForResponseTimer = new Timer(1, this);
        checkForResponseTimer.setActionCommand("Response Queue Timer");
        checkForResponseTimer.start();

        // set up "server" to send messages to
        tasks = new ArrayBlockingQueue<GameRequest>(25);
        responses = new ArrayBlockingQueue<GameRequest>(25);
        controller = new GameController(tasks, responses);
        new Thread(controller).start();

        gamePanel.setMoveList(controller.getMoveList());
    }

    private void addRequest(GameTask task, Object info)
    {
        tasks.add(new GameRequest(task, info, false));
    }

    private void doSetUp(MouseEvent e, int a, int b)
    {
        int button = e.getButton();
        ChessPiece pieceToAdd = determinePieceToAdd();
        switch (button)
        {
            case MouseEvent.BUTTON1: // Place piece
                if (pieceToAdd != null)
                {
                    gamePanel.myBoard.setPieceAt(a, b, pieceToAdd);
                }
                else
                {
                    gamePanel.myBoard.setPieceAt(a, b, null);
                }
                break;
            case MouseEvent.BUTTON2:  // Choose piece
                pieceToAdd = gamePanel.myBoard.getPieceAt(a, b);
                if (pieceToAdd == null)
                {
                    return;
                }
                pieceMenu.setPiece(pieceToAdd);
                colorMenu.setColor(pieceToAdd.getColor());
                lbl_pieceToAdd.setText(pieceToAdd.toString());
                break;
            case MouseEvent.BUTTON3: // Remove piece
                gamePanel.myBoard.setPieceAt(a, b, null);
                break;
            default:
                System.out.println("Button?: " + button);
        }
        addRequest(GameTask.SET_BOARD_POSITION, gamePanel.myBoard.getPiecesArray());
        repaint();
    }

    private void doGamePlay(MouseEvent e, int a, int b)
    {
        // Don't allow any game play actions while computer is thinking
        if (!boardEnabled || e.getButton() != MouseEvent.BUTTON1)
        {
            return;
        }

        Checkerboard boardGui = gamePanel.myBoard;
        boardGui.removePreviousMoveHighlight();
        Point selSpace = boardGui.selectedSpace;
        Point clickedSpace = new Point(a,b);
        ChessPiece clickedPiece = boardGui.getPieceAt(a, b);
        // select a piece
        if (selSpace == null)
        {
            if (clickedPiece != null && clickedPiece.getColor() == controller.getPlayerToMove())
            {
                boardGui.setSelectedSpace(clickedSpace);
                addRequest(GameTask.FIND_MOVES_FOR_PIECE, new Object[]
                {
                    boardGui.getPiecesArray(), clickedSpace
                });
                repaint();
            }
            return;
        } // unselect the piece
        else if (selSpace.equals(clickedSpace))
        {
            boardGui.setSelectedSpace(null);
            repaint();
            return;
        } // try to move, unselect if successful
        else
        {
            GameTask task = GameTask.CREATE_MOVE;
            ChessPiece selPiece = boardGui.getSelectedPiece();
            Object info = new ChessMove(selPiece, selSpace, clickedSpace);
            addRequest(task, info);
        }
        repaint();
    }

    private void displayEndGameMessage()
    {
        ChessPiece.Color winner = controller.getWinningSide();
        String msg;
        if (winner == humanPlayer)
        {
            msg = GAME_WON_MSG;
        }
        else if (winner == null)
        {
            msg = GAME_TIED_MSG;
        }
        else
        {
            msg = GAME_LOST_MSG;
        }
        JOptionPane.showMessageDialog(this, msg, GAME_OVER_TITLE, JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        String property = event.getPropertyName();
        switch (property)
        {
            case "piece":
                lbl_pieceToAdd.setText(determinePieceToAdd().toString());
                break;
            case "color":
                lbl_pieceToAdd.setText(determinePieceToAdd().toString());
                break;
            case "mode":
                handleModePropertyChange(event);
                break;
            case "Save":
                saveToFile();
                break;
            case "Load":
                load();
                break;
            default:
                break;
        }
    }

    private void enableAllMenus()
    {
        modeMenu.setEnabled(true);
        pieceMenu.setEnabled(true);
        colorMenu.setEnabled(true);
        fileMenu.enableAll();
    }

    /**
     * Handles the logic of how to transition between modes, such as enabling or
     * disabling menu items, prompting user for additional info, etc.
     *
     *
     * @param event - change event that is being handled
     */
    private void handleModePropertyChange(PropertyChangeEvent event)
    {
        GameMode newMode = (GameMode) event.getNewValue();
        GameMode oldMode = (GameMode) event.getOldValue();
        switchGameMode(oldMode, newMode);
    }

    private void switchGameMode(GameMode oldMode, GameMode newMode)
    {
        switch (newMode)
        {
            case SINGLE:
                if (!switchToSinglePlayer())
                {
                    modeMenu.setMode(oldMode);
                    return;
                }
                break;
            case SET_UP:
                if (!switchToSetUp())
                {
                    modeMenu.setMode(oldMode);
                    return;
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     * @return whether mode can be switched successfully
     */
    private boolean switchToSinglePlayer()
    {
        enableAllMenus();
        int choice = promptForGameBeginning();
        switch (choice)
        {
            case 0:
                if (!startGameWithDisplayedPieces())
                {
                    JOptionPane.showMessageDialog(this, ILLEGAL_BOARD_POS);
                    return false;
                }
                break;
            case 1:
                setUpNewGame();
                break;
            default:
                return false;

        }
        colorMenu.setEnabled(false);
        pieceMenu.setEnabled(false);
        fileMenu.disable(GameMode.SINGLE);
        return true;
    }

    private boolean switchToSetUp()
    {
        enableAllMenus();
        return true;
    }

    private int promptForGameBeginning()
    {

        Object[] options =
        {
            "Keep Current Set-Up", "New Game", "Cancel"
        };
        return JOptionPane.showOptionDialog(this, KEEP_BOARD_MSG, KEEP_BOARD_TITLE, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    }

    private ChessPiece promptForPieceType()
    {
        Object[] options =
        {
            "Queen", "Rook", "Bishop", "Knight"
        };
        int val = JOptionPane.showOptionDialog(this, "What piece to promote to?", "Promotion Time", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (val)
        {
            case 1:
                return new Rook();
            case 2:
                return new Bishop();
            case 3:
                return new Knight();
            default:
                return new Queen();
        }
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        String command = event.getActionCommand();
        switch (command)
        {
            case "Main Menu":
                modeMenu.setMode(GameMode.UNDECIDED);
                break;
            case "Response Queue Timer":
                updateEllipsis();
                if (responses.peek() != null)
                {
                    processResponse(responses.poll());
                }
        }
    }

    private void updateEllipsis()
    {
        if (++__count % 2 != 0)
        {
            return;
        }
        String text = lbl_pieceToAdd.getText();

        if (text.endsWith(" ....."))
        {
            text = text.substring(0, text.length() - 4);
        }
        else if (text.endsWith("."))
        {
            text = text.concat(".");
        }
        lbl_pieceToAdd.setText(text);
    }

    private void processResponse(GameRequest response)
    {
        if (!response.success)
        {
            System.out.println("Task unsuccessful: " + response.task);
            return;
        }

        switch (modeMenu.getMode())
        {
            case SINGLE:
                processResponseSinglePlayer(response);
                break;
            case SET_UP:
                processResponseSetUp(response);
                break;
            default:
                System.out.println("Unknown mode, cannot process Game Request");
        }
    }

    private void processResponseSinglePlayer(GameRequest response)
    {
        switch (response.task)
        {
            case CREATE_MOVE:
                ChessMove move = (ChessMove) response.info;
                gamePanel.myBoard.setSelectedSpace(null);
                addRequest(GameTask.SET_PLAYER_TO_MOVE, humanPlayer);
                addRequest(GameTask.PLAY_MOVE, move);
                break;
            case FIND_MOVES_FOR_PIECE:
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>) response.info;
                gamePanel.myBoard.highlightPossibleMoves(moves);
                repaint();
                break;
            case FIND_BEST_MOVE:
                move = (ChessMove) response.info;
                if (move == null)
                {
                    return;
                }
                lbl_pieceToAdd.setText("CPU's move is: " + move.toString());
                if (controller.getPlayerToMove() == humanPlayer.opposite())
                {
                    addRequest(GameTask.PLAY_MOVE, move);
                }
                break;
            case PLAY_MOVE:
                move = (ChessMove) response.info;
                if (move != null && move.promotes)
                {
                    ChessPiece piece = promptForPieceType();
                    piece.setColor(move.piece.getColor());
                    controller.promoteToThisType(piece);
                    move.promotionPiece = piece.oneLetterIdentifier();
                }
                gamePanel.myBoard.setPieces(controller.getPiecesArray());
                gamePanel.switchPlayerToMove();
                gamePanel.myBoard.highlightPreviousMove(move);
                repaint();
                if (controller.isGameOver())
                {
                    displayEndGameMessage();
                    gamePanel.stopTimer();
                    modeMenu.setMode(GameMode.UNDECIDED);
                    return;
                }
                if (controller.getPlayerToMove() != humanPlayer)
                {
                    lbl_pieceToAdd.setText("CPU is thinking ...");
                    addRequest(GameTask.FIND_BEST_MOVE, null);
                    boardEnabled = false;
                }
                else // CPU took a turn
                {
                    boardEnabled = true;
                }
                break;
            case SET_BOARD_POSITION:
                ChessPiece[][] pieces = (ChessPiece[][]) response.info;
                if (pieces == null)
                {
                    break;
                }
                this.gamePanel.myBoard.setPieces(pieces);
                gamePanel.myBoard.setSelectedSpace(null);
                repaint();
                break;
            case SET_PLAYER_TO_MOVE:
                break;
            default:
                System.out.println("Response: " + response.info.toString());
        }
    }

    private void processResponseSetUp(GameRequest response)
    {
        switch (response.task)
        {
            case VALIDATE_POSITION:
                System.out.println("Position is valid, big deal");
                break;
            default:
                System.out.println("Task: " + response.task.toString());
        }
    }

    private void saveToFile()
    {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        File selectedFile;
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
        }
        else
        {
            return;
        }
        if (GameController.savePositionToFile(new ChessBoard(gamePanel.myBoard.getPiecesArray()), selectedFile.getAbsolutePath()))
        {
            System.out.println("Save Successful");
        }
        else
        {
            System.out.println("Save Failed");
        }
    }

    private ChessPiece determinePieceToAdd()
    {
        ChessPiece piece = pieceMenu.getPiece();
        piece.setColor(colorMenu.getColor());
        return piece;
    }

    /**
     * Loads a position from the file with the given pathname and displays it on
     * the checkerboard.
     *
     * @param pathname location of board position save file
     * @return true if position could be loaded successfully, false otherwise
     */
    private boolean loadFromFile(String pathname)
    {
        ChessBoard cb = new ChessBoard();
        if (FileReader.loadPositionFromFile(pathname, cb))
        {
            controller.setBoardPosition(cb.getPiecesArray(), humanPlayer);
            gamePanel.myBoard.setPieces(cb.getPiecesArray());
            repaint();
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Prompts the user for file to load from and then attempts to load board
     * position from that file. Displays a status message and, if successful,
     * the new board position.
     */
    private void load()
    {
        File selectedFile;
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
        }
        else
        {
            return;
        }
        if (loadFromFile(selectedFile.getAbsolutePath()))
        {
            System.out.println("Load Successful");
        }
        else
        {
            System.out.println("Load failed");
        }
    }

    /**
     * Tries to start a game with the displayed pieces, fails if the pieces do
     * not constitute a legal position
     *
     * @return true if displayed position is legal, false otherwise
     */
    private boolean startGameWithDisplayedPieces()
    {

        return controller.setBoardPosition(gamePanel.myBoard.getPiecesArray(), humanPlayer);
    }

    /**
     * Sets up the standard start-of-game board position on the checkerboard
     */
    private void setUpNewGame()
    {
        controller.startNewGame();
        gamePanel.myBoard.setPieces(controller.getPiecesArray());
        repaint();
    }
}
