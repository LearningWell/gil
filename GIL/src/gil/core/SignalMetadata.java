/*
    Copyright (C) 2010 LearningWell AB (www.learningwell.com), Kärnkraftsäkerhet och Utbildning AB (www.ksu.se)

    This file is part of GIL (Generic Integration Layer).

    GIL is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GIL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with GIL.  If not, see <http://www.gnu.org/licenses/>.
*/
package gil.core;

import java.util.*;

/**
 * Specifies a signal value.
 * @author Göran Larsson - LearningWell AB
 */
public class SignalMetadata implements Cloneable {

    private SignalType _type = SignalType.Analog;
    private SignalDataType _dataType = SignalDataType.Illegal;
    private int _length = 1;
    private DataflowDirection _dataflowDirection = DataflowDirection.ToPM;
    private String _pmSignalID = "";
    private String _esSignalID = "";
    private String _description = "";
    private int _selFileLineNo = 0;
    private HashMap<String, String> _attributes = new HashMap<String, String>();
    private int _bufferPos = -1;

    private static final HashMap<SignalDataType, Integer> _DATA_TYPE_BYTES =
            new HashMap<SignalDataType, Integer>()
            {
                {
                    put(SignalDataType.Float32, 4);
                    put(SignalDataType.Float64, 8);
                    put(SignalDataType.Int8, 1);
                    put(SignalDataType.Int16, 2);
                    put(SignalDataType.Int32, 4);
                }
            };

    public static enum SignalType {
        Analog,
        Digital
    }

    // Note!!! The order of the enum names are important since they may be acessed from C++ via JNI.
    public static enum SignalDataType {
        Illegal,
        Float32,
        Float64,
        Int8,
        Int16,
        Int32
    }
    
    public static enum DataflowDirection {
        ToPM,
        ToES
    }

    public SignalMetadata() {
    }

    /**
     * Constructs a metadata object defining a set of values.
     */
    public SignalMetadata(SignalType type, SignalDataType dataType, int length, DataflowDirection dataflowDirection,
            String pmSignalID, String esSignalID, String description, HashMap<String, String> attributes) {

        _type = type;
        _dataType = dataType;
        _length = length;
        _dataflowDirection = dataflowDirection;
        _pmSignalID = pmSignalID;
        _esSignalID = esSignalID;
        _description = description;
        _attributes = attributes;
    }

    /**
     * Returns the the size in bytes for the given dataType.
     */
    public static int size(SignalDataType dataType) {
        return _DATA_TYPE_BYTES.get(dataType);
    }

    /**
     * Calculates the bytes necessary in a buffer to hold length number of dataType elements.
     */
    public static int calcByteCount(SignalDataType dataType, int length) {
        return _DATA_TYPE_BYTES.get(dataType) * length;
    }

    /**
     * Returns the file line number where this signal metadata was defined. 
     * <p>
     * The purpose is to make it possible to give precise error information to the user.
     * @return
     */
    public int getSelFileLineNo() {
        return _selFileLineNo;
    }

    /**
     * Sets the file line number where this signal metadata was defined.
     * <p>
     * Used when the SignalMetadata definition is read from file.
     * The purpose is to make it possible to output precise error information to the user.
     * @return
     */
    public void setSelFileLineNo(int lineNo) {
        _selFileLineNo = lineNo;
    }

    /**
     * Returns the signal type (Analog/Digital).
     * <p>
     * The meaning of type is if the signal value is a continuous analog value or a digital value that can
     * be either true or false (1/0).
     */
    public SignalType getType() {
        return _type;
    }


    /**
     * Sets the signal type (Analog/Digital).
     * <p>
     * The meaning of type is if the signal value is a continuous analog value or a digital value that can
     * be either true or false (1/0).
     */
    public void setType(SignalType st) {
        _type = st;
    }

    /**
     * Returns the data type (float, int, etc.).
     */
    public SignalDataType getDataType() {
        return _dataType;
    }

    /**
     * Sets the data type (float, int, etc.).
     */
    public void setDataType(SignalDataType sd) {
        _dataType = sd;
    }

    /**
     * Returns the number of elements of the given data type that this meta data object defines.
     */
    public int getLength() {
        return _length;
    }

    /**
     * Sets the number of elements of the given data type that this meta data object defines.
     */
    public void setLength(int length) {
        _length = length;
    }

    /**
     * Returns the ordinal of the {@link SignalDataType} enumeration constant.
     * This has only use if the SignalMetadata object is accessed from within native code through JNI.
     * @return The ordinal of this enumeration constant.
     */
    public int getDataTypeAsOrdinal() {
        return _dataType.ordinal();
    }

    public void setDataTypeAsOrdinal(int ordinal) {
        SignalDataType[] sds = SignalDataType.values();
        if (ordinal < sds.length)
        {
            _dataType = sds[ordinal];
        }
    }

    /**
     * Returns the data flow direction for this signal.
     */
    public DataflowDirection getDataflowDirection() {
        return _dataflowDirection;
    }

    /**
     * Returns the process model signal id.
     * <p>
     * This unique id identifies the signal for the process model adapter. This id has no meaning for the
     * GIL framework. It is used solely by the PM-adapter.
     */
    public String getPMSignalID() {
        return _pmSignalID;
    }

    /**
     * Returns the external system signal id.
     * <p>
     * This unique id identifies the signal for the external system adapter. This id has no meaning for the
     * GIL framework. It is used solely by the ES-adapter.
     */
    public String getESSignalID() {
        return _esSignalID;
    }

    /**
     * Returns a descriptive text for the signal.
     */
    public String getDescription() {
        return _description;
    }

    /**
     * Returns key-value pairs of custom attributes.
     * <p>
     * This is a set of arbitrary attributes that may be interpreted and used by either or all of the ES-adapter,
     * PM-adapter and pipeline stages.
     */
    public Map<String, String> getCustomAttributes() {
        return _attributes;
    }

    /**
     * Returns a deep copy of this object.
     */
    @Override
    public Object clone() {
        SignalMetadata md = new SignalMetadata(_type, _dataType, _length, _dataflowDirection,
            _pmSignalID, _esSignalID, _description, (HashMap<String, String>)_attributes.clone());
        md.setSelFileLineNo(_selFileLineNo);
        return md;
    }

    /**
     * Returns the necessary number of bytes a buffer must allocate to be able to hold
     * signal data specified by this metadata object.
     */
    public int getBufferSize() {
        return calcByteCount(this.getDataType(), _length);
    }


    /**
     * Sets the position in a ByteBuffer where data specified by this object is located.
     */
    public void setBufferPos(int pos) {
        _bufferPos = pos;

    }

    /**
     * Returns the position in a ByteBuffer where data specified by this object is located.
     * Returns -1 if not set.
     */
    public int getBufferPos() {
        return _bufferPos;
    }

    /**
     * Calculates the necessary number of bytes a buffer must allocate to be able to hold
     * signal data specified by the given signal metadata array.
     * @return Requested number of bytes
     */
    public static int calcBufferSize(SignalMetadata[] mds) {
        int total = 0;
        for (SignalMetadata md : mds) {
            total += md.getBufferSize();
        }
        return total;
    }

    /**
     * Retrieves the subset of signals with a dataflow direction from the PM to the ES.
     * <p>
     * @param allSignals The complete set of signal metadata objects whichever dataflow direction.
     * @return The subset of signals with a dataflow direction from the process model to the external system.
     */
    public static SignalMetadata[] getSignalsToExternalSystem(SignalMetadata[] allSignals) {
        return _getSignals(allSignals, DataflowDirection.ToES);
    }


    /**
     * Retrieves the subset of signals with a dataflow direction from the ES to the PM.
     * <p>
     * @param allSignals The complete set of signal metadata objects whichever dataflow direction.
     * @return The subset of signals with a dataflow direction from the external system to the process model.
     */
    public static SignalMetadata[] getSignalsToProcessModel(SignalMetadata[] allSignals) {
        return _getSignals(allSignals, DataflowDirection.ToPM);
    }
    
    private static SignalMetadata[] _getSignals(SignalMetadata[] signals, SignalMetadata.DataflowDirection requestedDir) {
        List<SignalMetadata> singleDirSignals = new ArrayList<SignalMetadata>();
        for (SignalMetadata sm : signals) {
            if (sm.getDataflowDirection() == requestedDir) {
                singleDirSignals.add(sm);
            }
        }
        return singleDirSignals.toArray(new SignalMetadata[0]);
    }
}
