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
package gil.web.jaxb;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB class representing a result from an execution of a command.
 */
@XmlRootElement(name = "commandResult")
@XmlAccessorType( XmlAccessType.PUBLIC_MEMBER )
public class CommandResult {

    private HashMap<String, String> resultParameters;

    public CommandResult() {
    }

    public CommandResult(Map<String, String> resultParameters) {
        this.resultParameters = new HashMap<String, String>(resultParameters);
    }

    public HashMap<String, String> getResultParameters() {
        return resultParameters;
    }

    public void setResultParameters(HashMap<String, String> resultParameters) {
        this.resultParameters = resultParameters;
    }
}
