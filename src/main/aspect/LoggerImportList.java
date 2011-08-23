aspect LoggerImportList {
    before(): call(* *(..)) && target(org.creativelabs.ImportList){
        String callName = thisJoinPointStaticPart.getSignature().getName();
        Object[] args = thisJoinPoint.getArgs();
        String[] argValues = new String[args.length];
        StringBuffer argString = new StringBuffer();
        for (int i = 0; i < args.length; ++i) {
            argValues[i] = "" + args[i];
            argString.append(args[i] + ", ");
        }

        System.out.println(callName + " " + argString.toString());
    }

    after() returning(Object x): call(* *(..)) && target(org.creativelabs.ImportList) {

        String callName = thisJoinPointStaticPart.getSignature().getName();

        System.out.println("Back from " + callName + " " + x);
    }

    after(org.creativelabs.ImportList c): this(c) && execution(*.new(..)){
        System.out.println("ImportList constructor " + c.className);
    }
}
