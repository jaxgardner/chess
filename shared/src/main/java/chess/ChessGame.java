package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard;
    private TeamColor currentTurn;
    private ChessPosition kingBlackPosition;
    private ChessPosition kingWhitePosition;

    public ChessGame() {
        gameBoard = new ChessBoard();
        currentTurn = TeamColor.BLACK;
//        kingBlackPosition = new ChessPosition(8, 5);
//        kingWhitePosition = new ChessPosition(1, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void movePiece(ChessBoard board, ChessMove move) {
        ChessPiece movePiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movePiece);
        board.addPiece(move.getStartPosition(), null);
    }

    public void findKing(ChessBoard board) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition kingLooker = new ChessPosition(i, j);
                if(board.getPiece(kingLooker) != null) {
                    ChessPiece maybeKing = board.getPiece(kingLooker);
                    if(maybeKing.getPieceType() == ChessPiece.PieceType.KING && maybeKing.getTeamColor() == TeamColor.WHITE) {
                        kingWhitePosition = new ChessPosition(kingLooker);
                    } else if(maybeKing.getPieceType() == ChessPiece.PieceType.KING && maybeKing.getTeamColor() == TeamColor.BLACK) {
                        kingBlackPosition = new ChessPosition(kingLooker);
                    }
                }
            }
        }
    }

    public void findKing() {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition kingLooker = new ChessPosition(1, 1);
                if(gameBoard.getPiece(kingLooker) != null) {
                    ChessPiece maybeKing = gameBoard.getPiece(kingLooker);
                    if(maybeKing.getPieceType() == ChessPiece.PieceType.KING && maybeKing.getTeamColor() == TeamColor.WHITE) {
                        kingWhitePosition = new ChessPosition(kingLooker);
                    } else if(maybeKing.getPieceType() == ChessPiece.PieceType.KING && maybeKing.getTeamColor() == TeamColor.BLACK) {
                        kingBlackPosition = new ChessPosition(kingLooker);
                    }
                }
            }
        }
    }

    public HashSet<ChessPosition> getOpponentMoves(ChessBoard board, ChessGame.TeamColor color) {
        HashSet<ChessPosition> enemyPositions = new HashSet<>();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition tempEnd = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(tempEnd);

                if (piece != null && piece.getTeamColor() != color) {
                    enemyPositions.addAll(extractPiecePositions(board, piece, tempEnd));
                }
            }
        }

        return enemyPositions;
    }

    private HashSet<ChessPosition> extractPiecePositions(ChessBoard board, ChessPiece piece, ChessPosition start) {
        HashSet<ChessPosition> positions = new HashSet<>();

        for (ChessMove move : piece.pieceMoves(board, start)) {
            positions.add(move.getEndPosition());
        }

        return positions;
    }


    public boolean simulateMove(ChessMove move) {
        ChessGame.TeamColor color = gameBoard.getPiece(move.getStartPosition()).getTeamColor();
        ChessBoard tempBoard = new ChessBoard(gameBoard);

        movePiece(tempBoard, move);

        HashSet<ChessPosition> enemyMoves = getOpponentMoves(tempBoard, color);

        findKing(tempBoard);

        if (color == TeamColor.WHITE) {
            return !enemyMoves.contains(kingWhitePosition);
        } else {
            return !enemyMoves.contains(kingBlackPosition);
        }
    }

    private void checkPromotion(ChessMove move) {
        ChessPiece.PieceType promotionPieceType = (move.getPromotionPiece() != null) ? move.getPromotionPiece() : null;
        if(promotionPieceType != null) {
            ChessPiece promotionPiece = new ChessPiece(currentTurn, move.getPromotionPiece());
            gameBoard.addPiece(move.getEndPosition(), promotionPiece);
        }
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> possibleMoves = (HashSet<ChessMove>) gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);

        possibleMoves.removeIf(m -> !simulateMove(m));
        return possibleMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(gameBoard.getPiece(move.getStartPosition()).getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Not your turn");
        }
        HashSet<ChessMove> validMovesList = (HashSet<ChessMove>) validMoves(move.getStartPosition());
        if(validMovesList.contains(move)) {
            movePiece(gameBoard, move);
            checkPromotion(move);
            currentTurn = currentTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        }
        else {
            throw new InvalidMoveException("Move is not valid");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessGame.TeamColor oppositeTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        HashSet<ChessPosition> enemyMoves = getOpponentMoves(gameBoard, teamColor);

        return containsOpponentKing(enemyMoves, oppositeTeam, gameBoard);
    }

    private boolean containsOpponentKing(HashSet<ChessPosition> opponentMoves, ChessGame.TeamColor color, ChessBoard board) {
        findKing(board);

        if(color == TeamColor.BLACK && opponentMoves.contains(kingWhitePosition)) {
            return true;
        }
        else return color == TeamColor.WHITE && opponentMoves.contains(kingBlackPosition);
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if(gameBoard.getPiece(currentPosition) != null && gameBoard.getPiece(currentPosition).getTeamColor() == teamColor) {
                    HashSet<ChessMove> pieceMoves = new HashSet<>(validMoves(currentPosition));
                    if(!pieceMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {
            return false;
        }

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 8; j++) {
                ChessPosition currentPosition = new ChessPosition(i, j);
                if(gameBoard.getPiece(currentPosition) != null && gameBoard.getPiece(currentPosition).getTeamColor() == teamColor) {
                    HashSet<ChessMove> currentMoves = new HashSet<>(validMoves(currentPosition));
                    if(!currentMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
