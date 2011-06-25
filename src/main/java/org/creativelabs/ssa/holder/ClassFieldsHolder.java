package org.creativelabs.ssa.holder;

import org.creativelabs.copy.Copyable;

import java.util.Collection;
import java.util.Set;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:07
 */
public interface ClassFieldsHolder extends Copyable {
    void addFieldName(String fieldName);
    void addFieldNames(Collection<String> fieldNames);

    Set<String> getFieldsNames();
    void setFieldsNames(Set<String> fieldsNames);

    boolean containsFieldName(String fieldName);

    int getCountOfFieldNames();


    void addCreated(String name);
    void addCreated(Collection<String> names);

    Set<String> getCreated();
    void setCreated(Set<String> names);

    boolean containsCreated(String name);

    int getCountOfCreated();
}
