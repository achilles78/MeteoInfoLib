 /* Copyright 2012 Yaqiang Wang,
 * yaqiang.wang@gmail.com
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 */
package org.meteoinfo.drawing;

import org.meteoinfo.geoprocess.Spline;
import org.meteoinfo.global.colors.ColorUtil;
import org.meteoinfo.global.Extent;
import org.meteoinfo.global.MIMath;
import org.meteoinfo.global.PointD;
import org.meteoinfo.global.PointF;
import org.meteoinfo.legend.BreakTypes;
import org.meteoinfo.legend.ChartBreak;
import org.meteoinfo.legend.LabelBreak;
import org.meteoinfo.legend.LineStyles;
import org.meteoinfo.legend.PointBreak;
import org.meteoinfo.legend.PolygonBreak;
import org.meteoinfo.legend.PolylineBreak;
import org.meteoinfo.shape.Graphic;
import org.meteoinfo.shape.Polyline;
import org.meteoinfo.shape.WindArraw;
import org.meteoinfo.shape.WindBarb;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.meteoinfo.global.GlobalUtil;
import org.meteoinfo.legend.MapFrame;
import org.meteoinfo.shape.StationModelShape;

/**
 * Draw class with some drawing methods
 *
 * @author Yaqiang Wang
 */
public class Draw {

    // <editor-fold desc="Point">
    /**
     * Create wind barb from wind direction/speed
     *
     * @param windDir
     * @param windSpeed
     * @param value
     * @param size
     * @param sPoint
     * @return
     */
    public static WindBarb calWindBarb(float windDir, float windSpeed, double value,
            float size, PointD sPoint) {
        WindBarb aWB = new WindBarb();

        windSpeed += 1;
        aWB.windSpeed = windSpeed;
        aWB.angle = windDir;
        aWB.setValue(value);
        aWB.size = size;
        aWB.setPoint(sPoint);
        aWB.windSpeesLine.W20 = (int) (windSpeed / 20);
        aWB.windSpeesLine.W4 = (int) ((windSpeed - aWB.windSpeesLine.W20 * 20) / 4);
        aWB.windSpeesLine.W2 = (int) ((windSpeed - aWB.windSpeesLine.W20 * 20
                - aWB.windSpeesLine.W4 * 4) / 2);

        return aWB;
    }

    /**
     * Create station model shape
     *
     * @param windDir Wind direction
     * @param windSpeed Wind speed
     * @param value Value
     * @param size Size
     * @param sPoint Location point
     * @param weather Weather
     * @param temp Temperature
     * @param dewPoint Dew point
     * @param pressure Pressure
     * @param cloudCover Cloud cover
     * @return Station model shape
     */
    public static StationModelShape calStationModel(float windDir, float windSpeed, double value,
            float size, PointD sPoint, int weather, int temp, int dewPoint, int pressure, int cloudCover) {
        StationModelShape aSM = new StationModelShape();
        aSM.setPoint(sPoint);
        aSM.setValue(value);
        aSM.size = size;
        aSM.temperature = temp;
        aSM.dewPoint = dewPoint;
        aSM.pressure = pressure;
        aSM.windBarb = calWindBarb(windDir, windSpeed, value, size, sPoint);
        aSM.weatherSymbol.size = size / 4 * 3;
        //sPoint.X = sPoint.X - size / 2;
        PointD aPoint = new PointD(sPoint.X - size / 2, sPoint.Y);
        aSM.weatherSymbol.setPoint(aPoint);
        aSM.weatherSymbol.weather = weather;
        aSM.cloudCoverage.cloudCover = cloudCover;
        aSM.cloudCoverage.size = size / 4 * 3;
        aSM.cloudCoverage.sPoint = aPoint;

        return aSM;
    }

    /**
     * Draw wind arrow
     *
     * @param aColor The color
     * @param sP Start point
     * @param aArraw The arrow
     * @param g Graphics2D
     * @param zoom Zoom
     */
    public static void drawArraw(Color aColor, PointF sP, WindArraw aArraw, Graphics2D g, double zoom) {
        PointF eP = new PointF(0, 0);
        PointF eP1 = new PointF(0, 0);
        double len = aArraw.length;
        double angle = aArraw.angle + 180;
        if (angle >= 360) {
            angle -= 360;
        }

        len = len * zoom;

        eP.X = (int) (sP.X + len * Math.sin(angle * Math.PI / 180));
        eP.Y = (int) (sP.Y - len * Math.cos(angle * Math.PI / 180));

        if (angle == 90) {
            eP.Y = sP.Y;
        }
        g.setColor(aColor);
        g.draw(new Line2D.Float(sP.X, sP.Y, eP.X, eP.Y));

        eP1.X = (int) (eP.X - aArraw.size * Math.sin((angle + 20.0) * Math.PI / 180));
        eP1.Y = (int) (eP.Y + aArraw.size * Math.cos((angle + 20.0) * Math.PI / 180));
        g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));

        eP1.X = (int) (eP.X - aArraw.size * Math.sin((angle - 20.0) * Math.PI / 180));
        eP1.Y = (int) (eP.Y + aArraw.size * Math.cos((angle - 20.0) * Math.PI / 180));
        g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));

    }

    /**
     * Draw wind barb
     *
     * @param aColor Color
     * @param sP Point
     * @param aWB WindBarb
     * @param g Grahics2D
     * @param size Size
     */
    public static void drawWindBarb(Color aColor, PointF sP, WindBarb aWB, Graphics2D g, float size) {
        PointF eP;
        PointF eP1;
        double len = size * 2;
        int i;

        double aLen = len;

        eP = new PointF();
        eP.X = (float) (sP.X + len * Math.sin(aWB.angle * Math.PI / 180));
        eP.Y = (float) (sP.Y - len * Math.cos(aWB.angle * Math.PI / 180));
        g.setColor(aColor);
        g.draw(new Line2D.Float(sP.X, sP.Y, eP.X, eP.Y));

        len = len / 2;
        if (aWB.windSpeesLine.W20 > 0) {
            for (i = 0; i < aWB.windSpeesLine.W20; i++) {
                eP1 = new PointF();
                eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 105) * Math.PI / 180));
                eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 105) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
                eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
                eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
            }
            eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
            eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
        }
        if (aWB.windSpeesLine.W4 > 0) {
            for (i = 0; i < aWB.windSpeesLine.W4; i++) {
                eP1 = new PointF();
                eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 120) * Math.PI / 180));
                eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 120) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
                eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
                eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
            }
        }
        if (aWB.windSpeesLine.W2 > 0) {
            len = len / 2;
            eP1 = new PointF();
            eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 120) * Math.PI / 180));
            eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 120) * Math.PI / 180));
            g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
        }
    }

    /**
     * Draw wind barb
     *
     * @param aColor Color
     * @param sP Point
     * @param aWB WindBarb
     * @param g Grahics2D
     * @param size Size
     * @param cut Cut
     */
    public static void drawWindBarb(Color aColor, PointF sP, WindBarb aWB, Graphics2D g, float size, float cut) {
        PointF eP;
        PointF eP1;
        double len = size * 2;
        int i;

        double aLen = len;

        eP = new PointF();
        eP.X = (float) (sP.X + len * Math.sin(aWB.angle * Math.PI / 180));
        eP.Y = (float) (sP.Y - len * Math.cos(aWB.angle * Math.PI / 180));
        PointF cutSP = new PointF(0, 0);
        cutSP.X = (float) (sP.X + cut * Math.sin(aWB.angle * Math.PI / 180));
        cutSP.Y = (float) (sP.Y - cut * Math.cos(aWB.angle * Math.PI / 180));
        g.setColor(aColor);
        g.draw(new Line2D.Float(cutSP.X, cutSP.Y, eP.X, eP.Y));

        len = len / 2;
        if (aWB.windSpeesLine.W20 > 0) {
            for (i = 0; i < aWB.windSpeesLine.W20; i++) {
                eP1 = new PointF();
                eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 105) * Math.PI / 180));
                eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 105) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
                eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
                eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
            }
            eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
            eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
        }
        if (aWB.windSpeesLine.W4 > 0) {
            for (i = 0; i < aWB.windSpeesLine.W4; i++) {
                eP1 = new PointF();
                eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 120) * Math.PI / 180));
                eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 120) * Math.PI / 180));
                g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
                eP.X = (float) (eP.X - aLen / 8 * Math.sin((aWB.angle) * Math.PI / 180));
                eP.Y = (float) (eP.Y + aLen / 8 * Math.cos((aWB.angle) * Math.PI / 180));
            }
        }
        if (aWB.windSpeesLine.W2 > 0) {
            len = len / 2;
            eP1 = new PointF();
            eP1.X = (float) (eP.X - len * Math.sin((aWB.angle - 120) * Math.PI / 180));
            eP1.Y = (float) (eP.Y + len * Math.cos((aWB.angle - 120) * Math.PI / 180));
            g.draw(new Line2D.Float(eP.X, eP.Y, eP1.X, eP1.Y));
        }
    }

    /**
     * Draw point
     *
     * @param aPS Point style
     * @param aP The point position
     * @param color The color
     * @param outlineColor Outline color
     * @param aSize size
     * @param drawOutline If draw outline
     * @param drawFill If draw fill
     * @param g Graphics2D
     */
    public static void drawPoint(PointStyle aPS, PointF aP, Color color, Color outlineColor,
            float aSize, Boolean drawOutline, Boolean drawFill, Graphics2D g) {
        PointBreak aPB = new PointBreak();
        aPB.setMarkerType(MarkerType.Simple);
        aPB.setStyle(aPS);
        aPB.setColor(color);
        aPB.setOutlineColor(outlineColor);
        aPB.setSize(aSize);
        aPB.setDrawOutline(drawOutline);
        aPB.setDrawFill(drawFill);

        drawPoint(aP, aPB, g);
    }

    /**
     * Draw point
     *
     * @param aP Position
     * @param aPB Point break
     * @param g Graphics
     */
    public static void drawPoint(PointF aP, PointBreak aPB, Graphics2D g) {
        switch (aPB.getMarkerType()) {
            case Simple:
                drawPoint_Simple(aP, aPB, g);
                break;
            case Character:
                drawPoint_Character(aP, aPB, g);
                break;
            case Image:
                drawPoint_Image(aP, aPB, g);
                break;
        }

        //if (aPB.Angle != 0)
        //    g.Transform = new Matrix();
    }

    private static void drawPoint_Simple(PointF aP, PointBreak aPB, Graphics2D g) {
        AffineTransform tempTrans = g.getTransform();
        if (aPB.getAngle() != 0) {
            AffineTransform myTrans = new AffineTransform();
            myTrans.translate(tempTrans.getTranslateX() + aP.X, tempTrans.getTranslateY() + aP.Y);
            myTrans.rotate(aPB.getAngle() * Math.PI / 180);
            g.setTransform(myTrans);
            aP.X = 0;
            aP.Y = 0;
        }

        int[] xPoints;
        int[] yPoints;
        float aSize = aPB.getSize();
        boolean drawFill = aPB.getDrawFill();
        boolean drawOutline = aPB.getDrawOutline();
        Color color = aPB.getColor();
        Color outlineColor = aPB.getOutlineColor();

        GeneralPath path = new GeneralPath();

        switch (aPB.getStyle()) {
            case Circle:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize / 2;
                if (drawFill) {
                    g.setColor(color);
                    g.fillOval((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawOval((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                break;
            case Square:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize / 2;
                if (drawFill) {
                    g.setColor(color);
                    g.fillRect((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawRect((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                break;
            case Diamond:
                xPoints = new int[4];
                yPoints = new int[4];
                xPoints[0] = (int) (aP.X - aSize / 2);
                yPoints[0] = (int) aP.Y;
                xPoints[1] = (int) aP.X;
                yPoints[1] = (int) (aP.Y - aSize / 2);
                xPoints[2] = (int) (aP.X + aSize / 2);
                yPoints[2] = (int) aP.Y;
                xPoints[3] = (int) aP.X;
                yPoints[3] = (int) (aP.Y + aSize / 2);
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
            case UpTriangle:
                xPoints = new int[3];
                yPoints = new int[3];
                xPoints[0] = (int) aP.X;
                yPoints[0] = (int) (aP.Y - aSize / 2);
                xPoints[1] = (int) (aP.X + aSize / 4 * Math.sqrt(3));
                yPoints[1] = (int) (aP.Y + aSize / 4);
                xPoints[2] = (int) (aP.X - aSize / 4 * Math.sqrt(3));
                yPoints[2] = (int) (aP.Y + aSize / 4);
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
            case DownTriangle:
                xPoints = new int[3];
                yPoints = new int[3];
                xPoints[0] = (int) aP.X;
                yPoints[0] = (int) (aP.Y + aSize / 2);
                xPoints[1] = (int) (aP.X - aSize / 4 * Math.sqrt(3));
                yPoints[1] = (int) (aP.Y - aSize / 4);
                xPoints[2] = (int) (aP.X + aSize / 4 * Math.sqrt(3));
                yPoints[2] = (int) (aP.Y - aSize / 4);
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
            case XCross:
                path.moveTo(aP.X - aSize / 2, aP.Y - aSize / 2);
                path.lineTo(aP.X + aSize / 2, aP.Y + aSize / 2);
                path.moveTo(aP.X - aSize / 2, aP.Y + aSize / 2);
                path.lineTo(aP.X + aSize / 2, aP.Y - aSize / 2);
                path.closePath();
                if (drawFill || drawOutline) {
                    g.setColor(color);
                    g.draw(path);
                }
                break;
            case Plus:
                path.moveTo(aP.X, aP.Y - aSize / 2);
                path.lineTo(aP.X, aP.Y + aSize / 2);
                path.moveTo(aP.X - aSize / 2, aP.Y);
                path.lineTo(aP.X + aSize / 2, aP.Y);
                path.closePath();
                if (drawFill || drawOutline) {
                    g.setColor(color);
                    g.draw(path);
                }
                break;
            case StarLines:
                path.moveTo(aP.X - aSize / 2, aP.Y - aSize / 2);
                path.lineTo(aP.X + aSize / 2, aP.Y + aSize / 2);
                path.moveTo(aP.X - aSize / 2, aP.Y + aSize / 2);
                path.lineTo(aP.X + aSize / 2, aP.Y - aSize / 2);
                path.moveTo(aP.X, aP.Y - aSize / 2);
                path.lineTo(aP.X, aP.Y + aSize / 2);
                path.moveTo(aP.X - aSize / 2, aP.Y);
                path.lineTo(aP.X + aSize / 2, aP.Y);
                path.closePath();
                if (drawFill || drawOutline) {
                    g.setColor(color);
                    g.draw(path);
                }
                break;
            case Star:
                float vRadius = aSize / 2;
                //Calculate 5 end points
                PointF[] vPoints = new PointF[5];
                double vAngle = 2.0 * Math.PI / 4 + Math.PI;
                for (int i = 0; i < vPoints.length; i++) {
                    vAngle += 2.0 * Math.PI / (double) vPoints.length;
                    vPoints[i] = new PointF(
                            (float) (Math.cos(vAngle) * vRadius) + aP.X,
                            (float) (Math.sin(vAngle) * vRadius) + aP.Y);
                }
                //Calculate 5 cross points
                PointF[] cPoints = new PointF[5];
                cPoints[0] = MIMath.getCrossPoint(vPoints[0], vPoints[2], vPoints[1], vPoints[4]);
                cPoints[1] = MIMath.getCrossPoint(vPoints[1], vPoints[3], vPoints[0], vPoints[2]);
                cPoints[2] = MIMath.getCrossPoint(vPoints[1], vPoints[3], vPoints[2], vPoints[4]);
                cPoints[3] = MIMath.getCrossPoint(vPoints[0], vPoints[3], vPoints[2], vPoints[4]);
                cPoints[4] = MIMath.getCrossPoint(vPoints[0], vPoints[3], vPoints[1], vPoints[4]);
                //New points
                xPoints = new int[10];
                yPoints = new int[10];
                for (int i = 0; i < 5; i++) {
                    xPoints[i * 2] = (int) vPoints[i].X;
                    yPoints[i * 2] = (int) vPoints[i].Y;
                    xPoints[i * 2 + 1] = (int) cPoints[i].X;
                    yPoints[i * 2 + 1] = (int) cPoints[i].Y;
                }
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
            case Pentagon:
                vRadius = aSize / 2;
                //Calculate 5 end points
                xPoints = new int[5];
                yPoints = new int[5];
                vAngle = 2.0 * Math.PI / 4 + Math.PI;
                for (int i = 0; i < 5; i++) {
                    vAngle += 2.0 * Math.PI / (double) 5;
                    xPoints[i] = (int) (Math.cos(vAngle) * vRadius + aP.X);
                    yPoints[i] = (int) (Math.sin(vAngle) * vRadius + aP.Y);
                }
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
            case UpSemiCircle:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize / 2;
                if (drawFill) {
                    g.setColor(color);
                    g.fill(new Arc2D.Float(aP.X, aP.Y, aSize, aSize, 180, 180, Arc2D.CHORD));
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.draw(new Arc2D.Float(aP.X, aP.Y, aSize, aSize, 180, 180, Arc2D.CHORD));
                }
                break;
            case DownSemiCircle:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize / 2;
                if (drawFill) {
                    g.setColor(color);
                    g.fill(new Arc2D.Float(aP.X, aP.Y, aSize, aSize, 0, 180, Arc2D.CHORD));
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.draw(new Arc2D.Float(aP.X, aP.Y, aSize, aSize, 0, 180, Arc2D.CHORD));
                }
                break;
        }

        if (aPB.getAngle() != 0) {
            g.setTransform(tempTrans);
        }
    }

    private static void drawPoint_Simple_Up(PointF aP, PointBreak aPB, Graphics2D g) {
        AffineTransform tempTrans = g.getTransform();
        if (aPB.getAngle() != 0) {
            AffineTransform myTrans = new AffineTransform();
            myTrans.translate(tempTrans.getTranslateX() + aP.X, tempTrans.getTranslateY() + aP.Y);
            myTrans.rotate(aPB.getAngle() * Math.PI / 180);
            g.setTransform(myTrans);
            aP.X = 0;
            aP.Y = 0;
        }

        int[] xPoints;
        int[] yPoints;
        float aSize = aPB.getSize();
        boolean drawFill = aPB.getDrawFill();
        boolean drawOutline = aPB.getDrawOutline();
        Color color = aPB.getColor();
        Color outlineColor = aPB.getOutlineColor();

        GeneralPath path = new GeneralPath();

        switch (aPB.getStyle()) {
            case Circle:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize;

                if (drawFill) {
                    g.setColor(color);
                    g.fillOval((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawOval((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                break;
            case Square:
                aP.X = aP.X - aSize / 2;
                aP.Y = aP.Y - aSize;

                if (drawFill) {
                    g.setColor(color);
                    g.fillRect((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawRect((int) aP.X, (int) aP.Y, (int) aSize, (int) aSize);
                }
                break;
            case Diamond:
                xPoints = new int[4];
                yPoints = new int[4];
                xPoints[0] = (int) (aP.X - aSize / 2);
                yPoints[0] = (int) aP.Y;
                xPoints[1] = (int) aP.X;
                yPoints[1] = (int) (aP.Y - aSize / 2);
                xPoints[2] = (int) (aP.X + aSize / 2);
                yPoints[2] = (int) aP.Y;
                xPoints[3] = (int) aP.X;
                yPoints[3] = (int) (aP.Y + aSize / 2);
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
            case UpTriangle:
                xPoints = new int[3];
                yPoints = new int[3];
                xPoints[0] = (int) aP.X;
                yPoints[0] = (int) (aP.Y - aSize * 3 / 4);
                xPoints[1] = (int) (aP.X + aSize / 4 * Math.sqrt(3));
                yPoints[1] = (int) (aP.Y);
                xPoints[2] = (int) (aP.X - aSize / 4 * Math.sqrt(3));
                yPoints[2] = (int) (aP.Y);
                if (drawFill) {
                    g.setColor(color);
                    g.fillPolygon(xPoints, yPoints, xPoints.length);
                }
                if (drawOutline) {
                    g.setColor(outlineColor);
                    g.drawPolygon(xPoints, yPoints, xPoints.length);
                }
                break;
        }

        if (aPB.getAngle() != 0) {
            g.setTransform(tempTrans);
        }
    }

    private static void drawPoint_Character(PointF aP, PointBreak aPB, Graphics2D g) {
        AffineTransform tempTrans = g.getTransform();
        if (aPB.getAngle() != 0) {
            AffineTransform myTrans = new AffineTransform();
            myTrans.translate(aP.X, aP.Y);
            myTrans.rotate(aPB.getAngle() * Math.PI / 180);
            g.setTransform(myTrans);
            aP.X = 0;
            aP.Y = 0;
        }

        String text = String.valueOf((char) aPB.getCharIndex());
        Font wFont = new Font(aPB.getFontName(), Font.PLAIN, (int) aPB.getSize());
        g.setFont(wFont);
        FontMetrics metrics = g.getFontMetrics();
        PointF sPoint = (PointF) aP.clone();
        sPoint.X = sPoint.X - metrics.stringWidth(text) / 2;
        sPoint.Y = sPoint.Y + metrics.getHeight() / 4;
        //sPoint.X = sPoint.X - aPB.getSize() / 2;
        //sPoint.Y = sPoint.Y + aPB.getSize() / 2;        

        g.setColor(aPB.getColor());
        g.drawString(text, sPoint.X, sPoint.Y);

        if (aPB.getAngle() != 0) {
            g.setTransform(tempTrans);
        }
    }

    private static void drawPoint_Image(PointF aP, PointBreak aPB, Graphics2D g) {
        AffineTransform tempTrans = g.getTransform();
        if (aPB.getAngle() != 0) {
            AffineTransform myTrans = new AffineTransform();
            myTrans.translate(aP.X, aP.Y);
            myTrans.rotate(aPB.getAngle() * Math.PI / 180);
            g.setTransform(myTrans);
            aP.X = 0;
            aP.Y = 0;
        }

        File imgFile = new File(aPB.getImagePath());
        if (!imgFile.exists()) {
            //String path = System.getProperty("user.dir");
            File directory = new File(".");
            String path = null;
            try {
                path = directory.getCanonicalPath();
            } catch (IOException ex) {
                Logger.getLogger(Draw.class.getName()).log(Level.SEVERE, null, ex);
            }
            path = path + File.separator + "Image";
            aPB.setImagePath(path + File.separator + imgFile.getName());
        }
        if (imgFile.exists()) {
            Image image = null;
            try {
                image = ImageIO.read(imgFile);
            } catch (IOException ex) {
                Logger.getLogger(Draw.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (image != null) {
                //((Bitmap)image).MakeTransparent(Color.White);
                PointF sPoint = aP;
                sPoint.X = sPoint.X - aPB.getSize() / 2;
                sPoint.Y = sPoint.Y - aPB.getSize() / 2;
                g.drawImage(image, (int) sPoint.X, (int) sPoint.Y, (int) aPB.getSize(), (int) aPB.getSize(), null);
            }
        }

        if (aPB.getAngle() != 0) {
            g.setTransform(tempTrans);
        }
    }

    /**
     * Draw label point
     *
     * @param aPoint The screen point
     * @param aLB The label break
     * @param g Graphics2D
     * @param rect The extent rectangle
     */
    public static void drawLabelPoint(PointF aPoint, LabelBreak aLB, Graphics2D g, Rectangle rect) {
        FontMetrics metrics = g.getFontMetrics(aLB.getFont());
        Dimension labSize = new Dimension(metrics.stringWidth(aLB.getText()), metrics.getHeight());
        switch (aLB.getAlignType()) {
            case Center:
                aPoint.X = aPoint.X - (float) labSize.getWidth() / 2;
                break;
            case Left:
                aPoint.X = aPoint.X - (float) labSize.getWidth();
                break;
        }
        aLB.setYShift((float) labSize.getHeight() / 2);
        aPoint.Y -= aLB.getYShift();
        aPoint.X += aLB.getXShift();
        float inx = aPoint.X;
        float iny = aPoint.Y;

        AffineTransform tempTrans = g.getTransform();
        if (aLB.getAngle() != 0) {
            AffineTransform myTrans = new AffineTransform();
            myTrans.translate(aPoint.X, aPoint.Y);
            myTrans.rotate(aLB.getAngle() * Math.PI / 180);
            g.setTransform(myTrans);
            aPoint.X = 0;
            aPoint.Y = 0;
        }

        g.setColor(aLB.getColor());
        g.setFont(aLB.getFont());
        g.drawString(aLB.getText(), aPoint.X, aPoint.Y + metrics.getHeight() / 2);

        rect.x = (int) aPoint.X;
        rect.y = (int) aPoint.Y - metrics.getHeight() / 2;
        rect.width = (int) labSize.getWidth();
        rect.height = (int) labSize.getHeight();

        if (aLB.getAngle() != 0) {
            g.setTransform(tempTrans);
            rect.x = (int) inx;
            rect.y = (int) iny;
        }
    }

    /**
     * Draw station model shape
     *
     * @param aColor Color
     * @param foreColor Foreground color
     * @param sP Start point
     * @param aSM Station model shape
     * @param g Graphics2D
     * @param size Size
     * @param cut Cut
     */
    public static void drawStationModel(Color aColor, Color foreColor, PointF sP, StationModelShape aSM, Graphics2D g,
            float size, float cut) {
        PointF sPoint = new PointF(0, 0);
        g.setColor(aColor);
        Font wFont;
        String text;

        //Draw cloud coverage     
        if (aSM.cloudCoverage.cloudCover >= 0 && aSM.cloudCoverage.cloudCover <= 9) {
            //Draw wind barb
            drawWindBarb(aColor, sP, aSM.windBarb, g, size, cut);
            text = String.valueOf((char) (aSM.cloudCoverage.cloudCover + 197));
            wFont = new Font("Weather", Font.PLAIN, (int) size);
            FontMetrics metrics = g.getFontMetrics(wFont);
            Dimension textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() / 2;
            sPoint.Y = sP.Y - (float) textSize.getHeight() / 2;
            g.setFont(wFont);
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        } else {
            //Draw wind barb
            drawWindBarb(aColor, sP, aSM.windBarb, g, size);

            wFont = new Font("Arial", Font.PLAIN, (int) (size / 4 * 3));
            text = "M";
            FontMetrics metrics = g.getFontMetrics(wFont);
            Dimension textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() / 2;
            sPoint.Y = sP.Y - (float) textSize.getHeight() / 3 * 2;
            g.setFont(wFont);
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
            wFont = new Font("Weather", Font.PLAIN, (int) size);
            text = String.valueOf((char) 197);
            metrics = g.getFontMetrics(wFont);
            textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() / 2;
            sPoint.Y = sP.Y - (float) textSize.getHeight() / 2;
            g.setFont(wFont);
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        }

        //Draw weather
        if (aSM.weatherSymbol.weather >= 4 && aSM.weatherSymbol.weather <= 99) {
            wFont = new Font("Weather", Font.PLAIN, (int) size);
            text = String.valueOf((char) (aSM.weatherSymbol.weather + 100));
            FontMetrics metrics = g.getFontMetrics(wFont);
            Dimension textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() - aSM.size / 2;
            sPoint.Y = sP.Y - (float) textSize.getHeight() / 2;
            text = String.valueOf((char) (aSM.weatherSymbol.weather + 28));
            if (aSM.weatherSymbol.weather == 99) {
                text = String.valueOf((char) (aSM.weatherSymbol.weather + 97));
            }
            g.setFont(wFont);
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        }

        wFont = new Font("Arial", Font.PLAIN, (int) (size / 4 * 3));
        g.setFont(wFont);
        FontMetrics metrics = g.getFontMetrics(wFont);
        //Draw temperature
        if (Math.abs(aSM.temperature) < 1000) {
            g.setColor(Color.red);
            text = String.valueOf(aSM.temperature);
            Dimension textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() - size / 3;
            sPoint.Y = sP.Y - (float) textSize.getHeight() - size / 3;
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        }

        //Draw dew point
        if (Math.abs(aSM.dewPoint) < 1000) {
            g.setColor(Color.green);
            text = String.valueOf(aSM.dewPoint);
            Dimension textSize = new Dimension(metrics.stringWidth(text), metrics.getHeight());
            sPoint.X = sP.X - (float) textSize.getWidth() - size / 3;
            sPoint.Y = sP.Y + size / 3;
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        }

        //Draw pressure
        if (Math.abs(aSM.pressure) < 1000) {
            g.setColor(foreColor);
            text = String.format("%1$03d", aSM.pressure);
            sPoint.X = sP.X + size / 3;
            sPoint.Y = sP.Y - metrics.getHeight() - size / 3;
            g.drawString(text, sPoint.X, sPoint.Y + metrics.getHeight() * 3 / 4);
        }
    }

    // </editor-fold>
    // <editor-fold desc="Graphic">
    /**
     * Draw graphic
     *
     * @param points The points
     * @param aGrahpic The graphic
     * @param g Graphics2D
     * @param isEditingVertice Is editing vertice
     */
    public static void drawGrahpic(PointF[] points, Graphic aGraphic, Graphics2D g, boolean isEditingVertice) {
        Rectangle rect = new Rectangle();
        Extent aExtent = MIMath.getPointFsExtent(points);
        rect.x = (int) aExtent.minX;
        rect.y = (int) aExtent.minY;
        rect.width = (int) aExtent.getWidth();
        rect.height = (int) aExtent.getHeight();

        switch (aGraphic.getShape().getShapeType()) {
            case Point:
                switch (aGraphic.getLegend().getBreakType()) {
                    case PointBreak:
                        drawPoint((PointF) points[0].clone(), (PointBreak) aGraphic.getLegend(), g);
                        int aSize = (int) ((PointBreak) aGraphic.getLegend()).getSize() / 2 + 2;
                        rect.x = (int) points[0].X - aSize;
                        rect.y = (int) points[0].Y - aSize;
                        rect.width = aSize * 2;
                        rect.height = aSize * 2;
                        break;
                    case LabelBreak:
                        drawLabelPoint((PointF) points[0].clone(), (LabelBreak) aGraphic.getLegend(), g, rect);
                        break;
                }
                break;
            case Polyline:
                drawPolyline(points, (PolylineBreak) aGraphic.getLegend(), g);
                break;
            case Polygon:
            case Rectangle:
                drawPolygon(points, (PolygonBreak) aGraphic.getLegend(), g);
                break;
            case CurveLine:
                drawCurveLine(points, (PolylineBreak) aGraphic.getLegend(), g);
                break;
            case CurvePolygon:
                drawCurvePolygon(points, (PolygonBreak) aGraphic.getLegend(), g);
                break;
            case Circle:
                drawCircle(points, (PolygonBreak) aGraphic.getLegend(), g);
                break;
            case Ellipse:
                drawEllipse(points, (PolygonBreak) aGraphic.getLegend(), g);
                break;
        }

        //Draw selected rectangle
        if (aGraphic.getShape().isSelected()) {
            if (isEditingVertice) {
                drawSelectedVertices(g, points);
            } else {
                float[] dashPattern = new float[]{2.0F, 1.0F};
                g.setColor(Color.cyan);
                g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
                g.draw(rect);
                switch (aGraphic.getShape().getShapeType()) {
                    case Point:
                        if (aGraphic.getLegend().getBreakType() == BreakTypes.PointBreak) {
                            drawSelectedCorners(g, rect);
                        }
                        break;
                    case Polyline:
                    case CurveLine:
                    case Polygon:
                    case Rectangle:
                    case Ellipse:
                    case CurvePolygon:
                        drawSelectedCorners(g, rect);
                        drawSelectedEdgeCenters(g, rect);
                        break;
                    case Circle:
                        drawSelectedCorners(g, rect);
                        break;
                }
            }
        }
    }

    /**
     * Draw polyline
     *
     * @param points Points list
     * @param g Graphics2D
     */
    public static void drawPolyline(List<PointF> points, Graphics2D g) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
        for (int i = 0; i < points.size(); i++) {
            if (i == 0) {
                path.moveTo(points.get(i).X, points.get(i).Y);
            } else {
                path.lineTo(points.get(i).X, points.get(i).Y);
            }
        }

        g.draw(path);
    }

    /**
     * Draw polyline
     *
     * @param points The points array
     * @param g Graphics2D
     */
    public static void drawPolyline(PointF[] points, Graphics2D g) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
        for (int i = 0; i < points.length; i++) {
            if (i == 0) {
                path.moveTo(points[i].X, points[i].Y);
            } else {
                path.lineTo(points[i].X, points[i].Y);
            }
        }

        g.draw(path);
    }

    /**
     * Fill polygon
     *
     * @param points The points array
     * @param g Graphics2D
     */
    public static void fillPolygon(PointF[] points, Graphics2D g) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.length);
        for (int i = 0; i < points.length; i++) {
            if (i == 0) {
                path.moveTo(points[i].X, points[i].Y);
            } else {
                path.lineTo(points[i].X, points[i].Y);
            }
        }
        path.closePath();

        g.fill(path);
    }

    /**
     * Draw polygon
     *
     * @param points The points
     * @param aPGB The polygon break
     * @param g Graphics2D
     */
    public static void drawPolygon(PointF[] points, PolygonBreak aPGB, Graphics2D g) {
        if (aPGB.getDrawFill()) {
            g.setColor(aPGB.getColor());
            fillPolygon(points, g);
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            drawPolyline(points, g);
        }
    }

    /**
     * Draw polygon
     *
     * @param points The points
     * @param aColor Fill oclor
     * @param outlineColor Outline color
     * @param width
     * @param height
     * @param drawFill
     * @param drawOutline
     * @param g
     */
    public static void drawPolygon(PointF[] points, Color aColor, Color outlineColor,
            boolean drawFill, boolean drawOutline, Graphics2D g) {
        if (drawFill) {
            g.setColor(aColor);
            fillPolygon(points, g);
        }
        if (drawOutline) {
            g.setColor(outlineColor);
            drawPolyline(points, g);
        }
    }

    /**
     * Get dash pattern from LineStyle
     *
     * @param style The line style
     * @return Dash pattern array
     */
    public static float[] getDashPattern(LineStyles style) {
        float[] dashPattern = {4.0f};
        switch (style) {
            case Solid:
                dashPattern = null;
                break;
            case Dash:
                dashPattern = new float[]{4.0f};
                break;
            case Dot:
                dashPattern = new float[]{2.0f};
                break;
            case DashDot:
                dashPattern = new float[]{10, 6, 2, 6};
                break;
            case DashDotDot:
                dashPattern = new float[]{10, 6, 2, 6, 2, 6};
                break;
        }

        return dashPattern;
    }

    /**
     * Draw polyline
     *
     * @param points The points
     * @param aPLB The polyline break
     * @param g Graphics2D
     */
    public static void drawPolyline(PointF[] points, PolylineBreak aPLB, Graphics2D g) {
        if (aPLB.getUsingDashStyle()) {
            g.setColor(aPLB.getColor());
            float[] dashPattern = getDashPattern(aPLB.getStyle());
            g.setStroke(new BasicStroke(aPLB.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
            drawPolyline(points, g);

            //Draw symbol            
            if (aPLB.getDrawSymbol()) {
                Object rend = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 0; i < points.length; i++) {
                    if (i % aPLB.getSymbolInterval() == 0) {
                        drawPoint(aPLB.getSymbolStyle(), points[i], aPLB.getSymbolColor(), aPLB.getSymbolColor(),
                                aPLB.getSymbolSize(), true, false, g);
                    }
                }
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, rend);
            }
        } else {
            Polyline aPLine = new Polyline();
            aPLine.setPoints(points);
            List<double[]> pos = aPLine.getPositions(30);
            float aSize = 16;
            int i;
            switch (aPLB.getStyle()) {
                case ColdFront:
                    if (pos != null) {
                        PointBreak aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(Color.blue);
                        aPB.setStyle(PointStyle.UpTriangle);
                        aPB.setOutlineColor(Color.blue);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 0; i < pos.size(); i++) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple_Up(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }
                    }

                    g.setColor(Color.blue);
                    g.setStroke(new BasicStroke(aPLB.getSize()));
                    drawPolyline(points, g);
                    break;
                case WarmFront:
                    if (pos != null) {
                        PointBreak aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(Color.red);
                        aPB.setStyle(PointStyle.UpSemiCircle);
                        aPB.setOutlineColor(Color.red);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 0; i < pos.size(); i++) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }
                    }

                    g.setColor(Color.red);
                    g.setStroke(new BasicStroke(aPLB.getSize()));
                    drawPolyline(points, g);
                    break;
                case OccludedFront:
                    Color aColor = new Color(255, 0, 255);
                    if (pos != null) {
                        PointBreak aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(aColor);
                        aPB.setStyle(PointStyle.UpTriangle);
                        aPB.setOutlineColor(aColor);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 0; i < pos.size(); i += 2) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple_Up(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }

                        aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(aColor);
                        aPB.setStyle(PointStyle.UpSemiCircle);
                        aPB.setOutlineColor(aColor);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 1; i < pos.size(); i += 2) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }
                    }

                    g.setColor(aColor);
                    g.setStroke(new BasicStroke(aPLB.getSize()));
                    drawPolyline(points, g);
                    break;
                case StationaryFront:
                    if (pos != null) {
                        PointBreak aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(Color.blue);
                        aPB.setStyle(PointStyle.UpTriangle);
                        aPB.setOutlineColor(Color.blue);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 0; i < pos.size(); i += 2) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple_Up(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }

                        aPB = new PointBreak();
                        aPB.setSize(aSize);
                        aPB.setColor(Color.red);
                        aPB.setStyle(PointStyle.DownSemiCircle);
                        aPB.setOutlineColor(Color.red);
                        aPB.setDrawFill(true);
                        aPB.setDrawOutline(true);
                        for (i = 1; i < pos.size(); i += 2) {
                            aPB.setAngle((float) pos.get(i)[2]);
                            drawPoint_Simple(new PointF((float) pos.get(i)[0], (float) pos.get(i)[1]), aPB, g);
                        }
                    }

                    g.setColor(Color.blue);
                    g.setStroke(new BasicStroke(aPLB.getSize()));
                    drawPolyline(points, g);
                    break;
            }
        }
    }

    /**
     * Draw polyline symbol
     *
     * @param aP The point
     * @param width The width
     * @param height The height
     * @param aPLB The polyline break
     * @param g Graphics2D
     */
    public static void drawPolylineSymbol(PointF aP, float width, float height, PolylineBreak aPLB, Graphics2D g) {
        if (aPLB.getUsingDashStyle()) {
            PointF[] points = new PointF[4];
            PointF aPoint = new PointF(0, 0);
            aPoint.X = aP.X - width / 2;
            aPoint.Y = aP.Y + height / 2;
            points[0] = aPoint;
            aPoint = new PointF();
            aPoint.X = aP.X - width / 6;
            aPoint.Y = aP.Y - height / 2;
            points[1] = aPoint;
            aPoint = new PointF();
            aPoint.X = aP.X + width / 6;
            aPoint.Y = aP.Y + height / 2;
            points[2] = aPoint;
            aPoint = new PointF();
            aPoint.X = aP.X + width / 2;
            aPoint.Y = aP.Y - height / 2;
            points[3] = aPoint;

            g.setColor(aPLB.getColor());
            float[] dashPattern = getDashPattern(aPLB.getStyle());
            g.setStroke(new BasicStroke(aPLB.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));

            if (aPLB.getDrawPolyline()) {
                drawPolyline(points, g);
            }
            g.setStroke(new BasicStroke());

            //Draw symbol
            if (aPLB.getDrawSymbol()) {
                drawPoint(aPLB.getSymbolStyle(), points[1], aPLB.getSymbolColor(), aPLB.getSymbolColor(), aPLB.getSymbolSize(), true, false, g);
                drawPoint(aPLB.getSymbolStyle(), points[2], aPLB.getSymbolColor(), aPLB.getSymbolColor(), aPLB.getSymbolSize(), true, false, g);
            }
        } else {
            PointF[] points = new PointF[2];
            PointF aPoint = new PointF(0, 0);
            aPoint.X = aP.X - width / 2;
            aPoint.Y = aP.Y;
            points[0] = aPoint;
            aPoint = new PointF();
            aPoint.X = aP.X + width / 2;
            aPoint.Y = aP.Y;
            points[1] = aPoint;
            float lineWidth = 2.0f;
            switch (aPLB.getStyle()) {
                case ColdFront:
                    PointBreak aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(Color.blue);
                    aPB.setStyle(PointStyle.UpTriangle);
                    aPB.setOutlineColor(Color.blue);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X - aPB.getSize() * 2 / 3, aP.Y - aPB.getSize() / 4), aPB, g);
                    drawPoint_Simple(new PointF(aP.X + aPB.getSize() * 2 / 3, aP.Y - aPB.getSize() / 4), aPB, g);

                    g.setColor(Color.blue);
                    g.setStroke(new BasicStroke(lineWidth));
                    drawPolyline(points, g);
                    break;
                case WarmFront:
                    aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(Color.red);
                    aPB.setStyle(PointStyle.UpSemiCircle);
                    aPB.setOutlineColor(Color.red);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X - aPB.getSize() * 2 / 3, aP.Y), aPB, g);
                    drawPoint_Simple(new PointF(aP.X + aPB.getSize() * 2 / 3, aP.Y), aPB, g);

                    g.setColor(Color.red);
                    g.setStroke(new BasicStroke(lineWidth));
                    drawPolyline(points, g);
                    break;
                case OccludedFront:
                    Color aColor = new Color(255, 0, 255);
                    aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(aColor);
                    aPB.setStyle(PointStyle.UpTriangle);
                    aPB.setOutlineColor(aColor);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X - aPB.getSize() * 2 / 3, aP.Y - aPB.getSize() / 4), aPB, g);
                    aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(aColor);
                    aPB.setStyle(PointStyle.UpSemiCircle);
                    aPB.setOutlineColor(aColor);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X + aPB.getSize() * 2 / 3, aP.Y), aPB, g);

                    g.setColor(aColor);
                    g.setStroke(new BasicStroke(lineWidth));
                    drawPolyline(points, g);
                    break;
                case StationaryFront:
                    aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(Color.blue);
                    aPB.setStyle(PointStyle.UpTriangle);
                    aPB.setOutlineColor(Color.blue);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X - aPB.getSize() * 2 / 3, aP.Y - aPB.getSize() / 4), aPB, g);
                    aPB = new PointBreak();
                    aPB.setSize(14);
                    aPB.setColor(Color.red);
                    aPB.setStyle(PointStyle.DownSemiCircle);
                    aPB.setOutlineColor(Color.red);
                    aPB.setDrawFill(true);
                    aPB.setDrawOutline(true);
                    drawPoint_Simple(new PointF(aP.X + aPB.getSize() * 2 / 3, aP.Y), aPB, g);

                    g.setColor(Color.blue);
                    g.setStroke(new BasicStroke(lineWidth));
                    drawPolyline(points, g);
                    break;
            }
        }
    }

//    /**
//     * Draw polygon symbol
//     *
//     * @param aP The point
//     * @param width The width
//     * @param height The height
//     * @param aPGB The polygon break
//     * @param transparencyPerc Transparency percent
//     * @param g Graphics2D
//     */
//    public static void drawPolygonSymbol(PointF aP, float width, float height, PolygonBreak aPGB,
//            int transparencyPerc, Graphics2D g) {
//        int alpha = (int) ((1 - (double) transparencyPerc / 100.0) * 255);
//        Color c = aPGB.getColor();
//        Color aColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
////            Brush aBrush;
////            if (aPGB.UsingHatchStyle)
////                aBrush = new HatchBrush(aPGB.Style, aColor, aPGB.BackColor);
////            else
////                aBrush = new SolidBrush(aColor);
//
//        aP.X = aP.X - width / 2;
//        aP.Y = aP.Y - height / 2;
//        if (aPGB.getDrawFill()) {
//            g.setColor(aColor);
//            g.fill(new Rectangle.Float(aP.X, aP.Y, width, height));
//        }
//        if (aPGB.getDrawOutline()) {
//            g.setColor(aPGB.getOutlineColor());
//            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
//            g.draw(new Rectangle.Float(aP.X, aP.Y, width, height));
//        }
//    }
    /**
     * Draw polygon symbol
     *
     * @param aP The point
     * @param width The width
     * @param height The height
     * @param aPGB The polygon break
     * @param g Graphics2D
     */
    public static void drawPolygonSymbol(PointF aP, float width, float height, PolygonBreak aPGB,
            Graphics2D g) {
//            Brush aBrush;
//            if (aPGB.UsingHatchStyle)
//                aBrush = new HatchBrush(aPGB.Style, aColor, aPGB.BackColor);
//            else
//                aBrush = new SolidBrush(aColor);

        aP.X = aP.X - width / 2;
        aP.Y = aP.Y - height / 2;
        if (aPGB.getDrawFill()) {
            g.setColor(aPGB.getColor());
            g.fill(new Rectangle.Float(aP.X, aP.Y, width, height));
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            g.draw(new Rectangle.Float(aP.X, aP.Y, width, height));
        }
    }

    /**
     * Draw polygon symbol
     *
     * @param aP The point
     * @param aColor Fill color
     * @param outlineColor Outline color
     * @param width Width
     * @param height Height
     * @param drawFill If draw fill
     * @param drawOutline If draw outline
     * @param g Grahics2D
     */
    public static void drawPolygonSymbol(PointF aP, Color aColor, Color outlineColor,
            float width, float height, Boolean drawFill, Boolean drawOutline, Graphics2D g) {
        aP.X = aP.X - width / 2;
        aP.Y = aP.Y - height / 2;
        if (drawFill) {
            g.setColor(aColor);
            g.fill(new Rectangle.Float(aP.X, aP.Y, width, height));
        }
        if (drawOutline) {
            g.setColor(outlineColor);
            g.draw(new Rectangle.Float(aP.X, aP.Y, width, height));
        }
    }

    /**
     * Draw rectangle
     *
     * @param aPoint Start point
     * @param width Width
     * @param height Height
     * @param aPGB Polygon break
     * @param g Graphics2D
     */
    public static void drawRectangle(PointF aPoint, float width, float height, PolygonBreak aPGB, Graphics2D g) {
        Color aColor = aPGB.getColor();
        if (aPGB.getDrawFill()) {
            g.setColor(aColor);
            g.fill(new Rectangle.Float(aPoint.X, aPoint.Y, width, height));
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            g.draw(new Rectangle.Float(aPoint.X, aPoint.Y, width, height));
        }
    }

    /**
     * Draw pie
     *
     * @param aPoint Start point
     * @param width Width
     * @param height Height
     * @param startAngle Start angle
     * @param sweepAngle Sweep angle
     * @param aPGB Polygon break
     * @param g Graphics2D
     */
    public static void drawPie(PointF aPoint, float width, float height, float startAngle, float sweepAngle, PolygonBreak aPGB, Graphics2D g) {
        Color aColor = aPGB.getColor();
        if (aPGB.getDrawFill()) {
            g.setColor(aColor);
            g.fill(new Arc2D.Float(aPoint.X, aPoint.Y, width, height, startAngle, sweepAngle, Arc2D.PIE));
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            g.draw(new Arc2D.Float(aPoint.X, aPoint.Y, width, height, startAngle, sweepAngle, Arc2D.PIE));
        }
    }

    /**
     * Draw curve line
     *
     * @param points The points
     * @param aPLB The polyline break
     * @param g Graphics2D
     */
    public static void drawCurveLine(PointF[] points, PolylineBreak aPLB, Graphics2D g) {
        List<PointD> opoints = new ArrayList<PointD>();
        int i;
        for (i = 0; i < points.length; i++) {
            opoints.add(new PointD(points[i].X, points[i].Y));
        }

        PointD[] rPoints = Spline.cardinalSpline((PointD[]) opoints.toArray(new PointD[opoints.size()]), 5);
        PointF[] dPoints = new PointF[rPoints.length];
        for (i = 0; i < dPoints.length; i++) {
            dPoints[i] = new PointF((float) rPoints[i].X, (float) rPoints[i].Y);
        }

        drawPolyline(dPoints, aPLB, g);
    }

    /**
     * Draw curve line
     *
     * @param points The points list
     * @param g Graphics2D
     */
    public static void drawCurveLine(List<PointF> points, Graphics2D g) {
        PointD[] opoints = new PointD[points.size()];
        int i;
        for (i = 0; i < points.size(); i++) {
            opoints[i] = new PointD(points.get(i).X, points.get(i).Y);
        }

        PointD[] rPoints = Spline.cardinalSpline(opoints, 5);
        PointF[] dPoints = new PointF[rPoints.length];
        for (i = 0; i < dPoints.length; i++) {
            dPoints[i] = new PointF((float) rPoints[i].X, (float) rPoints[i].Y);
        }

        drawPolyline(dPoints, g);
    }

    /**
     * Draw curve line
     *
     * @param points The points
     * @param g Graphics2D
     */
    public static void drawCurveLine(PointF[] points, Graphics2D g) {
        List<PointD> opoints = new ArrayList<PointD>();
        int i;
        for (i = 0; i < points.length; i++) {
            opoints.add(new PointD(points[i].X, points[i].Y));
        }

        PointD[] rPoints = Spline.cardinalSpline((PointD[]) opoints.toArray(), 5);
        PointF[] dPoints = new PointF[rPoints.length];
        for (i = 0; i < dPoints.length; i++) {
            dPoints[i] = new PointF((float) rPoints[i].X, (float) rPoints[i].Y);
        }

        drawPolyline(dPoints, g);
    }

    /**
     * Draw curve polygon
     *
     * @param points The points
     * @param aPGB Polygon break
     * @param g Graphics2D
     */
    public static void drawCurvePolygon(PointF[] points, PolygonBreak aPGB, Graphics2D g) {
        List<PointD> opoints = new ArrayList<PointD>();
        int i;
        for (i = 0; i < points.length; i++) {
            opoints.add(new PointD(points[i].X, points[i].Y));
        }

        PointD[] rPoints = Spline.cardinalSpline((PointD[]) opoints.toArray(new PointD[opoints.size()]), 5);
        PointF[] dPoints = new PointF[rPoints.length];
        for (i = 0; i < dPoints.length; i++) {
            dPoints[i] = new PointF((float) rPoints[i].X, (float) rPoints[i].Y);
        }

        drawPolygon(dPoints, aPGB, g);
    }

    /**
     * Draw circle
     *
     * @param points The points
     * @param aPGB The polygon break
     * @param g Graphics2D
     */
    public static void drawCircle(PointF[] points, PolygonBreak aPGB, Graphics2D g) {
        float radius = Math.abs(points[1].X - points[0].X);

        if (aPGB.getDrawFill()) {
            g.setColor(aPGB.getColor());
            g.fill(new Ellipse2D.Float(points[0].X, points[0].Y - radius, radius * 2, radius * 2));
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            g.draw(new Ellipse2D.Float(points[0].X, points[0].Y - radius, radius * 2, radius * 2));
        }
    }

    /**
     * Draw ellipse
     *
     * @param points The points
     * @param aPGB The polygon break
     * @param g Grahpics2D
     */
    public static void drawEllipse(PointF[] points, PolygonBreak aPGB, Graphics2D g) {
        float sx = Math.min(points[0].X, points[2].X);
        float sy = Math.min(points[0].Y, points[2].Y);
        float width = Math.abs(points[2].X - points[0].X);
        float height = Math.abs(points[2].Y - points[0].Y);

        if (aPGB.getDrawFill()) {
            g.setColor(aPGB.getColor());
            g.fill(new Ellipse2D.Float(sx, sy, width, height));
        }
        if (aPGB.getDrawOutline()) {
            g.setColor(aPGB.getOutlineColor());
            g.setStroke(new BasicStroke(aPGB.getOutlineSize()));
            g.draw(new Ellipse2D.Float(sx, sy, width, height));
        }
    }

    /**
     * Draw selected vertices rectangles
     *
     * @param g Graphics2D
     * @param points The points
     */
    public static void drawSelectedVertices(Graphics2D g, PointF[] points) {
        int size = 6;
        Rectangle rect = new Rectangle(0, 0, size, size);

        for (PointF aPoint : points) {
            rect.x = (int) aPoint.X - size / 2;
            rect.y = (int) aPoint.Y - size / 2;
            g.setColor(Color.cyan);
            g.fill(rect);
            g.setColor(Color.black);
            g.draw(rect);
        }
    }

    /**
     * Draw selected four corner rectangles
     *
     * @param g Graphics2D
     * @param gRect The rectangle
     */
    public static void drawSelectedCorners(Graphics2D g, Rectangle gRect) {
        int size = 6;
        Rectangle rect = new Rectangle(gRect.x - size / 2, gRect.y - size / 2, size, size);
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.y = gRect.y + gRect.height - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.x = gRect.x + gRect.width - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.y = gRect.y - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
    }

    /**
     * Draw selected four bouder edge center rectangles
     *
     * @param g Graphics2D
     * @param gRect The rectangle
     */
    public static void drawSelectedEdgeCenters(Graphics2D g, Rectangle gRect) {
        int size = 6;
        Rectangle rect = new Rectangle(gRect.x + gRect.width / 2 - size / 2, gRect.y - size / 2, size, size);
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.y = gRect.y + gRect.height - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.x = gRect.x - size / 2;
        rect.y = gRect.y + gRect.height / 2 - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
        rect.x = gRect.x + gRect.width - size / 2;
        g.setColor(Color.cyan);
        g.fill(rect);
        g.setColor(Color.black);
        g.draw(rect);
    }
    // </editor-fold>

    // <editor-fold desc="Chart">
    /**
     * Draw chart point
     *
     * @param aPoint Screen point
     * @param aCB Chart break
     * @param g Graphics2D
     */
    public static void drawChartPoint(PointF aPoint, ChartBreak aCB, Graphics2D g) {
        switch (aCB.getChartType()) {
            case BarChart:
                drawBarChartSymbol(aPoint, aCB, g);
                break;
            case PieChart:
                drawPieChartSymbol(aPoint, aCB, g);
                break;
        }

    }

    /**
     * Draw bar chart symbol
     *
     * @param aPoint Start point
     * @param aCB Chart break
     * @param g Graphics2D
     */
    public static void drawBarChartSymbol(PointF aPoint, ChartBreak aCB, Graphics2D g) {
        Font font = new Font("Arial", Font.PLAIN, 8);
        drawBarChartSymbol(aPoint, aCB, g, false, font);
    }

    /**
     * Draw bar chart symbol
     *
     * @param aPoint Start point
     * @param aCB Chart break
     * @param g Graphics2D
     * @param drawValue If draw value
     * @param font Value font
     */
    public static void drawBarChartSymbol(PointF sPoint, ChartBreak aCB, Graphics2D g, boolean drawValue, Font font) {
        PointF aPoint = (PointF) sPoint.clone();
        List<Integer> heights = aCB.getBarHeights();
        float y = aPoint.Y;
        for (int i = 0; i < heights.size(); i++) {
            if (heights.get(i) <= 0) {
                aPoint.X += aCB.getBarWidth();
                continue;
            }

            aPoint.Y = y - heights.get(i);
            PolygonBreak aPGB = (PolygonBreak) aCB.getLegendScheme().getLegendBreaks().get(i);
            if (aCB.isView3D()) {
                Color aColor = ColorUtil.modifyBrightness(aPGB.getColor(), 0.5f);
                PointF[] points = new PointF[4];
                points[0] = new PointF(aPoint.X, aPoint.Y);
                points[1] = new PointF(aPoint.X + aCB.getBarWidth(), aPoint.Y);
                points[2] = new PointF(points[1].X + aCB.getThickness(), points[1].Y - aCB.getThickness());
                points[3] = new PointF(points[0].X + aCB.getThickness(), points[0].Y - aCB.getThickness());
                g.setColor(aColor);
                Draw.fillPolygon(points, g);
                g.setColor(aPGB.getOutlineColor());
                Draw.drawPolyline(points, g);

                points[0] = new PointF(aPoint.X + aCB.getBarWidth(), aPoint.Y);
                points[1] = new PointF(aPoint.X + aCB.getBarWidth(), aPoint.Y + heights.get(i));
                points[2] = new PointF(points[1].X + aCB.getThickness(), points[1].Y - aCB.getThickness());
                points[3] = new PointF(points[0].X + aCB.getThickness(), points[0].Y - aCB.getThickness());
                g.setColor(aColor);
                Draw.fillPolygon(points, g);
                g.setColor(aPGB.getOutlineColor());
                Draw.drawPolyline(points, g);
            }
            drawRectangle(aPoint, aCB.getBarWidth(), heights.get(i), aPGB, g);

            aPoint.X += aCB.getBarWidth();

            if (i == heights.size() - 1) {
                if (drawValue) {
                    String vstr = String.valueOf(aCB.getChartData().get(i));
                    FontMetrics metrics = g.getFontMetrics(font);
                    Dimension labSize = new Dimension(metrics.stringWidth(vstr), metrics.getHeight());
                    aPoint.X += 2;
                    aPoint.Y = (float) (y - heights.get(i) / 2 - labSize.getHeight() / 2);
                    g.setColor(Color.black);
                    g.setFont(font);
                    g.drawString(vstr, aPoint.X, aPoint.Y + metrics.getHeight() / 2);
                }
            }
        }
    }

    /**
     * Draw pie chart symbol
     *
     * @param aPoint Start point
     * @param aCB Chart break
     * @param g Graphics2D
     */
    public static void drawPieChartSymbol(PointF aPoint, ChartBreak aCB, Graphics2D g) {
        int width = aCB.getWidth();
        int height = aCB.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        aPoint.Y -= height;
        List<List<Float>> angles = aCB.getPieAngles();
        float startAngle, sweepAngle;
        int i;
        if (aCB.isView3D()) {
            aPoint.Y = aPoint.Y + width / 6 - aCB.getThickness();
            for (i = 0; i < angles.size(); i++) {
                startAngle = angles.get(i).get(0);
                sweepAngle = angles.get(i).get(1);
                PolygonBreak aPGB = (PolygonBreak) aCB.getLegendScheme().getLegendBreaks().get(i);
                if (startAngle + sweepAngle > 180) {
                    PointF bPoint = new PointF(aPoint.X, aPoint.Y + aCB.getThickness());
                    Color aColor = ColorUtil.modifyBrightness(aPGB.getColor(), 0.5f);

                    g.setColor(aColor);
                    g.fill(new Arc2D.Float(bPoint.X, bPoint.Y, width, width * 2 / 3, startAngle, sweepAngle, Arc2D.PIE));
                    g.setColor(aPGB.getOutlineColor());
                    g.draw(new Arc2D.Float(bPoint.X, bPoint.Y, width, width * 2 / 3, startAngle, sweepAngle, Arc2D.PIE));
                }
            }
            float a = (float) width / 2;
            float b = (float) width / 3;
            float x0 = aPoint.X + a;
            float y0 = aPoint.Y + b;
            double sA, eA;
            for (i = 0; i < angles.size(); i++) {
                startAngle = angles.get(i).get(0);
                sweepAngle = angles.get(i).get(1);
                if (startAngle + sweepAngle > 180) {
                    sA = (360 - startAngle) / 180 * Math.PI;
                    eA = (360 - (startAngle + sweepAngle)) / 180 * Math.PI;
                    PolygonBreak aPGB = (PolygonBreak) aCB.getLegendScheme().getLegendBreaks().get(i);
                    PointF bPoint = MIMath.calEllipseCoordByAngle(x0, y0, a, b, eA);
                    PointF cPoint = new PointF(x0 - a, y0);
                    if (sA < Math.PI) {
                        cPoint = MIMath.calEllipseCoordByAngle(x0, y0, a, b, sA);
                    }

                    Color aColor = ColorUtil.modifyBrightness(aPGB.getColor(), 0.5f);
                    PointF[] points = new PointF[5];
                    points[0] = cPoint;
                    points[1] = new PointF(cPoint.X, cPoint.Y + aCB.getThickness());
                    points[2] = new PointF(bPoint.X, bPoint.Y + aCB.getThickness());
                    points[3] = bPoint;
                    points[4] = cPoint;
                    g.setColor(aColor);
                    Draw.fillPolygon(points, g);
                    g.setColor(aPGB.getOutlineColor());
                    g.draw(new Line2D.Float(points[0].X, points[0].Y, points[1].X, points[1].Y));
                    g.draw(new Line2D.Float(points[2].X, points[2].Y, points[3].X, points[3].Y));
                }
            }
            for (i = 0; i < angles.size(); i++) {
                startAngle = angles.get(i).get(0);
                sweepAngle = angles.get(i).get(1);
                PolygonBreak aPGB = (PolygonBreak) aCB.getLegendScheme().getLegendBreaks().get(i);
                drawPie(aPoint, width, width * 2 / 3, startAngle, sweepAngle, aPGB, g);
            }
        } else {
            for (i = 0; i < angles.size(); i++) {
                startAngle = angles.get(i).get(0);
                sweepAngle = angles.get(i).get(1);
                PolygonBreak aPGB = (PolygonBreak) aCB.getLegendScheme().getLegendBreaks().get(i);
                drawPie(aPoint, width, width, startAngle, sweepAngle, aPGB, g);
            }
        }
    }
    // </editor-fold>
    
}