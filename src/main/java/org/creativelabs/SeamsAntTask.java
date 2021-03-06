package org.creativelabs;

import java.io.*;
import java.util.*;
import java.net.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.util.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.taskdefs.MatchingTask;

import org.creativelabs.report.*;
import org.creativelabs.introspection.*;

public class SeamsAntTask extends MatchingTask {
    private String msg;
    private Path src;
    protected File[] compileList = new File[0];
    private File destDir;
    private Path classpath;
    private ReflectionAbstraction ra = null;
    private ClassLoader classLoader;
    private List<Report> reports = new ArrayList<Report>();
    private Set<String> supportedReports = new HashSet<String>();

    public SeamsAntTask() {
        supportedReports.add("deps-graph");
        supportedReports.add("deps-detail");
        supportedReports.add("int-inst-chart");
        supportedReports.add("errors");
    }

    public static class Report {

        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }

    public void addConfiguredReport(Report report) {
        reports.add(report);
    }

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

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
        String[] extensions = new String[] { "java" };

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
            m.setTo("*.deps");
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

    public URL[] getUrlsFromFileList() {
        String[] fileList = classpath.list();
        URL[] urls = new URL[fileList.length];
        try {
            for (int i = 0; i < fileList.length; ++i) {
                urls[i] = new File(fileList[i]).toURL();

                System.out.println(urls[i]);
            }
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }

        classLoader = new URLClassLoader(urls);
        return urls;
    }

    private void checkReports() throws BuildException {
        for (Report report : reports) {
            if (!supportedReports.contains(report.name)) {
                throw new BuildException("unknown report name : " + report.name);
            }
        }
    }

    public void execute() throws BuildException {
        checkReports();
        
        checkParameters();
        resetFileLists();

        URL[] urls = getUrlsFromFileList();

        ra = new HookReflectionAbstraction(new ReflectionAbstractionImpl(classLoader));
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

            scanDir(srcDir, new File("target"), files);
        }
        try {
            process();
        } catch (Exception e) {
            throw new BuildException(e);
        }

        System.out.println(src);
        for (File s : compileList) {
            System.out.println(s.getPath());
        }

    }

    protected void process() throws Exception {
        if (compileList.length > 0) {
            log("Processing " + compileList.length + " source file"
                + (compileList.length == 1 ? "" : "s")
                + (destDir != null ? " to " + destDir : ""));

            DataCollector dataCollector = new DataCollector();

            for (File file : compileList) {
                MainApp.processFileOrDirectory(file, dataCollector, ra);
            }
            for (Report report : reports) {
                //TODO Duplicating with constructor
                if (report.name.equals("deps-detail")) {
                    dataCollector.buildDetailedDependencyReport();
                }
                if (report.name.equals("int-inst-chart")) {
                    dataCollector.buildInternalInstancesByClassReport();
                }
                if (report.name.equals("errors")) {
                    dataCollector.buildNumberOfErrorsReport();
                }
            }
        }
    }

    public void setClasspath(Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        } else {
            this.classpath.append(classpath);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }
}
