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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "seqNo",
    "name",
    "commands"
})
@XmlRootElement(name = "pipelineStage")
public class PipelineStage {
    private int seqNo;
    private String name;
    private String commands;

    public PipelineStage(int seqNo, String name, String commandsLink) {
        this.seqNo = seqNo;
        this.name = name;
        this.commands = commandsLink;
    }

    public PipelineStage() { }

    /**
     * Sequence number tells where the stage is placed in the transfer pipeline. seqNo 0 is the first stage.
     */
    public int getSeqNo() {
        return seqNo;
    }

    public String getName() {
        return name;
    }

    public String getCommandsLink() {
        return commands;
    }
}
