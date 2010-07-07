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
package gil.web.html.page;

import gil.web.html.Div;
import gil.web.html.forms.*;
import gil.web.html.Heading;
import gil.web.html.Paragraph;
import gil.web.jaxb.Command;

/**
 * The html page for viewing and ivoking control commands. The page hosts two html forms. The first form for selecting
 * a command commads in a drop down list. The second form for parameter input and invoking the command.
 * @author Göran Larsson @ LearningWell AB
 */
public class CommandsPage extends MasterPage {

    /**
     *
     * @param commandBasePath the uri for the resource on which the commands may be invoked.
     * @param commands the collection of commands that may be invoked.
     * @param selectedCommand the name of the command in the collection of available commands that is selected. This
     * page displays all possible paramaters for the selected command.
     */
    public CommandsPage(String title, String commandBasePath, Command[] commands, String selectedCommand) {
        
        Div div = new Div("commandPage");

        div.addContent(new Heading(title, Heading.H2));

        if (commands.length < 1) {
            div.addContent(new Heading("No control commands ar available", Heading.H3));
            this.setSectionContent("content", div);
            return;
        }

        if (selectedCommand.isEmpty()) {
            // Select the first command in the drop-down if no command is previously selected.
            selectedCommand = commands[0].getName();
        }

        // Form for selecting command
        Form commandSelectForm = (Form)new Form(commandBasePath, Form.Method.get).addAttribute("name", "cmdSelectForm");
        String id = uniqueID();
        commandSelectForm.addContent(new Label("Command: ", id));
        Select selectControl = (Select) new Select(id).addAttribute("name", "command")
                .addAttribute("onChange", "cmdSelectForm.submit();").addAttribute("value", selectedCommand);
        addAvailableCommands(selectControl, commands, selectedCommand);
        commandSelectForm.addContent(selectControl);
        div.addContent(commandSelectForm);

        // Form for parameter input and invoking the command by submitting the form.
        div.addContent(new Heading("Parameters", Heading.H3));
        Command cd = Command.find(selectedCommand, commands);
        if (cd != null) {
            Form form = new Form(commandBasePath + "/" + selectedCommand, Form.Method.post);
            addParametersToForm(form, cd);
            Paragraph p = new Paragraph();
            form.addContent(p.addContent(new Input(Input.Type.submit).setClass("submit")));
            div.addContent(form);
        }

        this.setSectionContent("content", div); 
    }

    private Select addAvailableCommands(Select selectControl, Command[] cmds, String selected) {
        for (Command cmd : cmds) {
            boolean isSelected = (cmd.getName().equals(selected)) ? true : false;
            selectControl.addOption(cmd.getName(), cmd.getName(), isSelected);
        }
        return selectControl;
    }

    private void addParametersToForm(Form form, Command cmd) {
        for (Command.Parameters.Parameter pd : cmd.getParameters().getParameter()) {
            String id = uniqueID();
            Paragraph p = new Paragraph();
            p.addContent(new Label(pd.getName() + ":", id));
            p.addContent((Input)new Input(Input.Type.text, id).addAttribute("name", pd.getName()));
            form.addContent(p);
        }        
    }
}
