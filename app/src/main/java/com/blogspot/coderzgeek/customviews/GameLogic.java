package com.blogspot.coderzgeek.customviews;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {
    public enum moveType {
        O(-1),
        EMPTY(0),
        X(1);
        private int number;

        moveType(int i) {
            this.number = i;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    ;

    public enum levelType {
        EASY,
        MEDIUM,
        HARD
    }

    ;

    private int[][] a = new int[32][];        // winning cases of each cell
    private int[] gameMatrix = new int[56];   // initially zeros
    private int[] nextState = new int[56];
    public moveType[] visited = new moveType[32];
    private ArrayList<ArrayList<Integer>> spirals = new ArrayList<>(32);
    private ArrayList<ArrayList<Integer>> rings = new ArrayList<>(32);
    private ArrayList<ArrayList<Integer>> lines = new ArrayList<>(32);

    public GameLogic() {//constructor
        initialize();
    }

    public boolean isPlayed(int cell) {
        return visited[cell] != moveType.EMPTY;
    }

    private void initialize() {
        for (int i = 0; i < 32; ++i) {
            visited[i] = moveType.EMPTY;
            a[i] = new int[56];
            ArrayList<Integer> ring = new ArrayList<>();
            ArrayList<Integer> line = new ArrayList<>();
            ArrayList<Integer> spiral = new ArrayList<>();
            for (int j = 0; j < 4; ++j) {
                int v = i % 8;
                v = (v - j + 8) % 8;
                a[i][i / 8 * 8 + v] = 1;
                ring.add(i / 8 * 8 + v);
            }
            a[i][32 + i % 8] = 1;
            line.add(32 + i % 8);
            a[i][40 + (i + i / 8) % 8] = 1;
            spiral.add(40 + (i + i / 8) % 8);
            final int i1 = 48 + (i - i / 8 + 8) % 8;
            a[i][i1] = 1;

            spiral.add(i1);
            rings.add(ring);
            lines.add(line);
            spirals.add(spiral);
        }
    }

    public boolean humanMove(moveType type, int cell) {
        if (visited[cell] != moveType.EMPTY)
            return false;
        visited[cell] = type;
        for (int i = 0; i < 56; i++) {
            gameMatrix[i] += (int) type.getNumber() * a[cell][i];
        }
        return true;
    }

    private int evaluate(moveType player, int[] gameMatrix) {
        int opp = (-1 * (int) player.getNumber());
        for (int i = 0; i < 56; i++) {
            if (gameMatrix[i] == 4 * (int) player.getNumber())
                return 100;
            else if (gameMatrix[i] == 4 * (int) opp)
                return -100;
        }
        return 0;
    }

    private int minimax(moveType[] visited, int[] gameMatrix, moveType player, int depth, boolean isMax, int alpha, int beta) {
        boolean finished = true;
        for (int i = 0; i < 32; i++) {
            if (visited[i] == moveType.EMPTY) {
                finished = false;
                break;
            }
        }
        if (finished) {
            int score = evaluate(player, gameMatrix);
            if (score == 100)
                score -= depth;
            else if (score == -100)
                score += depth;
            return score;
        }
        int opp = (-1 * (int) player.getNumber());

        int best = (int) -1e9;
        if (!isMax)
            best = (int) 1e9;
        for (int cell = 0; cell < 32; cell++) {
            if (visited[cell] == moveType.EMPTY) {
                boolean exit = false;
                visited[cell] = player;
                if (!isMax)
                    visited[cell] = moveType.values()[opp];
                for (int i = 0; i < 56; i++) {
                    if (isMax)
                        gameMatrix[i] += (int) player.getNumber() * a[cell][i];
                    else
                        gameMatrix[i] += (int) opp * a[cell][i];
                }
                if (isMax) {
                    int value = minimax(visited, gameMatrix, player, depth + 1, false, alpha, beta);
                    best = Math.max(best, value);
                    alpha = Math.max(alpha, best);
                    if (beta <= alpha)
                        exit = true;
                } else {
                    int value = minimax(visited, gameMatrix, player, depth + 1, true, alpha, beta);
                    best = Math.min(best, value);
                    beta = Math.min(beta, best);
                    if (beta <= alpha)
                        exit = true;
                }
                //undo the move
                for (int i = 0; i < 56; i++) {
                    if (isMax)
                        gameMatrix[i] -= (int) player.getNumber() * a[cell][i];
                    else
                        gameMatrix[i] -= (int) opp * a[cell][i];
                }
                visited[cell] = moveType.EMPTY;
                if (exit)
                    break;
            }
        }
        return best;
    }

    private int findBestMove(moveType player) {
        int bestCell = -1;
        int bestVal = (int) -1e9;
        for (int cell = 0; cell < 32; cell++) {
            if (visited[cell] == moveType.EMPTY) {
                visited[cell] = player;
                for (int i = 0; i < 56; i++) {
                    nextState[i] = gameMatrix[i] + (int) player.getNumber() * a[cell][i];
                }
                int moveVal = minimax(visited, nextState, player, 0, false, (int) -1e9, (int) 1e9);
                //undo the move
                visited[cell] = moveType.EMPTY;
                System.arraycopy(gameMatrix, 0, nextState, 0, 56);
                if (moveVal > bestVal) {
                    bestVal = moveVal;
                    bestCell = cell;
                }
            }
        }
        return bestCell;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int mediumMove(moveType type) {
        ArrayList<Integer> optimalPlays = new ArrayList<>();
        int mx = (int) -1e9;  //maximum score
        for (int move = 0; move < 32; move++) {
            if (visited[move] != moveType.EMPTY)
                continue;
            for (int i = 0; i < 56; i++) {
                nextState[i] = gameMatrix[i] + (int) type.getNumber() * a[move][i];
            }
            int sScore = 0; //state score
            moveType oppType = (type == moveType.X) ? moveType.O : moveType.X;
            for (int i = 0; i < 56; i++) {
                if (nextState[i] == (int) type.getNumber() * 4)
                    sScore += (int) 1e7;
                else if (nextState[i] == (int) oppType.getNumber() * 3)
                    sScore += (int) -1e5;
                else if (nextState[i] == (int) type.getNumber() * 3)
                    sScore += (int) 1e3;
                else if (nextState[i] == (int) type.getNumber() * 2)
                    sScore += 1;
                else if (nextState[i] == (int) oppType.getNumber() * 2)
                    sScore += -1;
            }

            moveType spiralTrap = checkSpiralTrap();
            if (spiralTrap == type)
                sScore += 10;
            else if (spiralTrap == oppType)
                sScore += (int) -1e3;
            moveType othertrapopponent = checkOtherTraps(move, gameMatrix);
            moveType othertrap = checkOtherTraps(move, nextState);
            if (othertrap == type)
                sScore += 10;
            else if (othertrapopponent == oppType)
                sScore += (int) 3.6e3;
            if (sScore > mx) {
                mx = sScore;
                optimalPlays.clear();
                optimalPlays.add(move);
            } else if (sScore == mx)
                optimalPlays.add(move);
        }
        int randIdx = randomInRange(optimalPlays.size());
        if (randIdx < optimalPlays.size() - 1)
            return optimalPlays.get(randIdx);
        else
            return optimalPlays.get(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    int randomInRange(int max) {
        try {
            return ThreadLocalRandom.current().nextInt(0, max + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int randomMove() {
        ArrayList<Integer> moves = new ArrayList<>();
        for (int cell = 0; cell < 32; cell++) {
            if (visited[cell] == moveType.EMPTY)
                moves.add(cell);
        }
        int randIdx = randomInRange(moves.size());
        return moves.get(randIdx);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int computerMove(moveType type, levelType level) {
        int cnt = 0;
        for (int i = 0; i < 32; i++) {
            if (visited[i] != moveType.EMPTY)
                cnt++;
        }
        int move;
        if (level == levelType.EASY)
            move = randomMove();
        else if (level == levelType.HARD && cnt >= 20)
            move = findBestMove(type);
        else
            move = mediumMove(type);

        visited[move] = type;
        for (int i = 0; i < 56; i++)
            gameMatrix[i] += (int) type.getNumber() * a[move][i];

        return move;
    }

    public ArrayList<Integer> checkWinning() {
        ArrayList<Integer> vec = new ArrayList<>();
        for (int i = 0; i < 56; i++) {
            if (Math.abs(gameMatrix[i]) == 4) {
                for (int j = 0; j < 32; j++) {
                    if (a[j][i] == 1)
                        vec.add(j);
                }
                return vec;
            }
        }
        return vec;
    }


    private moveType checkSpiralTrap() {
        //every left spiral has four opposite right spirals that it intersects with
        //check for each left spiral, if it and one of its opposites has score 2
        //so if one put another on on that spiral, he will have two 3s.
        ArrayList<Integer> lSpiral = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            lSpiral.add(nextState[40 + i]);
        ArrayList<Integer> rSpiral = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            rSpiral.add(nextState[48]);
        for (int left = 0; left < 8; left++) {
            if (Math.abs(lSpiral.get(left)) == 2) {
                int right = left;
                for (int i = 0; i < 4; i++) {
                    if (rSpiral.get(right).equals(lSpiral.get(left)))
                        return moveType.values()[(rSpiral.get(right) / 2)];
                    right = (right + 2) % 8;
                }
            }
        }
        return moveType.EMPTY;
    }

    private moveType checkOtherTraps(int move, int[] gameMatrix) {
        for (int i = 0; i < spirals.get(move).size(); ++i)
            for (int j = 0; j < lines.get(move).size(); ++j) {
                int u = spirals.get(move).get(i), v = lines.get(move).get(j);
                if (gameMatrix[u] == 2 && gameMatrix[v] == 2)
                    return moveType.X;
                if (gameMatrix[u] == -2 && gameMatrix[v] == -2)
                    return moveType.O;
            }
        for (int i = 0; i < rings.get(move).size(); ++i)
            for (int j = 0; j < lines.get(move).size(); ++j) {
                int u = rings.get(move).get(i), v = lines.get(move).get(j);
                if (gameMatrix[u] == 2 && gameMatrix[v] == 2)
                    return moveType.X;
                if (gameMatrix[u] == -2 && gameMatrix[v] == -2)
                    return moveType.O;
            }
        for (int i = 0; i < spirals.get(move).size(); ++i)
            for (int j = 0; j < rings.get(move).size(); ++j) {
                int u = spirals.get(move).get(i), v = rings.get(move).get(j);
                if (gameMatrix[u] == 2 && gameMatrix[v] == 2)
                    return moveType.X;
                if (gameMatrix[u] == -2 && gameMatrix[v] == -2)
                    return moveType.O;
            }
        for (int i = 0; i < rings.get(move).size(); ++i)
            for (int j = 0; j < rings.get(move).size(); ++j) {
                int u = rings.get(move).get(i), v = rings.get(move).get(j);
                if (u != v && gameMatrix[u] == 2 && gameMatrix[v] == 2)
                    return moveType.X;
                if (u != v && gameMatrix[u] == -2 && gameMatrix[v] == -2)
                    return moveType.O;
            }
        return moveType.EMPTY;
    }
}