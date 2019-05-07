// DoodleView.java
// Main View for the Doodlz app.
package com.fernando.ikigai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// custom View for drawing
public class DoodleView extends View {
    // used to determine whether user moved a finger enough to draw again
    private static final float TOUCH_TOLERANCE = 10;
    private Bitmap bitmap; // drawing area for displaying or saving
    private Canvas bitmapCanvas; // used to to draw on the bitmap
    private final Paint paintScreen; // used to draw bitmap onto screen
    private final Paint paintLine; // used to draw lines onto bitmap
    private final Paint paintFilledShape;
    private int totalHeight;
    private int totalWidth;
    private double centerx1, centerx2, centerx3, centery1, centery2, centery3;
    double radius, diag;
    private Path circle1, circle2, circle3, circle4;
    private Point center1, center2, center3, center4;
    private String mode;
    Fragment fragment = new Fragment();

    // Maps of current Paths being drawn and Points in those Paths
    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap =  new HashMap<>();

    // DoodleView constructor initializes the DoodleView
    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs); // pass context to View's constructor
        paintScreen = new Paint(); // used to display bitmap onto screen

        // set the initial display settings for the painted line
        paintLine = new Paint();
        paintLine.setAntiAlias(true); // smooth edges of drawn line
        paintLine.setColor(Color.BLACK); // default color is black
        paintLine.setStyle(Paint.Style.STROKE); // solid line
        paintLine.setStrokeWidth(5); // set the default line width
        paintLine.setStrokeCap(Paint.Cap.ROUND); // rounded line ends
        paintFilledShape = new Paint();
        paintFilledShape.setStyle(Paint.Style.FILL);

    }

    // creates Bitmap and Canvas based on View's size
    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE); // erase the Bitmap with white

        getWidth();
    }

    // clear the painting
    public void clear() {
        pathMap.clear(); // remove all paths
        previousPointMap.clear(); // remove all previous points
        bitmap.eraseColor(Color.WHITE); // clear the bitmap
        invalidate(); // refresh the screen
    }

    // set the painted line's color
    public void setDrawingColor(int color) {
        paintLine.setColor(color);
    }

    // return the painted line's color
    public int getDrawingColor() {
        return paintLine.getColor();
    }

    // set the painted line's width
    public void setLineWidth(int width) {
        paintLine.setStrokeWidth(width);
    }

    // return the painted line's width
    public int getLineWidth() {
        return (int) paintLine.getStrokeWidth();
    }

    // perform custom drawing when the DoodleView is refreshed on screen
    @Override
    protected void onDraw(Canvas canvas) {
        // draw the background screen

        Dimensions();

        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        setPaintMode("automatic"); //força o desenho automático

        // for each path currently being drawn
        for (Integer key : pathMap.keySet())
            canvas.drawPath(pathMap.get(key), paintLine); // draw line
    }

    // handle touch event
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked(); // event type
        int actionIndex = event.getActionIndex(); // pointer (i.e., finger)

        // determine whether touch started, ended or is moving
        if (action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));

        }
        else if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        }
        else {
            touchMoved(event);
        }

        invalidate(); // redraw
        return true;
    }

    // called when the user touches the screen
    private void touchStarted(float x, float y, int lineID) {
        Path path; // used to store the path for the given touch id
        Point point; // used to store the last point in path


        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID); // get the Path
            path.reset(); // resets the Path because a new touch has started
            point = previousPointMap.get(lineID); // get Path's last point
        }
        else {
            path = new Path();
            pathMap.put(lineID, path); // add the Path to Map
            point = new Point(); // create a new Point
            previousPointMap.put(lineID, point); // add the Point to the Map
        }
        // move to the coordinates of the touch
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;

    }

    // called when the user drags along the screen
    private void touchMoved(MotionEvent event) {
        // for each of the pointers in the given MotionEvent
        for (int i = 0; i < event.getPointerCount(); i++) {
            // get the pointer ID and pointer index
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                // get the new coordinates for the pointer
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                // get the path and previous point associated with
                // this pointer
                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                // calculate how far the user moved from the last update
                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    // store the new coordinates
                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }



    // called when the user finishes a touch
    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID); // get the corresponding Path
        bitmapCanvas.drawPath(path, paintLine); // draw to bitmapCanvas
        path.reset(); // reset the Path
    }

    // save the current image to the Gallery
    public void saveImage() {
        // use "Doodlz" followed by current time as the image name
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";

        // insert the image on the device
        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), bitmap, name,
                "Doodlz Drawing");

        if (location != null) {
            // display a message indicating that the image was saved
            Toast message = Toast.makeText(getContext(),
                    R.string.message_saved,
                    Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
        else {
            // display a message indicating that there was an error saving
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_saving, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
    }

    // print the current image
    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            // use Android Support Library's PrintHelper to print image
            PrintHelper printHelper = new PrintHelper(getContext());

            // fit image in page bounds and print the image
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", bitmap);
        }
        else {
            // display message indicating that system does not allow printing
            Toast message = Toast.makeText(getContext(),
                    R.string.message_error_printing, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2,
                    message.getYOffset() / 2);
            message.show();
        }
    }



    public void setPaintMode (String mode) {


        if (mode.equals("automatic"))

        {
//            ColorDialogFragment colorDialogFragment = new ColorDialogFragment();
//           FragmentManager fragmentManager = colorDialogFragment.getFragmentManager();
//            colorDialogFragment.show(fragmentManager, "color diag");

            circle1 = new Path();
            circle2 = new Path();
            circle3 = new Path();
            circle4 = new Path();



            paintFilledShape.setARGB(192,0,64,128);

            circle1.addCircle(center1.x, center1.y, (float)radius, Path.Direction.CW);

            bitmapCanvas.drawPath(circle1, paintFilledShape);

            paintFilledShape.setARGB(192,64,128,255);

            circle2.addCircle(center2.x, center2.y, (float)radius, Path.Direction.CW);

            bitmapCanvas.drawPath(circle2, paintFilledShape);

            paintFilledShape.setARGB(192, 128, 255, 192);

            circle3.addCircle(center3.x, center3.y, (float)radius, Path.Direction.CW);

            bitmapCanvas.drawPath(circle3, paintFilledShape);

            paintFilledShape.setARGB(192, 255, 0, 64);

            circle4.addCircle(center4.x, center4.y, (float)radius, Path.Direction.CW);

            bitmapCanvas.drawPath(circle4, paintFilledShape);

            paintPoints();
        }
    }

    public void Dimensions() {

        //pega dimensões da tela
        totalHeight = getHeight();
        totalWidth = getWidth();

        //calcula comprimento da diagonal entre os centros usando uma proporção em relação à largura da tela
        diag = totalWidth/2.5;


        //determina o raio das circunferências conforme a diagonal
        radius = (int) Math.sqrt((Math.pow(diag, 2))/2);

        //define coordenadas x e y para os centros de cada círculo
        centerx2 = (totalWidth)/2;
        centerx1 = centerx2-diag/2;
        centerx3 = centerx2+diag/2;
        centery2 = totalHeight/2-50;
        centery1 = centery2-diag/2;
        centery3 = centery2+diag/2;

        center1 = new Point();
        center2 = new Point();
        center3 = new Point();
        center4 = new Point();

        //define os centros dos círculos
        center1.set((int)centerx2, (int)centery1);
        center2.set((int)centerx1, (int)centery2);
        center3.set((int)centerx3, (int)centery2);
        center4.set((int)centerx2, (int)centery3);

    }

    //calcula a distância - hipotenusa - entre um dado centro de um círculo e um dado ponto
    public double calculateDistance(Point center, Point point) {

        double hipotenusa;

        double cateto1, cateto2;

        if (point.x > center.x)
            cateto1 = point.x - center.x;
        else
            cateto1 = center.x - point.x;

        if (point.y > center.y)
            cateto2 = point.y - center.y;
        else
            cateto2 = center.y - point.y;

        hipotenusa = Math.sqrt(Math.pow(cateto1, 2) + Math.pow(cateto2, 2));

        return  hipotenusa;
    }

    public boolean testPoint (Point point, Point center) {


        return calculateDistance(center, point) <= radius;
    }

    public void setCircleColor() {

    }




    public void paintPoints ()
    {
        Point point = new Point();

        int i, j;


        for (i = 0;i <= totalHeight+100; i++)
        {
            for (j=0; j<=totalWidth; j++)

            {
                point.set(i, j);

                //se o dado ponto está situado a uma distância menor ou igual ao raio

                if (calculateDistance(center1, point)<=radius
                        &&calculateDistance(center2, point)<=radius
                        &&calculateDistance(center3, point)<=radius
                        &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 255, 255, 255);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        //&&calculateDistance(center2, point)<=radius
                        &&calculateDistance(center3, point)<=radius
                        &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 0, 0, 255);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        &&calculateDistance(center2, point)<=radius
                        //&&calculateDistance(center3, point)<=radius
                        &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 255, 0, 255);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (/*calculateDistance(center1, point)<=radius &&*/
                        calculateDistance(center2, point)<=radius
                                &&calculateDistance(center3, point)<=radius
                                &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 0, 255, 0);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        //&&calculateDistance(center2, point)<=radius
                        &&calculateDistance(center3, point)<=radius
                        &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 255, 0, 0);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        &&calculateDistance(center2, point)<=radius
                        &&calculateDistance(center3, point)<=radius
                    /*&&calculateDistance(center4, point)<=radius*/) {

                    paintFilledShape.setARGB(255, 255, 255, 0);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        //&&calculateDistance(center2, point)<=radius
                        &&calculateDistance(center3, point)<=radius
                    /*&&calculateDistance(center4, point)<=radius*/) {

                    paintFilledShape.setARGB(255, 255, 0, 255);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (calculateDistance(center1, point)<=radius
                        &&calculateDistance(center2, point)<=radius
                        /*&&calculateDistance(center3, point)<=radius
                        &&calculateDistance(center4, point)<=radius*/) {

                    paintFilledShape.setARGB(255, 0, 255, 255);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (/*calculateDistance(center1, point)<=radius*/
                        calculateDistance(center2, point)<=radius
                                //&&calculateDistance(center3, point)<=radius
                                &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 128, 128, 0);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
                else if (/*calculateDistance(center1, point)<=radius
                        &&calculateDistance(center2, point)<=radius*/
                        calculateDistance(center3, point)<=radius
                                &&calculateDistance(center4, point)<=radius) {

                    paintFilledShape.setARGB(255, 0, 0, 0);
                    bitmapCanvas.drawPoint(i, j, paintFilledShape);
                }
            }
        }
    }
}



