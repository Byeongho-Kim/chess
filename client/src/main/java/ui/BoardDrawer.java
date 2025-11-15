package ui;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    private static final String LIGHT_SQUARE = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE = SET_BG_COLOR_DARK_GREY;
    private static final String BORDER = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK;

    public String drawWhitePerspective() {
        StringBuilder sb = new StringBuilder();
        sb.append(drawBorder(true));

        for (int row = 8; row >= 1; row--) {
            sb.append(drawRow(row, true));
        }

        sb.append(drawBorder(true));
        return sb.toString();
    }

    public String drawBlackPerspective() {
        StringBuilder sb = new StringBuilder();
        sb.append(drawBorder(false));

        for (int row = 1; row <= 8; row++) {
            sb.append(drawRow(row, false));
        }

        sb.append(drawBorder(false));
        return sb.toString();
    }

    private String drawBorder(boolean isWhitePerspective) {
        StringBuilder sb = new StringBuilder();
        sb.append(BORDER).append("   ");

        if (isWhitePerspective) {
            for (char c = 'a'; c <= 'h'; c++) {
                sb.append(" ").append(c).append(" ");
            }
        }
        else {
            for (char c = 'h'; c >= 'a'; c--) {
                sb.append(" ").append(c).append(" ");
            }
        }

        sb.append("   ").append(RESET_BG_COLOR).append("\n");
        return sb.toString();
    }

    private String drawRow(int row, boolean isWhitePerspective) {
        StringBuilder sb = new StringBuilder();
        sb.append(BORDER).append(" ").append(row).append(" ");

        for (int col = 1; col <= 8; col++) {
            int actualCol = isWhitePerspective ? col : (9 - col);

            int boardRow = row;
            int boardCol = isWhitePerspective ? col : (9 - col);

            boolean isLightSquare = (boardRow + boardCol) % 2 == 1;

            sb.append(isLightSquare ? LIGHT_SQUARE : DARK_SQUARE);
            sb.append(" ").append(getPiece(row, actualCol)).append(" ");
        }

        sb.append(BORDER).append(" ").append(row).append(" ");
        sb.append(RESET_BG_COLOR).append("\n");
        return sb.toString();
    }

    private String getPiece(int row, int col) {
        if (row == 2) {
            return SET_TEXT_COLOR_RED + "P";
        }
        if (row == 7) {
            return SET_TEXT_COLOR_BLUE + "P";
        }

        if (row == 1) {
            return SET_TEXT_COLOR_RED + switch (col) {
                case 1, 8 -> "R";
                case 2, 7 -> "N";
                case 3, 6 -> "B";
                case 4 -> "Q";
                case 5 -> "K";
                default -> " ";
            };
        }

        if (row == 8) {
            return SET_TEXT_COLOR_BLUE + switch (col) {
                case 1, 8 -> "R";
                case 2, 7 -> "N";
                case 3, 6 -> "B";
                case 4 -> "Q";
                case 5 -> "K";
                default -> " ";
            };
        }
        return " ";
    }
}
