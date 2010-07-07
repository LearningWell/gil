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
package gil.web.html.forms;

import gil.web.html.PlainText;

/**
 * Class for a html label to be contained in a {@link Form Form}.
 * 
 * @author Göran Larsson @ LearningWell AB
 */
public class Label extends Control {
    /**
     * 
     * @param label The label text to be viewed.
     * @param labelForId Associates this label with the control with the given id.
     */
    public Label(String label, String labelForId) {
        super(ControlType.label);
        super.addAttribute("for", labelForId);
        super.setContent(new PlainText(label));
    }
}
