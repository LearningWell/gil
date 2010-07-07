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
package gil.io;

import java.io.IOException;
import java.util.Map;
import gil.core.Command;

/**
 * Interface for classes that have the possibility of invoking control commands.
 * @author Göran Larsson @ LearningWell AB
 */
public interface IControlCommandInvokeable {

    /**
     * Invokes a control command.
     * <p>
     * Used to invoke commands to control the behavior of the implementation. The commands are specific to each
     * implementation of this interface.
     * @param command The command to invoke.
     * @return A map of result parameters.
     * @throws IllegalArgumentException If the given command or any parameter is illegal.
     * @throws IOException If the execution of the command failed.
     */
    Map<String, String> invokeControlCommand(Command command) throws IllegalArgumentException, IOException;

}
