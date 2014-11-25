import scala.util.control.Breaks._
import scala.util.Random

/**
 * This is Tic-Tac-Toe game pitting the computer against itself. Player 'X' always goes first with
 * a randomly selected move. The players 'O' and 'X' then alternate moves using alpha-beta pruning
 * to select responding move. Statistics consistently show that from running the simulation
 * the player making the first move wins more games than the other player with both
 * players using the same counter-move strategy.
 *
 * <p>
 * Running a simulation of 100 games on a dual-core, 2.7 GHz Windows 7 machine produces
 * results similar to the following:
 *
 * <pre>
 * 100 games played:
 * 	Number of times Player X won: 30
 * 	Number of times Player O won: 26
 * 	Number of ties:               44
 *
 * Total run time: 0.292 second(s)
 * </pre>
 *
 * @author Cary Scofield carys689 <at> gmail <dot> com
 * @version 2.11.2
 *
 * Note: To give credit where credit is due, this program uses Java code (for alpha-beta pruning logic)
 * transcribed/adapted to Scala from https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 */
object TicTacToe {

  type Grid   = Array[Array[Char]]
  type RowCol = (Int,Int)
  val GRID : Grid = Array.ofDim[Char](3,3)

  val X : Char      = 'X'
  val O : Char      = 'O'
  val TIE : Char    = '-'
  val EMPTY : Char  = ' '

  val mySeed : Char  = X
  val oppSeed : Char = O

  /**
   * Initialize the grid to all empty cells.
   * @return
   */
  def initGrid(): Unit = {
    for( r <- 0 to 2 ) for( c <- 0 to 2 ) GRID(r)(c) = EMPTY
  }

  /**
   * Make a deep copy of the grid.
   * @param grid
   * @return
   */
  def deepCopyGrid( grid : Grid) : Grid = {
    val newGrid : Grid = Array.ofDim[Char](3,3)
    for( r <- 0 to 2) for( c <- 0 to 2 ) newGrid(r)(c) = grid(r)(c)
    newGrid
  }

  /**
   * Determine if a player is a winner by checking all rows, columns, and diagonals.
   * @param grid
   * @param player
   * @return
   */
  def isWinner( grid: Grid, player : Char ) : Boolean = {
    def checkRows() : Boolean = {
      for( r <- 0 to 2 ) if( grid(r)(0) == player && grid(r)(1) == player && grid(r)(2) == player ) return true
      false
    }
    def checkColumns() : Boolean = {
      for( c <- 0 to 2 ) if( grid(0)(c) == player && grid(1)(c) == player && grid(2)(c) == player ) return true
      false
    }
    def checkDiagonals() : Boolean = {
      if( grid(0)(0) == player && grid(1)(1) == player && grid(2)(2) == player ) return true
      if( grid(2)(0) == player && grid(1)(1) == player && grid(0)(2) == player ) return true
      false
    }
    return checkRows || checkColumns || checkDiagonals
  }

  /**
   * Make a picture of the Tic-Tac-Toe grid for printing.
   * @param grid
   * @return
   */
  def showGrid( grid: Grid): String = {
    val buf : StringBuilder = new StringBuilder(64)
    for( r <- 0 to 2 ) {
      for( c <- 0 to 2 ) {
        buf.append( grid(r)(c) )
        if( c < 2 ) buf.append( " | " )
      }
      buf.append( '\n' )
      if( r < 2 ) buf.append( "---------\n" )
    }
    buf.append( '\n' )
    buf.toString
  }

  /**
   * Make a list of cells as a String. Used for debugging.
   * @param ls The list of available cells.
   * @return String representation of available cell list.
   */
  def showAvailableCells( ls: List[ RowCol ] ) : String = {
    val buf: StringBuilder = new StringBuilder()
    def buildString( buf : StringBuilder, ls : List[ (Int,Int) ] ) : Unit = {
      ls match {
        case h :: tail =>
          buf.append( "(" + h._1 + "," + h._2 + ")," )
          buildString( buf, tail )
        case Nil => Nil
      }
    }
    buildString( buf, ls )
    buf.toString
  }

  def isEmptyCell( grid: Grid, rc: RowCol ) : Boolean = grid(rc._1)(rc._2) == EMPTY
  def isEmptyCell( grid: Grid, row: Int, col: Int ) : Boolean = grid(row)(col) == EMPTY

  /**
   * Produce a list of available cells for making the next move to.
   * @param grid The Tic-Tac-Toe grid
   * @return
   */
  def availableCells(grid: Grid) : List[ (Int,Int) ] = {
    var cells : List[ (Int,Int) ] = Nil.asInstanceOf[List[(Int,Int)]]
    if (isWinner(grid,mySeed) || isWinner(grid,oppSeed) ) return cells
    for( r <- 0 to 2 ) for( c <- 0 to 2 ) {
      if (isEmptyCell(grid, r, c)) cells = cells :+ (r, c)
    }
    cells
  }

  /**
   * Mark a cell in the grid
   * @param grid The Tic-Tac-Toe grid
   * @param player Either Player 'X' or Player 'O'
   * @param row Which row to mark
   * @param col Whick column to mark
   * @return Content of cell
   */
  def markCell( grid: Grid, player : Char, row : Int, col : Int ) : Char = {
    if( isEmptyCell(grid, row, col) ) grid(row)(col) = player
    grid(row)(col)
  }

  def markCell( grid: Grid, player : Char, rowcol: RowCol ) : Char = {
    markCell( grid, player, rowcol._1, rowcol._2 )
  }

    /** The heuristic evaluation function for the current board
       @return +100, +10, +1 for EACH 3-, 2-, 1-in-a-line for computer.
               -100, -10, -1 for EACH 3-, 2-, 1-in-a-line for opponent.
               0 otherwise
      Method adapted from: https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
    */
  def evaluate( grid: Grid) : Int = {
    var score : Int = 0
    // Evaluate score for each of the 8 lines (3 rows, 3 columns, 2 diagonals)
    score += evaluateLine(grid, 0, 0, 0, 1, 0, 2)  // row 0
    score += evaluateLine(grid, 1, 0, 1, 1, 1, 2)  // row 1
    score += evaluateLine(grid, 2, 0, 2, 1, 2, 2)  // row 2
    score += evaluateLine(grid, 0, 0, 1, 0, 2, 0)  // col 0
    score += evaluateLine(grid, 0, 1, 1, 1, 2, 1)  // col 1
    score += evaluateLine(grid, 0, 2, 1, 2, 2, 2)  // col 2
    score += evaluateLine(grid, 0, 0, 1, 1, 2, 2)  // diagonal
    score += evaluateLine(grid, 0, 2, 1, 1, 2, 0)  // alternate diagonal
    score
  }
  /** The heuristic evaluation function for the given line of 3 cells
       @return +100, +10, +1 for 3-, 2-, 1-in-a-line for computer.
               -100, -10, -1 for 3-, 2-, 1-in-a-line for opponent.
               0 otherwise
      Method adapted from: https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
    */
 def evaluateLine(grid : Grid,
                  row1 : Int, col1 : Int,
                  row2 : Int, col2 : Int,
                  row3 : Int, col3 : Int ) : Int  = {
    var score : Int = 0

    // First cell
    if (grid(row1)(col1) == mySeed) {
      score = 1
    } else if (grid(row1)(col1) == oppSeed) {
      score = -1
    }

    // Second cell
    if (grid(row2)(col2) == mySeed) {
      if (score == 1) {   // cell1 is mySeed
        score = 10
      } else if (score == -1) {  // cell1 is oppSeed
        return 0
      } else {  // cell1 is empty
        score = 1
      }
    } else if (grid(row2)(col2) == oppSeed) {
      if (score == -1) { // cell1 is oppSeed
        score = -10
      } else if (score == 1) { // cell1 is mySeed
        return 0
      } else {  // cell1 is empty
        score = -1
      }
    }

    // Third cell
    if (grid(row3)(col3) == mySeed) {
      if (score > 0) {  // cell1 and/or cell2 is mySeed
        score *= 10
      } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
        return 0
      } else {  // cell1 and cell2 are empty
        score = 1
      }
    } else if (grid(row3)(col3) == oppSeed) {
      if (score < 0) {  // cell1 and/or cell2 is oppSeed
        score *= 10
      } else if (score > 1) {  // cell1 and/or cell2 is mySeed
        return 0
      } else {  // cell1 and cell2 are empty
        score = -1
      }
    }
    score
  }

  /**
   * Select a random cell. To be used only at the start of each game. Not valid after this move has been made.
   * @return row and col as a Tuple2
   */
  def randomMove() : RowCol = {
    var rand : Random = new Random( System.currentTimeMillis() )
    var row = rand.nextInt(3)
    var col = rand.nextInt(3)
    (row,col)
  }

  def move( grid: Grid, player : Char) : RowCol = {
    val move_ : (Int,Int,Int) = minimax(grid,2, player, Integer.MIN_VALUE, Integer.MAX_VALUE)
    // depth, max-turn, alpha, beta
    showGrid( grid )
    (move_._2, move_._3)   // row, col
  }

  /** Minimax (recursive) at level of depth for maximizing or minimizing player
       with alpha-beta cut-off.
       @return score, row, and col as a Tuple3
      Method adapted from: https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
    */
  def minimax(grid: Grid, depth : Int, player : Char, alpha : Int, beta: Int) : (Int,Int,Int) = {
    // Generate possible next moves in a list of int[2] of {row, col}.
    val nextMoves : List[(Int,Int)] = availableCells( grid )
    // mySeed is maximizing while oppSeed is minimizing
    var score : Int = 0
    var bestRow : Int = -1
    var bestCol : Int = -1
    var alpha_ = alpha
    var beta_  = beta
    val newGrid = deepCopyGrid( grid )

    if (nextMoves.length == 0 || depth == 0) {
      // Gameover or depth reached, evaluate score
      score = evaluate( newGrid )
      return (score, bestRow, bestCol)
    } else {
      val iter: Iterator[(Int, Int)] = nextMoves.iterator
      breakable {
        while (iter.hasNext) {
          val (row, col) = iter.next
          // try this move for the current "player"
          markCell(newGrid, player, row, col)
          if (player == mySeed) {
            // mySeed (computer) is maximizing player
            score = minimax(newGrid, depth - 1, oppSeed, alpha_, beta_)._1
            if (score > alpha_) {
              alpha_ = score
              bestRow = row
              bestCol = col
            }
          } else {
            // oppSeed is minimizing player
            score = minimax(newGrid, depth - 1, mySeed, alpha_, beta_)._1
            if (score < beta_) {
              beta_ = score
              bestRow = row
              bestCol = col
            }
          } // end if

          // undo move
          markCell(newGrid, EMPTY, row, col)

          // cut-off
          if (alpha_ >= beta_) break

        } // end while
      }

      //println( showGrid( newGrid ) )

      if( player == mySeed ) (alpha, bestRow, bestCol )
      else (beta, bestRow, bestCol )
    }
  }

  def isGameOver( rowcol : RowCol ) : Boolean = rowcol._1 == -1 && rowcol._2 == -1;

  /**
   * Play one game. Return the name of the winner if it's 'X' or 'O' otherwise return TIE if there was a tie.
   * @return
   */
  def playGame() : Char = {
    initGrid()
    val initialMove : RowCol = randomMove()
    markCell( GRID, X, initialMove )
    println( "Player " + X + " marks cell (" + initialMove._1 + "," + initialMove._2 + ")" )
    println( showGrid(GRID))
    breakable {
      for( i <- 1 to 50) {
        val player : Char = if( i.%(2) == 0 ) X else O
        val rowcol: RowCol = move(GRID, player)
        if (isGameOver( rowcol )) break
        println( "Player " + player + " marks cell (" + rowcol._1 + "," + rowcol._2 + ")" )
        markCell(GRID, player, rowcol)
        println(showGrid(GRID) )
      }
    }

    if( isWinner( GRID, X ) ) {
      println( "Player X won the game" )
      return X
    }
    else if( isWinner( GRID, O ) ) {
      println( "Player O won the game" )
      return O
    }
    else {
      println( "Cat's got it" )
      return TIE
    }
  }

  /**
   * Pitting the computer against itself. 'X' always starts first choosing a random
   * cell in the 3x3 Tic-Tac-Toe grid. The program then alternates between player 'O'
   * and player 'X' until no more moves can be made. After the initial move by 'X',
   * each player uses alpha-beta pruning to decide what its next move is.
   * @param args
   * @return
   */
  def main(args: Array[String]): Unit = {
    var Xtally : Int = 0
    var Otally : Int = 0
    var ties   : Int = 0
    val GAMES  : Int = 100 // Should be a command-line option

    val startTime = System.currentTimeMillis()
    for( i <- 1 to GAMES ) {
      val winner = playGame()
      if( winner == X ) Xtally += 1
      else if( winner == O ) Otally += 1
      else ties += 1
    }
    val endTime = System.currentTimeMillis()

    println( "\n" + GAMES + " games played:" )
    println( "\tNumber of times Player X won: " + Xtally )
    println( "\tNumber of times Player O won: " + Otally )
    println( "\tNumber of ties:               " + ties )

    println( "\nTotal run time: " + ( endTime - startTime ) / 1000.0D + " second(s)" )
  }
}

