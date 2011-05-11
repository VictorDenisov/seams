import org.creativelabs.VariableList;
aspect LoggerClassProcessor {

    after(org.creativelabs.ClassProcessor c): this(c) && execution(*.new(..)){
        if (c.dependencyCounterBuilder != null) {
            VariableList varList = c.dependencyCounterBuilder.classFields;
            StringBuffer sb = new StringBuffer();
            for (String name : varList.getNames()) {
                sb.append(name + " -> " + varList.getFieldTypeAsClass(name) + " ");
            }
            System.out.println("After ClassProcessor constructor " + sb);
        } else {
            System.out.println("After ClassProcessor constructor");
        }
    }
}
