package org.creativelabs.ssa.holder;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author azotcsit
 *         Date: 20.05.11
 *         Time: 22:11
 */
public class SimpleClassFieldsHolder implements ClassFieldsHolder {

    private Set<String> fieldsNames;

    public SimpleClassFieldsHolder() {
        fieldsNames = new TreeSet<String>();
    }

    public SimpleClassFieldsHolder(Set<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
    }

    @Override
    public void addFieldName(String fieldName) {
        fieldsNames.add(fieldName);
    }

    @Override
    public void addFieldNames(Collection<String> fieldNames) {
        fieldsNames.addAll(fieldNames);
    }

    @Override
    public Set<String> getFieldsNames() {
        return fieldsNames;
    }

    @Override
    public void setFieldsNames(Set<String> fieldsNames) {
        this.fieldsNames = fieldsNames;
    }

    @Override
    public boolean containsFieldName(String fieldName) {
        return fieldsNames.contains(fieldName);
    }

    @Override
    public int getCountOfFieldNames() {
        return fieldsNames.size();
    }

    @Override
    public SimpleClassFieldsHolder copy() {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        holder.addFieldNames(fieldsNames);
        return holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleClassFieldsHolder holder = (SimpleClassFieldsHolder) o;

        if (fieldsNames != null ? !fieldsNames.equals(holder.fieldsNames) : holder.fieldsNames != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return fieldsNames != null ? fieldsNames.hashCode() : 0;
    }
}
