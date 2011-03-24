package org.creativelabs;

import java.io.*;
import java.util.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.util.*;

public class SeamsAntTask extends MatchingTask {
    private String msg;
    private Path src;
    protected File[] compileList = new File[0];

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
    protected void checkParameters() throws BuildException {
        if (src == null) {
            throw new BuildException("srcdir attribute must be set!",
                                     getLocation());
        }
        if (src.size() == 0) {
            throw new BuildException("srcdir attribute must be set!",
                                     getLocation());
        }

    }

    protected void resetFileLists() {
        compileList = new File[0];
    }

    private String[] findSupportedFileExtensions() {
        String[] extensions = null;
        if (extensions == null) {
            extensions = new String[] { "java" };
        }

        // now process the extensions to ensure that they are the
        // right format
        for (int i = 0; i < extensions.length; i++) {
            if (!extensions[i].startsWith("*.")) {
                extensions[i] = "*." + extensions[i];
            }
        }
        return extensions; 
    }

    protected void scanDir(File srcDir, File destDir, String[] files) {
        GlobPatternMapper m = new GlobPatternMapper();
        String[] extensions = findSupportedFileExtensions();
        
        for (int i = 0; i < extensions.length; i++) {
            m.setFrom(extensions[i]);
            m.setTo("*.class");
            SourceFileScanner sfs = new SourceFileScanner(this);
            File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);

            if (newFiles.length > 0) {
                File[] newCompileList
                    = new File[compileList.length + newFiles.length];
                System.arraycopy(compileList, 0, newCompileList, 0,
                                 compileList.length);
                System.arraycopy(newFiles, 0, newCompileList,
                                 compileList.length, newFiles.length);
                compileList = newCompileList;
            }
        }
    }

    public void execute() throws BuildException {
        checkParameters();
        resetFileLists();

        // scan source directories and dest directory to build up
        // compile lists
        String[] list = src.list();
        for (int i = 0; i < list.length; i++) {
            File srcDir = getProject().resolveFile(list[i]);
            if (!srcDir.exists()) {
                throw new BuildException("srcdir \""
                                         + srcDir.getPath()
                                         + "\" does not exist!", getLocation());
            }

            DirectoryScanner ds = this.getDirectoryScanner(srcDir);
            String[] files = ds.getIncludedFiles();

            scanDir(srcDir, srcDir, files);
        }

        System.out.println(msg);
        System.out.println(src);
        for (File s : compileList) {
            System.out.println(s.getName());
        }

    }

    public void setMessage(String msg) {
        this.msg = msg;
    }
}
