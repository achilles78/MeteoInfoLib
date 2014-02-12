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
package org.meteoinfo.data.meteodata;

import org.meteoinfo.data.GridData;
import org.meteoinfo.data.StationData;
import org.meteoinfo.data.meteodata.arl.ARLDataInfo;
import org.meteoinfo.data.meteodata.ascii.ASCIIGridDataInfo;
import org.meteoinfo.data.meteodata.ascii.LonLatStationDataInfo;
import org.meteoinfo.data.meteodata.ascii.SurferGridDataInfo;
import org.meteoinfo.data.meteodata.grads.GrADSDataInfo;
import org.meteoinfo.data.meteodata.hysplit.HYSPLITConcDataInfo;
import org.meteoinfo.data.meteodata.hysplit.HYSPLITPartDataInfo;
import org.meteoinfo.data.meteodata.hysplit.HYSPLITTrajDataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPS1DataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPS3DataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPS4DataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPSDataInfo;
import org.meteoinfo.data.meteodata.netcdf.NetCDFDataInfo;
import java.io.IOException;
import org.meteoinfo.projection.ProjectionInfo;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.meteoinfo.data.meteodata.micaps.MICAPS11DataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPS13DataInfo;
import org.meteoinfo.data.meteodata.micaps.MICAPS7DataInfo;
import org.meteoinfo.global.mathparser.MathParser;
import org.meteoinfo.global.mathparser.ParseException;

/**
 *
 * @author Yaqiang Wang
 */
public class MeteoDataInfo {
    // <editor-fold desc="Variables">

    private PlotDimension _dimensionSet = PlotDimension.Lat_Lon;
    private int _varIdx;
    private int _timeIdx;
    private int _levelIdx;
    private int _latIdx;
    private int _lonIdx;
    /// <summary>
    /// Meteological data type
    /// </summary>
    private MeteoDataType _dataType;
    /// <summary>
    /// Is Lont/Lat
    /// </summary>
    public boolean IsLonLat;
    /// <summary>
    /// If the U/V of the wind are along latitude/longitude.
    /// </summary>
    public boolean EarthWind;
    private DataInfo _dataInfo;
    /// <summary>
    /// Data information text
    /// </summary>
    private String _infoText;
    /// <summary>
    /// Wind U/V variable name
    /// </summary>
    private MeteoUVSet _meteoUVSet;
    /// <summary>
    /// If X reserved
    /// </summary>
    public boolean xReserve;
    /// <summary>
    /// If Y reserved
    /// </summary>
    public boolean yReserve;
    // </editor-fold>
    // <editor-fold desc="Constructor">

    /**
     * Constructor
     */
    public MeteoDataInfo() {
        _dataInfo = null;
        IsLonLat = true;
        EarthWind = true;
        _infoText = "";
        _meteoUVSet = new MeteoUVSet();
        xReserve = false;
        yReserve = false;
    }
    // </editor-fold>
    // <editor-fold desc="Get Set Methods">

    /**
     * Get data info
     *
     * @return Data info
     */
    public DataInfo getDataInfo() {
        return _dataInfo;
    }

    /**
     * Set data info
     *
     * @param value Data info
     */
    public void setDataInfo(DataInfo value) {
        _dataInfo = value;
    }

    /**
     * Get projection info
     *
     * @return Projection info
     */
    public ProjectionInfo getProjectionInfo() {
        return _dataInfo.getProjectionInfo();
    }

    /**
     * Get meteo data type
     *
     * @return Meteo data type
     */
    public MeteoDataType getDataType() {
        return this._dataType;
    }

    /**
     * Set meteo data type
     *
     * @param type Meteo data type
     */
    public void setDataType(MeteoDataType type) {
        _dataType = type;
    }

    /**
     * Get plot dimension
     *
     * @return
     */
    public PlotDimension getDimensionSet() {
        return _dimensionSet;
    }

    /**
     * Set plot dimension
     *
     * @param value Plot dimension
     */
    public void setDimensionSet(PlotDimension value) {
        _dimensionSet = value;
    }

    /**
     * Get data info text
     *
     * @return
     */
    public String getInfoText() {
        return _infoText;
    }

    /**
     * Get time index
     *
     * @return Time index
     */
    public int getTimeIndex() {
        return _timeIdx;
    }

    /**
     * Set time index
     *
     * @param value Time index
     */
    public void setTimeIndex(int value) {
        _timeIdx = value;
    }

    /**
     * Get level index
     *
     * @return Level index
     */
    public int getLevelIndex() {
        return _levelIdx;
    }

    /**
     * Set level index
     *
     * @param value Level index
     */
    public void setLevelIndex(int value) {
        _levelIdx = value;
    }

    /**
     * Get variable index
     *
     * @return Variable index
     */
    public int getVariableIndex() {
        return _varIdx;
    }

    /**
     * Set variable index
     *
     * @param value Variable index
     */
    public void setVariableIndex(int value) {
        _varIdx = value;
    }

    /**
     * Get longitude index
     *
     * @return Longitude index
     */
    public int getLonIndex() {
        return _lonIdx;
    }

    /**
     * Set longitude index
     *
     * @param value Longitude index
     */
    public void setLonIndex(int value) {
        _lonIdx = value;
    }

    /**
     * Get latitude index
     *
     * @return Latitude index
     */
    public int getLatIndex() {
        return _latIdx;
    }

    /**
     * Set latitude index
     *
     * @param value Latitude index
     */
    public void setLatIndex(int value) {
        _latIdx = value;
    }

    /**
     * Get Meteo U/V setting
     *
     * @return Meteo U/V setting
     */
    public MeteoUVSet getMeteoUVSet() {
        return _meteoUVSet;
    }

    /**
     * Set Meteo U/V Setting
     *
     * @param value Meteo U/V setting
     */
    public void setMeteoUVSet(MeteoUVSet value) {
        _meteoUVSet = value;
    }

    /**
     * Get missing value
     *
     * @return Missing value
     */
    public double getMissingValue() {
        return _dataInfo.getMissingValue();
    }

    /**
     * Get if is grid data
     *
     * @return Boolean
     */
    public boolean isGridData() {

        switch (_dataType) {
            case ARL_Grid:
            case ASCII_Grid:
            case GrADS_Grid:
            case GRIB1:
            case GRIB2:
            case HYSPLIT_Conc:
            case MICAPS_11:
            case MICAPS_13:
            case MICAPS_4:
            case Sufer_Grid:
                return true;
            case NetCDF:
                if (((NetCDFDataInfo) _dataInfo).isSWATH()) {
                    return false;
                } else {
                    return true;
                }
//                    case AWX:
//                        if (((AWXDataInfo)DataInfo).ProductType == 3) {
//                return true;
//            }
//                        else {
//                return false;
//            }
//                    case HDF:
//                        if (((HDF5DataInfo)DataInfo).CurrentVariable.IsSwath) {
//                return false;
//            }
//                        else {
//                return true;
//            }
            default:
                return false;
        }
    }

    /**
     * Get if is station data
     *
     * @return Boolean
     */
    public boolean isStationData() {
        switch (_dataType) {
            case GrADS_Station:
            case ISH:
            case METAR:
            case MICAPS_1:
            case MICAPS_2:
            case MICAPS_3:
            case LonLatStation:
            case SYNOP:
            case HYSPLIT_Particle:
                return true;
//                    case AWX:
//                        if (((AWXDataInfo)DataInfo).ProductType == 4)
//                            return true;
//                        else
//                            return false;
//                    case HDF:
//                        if (((HDF5DataInfo)DataInfo).CurrentVariable.IsSwath)
//                            return true;
//                        else
//                            return false;
            default:
                return false;
        }
    }

    /**
     * Get if is trajectory data
     *
     * @return Boolean
     */
    public boolean isTrajData() {
        switch (_dataType) {
            case HYSPLIT_Traj:
            case MICAPS_7:
                return true;
            default:
                return false;
        }
    }

    /**
     * Get if is SWATH data
     *
     * @return Boolean
     */
    public boolean isSWATHData() {
        switch (_dataType) {
            case NetCDF:
                if (((NetCDFDataInfo) _dataInfo).isSWATH()) {
                    return true;
                }
            default:
                return false;
        }
    }

    /**
     * Get variable dimension number
     *
     * @return Variable dimension number
     */
    public int getDimensionNumber() {
        int dn = 2;
        switch (_dimensionSet) {
            case Lat_Lon:
            case Level_Lat:
            case Level_Lon:
            case Level_Time:
            case Time_Lat:
            case Time_Lon:
                dn = 2;
                break;
            case Level:
            case Lon:
            case Time:
            case Lat:
                dn = 1;
                break;
        }

        return dn;
    }
    // </editor-fold>
    // <editor-fold desc="Methods">
    // <editor-fold desc="Open Data">

    /**
     * Open GrADS data
     *
     * @param aFile Data file path
     * @return If data opened correctly
     */
    public void openGrADSData(String aFile) {
        _dataInfo = new GrADSDataInfo();
        _dataInfo.readDataInfo(aFile);
        _infoText = _dataInfo.generateInfoText();
        GrADSDataInfo aDataInfo = (GrADSDataInfo) _dataInfo;
        if (aDataInfo.DTYPE.equals("Gridded")) {
            _dataType = MeteoDataType.GrADS_Grid;
            yReserve = aDataInfo.OPTIONS.yrev;

            if (!aDataInfo.isLatLon) {
                IsLonLat = false;
                EarthWind = aDataInfo.EarthWind;
            }
        } else {
            _dataType = MeteoDataType.GrADS_Station;
        }
    }

    /**
     * Open ARL packed meteorological data
     *
     * @param aFile File path
     */
    public void openARLData(String aFile) {
        ARLDataInfo aDataInfo = new ARLDataInfo();
        aDataInfo.readDataInfo(aFile);
        _dataType = MeteoDataType.ARL_Grid;
        _dataInfo = aDataInfo;
        IsLonLat = aDataInfo.isLatLon;

        //Get data info text
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open ASCII grid data
     *
     * @param aFile File path
     */
    public void openASCIIGridData(String aFile) {
        ASCIIGridDataInfo aDataInfo = new ASCIIGridDataInfo();
        aDataInfo.readDataInfo(aFile);
        _dataType = MeteoDataType.ARL_Grid;
        _dataInfo = aDataInfo;
        //ProjInfo = aDataInfo.projInfo;
        //IsLonLat = aDataInfo.isLatLon;

        //Get data info text
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open HYSPLIT concentration grid data
     *
     * @param aFile File path
     */
    public void openHYSPLITConcData(String aFile) {
        HYSPLITConcDataInfo aDataInfo = new HYSPLITConcDataInfo();
        aDataInfo.readDataInfo(aFile);
        _dataType = MeteoDataType.HYSPLIT_Conc;
        _dataInfo = aDataInfo;
        //ProjInfo = aDataInfo.projInfo;
        //IsLonLat = aDataInfo.isLatLon;

        //Get data info text
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open HYSPLIT trajectory data
     *
     * @param aFile File path
     */
    public void openHYSPLITTrajData(String aFile) {
        //Read data info                            
        HYSPLITTrajDataInfo aDataInfo = new HYSPLITTrajDataInfo();
        aDataInfo.readDataInfo(aFile);
        _dataType = MeteoDataType.HYSPLIT_Traj;
        _dataInfo = aDataInfo;
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open HYSPLIT traject data
     *
     * @param trajFiles File paths
     */
    public void openHYSPLITTrajData(String[] trajFiles) {
        try {
            //Read data info                            
            HYSPLITTrajDataInfo aDataInfo = new HYSPLITTrajDataInfo();
            aDataInfo.readDataInfo(trajFiles);
            _dataType = MeteoDataType.HYSPLIT_Traj;
            _dataInfo = aDataInfo;
            _infoText = aDataInfo.generateInfoText();
        } catch (IOException ex) {
            Logger.getLogger(MeteoDataInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Open HYSPLIT particle data
     *
     * @param fileName File path
     */
    public void openHYSPLITPartData(String fileName) {
        //Read data info                            
        HYSPLITPartDataInfo aDataInfo = new HYSPLITPartDataInfo();
        aDataInfo.readDataInfo(fileName);
        _dataType = MeteoDataType.HYSPLIT_Particle;
        _dataInfo = aDataInfo;
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open NetCDF data
     *
     * @param fileName File path
     */
    public void openNetCDFData(String fileName) {
        NetCDFDataInfo aDataInfo = new NetCDFDataInfo();
        aDataInfo.readDataInfo(fileName);
        _dataType = MeteoDataType.NetCDF;
        _dataInfo = aDataInfo;
        _infoText = aDataInfo.generateInfoText();
    }

    /**
     * Open Lon/Lat station data
     *
     * @param fileName File path
     */
    public void openLonLatData(String fileName) {
        _dataInfo = new LonLatStationDataInfo();
        _dataInfo.readDataInfo(fileName);
        _dataType = MeteoDataType.LonLatStation;
        _infoText = _dataInfo.generateInfoText();
    }

    /**
     * Open Surfer ASCII grid data
     *
     * @param fileName File path
     */
    public void openSurferGridData(String fileName) {
        _dataInfo = new SurferGridDataInfo();
        _dataInfo.readDataInfo(fileName);
        _dataType = MeteoDataType.Sufer_Grid;
        _infoText = _dataInfo.generateInfoText();
    }

    /**
     * Open MICAPS data
     *
     * @param fileName File name
     */
    public void openMICAPSData(String fileName) {
        MeteoDataType mdType = MICAPSDataInfo.getDataType(fileName);
        if (mdType == null) {
            return;
        }

        switch (mdType) {
            case MICAPS_1:
                _dataInfo = new MICAPS1DataInfo();
                _dataType = MeteoDataType.MICAPS_1;
                _meteoUVSet.setUV(false);
                _meteoUVSet.setFixUVStr(true);
                _meteoUVSet.setUStr("WindDirection");
                _meteoUVSet.setVStr("WindSpeed");
                break;
            case MICAPS_3:
                _dataInfo = new MICAPS3DataInfo();
                _dataType = MeteoDataType.MICAPS_3;
                _meteoUVSet.setUV(false);
                _meteoUVSet.setFixUVStr(true);
                _meteoUVSet.setUStr("WindDirection");
                _meteoUVSet.setVStr("WindSpeed");
                break;
            case MICAPS_4:
                _dataInfo = new MICAPS4DataInfo();
                _dataType = MeteoDataType.MICAPS_4;
                break;
            case MICAPS_7:
                _dataInfo = new MICAPS7DataInfo();
                _dataType = MeteoDataType.MICAPS_7;
                break;
            case MICAPS_11:
                _dataInfo = new MICAPS11DataInfo();
                _dataType = MeteoDataType.MICAPS_11;
                break;
            case MICAPS_13:
                _dataInfo = new MICAPS13DataInfo();
                _dataType = MeteoDataType.MICAPS_13;
                break;
        }
        _dataInfo.readDataInfo(fileName);
        _infoText = _dataInfo.generateInfoText();
    }
    // </editor-fold>

    // <editor-fold desc="Get Data">
    /**
     * Get file name
     *
     * @return
     */
    public String getFileName() {
        return _dataInfo.getFileName();
    }

    /**
     * Get grid data
     *
     * @param varName Variable name
     * @return Grid data
     */
    public GridData getGridData(String varName) {
        _varIdx = getVariableIndex(varName);
        if (_varIdx < 0) {
            MathParser mathParser = new MathParser(this);
            try {
                GridData gridData = (GridData) mathParser.evaluate(varName);
                return gridData;
            } catch (ParseException ex) {
                Logger.getLogger(MeteoDataInfo.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(MeteoDataInfo.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else {
            return this.getGridData();
        }
    }

    /**
     * Get grid data
     *
     * @return Grid data
     */
    public GridData getGridData() {
        if (_varIdx < 0) {
            return null;
        }

        switch (_dimensionSet) {
            case Lat_Lon:
                return ((IGridDataInfo) _dataInfo).getGridData_LonLat(_timeIdx, _varIdx, _levelIdx);
            case Time_Lon:
                return ((IGridDataInfo) _dataInfo).getGridData_TimeLon(_latIdx, _varIdx, _levelIdx);
            case Time_Lat:
                return ((IGridDataInfo) _dataInfo).getGridData_TimeLat(_lonIdx, _varIdx, _levelIdx);
            case Level_Lon:
                return ((IGridDataInfo) _dataInfo).getGridData_LevelLon(_latIdx, _varIdx, _timeIdx);
            case Level_Lat:
                return ((IGridDataInfo) _dataInfo).getGridData_LevelLat(_lonIdx, _varIdx, _timeIdx);
            case Level_Time:
                return ((IGridDataInfo) _dataInfo).getGridData_LevelTime(_latIdx, _varIdx, _lonIdx);
            case Lat:
                return ((IGridDataInfo) _dataInfo).getGridData_Lat(_timeIdx, _lonIdx, _varIdx, _levelIdx);
            case Level:
                return ((IGridDataInfo) _dataInfo).getGridData_Level(_lonIdx, _latIdx, _varIdx, _timeIdx);
            case Lon:
                return ((IGridDataInfo) _dataInfo).getGridData_Lon(_timeIdx, _latIdx, _varIdx, _levelIdx);
            case Time:
                return ((IGridDataInfo) _dataInfo).getGridData_Time(_lonIdx, _latIdx, _varIdx, _levelIdx);
            default:
                return null;
        }
    }

    /**
     * Get station data
     *
     * @param varName Variable name
     * @return Station data
     */
    public StationData getStationData(String varName) {
        _varIdx = getVariableIndex(varName);
        if (_varIdx >= 0) {
            return this.getStationData();
        } else {
            MathParser mathParser = new MathParser(this);
            try {
                StationData stationData = (StationData) mathParser.evaluate(varName);
                return stationData;
            } catch (ParseException ex) {
                Logger.getLogger(MeteoDataInfo.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(MeteoDataInfo.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    /**
     * Get station data
     *
     * @return Station data
     */
    public StationData getStationData() {
        if (_varIdx >= 0) {
            StationData stData = ((IStationDataInfo) _dataInfo).getStationData(_timeIdx, _varIdx, _levelIdx);
            return stData;
        } else {
            return null;
        }
    }

    /**
     * Get station model data
     *
     * @return Station model data
     */
    public StationModelData getStationModelData() {
        return ((IStationDataInfo) _dataInfo).getStationModelData(_timeIdx, _levelIdx);
    }

    /**
     * Get station info data
     *
     * @return Station info data
     */
    public StationInfoData getStationInfoData() {
        return ((IStationDataInfo) _dataInfo).getStationInfoData(_timeIdx, _levelIdx);
    }

    /**
     * Get station info data
     *
     * @param timeIndex Time index
     * @return Station info data
     */
    public StationInfoData getStationInfoData(int timeIndex) {
        return ((IStationDataInfo) _dataInfo).getStationInfoData(timeIndex, _levelIdx);
    }

    /**
     * Get variable index
     *
     * @param varName Variable name
     * @return Variable index
     */
    public int getVariableIndex(String varName) {
        List<String> varList = _dataInfo.getVariableNames();
        int idx = varList.indexOf(varName);

        return idx;
    }
    // </eidtor-fold>
    // </editor-fold>
}