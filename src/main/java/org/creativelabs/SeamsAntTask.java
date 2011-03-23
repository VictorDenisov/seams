package org.creativelabs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class SeamsAntTask extends MatchingTask {
    private String msg;
    private Path src;

    public Path createSrc() {
        if (src == null) {
            src = new Path(getProject());
        }
        return src.createPath();
    }

    protected Path recreateSrc() {
        src = null;
        return createSrc();
    }

    public void setSrcdir(Path srcDir) {
        if (src == null) {
            src = srcDir;
        } else {
            src.append(srcDir);
        }
    }

    public void execute() throws BuildException {
        System.out.println(msg);
        System.out.println(src);
        for (String s : src.list()) {
            System.out.println(s);
        }
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
