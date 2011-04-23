package org.creativelabs.report;

import org.creativelabs.Dependency;
import org.creativelabs.iig.InternalInstancesGraph;
import org.creativelabs.ssa.SsaFormAstRepresentation;

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
    public void setSsaFormRepresentations(String className, Set<SsaFormAstRepresentation> ssaFormRepresentations) {
        outData(ssaFormRepresentations, "detailedreport/" + className);
    }

    private void outData(Set<SsaFormAstRepresentation> ssaFormRepresentations, String fileName) {
        try {
            File file = new File(fileName + ".ssa");
            if (file.createNewFile()) {
                PrintWriter writer = new PrintWriter(file);
                printSsaFormRepresentations(ssaFormRepresentations, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printSsaFormRepresentations(Set<SsaFormAstRepresentation> ssaFormRepresentations, PrintWriter writer) {
        for (SsaFormAstRepresentation ssaFormRepresentation : ssaFormRepresentations) {
            writer.println(ssaFormRepresentation.getAst());
            writer.println();
        }
    }
}
