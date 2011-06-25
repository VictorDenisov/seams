package org.creativelabs.report;

import org.creativelabs.MainApp;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.SsaError;
import org.creativelabs.ssa.representation.SsaFormRepresentation;
import org.creativelabs.typefinder.Dependency;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author azotcsit
 *         Date: 25.06.11
 *         Time: 17:18
 */
public class ErrorIigReportBuilder implements ReportBuilder {
    @Override
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
    }

    @Override
    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
    }

    @Override
    public void setSsaErrors(String className, Set<SsaError> ssaErrors) {
        if (!ssaErrors.isEmpty()) {
            outData(ssaErrors, "detailedreport/errors/" + className);
        }
    }

    private void outData(Set<SsaError> ssaErrors, String fileName) {
        try {
            File file = new File(fileName + ".error");
            if (file.createNewFile() || MainApp.NEED_TO_REWRITE_OLD_REPORT) {
                PrintWriter writer = new PrintWriter(file);
                printSsaErrors(ssaErrors, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSsaErrors(Set<SsaError> ssaErrors, PrintWriter writer) {
        for (SsaError error : ssaErrors) {
            error.printError(writer);
        }
    }
}
