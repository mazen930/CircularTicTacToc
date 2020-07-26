package com.blogspot.coderzgeek.customviews.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.blogspot.coderzgeek.customviews.ChooseWhoIsStarting;
import com.blogspot.coderzgeek.customviews.CustomDialogClass;
import com.blogspot.coderzgeek.customviews.GameLogic;
import com.blogspot.coderzgeek.customviews.MainActivity;
import com.blogspot.coderzgeek.customviews.R;
import com.blogspot.coderzgeek.customviews.StartGameActivity;

import java.util.ArrayList;

public class CustomView extends View {

    private Paint mPaintCircle, filledCircleRed, filledCircleGreen;
    private float cX;
    private float cY;
    private float r1, r2, r3, r4;
    private boolean flag = false;
    private Drawable drawableRight, drawableFalse;
    private Bitmap bitmapRight, bitmapFalse, temp;
    boolean taken[][], visited[][];
    int turns;
    GameLogic gameLogic;
    int gameMode;
    int firstToPlay;

    public enum GameMode {
        onePlayer,
        twoPlayer
    }

    private GameLogic.moveType currentMove;
    private GameLogic.levelType level;
    private GameMode mode;

    ArrayList<ArrayList<Pair<Float, Float>>> cells;


    public CustomView(Context context) {
        super(context);

        init(null, context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, context);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs, context);
    }

    private void init(@Nullable AttributeSet set, Context context) {
        gameMode = StartGameActivity.gameMode;
        firstToPlay = ChooseWhoIsStarting.firstToPlay;
        turns = 0;
        mPaintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(5);
        mPaintCircle.setColor(Color.BLACK);

        filledCircleRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        filledCircleRed.setColor(Color.RED);

        filledCircleGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        filledCircleGreen.setColor(Color.GREEN);

        cX = (float) (Resources.getSystem().getDisplayMetrics().widthPixels / 2.0);
        cY = (float) (Resources.getSystem().getDisplayMetrics().heightPixels / 2.0);

        float basicRadius = (float) ((Resources.getSystem().getDisplayMetrics().widthPixels / 2.0) - 20);
        r1 = (float) (basicRadius * 0.25);
        r2 = (float) (basicRadius * 0.5);
        r3 = (float) (basicRadius * 0.75);
        r4 = basicRadius;

        temp = BitmapFactory.decodeResource(getResources(), R.drawable.false_20);

        drawableRight = context.getResources().getDrawable(R.drawable.right_8);
        drawableFalse = context.getResources().getDrawable(R.drawable.false_8);

        bitmapRight = drawableToBitmap(drawableRight);
        bitmapFalse = drawableToBitmap(drawableFalse);

        getCellsLocation();
        initializeTakenArray();

        gameLogic = new GameLogic();//initialize
        this.mode = GameMode.twoPlayer;
        //this.level
        if (firstToPlay == 0)
            currentMove = GameLogic.moveType.X;
        else
            currentMove = GameLogic.moveType.O;


        if (set == null)
            return;
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);
        ta.recycle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {

        //Draw 4 Concentric Circles
        canvas.drawCircle(cX, cY, r1, mPaintCircle);
        canvas.drawCircle(cX, cY, r2, mPaintCircle);
        canvas.drawCircle(cX, cY, r3, mPaintCircle);
        canvas.drawCircle(cX, cY, r4, mPaintCircle);

        float theta = (float) (1.0 / Math.sqrt(2));
        //Draw 4 Lines
        canvas.drawLine(cX - r4, cY, cX + r4, cY, mPaintCircle);//horizontal
        canvas.drawLine(cX, cY + r4, cX, cY - r4, mPaintCircle);//vertical
        canvas.drawLine(cX - r4 * theta, cY - r4 * theta, cX + r4 * theta, cY + r4 * theta, mPaintCircle);//line with slop 45
        canvas.drawLine(cX - r4 * theta, cY + r4 * theta, cX + r4 * theta, cY - r4 * theta, mPaintCircle);//line with slope 135 degree

        //Draw signs
        for (int i = 0; i < cells.size(); i++) {
            for (int j = 0; j < cells.get(i).size(); j++) {
                if (taken[i][j]) {
                    float x = cells.get(i).get(j).first;
                    float y = cells.get(i).get(j).second;
                    if (!visited[i][j])
                        canvas.drawCircle(x, y, (float) (0.1 * r1), filledCircleRed);
                    else
                        canvas.drawCircle(x, y, (float) (0.1 * r1), filledCircleGreen);
                }
            }
        }
        /**
         * You can also draw a full path given a set of points so that canvas can draw it
         **/


    }

    boolean insideCircle(float x, float y, float radius) {
        return Math.pow(x - cX, 2) + Math.pow(y - cY, 2) < Math.pow(radius, 2);
    }

    int cellNumber(float x, float y) {
        float slope = (y - cY) / (x - cX);
        slope = Math.abs(slope);
        float angle = (float) Math.atan(slope);
        angle = (float) (angle * 180.0 / Math.PI);
        if (x > cX && y > cY)
            angle = angle;
        else if (x < cX && y > cY)
            angle = 180 - angle;
        else if (x < cX && y < cY)
            angle = 180 + angle;
        else angle = 360 - angle;
        if (angle < 45) { //first quadrant
            //cell 1
            return 1;
        } else if (angle < 90) {
            // cell 2
            return 2;
        } else if (angle < 135) {//second quadrant
            //cell 3
            return 3;
        } else if (angle < 180) {
            //cell 4
            return 4;
        } else if (angle < 225) {//third quadrant
            //cell5
            return 5;
        } else if (angle < 270) {
            // cell 6
            return 6;
        } else if (angle < 315) {//fourth quadrant
            // cell 7
            return 7;
        } else if (angle < 360) {
            //cell 8
            return 8;
        } else
            return -1;
    }

    //This method to handle touch events to move canvas object
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            if (insideCircle(x, y, r1)) {
                //Inside Circle with r1
                if (!taken[cellNumber(x, y) - 1][0]) {
                    handlingTouchEvent(x, y, 0, 0);
                    postInvalidate();//update UI
                    //Toast.makeText(getContext(), "This point is inside circle 1 and cell # " + cellNumber(x, y), Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (insideCircle(x, y, r2)) {
                //inside Circle with r2
                if (!taken[cellNumber(x, y) - 1][1]) {
                    handlingTouchEvent(x, y, 1, 8);
                    postInvalidate();//update UI
                    //Toast.makeText(getContext(), "This point is inside circle 2 and cell # " + cellNumber(x, y), Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (insideCircle(x, y, r3)) {
                //inside circle with r3
                if (!taken[cellNumber(x, y) - 1][2]) {
                    handlingTouchEvent(x, y, 2, 16);
                    postInvalidate();//update UI
                    //Toast.makeText(getContext(), "This point is inside circle 3 and cell # " + cellNumber(x, y), Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else if (insideCircle(x, y, r4)) {
                //inside circle with r4
                if (!taken[cellNumber(x, y) - 1][3]) {
                    handlingTouchEvent(x, y, 3, 24);
                    postInvalidate();//update UI
                    //Toast.makeText(getContext(), "This point is inside circle 4 and cell # " + cellNumber(x, y), Toast.LENGTH_SHORT).show();
                    return true;
                }
            } else {
                Toast.makeText(getContext(), "This point is outside all circles", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return value;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {//this function change from drawable to bitmap

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    void getCellsLocation() {
        cells = new ArrayList<>();
        ArrayList<Pair<Float, Float>> arr = new ArrayList<>();
        for (float angle = (float) 22.5; angle < 360; angle += 45) {
            float x = (float) (cX + r1 / 2 * Math.cos(angle * Math.PI / 180)),
                    y = (float) (cY + r1 / 2 * Math.sin(angle * Math.PI / 180));
            arr.add(Pair.create(x, y));
            x = (float) (cX + (r2 / 2 + 50) * Math.cos(angle * Math.PI / 180));
            y = (float) (cY + (r2 / 2 + 50) * Math.sin(angle * Math.PI / 180));
            arr.add(Pair.create(x, y));
            x = (float) (cX + (r3 / 2 + 150) * Math.cos(angle * Math.PI / 180));
            y = (float) (cY + (r3 / 2 + 150) * Math.sin(angle * Math.PI / 180));
            arr.add(Pair.create(x, y));
            x = (float) (cX + (r4 / 2 + 200) * Math.cos(angle * Math.PI / 180));
            y = (float) (cY + (r4 / 2 + 200) * Math.sin(angle * Math.PI / 180));
            arr.add(Pair.create(x, y));
            cells.add(arr);
            arr = new ArrayList<>();
        }
    }

    void initializeTakenArray() {
        taken = new boolean[8][4];
        visited = new boolean[8][4];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 4; j++) {
                taken[i][j] = false;
                visited[i][j] = false;
            }
    }

    int mapping(int cellNumber) {
        if (cellNumber == 1)
            return 1;
        else if (cellNumber == 2) {
            return 0;

        } else if (cellNumber == 3) {
            return 7;

        } else if (cellNumber == 4) {
            return 6;

        } else if (cellNumber == 5) {
            return 5;

        } else if (cellNumber == 6) {
            return 4;

        } else if (cellNumber == 7) {
            return 3;

        } else if (cellNumber == 8) {
            return 2;
        } else
            return -1;

    }

    void handlingTouchEvent(float x, float y, int circleNumber, int levelFactor) {
        taken[cellNumber(x, y) - 1][circleNumber] = true;
        visited[cellNumber(x, y) - 1][circleNumber] = turns != 0;
        if (gameMode == 1)// 2 player Mode
            gameLogic.humanMove(currentMove, mapping(cellNumber(x, y)) + levelFactor);
        else if (gameMode == 0) {// 1 Player Mode
            if ((firstToPlay == 0 && currentMove == GameLogic.moveType.O) || (firstToPlay == 1 && currentMove == GameLogic.moveType.X))
                gameLogic.computerMove(currentMove, GameLogic.levelType.HARD);
            else
                gameLogic.humanMove(currentMove, mapping(cellNumber(x, y)) + levelFactor);
        }
        if (gameLogic.checkWinning().size() != 0) {
            CustomDialogClass cdd = new CustomDialogClass((Activity) getContext());
            cdd.show();
            //Toast.makeText(getContext(), "You won", Toast.LENGTH_SHORT).show();
        }
        currentMove = currentMove == GameLogic.moveType.X ? GameLogic.moveType.O : GameLogic.moveType.X;
        turns = turns == 1 ? 0 : 1;

    }

}
