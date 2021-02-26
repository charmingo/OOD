/*
 * 描述
 * 设计井字棋游戏。
 * 
 * 棋盘的尺寸为3
 * X 总是先行动走出第一步
 * 如果一个位置已经被占，且一名玩家打算占领该位置，一个AlreadyTakenException信息将被抛出
 * 如果一名玩家胜利，且有玩家打算继续行动，一个GameEndException信息将被抛出
 * 如果所有的地方都已被占领，你需要输出"it's a draw"
 *
 * board has fixed size of 3
 * X always take the first move
 * If a place already got taken, and one player want to take that place,
 * an AlreadyTakenException will be thrown
 * If one player wins, and somebody try to make another move, a GameEndException will be thrown.
 * If all the places got taken,you should print "it's a draw"
 * 
 * 样例
 *
 * 输入:
 * move(0, 0) // X 的回合
 * move(1, 0) // O 的回合 
 * move(1, 1) // X 的回合
 * move(2, 0) // O 的回合
 * move(2, 2) // X 的回合并获得胜利
 * move(0, 0)  //抛出 GameEndException
 * move(0, 0) // X 的回合
 * move(0, 0) // 抛出 AlreadyTakenException
 * move(1, 0) // O 的回合
 * move(1, 1) // X 的回合
 * move(2, 0) // o 的回合
 * move(2, 2) // X 的回合并获得胜利
 *
 * 输出:
 * x player wins!
 * x player wins!
 */

public class TicTacToe {
    private char[][] board;
    private char currentPlayerMark;
    private boolean gameEnd;
    
    public TicTacToe() {
        board = new char[3][3];
        initialize();
    }
    
    public char getCurrentPlayer() {
        return currentPlayerMark;
    }
    
    public void initialize() {
        gameEnd = false;
        currentPlayerMark = 'x';
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }
    
    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        gameEnd = true;
        return true;
    }
    
    public void changePlayer() {
        if (currentPlayerMark == 'x')
            currentPlayerMark = 'o';
        else
            currentPlayerMark = 'x';
    }
    
    // true means this move wins the game, false means otherwise
    public boolean move(int row, int col) throws AlreadyTakenException, GameEndException {
        
        if (gameEnd) {
            throw new GameEndException();
        }
        
        if (board[row][col] != '-') {
            throw new AlreadyTakenException();
        }
        
        board[row][col] = currentPlayerMark;
        
        boolean win;
        
        //check row
        win = true;
        for (int i = 0; i < board.length; i++) {
            if (board[row][i] != currentPlayerMark) {
                win = false;
                break;
            }
        }
        
        if (win) {
            gameEnd = true;
            return win;
        }
        
        //check column
        win = true;
        for (int i = 0; i < board.length; i++) {
            if (board[i][col] != currentPlayerMark) {
                win = false;
                break;
            }
        }
        
        if (win) {
            gameEnd = true;
            return win;
        }
        
        //check back diagonal
        win = true;
        for (int i = 0; i < board.length; i++) {
            if (board[i][i] != currentPlayerMark) {
                win = false;
                break;
            }
        }
        
        if (win) {
            gameEnd = true;
            return win;
        }
        
        //check forward diagonal
        win = true;
        for (int i = 0; i < board.length; i++) {
            if (board[i][board.length - i - 1] != currentPlayerMark) {
                win = false;
                break;
            }
        }
        
        if (win) {
            gameEnd = true;
            return win;
        }
        changePlayer();
        return win;
    }
}

class GameEndException extends Exception{
    public GameEndException() {
        super("Game has been ended, cannot make any more moves");
    }
}

class AlreadyTakenException extends Exception {
    public AlreadyTakenException() {
        super("This place has been taken");
    }
}
