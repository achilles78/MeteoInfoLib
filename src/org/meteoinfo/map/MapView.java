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
package org.meteoinfo.map;

import org.meteoinfo.data.mapdata.Field;
import org.meteoinfo.data.mapdata.MapDataManage;
import org.meteoinfo.drawing.Draw;
import org.meteoinfo.drawing.PointStyle;
import org.meteoinfo.geoprocess.GeoComputation;
import org.meteoinfo.global.colors.ColorUtil;
import org.meteoinfo.global.Direction;
import org.meteoinfo.global.event.GraphicSelectedEvent;
import org.meteoinfo.global.event.IGraphicSelectedListener;
import org.meteoinfo.global.event.ILayersUpdatedListener;
import org.meteoinfo.global.event.IProjectionChangedListener;
import org.meteoinfo.global.event.IViewExtentChangedListener;
import org.meteoinfo.global.event.LayersUpdatedEvent;
import org.meteoinfo.global.event.ProjectionChangedEvent;
import org.meteoinfo.global.event.ViewExtentChangedEvent;
import org.meteoinfo.global.Extent;
import org.meteoinfo.global.FrmMeasurement;
import org.meteoinfo.global.FrmMeasurement.MeasureTypes;
import org.meteoinfo.global.GlobalUtil;
import org.meteoinfo.global.MIMath;
import org.meteoinfo.global.PointD;
import org.meteoinfo.global.PointF;
import org.meteoinfo.global.table.DataTypes;
import org.meteoinfo.layer.ChartSet;
import org.meteoinfo.layer.ImageLayer;
import org.meteoinfo.layer.LabelSet;
import org.meteoinfo.layer.LayerCollection;
import org.meteoinfo.layer.LayerDrawType;
import org.meteoinfo.layer.LayerTypes;
import org.meteoinfo.layer.MapLayer;
import org.meteoinfo.layer.RasterLayer;
import org.meteoinfo.layer.VectorLayer;
import org.meteoinfo.layout.Edge;
import org.meteoinfo.legend.AlignType;
import org.meteoinfo.legend.BreakTypes;
import org.meteoinfo.legend.ChartBreak;
import org.meteoinfo.legend.ChartTypes;
import org.meteoinfo.legend.ColorBreak;
import org.meteoinfo.legend.FrmColorSymbolSet;
import org.meteoinfo.legend.FrmLabelSymbolSet;
import org.meteoinfo.legend.FrmPointSymbolSet;
import org.meteoinfo.legend.FrmPolygonSymbolSet;
import org.meteoinfo.legend.FrmPolylineSymbolSet;
import org.meteoinfo.legend.LabelBreak;
import org.meteoinfo.legend.LegendManage;
import org.meteoinfo.legend.LegendScheme;
import org.meteoinfo.legend.LineStyles;
import org.meteoinfo.legend.PointBreak;
import org.meteoinfo.legend.PolygonBreak;
import org.meteoinfo.legend.PolylineBreak;
import org.meteoinfo.projection.KnownCoordinateSystems;
import org.meteoinfo.projection.ProjectionInfo;
import org.meteoinfo.projection.ProjectionNames;
import org.meteoinfo.shape.CircleShape;
import org.meteoinfo.shape.CurveLineShape;
import org.meteoinfo.shape.CurvePolygonShape;
import org.meteoinfo.shape.EllipseShape;
import org.meteoinfo.shape.Graphic;
import org.meteoinfo.shape.GraphicCollection;
import org.meteoinfo.shape.PointShape;
import org.meteoinfo.shape.Polygon;
import org.meteoinfo.shape.PolygonShape;
import org.meteoinfo.shape.Polyline;
import org.meteoinfo.shape.PolylineShape;
import org.meteoinfo.shape.RectangleShape;
import org.meteoinfo.shape.Shape;
import org.meteoinfo.shape.ShapeTypes;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.SimpleDoc;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;
import static org.meteoinfo.layer.LayerDrawType.Barb;
import static org.meteoinfo.layer.LayerDrawType.StationModel;
import static org.meteoinfo.layer.LayerDrawType.Streamline;
import static org.meteoinfo.layer.LayerDrawType.Vector;
import org.meteoinfo.layer.VisibleScale;
import org.meteoinfo.legend.LegendType;
import static org.meteoinfo.legend.LegendType.GraduatedColor;
import static org.meteoinfo.legend.LegendType.SingleSymbol;
import static org.meteoinfo.legend.LegendType.UniqueValue;
import org.meteoinfo.projection.Reproject;
import static org.meteoinfo.shape.ShapeTypes.CurveLine;
import static org.meteoinfo.shape.ShapeTypes.Polyline;
import org.meteoinfo.shape.StationModelShape;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.meteoinfo.shape.WindArraw;
import org.meteoinfo.shape.WindBarb;

/**
 * MapView class
 *
 * @author Yaqiang Wang
 */
public class MapView extends JPanel {
    // <editor-fold desc="Variables">

    private EventListenerList _listeners = new EventListenerList();
    private FrmIdentifer _frmIdentifer = null;
    private FrmIdentiferGrid _frmIdentiferGrid = null;
    private FrmMeasurement _frmMeasure = null;
    private BufferedImage _mapBitmap = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage _tempImage = null;
    private boolean _antiAlias = false;
    private boolean _pointAntiAlias = true;
    private boolean _highSpeedWheelZoom = true;
    private boolean _lockViewUpdate = false;
    private LayerCollection _layers = new LayerCollection();
    private int _selectedLayer;
    private Extent _extent = new Extent();
    private Extent _viewExtent = new Extent();
    private Extent _drawExtent = new Extent();
    private double _scaleX = 1.0;
    private double _scaleY = 1.0;
    private double _XYScaleFactor = 1.0;
    private Color _selectColor = Color.yellow;
    private boolean _isGeoMap = true;
    private boolean _isLayoutMap = false;
    private ProjectionSet _projection = new ProjectionSet();
    private MouseTools _mouseTool = MouseTools.None;
    private VectorLayer _lonLatLayer = null;
    private VectorLayer _lonLatProjLayer = null;
    private GraphicCollection _graphicCollection = new GraphicCollection();
    private GraphicCollection _selectedGraphics = new GraphicCollection();
    private GraphicCollection _visibleGraphics = new GraphicCollection();
    private Rectangle _selectedRectangle = new Rectangle();
    private Edge _resizeSelectedEdge = Edge.None;
    private Rectangle _resizeRectangle = new Rectangle();
    private boolean _drawIdentiferShape = false;
    private boolean _mouseDoubleClicked = false;    //If fired mouse double click event
    Point _mouseDownPoint = new Point(0, 0);
    Point _mouseLastPos = new Point(0, 0);
    Point _mousePos = new Point(0, 0);
    private int _xShift = 0;
    private int _yShift = 0;
    private double _paintScale = 1.0;
    private MaskOut _maskOut;
    private GeneralPath _maskOutGraphicsPath = new GeneralPath();
    private FrmPointSymbolSet _frmPointSymbolSet = null;
    private FrmPolylineSymbolSet _frmPolylineSymbolSet = null;
    private FrmPolygonSymbolSet _frmPolygonSymbolSet = null;
    private FrmLabelSymbolSet _frmLabelSymbolSet = null;
    private FrmColorSymbolSet _frmColorSymbolSet = null;
    private boolean _startNewGraphic = true;
    private List<PointF> _graphicPoints = new ArrayList<PointF>();
    private PointBreak _defPointBreak = new PointBreak();
    private LabelBreak _defLabelBreak = new LabelBreak();
    private PolylineBreak _defPolylineBreak = new PolylineBreak();
    private PolygonBreak _defPolygonBreak = new PolygonBreak();
    private List<PointD> _editingVertices = new ArrayList<PointD>();
    private int _editingVerticeIndex;
    private boolean _dragMode = false;
    private boolean _multiGlobalDraw = true;
    private List<String> _xGridStrs = new ArrayList<String>();
    private List<String> _yGridStrs = new ArrayList<String>();
    private List<Object[]> _xGridPosLabel = new ArrayList<Object[]>();
    private List<Object[]> _yGridPosLabel = new ArrayList<Object[]>();
    private boolean _drawGridTickLine = false;
    private Color _gridLineColor = Color.gray;
    private float _gridLineSize = 1;
    private LineStyles _gridLineStyle = LineStyles.Dash;
    private boolean _drawGridLine = false;
    private float _gridXDelt = 10;
    private float _gridYDelt = 10;
    private float _gridXOrigin = 0;
    private float _gridYOrigin = 0;
    private boolean _gridDeltChanged = false;
    private List<GridLabel> _gridLabels = new ArrayList<GridLabel>();
    private Date _lastMouseWheelTime;
    private Timer _mouseWheelDetctionTimer;
    // </editor-fold>

    // <editor-fold desc="Constructor">
    /**
     * Constructor
     */
    public MapView() {
        super();
        this.setSize(200, 200);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                onComponentResized(e);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onMouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                onMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e);
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                onMouseMoved(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDragged(e);
            }
        });
        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                onMouseWheelMoved(e);
            }
        });
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                onKeyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                onKeyReleased(e);
            }
        });

        this._mouseWheelDetctionTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = new Date();
                if (now.getTime() - _lastMouseWheelTime.getTime() > 200) {
                    _xShift = 0;
                    _yShift = 0;
                    _paintScale = 1.0;
                    paintLayers();
                    _mouseWheelDetctionTimer.stop();
                }
            }
        });

        this.setBackground(Color.white);
        _maskOut = new MaskOut(this);
        _mouseTool = MouseTools.None;

        //FontUtil.registerWeatherFont();

        _viewExtent.minX = -180;
        _viewExtent.maxX = 180;
        _viewExtent.minY = -90;
        _viewExtent.maxY = 90;
        _drawExtent = (Extent) _viewExtent.clone();

        _scaleX = 1;
        _scaleY = 1;
        _XYScaleFactor = 1.2;

        //m_IsSelectedInLayout = false;
        _selectColor = Color.yellow;
        //_IsPaint = true;

        _defPointBreak.setSize(10);
        _defLabelBreak.setText("Text");
        _defLabelBreak.setFont(new Font(GlobalUtil.getDefaultFontName(), Font.PLAIN, 12));
        _defPolylineBreak.setColor(Color.red);
        _defPolylineBreak.setSize(2);
        _defPolygonBreak.setColor(new Color(104, 255, 104, 125));
    }
    // </editor-fold>

    // <editor-fold desc="Get Set Methods">
    /**
     * Get layers
     *
     * @return The layer collection
     */
    public LayerCollection getLayers() {
        return _layers;
    }

    /**
     * Set layers
     *
     * @param layers The layer collection
     */
    public void setLayers(LayerCollection layers) {
        _layers = layers;
    }

    /**
     * Get layer number
     *
     * @return Layer number
     */
    public int getLayerNum() {
        return _layers.size();
    }

    /**
     * Get selected layer handle
     *
     * @return
     */
    public int getSelectedLayer() {
        return this._selectedLayer;
    }

    /**
     * Set selected layer handle
     *
     * @param handle
     */
    public void setSelectedLayer(int handle) {
        this._selectedLayer = handle;
    }

    /**
     * Get last added layer
     *
     * @return
     */
    public MapLayer getLastAddedLayer() {
        int hnd = 0;
        for (int i = 0; i < _layers.size(); i++) {
            if (_layers.get(i).getHandle() > hnd) {
                hnd = _layers.get(i).getHandle();
            }
        }

        return getLayerFromHandle(hnd);
    }

    /**
     * Get if is layout map
     *
     * @return Boolean
     */
    public boolean isLayoutMap() {
        return _isLayoutMap;
    }

    /**
     * Set if is layout map
     *
     * @param istrue Boolean
     */
    public void setIsLayoutMap(boolean istrue) {
        _isLayoutMap = istrue;
    }

    /**
     * Get if is geo map
     *
     * @return Boolean
     */
    public boolean isGeoMap() {
        return _isGeoMap;
    }

    /**
     * Set if is geo map
     *
     * @param value Boolean
     */
    public void setGeoMap(boolean value) {
        _isGeoMap = value;
    }

    /**
     * Get extent of all layers
     *
     * @return The extent
     */
    public Extent getExtent() {
        return _extent;
    }

    /**
     * Set extent of all layers
     *
     * @param extent The extent
     */
    public void setExtent(Extent extent) {
        _extent = extent;
    }

    /**
     * Get view extent
     *
     * @return The view extent
     */
    public Extent getViewExtent() {
        return _viewExtent;
    }

    /**
     * Set view extent
     *
     * @param extent View extent
     */
    public void setViewExtent(Extent extent) {
        _viewExtent = (Extent) extent.clone();
        _drawExtent = (Extent) _viewExtent.clone();
        if (_isGeoMap) {
            this.setCoordinateGeoMap(extent, this.getWidth(), this.getHeight());
        } else {
            this.setCoordinateMap(extent, this.getWidth(), this.getHeight());
        }
    }

    /**
     * Get selected color
     *
     * @return Selected color
     */
    public Color getSelectColor() {
        return _selectColor;
    }

    /**
     * Set selected color
     *
     * @param color Selected Color
     */
    public void setSelectColor(Color color) {
        _selectColor = color;
    }

    /**
     * Get if draw multi global map
     *
     * @return Boolean
     */
    public boolean isMultiGlobalDraw() {
        return this._multiGlobalDraw;
    }

    /**
     * Set if draw multi global map
     *
     * @param istrue Boolean
     */
    public void setMultiGlobalDraw(boolean istrue) {
        this._multiGlobalDraw = istrue;
    }

    /**
     * Get x scale
     *
     * @return X scale
     */
    public double getXScale() {
        return _scaleX;
    }

    /**
     * Get y scale
     *
     * @return Y scale
     */
    public double getYScale() {
        return _scaleY;
    }

    /**
     * Get x/y scale factor
     *
     * @return X/Y scale factor
     */
    public double getXYScaleFactor() {
        return this._XYScaleFactor;
    }

    /**
     * Set x/y scale factor
     *
     * @param value X/Y scale factor
     */
    public void setXYScaleFactor(double value) {
        this._XYScaleFactor = value;
        if (_XYScaleFactor < 0.5 || _XYScaleFactor > 2) {
            _XYScaleFactor = 1;
        }
        zoomToExtent(_drawExtent);
    }

    /**
     * Get mouse tool
     *
     * @return The mouse tool
     */
    public MouseTools getMouseTool() {
        return this._mouseTool;
    }

    /**
     * Set mouse tool
     *
     * @param mt The mouse tool
     */
    public void setMouseTool(MouseTools mt) {
        this._mouseTool = mt;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image;
        Cursor customCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

        switch (mt) {
            case Zoom_In:
                image = toolkit.getImage(this.getClass().getResource("/org/meteoinfo/resources/zoom_in_32x32x32.png"));
                customCursor = toolkit.createCustomCursor(image, new Point(8, 8), "Zoom In");
                break;
            case Zoom_Out:
                image = toolkit.getImage(this.getClass().getResource("/org/meteoinfo/resources/zoom_out_32x32x32.png"));
                customCursor = toolkit.createCustomCursor(image, new Point(8, 8), "Zoom Out");
                break;
            case Pan:
                image = toolkit.getImage(this.getClass().getResource("/org/meteoinfo/resources/Pan_Open_32x32x32.png"));
                customCursor = toolkit.createCustomCursor(image, new Point(8, 8), "Pan");
                break;
            case Identifer:
                image = toolkit.getImage(this.getClass().getResource("/org/meteoinfo/resources/identifer_32x32x32.png"));
                customCursor = toolkit.createCustomCursor(image, new Point(8, 8), "Identifer");
                break;
            case SelectFeatures:
                customCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                this._tempImage = GlobalUtil.deepCopy(_mapBitmap);
                break;
            case New_Label:
            case New_Point:
            case New_Polygon:
            case New_Polyline:
            case New_Rectangle:
            case New_Circle:
            case New_Curve:
            case New_CurvePolygon:
            case New_Ellipse:
            case New_Freehand:
                customCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                break;
            case Measurement:
                customCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
                break;
        }

        this.setCursor(customCursor);
    }

    /**
     * Get projection set
     *
     * @return The projection set
     */
    public ProjectionSet getProjection() {
        return this._projection;
    }

    /**
     * Get if lock view update
     *
     * @return If lock view update
     */
    public boolean isLockViewUpdate() {
        return _lockViewUpdate;
    }

    /**
     * Set if lock view update
     *
     * @param istrue If lock view update
     */
    public void setLockViewUpdate(boolean istrue) {
        _lockViewUpdate = istrue;
    }

    /**
     * Get if antialias
     *
     * @return Boolean
     */
    public boolean isAntiAlias() {
        return _antiAlias;
    }

    /**
     * Set if antialias
     *
     * @param istrue Boolean
     */
    public void setAntiAlias(boolean istrue) {
        _antiAlias = istrue;
        if (_antiAlias) {
            _pointAntiAlias = true;
        }
    }

    /**
     * Get if is point antialias
     *
     * @return Boolean
     */
    public boolean isPointAntiAlias() {
        return _pointAntiAlias;
    }

    /**
     * Set if point antialias
     *
     * @param value Boolean
     */
    public void setPointAntiAlias(boolean value) {
        _pointAntiAlias = value;
    }

    /**
     * Get if is high speed mouse wheel zoom
     *
     * @return Boolean
     */
    public boolean isHighSpeedWheelZoom() {
        return _highSpeedWheelZoom;
    }

    /**
     * Set if is high speed mouse wheel zoom
     *
     * @param value Boolean
     */
    public void setHighSpeedWheelZoom(boolean value) {
        _highSpeedWheelZoom = value;
    }

    /**
     * Get selected graphics
     *
     * @return Selected graphics
     */
    public GraphicCollection getSelectedGraphics() {
        return this._selectedGraphics;
    }

    /**
     * Get default point break
     *
     * @return Default point break
     */
    public PointBreak getDefPointBreak() {
        return _defPointBreak;
    }

    /**
     * Set default point break
     *
     * @param pb Default point break
     */
    public void setDefPointBreak(PointBreak pb) {
        _defPointBreak = pb;
    }

    /**
     * Get default label break
     *
     * @return Default label break
     */
    public LabelBreak getDefLabelBreak() {
        return _defLabelBreak;
    }

    /**
     * Set default label break
     *
     * @param lb Default label break
     */
    public void setDefLabelBreak(LabelBreak lb) {
        _defLabelBreak = lb;
    }

    /**
     * Get default polyline break
     *
     * @return Default polyline break
     */
    public PolylineBreak getDefPolylineBreak() {
        return _defPolylineBreak;
    }

    /**
     * Set default polyline break
     *
     * @param pb Default polyline break
     */
    public void setDefPolylineBreak(PolylineBreak pb) {
        _defPolylineBreak = pb;
    }

    /**
     * Get default polygon break
     *
     * @return Default polygon break
     */
    public PolygonBreak getDefPolygonBreak() {
        return _defPolygonBreak;
    }

    /**
     * Set default polygon break
     *
     * @param pb Default polygon break
     */
    public void setDefPolygonBreak(PolygonBreak pb) {
        _defPolygonBreak = pb;
    }

    /**
     * Get if draw identifer shape
     *
     * @return Booleab
     */
    public boolean isDrawIdentiferShape() {
        return this._drawIdentiferShape;
    }

    /**
     * Set if draw identifer shape
     *
     * @param istrue boolean
     */
    public void setDrawIdentiferShape(boolean istrue) {
        this._drawIdentiferShape = istrue;
    }

    /**
     * Get if draw grid tick line
     *
     * @return Boolean
     */
    public boolean isDrawGridTickLine() {
        return _drawGridTickLine;
    }

    /**
     * Set if draw grid tick line
     *
     * @param istrue Boolean
     */
    public void setDrawGridTickLine(boolean istrue) {
        _drawGridTickLine = istrue;
    }

    /**
     * Get grid labels
     *
     * @return Grid labels
     */
    public List<GridLabel> getGridLabels() {
        return _gridLabels;
    }

    /**
     * Get grid line color
     *
     * @return Grid line color
     */
    public Color getGridLineColor() {
        return _gridLineColor;
    }

    /**
     * Set grid line color
     *
     * @param color The color
     */
    public void setGridLineColor(Color color) {
        _gridLineColor = color;
    }

    /**
     * Get grid line size
     *
     * @return Grid line size
     */
    public float getGridLineSize() {
        return _gridLineSize;
    }

    /**
     * Set grid line size
     *
     * @param size The size
     */
    public void setGridLineSize(float size) {
        _gridLineSize = size;
    }

    /**
     * Get grid line style
     *
     * @return Grid line style
     */
    public LineStyles getGridLineStyle() {
        return _gridLineStyle;
    }

    /**
     * Set grid line style
     *
     * @param style
     */
    public void setGridLineStyle(LineStyles style) {
        _gridLineStyle = style;
    }

    /**
     * Get if draw grid line
     *
     * @return Boolean
     */
    public boolean isDrawGridLine() {
        return _drawGridLine;
    }

    /**
     * Set if draw grid line
     *
     * @param istrue
     */
    public void setDrawGridLine(boolean istrue) {
        _drawGridLine = istrue;
    }

    /**
     * Get grid x/longitude delt
     *
     * @return Grid x delt
     */
    public float getGridXDelt() {
        return _gridXDelt;
    }

    /**
     * Set grid x/longitude delt
     *
     * @param delt Grid x delt
     */
    public void setGridXDelt(float delt) {
        _gridXDelt = delt;
        _gridDeltChanged = true;
    }

    /**
     * Get grid y/latitude delt
     *
     * @return Grid y delt
     */
    public float getGridYDelt() {
        return _gridYDelt;
    }

    /**
     * Set grid y/latitude delt
     *
     * @param delt Grid y delta
     */
    public void setGridYDelt(float delt) {
        _gridYDelt = delt;
        _gridDeltChanged = true;
    }

    /**
     * Get grid x/longitude origin
     *
     * @return Grid x origin
     */
    public float getGridXOrigin() {
        return _gridXOrigin;
    }

    /**
     * Set grid x/longitude origin
     *
     * @param origin Grid x origin
     */
    public void setGridXOrigin(float origin) {
        _gridXOrigin = origin;
        _gridDeltChanged = true;
    }

    /**
     * Get grid y/latitude origin
     *
     * @return Grid y origin
     */
    public float getGridYOrigin() {
        return _gridYOrigin;
    }

    /**
     * Set grid y/latitude origin
     *
     * @param origin Grid y origin
     */
    public void setGridYOrigin(float origin) {
        _gridYOrigin = origin;
        _gridDeltChanged = true;
    }

    /**
     * Get X grid labels
     *
     * @return X grid labels
     */
    public List<String> getXGridStrs() {
        return _xGridStrs;
    }

    /**
     * Set X grid labels
     *
     * @param value X grid lables
     */
    public void setXGridStrs(List<String> value) {
        _xGridStrs = value;
    }

    /**
     * Get Y grid labels
     *
     * @return Y grid labels
     */
    public List<String> getYGridStrs() {
        return _yGridStrs;
    }

    /**
     * Set Y grid labels
     *
     * @param value Y grid labels
     */
    public void setYGridStrs(List<String> value) {
        _yGridStrs = value;
    }

    /**
     * Get graphic collection
     *
     * @return The graphic collection
     */
    public GraphicCollection getGraphicCollection() {
        return this._graphicCollection;
    }

    /**
     * Set graphic collection
     *
     * @param aGCollection The graphic collection
     */
    public void setGraphicCollection(GraphicCollection aGCollection) {
        _graphicCollection = aGCollection;
    }

    /**
     * Get lon/lat layer
     *
     * @return The lon/lat layer
     */
    public VectorLayer getLonLatLayer() {
        return _lonLatLayer;
    }

    /**
     * Set lon/lat layer
     *
     * @param layer The lon/lat layer
     */
    public void setLonLatLayer(VectorLayer layer) {
        _lonLatLayer = layer;
    }

    /**
     * Get lon/lat projected layer
     *
     * @return The lon/lat projected layer
     */
    public VectorLayer getLonLatProjLayer() {
        return _lonLatProjLayer;
    }

    /**
     * Set lon/lat projected layer
     *
     * @param layer The lon/lat projected layer
     */
    public void setLonLatProjLayer(VectorLayer layer) {
        _lonLatProjLayer = layer;
    }

    /**
     * Get measurement form
     *
     * @return Measurement form
     */
    public FrmMeasurement getMeasurementForm() {
        return _frmMeasure;
    }

    /**
     * set measurement form
     *
     * @param form Measurement form
     */
    public void setMeasurementForm(FrmMeasurement form) {
        _frmMeasure = form;
    }

    /**
     * Get maskout
     *
     * @return Maskout
     */
    public MaskOut getMaskOut() {
        return _maskOut;
    }

    /**
     * Set maskout
     *
     * @param value Maskout
     */
    public void setMaskOut(MaskOut value) {
        _maskOut = value;
    }

    /**
     * Get view image
     *
     * @return View image
     */
    public BufferedImage getViewImage() {
        return this._mapBitmap;
    }

    // </editor-fold>
    // <editor-fold desc="Events">
    public void addViewExtentChangedListener(IViewExtentChangedListener listener) {
        this._listeners.add(IViewExtentChangedListener.class, listener);
    }

    public void removeViewExtentChangedListener(IViewExtentChangedListener listener) {
        this._listeners.remove(IViewExtentChangedListener.class, listener);
    }

    public void fireViewExtentChangedEvent() {
        fireViewExtentChangedEvent(new ViewExtentChangedEvent(this));
    }

    private void fireViewExtentChangedEvent(ViewExtentChangedEvent event) {
        Object[] listeners = _listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IViewExtentChangedListener.class) {
                ((IViewExtentChangedListener) listeners[i + 1]).viewExtentChangedEvent(event);
            }
        }
    }

    public void addLayersUpdatedListener(ILayersUpdatedListener listener) {
        this._listeners.add(ILayersUpdatedListener.class, listener);
    }

    public void removeLayersUpdatedListener(ILayersUpdatedListener listener) {
        this._listeners.remove(ILayersUpdatedListener.class, listener);
    }

    public void fireLayersUpdatedEvent() {
        fireLayersUpdatedEvent(new LayersUpdatedEvent(this));
    }

    private void fireLayersUpdatedEvent(LayersUpdatedEvent event) {
        Object[] listeners = _listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ILayersUpdatedListener.class) {
                ((ILayersUpdatedListener) listeners[i + 1]).layersUpdatedEvent(event);
            }
        }
    }

    public void addGraphicSelectedListener(IGraphicSelectedListener listener) {
        this._listeners.add(IGraphicSelectedListener.class, listener);
    }

    public void removeGraphicSelectedListener(IGraphicSelectedListener listener) {
        this._listeners.remove(IGraphicSelectedListener.class, listener);
    }

    public void fireGraphicSelectedEvent() {
        fireGraphicSelectedEvent(new GraphicSelectedEvent(this));
    }

    private void fireGraphicSelectedEvent(GraphicSelectedEvent event) {
        Object[] listeners = _listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IGraphicSelectedListener.class) {
                ((IGraphicSelectedListener) listeners[i + 1]).graphicSelectedEvent(event);
            }
        }
    }

    public void addProjectionChangedListener(IProjectionChangedListener listener) {
        this._listeners.add(IProjectionChangedListener.class, listener);
    }

    public void removeViewExtentChangedListener(IProjectionChangedListener listener) {
        this._listeners.remove(IProjectionChangedListener.class, listener);
    }

    public void fireProjectionChangedEvent() {
        fireProjectionChangedEvent(new ProjectionChangedEvent(this));
    }

    private void fireProjectionChangedEvent(ProjectionChangedEvent event) {
        Object[] listeners = _listeners.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == IProjectionChangedListener.class) {
                ((IProjectionChangedListener) listeners[i + 1]).projectionChangedEvent(event);
            }
        }
    }

    void onComponentResized(ComponentEvent e) {
        this.zoomToExtent(this.getViewExtent());
    }

    void onMousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            Graphics2D g = (Graphics2D) this.getGraphics();
            switch (_mouseTool) {
                case Zoom_In:
                    break;
                case Pan:
                    break;
                case SelectElements:
                    PointF mousePoint = new PointF();
                    mousePoint.X = e.getX();
                    mousePoint.Y = e.getY();
                    double lonShift = 0;

                    GraphicCollection tempGraphics = new GraphicCollection();
                    if (selectGraphics(mousePoint, _selectedGraphics, tempGraphics, lonShift, 3)) {
                        //_isInSelectedGraphics = true;
                        _selectedRectangle = getGraphicRectangle(g, _selectedGraphics.get(0), lonShift);
                        _resizeRectangle = _selectedRectangle;
                        if (_resizeSelectedEdge == Edge.None) {
                            _mouseTool = MouseTools.MoveSelection;
                        } else {
                            _mouseTool = MouseTools.ResizeSelection;
                        }
                    } else {
                        _mouseTool = MouseTools.CreateSelection;
                    }

                    break;
                case New_Point:
                    PointShape aPS = new PointShape();
                    float[] pXY = screenToProj(e.getX(), e.getY());
                    aPS.setPoint(new PointD(pXY[0], pXY[1]));
                    Graphic aGraphic = new Graphic();
                    aGraphic.setShape(aPS);
                    aGraphic.setLegend((PointBreak) _defPointBreak.clone());
                    _graphicCollection.add(aGraphic);
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    drawGraphic(g, aGraphic, 0);
                    break;
                case New_Label:
                    pXY = screenToProj(e.getX(), e.getY());
                    aPS = new PointShape();
                    aPS.setPoint(new PointD(pXY[0], pXY[1]));
                    aGraphic = new Graphic(aPS, (LabelBreak) _defLabelBreak.clone());
                    _graphicCollection.add(aGraphic);
                    drawGraphic(g, aGraphic, 0);
                    break;
                case New_Polyline:
                case New_Polygon:
                case New_Curve:
                case New_CurvePolygon:
                case New_Freehand:
                    if (_startNewGraphic) {
                        _graphicPoints = new ArrayList<PointF>();
                        _startNewGraphic = false;
                    }
                    _graphicPoints.add(new PointF(e.getX(), e.getY()));
                    break;
                case EditVertices:
                    if (_selectedGraphics.size() > 0) {
                        _editingVerticeIndex = selectEditVertices(new Point(e.getX(), e.getY()), _selectedGraphics.get(0).getShape(),
                                _editingVertices);
                        if (_editingVerticeIndex >= 0) {
                            _mouseTool = MouseTools.InEditingVertices;
                        }
                    }
                    break;
                case Measurement:
                    if (_frmMeasure == null) {
                        break;
                    }
                    if (_frmMeasure.isVisible()) {
                        switch (_frmMeasure.getMeasureType()) {
                            case Length:
                            case Area:
                                if (_startNewGraphic) {
                                    _graphicPoints = new ArrayList<PointF>();
                                    _startNewGraphic = false;
                                }
                                _frmMeasure.setPreviousValue(_frmMeasure.getTotalValue());
                                _graphicPoints.add(new PointF(e.getX(), e.getY()));
                                break;
                            case Feature:
                                MapLayer aMLayer = getLayerFromHandle(_selectedLayer);
                                if (aMLayer != null) {
                                    if (aMLayer.getLayerType() == LayerTypes.VectorLayer) {
                                        VectorLayer aLayer = (VectorLayer) aMLayer;
                                        if (aLayer.getShapeType() != ShapeTypes.Point) {
                                            PointF aPoint = new PointF(e.getX(), e.getY());
                                            List<Integer> selectedShapes = selectShapes(aLayer, aPoint);
                                            if (selectedShapes.size() > 0) {
                                                Shape aShape = aLayer.getShapes().get(selectedShapes.get(0));
                                                aLayer.setIdentiferShape(selectedShapes.get(0));
                                                _drawIdentiferShape = true;
                                                this.repaint();
                                                //drawIdShape(g, aShape);
                                                double value = 0.0;
                                                switch (aShape.getShapeType()) {
                                                    case Polyline:
                                                    case PolylineZ:
                                                        _frmMeasure.setArea(false);
                                                        if (_projection.isLonLatMap()) {
                                                            value = GeoComputation.getDistance(((PolylineShape) aShape).getPoints(), true);
                                                        } else {
                                                            value = ((PolylineShape) aShape).getLength();
                                                            value *= _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres();
                                                        }
                                                        break;
                                                    case Polygon:
                                                    case PolygonM:
                                                        _frmMeasure.setArea(true);
                                                        if (_projection.isLonLatMap()) {
                                                            value = ((PolygonShape) aShape).getSphericalArea();
                                                        } else {
                                                            value = ((PolygonShape) aShape).getArea();
                                                        }
                                                        value *= _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres()
                                                                * _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres();
                                                        break;
                                                }
                                                _frmMeasure.setCurrentValue(value);
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    break;
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) {
            switch (_mouseTool) {
                case Measurement:
                    if (_frmMeasure.isVisible()) {
                        switch (_frmMeasure.getMeasureType()) {
                            case Length:
                            case Area:
                                _startNewGraphic = true;
                                _frmMeasure.setTotalValue(0);
                                break;
                        }
                    }
                    break;
            }
        }

        _mouseDownPoint.x = e.getX();
        _mouseDownPoint.y = e.getY();
        _mouseLastPos = (Point) _mouseDownPoint.clone();
    }

    void onMouseDragged(MouseEvent e) {
        this._dragMode = true;
        int deltaX = e.getX() - _mouseLastPos.x;
        int deltaY = e.getY() - _mouseLastPos.y;
        _mouseLastPos.x = e.getX();
        _mouseLastPos.y = e.getY();

        Graphics2D g = (Graphics2D) this.getGraphics();
        int aWidth, aHeight, aX, aY;
        g.setColor(this.getForeground());
        switch (_mouseTool) {
            case Zoom_In:
                this.repaint();
                break;
            case Pan:
                _xShift = e.getX() - _mouseDownPoint.x;
                _yShift = e.getY() - _mouseDownPoint.y;
                this.repaint();
                break;
            case CreateSelection:
                this.repaint();
                break;
            case MoveSelection:
                //Move selected graphics
                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                this.repaint();
                break;
            case ResizeSelection:
                Graphic aGraphic = _selectedGraphics.get(0);
                if (_selectedRectangle.width > 2 && _selectedRectangle.height > 2) {
                    switch (aGraphic.getResizeAbility()) {
                        case SameWidthHeight:
                            //deltaY = deltaX;
                            switch (_resizeSelectedEdge) {
                                case TopLeft:
                                    _resizeRectangle.x += deltaX;
                                    _resizeRectangle.y += deltaX;
                                    _resizeRectangle.width -= deltaX;
                                    _resizeRectangle.height -= deltaX;
                                    break;
                                case BottomRight:
                                    _resizeRectangle.width += deltaX;
                                    _resizeRectangle.height += deltaX;
                                    break;
                                case TopRight:
                                    _resizeRectangle.y += deltaY;
                                    _resizeRectangle.width -= deltaY;
                                    _resizeRectangle.height -= deltaY;
                                    break;
                                case BottomLeft:
                                    _resizeRectangle.x += deltaX;
                                    _resizeRectangle.width -= deltaX;
                                    _resizeRectangle.height -= deltaX;
                                    break;
                            }
                            break;
                        case ResizeAll:
                            switch (_resizeSelectedEdge) {
                                case TopLeft:
                                    _resizeRectangle.x += deltaX;
                                    _resizeRectangle.y += deltaY;
                                    _resizeRectangle.width -= deltaX;
                                    _resizeRectangle.height -= deltaY;
                                    break;
                                case BottomRight:
                                    _resizeRectangle.width += deltaX;
                                    _resizeRectangle.height += deltaY;
                                    break;
                                case Top:
                                    _resizeRectangle.y += deltaY;
                                    _resizeRectangle.height -= deltaY;
                                    break;
                                case Bottom:
                                    _resizeRectangle.height += deltaY;
                                    break;
                                case TopRight:
                                    _resizeRectangle.y += deltaY;
                                    _resizeRectangle.width += deltaX;
                                    _resizeRectangle.height -= deltaY;
                                    break;
                                case BottomLeft:
                                    _resizeRectangle.x += deltaX;
                                    _resizeRectangle.width -= deltaX;
                                    _resizeRectangle.height += deltaY;
                                    break;
                                case Left:
                                    _resizeRectangle.x += deltaX;
                                    _resizeRectangle.width -= deltaX;
                                    break;
                                case Right:
                                    _resizeRectangle.width += deltaX;
                                    break;
                            }
                            break;
                    }
                } else {
                    _resizeRectangle.width = 3;
                    _resizeRectangle.height = 3;
                }
                this.repaint();
                break;
            case New_Rectangle:
            case New_Ellipse:
            case New_Freehand:
            case New_Circle:
                this.repaint();
                break;
            case InEditingVertices:
                this.repaint();
                break;
        }
    }

    void onMouseMoved(MouseEvent e) {
        int deltaX = e.getX() - _mouseLastPos.x;
        int deltaY = e.getY() - _mouseLastPos.y;
        _mouseLastPos.x = e.getX();
        _mouseLastPos.y = e.getY();

        Graphics2D g = (Graphics2D) this.getGraphics();
        float aWidth, aHeight, aX, aY;
        g.setColor(this.getForeground());
        switch (_mouseTool) {
            case SelectElements:
                if (_selectedGraphics.size() > 0) {

                    GraphicCollection tempGraphics = new GraphicCollection();
                    double lonShift = 0;
                    if (selectGraphics(new PointF(e.getX(), e.getY()), _selectedGraphics, tempGraphics, lonShift, 3)) {
                        //Change mouse cursor
                        Rectangle aRect = getGraphicRectangle(g, _selectedGraphics.get(0), lonShift);
                        _resizeSelectedEdge = intersectElementEdge(aRect, new PointF(e.getX(), e.getY()), 3F);
                        switch (_selectedGraphics.get(0).getResizeAbility()) {
                            case SameWidthHeight:
                                switch (_resizeSelectedEdge) {
                                    case TopLeft:
                                    case BottomRight:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeNWSE;
                                        break;
                                    case TopRight:
                                    case BottomLeft:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeNESW;
                                        break;
                                    default:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                        //this.Cursor = Cursors.SizeAll;
                                        break;
                                }
                                break;
                            case ResizeAll:
                                switch (_resizeSelectedEdge) {
                                    case TopLeft:
                                    case BottomRight:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeNWSE;
                                        break;
                                    case Top:
                                    case Bottom:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeNS;
                                        break;
                                    case TopRight:
                                    case BottomLeft:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeNESW;
                                        break;
                                    case Left:
                                    case Right:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                                        //this.Cursor = Cursors.SizeWE;
                                        break;
                                    case None:
                                        this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                        //this.Cursor = Cursors.SizeAll;
                                        break;
                                }
                                break;
                            default:
                                this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                                //this.Cursor = Cursors.SizeAll;
                                break;
                        }
                    } else {
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }

                } else {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
                break;
            case New_Polyline:
            case New_Polygon:
            case New_Curve:
            case New_CurvePolygon:
            case New_Freehand:
                if (!_startNewGraphic) {
                    this.repaint();
                }
                break;
            case EditVertices:
                if (_selectedGraphics.size() > 0) {
                    _editingVerticeIndex = selectEditVertices(new Point(e.getX(), e.getY()), _selectedGraphics.get(0).getShape(),
                            _editingVertices);
                    if (_editingVerticeIndex >= 0) {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        Image image = toolkit.getImage(this.getClass().getResource("/org/meteoinfo/resources/VertexEdit_32x32x32.png"));
                        this.setCursor(toolkit.createCustomCursor(image, new Point(8, 8), "Vertices edit"));
                    } else {
                        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                break;
            case Measurement:
                if (_frmMeasure == null) {
                    break;
                }
                if (_frmMeasure.isVisible()) {
                    switch (_frmMeasure.getMeasureType()) {
                        case Length:
                        case Area:
                            if (!_startNewGraphic) {
                                //Draw graphic                                    
                                //g.SmoothingMode = SmoothingMode.AntiAlias;
                                this.repaint();
                                PointF[] fpoints = (PointF[]) _graphicPoints.toArray(new PointF[_graphicPoints.size()]);
                                PointF[] points = new PointF[fpoints.length + 1];
                                System.arraycopy(fpoints, 0, points, 0, fpoints.length);
                                points[_graphicPoints.size()] = new PointF(e.getX(), e.getY());

                                //Calculate             
                                float pXY[] = screenToProj(e.getX(), e.getY());
                                if (_frmMeasure.getMeasureType() == MeasureTypes.Length) {
                                    float[] ppXY = screenToProj(_mouseDownPoint.x, _mouseDownPoint.y);
                                    double dx = Math.abs(pXY[0] - ppXY[0]);
                                    double dy = Math.abs(pXY[1] - ppXY[1]);
                                    double dist;
                                    if (_projection.isLonLatMap()) {
                                        double y = (pXY[1] + ppXY[1]) / 2;
                                        double factor = Math.cos(y * Math.PI / 180);
                                        dx *= factor;
                                        dist = Math.sqrt(dx * dx + dy * dy);
                                        dist = dist * 111319.5;
                                    } else {
                                        dist = Math.sqrt(dx * dx + dy * dy);
                                        dist *= _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres();
                                    }

                                    _frmMeasure.setCurrentValue(dist);
                                } else {
                                    List<PointD> mPoints = new ArrayList<PointD>();
                                    for (int i = 0; i < points.length; i++) {
                                        pXY = screenToProj(points[i].X, points[i].Y);
                                        mPoints.add(new PointD(pXY[0], pXY[1]));
                                    }
                                    double area = GeoComputation.getArea(mPoints);
                                    if (_projection.isLonLatMap()) {
                                        area = area * 111319.5 * 111319.5;
                                    } else {
                                        area *= _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres()
                                                * _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getFromMetres();
                                    }
                                    _frmMeasure.setCurrentValue(area);
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }

    void onMouseReleased(MouseEvent e) {
        this._dragMode = false;
        double MinX, MaxX, MinY, MaxY, lonRan, latRan, ZoomF, ZoomFY;
        double mouseLon, mouseLat, lon, lat;
        lonRan = _drawExtent.maxX - _drawExtent.minX;
        latRan = _drawExtent.maxY - _drawExtent.minY;
        mouseLon = _drawExtent.minX + e.getX() / _scaleX;
        mouseLat = _drawExtent.maxY - e.getY() / _scaleY;

        _mousePos.x = e.getX();
        _mousePos.y = e.getY();
        switch (_mouseTool) {
            case Zoom_In:
                if (Math.abs(_mousePos.x - _mouseDownPoint.x) > 5) {
                    ZoomF = Math.abs(_mousePos.x - _mouseDownPoint.x) / (double) this.getWidth();
                    ZoomFY = Math.abs(_mousePos.y - _mouseDownPoint.y) / (double) this.getHeight();
                    if (_isGeoMap) {
                        if (ZoomF < ZoomFY) {
                            ZoomF = ZoomFY;
                        } else {
                            ZoomFY = ZoomF;
                        }
                    }
                    mouseLon = _drawExtent.minX + ((_mouseDownPoint.x + (_mousePos.x - _mouseDownPoint.x) / 2)) / _scaleX;
                    mouseLat = _drawExtent.maxY - ((_mouseDownPoint.y + (_mousePos.y - _mouseDownPoint.y) / 2)) / _scaleY;
                    MinX = mouseLon - (lonRan / 2 * ZoomF);
                    MaxX = mouseLon + (lonRan / 2 * ZoomF);
                    MinY = mouseLat - (latRan / 2 * ZoomFY);
                    MaxY = mouseLat + (latRan / 2 * ZoomFY);
                } else {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        ZoomF = 0.75;
                    } else {
                        ZoomF = 1.5;
                    }
                    MinX = mouseLon - (lonRan / 2 * ZoomF);
                    MaxX = mouseLon + (lonRan / 2 * ZoomF);
                    MinY = mouseLat - (latRan / 2 * ZoomF);
                    MaxY = mouseLat + (latRan / 2 * ZoomF);
                }

                if (MaxX - MinX > 0.001) {
                    zoomToExtent(MinX, MaxX, MinY, MaxY);
                }
                break;
            case Zoom_Out:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    ZoomF = 1.5;
                } else {
                    ZoomF = 0.75;
                }
                MinX = mouseLon - (lonRan / 2 * ZoomF);
                MaxX = mouseLon + (lonRan / 2 * ZoomF);
                MinY = mouseLat - (latRan / 2 * ZoomF);
                MaxY = mouseLat + (latRan / 2 * ZoomF);

                zoomToExtent(MinX, MaxX, MinY, MaxY);
                break;
            case Pan:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    _xShift = 0;
                    _yShift = 0;

                    lon = _drawExtent.minX + _mouseDownPoint.x / _scaleX;
                    lat = _drawExtent.maxY - _mouseDownPoint.y / _scaleY;
                    MinX = _drawExtent.minX - (mouseLon - lon);
                    MaxX = _drawExtent.maxX - (mouseLon - lon);
                    MinY = _drawExtent.minY - (mouseLat - lat);
                    MaxY = _drawExtent.maxY - (mouseLat - lat);

                    zoomToExtent(MinX, MaxX, MinY, MaxY);
                }
                break;
            case CreateSelection:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (!(e.isControlDown() || e.isShiftDown())) {
                        //Remove selected graphics
                        for (Graphic aGraphic : _selectedGraphics) {
                            aGraphic.getShape().setSelected(false);
                        }
                        _selectedGraphics.clear();
                    }

                    //Select graphics
                    if (Math.abs(e.getX() - _mouseDownPoint.x) > 5 || Math.abs(e.getY() - _mouseDownPoint.y) > 5) {
                        int minx = Math.min(_mouseDownPoint.x, e.getX());
                        int miny = Math.min(_mouseDownPoint.y, e.getY());
                        int width = Math.abs(e.getX() - _mouseDownPoint.x);
                        int height = Math.abs(e.getY() - _mouseDownPoint.y);
                        Rectangle rect = new Rectangle(minx, miny, width, height);
                        double lonShift = 0;
                        GraphicCollection tempGraphics = new GraphicCollection();
                        if (selectGraphics(rect, tempGraphics, lonShift)) {
                            if (!(e.isControlDown() || e.isShiftDown())) {
                                for (Graphic aGraphic : tempGraphics) {
                                    aGraphic.getShape().setSelected(true);
                                    _selectedGraphics.add(aGraphic);
                                }
                            } else {
                                for (Graphic aGraphic : tempGraphics) {
                                    aGraphic.getShape().setSelected(!aGraphic.getShape().isSelected());
                                    if (aGraphic.getShape().isSelected()) {
                                        _selectedGraphics.add(aGraphic);
                                    } else {
                                        _selectedGraphics.remove(aGraphic);
                                    }
                                }
                            }
                        }

                        paintLayers();
                        return;
                    } else {
                        PointF mousePoint = new PointF(_mouseDownPoint.x, _mouseDownPoint.y);
                        double lonShift = 0;
                        GraphicCollection tempGraphics = new GraphicCollection();
                        if (selectGraphics(mousePoint, tempGraphics, lonShift)) {
                            Graphic aGraphic = tempGraphics.get(0);
                            if (!(e.isControlDown() || e.isShiftDown())) {
                                aGraphic.getShape().setSelected(true);
                                _selectedGraphics.add(aGraphic);
                            } else {
                                aGraphic.getShape().setSelected(!aGraphic.getShape().isSelected());
                                if (aGraphic.getShape().isSelected()) {
                                    _selectedGraphics.add(aGraphic);
                                } else {
                                    _selectedGraphics.remove(aGraphic);
                                }
                            }

                            //Show symbol form
                            switch (aGraphic.getLegend().getBreakType()) {
                                case PointBreak:
                                    if (_frmPointSymbolSet != null) {
                                        if (_frmPointSymbolSet.isVisible()) {
                                            _frmPointSymbolSet.setPointBreak((PointBreak) aGraphic.getLegend());
                                        }
                                    }
                                    break;
                                case LabelBreak:
                                    if (_frmLabelSymbolSet != null) {
                                        if (_frmLabelSymbolSet.isVisible()) {
                                            _frmLabelSymbolSet.setLabelBreak((LabelBreak) aGraphic.getLegend());
                                        }
                                    }
                                    break;
                                case PolylineBreak:
                                    if (_frmPolylineSymbolSet != null) {
                                        if (_frmPolylineSymbolSet.isVisible()) {
                                            _frmPolylineSymbolSet.setPolylineBreak((PolylineBreak) aGraphic.getLegend());
                                        }
                                    }
                                    break;
                                case PolygonBreak:
                                    if (_frmPolygonSymbolSet != null) {
                                        if (_frmPolygonSymbolSet.isVisible()) {
                                            _frmPolygonSymbolSet.setPolygonBreak((PolygonBreak) aGraphic.getLegend());
                                        }
                                    }
                                    break;
                            }
                        }
                        this.fireGraphicSelectedEvent();
                    }

                    paintLayers();
                }
                _mouseTool = MouseTools.SelectElements;
                break;
            case MoveSelection:
                if (_mouseDoubleClicked) {
                    _mouseDoubleClicked = false;
                } else {
                    if (_selectedGraphics.size() > 0) {
                        if (Math.abs(e.getX() - _mouseDownPoint.x) < 2 && Math.abs(e.getY() - _mouseDownPoint.y) < 2) {
                            Graphic aGraphic = _selectedGraphics.get(0);
                            PointF mousePoint = new PointF(_mouseDownPoint.x, _mouseDownPoint.y);
                            double lonShift = 0;
                            GraphicCollection tempGraphics = new GraphicCollection();
                            selectGraphics(mousePoint, tempGraphics, lonShift);
                            if (e.isControlDown() || e.isShiftDown()) {
                                if (tempGraphics.size() > 0) {
                                    aGraphic = tempGraphics.get(0);
                                    aGraphic.getShape().setSelected(!aGraphic.getShape().isSelected());
                                    if (aGraphic.getShape().isSelected()) {
                                        _selectedGraphics.add(aGraphic);
                                    } else {
                                        _selectedGraphics.remove(aGraphic);
                                    }
                                }
                            } else {
                                if (tempGraphics.size() > 1) {
                                    aGraphic.getShape().setSelected(false);
                                    int idx = tempGraphics.indexOf(aGraphic);
                                    if (idx == 0) {
                                        idx = tempGraphics.size() - 1;
                                    } else {
                                        idx -= 1;
                                    }
                                    aGraphic = tempGraphics.get(idx);
                                    _selectedGraphics.clear();
                                    _selectedGraphics.add(aGraphic);
                                    _selectedGraphics.get(0).getShape().setSelected(true);

                                    //Show symbol form
                                    switch (aGraphic.getLegend().getBreakType()) {
                                        case PointBreak:
                                            if (_frmPointSymbolSet != null) {
                                                if (_frmPointSymbolSet.isVisible()) {
                                                    _frmPointSymbolSet.setPointBreak((PointBreak) aGraphic.getLegend());
                                                }
                                            }
                                            break;
                                        case LabelBreak:
                                            if (_frmLabelSymbolSet != null) {
                                                if (_frmLabelSymbolSet.isVisible()) {
                                                    _frmLabelSymbolSet.setLabelBreak((LabelBreak) aGraphic.getLegend());
                                                }
                                            }
                                            break;
                                        case PolylineBreak:
                                            if (_frmPolylineSymbolSet != null) {
                                                if (_frmPolylineSymbolSet.isVisible()) {
                                                    _frmPolylineSymbolSet.setPolylineBreak((PolylineBreak) aGraphic.getLegend());
                                                }
                                            }
                                            break;
                                        case PolygonBreak:
                                            if (_frmPolygonSymbolSet != null) {
                                                if (_frmPolygonSymbolSet.isVisible()) {
                                                    _frmPolygonSymbolSet.setPolygonBreak((PolygonBreak) aGraphic.getLegend());
                                                }
                                            }
                                            break;
                                    }
                                }
                                this.fireGraphicSelectedEvent();
                            }
                        } else {
                            Graphic aGraphic = _selectedGraphics.get(0);
                            Shape aShape = aGraphic.getShape();
                            moveShapeOnScreen(aShape, _mouseDownPoint, new Point(e.getX(), e.getY()));
                            aGraphic.setShape(aShape);

                            _selectedGraphics.remove(aGraphic);
                            _selectedGraphics.add(0, aGraphic);
                        }

                        paintLayers();
                    }
                }
                _mouseTool = MouseTools.SelectElements;
                break;
            case ResizeSelection:
                Graphic aG = _selectedGraphics.get(0);
                Shape shape = aG.getShape();
                resizeShapeOnScreen(shape, aG.getLegend(), _resizeRectangle);
                aG.setShape(shape);

                _selectedGraphics.remove(aG);
                _selectedGraphics.add(0, aG);

                paintLayers();

                _mouseTool = MouseTools.SelectElements;
                break;
            case New_Rectangle:
            case New_Ellipse:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (Math.abs(e.getX() - _mouseDownPoint.x) < 2 || Math.abs(e.getY() - _mouseDownPoint.y) < 2) {
                        return;
                    }

                    _startNewGraphic = true;
                    _graphicPoints = new ArrayList<PointF>();
                    _graphicPoints.add(new PointF(_mouseDownPoint.x, _mouseDownPoint.y));
                    _graphicPoints.add(new PointF(_mouseDownPoint.x, e.getY()));
                    _graphicPoints.add(new PointF(e.getX(), e.getY()));
                    _graphicPoints.add(new PointF(e.getX(), _mouseDownPoint.y));
                    List<PointD> points = new ArrayList<PointD>();
                    float[] pXY;
                    for (PointF aPoint : _graphicPoints) {
                        pXY = screenToProj(aPoint.X, aPoint.Y);
                        points.add(new PointD(pXY[0], pXY[1]));
                    }

                    Graphic aGraphic = null;
                    switch (_mouseTool) {
                        case New_Rectangle:
                            RectangleShape aPGS = new RectangleShape();
                            points.add((PointD) points.get(0).clone());
                            aPGS.setPoints(points);
                            aGraphic = new Graphic(aPGS, (PolygonBreak) _defPolygonBreak.clone());
                            break;
                        case New_Ellipse:
                            EllipseShape aES = new EllipseShape();
                            aES.setPoints(points);
                            aGraphic = new Graphic(aES, (PolygonBreak) _defPolygonBreak.clone());
                            break;
                    }

                    if (aGraphic != null) {
                        _graphicCollection.add(aGraphic);
                        paintLayers();
                    } else {
                        this.repaint();
                    }
                }
                break;
            case New_Freehand:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    _startNewGraphic = true;
                    if (_graphicPoints.size() < 2) {
                        break;
                    }

                    List<PointD> points = new ArrayList<PointD>();
                    float[] pXY;
                    for (PointF aPoint : _graphicPoints) {
                        pXY = screenToProj(aPoint.X, aPoint.Y);
                        points.add(new PointD(pXY[0], pXY[1]));
                    }

                    Graphic aGraphic;
                    PolylineShape aPLS = new PolylineShape();
                    aPLS.setPoints(points);
                    aGraphic = new Graphic(aPLS, (PolylineBreak) _defPolylineBreak.clone());

                    if (aGraphic != null) {
                        _graphicCollection.add(aGraphic);
                        paintLayers();
                    } else {
                        this.repaint();
                    }
                }
                break;
            case New_Circle:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getX() - _mouseDownPoint.x < 2 || e.getY() - _mouseDownPoint.y < 2) {
                        return;
                    }

                    float radius = (float) Math.sqrt(Math.pow(e.getX() - _mouseDownPoint.x, 2)
                            + Math.pow(e.getY() - _mouseDownPoint.y, 2));
                    _startNewGraphic = true;
                    _graphicPoints = new ArrayList<PointF>();
                    _graphicPoints.add(new PointF(_mouseDownPoint.x - radius, _mouseDownPoint.y));
                    _graphicPoints.add(new PointF(_mouseDownPoint.x, _mouseDownPoint.y - radius));
                    _graphicPoints.add(new PointF(_mouseDownPoint.x + radius, _mouseDownPoint.y));
                    _graphicPoints.add(new PointF(_mouseDownPoint.x, _mouseDownPoint.y + radius));
                    List<PointD> points = new ArrayList<PointD>();
                    float[] pXY;
                    for (PointF aPoint : _graphicPoints) {
                        pXY = screenToProj(aPoint.X, aPoint.Y);
                        points.add(new PointD(pXY[0], pXY[1]));
                    }

                    Graphic aGraphic;
                    CircleShape aPGS = new CircleShape();
                    aPGS.setPoints(points);
                    aGraphic = new Graphic(aPGS, (PolygonBreak) _defPolygonBreak.clone());

                    if (aGraphic != null) {
                        _graphicCollection.add(aGraphic);
                        paintLayers();
                    } else {
                        this.repaint();
                    }
                }
                break;
            case InEditingVertices:
                float[] pXY = screenToProj(e.getX(), e.getY());
                _selectedGraphics.get(0).verticeEditUpdate(_editingVerticeIndex, pXY[0], pXY[1]);

                _mouseTool = MouseTools.EditVertices;
                paintLayers();
                break;
        }
    }

    private void showSymbolSetForm(ColorBreak aCB) {
        switch (aCB.getBreakType()) {
            case PointBreak:
                PointBreak aPB = (PointBreak) aCB;

                if (_frmPointSymbolSet == null) {
                    _frmPointSymbolSet = new FrmPointSymbolSet((JFrame) SwingUtilities.getWindowAncestor(this), false, this);
                    _frmPointSymbolSet.setLocationRelativeTo(this);
                    _frmPointSymbolSet.setVisible(true);
                }
                _frmPointSymbolSet.setPointBreak(aPB);
                _frmPointSymbolSet.setVisible(true);
                break;
            case LabelBreak:
                LabelBreak aLB = (LabelBreak) aCB;

                if (_frmLabelSymbolSet == null) {
                    _frmLabelSymbolSet = new FrmLabelSymbolSet((JFrame) SwingUtilities.getWindowAncestor(this), false, this);
                    _frmLabelSymbolSet.setLocationRelativeTo(this);
                    _frmLabelSymbolSet.setVisible(true);
                }
                _frmLabelSymbolSet.setLabelBreak(aLB);
                _frmLabelSymbolSet.setVisible(true);
                break;
            case PolylineBreak:
                PolylineBreak aPLB = (PolylineBreak) aCB;

                if (_frmPolylineSymbolSet == null) {
                    _frmPolylineSymbolSet = new FrmPolylineSymbolSet((JFrame) SwingUtilities.getWindowAncestor(this), false, this);
                    _frmPolylineSymbolSet.setLocationRelativeTo(this);
                    _frmPolylineSymbolSet.setVisible(true);
                }
                _frmPolylineSymbolSet.setPolylineBreak(aPLB);
                _frmPolylineSymbolSet.setVisible(true);
                break;
            case PolygonBreak:
                PolygonBreak aPGB = (PolygonBreak) aCB;

                if (_frmPolygonSymbolSet == null) {
                    _frmPolygonSymbolSet = new FrmPolygonSymbolSet((JFrame) SwingUtilities.getWindowAncestor(this), false, this);
                    _frmPolygonSymbolSet.setLocationRelativeTo(this);
                    _frmPolygonSymbolSet.setVisible(true);
                }
                _frmPolygonSymbolSet.setPolygonBreak(aPGB);
                _frmPolygonSymbolSet.setVisible(true);
                break;
        }
    }

    void onMouseClicked(MouseEvent e) {
        int clickTimes = e.getClickCount();
        if (clickTimes == 1) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                switch (_mouseTool) {
                    case Identifer:
                        if (_selectedLayer < 0) {
                            return;
                        }
                        MapLayer aMLayer = getLayerFromHandle(_selectedLayer);
                        if (aMLayer == null) {
                            return;
                        }
                        if (aMLayer.getLayerType() == LayerTypes.ImageLayer) {
                            return;
                        }

                        PointF aPoint = new PointF(e.getX(), e.getY());
                        if (aMLayer.getLayerType() == LayerTypes.VectorLayer) {
                            VectorLayer aLayer = (VectorLayer) aMLayer;
                            List<Integer> selectedShapes = selectShapes(aLayer, aPoint);
                            if (selectedShapes.size() > 0) {
                                if (_frmIdentifer == null) {
                                    _frmIdentifer = new FrmIdentifer((JFrame) SwingUtilities.getWindowAncestor(this), false, this);
                                    _frmIdentifer.addWindowListener(new WindowAdapter() {
                                        @Override
                                        public void windowClosed(WindowEvent e) {
                                            _drawIdentiferShape = false;
                                            repaint();
                                        }
                                    });
                                }
                                String[] colNames = {"Field", "Value"};
                                String fieldStr, valueStr;
                                int shapeIdx = selectedShapes.get(0);
                                aLayer.setIdentiferShape(shapeIdx);
                                _drawIdentiferShape = true;

                                Object[][] tData = new Object[aLayer.getFieldNumber() + 1][2];
                                fieldStr = "Index";
                                valueStr = String.valueOf(shapeIdx);
                                tData[0][0] = fieldStr;
                                tData[0][1] = valueStr;
                                if (aLayer.getShapeNum() > 0) {
                                    for (int i = 0; i < aLayer.getFieldNumber(); i++) {
                                        fieldStr = aLayer.getFieldName(i);
                                        valueStr = aLayer.getCellValue(i, shapeIdx).toString();
                                        tData[i + 1][0] = fieldStr;
                                        tData[i + 1][1] = valueStr;
                                    }
                                }
                                DefaultTableModel dtm = new javax.swing.table.DefaultTableModel(tData, colNames) {
                                    @Override
                                    public boolean isCellEditable(int row, int column) {
                                        return false;
                                    }
                                };
                                this._frmIdentifer.getTable().setModel(dtm);
                                this._frmIdentifer.repaint();
                                if (!this._frmIdentifer.isVisible()) {
                                    //this._frmIdentifer.setLocation(e.getX(), e.getY());
                                    this._frmIdentifer.setLocationRelativeTo(this);
                                    this._frmIdentifer.setVisible(true);
                                }

                                this.repaint();
                            }
                        } else if (aMLayer.getLayerType() == LayerTypes.RasterLayer) {
                            RasterLayer aRLayer = (RasterLayer) aMLayer;
                            int[] ijIdx = selectGridCell(aRLayer, aPoint);
                            if (ijIdx != null) {
                                int iIdx = ijIdx[0];
                                int jIdx = ijIdx[1];
                                double aValue = aRLayer.getCellValue(iIdx, jIdx);
                                if (_frmIdentiferGrid == null) {
                                    _frmIdentiferGrid = new FrmIdentiferGrid((JFrame) SwingUtilities.getWindowAncestor(this), false);
                                }

                                _frmIdentiferGrid.setIIndex(iIdx);
                                _frmIdentiferGrid.setJIndex(jIdx);
                                _frmIdentiferGrid.setCellValue(aValue);
                                if (!this._frmIdentiferGrid.isVisible()) {
                                    //this._frmIdentiferGrid.setLocation(e.getX(), e.getY());
                                    this._frmIdentiferGrid.setLocationRelativeTo(this);
                                    this._frmIdentiferGrid.setVisible(true);
                                }
                            }
                        }
                        break;
                    case SelectFeatures:
                        if (_selectedLayer < 0) {
                            return;
                        }
                        aMLayer = getLayerFromHandle(_selectedLayer);
                        if (aMLayer == null) {
                            return;
                        }
                        if (aMLayer.getLayerType() != LayerTypes.VectorLayer) {
                            return;
                        }

                        VectorLayer aLayer = (VectorLayer) aMLayer;
                        if (!e.isControlDown() && !e.isShiftDown()) {
                            aLayer.clearSelectedShapes();
                        }
                        aPoint = new PointF(e.getX(), e.getY());
                        List<Integer> selectedShapes = this.selectShapes(aLayer, aPoint);
                        this._mapBitmap = GlobalUtil.deepCopy(this._tempImage);
                        if (selectedShapes.size() > 0) {
                            int shapeIdx = selectedShapes.get(0);
                            Shape selShape = aLayer.getShapes().get(shapeIdx);
                            if (!e.isControlDown() && !e.isShiftDown()) {
                                selShape.setSelected(true);
                                //this.repaint();
                                drawIdShape((Graphics2D) this._mapBitmap.getGraphics(), selShape);
                            } else {
                                selShape.setSelected(!selShape.isSelected());
                                //this.repaint();
                                for (int sIdx : aLayer.getSelectedShapeIndexes()) {
                                    drawIdShape((Graphics2D) this._mapBitmap.getGraphics(), aLayer.getShapes().get(sIdx));
                                }
                            }
                            this.repaint();
                            //OnShapeSelected();
                        } else {
                            if (!e.isControlDown() && !e.isShiftDown()) {
                                this.repaint();
                            }
                        }
                        break;
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {
                switch (_mouseTool) {
                    case SelectElements:
                    case MoveSelection:
                    case ResizeSelection:
                        if (_selectedGraphics.size() > 0) {
                            Graphic aGraphic = _selectedGraphics.get(0);
                            JPopupMenu jPopupMenu_Graphic = new JPopupMenu();
                            JMenu jMenu_Order = new JMenu("Order");
                            jPopupMenu_Graphic.add(jMenu_Order);

                            JMenuItem jMenuItem_BTF = new JMenuItem("Bring to Front");
                            jMenuItem_BTF.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    onBringToFrontClick(e);
                                }
                            });
                            jMenu_Order.add(jMenuItem_BTF);

                            JMenuItem jMenuItem_STB = new JMenuItem("Send to Back");
                            jMenuItem_STB.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    onSendToBackClick(e);
                                }
                            });
                            jMenu_Order.add(jMenuItem_STB);

                            JMenuItem jMenuItem_BF = new JMenuItem("Bring Forward");
                            jMenuItem_BF.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    onBringForwardClick(e);
                                }
                            });
                            jMenu_Order.add(jMenuItem_BF);

                            JMenuItem jMenuItem_SB = new JMenuItem("Send Backward");
                            jMenuItem_SB.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    onSendBackwardClick(e);
                                }
                            });
                            jMenu_Order.add(jMenuItem_SB);

                            jPopupMenu_Graphic.add(new JSeparator());

                            JMenuItem jMenuItem_Remove = new JMenuItem("Remove");
                            jMenuItem_Remove.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    onRemoveGraphicClick(e);
                                }
                            });
                            jPopupMenu_Graphic.add(jMenuItem_Remove);


                            if (aGraphic.getLegend().getBreakType() == BreakTypes.PolylineBreak || aGraphic.getLegend().getBreakType() == BreakTypes.PolygonBreak) {
                                JMenuItem jMenuItem_Reverse = new JMenuItem("Reverse");
                                jMenuItem_Reverse.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        onReverseGraphicClick(e);
                                    }
                                });
                                jPopupMenu_Graphic.add(jMenuItem_Reverse);

                                if (aGraphic.getShape().getShapeType() == ShapeTypes.Polyline || aGraphic.getShape().getShapeType() == ShapeTypes.Polygon) {
                                    jPopupMenu_Graphic.add(new JSeparator());
                                    JMenuItem jMenuItem_Smooth = new JMenuItem("Smooth Graphic");
                                    jMenuItem_Smooth.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            onGrahpicSmoothClick(e);
                                        }
                                    });
                                    jPopupMenu_Graphic.add(jMenuItem_Smooth);
                                }
                                if (aGraphic.getLegend().getBreakType() == BreakTypes.PolygonBreak) {
                                    jPopupMenu_Graphic.add(new JSeparator());
                                    JMenuItem jMenuItem_Maskout = new JMenuItem("Set Maskout");
                                    if (((PolygonBreak) aGraphic.getLegend()).isMaskout()) {
                                        jMenuItem_Maskout.setText("No Maskout");
                                    }
                                    jMenuItem_Maskout.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            onGraphicMaskoutClick(e);
                                        }
                                    });
                                    jPopupMenu_Graphic.add(jMenuItem_Maskout);
                                }
                            }

                            jPopupMenu_Graphic.show(this, e.getX(), e.getY());
                        }
                        break;
                }
            }
        } else if (clickTimes == 2) {
            switch (_mouseTool) {
                case SelectElements:
                case MoveSelection:
                case ResizeSelection:
                    if (_mouseTool == MouseTools.MoveSelection) {
                        _mouseDoubleClicked = true;
                    }

                    if (_selectedGraphics.size() == 0) {
                        return;
                    }

                    Graphic aGraphic = _selectedGraphics.get(0);

                    //Remove selected graphics
                    for (Graphic aG : _selectedGraphics) {
                        aG.getShape().setSelected(false);
                    }
                    _selectedGraphics.clear();

                    //Select graphics                    
                    PointF mousePoint = new PointF(_mouseDownPoint.x, _mouseDownPoint.y);
                    double lonShift = 0;
                    if (selectGraphics(mousePoint, _selectedGraphics, lonShift)) {
                        if (_selectedGraphics.size() > 1) {
                            aGraphic.getShape().setSelected(false);
                            int idx = _selectedGraphics.indexOf(aGraphic);
                            idx += 2;
                            if (idx > _selectedGraphics.size() - 1) {
                                idx = idx - _selectedGraphics.size();
                            }
                            aGraphic = _selectedGraphics.get(idx);
                            _selectedGraphics.clear();
                            _selectedGraphics.add(aGraphic);
                        }
                        //aGraphic = _selectedGraphics.GraphicList[0];
                        aGraphic.getShape().setSelected(true);
                        this.paintLayers();

                        showSymbolSetForm(aGraphic.getLegend());
                    }
                    break;
                case New_Polyline:
                case New_Polygon:
                case New_Curve:
                case New_CurvePolygon:
                case New_Freehand:
                    if (!_startNewGraphic) {
                        _startNewGraphic = true;
                        //_graphicPoints.Add(new PointF(e.getX(), e.getY()));
                        _graphicPoints.remove(_graphicPoints.size() - 1);
                        List<PointD> points = new ArrayList<PointD>();
                        float[] pXY;
                        for (PointF aPoint : _graphicPoints) {
                            pXY = screenToProj(aPoint.X, aPoint.Y);
                            points.add(new PointD(pXY[0], pXY[1]));
                        }

                        aGraphic = null;
                        switch (_mouseTool) {
                            case New_Polyline:
                            case New_Freehand:
                                PolylineShape aPLS = new PolylineShape();
                                aPLS.setPoints(points);
                                aGraphic = new Graphic(aPLS, (PolylineBreak) _defPolylineBreak.clone());
                                break;
                            case New_Polygon:
                                if (points.size() > 2) {
                                    PolygonShape aPGS = new PolygonShape();
                                    points.add((PointD) points.get(0).clone());
                                    aPGS.setPoints(points);
                                    aGraphic = new Graphic(aPGS, (PolygonBreak) _defPolygonBreak.clone());
                                }
                                break;
                            case New_Curve:
                                CurveLineShape aCLS = new CurveLineShape();
                                aCLS.setPoints(points);
                                aGraphic = new Graphic(aCLS, (PolylineBreak) _defPolylineBreak.clone());
                                break;
                            case New_CurvePolygon:
                                if (points.size() > 2) {
                                    CurvePolygonShape aCPS = new CurvePolygonShape();
                                    points.add((PointD) points.get(0).clone());
                                    aCPS.setPoints(points);
                                    aGraphic = new Graphic(aCPS, (PolygonBreak) _defPolygonBreak.clone());
                                }
                                break;
                        }

                        if (aGraphic != null) {
                            _graphicCollection.add(aGraphic);
                            paintLayers();
                        } else {
                            this.repaint();
                        }
                    }
                    break;
            }
        }
    }

    private void onBringToFrontClick(ActionEvent e) {
        Graphic aG = _selectedGraphics.get(0);
        int idx = _graphicCollection.indexOf(aG);
        if (idx < _graphicCollection.size() - 1) {
            _graphicCollection.remove(aG);
            _graphicCollection.add(aG);
            this.paintLayers();
        }
    }

    private void onSendToBackClick(ActionEvent e) {
        Graphic aG = _selectedGraphics.get(0);
        int idx = _graphicCollection.indexOf(aG);
        if (idx > 0) {
            _graphicCollection.remove(aG);
            _graphicCollection.add(0, aG);
            this.paintLayers();
        }
    }

    private void onBringForwardClick(ActionEvent e) {
        Graphic aG = _selectedGraphics.get(0);
        int idx = _graphicCollection.indexOf(aG);
        if (idx < _graphicCollection.size() - 1) {
            _graphicCollection.remove(aG);
            _graphicCollection.add(idx + 1, aG);
            this.paintLayers();
        }
    }

    private void onSendBackwardClick(ActionEvent e) {
        Graphic aG = _selectedGraphics.get(0);
        int idx = _graphicCollection.indexOf(aG);
        if (idx > 0) {
            _graphicCollection.remove(aG);
            _graphicCollection.add(idx - 1, aG);
            this.paintLayers();
        }
    }

    private void onRemoveGraphicClick(ActionEvent e) {
        removeSelectedGraphics();
        _startNewGraphic = true;
        paintLayers();
    }

    private void onReverseGraphicClick(ActionEvent e) {
        Graphic aGraphic = _selectedGraphics.get(0);
        List<PointD> points = (List<PointD>) aGraphic.getShape().getPoints();
        Collections.reverse(points);
        aGraphic.getShape().setPoints(points);
        this.paintLayers();
    }

    private void onGrahpicSmoothClick(ActionEvent e) {
        Graphic aGraphic = _selectedGraphics.get(0);
        List<wContour.Global.PointD> pointList = new ArrayList<wContour.Global.PointD>();
        List<PointD> newPoints = new ArrayList<PointD>();

        for (PointD aP : aGraphic.getShape().getPoints()) {
            pointList.add(new wContour.Global.PointD(aP.X, aP.Y));
        }

        if (aGraphic.getShape().getShapeType() == ShapeTypes.Polygon) {
            pointList.add(pointList.get(0));
        }

        pointList = wContour.Contour.smoothPoints(pointList);
        for (wContour.Global.PointD aP : pointList) {
            newPoints.add(new PointD(aP.X, aP.Y));
        }
        aGraphic.getShape().setPoints(newPoints);
        this.paintLayers();
    }

    private void onGraphicMaskoutClick(ActionEvent e) {
        Graphic aGraphic = _selectedGraphics.get(0);
        ((PolygonBreak) aGraphic.getLegend()).setMaskout(!((PolygonBreak) aGraphic.getLegend()).isMaskout());
        this.paintLayers();
    }

    void onMouseWheelMoved(MouseWheelEvent e) {
        double MinX, MaxX, MinY, MaxY, lonRan, latRan, ZoomF;
        double mouseLon, mouseLat;
        lonRan = _drawExtent.maxX - _drawExtent.minX;
        latRan = _drawExtent.maxY - _drawExtent.minY;
        mouseLon = _drawExtent.minX + lonRan / 2;
        mouseLat = _drawExtent.minY + latRan / 2;

        ZoomF = 1 + e.getWheelRotation() / 10.0f;

        MinX = mouseLon - (lonRan / 2 * ZoomF);
        MaxX = mouseLon + (lonRan / 2 * ZoomF);
        MinY = mouseLat - (latRan / 2 * ZoomF);
        MaxY = mouseLat + (latRan / 2 * ZoomF);

        if (!_highSpeedWheelZoom) {
            zoomToExtent(MinX, MaxX, MinY, MaxY);
        } else {
//            BufferedImage dImage = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
//            Graphics2D dg = (Graphics2D) dImage.getGraphics();
            //float nWidth = this.getWidth() / (float) ZoomF;
            //float nHeight = this.getHeight() / (float) ZoomF;
            _paintScale = _paintScale / ZoomF;
            float nWidth = this.getWidth() * (float) _paintScale;
            float nHeight = this.getHeight() * (float) _paintScale;
            float nx = (this.getWidth() - nWidth) / 2;
            float ny = (this.getHeight() - nHeight) / 2;
            _xShift = (int) nx;
            _yShift = (int) ny;
//            AffineTransform mx = new AffineTransform();
//            mx.translate(nx, ny);
//            mx.scale(1.0f / ZoomF, 1.0f / ZoomF);
//            AffineTransformOp aop = new AffineTransformOp(mx, AffineTransformOp.TYPE_BICUBIC);      
//            dg.drawImage(_mapBitmap, aop, 0, 0);        
//            _mapBitmap = dImage;
//            dg.dispose();
            this.repaint();
            _viewExtent = new Extent(MinX, MaxX, MinY, MaxY);
            refreshXYScale();

            this._lastMouseWheelTime = new Date();
            if (!this._mouseWheelDetctionTimer.isRunning()) {
                this._mouseWheelDetctionTimer.start();
            }
        }
    }

    void onKeyTyped(KeyEvent e) {
    }

    void onKeyPressed(KeyEvent e) {
        if (_mouseTool == MouseTools.SelectElements || _mouseTool == MouseTools.CreateSelection) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                removeSelectedGraphics();
                _startNewGraphic = true;
                paintLayers();
            }
        }
    }

    void onKeyReleased(KeyEvent e) {
    }

    // </editor-fold>
    // <editor-fold desc="Methods">
    // <editor-fold desc="Layer">
    /**
     * Add a layer
     *
     * @param aLayer The layer
     * @return Layer handle
     */
    public int addLayer(MapLayer aLayer) {
        int handle = -1;
        switch (aLayer.getLayerType()) {
            case VectorLayer:
                handle = addVectorLayer((VectorLayer) aLayer);
                break;
            case RasterLayer:
                handle = addRasterLayer((RasterLayer) aLayer);
                break;
            case ImageLayer:
                handle = addImageLayer((ImageLayer) aLayer);
                break;
        }

        _extent = getLayersWholeExtent();
        if (_layers.size() == 1) {
            this.zoomToExtent(_extent);
        } else {
            this.paintLayers();
        }
        return handle;
    }

    /**
     * Add wind layer
     *
     * @param aLayer Wind layer
     * @param EarthWind If wind relative to earth
     * @return Layer handle
     */
    public int addWindLayer(VectorLayer aLayer, boolean EarthWind) {
        int handle;
        handle = getNewLayerHandle();
        aLayer.setHandle(handle);
        ProjectionInfo aProjInfo = aLayer.getProjInfo();
        ProjectionInfo GeoProjInfo = KnownCoordinateSystems.geographic.world.WGS1984;

        if (!aLayer.getProjInfo().equals(_projection.getProjInfo())) {
            if (EarthWind) {
                if (aProjInfo.getProjectionName() == ProjectionNames.LongLat) {
                    _projection.projectLayer(aLayer, _projection.getProjInfo());
                } else {
                    _projection.projectWindLayer(aLayer, _projection.getProjInfo(), false);
                    _projection.projectLayerAngle(aLayer, GeoProjInfo, _projection.getProjInfo());
                }
            } else {
                _projection.projectLayer(aLayer, _projection.getProjInfo());
            }


        }
        _layers.add(aLayer);
        _extent = getLayersWholeExtent();
        this.paintLayers();
        this.fireLayersUpdatedEvent();

        return handle;
    }

    /**
     * Add a vector layer
     *
     * @param aLayer The vector layer
     * @return The layer handle
     */
    private int addVectorLayer(VectorLayer aLayer) {
        int handle = getNewLayerHandle();
        aLayer.setHandle(handle);
        boolean projectLabels = true;
        if (aLayer.getLabelPoints().size() > 0) {
            projectLabels = false;
        }

        if (!aLayer.getProjInfo().equals(_projection.getProjInfo())) {
            _projection.projectLayer(aLayer, _projection.getProjInfo(), projectLabels);
        }

        _layers.add(aLayer);

        this.fireLayersUpdatedEvent();

        return handle;
    }

    /**
     * Add image layer
     *
     * @param aLayer Image layer
     * @return Layer handle
     */
    private int addImageLayer(ImageLayer aLayer) {
        int handle;
        handle = getNewLayerHandle();
        aLayer.setHandle(handle);
        _layers.add(aLayer);

        this.paintLayers();
        this.fireLayersUpdatedEvent();

        return handle;
    }

    /**
     * Add raster layer
     *
     * @param aLayer Raster layer
     * @return Layer handle
     */
    private int addRasterLayer(RasterLayer aLayer) {
        int handle = getNewLayerHandle();
        aLayer.setHandle(handle);

        if (!aLayer.getProjInfo().equals(_projection.getProjInfo())) {
            _projection.projectLayer(aLayer, _projection.getProjInfo());
        }

        _layers.add(aLayer);

        this.paintLayers();
        this.fireLayersUpdatedEvent();

        return handle;
    }

    /**
     * Get new layer handle
     *
     * @return New layer handle
     */
    public int getNewLayerHandle() {
        int handle = 0;
        for (int i = 0; i < _layers.size(); i++) {
            if (handle < _layers.get(i).getHandle()) {
                handle = _layers.get(i).getHandle();
            }
        }
        handle += 1;

        return handle;
    }

    /**
     * Get layers whole extent
     *
     * @return The extent
     */
    public Extent getLayersWholeExtent() {
        Extent aExtent = new Extent();
        Extent bExtent;
        for (int i = 0; i < _layers.size(); i++) {
            bExtent = _layers.get(i).getExtent();
            if (i == 0) {
                aExtent = bExtent;
            } else {
                aExtent = MIMath.getLagerExtent(aExtent, bExtent);
            }
        }

        return aExtent;
    }

    /**
     * Get layer handle from layer name
     *
     * @param name The layer name
     * @return Layer handle
     */
    public int getLayerHandleFromName(String name) {
        int handle = -1;
        for (int i = 0; i < _layers.size(); i++) {
            if (_layers.get(i).getLayerName().equals(name)) {
                handle = _layers.get(i).getHandle();
                break;
            }
        }

        return handle;
    }

    /**
     * Get layer handle from layer index
     *
     * @param lIdx Layer index
     * @return Layer handle
     */
    public int getLayerHandleFromIdx(int lIdx) {
        return _layers.get(lIdx).getHandle();
    }

    /**
     * Get layer from handle
     *
     * @param handle The layer handle
     * @return The layer object
     */
    public MapLayer getLayerFromHandle(int handle) {
        MapLayer aLayer = null;
        for (int i = 0; i < _layers.size(); i++) {
            if (_layers.get(i).getHandle() == handle) {
                aLayer = _layers.get(i);
                break;
            }
        }

        return aLayer;
    }

    /**
     * Get layer from layer name
     *
     * @param name The layer name
     * @return The layer
     */
    public MapLayer getLayerFromName(String name) {
        MapLayer aLayer = null;
        for (MapLayer ml : _layers) {
            if (ml.getLayerName().equals(name)) {
                aLayer = ml;
                break;
            }
        }

        return aLayer;
    }

    /**
     * Get layer index from layer handle
     *
     * @param handle The layer handle
     * @return Layer index
     */
    public int getLayerIdxFromHandle(int handle) {
        int lIdx = -1;
        for (int i = 0; i < _layers.size(); i++) {
            if (_layers.get(i).getHandle() == handle) {
                lIdx = i;
                break;
            }
        }

        return lIdx;
    }

    /**
     * Move layer position
     *
     * @param lPreIdx Previous index
     * @param lNewIdx New index
     */
    public void moveLayer(int lPreIdx, int lNewIdx) {

        if (lNewIdx > lPreIdx) {
            if (lNewIdx == _layers.size() - 1) {
                _layers.add(_layers.get(lPreIdx));
            } else {
                _layers.add(lNewIdx + 1, _layers.get(lPreIdx));
            }
            _layers.remove(lPreIdx);
        } else {
            _layers.add(lNewIdx, _layers.get(lPreIdx));
            _layers.remove(lPreIdx + 1);
        }
    }

    /**
     * Remove a layer by index
     *
     * @param aIdx Layer index
     */
    public void removeLayer(int aIdx) {
        _layers.remove(aIdx);
        _extent = getLayersWholeExtent();
    }

    /**
     * Remove layer by handle
     *
     * @param handle Layer handle
     */
    public void removeLayerHandle(int handle) {
        int lIdx = getLayerIdxFromHandle(handle);
        if (lIdx >= 0) {
            removeLayer(lIdx);
        }
    }

    /**
     * Remove a layer
     *
     * @param aLayer The layer
     */
    public void removeLayer(MapLayer aLayer) {
        int aIdx = getLayerIdxFromHandle(aLayer.getHandle());
        removeLayer(aIdx);
    }

    /**
     * Remove all layers
     */
    public void removeAllLayers() {
        int aNum = _layers.size();
        for (int i = 0; i < aNum; i++) {
            removeLayer(0);
        }
        //this.repaint();
        this.paintLayers();
    }

    /**
     * Get last polyline layer index
     *
     * @return Layer index
     */
    public int getLineLayerIdx() {
        VectorLayer bLayer;
        int lIdx = -1;
        for (int i = _layers.size() - 1; i >= 0; i--) {
            if (_layers.get(i).getLayerType() == LayerTypes.VectorLayer) {
                bLayer = (VectorLayer) _layers.get(i);
                switch (bLayer.getShapeType()) {
                    case Polyline:
                    case PolylineM:
                    case PolylineZ:
                    case Polygon:
                    case PolygonM:
                        lIdx = i;
                        break;
                }

                if (lIdx > -1) {
                    break;
                }
            } else {
                lIdx = i;
                break;
            }
        }

        return lIdx;
    }

    /**
     * Get last polygon layer index
     *
     * @return Layer index
     */
    public int getPolygonLayerIdx() {
        VectorLayer bLayer;
        int lIdx = -1;
        for (int i = _layers.size() - 1; i >= 0; i--) {
            if (_layers.get(i).getLayerType() == LayerTypes.VectorLayer) {
                bLayer = (VectorLayer) _layers.get(i);
                switch (bLayer.getShapeType()) {
                    case Polygon:
                    case PolygonM:
                        lIdx = i;
                        break;
                }

                if (lIdx > -1) {
                    break;
                }
            } else {
                lIdx = i;
                break;
            }
        }

        return lIdx;
    }

    /**
     * Get last image layer index
     *
     * @return Layer index
     */
    public int getImageLayerIdx() {
        int lIdx = -1;
        for (int i = _layers.size() - 1; i >= 0; i--) {
            if (_layers.get(i).getLayerType() == LayerTypes.ImageLayer
                    || _layers.get(i).getLayerType() == LayerTypes.RasterLayer) {
                lIdx = i;
                break;
            }
        }

        return lIdx;
    }
    // </editor-fold>

    // <editor-fold desc="Painting Methods">
    /**
     * Paint component
     *
     * @param g Graphics
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //this.setBackground(Color.white);
        Graphics2D g2 = (Graphics2D) g;
        //g2.setColor(this.getBackground());
        //g2.clearRect(0, 0, this.getWidth(), this.getHeight());
        //g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        //g2.drawImage(_mapBitmap, _xShift, _yShift, this.getBackground(), this);

        AffineTransform mx = new AffineTransform();
        mx.translate((float) _xShift, (float) _yShift);
        mx.scale(_paintScale, _paintScale);
        AffineTransformOp aop = new AffineTransformOp(mx, AffineTransformOp.TYPE_BILINEAR);
        g2.drawImage(_mapBitmap, aop, 0, 0);

        if (this._dragMode) {
            switch (this._mouseTool) {
                case Zoom_In:
                    int aWidth = Math.abs(_mouseLastPos.x - _mouseDownPoint.x);
                    int aHeight = Math.abs(_mouseLastPos.y - _mouseDownPoint.y);
                    int aX = Math.min(_mouseLastPos.x, _mouseDownPoint.x);
                    int aY = Math.min(_mouseLastPos.y, _mouseDownPoint.y);
                    g2.setColor(this.getForeground());
                    float dash1[] = {2.0f};
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
                    g2.draw(new Rectangle(aX, aY, aWidth, aHeight));
                    break;
                case MoveSelection:
                    Rectangle rect = new Rectangle();
                    rect.x = _selectedRectangle.x + _mouseLastPos.x - _mouseDownPoint.x;
                    rect.y = _selectedRectangle.y + _mouseLastPos.y - _mouseDownPoint.y;
                    rect.width = _selectedRectangle.width;
                    rect.height = _selectedRectangle.height;

                    g2.setColor(Color.red);
                    float dash2[] = {2.0f};
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                            10.0f, dash2, 0.0f));
                    g2.draw(rect);
                    break;
                case ResizeSelection:
                    g2.setColor(Color.red);
                    float[] dashPattern = new float[]{2.0F, 1.0F};
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                            10.0f, dashPattern, 0.0f));
                    g2.draw(_resizeRectangle);
                    break;
                case CreateSelection:
                case New_Rectangle:
                case New_Ellipse:
                    int sx = Math.min(_mouseDownPoint.x, _mouseLastPos.x);
                    int sy = Math.min(_mouseDownPoint.y, _mouseLastPos.y);
                    g2.setColor(this.getForeground());
                    g2.draw(new Rectangle(sx, sy, Math.abs(_mouseLastPos.x - _mouseDownPoint.x),
                            Math.abs(_mouseLastPos.y - _mouseDownPoint.y)));
                    break;
                case New_Freehand:
                    List<PointF> points = new ArrayList<PointF>(_graphicPoints);
                    points.add(new PointF(_mouseLastPos.x, _mouseLastPos.y));
                    g2.setColor(this.getForeground());
                    _graphicPoints.add(new PointF(_mouseLastPos.x, _mouseLastPos.y));
                    Draw.drawPolyline(points, g2);
                    break;
                case New_Circle:
                    int radius = (int) Math.sqrt(Math.pow(_mouseLastPos.x - _mouseDownPoint.x, 2)
                            + Math.pow(_mouseLastPos.y - _mouseDownPoint.y, 2));
                    g2.setColor(this.getForeground());
                    g2.drawLine(_mouseDownPoint.x, _mouseDownPoint.y, _mouseLastPos.x, _mouseLastPos.y);
                    g2.drawOval(_mouseDownPoint.x - radius, _mouseDownPoint.y - radius,
                            radius * 2, radius * 2);
                    break;
                case InEditingVertices:
                    double[] sXY = projToScreen(_editingVertices.get(1).X, _editingVertices.get(1).Y);
                    g2.setColor(Color.black);
                    g2.drawLine((int) sXY[0], (int) sXY[1], _mouseLastPos.x, _mouseLastPos.y);
                    if (_editingVertices.size() == 3) {
                        sXY = projToScreen(_editingVertices.get(2).X, _editingVertices.get(2).Y);
                        g2.drawLine((int) sXY[0], (int) sXY[1], _mouseLastPos.x, _mouseLastPos.y);
                    }

                    Rectangle nRect = new Rectangle(_mouseLastPos.x - 3, _mouseLastPos.y - 3, 6, 6);
                    g2.setColor(Color.cyan);
                    g2.fill(nRect);
                    g2.setColor(Color.black);
                    g2.draw(nRect);
                    break;
            }
        }

        switch (_mouseTool) {
            case New_Polyline:
            case New_Polygon:
            case New_Curve:
            case New_CurvePolygon:
                //case New_Freehand:
                if (!_startNewGraphic) {
                    //this.repaint();
                    List<PointF> points = new ArrayList<PointF>(_graphicPoints);
                    points.add(new PointF(_mouseLastPos.x, _mouseLastPos.y));
                    g.setColor(this.getForeground());
                    switch (_mouseTool) {
                        case New_Polyline:
                            Draw.drawPolyline(points, g2);
                            break;
                        case New_Polygon:
                            points.add(points.get(0));
                            Draw.drawPolyline(points, g2);
                            break;
                        case New_Curve:
                            Draw.drawCurveLine(points, g2);
                            break;
                        case New_CurvePolygon:
                            points.add(points.get(0));
                            Draw.drawCurveLine(points, g2);
                            break;
                    }
                }
                break;
            case Measurement:
                if (!_startNewGraphic) {
                    PointF[] fpoints = (PointF[]) _graphicPoints.toArray(new PointF[_graphicPoints.size()]);
                    PointF[] points = new PointF[fpoints.length + 1];
                    System.arraycopy(fpoints, 0, points, 0, fpoints.length);
                    points[_graphicPoints.size()] = new PointF(_mouseLastPos.x, _mouseLastPos.y);

                    if (_frmMeasure.getMeasureType() == MeasureTypes.Length) {
                        g2.setColor(Color.red);
                        g2.setStroke(new BasicStroke(2));
                        Draw.drawPolyline(points, g2);
                    } else {
                        PointF[] ppoints = new PointF[points.length + 1];
                        System.arraycopy(points, 0, ppoints, 0, points.length);
                        ppoints[ppoints.length - 1] = _graphicPoints.get(0);
                        Color aColor = new Color(Color.blue.getRed(), Color.blue.getGreen(), Color.blue.getBlue(), 100);
                        g.setColor(aColor);
                        PolygonBreak aPB = new PolygonBreak();
                        aPB.setColor(aColor);
                        Draw.drawPolygon(ppoints, aPB, g2);
                        g.setColor(Color.red);
                        Draw.drawPolyline(ppoints, g2);
                    }
                }
                break;
        }

        if (this._drawIdentiferShape) {
            if (this.getSelectedLayer() >= 0) {
                MapLayer aLayer = this.getLayerFromHandle(this.getSelectedLayer());
                if (aLayer != null) {
                    if (aLayer.getLayerType() == LayerTypes.VectorLayer) {
                        VectorLayer vLayer = (VectorLayer) aLayer;
                        drawIdShape(g2, vLayer.getShapes().get(vLayer.getIdentiferShape()));
                    }
                }
            }
        }

        g2.dispose();
    }

    public void paintLayers() {
        if (this.getWidth() < 10 || this.getHeight() < 10) {
            return;
        }
        if (this.getLayerNum() == 0) {
            return;
        }

        if (!this._lockViewUpdate) {
            this._mapBitmap = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
            //this._mapBitmap = (BufferedImage)this.createImage(this.getWidth(), this.getHeight());
            Graphics2D g = this._mapBitmap.createGraphics();
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            if (_antiAlias) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            } else {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
            }

            _xGridPosLabel.clear();
            _yGridPosLabel.clear();

            if (_isGeoMap) {
                updateLonLatLayer();
                if (_projection.isLonLatMap()) {
                    drawLonLatMap(g);
                } else {
                    drawProjectedMap(g);
                }
            } else {
                draw2DMap(g);
            }

            this.repaint();
        }
    }

    /**
     * Paint graphics
     *
     * @param g Graphics2D
     */
    public void paintGraphics(Graphics2D g) {
        getMaskOutGraphicsPath(g);

        //g.SmoothingMode = _smoothingMode;
        //g.TextRenderingHint = TextRenderingHint.AntiAlias;

        _xGridPosLabel.clear();
        _yGridPosLabel.clear();

        if (_isGeoMap) {
            updateLonLatLayer();
            if (_projection.isLonLatMap()) {
                drawLonLatMap(g);
            } else {
                drawProjectedMap(g);
            }
            getLonLatGridLabels();
        } else {
            draw2DMap(g);
        }
    }

    /**
     * Paint graphics
     *
     * @param g Graphics2D
     * @param rect Target rectangle
     */
    public void paintGraphics(Graphics2D g, Rectangle rect) {
        refreshXYScale(rect.width, rect.height);

        AffineTransform oldMatrix = g.getTransform();
        Rectangle oldRegion = g.getClipBounds();
        //GraphicsPath path = new GraphicsPath();
        //path.AddRectangle(rect);
        g.setClip(rect);

        getMaskOutGraphicsPath(g);

        g.translate(rect.x, rect.y);
        _maskOutGraphicsPath.transform(g.getTransform());

        //g.SmoothingMode = _smoothingMode;
        //g.TextRenderingHint = TextRenderingHint.AntiAlias;

        _xGridPosLabel.clear();
        _yGridPosLabel.clear();

        if (_isGeoMap) {
            updateLonLatLayer();
            if (_projection.isLonLatMap()) {
                drawLonLatMap(g, rect.width, rect.height);
            } else {
                drawProjectedMap(g, rect.width, rect.height);
            }
            getLonLatGridLabels();
        } else {
            draw2DMap(g);
        }

        g.setTransform(oldMatrix);
        g.setClip(oldRegion);
    }

    private void drawLonLatMap(Graphics2D g) {
        drawLonLatMap(g, this.getWidth(), this.getHeight());
    }

    private void drawLonLatMap(Graphics2D g, int width, int heigth) {
        //Draw layers
        drawLayers(g, width, heigth);

        //Draw lon lat
        if (_drawGridLine) {
            LegendScheme aLS = _lonLatLayer.getLegendScheme();
            PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(0);
            aPLB.setColor(_gridLineColor);
            aPLB.setSize(_gridLineSize);
            aPLB.setStyle(_gridLineStyle);

            drawLonLatLayer(_lonLatLayer, g, 0);
            if (_multiGlobalDraw) {
                if (_lonLatLayer.getExtent().minX > -360 && _lonLatLayer.getExtent().maxX > 0) {
                    drawLonLatLayer(_lonLatLayer, g, -360);
                }
                if (_lonLatLayer.getExtent().maxX < 360 && _lonLatLayer.getExtent().minX < 0) {
                    drawLonLatLayer(_lonLatLayer, g, 360);
                }
            }
        }

        //Draw graphics
        if (_graphicCollection.size() > 0) {
            drawGraphicList(g, 0);
            if (_multiGlobalDraw) {
                if (_graphicCollection.getExtent().minX > -360 && _graphicCollection.getExtent().maxX > 0) {
                    drawGraphicList(g, -360);
                }
                if (_graphicCollection.getExtent().maxX < 360 && _graphicCollection.getExtent().minX < 0) {
                    drawGraphicList(g, 360);
                }
            }
        }
    }

    private void drawProjectedMap(Graphics2D g) {
        drawProjectedMap(g, this.getWidth(), this.getHeight());
    }

    private void drawProjectedMap(Graphics2D g, int width, int heigth) {
        //Draw layers
        drawProjectedLayers(g, width, heigth);

        //Draw lon/lat
        if (_drawGridLine) {
            //drawProjectedLonLat(g);
            this.drawLonLatLayer(_lonLatLayer, g, 0);
        }

        //Draw graphics
        drawGraphicList(g, 0);
    }

    private void draw2DMap(Graphics2D g) {

        //Draw layers
        drawLayers(g);

        //Draw X/Y grid
        drawXYGrid(g, _xGridStrs, _yGridStrs);

    }

    private void drawLayers(Graphics2D g) {
        drawLayers(g, this.getWidth(), this.getHeight());
    }

    private void drawLayers(Graphics2D g, int width, int height) {
        java.awt.Shape oldRegion = g.getClip();
        double geoScale = this.getGeoScale();
        for (int i = 0; i < _layers.size(); i++) {
            MapLayer aLayer = _layers.get(i);
            if (aLayer.isVisible()) {
                if (aLayer.getVisibleScale().isEnableMinVisScale()) {
                    if (geoScale > aLayer.getVisibleScale().getMinVisScale()) {
                        continue;
                    }
                }
                if (aLayer.getVisibleScale().isEnableMaxVisScale()) {
                    if (geoScale < aLayer.getVisibleScale().getMaxVisScale()) {
                        continue;
                    }
                }
                if (aLayer.isMaskout()) {
                    setClipRegion(g);
                }

                switch (_layers.get(i).getLayerType()) {
                    case ImageLayer:
                        ImageLayer aImageLayer = (ImageLayer) aLayer;
                        drawImage(g, aImageLayer, 0, width, height);
                        if (_multiGlobalDraw) {
                            if (aImageLayer.getExtent().minX > -360 && aImageLayer.getExtent().maxX > 0) {
                                drawImage(g, aImageLayer, -360, width, height);
                            }
                            if (aImageLayer.getExtent().maxX < 360 && aImageLayer.getExtent().minX < 0) {
                                drawImage(g, aImageLayer, 360, width, height);
                            }
                        }
                        break;
                    case RasterLayer:
                        RasterLayer aRLayer = (RasterLayer) aLayer;
                        drawRasterLayer(g, aRLayer, 0);
                        if (_multiGlobalDraw) {
                            if (aRLayer.getExtent().minX > -360 && aRLayer.getExtent().maxX > 0) {
                                drawRasterLayer(g, aRLayer, -360);
                            }
                            if (aRLayer.getExtent().maxX < 360 && aRLayer.getExtent().minX < 0) {
                                drawRasterLayer(g, aRLayer, 360);
                            }
                        }
                        break;
                    case VectorLayer:
                        VectorLayer aVLayer = (VectorLayer) aLayer;
                        switch (aVLayer.getLayerDrawType()) {
                            case Vector:
                                drawVectLayerWithLegendScheme(aVLayer, g, 0);
                                if (this._multiGlobalDraw) {
                                    if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                                        drawVectLayerWithLegendScheme(aVLayer, g, -360);
                                    }
                                    if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                                        drawVectLayerWithLegendScheme(aVLayer, g, 360);
                                    }
                                }
                                break;
                            case Barb:
                                drawBarbLayerWithLegendScheme(aVLayer, g, 0);
                                if (this._multiGlobalDraw) {
                                    if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                                        drawBarbLayerWithLegendScheme(aVLayer, g, -360);
                                    }
                                    if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                                        drawBarbLayerWithLegendScheme(aVLayer, g, 360);
                                    }
                                }
                                break;
                            case StationModel:
                                drawStationModelLayer(aVLayer, g, 0);
                                if (this._multiGlobalDraw) {
                                    if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                                        drawStationModelLayer(aVLayer, g, -360);
                                    }
                                    if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                                        drawStationModelLayer(aVLayer, g, 360);
                                    }
                                }
                                break;
                            default:
                                drawLayerWithLegendScheme(aVLayer, g, 0);
                                if (this._multiGlobalDraw) {
                                    if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                                        drawLayerWithLegendScheme(aVLayer, g, -360);
                                    }
                                    if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                                        drawLayerWithLegendScheme(aVLayer, g, 360);
                                    }
                                }
                                break;
                        }
                        break;
                }

                if (aLayer.isMaskout()) {
                    g.setClip(oldRegion);
                }

            }

        }
    }

    private void drawImage(Graphics2D g, ImageLayer aILayer, double LonShift, int width, int height) {
        Extent lExtent = MIMath.shiftExtentLon(aILayer.getExtent(), LonShift);
        if (MIMath.isExtentCross(lExtent, _drawExtent)) {
            double XUL, YUL, XBR, YBR;
            XUL = aILayer.getExtent().minX;
            YUL = aILayer.getExtent().maxY;
            XBR = aILayer.getExtent().maxX;
            YBR = aILayer.getExtent().minY;
            double[] sXY = projToScreen(XUL - aILayer.getWorldFilePara().xScale / 2, YUL - aILayer.getWorldFilePara().yScale / 2,
                    LonShift);
            double sX = sXY[0];
            double sY = sXY[1];
            sXY = projToScreen(XBR, YBR, LonShift);
            double aWidth = sXY[0] - sX;
            double aHeight = sXY[1] - sY;

            if (aWidth < 5 || aHeight < 5) {
                return;
            }

            //Draw image
            BufferedImage dImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D dg = (Graphics2D) dImage.getGraphics();
            //dg.drawImage(aILayer.getImage(), 0, 0, null);
//                Image dImage = new Bitmap(width, height);
//                Graphics dg = Graphics.FromImage(dImage);
//                dg.PixelOffsetMode = PixelOffsetMode.Half;

            BufferedImage vImage = aILayer.getImage();
            double iWidth = vImage.getWidth();
            double iHeight = vImage.getHeight();
            double cw = aWidth / iWidth;
            double ch = aHeight / iHeight;
            double shx = aILayer.getWorldFilePara().xRotate;
            double shy = aILayer.getWorldFilePara().yRotate;
            AffineTransform mx = new AffineTransform();
            if (shx == 0.0 && shy == 0.0) {
                mx.translate(sX, sY);
                mx.scale(cw, ch);
            } else {
                shx = cw / aILayer.getWorldFilePara().xScale * shx;
                shy = ch / aILayer.getWorldFilePara().yScale * shy;
                mx = new AffineTransform(cw, shy, shx, ch, sX, sY);
            }
            dg.setTransform(mx);
            dg.drawImage(vImage, 0, 0, null);
            dg.dispose();

            if (aILayer.getTransparency() > 0) {
                //Set transparency
                int transPerc = 100 - aILayer.getTransparency();
                float[] scales = {1f, 1f, 1f, transPerc / 100.0f};
                float[] offsets = new float[4];
                RescaleOp rop = new RescaleOp(scales, offsets, null);

                /* Draw the image, applying the filter */
                g.drawImage(dImage, rop, 0, 0);
            } else {
                g.drawImage(dImage, 0, 0, null);
            }
        }
    }

    private void drawImage_back(Graphics2D g, ImageLayer aILayer, double LonShift, int width, int height) {
        Extent lExtent = MIMath.shiftExtentLon(aILayer.getExtent(), LonShift);
        if (MIMath.isExtentCross(lExtent, _drawExtent)) {
            double XUL, YUL, XBR, YBR;
            XUL = aILayer.getExtent().minX;
            YUL = aILayer.getExtent().maxY;
            XBR = aILayer.getExtent().maxX;
            YBR = aILayer.getExtent().minY;
            double[] sXY = projToScreen(XUL - aILayer.getWorldFilePara().xScale / 2, YUL - aILayer.getWorldFilePara().yScale / 2,
                    LonShift);
            double sX = sXY[0];
            double sY = sXY[1];
            sXY = projToScreen(XBR, YBR, LonShift);
            double aWidth = sXY[0] - sX;
            double aHeight = sXY[1] - sY;

            if (aWidth < 5 || aHeight < 5) {
                return;
            }

            //Draw image
            BufferedImage dImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D dg = (Graphics2D) dImage.getGraphics();
            //dg.drawImage(aILayer.getImage(), 0, 0, null);
//                Image dImage = new Bitmap(width, height);
//                Graphics dg = Graphics.FromImage(dImage);
//                dg.PixelOffsetMode = PixelOffsetMode.Half;

            BufferedImage vImage = aILayer.getImage();
            float iWidth = vImage.getWidth();
            float iHeight = vImage.getHeight();
            float cw = (float) (aWidth / iWidth);
            float ch = (float) (aHeight / iHeight);
            AffineTransform mx = new AffineTransform();
            mx.translate((float) sX, (float) sY);
            mx.scale(cw, ch);
            dg.setTransform(mx);
            if (aILayer.getTransparency() > 0) {
                //Set transparency
                int transPerc = 100 - aILayer.getTransparency();
                float[] scales = {1f, 1f, 1f, transPerc / 100.0f};
                float[] offsets = new float[4];
                RescaleOp rop = new RescaleOp(scales, offsets, null);

                /* Draw the image, applying the filter */
                dg.drawImage(vImage, rop, 0, 0);
            } else {
//                    if (vImage.HorizontalResolution != dg.DpiX || vImage.VerticalResolution != dg.DpiY)
//                        vImage.SetResolution(dg.DpiX, dg.DpiY);

                dg.drawImage(vImage, 0, 0, null);
            }
            dg.dispose();

            g.drawImage(dImage, 0, 0, null);
        }
    }

    private void drawRasterLayer(Graphics2D g, RasterLayer aRLayer, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aRLayer.getExtent(), LonShift);
        if (MIMath.isExtentCross(lExtent, _drawExtent)) {
            double XUL, YUL, XBR, YBR;
            XUL = aRLayer.getExtent().minX;
            YUL = aRLayer.getExtent().maxY;
            XBR = aRLayer.getExtent().maxX;
            YBR = aRLayer.getExtent().minY;

            double[] sXY = projToScreen(XUL, YUL, LonShift);
            double sX = sXY[0];
            double sY = sXY[1];
            sXY = projToScreen(XBR, YBR, LonShift);
            double aWidth = sXY[0] - sX;
            double aHeigh = sXY[1] - sY;

            if (aWidth < 5 || aHeigh < 5) {
                return;
            }
            //g.InterpolationMode = InterpolationMode.NearestNeighbor;
            //g.InterpolationMode = aRLayer.InterpMode;
            BufferedImage aImage = aRLayer.getImage();
            g.drawImage(aImage, (int) sX, (int) sY, (int) (sX + aWidth), (int) (sY + aHeigh),
                    0, 0, aImage.getWidth(), aImage.getHeight(), null);
        }
    }

    private void drawProjectedLayers(Graphics2D g) {
        drawProjectedLayers(g, this.getWidth(), this.getHeight());
    }

    private void drawProjectedLayers(Graphics2D g, int width, int height) {
        java.awt.Shape oldRegion = g.getClip();
        for (int i = 0; i < _layers.size(); i++) {
            MapLayer aLayer = _layers.get(i);
            if (aLayer.isVisible()) {
                if (aLayer.isMaskout()) {
                    setClipRegion(g);
                }
                switch (_layers.get(i).getLayerType()) {
                    case ImageLayer:
                        ImageLayer aImageLayer = (ImageLayer) aLayer;
                        drawImage(g, aImageLayer, 0, width, height);
                        break;
                    case RasterLayer:
                        RasterLayer aRLayer = (RasterLayer) aLayer;
                        drawRasterLayer(g, aRLayer, 0);
                        break;
                    case VectorLayer:
                        VectorLayer aVLayer = (VectorLayer) aLayer;
                        switch (aLayer.getLayerDrawType()) {
//                                case Vector:                                    
//                                    drawVectLayerWithLegendScheme(aLayer, g, 0);                                    
//                                    break;
//                                case Barb:                                    
//                                    drawBarbLayerWithLegendScheme(aLayer, g, 0);                                    
//                                    break;
//                                case WeatherSymbol:                                    
//                                    drawWeatherLayerWithLegendScheme(aLayer, g, 0);                                    
//                                    break;
//                                case StationModel:                                    
//                                    drawStationModelLayerWithLegendScheme(aLayer, g, 0);                                    
//                                    break;
                            default:
                                drawLayerWithLegendScheme(aVLayer, g, 0);
                                break;
                        }
                        break;
                }

                if (aLayer.isMaskout()) {
                    g.setClip(oldRegion);
                }
            }
        }
    }

    /**
     * Draw layer with legend scheme
     *
     * @param aLayer Vector layer
     * @param g Graphics
     * @param LonShift Longitude shift
     */
    public void drawLayerWithLegendScheme(VectorLayer aLayer,
            Graphics2D g, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        boolean hasDrawCharts = false;
        switch (aLayer.getShapeType()) {
            case Point:
                //Draw layer charts
//                    if (aLayer.getChartSet().isDrawCharts())
//                    {
//                        drawLayerCharts(g, aLayer, LonShift);
//                        hasDrawCharts = true;
//                    }

                drawPointLayer(aLayer, g, LonShift);
                break;
            case Polygon:
                drawPolygonLayer(aLayer, g, LonShift);
                break;
            case Polyline:
            case PolylineZ:
                drawPolylineLayer(aLayer, g, LonShift);
                break;
        }

        //Draw layer labels
        if (aLayer.getLabelSet().isDrawLabels()) {
            drawLayerLabels(g, aLayer, LonShift);
        }

        //Draw layer charts
        if (aLayer.getChartSet().isDrawCharts()) {
            drawLayerCharts(g, aLayer, LonShift);
        }
    }

    /**
     * Draw vector layer with legend scheme
     *
     * @param aLayer The vector layer
     * @param g Graphics2D
     * @param LonShift Longitude shift
     */
    public void drawVectLayerWithLegendScheme(VectorLayer aLayer,
            Graphics2D g, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        PointD aPoint;
        PointF sPoint = new PointF(0, 0);
        //Pen aPen = new Pen(Color.Black);
        double zoom = 1;
        float max;
        max = ((PointBreak) aLayer.getLegendScheme().getLegendBreaks().get(0)).getSize() * 3;
        List<WindArraw> windArraws = new ArrayList<WindArraw>();

        int shapeIdx = 0;
        List<Integer> idxList = new ArrayList<Integer>();
        for (Shape aShape : aLayer.getShapes()) {
            WindArraw aArraw = (WindArraw) aShape;
            aPoint = aArraw.getPoint();
            if (!(aPoint.X + LonShift < _drawExtent.minX || aPoint.X + LonShift > _drawExtent.maxX
                    || aPoint.Y < _drawExtent.minY || aPoint.Y > _drawExtent.maxY)) {
                windArraws.add(aArraw);
                idxList.add(shapeIdx);
            }
            shapeIdx += 1;
        }
        //Draw.GetMaxMinWindSpeed(windArraws, ref min, ref max);
        //zoom = 30.0 / (double)max;
        zoom = (double) max / 30.0;
        aLayer.setDrawingZoom((float) zoom);
        //zoom = zoom * 360 / (aLLSS.maxLon - aLLSS.minLon);
        LegendScheme aLS = aLayer.getLegendScheme();
        Color aColor;
        double value;
        switch (aLS.getLegendType()) {
            case SingleSymbol:
                PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(0);
                aColor = aPB.getColor();
                for (WindArraw aArraw : windArraws) {
                    aPoint = aArraw.getPoint();
                    double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
                    sPoint.X = (float) xy[0];
                    sPoint.Y = (float) xy[1];
                    Draw.drawArraw(aColor, sPoint, aArraw, g, zoom);
                }
                break;
            case UniqueValue:

                break;
            case GraduatedColor:
                for (int w = 0; w < windArraws.size(); w++) {
                    WindArraw aArraw = windArraws.get(w);
                    shapeIdx = idxList.get(w);
                    //value = aArraw.Value;
                    aPoint = aArraw.getPoint();
                    double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
                    sPoint.X = (float) xy[0];
                    sPoint.Y = (float) xy[1];

                    String vStr = aLayer.getCellValue(aLS.getFieldName(), shapeIdx).toString().trim();
                    if (vStr.isEmpty() || vStr == null) {
                        value = 0;
                    } else {
                        value = Double.parseDouble(vStr);
                    }
                    int blNum = 0;
                    for (int i = 0; i < aLS.getLegendBreaks().size(); i++) {
                        aPB = (PointBreak) aLS.getLegendBreaks().get(i);
                        if (value == Double.parseDouble(aPB.getStartValue().toString()) || (value > Double.parseDouble(aPB.getStartValue().toString())
                                && value < Double.parseDouble(aPB.getEndValue().toString()))
                                || (blNum == aLS.getLegendBreaks().size() && value == Double.parseDouble(aPB.getEndValue().toString()))) {
                            aColor = aPB.getColor();
                            Draw.drawArraw(aColor, sPoint, aArraw, g, zoom);
                        }
                    }
                }
                break;
        }
    }

    /**
     * Draw wind barb layer with legendscheme
     *
     * @param aLayer The layer
     * @param g Graphics2D
     * @param LonShift Longitude shift
     */
    public void drawBarbLayerWithLegendScheme(VectorLayer aLayer,
            Graphics2D g, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        PointD aPoint;
        PointF sPoint = new PointF(0, 0);
        LegendScheme aLS = aLayer.getLegendScheme();
        Color aColor;
        double value;
        List<WindBarb> windBarbs = new ArrayList<WindBarb>();
        int shapeIdx = 0;
        List<Integer> idxList = new ArrayList<Integer>();
        for (Shape aShape : aLayer.getShapes()) {
            WindBarb wBarb = (WindBarb) aShape;
            aPoint = wBarb.getPoint();
            if (!(aPoint.X + LonShift < _drawExtent.minX || aPoint.X + LonShift > _drawExtent.maxX
                    || aPoint.Y < _drawExtent.minY || aPoint.Y > _drawExtent.maxY)) {
                windBarbs.add(wBarb);
                idxList.add(shapeIdx);
            }
            shapeIdx += 1;
        }

        List<Extent> extentList = new ArrayList<Extent>();
        Extent maxExtent = new Extent();
        Extent aExtent = new Extent();
        if (aLS.getLegendType() == LegendType.SingleSymbol) {
            PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(0);
            aColor = aPB.getColor();
            for (WindBarb aWB : windBarbs) {
                aPoint = aWB.getPoint();
                double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
                sPoint.X = (float) xy[0];
                sPoint.Y = (float) xy[1];

                if (aLayer.getAvoidCollision()) {
                    //Judge extent
                    float aSize = aPB.getSize() / 2;
                    aExtent.minX = sPoint.X - aSize;
                    aExtent.maxX = sPoint.X + aSize;
                    aExtent.minY = sPoint.Y - aSize;
                    aExtent.maxY = sPoint.Y + aSize;
                    if (extentList.isEmpty()) {
                        maxExtent = (Extent) aExtent.clone();
                        extentList.add((Extent) aExtent.clone());
                        Draw.drawWindBarb(aColor, sPoint, aWB, g, aPB.getSize());
                    } else {
                        if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                            extentList.add((Extent) aExtent.clone());
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                            Draw.drawWindBarb(aColor, sPoint, aWB, g, aPB.getSize());
                        } else {
                            boolean ifDraw = true;
                            for (int i = 0; i < extentList.size(); i++) {
                                if (MIMath.isExtentCross(aExtent, extentList.get(i))) {
                                    ifDraw = false;
                                    break;
                                }
                            }
                            if (ifDraw) {
                                extentList.add((Extent) aExtent.clone());
                                maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                                Draw.drawWindBarb(aColor, sPoint, aWB, g, aPB.getSize());
                            }
                        }
                    }
                } else {
                    Draw.drawWindBarb(aColor, sPoint, aWB, g, aPB.getSize());
                }

            }
        } else {
            shapeIdx = 0;
            for (WindBarb aWB : windBarbs) {
                //value = aWB.Value;
                String vStr = aLayer.getCellValue(aLS.getFieldName(), shapeIdx).toString().trim();
                if (vStr.isEmpty() || vStr == null) {
                    value = 0;
                } else {
                    value = Double.parseDouble(vStr);
                }

                aPoint = aWB.getPoint();
                double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
                sPoint.X = (float) xy[0];
                sPoint.Y = (float) xy[1];

                if (aLayer.getAvoidCollision()) {
                    //Judge extent
                    float aSize = ((PointBreak) aLS.getLegendBreaks().get(0)).getSize() / 2;
                    aExtent.minX = sPoint.X - aSize;
                    aExtent.maxX = sPoint.X + aSize;
                    aExtent.minY = sPoint.Y - aSize;
                    aExtent.maxY = sPoint.Y + aSize;
                    if (extentList.isEmpty()) {
                        maxExtent = (Extent) aExtent.clone();
                        extentList.add((Extent) aExtent.clone());
                        float bSize = ((PointBreak) aLS.getLegendBreaks().get(0)).getSize();
                        for (ColorBreak aCB : aLS.getLegendBreaks()) {
                            PointBreak aPB = (PointBreak) aCB;
                            if (value == Double.parseDouble(aPB.getStartValue().toString()) || (value > Double.parseDouble(aPB.getStartValue().toString())
                                    && value < Double.parseDouble(aPB.getEndValue().toString()))) {
                                aColor = aPB.getColor();
                                Draw.drawWindBarb(aColor, sPoint, aWB, g, bSize);
                            }
                        }
                    } else {
                        if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                            extentList.add((Extent) aExtent.clone());
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                            float bSize = ((PointBreak) aLS.getLegendBreaks().get(0)).getSize();
                            for (ColorBreak aCB : aLS.getLegendBreaks()) {
                                PointBreak aPB = (PointBreak) aCB;
                                if (value == Double.parseDouble(aPB.getStartValue().toString()) || (value > Double.parseDouble(aPB.getStartValue().toString())
                                        && value < Double.parseDouble(aPB.getEndValue().toString()))) {
                                    aColor = aPB.getColor();
                                    Draw.drawWindBarb(aColor, sPoint, aWB, g, bSize);
                                }
                            }
                        } else {
                            boolean ifDraw = true;
                            for (int i = 0; i < extentList.size(); i++) {
                                if (MIMath.isExtentCross(aExtent, extentList.get(i))) {
                                    ifDraw = false;
                                    break;
                                }
                            }
                            if (ifDraw) {
                                extentList.add((Extent) aExtent.clone());
                                maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                                float bSize = ((PointBreak) aLS.getLegendBreaks().get(0)).getSize();
                                for (ColorBreak aCB : aLS.getLegendBreaks()) {
                                    PointBreak aPB = (PointBreak) aCB;
                                    if (value == Double.parseDouble(aPB.getStartValue().toString()) || (value > Double.parseDouble(aPB.getStartValue().toString())
                                            && value < Double.parseDouble(aPB.getEndValue().toString()))) {
                                        aColor = aPB.getColor();
                                        Draw.drawWindBarb(aColor, sPoint, aWB, g, bSize);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    float bSize = ((PointBreak) aLS.getLegendBreaks().get(0)).getSize();
                    for (ColorBreak aCB : aLS.getLegendBreaks()) {
                        PointBreak aPB = (PointBreak) aCB;
                        if (value == Double.parseDouble(aPB.getStartValue().toString()) || (value > Double.parseDouble(aPB.getStartValue().toString())
                                && value < Double.parseDouble(aPB.getEndValue().toString()))) {
                            aColor = aPB.getColor();
                            Draw.drawWindBarb(aColor, sPoint, aWB, g, bSize);
                        }
                    }
                }

                shapeIdx += 1;
            }
        }
    }

    private void drawProjectedLonLat(Graphics2D g) {
        if (_lonLatProjLayer != null) {
            LegendScheme aLS = _lonLatProjLayer.getLegendScheme();
            PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(0);
            aPLB.setColor(_gridLineColor);
            aPLB.setSize(_gridLineSize);
            aPLB.setStyle(_gridLineStyle);
            drawLonLatLayer(_lonLatProjLayer, g, 0);
        }
    }

    private void drawLonLatLayer(VectorLayer aLayer, Graphics2D g, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        LegendScheme aLS = _lonLatLayer.getLegendScheme();
        PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(0);
        aPLB.setColor(_gridLineColor);
        aPLB.setSize(_gridLineSize);
        aPLB.setStyle(_gridLineStyle);

        for (PolylineShape aPLS : (List<PolylineShape>) aLayer.getShapes()) {
            if (!aPLS.isVisible()) {
                continue;
            }

            if (aPLB.getDrawPolyline()) {
                drawLonLatPolylineShape(g, aPLS, aPLB, LonShift);
            }
        }
    }

    private void drawLonLatPolylineShape(Graphics2D g, PolylineShape aPLS, PolylineBreak aPLB, double LonShift) {
        Extent shapeExtent = MIMath.shiftExtentLon(aPLS.getExtent(), LonShift);
        if (!MIMath.isExtentCross(shapeExtent, _drawExtent)) {
            return;
        }

        List<PointD> newPList = (List<PointD>) aPLS.getPoints();
        PointF[] Points = new PointF[newPList.size()];
        PointF aPoint;
        for (int i = 0; i < newPList.size(); i++) {
            PointD wPoint = newPList.get(i);
            double[] sXY = projToScreen(wPoint.X, wPoint.Y, LonShift);
            aPoint = new PointF();
            aPoint.X = (float) sXY[0];
            aPoint.Y = (float) sXY[1];
            Points[i] = aPoint;
        }

        Color aColor = aPLB.getColor();
        if (aPLS.isSelected()) {
            aColor = _selectColor;
        }
        float[] dashPattern = getDashPattern(aPLB.getStyle());
        BasicStroke pen = new BasicStroke(aPLB.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
        g.setColor(aColor);
        g.setStroke(pen);

        if (!(aPLS.getPartNum() > 1)) {
            int p = 0;
            for (int i = 1; i < Points.length; i++) {
                g.draw(new Line2D.Float(Points[p].X, Points[p].Y, Points[i].X, Points[i].Y));
                p = i;
            }
        } else {
            int p, pp;
            PointF[] Pointps;
            for (p = 0; p < aPLS.getPartNum(); p++) {
                if (p == aPLS.getPartNum() - 1) {
                    Pointps = new PointF[aPLS.getPointNum() - aPLS.parts[p]];
                    for (pp = aPLS.parts[p]; pp < aPLS.getPointNum(); pp++) {
                        Pointps[pp - aPLS.parts[p]] = Points[pp];
                    }
                } else {
                    Pointps = new PointF[aPLS.parts[p + 1] - aPLS.parts[p]];
                    for (pp = aPLS.parts[p]; pp < aPLS.parts[p + 1]; pp++) {
                        Pointps[pp - aPLS.parts[p]] = Points[pp];
                    }
                }

                int f = 0;
                for (int i = 1; i < Pointps.length; i++) {
                    g.draw(new Line2D.Float(Pointps[f].X, Pointps[f].Y, Pointps[i].X, Pointps[i].Y));
                    f = i;
                }
            }
        }
    }

    private void drawPointLayer(VectorLayer aLayer, Graphics2D g, double LonShift) {
        //Object rend = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        RenderingHints rend = g.getRenderingHints();
        if (this._pointAntiAlias) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        }

        PointF aPoint = new PointF();
        LegendScheme aLS = aLayer.getLegendScheme();
        List<Extent> extentList = new ArrayList<Extent>();
        Extent maxExtent = new Extent();
        Extent aExtent;
        for (PointShape aPS : (List<PointShape>) aLayer.getShapes()) {
            if (aPS.getPoint().X + LonShift < _drawExtent.minX || aPS.getPoint().X + LonShift > _drawExtent.maxX
                    || aPS.getPoint().Y < _drawExtent.minY || aPS.getPoint().Y > _drawExtent.maxY) {
                continue;
            }
            if (aPS.getLegendIndex() < 0) {
                continue;
            }

            PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(aPS.getLegendIndex());
            if (aPB.isDrawShape()) {
                double[] screenXY;
                screenXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
                aPoint.X = (float) screenXY[0];
                aPoint.Y = (float) screenXY[1];
                boolean ifDraw = true;
                if (aLayer.getAvoidCollision()) {
                    float aSize = aPB.getSize() / 2;
                    aExtent = new Extent();
                    aExtent.minX = aPoint.X - aSize;
                    aExtent.maxX = aPoint.X + aSize;
                    aExtent.minY = aPoint.Y - aSize;
                    aExtent.maxY = aPoint.Y + aSize;
                    if (extentList.isEmpty()) {
                        maxExtent = (Extent) aExtent.clone();
                        extentList.add(aExtent);
                    } else {
                        if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                            extentList.add(aExtent);
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                        } else {
                            for (int i = 0; i < extentList.size(); i++) {
                                if (MIMath.isExtentCross(aExtent, extentList.get(i))) {
                                    ifDraw = false;
                                    break;
                                }
                            }
                            if (ifDraw) {
                                extentList.add(aExtent);
                                maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                            }
                        }
                    }
                }

                if (ifDraw) {
                    if (aPS.isSelected()) {
                        PointBreak newPB = (PointBreak) aPB.clone();
                        newPB.setColor(_selectColor);
                        Draw.drawPoint(aPoint, newPB, g);
                    } else {
                        Draw.drawPoint(aPoint, aPB, g);
                    }
                }
            }
        }

//        //Draw identifer shape
//        if (_drawIdentiferShape) {
//            PointShape aPS = (PointShape) aLayer.getShapes().get(aLayer.getIdentiferShape());
//            float[] screenXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
//            aPoint.X = screenXY[0];
//            aPoint.Y = screenXY[1];
//            PointBreak aPB = new PointBreak();
//            aPB.setOutlineColor(Color.red);
//            aPB.setSize(10);
//            aPB.setStyle(PointStyle.Square);
//            aPB.setDrawFill(false);
//
//            Draw.drawPoint(aPoint, aPB, g);
//        }

        if (this._pointAntiAlias) {
            g.setRenderingHints(rend);
        }
    }

    private void drawStationModelLayer(VectorLayer aLayer, Graphics2D g, double LonShift) {
        //Object rend = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
//        RenderingHints rend = g.getRenderingHints();
//        if (this._pointAntiAlias) {
//            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
//            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
//            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
//        }

        PointF aPoint = new PointF();
        LegendScheme aLS = aLayer.getLegendScheme();
        List<Extent> extentList = new ArrayList<Extent>();
        Extent maxExtent = new Extent();
        Extent aExtent;
        for (StationModelShape aPS : (List<StationModelShape>) aLayer.getShapes()) {
            if (aPS.getPoint().X + LonShift < _drawExtent.minX || aPS.getPoint().X + LonShift > _drawExtent.maxX
                    || aPS.getPoint().Y < _drawExtent.minY || aPS.getPoint().Y > _drawExtent.maxY) {
                continue;
            }
            if (aPS.getLegendIndex() < 0) {
                continue;
            }

            PointBreak aPB = (PointBreak) aLS.getLegendBreaks().get(aPS.getLegendIndex());
            if (aPB.isDrawShape()) {
                double[] screenXY;
                screenXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
                aPoint.X = (float) screenXY[0];
                aPoint.Y = (float) screenXY[1];
                boolean ifDraw = true;
                if (aLayer.getAvoidCollision()) {
                    float aSize = aPB.getSize();
                    aExtent = new Extent();
                    aExtent.minX = aPoint.X - aSize;
                    aExtent.maxX = aPoint.X + aSize;
                    aExtent.minY = aPoint.Y - aSize;
                    aExtent.maxY = aPoint.Y + aSize;
                    if (extentList.isEmpty()) {
                        maxExtent = (Extent) aExtent.clone();
                        extentList.add(aExtent);
                    } else {
                        if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                            extentList.add(aExtent);
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                        } else {
                            for (int i = 0; i < extentList.size(); i++) {
                                if (MIMath.isExtentCross(aExtent, extentList.get(i))) {
                                    ifDraw = false;
                                    break;
                                }
                            }
                            if (ifDraw) {
                                extentList.add(aExtent);
                                maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                            }
                        }
                    }
                }

                if (ifDraw) {
                    if (aPS.isSelected()) {
                        PointBreak newPB = (PointBreak) aPB.clone();
                        newPB.setColor(_selectColor);
                        Draw.drawStationModel(_selectColor, this.getForeground(), aPoint, aPS,
                                g, aPB.getSize(), aPB.getSize() / 8 * 3);
                    } else {
                        Draw.drawStationModel(aPB.getColor(), this.getForeground(), aPoint, aPS,
                                g, aPB.getSize(), aPB.getSize() / 8 * 3);
                    }
                }
            }
        }

//        //Draw identifer shape
//        if (_drawIdentiferShape) {
//            PointShape aPS = (PointShape) aLayer.getShapes().get(aLayer.getIdentiferShape());
//            float[] screenXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
//            aPoint.X = screenXY[0];
//            aPoint.Y = screenXY[1];
//            PointBreak aPB = new PointBreak();
//            aPB.setOutlineColor(Color.red);
//            aPB.setSize(10);
//            aPB.setStyle(PointStyle.Square);
//            aPB.setDrawFill(false);
//
//            Draw.drawPoint(aPoint, aPB, g);
//        }

//        if (this._pointAntiAlias) {
//            g.setRenderingHints(rend);
//        }
    }

    private void drawPolygonLayer(VectorLayer aLayer, Graphics2D g, double LonShift) {
        LegendScheme aLS = aLayer.getLegendScheme();

        for (int s = 0; s < aLayer.getShapeNum(); s++) {
            PolygonShape aPGS = (PolygonShape) aLayer.getShapes().get(s);
            if (aPGS.getLegendIndex() < 0) {
                continue;
            }

            PolygonBreak aPGB = (PolygonBreak) aLS.getLegendBreaks().get(aPGS.getLegendIndex());
            if (aPGB.isDrawShape()) {
                drawPolygonShape(g, aPGS, aPGB, LonShift);
            }
        }

//        //Draw identifer shape
//        if (_drawIdentiferShape) {
//            PolygonShape aPGS = (PolygonShape) aLayer.getShapes().get(aLayer.getIdentiferShape());
//            PolygonBreak aPGB = new PolygonBreak();
//            aPGB.setOutlineColor(Color.red);
//            aPGB.setOutlineSize(2);
//            aPGB.setColor(Color.red);
//            drawPolygonShape(g, aPGS, aPGB, LonShift, 50);
//        }
    }

    private void drawPolylineLayer(VectorLayer aLayer, Graphics2D g, double LonShift) {
        LegendScheme aLS = aLayer.getLegendScheme();
        //PointD wPoint;
        boolean isStreamline = false;
        //double[] screenXY;

        switch (aLayer.getLayerDrawType()) {
//            case TrajLine:
//                //Draw start point symbol                                     
//                PointF aPF = new PointF();
//                for (int s = 0; s < aLayer.getShapeNum(); s++) {
//                    PolylineShape aPLS = (PolylineShape) aLayer.getShapes().get(s);
//                    wPoint = aPLS.getPoints().get(0);
//                    aPF.X = (float) (wPoint.X + LonShift);
//                    aPF.Y = (float) (wPoint.Y);
//                    if (MIMath.pointInExtent(aPF, _drawExtent)) {
//                        screenXY = projToScreen(wPoint.X, wPoint.Y, LonShift);
//                        aPF.X = (float) screenXY[0];
//                        aPF.Y = (float) screenXY[1];
//                        Draw.drawPoint(PointStyle.UpTriangle, aPF, this.getForeground(),
//                                this.getForeground(), 10, true, true, g);
//                    }
//                }
//                break;
            case Streamline:
                isStreamline = true;
                break;
        }

        for (int s = 0; s < aLayer.getShapeNum(); s++) {
            PolylineShape aPLS = (PolylineShape) aLayer.getShapes().get(s);
            if (!aPLS.isVisible()) {
                continue;
            }
            if (aPLS.getLegendIndex() < 0) {
                continue;
            }

            PolylineBreak aPLB = (PolylineBreak) aLS.getLegendBreaks().get(aPLS.getLegendIndex());
            if (aPLB.getDrawPolyline() || aPLB.getDrawSymbol()) {
                drawPolylineShape(g, aPLS, aPLB, LonShift, isStreamline);
            }
        }

//        //Draw identifer shape
//        if (_drawIdentiferShape) {
//            PolylineShape aPLS = (PolylineShape) aLayer.getShapes().get(aLayer.getIdentiferShape());
//            PolylineBreak aPLB = new PolylineBreak();
//            aPLB.setColor(Color.red);
//            aPLB.setSize(2);
//            drawPolylineShape(g, aPLS, aPLB, LonShift, isStreamline);
//        }
    }

    private static float[] getDashPattern(LineStyles style) {
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

    private void drawPolylineShape(Graphics2D g, PolylineShape aPLS, PolylineBreak aPLB, double LonShift,
            boolean isStreamline) {
        drawPolylineShape(g, aPLS, aPLB, LonShift, isStreamline, false);
    }

    private void drawPolylineShape(Graphics2D g, PolylineShape aPLS, PolylineBreak aPLB, double LonShift,
            boolean isStreamline, boolean isSelected) {
        Extent shapeExtent = MIMath.shiftExtentLon(aPLS.getExtent(), LonShift);
        if (!MIMath.isExtentCross(shapeExtent, _drawExtent)) {
            return;
        }

        int len1 = aPLS.getPoints().size();
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, len1);

        Color aColor = aPLB.getColor();
        if (aPLS.isSelected()) {
            aColor = _selectColor;
        }
        float[] dashPattern = getDashPattern(aPLB.getStyle());
        BasicStroke pen = new BasicStroke(aPLB.getSize(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
        g.setColor(aColor);
        g.setStroke(pen);

        List<PointF> drawPs = new ArrayList<PointF>();
        if (aPLB.getDrawPolyline()) {
            for (Polyline aline : aPLS.getPolylines()) {
                double[] sXY;
                PointF[] Points = new PointF[aline.getPointList().size()];
                for (int i = 0; i < aline.getPointList().size(); i++) {
                    PointD wPoint = aline.getPointList().get(i);
                    sXY = projToScreen(wPoint.X, wPoint.Y, LonShift);
                    if (i == 0) {
                        path.moveTo(sXY[0], sXY[1]);
                    } else {
                        path.lineTo(sXY[0], sXY[1]);
                    }
                    Points[i] = new PointF((float) sXY[0], (float) sXY[1]);
                    drawPs.add(new PointF((float) sXY[0], (float) sXY[1]));
                }

                if (isStreamline) {
                    int len = (int) (aPLS.value * 3);
                    PointF aPoint;
                    for (int i = 0; i < Points.length; i++) {
                        if (i > 0 && i < Points.length - 2 && i % len == 0) {
                            //Draw arraw
                            aPoint = Points[i];
                            PointF bPoint = Points[i + 1];
                            double U = bPoint.X - aPoint.X;
                            double V = bPoint.Y - aPoint.Y;
                            double angle = Math.atan((V) / (U)) * 180 / Math.PI;
                            if (Double.isNaN(angle)) {
                                continue;
                            }

                            angle = angle + 90;
                            if (U < 0) {
                                angle = angle + 180;
                            }

                            if (angle >= 360) {
                                angle = angle - 360;
                            }

                            PointF eP1 = new PointF();
                            double aSize = 8;
                            eP1.X = (int) (aPoint.X - aSize * Math.sin((angle + 20.0) * Math.PI / 180));
                            eP1.Y = (int) (aPoint.Y + aSize * Math.cos((angle + 20.0) * Math.PI / 180));
                            path.moveTo(aPoint.X, aPoint.Y);
                            path.lineTo(eP1.X, eP1.Y);

                            eP1.X = (int) (aPoint.X - aSize * Math.sin((angle - 20.0) * Math.PI / 180));
                            eP1.Y = (int) (aPoint.Y + aSize * Math.cos((angle - 20.0) * Math.PI / 180));
                            path.moveTo(aPoint.X, aPoint.Y);
                            path.lineTo(eP1.X, eP1.Y);
                        }
                    }
                }

                g.draw(path);
            }
        }

        //Draw symbol            
        if (aPLB.getDrawSymbol()) {
            Object rend = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < drawPs.size(); i++) {
                if (i % aPLB.getSymbolInterval() == 0) {
                    Draw.drawPoint(aPLB.getSymbolStyle(), drawPs.get(i), aPLB.getSymbolColor(), aPLB.getSymbolColor(),
                            aPLB.getSymbolSize(), true, false, g);
                }
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, rend);
        }

        //Draw selected rectangle
        if (isSelected) {
            Extent aExtent = MIMath.getPointFsExtent(drawPs);

            //aPen.DashStyle = DashStyle.Dash;
            g.setColor(Color.cyan);
            g.drawRect((int) aExtent.minX, (int) aExtent.minY, (int) aExtent.getWidth(), (int) aExtent.getHeight());
        }
    }

    private void drawPolygonShape(Graphics2D g, PolygonShape aPGS, PolygonBreak aPGB, double LonShift) {
        drawPolygonShape(g, aPGS, aPGB, LonShift, false);
    }

    private void drawPolygonShape(Graphics2D g, PolygonShape aPGS, PolygonBreak aPGB, double LonShift,
            boolean isSelected) {
        Extent shapeExtent = MIMath.shiftExtentLon(aPGS.getExtent(), LonShift);
        if (!MIMath.isExtentCross(shapeExtent, _drawExtent)) {
            return;
        }

        List<PointF> pointList = new ArrayList<PointF>();
        for (Polygon aPolygon : aPGS.getPolygons()) {
            pointList.addAll(drawPolygon(g, aPolygon, aPGB, LonShift, aPGS.isSelected()));
        }

        //Draw selected rectangle
        if (isSelected) {
            Extent aExtent = MIMath.getPointFsExtent(pointList);

            //aPen.DashStyle = DashStyle.Dash;
            g.setColor(Color.red);
            g.drawRect((int) aExtent.minX, (int) aExtent.minY, (int) aExtent.getWidth(), (int) aExtent.getHeight());
        }
    }

    private List<PointF> drawPolygon(Graphics2D g, Polygon aPG, PolygonBreak aPGB, double LonShift,
            boolean isSelected) {
        int len = aPG.getOutLine().size();
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD, len);
        PointD wPoint;
        double[] sXY;
        List<PointF> rPoints = new ArrayList<PointF>();
        for (int i = 0; i < aPG.getOutLine().size(); i++) {
            wPoint = aPG.getOutLine().get(i);
            sXY = projToScreen(wPoint.X, wPoint.Y, LonShift);
            if (i == 0) {
                path.moveTo(sXY[0], sXY[1]);
            } else {
                path.lineTo(sXY[0], sXY[1]);
            }
            rPoints.add(new PointF((float) sXY[0], (float) sXY[1]));
        }

        List<PointD> newPList;
        if (aPG.hasHole()) {
            for (int h = 0; h < aPG.getHoleLines().size(); h++) {
                newPList = aPG.getHoleLines().get(h);
                for (int j = 0; j < newPList.size(); j++) {
                    wPoint = newPList.get(j);
                    sXY = projToScreen(wPoint.X, wPoint.Y, LonShift);
                    if (j == 0) {
                        path.moveTo(sXY[0], sXY[1]);
                    } else {
                        path.lineTo(sXY[0], sXY[1]);
                    }
                }
            }
        }
        path.closePath();

        if (aPGB.getDrawFill()) {
            //int alpha = (int)((1 - (double)transparencyPerc / 100.0) * 255);
            //Color aColor = Color.FromArgb(alpha, aPGB.Color);
            Color aColor = aPGB.getColor();
            if (isSelected) {
                aColor = _selectColor;
            }
//                Brush aBrush;
//                if (aPGB.UsingHatchStyle)
//                    aBrush = new HatchBrush(aPGB.Style, aColor, aPGB.BackColor);
//                else
//                    aBrush = new SolidBrush(aColor);

            g.setColor(aColor);
            g.fill(path);
        } else {
            if (isSelected) {
                g.setColor(_selectColor);
                g.fill(path);
            }
        }

        if (aPGB.getDrawOutline()) {
            BasicStroke pen = new BasicStroke(aPGB.getOutlineSize());
            g.setStroke(pen);
            g.setColor(aPGB.getOutlineColor());
            g.draw(path);
        }

        return rPoints;
    }

    /**
     * Draw graphic list
     *
     * @param g Graphics2D
     * @param lonShift Longitude shift
     */
    public void drawGraphicList(Graphics2D g, double lonShift) {
        if (_graphicCollection.size() > 0) {
            Extent aExtent = MIMath.shiftExtentLon(_graphicCollection.getExtent(), lonShift);
            if (!MIMath.isExtentCross(aExtent, _drawExtent)) {
                return;
            }

            Object aSM = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (Graphic aGraphic : _graphicCollection) {
                drawGraphic(g, aGraphic, lonShift);
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aSM);
        }
    }

    /**
     * Draw a graphic
     *
     * @param g Graphics2D
     * @param aGraphic The Graphic
     * @param lonShift Longitude shift
     */
    public void drawGraphic(Graphics2D g, Graphic aGraphic, double lonShift) {
        Extent aExtent = MIMath.shiftExtentLon(aGraphic.getShape().getExtent(), lonShift);
        if (MIMath.isExtentCross(aExtent, _drawExtent)) {
            Object rend = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            double[] sXY;

            //Get screen points
            List<PointD> points = (List<PointD>) aGraphic.getShape().getPoints();
            PointF[] screenPoints = new PointF[points.size()];
            for (int i = 0; i < points.size(); i++) {
                sXY = projToScreen(points.get(i).X, points.get(i).Y, lonShift);
                screenPoints[i] = new PointF((float) sXY[0], (float) sXY[1]);
            }

            //Region oldRegion = g.Clip;
            switch (aGraphic.getShape().getShapeType()) {
                case Polygon:
                case Rectangle:
                case Circle:
                case CurvePolygon:
                case Ellipse:
                    if (((PolygonBreak) aGraphic.getLegend()).isMaskout()) {
                        setClipRegion(g);
                    }
                    break;
            }

            Draw.drawGrahpic(screenPoints, aGraphic, g, _mouseTool == MouseTools.EditVertices);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, rend);
        }
    }

    private void getMaskOutGraphicsPath(Graphics2D g) {
        if (_maskOut.isMask()) {
            int aLayerHandle = getLayerHandleFromName(_maskOut.getMaskLayer());
            if (aLayerHandle > 0) {
                GeneralPath tPath = new GeneralPath();
                VectorLayer aLayer = (VectorLayer) getLayerFromHandle(aLayerHandle);
                double[] lonShiftList = new double[]{0};
                if (_projection.isLonLatMap()) {
                    lonShiftList = new double[]{0, 360, -360};
                }

                for (double lonShift : lonShiftList) {
                    for (PolygonShape aPGS : (List<PolygonShape>) aLayer.getShapes()) {
                        for (Polygon aPolygon : aPGS.getPolygons()) {
                            GeneralPath aPath = new GeneralPath();
                            PointD wPoint;
                            double[] sXY;
                            for (int i = 0; i < aPolygon.getOutLine().size(); i++) {
                                wPoint = aPolygon.getOutLine().get(i);
                                sXY = projToScreen(wPoint.X, wPoint.Y, lonShift);
                                if (i == 0) {
                                    aPath.moveTo(sXY[0], sXY[1]);
                                } else {
                                    aPath.lineTo(sXY[0], sXY[1]);
                                }
                            }
                            tPath.append(aPath, false);
                        }
                    }
                }

                _maskOutGraphicsPath = tPath;
            }
        }
    }

    private void setClipRegion(Graphics2D g) {

        if (_maskOut.isMask()) {
            int aLayerHandle = getLayerHandleFromName(_maskOut.getMaskLayer());
            if (aLayerHandle > 0) {
                getMaskOutGraphicsPath(g);
                g.setClip(this._maskOutGraphicsPath);
            }
        }
    }

    /**
     * Draw identifer shape
     *
     * @param g Graphics2D
     * @param aShape A shape
     */
    public void drawIdShape(Graphics2D g, Shape aShape) {
        List<Double> lonShifts = new ArrayList<Double>();
        if (MIMath.isExtentCross(this.getViewExtent(), aShape.getExtent())) {
            lonShifts.add(new Double(0));
        }
        if (MIMath.isExtentCross(this.getViewExtent(), MIMath.shiftExtentLon(aShape.getExtent(), 360))) {
            lonShifts.add(new Double(360));
        }
        if (MIMath.isExtentCross(this.getViewExtent(), MIMath.shiftExtentLon(aShape.getExtent(), -360))) {
            lonShifts.add(new Double(-360));
        }

        getMaskOutGraphicsPath(g);

        for (double LonShift : lonShifts) {
            switch (aShape.getShapeType()) {
                case Point:
                case PointM:
                case PointZ:
                    PointShape aPS = (PointShape) aShape;
                    double[] sXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
                    PointF aPoint = new PointF();
                    aPoint.X = (float) sXY[0];
                    aPoint.Y = (float) sXY[1];
                    PointBreak aPB = new PointBreak();
                    aPB.setOutlineColor(Color.red);
                    aPB.setSize(10);
                    aPB.setStyle(PointStyle.Square);
                    aPB.setDrawFill(false);

                    Draw.drawPoint(aPoint, aPB, g);
                    break;
                case Polyline:
                case PolylineM:
                case PolylineZ:
                    PolylineShape aPLS = (PolylineShape) aShape;
                    PolylineBreak aPLB = new PolylineBreak();
                    aPLB.setColor(Color.red);
                    aPLB.setSize(2);
                    drawPolylineShape(g, aPLS, aPLB, LonShift, false);
                    break;
                case Polygon:
                case PolygonM:
                    PolygonShape aPGS = (PolygonShape) aShape;
                    PolygonBreak aPGB = new PolygonBreak();
                    aPGB.setOutlineColor(Color.red);
                    aPGB.setOutlineSize(2);
                    aPGB.setColor(Color.red);
                    drawPolygonShape(g, aPGS, aPGB, LonShift);
                    break;
            }
        }
    }

    /**
     * Draw identifer shape
     *
     * @param g Graphics2D
     * @param aShape The identifer shape
     * @param rect Rectangle extent
     */
    public void drawIdShape(Graphics2D g, Shape aShape, Rectangle rect) {
        List<Double> lonShifts = new ArrayList<Double>();
        if (MIMath.isExtentCross(this.getViewExtent(), aShape.getExtent())) {
            lonShifts.add(new Double(0));
        }
        if (MIMath.isExtentCross(this.getViewExtent(), MIMath.shiftExtentLon(aShape.getExtent(), 360))) {
            lonShifts.add(new Double(360));
        }
        if (MIMath.isExtentCross(this.getViewExtent(), MIMath.shiftExtentLon(aShape.getExtent(), -360))) {
            lonShifts.add(new Double(-360));
        }

        AffineTransform oldMatrix = g.getTransform();
        java.awt.Shape oldRegion = g.getClip();
        g.setClip(rect);

        getMaskOutGraphicsPath(g);

        g.translate(rect.x, rect.y);

        for (double LonShift : lonShifts) {
            switch (aShape.getShapeType()) {
                case Point:
                case PointM:
                case PointZ:
                    PointShape aPS = (PointShape) aShape;
                    double[] sXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, LonShift);
                    PointF aPoint = new PointF();
                    aPoint.X = (float) sXY[0];
                    aPoint.Y = (float) sXY[1];
                    PointBreak aPB = new PointBreak();
                    aPB.setOutlineColor(Color.red);
                    aPB.setSize(10);
                    aPB.setStyle(PointStyle.Square);
                    aPB.setDrawFill(false);

                    Draw.drawPoint(aPoint, aPB, g);
                    break;
                case Polyline:
                case PolylineM:
                case PolylineZ:
                    PolylineShape aPLS = (PolylineShape) aShape;
                    PolylineBreak aPLB = new PolylineBreak();
                    aPLB.setColor(Color.red);
                    aPLB.setSize(2);
                    drawPolylineShape(g, aPLS, aPLB, LonShift, false);
                    break;
                case Polygon:
                case PolygonM:
                    PolygonShape aPGS = (PolygonShape) aShape;
                    PolygonBreak aPGB = new PolygonBreak();
                    aPGB.setOutlineColor(Color.red);
                    aPGB.setOutlineSize(2);
                    aPGB.setColor(Color.red);
                    drawPolygonShape(g, aPGS, aPGB, LonShift);
                    break;
            }
        }

        g.setTransform(oldMatrix);
        g.setClip(oldRegion);
    }

    private void drawLayerLabels(Graphics2D g, VectorLayer aLayer, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        Font drawFont;
        List<Extent> extentList = new ArrayList<Extent>();
        Extent maxExtent = new Extent();
        Extent aExtent;
        int i, j;
        List<Graphic> LabelPoints = aLayer.getLabelPoints();
        String LabelStr;
        PointF aPoint = new PointF();

        for (i = 0; i < LabelPoints.size(); i++) {
            Graphic aLP = LabelPoints.get(i);
            PointShape aPS = (PointShape) aLP.getShape();
            LabelBreak aLB = (LabelBreak) aLP.getLegend();
            aPS.setVisible(true);
            LabelStr = aLB.getText();
            aPoint.X = (float) aPS.getPoint().X;
            aPoint.Y = (float) aPS.getPoint().Y;
            drawFont = aLB.getFont();
            if (aPoint.X + LonShift < _drawExtent.minX || aPoint.X + LonShift > _drawExtent.maxX
                    || aPoint.Y < _drawExtent.minY || aPoint.Y > _drawExtent.maxY) {
                continue;
            }
            double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
            aPoint.X = (float) xy[0];
            aPoint.Y = (float) xy[1];
            FontMetrics metrics = g.getFontMetrics(drawFont);
            Dimension labSize = new Dimension(metrics.stringWidth(LabelStr), metrics.getHeight());
            switch (aLB.getAlignType()) {
                case Center:
                    aPoint.X = (float) xy[0] - labSize.width / 2;
                    break;
                case Left:
                    aPoint.X = (float) xy[0] - labSize.width;
                    break;
            }
            aPoint.Y += labSize.height / 2;
            aPoint.Y -= aLB.getYShift();
            aPoint.X += aLB.getXShift();

            AffineTransform tempTrans = g.getTransform();
            if (aLB.getAngle() != 0) {
                AffineTransform myTrans = new AffineTransform();
                myTrans.translate(aPoint.X, aPoint.Y);
                myTrans.rotate(aLB.getAngle() * Math.PI / 180);
                g.setTransform(myTrans);
                aPoint.X = 0;
                aPoint.Y = 0;
            }

            boolean ifDraw = true;
            aExtent = new Extent();
            aExtent.minX = aPoint.X;
            aExtent.maxX = aPoint.X + labSize.width;
            aExtent.minY = aPoint.Y - labSize.height;
            aExtent.maxY = aPoint.Y;
            if (aLayer.getLabelSet().isAvoidCollision()) {
                //Judge extent                                        
                if (extentList.isEmpty()) {
                    maxExtent = (Extent) aExtent.clone();
                    extentList.add(aExtent);
                } else {
                    if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                        extentList.add(aExtent);
                        maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                    } else {
                        for (j = 0; j < extentList.size(); j++) {
                            if (MIMath.isExtentCross(aExtent, extentList.get(j))) {
                                ifDraw = false;
                                break;
                            }
                        }
                        if (ifDraw) {
                            extentList.add(aExtent);
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                        } else {
                            aPS.setVisible(false);
                        }
                    }
                }
            }

            if (ifDraw) {
                if (aLayer.getLabelSet().isDrawShadow()) {
                    g.setColor(aLayer.getLabelSet().getShadowColor());
                    g.fill(new Rectangle.Float(aPoint.X, aPoint.Y - labSize.height, labSize.width, labSize.height));
                }
                g.setFont(drawFont);
                //g.setColor(aLayer.getLabelSet().getLabelColor());
                g.setColor(aLP.getLegend().getColor());
                g.drawString(LabelStr, aPoint.X, aPoint.Y);

                //Draw selected rectangle
                if (aPS.isSelected()) {
                    float[] dashPattern = new float[]{2.0F, 1.0F};
                    g.setColor(Color.cyan);
                    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
                    g.draw(new Rectangle.Float((float) aExtent.minX, (float) aExtent.minY, labSize.width, labSize.height));
                }
            }

            if (aLB.getAngle() != 0) {
                g.setTransform(tempTrans);
            }
        }
    }

    private void drawLayerCharts(Graphics2D g, VectorLayer aLayer, double LonShift) {
        Extent lExtent = MIMath.shiftExtentLon(aLayer.getExtent(), LonShift);
        if (!MIMath.isExtentCross(lExtent, _drawExtent)) {
            return;
        }

        LegendScheme aLS = aLayer.getLegendScheme();
        List<Shape> shapeList = new ArrayList<Shape>(aLayer.getShapes());

        //Font drawFont = aLayer.LabelSet.LabelFont;
        //SolidBrush labelBrush = new SolidBrush(aLayer.LabelSet.LabelColor);

        List<Extent> extentList = new ArrayList<Extent>();
        Extent maxExtent = new Extent();
        Extent aExtent = new Extent();
        int i, j;
        List<Graphic> chartPoints = aLayer.getChartPoints();
        PointF aPoint = new PointF();
        float X, Y;
        X = 0;
        Y = 0;

        for (i = 0; i < chartPoints.size(); i++) {
            Graphic aCP = chartPoints.get(i);
            PointShape aPS = (PointShape) aCP.getShape();
            ChartBreak aCB = (ChartBreak) aCP.getLegend();
            aPS.setVisible(true);
            aPoint.X = (float) aPS.getPoint().X;
            aPoint.Y = (float) aPS.getPoint().Y;
            if (aPoint.X + LonShift < _drawExtent.minX || aPoint.X + LonShift > _drawExtent.maxX
                    || aPoint.Y < _drawExtent.minY || aPoint.Y > _drawExtent.maxY) {
                continue;
            }
            double[] xy = projToScreen(aPoint.X, aPoint.Y, LonShift);
            aPoint.X = (float) xy[0];
            aPoint.Y = (float) xy[1];

            aExtent = aCB.getDrawExtent(aPoint);
            aPoint.X = (float) aExtent.minX;
            aPoint.Y = (float) aExtent.maxY;

            boolean ifDraw = true;
            if (aLayer.getChartSet().isAvoidCollision()) {
                //Judge extent                                        
                if (extentList.isEmpty()) {
                    maxExtent = aExtent;
                    extentList.add(aExtent);
                } else {
                    if (!MIMath.isExtentCross(aExtent, maxExtent)) {
                        extentList.add(aExtent);
                        maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                    } else {
                        for (j = 0; j < extentList.size(); j++) {
                            if (MIMath.isExtentCross(aExtent, extentList.get(j))) {
                                ifDraw = false;
                                break;
                            }
                        }
                        if (ifDraw) {
                            extentList.add(aExtent);
                            maxExtent = MIMath.getLagerExtent(maxExtent, aExtent);
                        } else {
                            aPS.setVisible(false);
                        }
                    }
                }
            }

            if (ifDraw) {
                Draw.drawChartPoint(aPoint, aCB, g);

                //Draw selected rectangle
                if (aPS.isSelected()) {
                    float[] dashPattern = new float[]{2.0F, 1.0F};
                    g.setColor(Color.cyan);
                    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f));
                    g.draw(new Rectangle.Float((float) aExtent.minX, (float) aExtent.minY, (float) aExtent.getWidth(), (float) aExtent.getHeight()));
                }
            }
        }

    }

    private void drawXYGrid(Graphics2D g, List<String> XGridStrs, List<String> YGridStrs) {
        if (this._layers.size() == 0) {
            return;
        }

        int XDelt, YDelt, vXNum, vYNum;
        vXNum = (int) (_drawExtent.maxX - _drawExtent.minX);
        vYNum = (int) (_drawExtent.maxY - _drawExtent.minY);
        XDelt = (int) vXNum / 10 + 1;
        YDelt = (int) vYNum / 10 + 1;

        int i;
        PointF sP, eP;
        sP = new PointF(0, 0);
        eP = new PointF(0, 0);
        float X = 0, Y = 0;
        Color lineColor = _gridLineColor;
        float[] dashPattern = getDashPattern(_gridLineStyle);
        BasicStroke pen = new BasicStroke(_gridLineSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
        Font drawFont = new Font("Arial", Font.PLAIN, 10);
        if (!_drawGridLine) {

            lineColor = this.getForeground();
            dashPattern = getDashPattern(LineStyles.Solid);
            pen = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0.0f);
        }
        g.setColor(lineColor);
        g.setStroke(pen);
        g.setFont(drawFont);

        //SolidBrush aBrush = new SolidBrush(this.ForeColor);
        String drawStr;
        int XGridNum = XGridStrs.size();
        int YGridNum = YGridStrs.size();

        //Draw X grid
        _gridLabels = new ArrayList<GridLabel>();
        for (i = 0; i < XGridNum; i += XDelt) {
            if (i >= _drawExtent.minX && i <= _drawExtent.maxX) {
                double[] sXY = projToScreen(i, _drawExtent.minY, 0);
                sP.X = (float) sXY[0];
                sP.Y = (float) sXY[1];

                if (_drawGridLine) {
                    sXY = projToScreen(i, _drawExtent.maxY, 0);
                    eP.X = (float) sXY[0];
                    eP.Y = (float) sXY[1];
                    if (i > _drawExtent.minX && i < _drawExtent.maxX) {
                        g.draw(new Line2D.Float(sP.X, sP.Y, eP.X, eP.Y));
                    }
                }

                drawStr = XGridStrs.get(i);

                GridLabel aGL = new GridLabel();
                aGL.setBorder(true);
                aGL.setLabPoint(new PointD(sP.X, sP.Y));
                aGL.setLabDirection(Direction.South);
                aGL.setLabString(drawStr);
                _gridLabels.add(aGL);
            }
        }

        //Draw Y grid   
        _yGridPosLabel.clear();
        for (i = 0; i < YGridNum; i += YDelt) {
            if (i > _drawExtent.minY && i < _drawExtent.maxY) {
                double[] sXY = projToScreen(_drawExtent.minX, i, 0);
                sP.X = (float) sXY[0];
                sP.Y = (float) sXY[1];

                if (_drawGridLine) {
                    sXY = projToScreen(_drawExtent.maxX, i, 0);
                    eP.X = (float) sXY[0];
                    eP.Y = (float) sXY[1];
                    if (i > _drawExtent.minY && i < _drawExtent.maxY) {
                        g.draw(new Line2D.Float(sP.X, sP.Y, eP.X, eP.Y));
                    }
                }

                drawStr = YGridStrs.get(i);

                GridLabel aGL = new GridLabel();
                aGL.setBorder(true);
                aGL.setLabPoint(new PointD(sP.X, sP.Y));
                aGL.setLabDirection(Direction.Weast);
                aGL.setLabString(drawStr);
                _gridLabels.add(aGL);
            }
        }
    }

    /**
     * Export to a picture file
     *
     * @param aFile File path
     */
    public void exportToPicture(String aFile) throws FileNotFoundException, PrintException, IOException {
        if (aFile.endsWith(".ps")) {
            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
            String mimeType = "application/postscript";
            StreamPrintServiceFactory[] factories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor, mimeType);
            FileOutputStream out = new FileOutputStream(aFile);
            if (factories.length > 0) {
                PrintService service = factories[0].getPrintService(out);
                SimpleDoc doc = new SimpleDoc(new Printable() {
                    @Override
                    public int print(Graphics g, PageFormat pf, int page) {
                        if (page >= 1) {
                            return Printable.NO_SUCH_PAGE;
                        } else {
                            double sf1 = pf.getImageableWidth() / (getWidth() + 1);
                            double sf2 = pf.getImageableHeight() / (getHeight() + 1);
                            double s = Math.min(sf1, sf2);
                            Graphics2D g2 = (Graphics2D) g;
                            g2.translate((pf.getWidth() - pf.getImageableWidth()) / 2, (pf.getHeight() - pf.getImageableHeight()) / 2);
                            g2.scale(s, s);

                            paintGraphics(g2);
                            return Printable.PAGE_EXISTS;
                        }
                    }
                }, flavor, null);
                DocPrintJob job = service.createPrintJob();
                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                job.print(doc, attributes);
                out.close();
            }
        } else {
            String extension = aFile.substring(aFile.lastIndexOf('.') + 1);
            ImageIO.write(this._mapBitmap, extension, new File(aFile));
        }
    }

    // </editor-fold>
    // <editor-fold desc="Lon/Lat Layer">
    /**
     * Generate longitude/latitude grid line layer
     *
     * @return Lon/lat layer
     */
    public VectorLayer generateLonLatLayer() {
        return generateLonLatLayer(_gridXOrigin, _gridYOrigin, _gridXDelt, _gridYDelt);
    }

    private VectorLayer generateLonLatLayer(float origin_Lon, float origin_Lat, float Delt_Lon, float Delt_Lat) {
        //Create lon/lat layer                        
        PolylineShape aPLS;
        int lineNum;
        float lon, lat;
        List<PointD> PList;

        VectorLayer aLayer = new VectorLayer(ShapeTypes.Polyline);
        String columnName = "Value";
        Field aDC = new Field(columnName, DataTypes.Float);
        aLayer.editAddField(aDC);
        aDC = new Field("Longitude", DataTypes.String);
        aLayer.editAddField(aDC);
        int shapeNum;

        float refLon = (float) _projection.getRefCutLon();

        //Longitude
        lineNum = 0;
        Extent extent = new Extent();
        boolean isLabelLon = false;
        lon = origin_Lon;
        while (true) {
            if (lon >= origin_Lon && lineNum > 0 && lon - Delt_Lon < origin_Lon) {
                break;
            }

            if (lon > 180) {
                lon = lon - 360;
            }

            if (!_projection.isLonLatMap()) {
                if (refLon == 180 || refLon == -180) {
                    if (lon == 180 || lon == -180) {
                        isLabelLon = true;
                        lon += Delt_Lon;
                        continue;
                    }
                } else {
                    if (MIMath.doubleEquals(lon, refLon)) {
                        isLabelLon = true;
                        lon += Delt_Lon;
                        continue;
                    }
                }
            }

            aPLS = new PolylineShape();
            aPLS.value = lon;
            extent.minX = lon;
            extent.maxX = lon;
            extent.minY = -90;
            extent.maxY = 90;
            aPLS.setExtent(extent);
            PList = new ArrayList<PointD>();

            lat = -90;
            while (lat <= 90) {

                PList.add(new PointD(lon, lat));
                lat += 1;
            }
            aPLS.setPoints(PList);

            shapeNum = aLayer.getShapeNum();
            try {
                if (aLayer.editInsertShape(aPLS, shapeNum)) {
                    aLayer.editCellValue(0, shapeNum, lon);
                    aLayer.editCellValue(1, shapeNum, "Y");
                }
            } catch (Exception ex) {
                Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
            }

            lineNum += 1;
            lon += Delt_Lon;
        }

        //Add longitudes around reference longitude
        switch (_projection.getProjInfo().getProjectionName()) {
            case LongLat:
            case Oblique_Stereographic_Alternative:
                break;
            default:
                float value;
                lon = refLon - 0.0001f;
                if (lon < -180) {
                    lon += 360;
                }
                aPLS = new PolylineShape();
                aPLS.value = lon;
                extent.minX = lon;
                extent.maxX = lon;
                extent.minY = -90;
                extent.maxY = 90;
                aPLS.setExtent(extent);
                PList = new ArrayList<PointD>();

                lat = -90;
                while (lat <= 90) {

                    PList.add(new PointD(lon, lat));
                    lat += 1;
                }
                aPLS.setPoints(PList);

                if (isLabelLon) {
                    value = refLon;
                } else {
                    value = -9999.0f;
                }
                shapeNum = aLayer.getShapeNum();
                try {
                    if (aLayer.editInsertShape(aPLS, shapeNum)) {
                        aLayer.editCellValue(0, shapeNum, value);
                        aLayer.editCellValue(1, shapeNum, "Y");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
                }

                lon = refLon + 0.0001f;
                if (lon > 180) {
                    lon -= 360;
                }
                aPLS = new PolylineShape();
                aPLS.value = lon;
                extent.minX = lon;
                extent.maxX = lon;
                extent.minY = -90;
                extent.maxY = 90;
                aPLS.setExtent(extent);
                PList = new ArrayList<PointD>();

                lat = -90;
                while (lat <= 90) {

                    PList.add(new PointD(lon, lat));
                    lat += 1;
                }
                aPLS.setPoints(PList);

                if (isLabelLon) {
                    value = refLon;
                } else {
                    value = -9999.0f;
                }
                shapeNum = aLayer.getShapeNum();
                try {
                    if (aLayer.editInsertShape(aPLS, shapeNum)) {
                        aLayer.editCellValue(0, shapeNum, value);
                        aLayer.editCellValue(1, shapeNum, "Y");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }

        //Latitue
        lat = -90;
        while (lat <= 90) {
            aPLS = new PolylineShape();
            aPLS.value = lat;
            extent.minX = -180;
            extent.minY = lat;
            extent.maxY = lat;
            extent.maxX = 180;
            aPLS.setExtent(extent);
            PList = new ArrayList<PointD>();

            lon = -180;
            while (lon <= 180) {
                PList.add(new PointD(lon, lat));
                lon += 1;
            }
            aPLS.setPoints(PList);

            shapeNum = aLayer.getShapeNum();
            try {
                if (aLayer.editInsertShape(aPLS, shapeNum)) {
                    aLayer.editCellValue(0, shapeNum, lat);
                    aLayer.editCellValue(1, shapeNum, "N");
                }
            } catch (Exception ex) {
                Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
            }

            lineNum += 1;
            lat += Delt_Lat;
        }

        //Generate layer
        Extent lExt = new Extent();
        lExt.minX = -180;
        lExt.maxX = 180;
        lExt.minY = -90;
        lExt.maxY = 90;

        aLayer.setExtent(lExt);
        aLayer.setLayerName("Map_LonLat");
        aLayer.setFileName("");
        aLayer.setLayerDrawType(LayerDrawType.Map);
        aLayer.setLegendScheme(LegendManage.createSingleSymbolLegendScheme(ShapeTypes.Polyline, Color.darkGray, 1.0F));
        PolylineBreak aPLB = (PolylineBreak) aLayer.getLegendScheme().getLegendBreaks().get(0);
        aPLB.setStyle(LineStyles.Dash);
        aLayer.setVisible(true);

        //Get projected lon/lat layer   
        return aLayer;
    }

    private void updateLonLatLayer() {
        if (_lonLatLayer == null || _gridDeltChanged) {
            _lonLatLayer = generateLonLatLayer();
            if (!_projection.isLonLatMap()) {
                ProjectionInfo toProj = _projection.getProjInfo();
                _projection.projectLayer(_lonLatLayer, toProj);
            }

            _gridDeltChanged = false;
        }
    }

    private void getLonLatGridLabels() {
        if (_isGeoMap) {
            _gridLabels = new ArrayList<GridLabel>();
            if (_projection.isLonLatMap()) {
                if (_lonLatLayer == null) {
                    return;
                }

                for (int i = 0; i < _lonLatLayer.getShapeNum(); i++) {
                    PolylineShape aPLS = (PolylineShape) _lonLatLayer.getShapes().get(i);
                    String labStr = _lonLatLayer.getCellValue(0, i).toString().trim();
                    if (labStr.endsWith(".0")) {
                        labStr = labStr.substring(0, labStr.length() - 2);
                    }
                    float value = Float.parseFloat(labStr);
                    String isLonStr = _lonLatLayer.getCellValue(1, i).toString();
                    boolean isLon = ("Y".equals(isLonStr));
                    if (isLon) {
                        if (value == -180) {
                            labStr = "180";
                        } else if (!(value == 0 || value == 180)) {
                            if (labStr.substring(0, 1).equals("-")) {
                                labStr = labStr.substring(1) + "W";
                            } else {
                                labStr = labStr + "E";
                            }
                        }
                    } else {
                        if (!(value == 0)) {
                            if (labStr.substring(0, 1).equals("-")) {
                                labStr = labStr.substring(1) + "S";
                            } else {
                                labStr = labStr + "N";
                            }
                        }
                    }
                    List<GridLabel> gLabels = new ArrayList<GridLabel>();
                    for (int l = 0; l < aPLS.getPolylines().size(); l++) {
                        Polyline aPL = aPLS.getPolylines().get(l);
                        gLabels.addAll(GeoComputation.getGridLabels_StraightLine(aPL, _drawExtent, isLon));

                        if (isLon) {
                            List<PointD> aPList = new ArrayList<PointD>(aPL.getPointList());
                            for (int j = 0; j < aPList.size(); j++) {
                                PointD aP = (PointD) aPList.get(j).clone();
                                aP.X = aP.X + 360;
                            }
                            aPL = new Polyline();
                            aPL.setPointList(aPList);
                            gLabels.addAll(GeoComputation.getGridLabels_StraightLine(aPL, _drawExtent, isLon));
                            for (int j = 0; j < aPList.size(); j++) {
                                PointD aP = (PointD) aPList.get(j).clone();
                                aP.X = aP.X - 720;
                            }
                            aPL = new Polyline();
                            aPL.setPointList(aPList);
                            gLabels.addAll(GeoComputation.getGridLabels_StraightLine(aPL, _drawExtent, isLon));
                        }
                    }

                    for (int j = 0; j < gLabels.size(); j++) {
                        gLabels.get(j).setLabString(labStr);
                    }

                    _gridLabels.addAll(gLabels);
                }
            } else {
                if (_lonLatProjLayer == null) {
                    return;
                }

                List<GridLabel> gridLabels = new ArrayList<GridLabel>();
                for (int i = 0; i < _lonLatProjLayer.getShapeNum(); i++) {
                    PolylineShape aPLS = (PolylineShape) _lonLatProjLayer.getShapes().get(i);
                    String labStr = _lonLatProjLayer.getCellValue(0, i).toString().trim();
                    float value = Float.parseFloat(labStr);
                    if (value == -9999.0) {
                        continue;
                    }

                    String isLonStr = _lonLatProjLayer.getCellValue(1, i).toString();
                    boolean isLon = (isLonStr.equals("Y"));
                    if (isLon) {
                        if (value == -180) {
                            labStr = "180";
                        } else if (!(value == 0 || value == 180)) {
                            if (labStr.substring(0, 1).equals("-")) {
                                labStr = labStr.substring(1) + "W";
                            } else {
                                labStr = labStr + "E";
                            }
                        }
                    } else {
                        if (value == 90 || value == -90) {
                            continue;
                        }

                        if (!(value == 0)) {
                            if (labStr.substring(0, 1).equals("-")) {
                                labStr = labStr.substring(1) + "S";
                            } else {
                                labStr = labStr + "N";
                            }
                        }
                    }
                    List<GridLabel> gLabels = new ArrayList<GridLabel>();
                    for (Polyline aPL : aPLS.getPolylines()) {
                        gLabels.addAll(GeoComputation.getGridLabels(aPL, _drawExtent, isLon));
                    }

                    for (int j = 0; j < gLabels.size(); j++) {
                        gLabels.get(j).setLabString(labStr);
                        gLabels.get(j).setValue(value);
                    }

                    gridLabels.addAll(gLabels);
                }

                //Adjust for diferent projections
                float refLon;
                switch (_projection.getProjInfo().getProjectionName()) {
                    case Lambert_Conformal_Conic:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (!aGL.isLongitude()) {
                                    aGL.setLabDirection(Direction.North);
                                } else {
                                    if (aGL.getLabPoint().Y > 0 && Math.abs(aGL.getLabPoint().X) < 1000) {
                                        continue;
                                    }

                                    if (MIMath.lonDistance(aGL.getValue(), (float) _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getProjectionLongitudeDegrees()) > 60) {
                                        if (aGL.getLabPoint().X < 0) {
                                            aGL.setLabDirection(Direction.Weast);
                                        } else {
                                            aGL.setLabDirection(Direction.East);
                                        }
                                    } else {
                                        aGL.setLabDirection(Direction.South);
                                    }
                                }
                            }
                            _gridLabels.add(aGL);
                        }
                        break;
                    case Albers_Equal_Area:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (!aGL.isLongitude()) {
                                    aGL.setLabDirection(Direction.North);
                                } else {
                                    if (aGL.getLabPoint().Y > 7000000 && Math.abs(aGL.getLabPoint().X) < 5000000) {
                                        continue;
                                    }

                                    if (MIMath.lonDistance(aGL.getValue(), (float) _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getProjectionLongitudeDegrees()) > 60) {
                                        if (aGL.getLabPoint().X < 0) {
                                            aGL.setLabDirection(Direction.Weast);
                                        } else {
                                            aGL.setLabDirection(Direction.East);
                                        }
                                    } else {
                                        aGL.setLabDirection(Direction.South);
                                    }
                                }
                            }
                            _gridLabels.add(aGL);
                        }
                        break;
                    case North_Polar_Stereographic_Azimuthal:
                    case South_Polar_Stereographic_Azimuthal:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (aGL.isLongitude()) {
                                    if (Math.abs(aGL.getLabPoint().X) < 1000 && Math.abs(aGL.getLabPoint().Y) < 1000) {
                                        continue;
                                    }

                                    refLon = (float) _projection.getProjInfo().getCoordinateReferenceSystem().getProjection().getProjectionLongitudeDegrees();
                                    if (MIMath.lonDistance(aGL.getValue(), refLon) < 45) {
                                        if (_projection.getProjInfo().getProjectionName() == ProjectionNames.North_Polar_Stereographic_Azimuthal) {
                                            aGL.setLabDirection(Direction.South);
                                        } else {
                                            aGL.setLabDirection(Direction.North);
                                        }
                                    } else {
                                        refLon = MIMath.lonAdd(refLon, 180);
                                        if (MIMath.lonDistance(aGL.getValue(), refLon) < 45) {
                                            if (_projection.getProjInfo().getProjectionName() == ProjectionNames.North_Polar_Stereographic_Azimuthal) {
                                                aGL.setLabDirection(Direction.North);
                                            } else {
                                                aGL.setLabDirection(Direction.South);
                                            }
                                        } else {
                                            if (aGL.getLabPoint().X < 0) {
                                                aGL.setLabDirection(Direction.Weast);
                                            } else {
                                                aGL.setLabDirection(Direction.East);
                                            }
                                        }
                                    }
                                } else {
                                    continue;
                                }
                            }

                            _gridLabels.add(aGL);
                        }
                        break;
                    case Robinson:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (aGL.isLongitude()) {
                                    if (aGL.getLabPoint().Y < 0) {
                                        aGL.setLabDirection(Direction.South);
                                    } else {
                                        aGL.setLabDirection(Direction.North);
                                    }
                                } else {
                                    if (aGL.getLabPoint().X < 0) {
                                        aGL.setLabDirection(Direction.Weast);
                                    } else {
                                        aGL.setLabDirection(Direction.East);
                                    }
                                }
                            }

                            _gridLabels.add(aGL);
                        }
                        break;
                    case Molleweide:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (aGL.isLongitude()) {
                                    continue;
                                } else {
                                    if (aGL.getLabPoint().X < 0) {
                                        aGL.setLabDirection(Direction.Weast);
                                    } else {
                                        aGL.setLabDirection(Direction.East);
                                    }
                                }
                            }

                            _gridLabels.add(aGL);
                        }
                        break;
                    case Orthographic_Azimuthal:
                        //case Geostationary:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                if (aGL.isLongitude()) {
                                    continue;
                                } else {
                                    if (aGL.getLabPoint().X < 0) {
                                        aGL.setLabDirection(Direction.Weast);
                                    } else {
                                        aGL.setLabDirection(Direction.East);
                                    }
                                }
                            }

                            _gridLabels.add(aGL);
                        }
                        break;
                    case Oblique_Stereographic_Alternative:
                    case Transverse_Mercator:
                        for (GridLabel aGL : gridLabels) {
                            if (!aGL.isBorder()) {
                                continue;
                            }

                            _gridLabels.add(aGL);
                        }
                        break;
                    default:
                        _gridLabels = gridLabels;
                        break;
                }
            }

            for (int i = 0; i < _gridLabels.size(); i++) {
                GridLabel aGL = _gridLabels.get(i);
                double[] sXY = projToScreen(aGL.getLabPoint().X, aGL.getLabPoint().Y);
                aGL.setLabPoint(new PointD(sXY[0], sXY[1]));
                //_gridLabels[i] = aGL;
            }
        }
    }

    // </editor-fold>
    // <editor-fold desc="Coordinate Transfer">
    /**
     * Convert coordinate from map to screen
     *
     * @param projX Map X
     * @param projY Map Y
     * @return Screen X/Y array
     */
    public double[] projToScreen(double projX, double projY) {
        double screenX = (projX - _drawExtent.minX) * _scaleX;
        double screenY = (_drawExtent.maxY - projY) * _scaleY;

        return new double[]{screenX, screenY};
    }

    /**
     * Convert coordinate from map to screen
     *
     * @param projX Map X
     * @param projY Map Y
     * @param LonShift Longitude shift
     * @return Screen X/Y array
     */
    public double[] projToScreen(double projX, double projY, double LonShift) {
        double screenX = (projX + LonShift - _drawExtent.minX) * _scaleX;
        double screenY = (_drawExtent.maxY - projY) * _scaleY;

        return new double[]{screenX, screenY};
    }

    /**
     * Longitude/Latitude convert to screen X/Y
     *
     * @param lon Longitude
     * @param lat Latitude
     * @return Screen X/Y array
     */
    public double[] lonLatToScreen(double lon, double lat) {
        double screenX = 0.0, screenY = 0.0;
        if (_projection.isLonLatMap()) {
            double lonShift = getLonShift(lon);
            double[] sxy = projToScreen(lon, lat, lonShift);
            screenX = sxy[0];
            screenY = sxy[1];
        } else {
            ProjectionInfo fromProj = KnownCoordinateSystems.geographic.world.WGS1984;
            ProjectionInfo toProj = _projection.getProjInfo();
            double[][] points = new double[1][];
            points[0] = new double[]{lon, lat};
            try {
                Reproject.reprojectPoints(points, fromProj, toProj, 0, 1);
                double projX = points[0][0];
                double projY = points[0][1];
                double[] sxy = projToScreen(projX, projY);
                screenX = sxy[0];
                screenY = sxy[1];
            } catch (Exception e) {
            }
        }

        return new double[]{screenX, screenY};
    }

    /**
     * Convert coordinate from screen to map
     *
     * @param screenX Screen X
     * @param screenY Screen Y
     * @return Projected X/Y
     */
    public double[] screenToProj(double screenX, double screenY) {
        double projX = screenX / _scaleX + _drawExtent.minX;
        double projY = _drawExtent.maxY - screenY / _scaleY;

        return new double[]{projX, projY};
    }

    /**
     * Convert coordiante from screen to map
     *
     * @param screenX Screen X
     * @param screenY Screen Y
     * @param zoom Zoom factor
     * @return Project X/Y
     */
    public double[] screenToProj(double screenX, double screenY, double zoom) {
        double projX = screenX / _scaleX * zoom + _drawExtent.minX;
        double projY = _drawExtent.maxY - screenY / _scaleY * zoom;

        return new double[]{projX, projY};
    }

    /**
     * Convert coordinate from screen to map
     *
     * @param screenX Screen X
     * @param screenY Screen Y
     * @return Projected X/Y
     */
    public float[] screenToProj(float screenX, float screenY) {
        float projX = (float) (screenX / _scaleX + _drawExtent.minX);
        float projY = (float) (_drawExtent.maxY - screenY / _scaleY);

        return new float[]{projX, projY};
    }

    /**
     * Convert coordinate from screen to map
     *
     * @param screenX Screen X
     * @param screenY Screen Y
     * @param LonShift Longitude shift
     * @return Projected X/Y
     */
    public float[] screenToProj(float screenX, float screenY, double LonShift) {
        float projX = (float) (screenX / _scaleX + _drawExtent.minX + LonShift);
        float projY = (float) (_drawExtent.maxY - screenY / _scaleY);

        return new float[]{projX, projY};
    }

    private double[] getProjXYShift(Point point1, Point point2) {
        double[] pXY1 = screenToProj((double) point1.x, (double) point1.y);
        double[] pXY2 = screenToProj((double) point2.x, (double) point2.y);
        double xShift = pXY2[0] - pXY1[0];
        double yShift = pXY2[1] - pXY1[1];

        return new double[]{xShift, yShift};
    }

    private void moveShapeOnScreen(Shape aShape, Point point1, Point point2) {
        double[] sXY = getProjXYShift(point1, point2);
        moveShape(aShape, sXY[0], sXY[1]);
    }

    private void moveShape(Shape aShape, double xShift, double yShift) {
        List<PointD> points = (List<PointD>) aShape.getPoints();
        for (int i = 0; i < points.size(); i++) {
            PointD aPoint = points.get(i);
            aPoint.X += xShift;
            aPoint.Y += yShift;
        }

        aShape.setPoints(points);
    }

    private void resizeShapeOnScreen(Shape aShape, ColorBreak legend, Rectangle newRect) {
        double[] min = screenToProj((double) newRect.x, (double) newRect.y + newRect.height);
        double[] max = screenToProj((double) newRect.x + newRect.width, (double) newRect.y);
        Extent newExtent = new Extent(min[0], max[0], min[1], max[1]);
        List<PointD> points = (List<PointD>) aShape.getPoints();
        Extent aExtent = aShape.getExtent();

        switch (aShape.getShapeType()) {
            case Point:
            case PointM:
                if (legend.getBreakType() == BreakTypes.PointBreak) {
                    PointBreak aPB = (PointBreak) legend;
                    aPB.setSize(newRect.width);
                }
                break;
            case Polyline:
            case CurveLine:
            case Polygon:
            case PolygonM:
            case Circle:
            case CurvePolygon:
                moveShape(aShape, newExtent.minX - aExtent.minX, newExtent.minY - aExtent.minY);

                double deltaX = newExtent.getWidth() - aExtent.getWidth();
                double deltaY = newExtent.getHeight() - aExtent.getHeight();
                for (int i = 0; i < points.size(); i++) {
                    PointD aP = points.get(i);
                    aP.X = aP.X + deltaX * (aP.X - aExtent.minX) / aExtent.getWidth();
                    aP.Y = aP.Y + deltaY * (aP.Y - aExtent.minY) / aExtent.getHeight();
                    points.set(i, aP);
                }
                aShape.setPoints(points);
                break;
            case Rectangle:
            case Ellipse:
                points = new ArrayList<PointD>();
                points.add(new PointD(newExtent.minX, newExtent.minY));
                points.add(new PointD(newExtent.minX, newExtent.maxY));
                points.add(new PointD(newExtent.maxX, newExtent.maxY));
                points.add(new PointD(newExtent.maxX, newExtent.minY));
                if (aShape.getShapeType() == ShapeTypes.Rectangle) {
                    points.add((PointD) points.get(0).clone());
                }
                aShape.setPoints(points);
                break;
        }
    }

    /**
     * Get longitude shift
     *
     * @param aExtent Extent
     * @return Longitude shift
     */
    public double getLonShift(Extent aExtent) {
        double LonShift = 0;
        if (_drawExtent.maxX < aExtent.minX) {
            LonShift = -360;
        }
        if (_drawExtent.minX > aExtent.maxX) {
            LonShift = 360;
        }

        return LonShift;
    }

    /**
     * Get longitude shift
     *
     * @param lon Longitude
     * @return Longitude shift
     */
    public double getLonShift(double lon) {
        double LonShift = 0;
        if (_drawExtent.maxX < lon) {
            LonShift = -360;
        }
        if (_drawExtent.minX > lon) {
            LonShift = 360;
        }

        return LonShift;
    }
    // </editor-fold>

    // <editor-fold desc="Zoom Methods">
    /**
     * Zoom to extent
     *
     * @param aExtent The extent
     */
    public void zoomToExtent(Extent aExtent) {
        _viewExtent = aExtent;
        refreshXYScale();

        paintLayers();

        this.fireViewExtentChangedEvent();
    }

    /**
     * Zoom to extent
     *
     * @param minX Minimum x
     * @param maxX Maximum x
     * @param minY Minimum y
     * @param maxY Maximum y
     */
    public void zoomToExtent(double minX, double maxX, double minY, double maxY) {
        Extent aExtent = new Extent();
        aExtent.minX = minX;
        aExtent.maxX = maxX;
        aExtent.minY = minY;
        aExtent.maxY = maxY;

        zoomToExtent(aExtent);
    }

    /**
     * Zoom to extent by screen coordinate
     *
     * @param minX Minimum x
     * @param maxX Maximum x
     * @param minY Minimum y
     * @param maxY Maximum y
     * @param zoom Zoom
     */
    public void zoomToExtentScreen(double minX, double maxX, double minY, double maxY, double zoom) {
        double[] pMin = screenToProj(minX, maxY, zoom);
        double[] pMax = screenToProj(maxX, minY, zoom);
        zoomToExtent(pMin[0], pMax[0], pMin[1], pMax[1]);
    }

    /**
     * Zoom to exactly lon/lat extent
     *
     * @param aExtent The extent
     */
    public void zoomToExtentLonLatEx(Extent aExtent) {
        if (!_projection.isLonLatMap()) {
            aExtent = _projection.getProjectedExtentFromLonLat(aExtent);
        }

        _viewExtent = aExtent;

        if (_isGeoMap) {
            setCoordinateGeoMapEx(aExtent);
        } else {
            setCoordinateMap(aExtent);
        }

        _drawExtent = aExtent;

        paintLayers();
        this.fireViewExtentChangedEvent();
    }

    private void setCoordinateGeoMap(Extent aExtent) {
        setCoordinateGeoMap(aExtent, this.getWidth(), this.getHeight());
    }

    private void setCoordinateGeoMap(Extent aExtent, int width, int height) {
        double scaleFactor, lonRan, latRan, temp;

        _scaleX = width / (aExtent.maxX - aExtent.minX);
        _scaleY = height / (aExtent.maxY - aExtent.minY);
        if (_projection.isLonLatMap()) {
            scaleFactor = _XYScaleFactor;
        } else {
            scaleFactor = 1;
        }

        if (_scaleX > _scaleY) {
            _scaleX = _scaleY / scaleFactor;
            temp = aExtent.minX;
            aExtent.minX = aExtent.maxX - width / _scaleX;
            lonRan = (aExtent.minX - temp) / 2;
            aExtent.minX = aExtent.minX - lonRan;
            aExtent.maxX = aExtent.maxX - lonRan;
        } else {
            _scaleY = _scaleX * scaleFactor;
            temp = aExtent.minY;
            aExtent.minY = aExtent.maxY - height / _scaleY;
            latRan = (aExtent.minY - temp) / 2;
            aExtent.minY = aExtent.minY - latRan;
            aExtent.maxY = aExtent.maxY - latRan;
        }
    }

    private void setCoordinateGeoMapEx(Extent aExtent) {
        setCoordinateGeoMapEx(aExtent, this.getWidth(), this.getHeight());
    }

    private void setCoordinateGeoMapEx(Extent aExtent, int width, int height) {
        double scaleFactor;

        _scaleX = width / (aExtent.maxX - aExtent.minX);
        _scaleY = height / (aExtent.maxY - aExtent.minY);
        if (_projection.isLonLatMap()) {
            scaleFactor = _XYScaleFactor;
        } else {
            scaleFactor = 1;
        }

        if (_scaleX < _scaleY) {
            _scaleX = _scaleY / scaleFactor;
            //width = (int)((aExtent.maxX - aExtent.minX) * _scaleX);
        } else {
            _scaleY = _scaleX * scaleFactor;
            //height = (int)((aExtent.maxY - aExtent.minY) * _scaleY);
        }
    }

    private void setCoordinateMap(Extent aExtent) {
        setCoordinateMap(aExtent, this.getWidth(), this.getHeight());
    }

    private void setCoordinateMap(Extent aExtent, int width, int height) {
        _scaleX = width / (aExtent.maxX - aExtent.minX);
        _scaleY = height / (aExtent.maxY - aExtent.minY);
    }

    /**
     * Refresh X/Y scale
     */
    public void refreshXYScale() {
        refreshXYScale(this.getWidth(), this.getHeight());
    }

    /**
     * Refresh X/Y scale
     *
     * @param width The width
     * @param height The height
     */
    public void refreshXYScale(int width, int height) {
        Extent aExtent = (Extent) _viewExtent.clone();

        if (_isGeoMap) {
            setCoordinateGeoMap(aExtent, width, height);
        } else {
            setCoordinateMap(aExtent, width, height);
        }

        _drawExtent = aExtent;
    }

    private double getGeoWidth(double width) {
        double geoWidth = width / _scaleX;
        if (_projection.isLonLatMap()) {
            geoWidth = geoWidth * getLonDistScale();
        }

        return geoWidth;
    }

    private double getLonDistScale() {
        //Get meters of one longitude degree
        double pY = (_viewExtent.maxY + _viewExtent.minY) / 2;
        double ProjX = 0, ProjY = pY, pProjX = 1, pProjY = pY;
        double dx = Math.abs(ProjX - pProjX);
        double dy = Math.abs(ProjY - pProjY);
        double dist;
        double y = (ProjY + pProjY) / 2;
        double factor = Math.cos(y * Math.PI / 180);
        dx *= factor;
        dist = Math.sqrt(dx * dx + dy * dy);
        dist = dist * 111319.5;

        return dist;
    }

    /**
     * Get geographic scale
     *
     * @return Geographic scale
     */
    public double getGeoScale() {
        double breakWidth = 1;
        double geoBreakWidth = getGeoWidth(breakWidth);
        double scale = geoBreakWidth * 100 / (breakWidth / 96 * 2.539999918);

        return scale;
    }

    // </editor-fold>
    // <editor-fold desc="Select">
    /**
     * Select graphics by point
     *
     * @param aPoint The point
     * @param selectedGraphics Selected graphics
     * @param lonShift Longitue shift
     * @return Boolean
     */
    public boolean selectGraphics(PointF aPoint, GraphicCollection selectedGraphics, double lonShift) {
        _visibleGraphics = getVisibleGraphics();
        return selectGraphics(aPoint, _visibleGraphics, selectedGraphics, lonShift, 0);
    }

    private GraphicCollection getVisibleGraphics() {
        GraphicCollection graphicCollection = new GraphicCollection();
        for (Graphic aGraphic : _graphicCollection) {
            graphicCollection.add(aGraphic);
        }

        for (MapLayer aLayer : _layers) {
            if (aLayer.getLayerType() == LayerTypes.VectorLayer && aLayer.isVisible()) {
                VectorLayer vLayer = (VectorLayer) aLayer;
                for (Graphic aGraphic : vLayer.getLabelPoints()) {
                    if (aGraphic.getShape().isVisible()) {
                        graphicCollection.add(aGraphic);
                    }
                }
                for (Graphic aGraphic : vLayer.getChartPoints()) {
                    if (aGraphic.getShape().isVisible()) {
                        graphicCollection.add(aGraphic);
                    }
                }
            }
        }

        return graphicCollection;
    }

    /**
     * Select graphics by point
     *
     * @param aPoint The point
     * @param baseGraphics Base graphics
     * @param selectedGraphics Selected graphics
     * @param lonShift Longitude shift
     * @param limit Tolerance limit
     * @return Boolean
     */
    public boolean selectGraphics_back(PointF aPoint, GraphicCollection baseGraphics, GraphicCollection selectedGraphics,
            double lonShift, int limit) {
        if (baseGraphics.size() == 0) {
            return false;
        }

        selectedGraphics.clear();
        int i;
        Graphics g = this.getGraphics();
        boolean ifSel = true;

        if (_projection.isLonLatMap()) {
            boolean ifCheckLonShift = true;
            if (baseGraphics.get(0).getShape().getShapeType() == ShapeTypes.Point) {
                for (i = 0; i < baseGraphics.size(); i++) {
                    Graphic aGraphic = baseGraphics.get(i);
                    Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                    rect.width += limit;
                    rect.height += limit;
                    if (MIMath.pointInRectangle(aPoint, rect)) {
                        selectedGraphics.add(aGraphic);
                        break;
                    }
                }

                if (selectedGraphics.size() > 0) {
                    ifCheckLonShift = false;
                    ifSel = false;
                }
            }

            if (ifCheckLonShift) {
                float[] pXY = screenToProj(aPoint.X, aPoint.Y);
                if (pXY[0] < baseGraphics.getExtent().minX) {
                    if (baseGraphics.getExtent().minX > -360 && baseGraphics.getExtent().maxX > 0) {
                        lonShift = -360;
                    }
                }
                if (pXY[0] > baseGraphics.getExtent().maxX) {
                    if (baseGraphics.getExtent().maxX < 360 && baseGraphics.getExtent().minX < 0) {
                        lonShift = 360;
                    }
                }
            }
        }

        if (ifSel) {
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                rect.width += limit;
                rect.height += limit;
                if (MIMath.pointInRectangle(aPoint, rect)) {
                    selectedGraphics.add(aGraphic);
                    //break;
                }
            }
        }

        if (selectedGraphics.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Select graphics by point
     *
     * @param aPoint The point
     * @param baseGraphics Base graphics
     * @param selectedGraphics Selected graphics
     * @param lonShift Longitude shift
     * @param limit Tolerance limit
     * @return Boolean
     */
    public boolean selectGraphics(PointF aPoint, GraphicCollection baseGraphics, GraphicCollection selectedGraphics,
            double lonShift, int limit) {
        if (baseGraphics.size() == 0) {
            return false;
        }

        selectedGraphics.clear();
        int i;
        Graphics g = this.getGraphics();
        boolean ifSel = true;
        double[] projXY = screenToProj((double) aPoint.X, (double) aPoint.Y);
        double projX = projXY[0] + lonShift;
        double projY = projXY[1];
        PointD pp = new PointD(projX, projY);
        double buffer = 5 / this._scaleX;

        if (_projection.isLonLatMap()) {
            boolean ifCheckLonShift = true;
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                switch (aGraphic.getShape().getShapeType()) {
                    case Polyline:
                    case CurveLine:
                        PolylineShape aPLS = (PolylineShape) aGraphic.getShape();
                        if (GeoComputation.selectPolylineShape(pp, aPLS, buffer)) {
                            selectedGraphics.add(aGraphic);
                        }
                        break;
                    default:
                        Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                        rect.width += limit;
                        rect.height += limit;
                        if (MIMath.pointInRectangle(aPoint, rect)) {
                            selectedGraphics.add(aGraphic);
                        }
                        break;
                }
            }

            if (selectedGraphics.size() > 0) {
                ifCheckLonShift = false;
                ifSel = false;
            }

            if (ifCheckLonShift) {
                float[] pXY = screenToProj(aPoint.X, aPoint.Y);
                if (pXY[0] < baseGraphics.getExtent().minX) {
                    if (baseGraphics.getExtent().minX > -360 && baseGraphics.getExtent().maxX > 0) {
                        lonShift = -360;
                    }
                }
                if (pXY[0] > baseGraphics.getExtent().maxX) {
                    if (baseGraphics.getExtent().maxX < 360 && baseGraphics.getExtent().minX < 0) {
                        lonShift = 360;
                    }
                }
            }
        }

        if (ifSel) {
            projX = projXY[0] + lonShift;
            pp = new PointD(projX, projY);
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                switch (aGraphic.getShape().getShapeType()) {
                    case Polyline:
                    case CurveLine:
                        PolylineShape aPLS = (PolylineShape) aGraphic.getShape();
                        if (GeoComputation.selectPolylineShape(pp, aPLS, buffer)) {
                            selectedGraphics.add(aGraphic);
                        }
                        break;
                    default:
                        Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                        rect.width += limit;
                        rect.height += limit;
                        if (MIMath.pointInRectangle(aPoint, rect)) {
                            selectedGraphics.add(aGraphic);
                        }
                        break;
                }
            }
        }

        if (selectedGraphics.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Select graphics by rectangle
     *
     * @param aRect The rectangle
     * @param selectedGraphics Selected graphics
     * @param lonShift Longitude shift
     * @return Boolean
     */
    public boolean selectGraphics(Rectangle aRect, GraphicCollection selectedGraphics, double lonShift) {
        _visibleGraphics = getVisibleGraphics();
        return selectGraphics(aRect, _visibleGraphics, selectedGraphics, lonShift);
    }

    /**
     * Select graphics by rectangle
     *
     * @param aRect Select rectangle
     * @param baseGraphics Base graphics
     * @param selectedGraphics Selected graphics
     * @param lonShift Logitude shift
     * @return
     */
    public boolean selectGraphics_back(Rectangle aRect, GraphicCollection baseGraphics, GraphicCollection selectedGraphics,
            double lonShift) {
        if (baseGraphics.size() == 0) {
            return false;
        }

        selectedGraphics.clear();
        int i;
        Graphics g = this.getGraphics();
        boolean ifSel = true;

        if (_projection.isLonLatMap()) {
            boolean ifCheckLonShift = true;
            if (baseGraphics.get(0).getShape().getShapeType() == ShapeTypes.Point) {
                for (i = 0; i < baseGraphics.size(); i++) {
                    Graphic aGraphic = baseGraphics.get(i);
                    Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                    if (MIMath.isInclude(aRect, rect)) {
                        selectedGraphics.add(aGraphic);
                        break;
                    }
                }

                if (selectedGraphics.size() > 0) {
                    ifCheckLonShift = false;
                    ifSel = false;
                }
            }

            if (ifCheckLonShift) {
                Point aPoint = new Point(aRect.x + aRect.width / 2, aRect.y + aRect.height / 2);
                float[] pXY = screenToProj(aPoint.x, aPoint.y);
                if (pXY[0] < baseGraphics.getExtent().minX) {
                    if (baseGraphics.getExtent().minX > -360 && baseGraphics.getExtent().maxX > 0) {
                        lonShift = -360;
                    }
                }
                if (pXY[0] > baseGraphics.getExtent().maxX) {
                    if (baseGraphics.getExtent().maxX < 360 && baseGraphics.getExtent().minX < 0) {
                        lonShift = 360;
                    }
                }
            }
        }

        if (ifSel) {
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                if (MIMath.isInclude(aRect, rect)) {
                    selectedGraphics.add(aGraphic);
                    //break;
                }
            }
        }

        if (selectedGraphics.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Select graphics by rectangle
     *
     * @param aRect Select rectangle
     * @param baseGraphics Base graphics
     * @param selectedGraphics Selected graphics
     * @param lonShift Logitude shift
     * @return
     */
    public boolean selectGraphics(Rectangle aRect, GraphicCollection baseGraphics, GraphicCollection selectedGraphics,
            double lonShift) {
        if (baseGraphics.size() == 0) {
            return false;
        }

        selectedGraphics.clear();
        int i;
        Graphics g = this.getGraphics();
        boolean ifSel = true;

        if (_projection.isLonLatMap()) {
            boolean ifCheckLonShift = true;
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                if (MIMath.isInclude(aRect, rect)) {
                    selectedGraphics.add(aGraphic);
                    break;
                }
            }

            if (selectedGraphics.size() > 0) {
                ifCheckLonShift = false;
                ifSel = false;
            }

            if (ifCheckLonShift) {
                Point aPoint = new Point(aRect.x + aRect.width / 2, aRect.y + aRect.height / 2);
                float[] pXY = screenToProj(aPoint.x, aPoint.y);
                if (pXY[0] < baseGraphics.getExtent().minX) {
                    if (baseGraphics.getExtent().minX > -360 && baseGraphics.getExtent().maxX > 0) {
                        lonShift = -360;
                    }
                }
                if (pXY[0] > baseGraphics.getExtent().maxX) {
                    if (baseGraphics.getExtent().maxX < 360 && baseGraphics.getExtent().minX < 0) {
                        lonShift = 360;
                    }
                }
            }
        }

        if (ifSel) {
            for (i = 0; i < baseGraphics.size(); i++) {
                Graphic aGraphic = baseGraphics.get(i);
                Rectangle rect = getGraphicRectangle(g, aGraphic, lonShift);
                if (MIMath.isInclude(aRect, rect)) {
                    selectedGraphics.add(aGraphic);
                    //break;
                }
            }
        }

        if (selectedGraphics.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates which edge of a rectangle the point intersects with, within a
     * certain limit
     *
     * @param screen
     * @param pt
     * @param limit
     * @return
     */
    private static Edge intersectElementEdge(Rectangle screen, PointF pt, float limit) {
        Rectangle.Float ptRect = new Rectangle.Float(pt.X - limit, pt.Y - limit, 2F * limit, 2F * limit);
        if ((pt.X >= screen.x - limit && pt.X <= screen.x + limit) && (pt.Y >= screen.y - limit
                && pt.Y <= screen.y + limit)) {
            return Edge.TopLeft;
        }
        if ((pt.X >= screen.x + screen.width - limit && pt.X <= screen.x + screen.width + limit)
                && (pt.Y >= screen.y - limit && pt.Y <= screen.y + limit)) {
            return Edge.TopRight;
        }
        if ((pt.X >= screen.x + screen.width - limit && pt.X <= screen.x + screen.width + limit)
                && (pt.Y >= screen.y + screen.height - limit && pt.Y <= screen.y + screen.height + limit)) {
            return Edge.BottomRight;
        }
        if ((pt.X >= screen.x - limit && pt.X <= screen.x + limit) && (pt.Y >= screen.y + screen.height - limit
                && pt.Y <= screen.y + screen.height + limit)) {
            return Edge.BottomLeft;
        }
        if (ptRect.intersects(new Rectangle.Float(screen.x, screen.y, screen.width, 1F))) {
            return Edge.Top;
        }
        if (ptRect.intersects(new Rectangle.Float(screen.x, screen.y, 1F, screen.height))) {
            return Edge.Left;
        }
        if (ptRect.intersects(new Rectangle.Float(screen.x, screen.y + screen.height, screen.width, 1F))) {
            return Edge.Bottom;
        }
        if (ptRect.intersects(new Rectangle.Float(screen.x + screen.width, screen.y, 1F, screen.height))) {
            return Edge.Right;
        }
        return Edge.None;
    }

    private int selectEditVertices(Point aPoint, Shape aShape, List<PointD> vertices) {
        int vIdx = -1;
        List<PointD> points = (List<PointD>) aShape.getPoints();
        int buffer = 4;
        Extent aExtent = new Extent();
        float[] pXY;
        pXY = screenToProj(aPoint.x - buffer, aPoint.y + buffer);
        aExtent.minX = pXY[0];
        aExtent.minY = pXY[1];
        pXY = screenToProj(aPoint.x + buffer, aPoint.y - buffer);
        aExtent.maxX = pXY[0];
        aExtent.maxY = pXY[1];

        vertices.clear();
        PointD aPD;
        for (int i = 0; i < points.size(); i++) {
            if (MIMath.pointInExtent(points.get(i), aExtent)) {
                vIdx = i;
                vertices.add(points.get(i));
                switch (aShape.getShapeType()) {
                    case Polyline:
                    case CurveLine:
                        if (i == 0) {
                            vertices.add(points.get(i + 1));
                        } else if (i == points.size() - 1) {
                            vertices.add(points.get(i - 1));
                        } else {
                            vertices.add(points.get(i - 1));
                            vertices.add(points.get(i + 1));
                        }
                        break;
                    default:
                        if (i == 0) {
                            vertices.add(points.get(i + 1));
                            aPD = points.get(points.size() - 1);
                            if (aPD.X == points.get(i).X && aPD.Y == points.get(i).Y) {
                                vertices.add(points.get(points.size() - 2));
                            } else {
                                vertices.add(aPD);
                            }
                        } else if (i == points.size() - 1) {
                            vertices.add(points.get(i - 1));
                            aPD = points.get(0);
                            if (aPD.X == points.get(i).X && aPD.Y == points.get(i).Y) {
                                vertices.add(points.get(1));
                            } else {
                                vertices.add(points.get(0));
                            }
                        } else {
                            vertices.add(points.get(i - 1));
                            vertices.add(points.get(i + 1));
                        }
                        break;
                }
                break;
            }
        }

        return vIdx;
    }

    /**
     * Select shaped
     *
     * @param aLayer Vector layer
     * @param aPoint The point
     * @param isSel If the selected shapes will be set as selected
     * @return Selected shapes
     */
    public List<Integer> selectShapes(VectorLayer aLayer, PointF aPoint, boolean isSel) {
        float sX = aPoint.X;
        float sY = aPoint.Y;
        double[] projXY = screenToProj((double) aPoint.X, aPoint.Y);
        double ProjX = projXY[0];
        double ProjY = projXY[1];
        double[] sXY;
        if (_projection.isLonLatMap()) {
            if (ProjX < aLayer.getExtent().minX) {
                if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                    sXY = projToScreen(ProjX, ProjY, 360);
                    sX = (float) sXY[0];
                    sY = (float) sXY[1];
                }
            }
            if (ProjX > aLayer.getExtent().maxX) {
                if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                    sXY = projToScreen(ProjX, ProjY, -360);
                    sX = (float) sXY[0];
                    sY = (float) sXY[1];
                }
            }
        }

        int Buffer = 5;
        Extent aExtent = new Extent();
        projXY = screenToProj((double) sX - Buffer, sY + Buffer);
        ProjX = projXY[0];
        ProjY = projXY[1];
        aExtent.minX = ProjX;
        aExtent.minY = ProjY;
        projXY = screenToProj((double) sX + Buffer, sY - Buffer);
        ProjX = projXY[0];
        ProjY = projXY[1];
        aExtent.maxX = ProjX;
        aExtent.maxY = ProjY;

        List<Integer> selectedShapes = aLayer.selectShapes(aExtent, true);
        if (isSel) {
            for (int i : selectedShapes) {
                aLayer.getShapes().get(i).setSelected(true);
            }
        }

        return selectedShapes;
    }

    /**
     * Select shapes
     *
     * @param aLayer Vector layer
     * @param aPoint The point
     * @return Selected shapes
     */
    public List<Integer> selectShapes(VectorLayer aLayer, PointF aPoint) {
        return selectShapes(aLayer, aPoint, false);
    }

    /**
     * Select grid cell
     *
     * @param aLayer Raster layer
     * @param aPoint Point
     * @return Selected i/j index
     */
    public int[] selectGridCell(RasterLayer aLayer, PointF aPoint) {
        double LonShift = 0;
        double[] projXY = screenToProj((double) aPoint.X, aPoint.Y);
        double aX = projXY[0];
        double aY = projXY[1];
        if (_projection.isLonLatMap()) {
            if (aX < aLayer.getExtent().minX) {
                if (aLayer.getExtent().minX > -360 && aLayer.getExtent().maxX > 0) {
                    LonShift = 360;
                }
            }
            if (aX > aLayer.getExtent().maxX) {
                if (aLayer.getExtent().maxX < 360 && aLayer.getExtent().minX < 0) {
                    LonShift = -360;
                }
            }
        }
        aX = aX + (float) LonShift;

        Extent aExtent = new Extent();
        double XDelt = aLayer.getGridData().xArray[1] - aLayer.getGridData().xArray[0];
        double YDelt = aLayer.getGridData().yArray[1] - aLayer.getGridData().yArray[0];
        aExtent.minX = aX - XDelt / 2;
        aExtent.maxX = aX + XDelt / 2;
        aExtent.minY = aY - YDelt / 2;
        aExtent.maxY = aY + YDelt / 2;

        int iIdx = -1;
        int jIdx = -1;
        for (int i = 0; i < aLayer.getGridData().getYNum(); i++) {
            if (aLayer.getGridData().yArray[i] >= aExtent.minY && aLayer.getGridData().yArray[i] <= aExtent.maxY) {
                iIdx = i;
                break;
            }
        }
        for (int j = 0; j < aLayer.getGridData().getXNum(); j++) {
            if (aLayer.getGridData().xArray[j] >= aExtent.minX && aLayer.getGridData().xArray[j] <= aExtent.maxX) {
                jIdx = j;
                break;
            }
        }

        if (iIdx == -1 || jIdx == -1) {
            return null;
        } else {
            return new int[]{iIdx, jIdx};
        }
    }

    // </editor-fold>
    // <editor-fold desc="Graphic">
    private Rectangle getGraphicRectangle(Graphics g, Graphic aGraphic, double lonShift) {
        Rectangle rect = new Rectangle();
        double[] sXY;
        float aX, aY;
        switch (aGraphic.getShape().getShapeType()) {
            case Point:
            case PointM:
                PointShape aPS = (PointShape) aGraphic.getShape();
                sXY = projToScreen(aPS.getPoint().X, aPS.getPoint().Y, lonShift);
                aX = (float) sXY[0];
                aY = (float) sXY[1];
                switch (aGraphic.getLegend().getBreakType()) {
                    case PointBreak:
                        PointBreak aPB = (PointBreak) aGraphic.getLegend();
                        int buffer = (int) aPB.getSize() + 2;
                        rect.x = (int) aX - buffer / 2;
                        rect.y = (int) aY - buffer / 2;
                        rect.width = buffer;
                        rect.height = buffer;
                        break;
                    case LabelBreak:
                        LabelBreak aLB = (LabelBreak) aGraphic.getLegend();
                        FontMetrics metrics = this.getGraphics().getFontMetrics(aLB.getFont());
                        Dimension labSize = new Dimension(metrics.stringWidth(aLB.getText()), metrics.getHeight());
                        switch (aLB.getAlignType()) {
                            case Center:
                                aX = aX - labSize.width / 2;
                                break;
                            case Left:
                                aX = aX - labSize.width;
                                break;
                        }
                        aY -= aLB.getYShift();
                        aY -= labSize.height / 2;
                        rect.x = (int) aX;
                        rect.y = (int) aY;
                        rect.width = (int) labSize.width;
                        rect.height = (int) labSize.height;
                        break;
                    case ChartBreak:
                        ChartBreak aCB = (ChartBreak) aGraphic.getLegend();
                        rect = aCB.getDrawExtent(new PointF(aX, aY)).convertToRectangle();
                        break;
                }
                break;
            case Polyline:
            case Polygon:
            case Rectangle:
            case CurveLine:
            case Ellipse:
            case Circle:
            case CurvePolygon:
                List<PointD> newPList = (List<PointD>) aGraphic.getShape().getPoints();
                List<PointD> points = new ArrayList<PointD>();
                for (int i = 0; i < newPList.size(); i++) {
                    PointD wPoint = newPList.get(i);
                    sXY = projToScreen(wPoint.X, wPoint.Y, lonShift);
                    aX = (float) sXY[0];
                    aY = (float) sXY[1];
                    points.add(new PointD(aX, aY));
                }
                Extent aExtent = MIMath.getPointsExtent(points);
                rect.x = (int) aExtent.minX;
                rect.y = (int) aExtent.minY;
                rect.width = (int) (aExtent.maxX - aExtent.minX);
                rect.height = (int) (aExtent.maxY - aExtent.minY);
                break;
        }

        return rect;
    }

    /**
     * Remove a graphic
     *
     * @param aGraphic The graphic
     */
    public void removeGraphic(Graphic aGraphic) {
        if (_graphicCollection.contains(aGraphic)) {
            _graphicCollection.remove(aGraphic);
        } else {
            for (MapLayer aLayer : _layers) {
                if (aLayer.getLayerType() == LayerTypes.VectorLayer) {
                    VectorLayer aVLayer = (VectorLayer) aLayer;
                    if (aVLayer.getLabelPoints().contains(aGraphic)) {
                        aVLayer.getLabelPoints().remove(aGraphic);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Remove selected graphics
     */
    public void removeSelectedGraphics() {
        for (Graphic aGraphic : _selectedGraphics) {
            removeGraphic(aGraphic);
        }

        _selectedGraphics.clear();
    }

    // </editor-fold>
    // <editor-fold desc="XML import and export">
    /**
     * Add extent element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     */
    public void exportExtentsElement(Document m_Doc, Element parent) {
        Element Extents = m_Doc.createElement("Extents");
        Attr xMin = m_Doc.createAttribute("xMin");
        Attr xMax = m_Doc.createAttribute("xMax");
        Attr yMin = m_Doc.createAttribute("yMin");
        Attr yMax = m_Doc.createAttribute("yMax");

        xMin.setValue(String.valueOf(_viewExtent.minX));
        xMax.setValue(String.valueOf(_viewExtent.maxX));
        yMin.setValue(String.valueOf(_viewExtent.minY));
        yMax.setValue(String.valueOf(_viewExtent.maxY));

        Extents.setAttributeNode(xMin);
        Extents.setAttributeNode(xMax);
        Extents.setAttributeNode(yMin);
        Extents.setAttributeNode(yMax);

        parent.appendChild(Extents);
    }

    /**
     * Export map property element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     */
    public void exportMapPropElement(Document m_Doc, Element parent) {
        Element MapProperty = m_Doc.createElement("MapProperty");
        Attr BackColor = m_Doc.createAttribute("BackColor");
        Attr ForeColor = m_Doc.createAttribute("ForeColor");
        Attr SmoothingMode = m_Doc.createAttribute("SmoothingMode");

        BackColor.setValue(ColorUtil.toHexEncoding(this.getBackground()));
        ForeColor.setValue(ColorUtil.toHexEncoding(this.getForeground()));
        SmoothingMode.setValue(String.valueOf(_antiAlias));

        MapProperty.setAttributeNode(BackColor);
        MapProperty.setAttributeNode(ForeColor);
        MapProperty.setAttributeNode(SmoothingMode);

        parent.appendChild(MapProperty);
    }

    /**
     * Export grid line element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     */
    public void exportGridLineElement(Document m_Doc, Element parent) {
        Element GridLine = m_Doc.createElement("GridLine");
        Attr GridLineColor = m_Doc.createAttribute("GridLineColor");
        Attr GridLineSize = m_Doc.createAttribute("GridLineSize");
        Attr GridLineStyle = m_Doc.createAttribute("GridLineStyle");
        Attr DrawGridLine = m_Doc.createAttribute("DrawGridLine");
        Attr DrawGridTickLine = m_Doc.createAttribute("DrawGridTickLine");

        GridLineColor.setValue(ColorUtil.toHexEncoding(_gridLineColor));
        GridLineSize.setValue(String.valueOf(_gridLineSize));
        GridLineStyle.setValue(_gridLineStyle.toString());
        DrawGridLine.setValue(String.valueOf(_drawGridLine));
        DrawGridTickLine.setValue(String.valueOf(_drawGridTickLine));

        GridLine.setAttributeNode(GridLineColor);
        GridLine.setAttributeNode(GridLineSize);
        GridLine.setAttributeNode(GridLineStyle);
        GridLine.setAttributeNode(DrawGridLine);
        GridLine.setAttributeNode(DrawGridTickLine);

        parent.appendChild(GridLine);
    }

    /**
     * Export maskout element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     */
    public void exportMaskOutElement(Document m_Doc, Element parent) {
        Element MaskOut = m_Doc.createElement("MaskOut");
        Attr SetMaskLayer = m_Doc.createAttribute("SetMaskLayer");
        Attr MaskLayer = m_Doc.createAttribute("MaskLayer");

        SetMaskLayer.setValue(String.valueOf(_maskOut.isMask()));
        MaskLayer.setValue(_maskOut.getMaskLayer());

        MaskOut.setAttributeNode(SetMaskLayer);
        MaskOut.setAttributeNode(MaskLayer);

        parent.appendChild(MaskOut);
    }

    /**
     * Export projection element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     */
    public void exportProjectionElement(Document m_Doc, Element parent) {
        Element Projection = m_Doc.createElement("Projection");
        Attr IsLonLatMap = m_Doc.createAttribute("IsLonLatMap");
        Attr ProjStr = m_Doc.createAttribute("ProjStr");
        Attr RefLon = m_Doc.createAttribute("RefLon");
        Attr RefCutLon = m_Doc.createAttribute("RefCutLon");

        IsLonLatMap.setValue(String.valueOf(_projection.isLonLatMap()));
        ProjStr.setValue(_projection.getProjInfo().toProj4String());
        RefLon.setValue(String.valueOf(_projection.getRefLon()));
        RefCutLon.setValue(String.valueOf(_projection.getRefCutLon()));

        Projection.setAttributeNode(IsLonLatMap);
        Projection.setAttributeNode(ProjStr);
        Projection.setAttributeNode(RefLon);
        Projection.setAttributeNode(RefCutLon);

        parent.appendChild(Projection);
    }

    /**
     * Export vector layer element
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     * @param aVLayer The vector layer
     * @param projectFilePath Project file path
     */
    public void exportVectorLayerElement(Document m_Doc, Element parent, VectorLayer aVLayer,
            String projectFilePath) {
        Element Layer = m_Doc.createElement("Layer");
        Attr Handle = m_Doc.createAttribute("Handle");
        Attr LayerName = m_Doc.createAttribute("LayerName");
        Attr FileName = m_Doc.createAttribute("FileName");
        Attr Visible = m_Doc.createAttribute("Visible");
        Attr IsMaskout = m_Doc.createAttribute("IsMaskout");
        Attr LayerType = m_Doc.createAttribute("LayerType");
        Attr LayerDrawType = m_Doc.createAttribute("LayerDrawType");
        Attr ShapeType = m_Doc.createAttribute("ShapeType");
        Attr AvoidCollision = m_Doc.createAttribute("AvoidCollision");
        Attr TransparencyPerc = m_Doc.createAttribute("TransparencyPerc");
        Attr Expanded = m_Doc.createAttribute("Expanded");

        Handle.setValue(String.valueOf(aVLayer.getHandle()));
        LayerName.setValue(aVLayer.getLayerName());
        FileName.setValue(GlobalUtil.getRelativePath(aVLayer.getFileName(), projectFilePath));
        Visible.setValue(String.valueOf(aVLayer.isVisible()));
        IsMaskout.setValue(String.valueOf(aVLayer.isMaskout()));
        LayerType.setValue(aVLayer.getLayerType().toString());
        LayerDrawType.setValue(aVLayer.getLayerDrawType().toString());
        ShapeType.setValue(aVLayer.getShapeType().toString());
        AvoidCollision.setValue(String.valueOf(aVLayer.getAvoidCollision()));
        TransparencyPerc.setValue(String.valueOf(aVLayer.getTransparency()));
        Expanded.setValue(String.valueOf(aVLayer.isExpanded()));

        Layer.setAttributeNode(Handle);
        Layer.setAttributeNode(LayerName);
        Layer.setAttributeNode(FileName);
        Layer.setAttributeNode(Visible);
        Layer.setAttributeNode(IsMaskout);
        Layer.setAttributeNode(LayerType);
        Layer.setAttributeNode(LayerDrawType);
        Layer.setAttributeNode(ShapeType);
        Layer.setAttributeNode(AvoidCollision);
        Layer.setAttributeNode(TransparencyPerc);
        Layer.setAttributeNode(Expanded);

        //Add legend scheme            
        aVLayer.getLegendScheme().exportToXML(m_Doc, Layer);

        //Add label set
        exportLabelSet(m_Doc, Layer, aVLayer.getLabelSet());

        //Add graphics
        exportGraphics(m_Doc, Layer, aVLayer.getLabelPoints());

        //Add chart set
        exportChartSet(m_Doc, Layer, aVLayer.getChartSet());

        //Add charts
        exportChartGraphics(m_Doc, Layer, aVLayer.getChartPoints());

        //Add visible scale
        exportVisibleScale(m_Doc, Layer, aVLayer.getVisibleScale());

        parent.appendChild(Layer);
    }

    private void exportLabelSet(Document m_Doc, Element parent, LabelSet aLabelSet) {
        Element LabelSet = m_Doc.createElement("LabelSet");
        Attr DrawLabels = m_Doc.createAttribute("DrawLabels");
        Attr FieldName = m_Doc.createAttribute("FieldName");
        Attr FontName = m_Doc.createAttribute("FontName");
        Attr FontSize = m_Doc.createAttribute("FontSize");
        Attr LabelColor = m_Doc.createAttribute("LabelColor");
        Attr DrawShadow = m_Doc.createAttribute("DrawShadow");
        Attr ShadowColor = m_Doc.createAttribute("ShadowColor");
        Attr AlignType = m_Doc.createAttribute("AlignType");
        Attr Offset = m_Doc.createAttribute("Offset");
        Attr AvoidCollision = m_Doc.createAttribute("AvoidCollision");
        Attr autoDecimal = m_Doc.createAttribute("AutoDecimal");
        Attr decimalDigits = m_Doc.createAttribute("DecimalDigits");

        DrawLabels.setValue(String.valueOf(aLabelSet.isDrawLabels()));
        FieldName.setValue(aLabelSet.getFieldName());
        FontName.setValue(aLabelSet.getLabelFont().getFontName());
        FontSize.setValue(String.valueOf(aLabelSet.getLabelFont().getSize()));
        LabelColor.setValue(ColorUtil.toHexEncoding(aLabelSet.getLabelColor()));
        DrawShadow.setValue(String.valueOf(aLabelSet.isDrawShadow()));
        ShadowColor.setValue(ColorUtil.toHexEncoding(aLabelSet.getShadowColor()));
        AlignType.setValue(aLabelSet.getLabelAlignType().toString());
        Offset.setValue(String.valueOf(aLabelSet.getYOffset()));
        AvoidCollision.setValue(String.valueOf(aLabelSet.isAvoidCollision()));
        autoDecimal.setValue(String.valueOf(aLabelSet.isAutoDecimal()));
        decimalDigits.setValue(String.valueOf(aLabelSet.getDecimalDigits()));

        LabelSet.setAttributeNode(DrawLabels);
        LabelSet.setAttributeNode(FieldName);
        LabelSet.setAttributeNode(FontName);
        LabelSet.setAttributeNode(FontSize);
        LabelSet.setAttributeNode(LabelColor);
        LabelSet.setAttributeNode(DrawShadow);
        LabelSet.setAttributeNode(ShadowColor);
        LabelSet.setAttributeNode(AlignType);
        LabelSet.setAttributeNode(Offset);
        LabelSet.setAttributeNode(AvoidCollision);
        LabelSet.setAttributeNode(autoDecimal);
        LabelSet.setAttributeNode(decimalDigits);

        parent.appendChild(LabelSet);
    }

    private void exportChartSet(Document m_Doc, Element parent, ChartSet aChartSet) {
        Element chartSet = m_Doc.createElement("ChartSet");
        Attr drawCharts = m_Doc.createAttribute("DrawCharts");
        Attr chartType = m_Doc.createAttribute("ChartType");
        Attr fieldNames = m_Doc.createAttribute("FieldNames");
        Attr xShift = m_Doc.createAttribute("XShift");
        Attr yShift = m_Doc.createAttribute("YShift");
        Attr maxSize = m_Doc.createAttribute("MaxSize");
        Attr minSize = m_Doc.createAttribute("MinSize");
        Attr maxValue = m_Doc.createAttribute("MaxValue");
        Attr minValue = m_Doc.createAttribute("MinValue");
        Attr barWidth = m_Doc.createAttribute("BarWidth");
        Attr avoidCollision = m_Doc.createAttribute("AvoidCollision");
        Attr alignType = m_Doc.createAttribute("AlignType");
        Attr view3D = m_Doc.createAttribute("View3D");
        Attr thickness = m_Doc.createAttribute("Thickness");

        drawCharts.setValue(String.valueOf(aChartSet.isDrawCharts()));
        chartType.setValue(String.valueOf(aChartSet.getChartType()));
        String fns = "";
        for (int i = 0; i < aChartSet.getFieldNames().size(); i++) {
            if (i == 0) {
                fns = aChartSet.getFieldNames().get(i);
            } else {
                fns = fns + "," + aChartSet.getFieldNames().get(i);
            }
        }
        fieldNames.setValue(fns);
        xShift.setValue(String.valueOf(aChartSet.getXShift()));
        yShift.setValue(String.valueOf(aChartSet.getYShift()));
        maxSize.setValue(String.valueOf(aChartSet.getMaxSize()));
        minSize.setValue(String.valueOf(aChartSet.getMinSize()));
        maxValue.setValue(String.valueOf(aChartSet.getMaxValue()));
        minValue.setValue(String.valueOf(aChartSet.getMinValue()));
        barWidth.setValue(String.valueOf(aChartSet.getBarWidth()));
        avoidCollision.setValue(String.valueOf(aChartSet.isAvoidCollision()));
        alignType.setValue(aChartSet.getAlignType().toString());
        view3D.setValue(String.valueOf(aChartSet.isView3D()));
        thickness.setValue(String.valueOf(aChartSet.getThickness()));

        chartSet.setAttributeNode(drawCharts);
        chartSet.setAttributeNode(chartType);
        chartSet.setAttributeNode(fieldNames);
        chartSet.setAttributeNode(xShift);
        chartSet.setAttributeNode(yShift);
        chartSet.setAttributeNode(maxSize);
        chartSet.setAttributeNode(minSize);
        chartSet.setAttributeNode(maxValue);
        chartSet.setAttributeNode(minValue);
        chartSet.setAttributeNode(barWidth);
        chartSet.setAttributeNode(avoidCollision);
        chartSet.setAttributeNode(alignType);
        chartSet.setAttributeNode(view3D);
        chartSet.setAttributeNode(thickness);

        //Export legend scheme
        aChartSet.getLegendScheme().exportToXML(m_Doc, chartSet);

        parent.appendChild(chartSet);
    }

    private void exportChartGraphics(Document m_Doc, Element parent, List<Graphic> graphicList) {
        Element graphics = m_Doc.createElement("ChartGraphics");

        //Add graphics
        for (Graphic aGraphic : graphicList) {
            //AddGraphic(ref m_Doc, graphics, aGraphic);
            aGraphic.exportToXML(m_Doc, graphics);
        }

        parent.appendChild(graphics);
    }

    /**
     * Export graphics
     *
     * @param m_Doc XML document
     * @param parent Parent XML element
     * @param graphicList Graphic list
     */
    public void exportGraphics(Document m_Doc, Element parent, List<Graphic> graphicList) {
        Element graphics = m_Doc.createElement("Graphics");

        //Add graphics
        for (Graphic aGraphic : graphicList) {
            aGraphic.exportToXML(m_Doc, graphics);
        }

        parent.appendChild(graphics);
    }

    private void exportVisibleScale(Document m_Doc, Element parent, VisibleScale visibleScale) {
        Element visibleScaleElem = m_Doc.createElement("VisibleScale");
        Attr enableMinVisScale = m_Doc.createAttribute("EnableMinVisScale");
        Attr enableMaxVisScale = m_Doc.createAttribute("EnableMaxVisScale");
        Attr minVisScale = m_Doc.createAttribute("MinVisScale");
        Attr maxVisScale = m_Doc.createAttribute("MaxVisScale");

        enableMinVisScale.setValue(String.valueOf(visibleScale.isEnableMinVisScale()));
        enableMaxVisScale.setValue(String.valueOf(visibleScale.isEnableMaxVisScale()));
        minVisScale.setValue(String.valueOf(visibleScale.getMinVisScale()));
        maxVisScale.setValue(String.valueOf(visibleScale.getMaxVisScale()));

        visibleScaleElem.setAttributeNode(enableMinVisScale);
        visibleScaleElem.setAttributeNode(enableMaxVisScale);
        visibleScaleElem.setAttributeNode(minVisScale);
        visibleScaleElem.setAttributeNode(maxVisScale);

        parent.appendChild(visibleScaleElem);
    }

    /**
     * Export image layer element
     *
     * @param m_Doc XML document
     * @param parent Parent element
     * @param aILayer The image layer
     * @param projectFilePath Project file path
     */
    public void exportImageLayer(Document m_Doc, Element parent, ImageLayer aILayer, String projectFilePath) {
        Element Layer = m_Doc.createElement("Layer");
        Attr Handle = m_Doc.createAttribute("Handle");
        Attr LayerName = m_Doc.createAttribute("LayerName");
        Attr FileName = m_Doc.createAttribute("FileName");
        Attr Visible = m_Doc.createAttribute("Visible");
        Attr IsMaskout = m_Doc.createAttribute("IsMaskout");
        Attr LayerType = m_Doc.createAttribute("LayerType");
        Attr LayerDrawType = m_Doc.createAttribute("LayerDrawType");
        Attr transparencyPerc = m_Doc.createAttribute("TransparencyPerc");
        Attr transparencyColor = m_Doc.createAttribute("TransparencyColor");
        Attr setTransColor = m_Doc.createAttribute("SetTransColor");

        Handle.setValue(String.valueOf(aILayer.getHandle()));
        LayerName.setValue(aILayer.getLayerName());
        FileName.setValue(GlobalUtil.getRelativePath(aILayer.getFileName(), projectFilePath));
        Visible.setValue(String.valueOf(aILayer.isVisible()));
        IsMaskout.setValue(String.valueOf(aILayer.isMaskout()));
        LayerType.setValue(aILayer.getLayerType().toString());
        LayerDrawType.setValue(aILayer.getLayerDrawType().toString());
        transparencyPerc.setValue(String.valueOf(aILayer.getTransparency()));
        transparencyColor.setValue(ColorUtil.toHexEncoding(aILayer.getTransparencyColor()));
        setTransColor.setValue(String.valueOf(aILayer.isUseTransColor()));

        Layer.setAttributeNode(Handle);
        Layer.setAttributeNode(LayerName);
        Layer.setAttributeNode(FileName);
        Layer.setAttributeNode(Visible);
        Layer.setAttributeNode(IsMaskout);
        Layer.setAttributeNode(LayerType);
        Layer.setAttributeNode(LayerDrawType);
        Layer.setAttributeNode(transparencyPerc);
        Layer.setAttributeNode(transparencyColor);
        Layer.setAttributeNode(setTransColor);

        //Add visible scale
        exportVisibleScale(m_Doc, Layer, aILayer.getVisibleScale());

        parent.appendChild(Layer);
    }

    /**
     * Load map property element
     *
     * @param parent Parent XML element
     */
    public void loadMapPropElement(Element parent) {
        Node MapProperty = parent.getElementsByTagName("MapProperty").item(0);
        try {
            this.setBackground(ColorUtil.parseToColor(MapProperty.getAttributes().getNamedItem("BackColor").getNodeValue()));
            this.setForeground(ColorUtil.parseToColor(MapProperty.getAttributes().getNamedItem("ForeColor").getNodeValue()));
            _antiAlias = Boolean.parseBoolean(MapProperty.getAttributes().getNamedItem("SmoothingMode").getNodeValue());
        } catch (Exception e) {
        }
    }

    /**
     * Load grid line element
     *
     * @param parent Parent XML element
     */
    public void loadGridLineElement(Element parent) {
        Node GridLine = parent.getElementsByTagName("GridLine").item(0);
        try {
            _gridLineColor = ColorUtil.parseToColor(GridLine.getAttributes().getNamedItem("GridLineColor").getNodeValue());
            _gridLineSize = Integer.parseInt(GridLine.getAttributes().getNamedItem("GridLineSize").getNodeValue());
            _gridLineStyle = LineStyles.valueOf(GridLine.getAttributes().getNamedItem("GridLineStyle").getNodeValue());
            _drawGridLine = Boolean.parseBoolean(GridLine.getAttributes().getNamedItem("DrawGridLine").getNodeValue());
            _drawGridTickLine = Boolean.parseBoolean(GridLine.getAttributes().getNamedItem("DrawGridTickLine").getNodeValue());
        } catch (Exception e) {
        }
    }

    /**
     * Load mask out element
     *
     * @param parent Parent XML element
     */
    public void loadMaskOutElement(Element parent) {
        Node MaskOut = parent.getElementsByTagName("MaskOut").item(0);
        try {
            _maskOut.setMask(Boolean.parseBoolean(MaskOut.getAttributes().getNamedItem("SetMaskLayer").getNodeValue()));
            _maskOut.setMaskLayer(MaskOut.getAttributes().getNamedItem("MaskLayer").getNodeValue());
        } catch (Exception e) {
        }
    }

    /**
     * Load projection element
     *
     * @param parent Parent XML element
     */
    public void loadProjectionElement(Element parent) {
        Node Projection = parent.getElementsByTagName("Projection").item(0);
        try {
            _projection.setProjStr(Projection.getAttributes().getNamedItem("ProjStr").getNodeValue());
            _projection.setRefLon(Double.parseDouble(Projection.getAttributes().getNamedItem("RefLon").getNodeValue()));
            _projection.setRefCutLon(Double.parseDouble(Projection.getAttributes().getNamedItem("RefCutLon").getNodeValue()));
            if (!(_projection.getProjInfo().getProjectionName() == ProjectionNames.LongLat)) {
                ProjectionInfo fromProj = KnownCoordinateSystems.geographic.world.WGS1984;
                ProjectionInfo toProj = new ProjectionInfo(_projection.getProjStr());
                projectLayers(toProj);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Load extent element
     *
     * @param parent Parent XML element
     */
    public void loadExtentsElement(Element parent) {
        Node Extents = parent.getElementsByTagName("Extents").item(0);
        Extent aExtent = new Extent();
        aExtent.minX = Double.parseDouble(Extents.getAttributes().getNamedItem("xMin").getNodeValue());
        aExtent.maxX = Double.parseDouble(Extents.getAttributes().getNamedItem("xMax").getNodeValue());
        aExtent.minY = Double.parseDouble(Extents.getAttributes().getNamedItem("yMin").getNodeValue());
        aExtent.maxY = Double.parseDouble(Extents.getAttributes().getNamedItem("yMax").getNodeValue());

        this.setViewExtent(aExtent);
    }

    /**
     * Load vector layer
     *
     * @param aVLayer Vector layer XML node
     * @return Vector layer
     */
    public VectorLayer loadVectorLayer(Node aVLayer) {
        String aFile = aVLayer.getAttributes().getNamedItem("FileName").getNodeValue();
        File lFile = new File(aFile);
        String curDir = System.getProperty("user.dir");
        if (new File(curDir).isFile()) {
            System.setProperty("user.dir", new File(curDir).getParent());
        }
        aFile = lFile.getAbsolutePath();
        VectorLayer aLayer = null;

        if (new File(aFile).isFile()) {
            try {
                aLayer = (VectorLayer) MapDataManage.loadLayer(aFile);
            } catch (IOException ex) {
                Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                aLayer.setHandle(Integer.parseInt(aVLayer.getAttributes().getNamedItem("Handle").getNodeValue()));
                aLayer.setLayerName(aVLayer.getAttributes().getNamedItem("LayerName").getNodeValue());
                aLayer.setVisible(Boolean.parseBoolean(aVLayer.getAttributes().getNamedItem("Visible").getNodeValue()));
                aLayer.setMaskout(Boolean.parseBoolean(aVLayer.getAttributes().getNamedItem("IsMaskout").getNodeValue()));
                aLayer.setTransparency(Integer.parseInt(aVLayer.getAttributes().getNamedItem("TransparencyPerc").getNodeValue()));
                aLayer.setAvoidCollision(Boolean.parseBoolean(aVLayer.getAttributes().getNamedItem("AvoidCollision").getNodeValue()));
                aLayer.setExpanded(Boolean.parseBoolean(aVLayer.getAttributes().getNamedItem("Expanded").getNodeValue()));
                aLayer.setLayerType(LayerTypes.valueOf(aVLayer.getAttributes().getNamedItem("LayerType").getNodeValue()));
                aLayer.setLayerDrawType(LayerDrawType.valueOf(aVLayer.getAttributes().getNamedItem("LayerDrawType").getNodeValue()));
            } catch (Exception e) {
            }

            //Load legend scheme
            Node LS = (Node) ((Element) aVLayer).getElementsByTagName("LegendScheme").item(0);
            LegendScheme ls = new LegendScheme(aLayer.getShapeType());
            ls.importFromXML(LS);
            aLayer.setLegendScheme(ls);

            //Load label set
            Node labelNode = (Node) ((Element) aVLayer).getElementsByTagName("LabelSet").item(0);
            LabelSet aLabelSet = new LabelSet();
            loadLabelSet(labelNode, aLabelSet);
            aLayer.setLabelSet(aLabelSet);

            //Load label graphics
            GraphicCollection gc = loadGraphicCollection((Element) aVLayer);
            aLayer.setLabelPoints(gc);

            //Load chart set 
            NodeList chartNodes = ((Element) aVLayer).getElementsByTagName("ChartSet");
            if (chartNodes.getLength() > 0) {
                Node chartNode = chartNodes.item(0);
                ChartSet aChartSet = new ChartSet();
                loadChartSet(chartNode, aChartSet);
                aLayer.setChartSet(aChartSet);

                //Load chart graphics
                gc = loadChartGraphicCollection((Element) aVLayer);
                aLayer.setChartPoints(gc);
                aLayer.updateChartsProp();
            }

            //Load visible scale
            NodeList visScaleNodes = ((Element) aVLayer).getElementsByTagName("VisibleScale");
            if (visScaleNodes.getLength() > 0) {
                Node visScaleNode = visScaleNodes.item(0);
                VisibleScale visScale = aLayer.getVisibleScale();
                loadVisibleScale(visScaleNode, visScale);
            }
        }

        return aLayer;
    }

    private void loadLabelSet(Node LabelNode, LabelSet aLabelSet) {
        try {
            aLabelSet.setDrawLabels(Boolean.parseBoolean(LabelNode.getAttributes().getNamedItem("DrawLabels").getNodeValue()));
            aLabelSet.setFieldName(LabelNode.getAttributes().getNamedItem("FieldName").getNodeValue());
            String fontName = LabelNode.getAttributes().getNamedItem("FontName").getNodeValue();
            float fontSize = Float.parseFloat(LabelNode.getAttributes().getNamedItem("FontSize").getNodeValue());
            aLabelSet.setLabelFont(new Font(fontName, Font.PLAIN, (int) fontSize));
            aLabelSet.setLabelColor(ColorUtil.parseToColor(LabelNode.getAttributes().getNamedItem("LabelColor").getNodeValue()));
            aLabelSet.setDrawShadow(Boolean.parseBoolean(LabelNode.getAttributes().getNamedItem("DrawShadow").getNodeValue()));
            aLabelSet.setShadowColor(ColorUtil.parseToColor(LabelNode.getAttributes().getNamedItem("ShadowColor").getNodeValue()));
            aLabelSet.setLabelAlignType(AlignType.valueOf(LabelNode.getAttributes().getNamedItem("AlignType").getNodeValue()));
            aLabelSet.setYOffset(Integer.parseInt(LabelNode.getAttributes().getNamedItem("Offset").getNodeValue()));
            aLabelSet.setAvoidCollision(Boolean.parseBoolean(LabelNode.getAttributes().getNamedItem("AvoidCollision").getNodeValue()));
            aLabelSet.setAutoDecimal(Boolean.parseBoolean(LabelNode.getAttributes().getNamedItem("AutoDecimal").getNodeValue()));
            aLabelSet.setDecimalDigits(Integer.parseInt(LabelNode.getAttributes().getNamedItem("DecimalDigits").getNodeValue()));
        } catch (Exception e) {
        }
    }

    private void loadChartSet(Node chartNode, ChartSet aChartSet) {
        try {
            aChartSet.setDrawCharts(Boolean.parseBoolean(chartNode.getAttributes().getNamedItem("DrawCharts").getNodeValue()));
            aChartSet.setChartType(ChartTypes.valueOf(chartNode.getAttributes().getNamedItem("ChartType").getNodeValue()));
            aChartSet.setFieldNames(new ArrayList<String>(Arrays.asList(chartNode.getAttributes().getNamedItem("FieldNames").getNodeValue().split(","))));
            aChartSet.setXShift(Integer.parseInt(chartNode.getAttributes().getNamedItem("XShift").getNodeValue()));
            aChartSet.setYShift(Integer.parseInt(chartNode.getAttributes().getNamedItem("YShift").getNodeValue()));
            aChartSet.setMaxSize(Integer.parseInt(chartNode.getAttributes().getNamedItem("MaxSize").getNodeValue()));
            aChartSet.setMinSize(Integer.parseInt(chartNode.getAttributes().getNamedItem("MinSize").getNodeValue()));
            aChartSet.setMaxValue(Float.parseFloat(chartNode.getAttributes().getNamedItem("MaxValue").getNodeValue()));
            aChartSet.setMinValue(Float.parseFloat(chartNode.getAttributes().getNamedItem("MinValue").getNodeValue()));
            aChartSet.setBarWidth(Integer.parseInt(chartNode.getAttributes().getNamedItem("BarWidth").getNodeValue()));
            aChartSet.setAvoidCollision(Boolean.parseBoolean(chartNode.getAttributes().getNamedItem("AvoidCollision").getNodeValue()));
            aChartSet.setAlignType(AlignType.valueOf(chartNode.getAttributes().getNamedItem("AlignType").getNodeValue()));
            aChartSet.setView3D(Boolean.parseBoolean(chartNode.getAttributes().getNamedItem("View3D").getNodeValue()));
            aChartSet.setThickness(Integer.parseInt(chartNode.getAttributes().getNamedItem("Thickness").getNodeValue()));
        } catch (Exception e) {
        }

        //Load legend scheme
        Node lsNode = ((Element) chartNode).getElementsByTagName("LegendScheme").item(0);
        aChartSet.getLegendScheme().importFromXML(lsNode);
    }

    private void loadVisibleScale(Node visScaleNode, VisibleScale visibleScale) {
        try {
            visibleScale.setEnableMinVisScale(Boolean.parseBoolean(visScaleNode.getAttributes().getNamedItem("EnableMinVisScale").getNodeValue()));
            visibleScale.setEnableMaxVisScale(Boolean.parseBoolean(visScaleNode.getAttributes().getNamedItem("EnableMaxVisScale").getNodeValue()));
            visibleScale.setMinVisScale(Double.parseDouble(visScaleNode.getAttributes().getNamedItem("MinVisScale").getNodeValue()));
            visibleScale.setMaxVisScale(Double.parseDouble(visScaleNode.getAttributes().getNamedItem("MaxVisScale").getNodeValue()));
        } catch (Exception e) {
        }
    }

    /**
     * Load image layer
     *
     * @param aILayer Image layer XML node
     * @return Image layer
     */
    public ImageLayer loadImageLayer(Node aILayer) {
        String aFile = aILayer.getAttributes().getNamedItem("FileName").getNodeValue();
        File lFile = new File(aFile);
        String curDir = System.getProperty("user.dir");
        if (new File(curDir).isFile()) {
            System.setProperty("user.dir", new File(curDir).getParent());
        }
        aFile = lFile.getAbsolutePath();
        ImageLayer aLayer = null;

        if (new File(aFile).exists()) {
            try {
                aLayer = MapDataManage.readImageFile(aFile);
            } catch (IOException ex) {
                Logger.getLogger(MapView.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                aLayer.setHandle(Integer.parseInt(aILayer.getAttributes().getNamedItem("Handle").getNodeValue()));
                aLayer.setLayerName(aILayer.getAttributes().getNamedItem("LayerName").getNodeValue());
                aLayer.setVisible(Boolean.parseBoolean(aILayer.getAttributes().getNamedItem("Visible").getNodeValue()));
                aLayer.setMaskout(Boolean.parseBoolean(aILayer.getAttributes().getNamedItem("IsMaskout").getNodeValue()));
                aLayer.setLayerType(LayerTypes.valueOf(aILayer.getAttributes().getNamedItem("LayerType").getNodeValue()));
                aLayer.setLayerDrawType(LayerDrawType.valueOf(aILayer.getAttributes().getNamedItem("LayerDrawType").getNodeValue()));
                aLayer.setTransparency(Integer.parseInt(aILayer.getAttributes().getNamedItem("TransparencyPerc").getNodeValue()));
                aLayer.setTransparencyColor(ColorUtil.parseToColor(aILayer.getAttributes().getNamedItem("TransparencyColor").getNodeValue()));
                aLayer.setUseTransColor(Boolean.parseBoolean(aILayer.getAttributes().getNamedItem("SetTransColor").getNodeValue()));

                //Load visible scale
                NodeList visScaleNodes = ((Element) aILayer).getElementsByTagName("VisibleScale");
                if (visScaleNodes.getLength() > 0) {
                    Node visScaleNode = visScaleNodes.item(0);
                    VisibleScale visScale = aLayer.getVisibleScale();
                    loadVisibleScale(visScaleNode, visScale);
                }
            } catch (Exception e) {
            }
        }

        return aLayer;
    }

    /**
     * Load graphics
     *
     * @param parent Parent XML element
     */
    public void loadGraphics(Element parent) {
        _graphicCollection = loadGraphicCollection(parent);
    }

    private GraphicCollection loadChartGraphicCollection(Element parent) {
        GraphicCollection gc = new GraphicCollection();
        Element graphics = (Element) parent.getElementsByTagName("ChartGraphics").item(0);
//        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
//            Node aNode = parent.getChildNodes().item(i);
//            if ("ChartGraphics".equals(aNode.getNodeName())) {
//                graphics = aNode;
//                break;
//            }
//        }
        if (graphics != null) {
            NodeList nList = graphics.getElementsByTagName("Graphic");
            for (int i = 0; i < nList.getLength(); i++) {
                Node graphicNode = nList.item(i);
                Graphic aGraphic = new Graphic();
                aGraphic.importFromXML((Element) graphicNode);
                gc.add(aGraphic);
            }
        }

        return gc;
    }

    /**
     * Load graphic collection
     *
     * @param parent Parent graphics node
     * @return Graphic collection
     */
    private GraphicCollection loadGraphicCollection(Element parent) {
        GraphicCollection gc = new GraphicCollection();
        //Element graphics = (Element)parent.getElementsByTagName("Graphics").item(0);
        Node graphics = null;
        for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
            Node aNode = parent.getChildNodes().item(i);
            if ("Graphics".equals(aNode.getNodeName())) {
                graphics = aNode;
                break;
            }
        }
        if (graphics != null) {
            NodeList nList = ((Element) graphics).getElementsByTagName("Graphic");
            for (int i = 0; i < nList.getLength(); i++) {
                Node graphicNode = nList.item(i);
                Graphic aGraphic = new Graphic();
                aGraphic.importFromXML((Element) graphicNode);
                gc.add(aGraphic);
            }
        }

        return gc;
    }
    // </editor-fold>

    // <editor-fold desc="General">
    /**
     * Projection layers
     *
     * @param toProj To projection info
     */
    public void projectLayers(ProjectionInfo toProj) {
        _projection.projectLayers(this, toProj);
        this.fireProjectionChangedEvent();
    }

    /**
     * Show measurment form
     */
    public void showMeasurementForm() {
        if (_frmMeasure == null) {
            _frmMeasure = new FrmMeasurement((JFrame) SwingUtilities.getWindowAncestor(this), false);
            _frmMeasure.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    repaint();
                    setDrawIdentiferShape(false);
                }
            });
            _frmMeasure.setLocationRelativeTo(this);
            _frmMeasure.setVisible(true);
        } else if (!_frmMeasure.isVisible()) {
            _frmMeasure.setVisible(true);
        }
    }
    // </editor-fold>
    // </editor-fold>    
}