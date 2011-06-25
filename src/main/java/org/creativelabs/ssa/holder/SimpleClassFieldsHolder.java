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
    private Set<String> created;

    public SimpleClassFieldsHolder() {
        fieldsNames = new TreeSet<String>();
        created = new TreeSet<String>();
    }

    public SimpleClassFieldsHolder(Set<String> fieldsNames, Set<String> created) {
        this.fieldsNames = fieldsNames;
        this.created = created;
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
    public void addCreated(String name) {
        created.add(name);
    }

    @Override
    public void addCreated(Collection<String> names) {
        created.addAll(names);
    }

    @Override
    public Set<String> getCreated() {
        return created;
    }

    @Override
    public void setCreated(Set<String> names) {
        created = names;
    }

    @Override
    public boolean containsCreated(String name) {
        return created.contains(name);
    }

    @Override
    public int getCountOfCreated() {
        return created.size();
    }

    @Override
    public SimpleClassFieldsHolder copy() {
        SimpleClassFieldsHolder holder = new SimpleClassFieldsHolder();
        holder.addFieldNames(fieldsNames);
        holder.addCreated(created);
        return holder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleClassFieldsHolder holder = (SimpleClassFieldsHolder) o;

        if (created != null ? !created.equals(holder.created) : holder.created != null) return false;
        if (fieldsNames != null ? !fieldsNames.equals(holder.fieldsNames) : holder.fieldsNames != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldsNames != null ? fieldsNames.hashCode() : 0;
        result = 31 * result + (created != null ? created.hashCode() : 0);
        return result;
    }
}
