import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;

class TicTacToeTest {

	@Test
	void AI_MinMax() {
		String[] testLine = {"O", "b", "b", "b", "b", "b", "b", "b", "b"};
		AI_MinMax aiminmax = new AI_MinMax(testLine);
		assertEquals("AI_MinMax", aiminmax.getClass().getName(), "Expected Name: AI_MinMax");
	}

	@Test
	void MinMax() {
		String[] testLine = {"O", "O", "O", "O", "O", "O", "O", "O", "b"};
		MinMax minmax = new MinMax(testLine);
		assertEquals("MinMax", minmax.getClass().getName(), "Expected Name: MinMax");
	}

	@Test
	void Node() {
		String[] testLine = {"O", "O", "O", "O", "O", "O", "O", "O", "b"};
		Node node = new Node(testLine, 0);
		assertEquals("Node", node.getClass().getName(), "Expected Name: Node");
	}

	@Test
	void BoardSpot8() {
		String[] testLine = {"O", "O", "O", "O", "O", "O", "O", "O", "b"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 8, "Min Max AI placement on Board should be 8");

	}

	@Test
	void BoardSpot7() {
		String[] testLine = {"O", "O", "O", "O", "O", "O", "O", "b", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 7, "Min Max AI placement on Board should be 7");

	}

	@Test
	void BoardSpot6() {
		String[] testLine = {"O", "O", "O", "O", "O", "O", "b", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 6, "Min Max AI placement on Board should be 6");

	}

	@Test
	void BoardSpot5() {
		String[] testLine = {"O", "O", "O", "O", "O", "b", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 5, "Min Max AI placement on Board should be 5");

	}

	@Test
	void BoardSpot4() {
		String[] testLine = {"O", "O", "O", "O", "b", "O", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 4, "Min Max AI placement on Board should be 4");

	}
	@Test
	void BoardSpot3() {
		String[] testLine = {"O", "O", "O", "b", "O", "O", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 3, "Min Max AI placement on Board should be 3");

	}
	@Test
	void BoardSpot2() {
		String[] testLine = {"O", "O", "b", "O", "O", "O", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 2, "Min Max AI placement on Board should be 2");

	}
	@Test
	void BoardSpot1() {
		String[] testLine = {"O", "b", "O", "O", "O", "O", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 1, "Min Max AI placement on Board should be 1");

	}

	@Test
	void BoardSpot0() {
		String[] testLine = {"b", "O", "O", "O", "O", "O", "O", "O", "O"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 0, "Min Max AI placement on Board should be 0");

	}

	@Test
	void BestPossibleMove() {
		String[] testLine = {"O", "O", "b", "b", "b", "b", "b", "b", "b"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(0).getMovedTo()-1;
		assertEquals(move, 2, "Min Max AI placement on Board should be 2");
	}

	@Test
	void BestPossibleMove2() {
		String[] testLine = {"b", "b", "b", "O", "O", "b", "b", "b", "b"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(3).getMovedTo()-1;
		assertEquals(move, 5, "Min Max AI placement on Board should be 2");
	}

	@Test
	void BestPossibleMove3() {
		String[] testLine = {"b", "b", "b", "b", "b", "b", "O", "O", "b"};
		AI_MinMax possibleMoves = new AI_MinMax(testLine);
		ArrayList<Node> moveList = possibleMoves.getBestMoves();
		int move = moveList.get(6).getMovedTo()-1;
		assertEquals(move, 8, "Min Max AI placement on Board should be 2");
	}

}
