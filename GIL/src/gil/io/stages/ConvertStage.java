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
package gil.io.stages;

import gil.io.PipelineStage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import gil.common.Parameters;
import gil.core.Command;
import gil.core.CommandDescriptor;
import gil.core.IllegalConfigurationException;
import gil.core.ParameterDescriptor;
import gil.core.SignalMetadata;
import gil.core.SignalMetadata.DataflowDirection;

/**
 * This stage converts the values between electrical and engineering units. The stage supports primitive
 * Float32 and Float64 datatypes. Other datatypes are left untouched by this stage.
 * @author Göran Larsson @ LearningWell AB
 */
public class ConvertStage extends PipelineStage {

    private static Logger _logger = Logger.getLogger(ConvertStage.class);

    public class ConversionFactor {
        public double gain;
        public double offset;
    }
    
    private ConversionFactor[] _factorsToES = new ConversionFactor[0];
    private ConversionFactor[] _factorsToPM = new ConversionFactor[0];
    private boolean _bypass = false;
    
    public void processSignals(ByteBuffer values, DataflowDirection direction) {
        if (_bypass) {
            return;
        }

        if (direction == DataflowDirection.ToES) {
            int i = 0;
            for (SignalMetadata md : getSignalsToES()) {
                int pos = values.position();
                for (int elm = 0; elm < md.getLength(); elm++) {
                    int pos2 = values.position();
                    if (md.getDataType() == SignalMetadata.SignalDataType.Float32) {
                        double oldValue = values.getFloat();
                        double newValue = (oldValue * _factorsToES[i].gain) + _factorsToES[i].offset;
                        values.putFloat(pos2, (float)newValue);
                    }
                    if (md.getDataType() == SignalMetadata.SignalDataType.Float64) {
                        double oldValue = values.getDouble();
                        double newValue = (oldValue * _factorsToES[i].gain) + _factorsToES[i].offset;
                        values.putDouble(pos2, newValue);
                    }
                }
                values.position(pos + md.getBufferSize());
                ++i;
            }
        }
        else if (direction == DataflowDirection.ToPM) {
            int i = 0;
            for (SignalMetadata md : getSignalsToPM()) {
                int pos = values.position();
                for (int elm = 0; elm < md.getLength(); elm++) {
                    int pos2 = values.position();

                    if (md.getDataType() == SignalMetadata.SignalDataType.Float32) {
                        double oldValue = values.getFloat();
                        double newValue = (oldValue - _factorsToPM[i].offset) / _factorsToPM[i].gain;
                        values.putFloat(pos2, (float)newValue);
                    }
                    if (md.getDataType() == SignalMetadata.SignalDataType.Float64) {
                        double oldValue = values.getDouble();
                        double newValue = (oldValue - _factorsToPM[i].offset) / _factorsToPM[i].gain;
                        values.putDouble(pos2, newValue);
                    }
                }
                values.position(pos + md.getBufferSize());
                ++i;
            }
        }
        else {
            throw new AssertionError("Unexpected DataflowDirection");
        }
    }

    @Override
    public void setup(String id, SignalMetadata[] signalsToPM, SignalMetadata[] signalsToES, Parameters config) throws IllegalConfigurationException {
        super.setup(id, signalsToPM, signalsToES, config);
        _factorsToES = _createFactors(signalsToES);
        _factorsToPM = _createFactors(signalsToPM);
    }

    public Map<String, String> invokeControlCommand(Command command) {
        if (command.isCommand("bypass")) {
            if (command.getParameter("doBypass").equals("false"))
                _bypass = false;
            else if (command.getParameter("doBypass").equals("true"))
                _bypass = true;
            else
                throw new IllegalArgumentException("Parameter 'doBypass' is neither 'false' or 'true' in command '" + command.getID() + "'");

            _logger.info(String.format("Command '%s' invoked", command));
            return new HashMap<String, String>();
        }        
        throw new IllegalArgumentException("Unknown control command: " + command);
    }

    private ConversionFactor[] _createFactors(SignalMetadata[] signalMetadata) throws IllegalConfigurationException {
        ConversionFactor[] factors = new ConversionFactor[signalMetadata.length];
        for (int i = 0; i < signalMetadata.length; i++) {
            ConversionFactor cf = new ConversionFactor();
            String sPMMax = signalMetadata[i].getCustomAttributes().get("PMMax");
            String sPMMin = signalMetadata[i].getCustomAttributes().get("PMMin");
            String sESMax = signalMetadata[i].getCustomAttributes().get("ESMax");
            String sESMin = signalMetadata[i].getCustomAttributes().get("ESMin");
            if (sPMMax == null && sPMMin == null && sESMax == null && sESMin == null) {
                cf.gain = 1.0;
                cf.offset = 0;
            } else {
                try {
                    cf.gain = (Double.parseDouble(sESMax) - Double.parseDouble(sESMin)) / (Double.parseDouble(sPMMax) - Double.parseDouble(sPMMin));
                    cf.offset = Double.parseDouble(sESMin) - (cf.gain * Double.parseDouble(sPMMin));
                    checkSupportedDatatypes(signalMetadata[i]);

                } catch (NullPointerException ex) {
                    throw new IllegalConfigurationException("Not all attributes PMMax, PMin, ESMax and ESMin found on index " + i);
                } catch (NumberFormatException ex) {
                    throw new IllegalConfigurationException("Illegal number format on attribute PMMax, PMin, ESMax or ESMin on index " + i);
                }                
            }
            factors[i] = cf;
        }
        return factors;
    }

    public CommandDescriptor[] availableCommands() {
        return new CommandDescriptor[] {
            new CommandDescriptor("bypass", "Bypasses the convert stage i.e. the passing values are unaffected.",
                new ParameterDescriptor[]
                    {
                        new ParameterDescriptor("doBypass", "true|false to switch the bypass on or off.", true),
                    }
            )
        };
    }

    private void checkSupportedDatatypes(SignalMetadata signalMetadata) throws IllegalConfigurationException {
        if (signalMetadata.getDataType() != SignalMetadata.SignalDataType.Float32 && signalMetadata.getDataType() != SignalMetadata.SignalDataType.Float64) {
            throw new IllegalConfigurationException("Conversion stage does not support the datatype " + signalMetadata.getDataType().name());
        }
    }
}
