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

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import gil.web.html.PlainText;
import gil.web.html.Tag;

/**
 * Class for a html select control to be contained in a {@link Form Form}.
 * 
 * @author Göran Larsson @ LearningWell AB
 */
public class Select extends Control {

    private class Option {

        public Option(String optionValue, String optionText, boolean isSelected) {
            this.optionValue = optionValue;
            this.optionText = optionText;
            this.isSelected = isSelected;
        }

        public String optionValue;
        public String optionText;
        public boolean isSelected;
    }

    private List<Option> _options = new ArrayList<Option>();

    public Select(String id) {
        super(Control.ControlType.select, id);
    }

    public Select addOption(String optionValue, String optionText, boolean isSelected) {
        _options.add(new Option(optionValue, optionText, isSelected));
        return this;
    }

    @Override
    public Node asNode(Document ownerDocument) {
        Element select = (Element) super.asNode(ownerDocument);
        int i = 0;
        for (Option o : _options) {
            Tag t = new Tag("option").setContent(new PlainText(o.optionText))
                    .addAttribute("value", o.optionValue);
            
            if (o.isSelected) {
                t.addAttribute("selected", "");
            }
            select.appendChild(t.asNode(ownerDocument));
            ++i;
        }
        return select;
    }
}
