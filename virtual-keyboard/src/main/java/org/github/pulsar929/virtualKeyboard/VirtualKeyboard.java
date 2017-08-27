package org.github.pulsar929.virtualKeyboard;

import java.util.ArrayList;
import java.util.List;

public class VirtualKeyboard {

	public static void main(String[] args) {
		System.out.println(new VirtualKeyboard().getPath("QWERTYUIBPAS", 5, 'B', "BAR"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'Y', "TILT"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'R', "LLLL"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'R', "O"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'R', "Y"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'R', "L"));
		System.out.println(new VirtualKeyboard().getPath("RTYASDEUIOL", 3, 'R', "I"));
	}

	public String getPath(String alphabet, int rowLength, char startingFocus, String word) {

		int width = rowLength;
		int height = (int) Math.ceil((double) alphabet.length() / rowLength);

		List<Action> actions = new ArrayList<Action>();

		char startChar = startingFocus;
		int startPosX = alphabet.indexOf(startChar) % width;
		int startPosY = alphabet.indexOf(startChar) / width;
		for (char endChar : word.toCharArray()) {
			if (startChar != endChar) {
				int endPosX = alphabet.indexOf(endChar) % width;
				int endPosY = alphabet.indexOf(endChar) / width;
				
				int maxWidthStartPos = maxLength(alphabet.length(), rowLength, false, startPosY);
				int maxHeightStartPos = maxLength(alphabet.length(), rowLength, true, startPosX);
				int maxWidthEndPos  = maxLength(alphabet.length(), rowLength, false, endPosY);
				int maxHeightEndPos = maxLength(alphabet.length(), rowLength, true, endPosX);
				if( endPosX < maxWidthStartPos ) {
					// Move horizontally first since there's enough with in current row to get to
					// the final column.

					// Move Horizontal
					actions.add(getLinearPath(maxWidthStartPos, startPosX, endPosX, 'l','r'));
					// Move Vertical
					actions.add(getLinearPath(maxHeightEndPos, startPosY, endPosY, 'u','d'));
				} else {
					// Move vertically first otherwise.
					
					// Move Vertical
					actions.add(getLinearPath(maxHeightStartPos, startPosY, endPosY, 'u','d'));
					// Move Horizontal
					actions.add(getLinearPath(maxWidthEndPos, startPosX, endPosX, 'l','r'));
				}
				startPosX = endPosX;
				startPosY = endPosY;
			}
			// Press
			actions.add(new Action(1, 'p'));
			startChar = endChar;
		}

		String path = "";
		int distance = 0;
		for (Action action : actions) {
			if (action.count > 0) {
				for (int i = 0; i < action.count; i++) {
					path += action.type;
				}
				if( action.type != 'p' ) {
					distance += action.count; 
				}
			}
		}
		return "" + distance + ":" + path;
	}

	public static class Action {
		public Action(int count, char type) {
			this.count = count;
			this.type = type;
		}

		public int count;
		public char type;

		@Override
		public String toString() {
			return "" + type + count;
		}
	}

	public static Action getLinearPath(int length, int startPos, int endPos, char prev, char next) {
		int distance = Math.abs(endPos - startPos);
		if (distance > length / 2) {
			// wrap around
			if (endPos < startPos) {
				// wrap right
				return new Action(getWrapAroundDistance(length, startPos, endPos, false), next);
			} else {
				// wrap left
				return new Action(getWrapAroundDistance(length, startPos, endPos, true), prev);
			}
		} else {
			// no wrap
			if (endPos < startPos) {
				// move left
				return new Action(distance, prev);
			} else {
				// move right
				return new Action(distance, next);
			}
		}
	}

	public static int getWrapAroundDistance(int width, int startPos, int endPos, boolean isLeft) {
		if (isLeft)
			return startPos + (width - endPos);
		else
			return (width - startPos) + endPos;
	}

	public int maxLength( int runLength, int rowLength, boolean isHeight, int rowOrColumn ){
		if( runLength % rowLength == 0 ) {
			// A rectangle
			return isHeight? runLength / rowLength : rowLength;
		}
		else {
			int maxRow = runLength / rowLength;
			int lastRowLength = runLength % rowLength;
			if( isHeight) {
				if( rowOrColumn < lastRowLength ) {
					return maxRow + 1;
				} 
				else {
					return maxRow;
				}
			}
			else {
				if( rowOrColumn < maxRow ) {
					return rowLength;
				} else {
					return lastRowLength;
				}
			}
		}		
	}
}
