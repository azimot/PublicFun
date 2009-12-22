import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame {

	private TTTSquare[][] squares;

	private X_O turn;

	public MainFrame() {
		super("JTicTacToe");
		turn = X_O.X;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(3, 3));
		squares = new TTTSquare[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				squares[i][j] = new TTTSquare(this);
				add(squares[i][j]);
			}
		}
		pack();
		setVisible(true);
	}

	public X_O getTurn() {
		return turn;
	}

	public void changeTurn() {
		X_O winner = checkForWin();
		if (winner == X_O.NONE) {
			turn = turn == X_O.X ? X_O.O : X_O.X;
			return;
		} 
		JOptionPane.showMessageDialog(this, "Yay, good job " + winner.toString() + ", you won!", "Winner!", JOptionPane.INFORMATION_MESSAGE);
		resetBoard();
	}

	public void resetBoard(){
		for(int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				squares[i][j].setOwner(X_O.NONE);
				squares[i][j].repaint();
			}
		}
	}
	
	public X_O checkForWin() {
		// Horizontal
		if (squares[0][0].getOwner() == squares[0][1].getOwner()
				&& squares[0][0].getOwner() == squares[0][2].getOwner())
			return squares[0][0].getOwner();
		if (squares[1][0].getOwner() == squares[1][1].getOwner()
				&& squares[1][0].getOwner() == squares[1][2].getOwner())
			return squares[1][0].getOwner();
		if (squares[2][0].getOwner() == squares[2][1].getOwner()
				&& squares[2][0].getOwner() == squares[2][2].getOwner())
			return squares[2][0].getOwner();

		// Vertical
		if (squares[0][0].getOwner() == squares[1][0].getOwner()
				&& squares[0][0].getOwner() == squares[2][0].getOwner())
			return squares[0][0].getOwner();
		if (squares[0][1].getOwner() == squares[1][1].getOwner()
				&& squares[0][1].getOwner() == squares[2][1].getOwner())
			return squares[0][1].getOwner();
		if (squares[0][2].getOwner() == squares[1][2].getOwner()
				&& squares[0][2].getOwner() == squares[2][2].getOwner())
			return squares[0][2].getOwner();

		// Diagonal
		if (squares[0][0].getOwner() == squares[1][1].getOwner()
				&& squares[1][1].getOwner() == squares[2][2].getOwner())
			return squares[1][1].getOwner();
		if (squares[0][2].getOwner() == squares[1][1].getOwner()
				&& squares[1][1].getOwner() == squares[2][0].getOwner())
			return squares[1][1].getOwner();

		// No winner
		return X_O.NONE;
	}

}
