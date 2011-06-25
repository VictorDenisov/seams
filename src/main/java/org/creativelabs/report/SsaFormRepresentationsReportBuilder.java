package org.creativelabs.report;

import org.creativelabs.ssa.SsaError;
import org.creativelabs.typefinder.Dependency;
import org.creativelabs.MainApp;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.representation.SsaFormRepresentation;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author azotcsit
 *         Date: 23.04.11
 *         Time: 18:24
 */
public class SsaFormRepresentationsReportBuilder implements ReportBuilder {

    @Override
    public void setDependencies(String className, Map<String, Collection<Dependency>> dependencies) {
    }

    @Override
    public void setInternalInstances(String className, Map<String, InternalInstancesGraph> instances) {
    }

    @Override
    public void setSsaFormRepresentations(String className, Set<SsaFormRepresentation> ssaFormRepresentations) {
        outData(ssaFormRepresentations, "detailedreport/ssa/" + className);
    }

    @Override
    public void setSsaErrors(String className, Set<SsaError> ssaErrors) {
    }

    private void outData(Set<SsaFormRepresentation> ssaFormRepresentations, String fileName) {
        try {
            File file = new File(fileName + ".ssa");
            if (file.createNewFile() || MainApp.NEED_TO_REWRITE_OLD_REPORT) {
                PrintWriter writer = new PrintWriter(file);
                printSsaFormRepresentations(ssaFormRepresentations, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSsaFormRepresentations(Set<SsaFormRepresentation> ssaFormRepresentations, PrintWriter writer) {
        for (SsaFormRepresentation ssaFormRepresentation : ssaFormRepresentations) {
            ssaFormRepresentation.removeRedundantInformation();
            writer.println(ssaFormRepresentation.getSsaFormStringRepresentation());
            writer.println();
        }

    }
}
