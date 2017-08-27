package org.github.pulsar929.virtualKeyboard;

import java.util.ArrayList;
import java.util.List;

public class VirtualKeyboard {

	public static class PathResult {
		public PathResult(int distance, String path) {
			this.distance = distance;
			this.path = path;
		}

		public int distance;
		public String path;

		@Override
		public String toString() {
			return "" + distance + ":" + path;
		}
	}

	public PathResult getPath(String alphabet, int rowLength, char startingFocus, String word) {
		List<Action> actions = new ArrayList<Action>();

		/*
		 * Starting char and its position(x,y)
		 */
		char startChar = startingFocus;
		int startPosX = alphabet.indexOf(startChar) % rowLength;
		int startPosY = alphabet.indexOf(startChar) / rowLength;

		/*
		 * Iterate each char in word. Find a path from startChar and the next
		 * letter in word (endChar).
		 */
		for (char endChar : word.toCharArray()) {
			if (startChar != endChar) {
				/*
				 * Need to move to endChar
				 */

				/*
				 * The position(x,y) of endChar
				 */
				int endPosX = alphabet.indexOf(endChar) % rowLength;
				int endPosY = alphabet.indexOf(endChar) / rowLength;

				/*
				 * Since the keyboard may not be a perfect rectangle, calculate
				 * the maximum width and height of the keyboard at the position
				 * of startChar and endChar. This information is used to
				 * determine 1) whether the first move is horizontally
				 * (preferred) or vertically and 2) to find optimal direction of
				 * move (wrap-around or not).
				 */
				int maxWidthStartPos = maxLength(alphabet.length(), rowLength, false, startPosY);
				int maxHeightStartPos = maxLength(alphabet.length(), rowLength, true, startPosX);
				int maxWidthEndPos = maxLength(alphabet.length(), rowLength, false, endPosY);
				int maxHeightEndPos = maxLength(alphabet.length(), rowLength, true, endPosX);
				if (endPosX < maxWidthStartPos) {
					/*
					 * Move horizontally first since there's enough width in
					 * current row (startPos) to get to the final column.
					 */

					// Move Horizontal
					actions.add(getLinearPath(maxWidthStartPos, startPosX, endPosX, 'l', 'r'));
					// Move Vertical
					actions.add(getLinearPath(maxHeightEndPos, startPosY, endPosY, 'u', 'd'));
				} else {
					// Move vertically first otherwise.

					// Move Vertical
					actions.add(getLinearPath(maxHeightStartPos, startPosY, endPosY, 'u', 'd'));
					// Move Horizontal
					actions.add(getLinearPath(maxWidthEndPos, startPosX, endPosX, 'l', 'r'));
				}

				/*
				 * For the next iteration, endPos becomes new startPos.
				 */
				startPosX = endPosX;
				startPosY = endPosY;
			}
			// Press
			actions.add(new Action(1, 'p'));

			/*
			 * For the next iteration, endChar becomes new startChar.
			 */
			startChar = endChar;
		}

		/*
		 * Summarize path and calculate the distance. Any action with zero count
		 * is ignored.
		 */
		String path = "";
		int distance = 0;
		for (Action action : actions) {
			if (action.count > 0) {
				for (int i = 0; i < action.count; i++) {
					path += action.type;
				}
				if (action.type != 'p') {
					distance += action.count;
				}
			}
		}
		return new PathResult(distance, path);
	}

	/**
	 * An Action.
	 */
	public static class Action {
		public Action(int count, char type) {
			this.count = count;
			this.type = type;
		}

		/**
		 * The number of actions taken. 0 for no action.
		 */
		public int count;

		/**
		 * One of up, down, left, right and press.
		 */
		public char type;

		@Override
		public String toString() {
			return "" + type + count;
		}
	}

	/**
	 * Calculate a move in a linear path, horizontal or vertical.
	 * 
	 * @param length
	 *            The maximum length: width if horizontal or height if vertical.
	 * @param startPos
	 *            The position to start. If horizontal, startPosX, if vertical,
	 *            startPosY.
	 * @param endPos
	 *            The position to end with. If horizontal, endPosX, if vertical,
	 *            endPosY.
	 * @param prev
	 *            The letter to indicate the negative movement. If horizontal,
	 *            left, if vertical, up
	 * @param next
	 *            The letter to indicate the positive movement. If horizontal,
	 *            right, if vertical, down.
	 * @return
	 */
	public static Action getLinearPath(int length, int startPos, int endPos, char prev, char next) {
		/*
		 * Since wrap-around is allowed, you never need to move more than
		 * length/2. If needed more than that, wrap-around is shorter.
		 */
		int distance = Math.abs(endPos - startPos);
		if (distance > length / 2) {
			// wrap around
			if (endPos < startPos) {
				// wrap right(positive)
				return new Action(getWrapAroundDistance(length, startPos, endPos, false), next);
			} else {
				// wrap left(negative)
				return new Action(getWrapAroundDistance(length, startPos, endPos, true), prev);
			}
		} else {
			// no wrap
			if (endPos < startPos) {
				// move left(negative)
				return new Action(distance, prev);
			} else {
				// move right(positive)
				return new Action(distance, next);
			}
		}
	}

	/**
	 * Get the distance for a linear wrap-around move.
	 * 
	 * @param width
	 * @param startPos
	 * @param endPos
	 * @param isLeft
	 *            Whether the move is negative (isLeft=true) or
	 *            positive(isLeft=false).
	 * @return
	 */
	public static int getWrapAroundDistance(int width, int startPos, int endPos, boolean isLeft) {
		if (isLeft)
			return startPos + (width - endPos);
		else
			return (width - startPos) + endPos;
	}

	/**
	 * Get the maximum height or width based on given position.
	 * 
	 * @param runLength
	 *            Total length of alphabet.
	 * @param rowLength
	 * @param isHeight
	 *            Whether to calculate height or width of keyboard at given
	 *            position.
	 * @param rowOrColumn
	 *            Row index if isHeight = false or column index if isHeight =
	 *            true.
	 * @return
	 */
	public int maxLength(int runLength, int rowLength, boolean isHeight, int rowOrColumn) {
		if (runLength % rowLength == 0) {
			// A rectangle
			return isHeight ? runLength / rowLength : rowLength;
		} else {
			int maxRow = runLength / rowLength;
			int lastRowLength = runLength % rowLength;
			if (isHeight) {
				if (rowOrColumn < lastRowLength) {
					return maxRow + 1;
				} else {
					return maxRow;
				}
			} else {
				if (rowOrColumn < maxRow) {
					return rowLength;
				} else {
					return lastRowLength;
				}
			}
		}
	}
}
