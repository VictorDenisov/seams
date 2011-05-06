aspect Logger {
    before(): call(* *(..)) && !call(* addToBoxing(..)) && target(org.creativelabs.introspection.ReflectionAbstractionImpl){
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

    after() returning(Object x): call(* *(..)) && !call(* addToBoxing(..)) && target(org.creativelabs.introspection.ReflectionAbstractionImpl) {

        String callName = thisJoinPointStaticPart.getSignature().getName();

        System.out.println("Back from " + callName + " " + x);
    }
}

