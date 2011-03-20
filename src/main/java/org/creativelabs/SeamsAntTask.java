package org.creativelabs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SeamsAntTask extends Task {
    private String msg;

    public void execute() throws BuildException {
        System.out.println(msg);
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
