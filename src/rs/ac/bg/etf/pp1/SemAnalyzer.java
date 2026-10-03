package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

//Visitor interface - sadrzi sve visit metode za svaki cvor
//VisitorAdaptor implementira taj interface

public class SemAnalyzer extends VisitorAdaptor {

    private boolean errorDetected = false;

    Logger log = Logger.getLogger(getClass());

    private Obj currentProgram;
    private Struct currentType;

    private int constant;
    private Struct constantType;

    // jer je pri init ubacen u tabelu simbola
    private Struct boolType = Tab.find("bool").getType();

    private Obj currentMethod;
    private Obj mainMethod;

    private boolean returnHappend;
    private int loopCnt;

    private java.util.Stack<java.util.List<Struct>> actParsStack = new java.util.Stack<>();

    int nVars;

    private int tempCounter = 0; // brojac pomocnih promenljivih u kompajleru: $ia_i0, $ia_i1...

    public static java.util.Map<DesignatorList_findany, Obj[]> findAnyTemps = new java.util.HashMap<>();
    public static java.util.Map<DesignatorList_map, Obj[]> mapTemps = new java.util.HashMap<>(); // {counter, resultArr, sourceLenNotNeeded}

    // final
    public boolean changedElem = false;
    
    public void report_error(String message, SyntaxNode info) {
        errorDetected = true;

        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();

        if (line != 0) {
            msg.append(" na liniji ").append(line);
        }

        log.error(msg.toString());
    }

    public void report_info(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);

        int line = (info == null) ? 0 : info.getLine();

        if (line != 0) {
            msg.append(" na liniji ").append(line);
        }

        log.info(msg.toString());
    }

    public boolean passed() {
        return !errorDetected;
    }

    /* SEMANTIC PASS CODE */

    @Override
    public void visit(ProgramName programName) { // dodajes objektni cvor u trenutni scope
        currentProgram = Tab.insert(Obj.Prog, programName.getI1(), Tab.noType);

        Tab.openScope();
    }

    @Override
    public void visit(Program program) {
        nVars = Tab.currentScope().getnVars(); 	// broji promenljive (ne konstante i ne metode)

        Tab.chainLocalSymbols(currentProgram);	// moras da prelancas sve unutar preograma jer ces zatvoriti scope

        Tab.closeScope();						// zatvaras scope - ovde jer se na kraju poziva Program
        currentProgram = null;

        if (mainMethod == null || mainMethod.getLevel() > 0) {
            report_error("Program nema adekvatnu main metodu", program);
        }
    }
    
    /* CONST DECLARATIONS */

    @Override
    public void visit(ConDecl conDecl) {
        // ne moze: const int pi = 3, pii = 33;

        Obj conObj = Tab.find(conDecl.getI1());

        if (conObj != Tab.noObj) {
            report_error("Dvostruka definicija konstante: " + conDecl.getI1(), conDecl);
        } else {

            // ako se tipovi poklapaju (npr. const int a = 'c')
            if (constantType.assignableTo(currentType)) {
                conObj = Tab.insert(
                        Obj.Con,
                        conDecl.getI1(),
                        currentType
                );
                conObj.setAdr(constant); // za konstantu se vrednost cuva u adr
              
            } else {
                report_error("Neadekvatan tip pri dodeli konstanti: " + conDecl.getI1(), conDecl);
            }
        }
    }

    @Override
    public void visit(Type type) {
        Obj typeObj = Tab.find(type.getI1()); // npr. Tab.find("int") trazi se unutar universe scope (Obj.Type "int" u tebli simbola)- Tab.intType
        
        if (typeObj == Tab.noObj) {
            report_error("Nepostojeci tip podatka: " + type.getI1(), type);
            currentType = Tab.noType;
            
        } else if (typeObj.getKind() != Obj.Type) {
            report_error("Neadekvatan tip podatka: " + type.getI1(), type);
            currentType = Tab.noType;
        } else {
            currentType = typeObj.getType();
        }
        
        type.struct = currentType;
    }

    @Override
    public void visit(Constant_num constant_num) {
        constant = constant_num.getN1();
        constantType = Tab.intType;
    }

    @Override
    public void visit(Constant_char constant_char) {
        constant = constant_char.getC1();
        constantType = Tab.charType;
    }

    @Override
    public void visit(Constant_bool constant_bool) {
        // bool je 0 ili 1 pa moze u int constant
        constant = constant_bool.getB1();
        constantType = boolType;
    }

    /*  VAR DECLARATIONS */

    @Override
    public void visit(VarDecl_var varDecl_var) {
        Obj varObj;
        if (currentMethod == null) {
            varObj = Tab.find(varDecl_var.getI1());                    	
        } else { 															// proverava se globalni scope
            varObj = Tab.currentScope().findSymbol(varDecl_var.getI1());	// proverava se lokalni scope
        }
        if (varObj == null || varObj == Tab.noObj) {
            Tab.insert(
                    Obj.Var,
                    varDecl_var.getI1(),
                    currentType
            );
        } else {
            report_error("Dvostruka definicija promenljiva: " + varDecl_var.getI1(), varDecl_var);
        }
    }

    @Override
    public void visit(VarDecl_array varDecl_array) {
        Obj varObj;
        if (currentMethod == null) { // globalni scope
            varObj = Tab.find(varDecl_array.getI1());
        } else {
            varObj = Tab.currentScope().findSymbol(varDecl_array.getI1());
        }

        if (varObj == null || varObj == Tab.noObj) {
            Obj newArrObj = Tab.insert(Obj.Var, varDecl_array.getI1(), new Struct(Struct.Array, currentType));

            // final
            if (changedElem) {
                if (currentMethod != null) {
                    report_error("final se moze koristiti samo za globalne nizove: " + varDecl_array.getI1(), varDecl_array);
                } else {
                    newArrObj.setFpPos(2);   // markiraj kao final niz
                }
            }
        } else {
            report_error("Dvostruka definicija promenljiva: " + varDecl_array.getI1(), varDecl_array);
        }
    }
    
    // final
    @Override
    public void visit(VarDeclList VarDeclList) { // resetujes flag na kraju liste deklaracija
    	changedElem = false;
    }
    
    @Override
    public void visit(VarDeclPrefix_final VarDeclPrefix_final) {
    	if (currentMethod != null) {
    		report_error("final ne moze da se koristi unutar metode - samo za globalne promenljive", VarDeclPrefix_final);
    	}
    	
    	changedElem = true;
    }



    /* METHOD DECLARATIONS */

    // void main() {}
    // int m1() {}

    @Override
    public void visit(MethRetAndName_void methRetAndName_void) {
        currentMethod = Tab.insert(Obj.Meth, methRetAndName_void.getI1(), Tab.noType);
        methRetAndName_void.obj = currentMethod;

        Tab.openScope();

        if (methRetAndName_void.getI1().equalsIgnoreCase("main")) {
            mainMethod = currentMethod;
        }
    }

    @Override
    public void visit(MethRetAndName_type methRetAndName_type) {
        currentMethod = Tab.insert(Obj.Meth, methRetAndName_type.getI2(), currentType);
        methRetAndName_type.obj = currentMethod;

        Tab.openScope();
    }

    @Override
    public void visit(MethodDecl methodDecl) {
        Tab.chainLocalSymbols(currentMethod);
        Tab.closeScope();

        if (currentMethod.getType() != Tab.noType && !returnHappend) {
            report_error("Ne postoji iskaz return unutar metode: " + currentMethod.getName(), methodDecl);
        }

        currentMethod = null;
        returnHappend = false;
    }

    /* FORMAL PARAMETERS */

    // char m2(int fp1, char fp2[])

    @Override
    public void visit(FormPar_var formPar_var) {
        Obj varObj = null;

        if (currentMethod == null) {
            report_error("Semanticka greska. [FormPar_var]", formPar_var);
            return;
        }

        varObj = Tab.currentScope().findSymbol(formPar_var.getI2());

        if (varObj == null || varObj == Tab.noObj) {
            varObj = Tab.insert(Obj.Var, formPar_var.getI2(), currentType);
            varObj.setFpPos(1);
            currentMethod.setLevel(currentMethod.getLevel() + 1);
        } else {
            report_error("Dvostruka definicija formalnog parametra: " + formPar_var.getI2(), formPar_var);
        }
    }

    @Override
    public void visit(FormPar_array formPar_array) {
        Obj varObj = null;

        if (currentMethod == null) {
            report_error("Semanticka greska. [FormPar_array]", formPar_array);
            return;
        }

        varObj = Tab.currentScope().findSymbol(formPar_array.getI2());

        if (varObj == null || varObj == Tab.noObj) {
            varObj = Tab.insert(Obj.Var, formPar_array.getI2(), new Struct(Struct.Array, currentType));
            varObj.setFpPos(1);
            currentMethod.setLevel(currentMethod.getLevel() + 1);
        } else {
            report_error("Dvostruka definicija formalnog parametra: " + formPar_array.getI2(), formPar_array);
        }
    }

    /* CONTEXT CONDITIONS */

    // Designator


    @Override
    public void visit(DesignatorName designatorName) {
	    Obj obj = Tab.find(designatorName.getI1());
	        if (obj == Tab.noObj) {
	            report_error("Nepostojeci identifikator: " + designatorName.getI1(), designatorName);
	            designatorName.obj = Tab.noObj;
	        }
	    else {
	        designatorName.obj = obj;
	    }
    }
    
    @Override
    public void visit(Designator designator) {
        designator.obj = designator.getDesignatorList().obj;
    }

    @Override
    public void visit(DesignatorList_e designatorList_e) {
        SyntaxNode parent = designatorList_e.getParent();

        while (parent != null && !(parent instanceof Designator)) {
            parent = parent.getParent();
        }

        if (parent instanceof Designator) {
            Designator d = (Designator) parent;
            designatorList_e.obj = d.getDesignatorName().obj;
        } else {
            designatorList_e.obj = Tab.noObj;
        }
    }

    @Override
    public void visit(DesignatorList_ident designatorList_ident) {
        Obj parent = designatorList_ident.getDesignatorList().obj;

        if (parent == Tab.noObj) {
            designatorList_ident.obj = Tab.noObj;
            return;
        }

        String fieldName = designatorList_ident.getI2();
        Struct type = parent.getType();

        if (type.getKind() == Struct.Class) {
            Obj field = type.getMembersTable().searchKey(fieldName);

            if (field == null) {
                report_error("Nepostojece polje: " + fieldName, designatorList_ident);
                designatorList_ident.obj = Tab.noObj;
            } else {
                designatorList_ident.obj = field;
            }

        } else if (parent.getKind() == Obj.Type) {
            Obj field = null;

            for (Obj o : parent.getLocalSymbols()) {
                if (o.getName().equals(fieldName)) {
                    field = o;
                    break;
                }
            }

            if (field == null) {
                report_error("Nepostojeca konstanta enuma: " + fieldName, designatorList_ident);
                designatorList_ident.obj = Tab.noObj;
            } else {
                designatorList_ident.obj = field;
            }

        } else {
            report_error("Operator . moze samo nad klasom ili enumom", designatorList_ident);
            designatorList_ident.obj = Tab.noObj;
        }
    }   
    
    
    @Override
    public void visit(DesignatorList_length designatorList_length) {
        Obj parent = designatorList_length.getDesignatorList().obj;

        if (parent == Tab.noObj) {
            designatorList_length.obj = Tab.noObj;
            return;
        }

        if (parent.getType().getKind() != Struct.Array) {
            report_error(".length pristup na ne-nizovnom tipu.", designatorList_length);
            designatorList_length.obj = Tab.noObj;
        } else {
            designatorList_length.obj = new Obj(Obj.Var, "length", Tab.intType);
        }
    }

    @Override
    public void visit(DesignatorList_expr designatorList_expr) { // parent je DesignatorList
        Obj parent = designatorList_expr.getDesignatorList().obj;

        if (parent == Tab.noObj) {
            designatorList_expr.obj = Tab.noObj;
            return;
        }

        if (parent.getType().getKind() != Struct.Array) {
            report_error("Indeksiranje ne-nizovnog tipa.", designatorList_expr);
            designatorList_expr.obj = Tab.noObj;
            return;
        }

        if (!designatorList_expr.getExpr().struct.equals(Tab.intType)) {
            report_error("Indeks niza nije tipa int.", designatorList_expr);
            designatorList_expr.obj = Tab.noObj;
            return;
        }

        designatorList_expr.obj = new Obj(Obj.Elem, "", parent.getType().getElemType());

        report_info("Pristup elementu niza: " + parent.getName(), designatorList_expr);
    }
    

    // Map
  	@Override
 	public void visit(MapBegin mapBegin) {
 	    DesignatorList_map parentNode = (DesignatorList_map) mapBegin.getParent();
 	    Obj arr = parentNode.getDesignatorList().obj;

 	    if (arr == Tab.noObj || arr == null || arr.getType().getKind() != Struct.Array) {
 	        report_error("map moze biti samo nad nizom.", mapBegin);
 	        return;
 	    }

 	    Struct elemType = arr.getType().getElemType();

 	    Obj identObj = Tab.find(parentNode.getI2()); // "x" iz x => expr — validno OVDE, scope je jos otvoren

 	    if (identObj == Tab.noObj) {
 	        report_error("Nepostojeci identifikator u map izrazu: " + parentNode.getI2(), mapBegin);
 	        return;
 	    }

 	    Obj counter   = Tab.insert(Obj.Var, "$map_i" + tempCounter, Tab.intType);							// brojac za petlju i 
 	    Obj resultArr = Tab.insert(Obj.Var, "$map_r" + tempCounter, new Struct(Struct.Array, elemType));	// novi arr koji se vraca
 	    Obj valueTemp = Tab.insert(Obj.Var, "$map_v" + tempCounter, elemType);								// privremeno cuva rezultat jedne iteracije npr x * 2
 	    tempCounter++;

 	    mapTemps.put(parentNode, new Obj[]{counter, resultArr, identObj, valueTemp});
 	}
    
    @Override
    public void visit(DesignatorList_map designatorList_map) {
        Obj parent = designatorList_map.getDesignatorList().obj;

        if (parent == Tab.noObj || parent == null) {
            designatorList_map.obj = Tab.noObj;
            return;
        }

        if (parent.getType().getKind() != Struct.Array) {
            report_error("map moze biti samo nad nizom.", designatorList_map);
            designatorList_map.obj = Tab.noObj;
            return;
        }

        Struct elemType = parent.getType().getElemType();
        Struct boolType = Tab.find("bool").getType();

        if (elemType != Tab.intType &&
                elemType != Tab.charType &&
                elemType != boolType) {

            report_error("map moze biti samo nad nizom ugradjenog tipa.", designatorList_map);
            designatorList_map.obj = Tab.noObj;
            return;
        }

        Obj identObj = Tab.find(designatorList_map.getI2());

        if (identObj == Tab.noObj) {
            report_error("Nepostojeci identifikator u map izrazu: "+ designatorList_map.getI2(), designatorList_map);
            designatorList_map.obj = Tab.noObj;
            return;
        }

        if ((identObj.getKind() != Obj.Var &&
                identObj.getKind() != Obj.Fld) ||
                !identObj.getType().equals(elemType)) {

            report_error("Ident u map izrazu mora biti promenljiva istog tipa kao elementi niza.", designatorList_map);
            designatorList_map.obj = Tab.noObj;
            return;
        }

        Struct exprType = designatorList_map.getExpr().struct;

        if (!exprType.equals(elemType)) {
            report_error("Telo map izraza mora biti istog tipa kao elementi niza.", designatorList_map);
            designatorList_map.obj = Tab.noObj;
            return;
        }

        designatorList_map.obj = new Obj(Obj.Var, "", new Struct(Struct.Array, elemType)); // ceo niz je tipa int[]
    }
    

    // FindAny
    @Override
	public void visit(FindAnyBegin findAnyBegin) {

	}
    
    @Override
    public void visit(DesignatorList_findany designatorList_findany) {
        Obj parent = designatorList_findany.getDesignatorList().obj;

        if (parent == Tab.noObj || parent == null) {
            designatorList_findany.obj = Tab.noObj;
            return;
        }

        if (parent.getType().getKind() != Struct.Array) {
            report_error("findAny moze biti samo nad nizom.", designatorList_findany);
            designatorList_findany.obj = Tab.noObj;
            return;
        }

        Struct elemType = parent.getType().getElemType();

        if (elemType != Tab.intType &&
                elemType != Tab.charType &&
                elemType != boolType) {

            report_error(
                    "findAny moze biti samo nad nizom ugradjenog tipa.",
                    designatorList_findany
            );
            designatorList_findany.obj = Tab.noObj;
            return;
        }

        if (!designatorList_findany.getExpr().struct.assignableTo(elemType)) {
            report_error(
                    "Tip izraza u findAny mora odgovarati tipu elemenata niza.",
                    designatorList_findany
            );
        }

        // Rezervisu se privremene promenljive u tabeli simbola 
        Obj counter = Tab.insert(Obj.Var, "$fa_i" + tempCounter, Tab.intType); 	// brojac kroz niz
        Obj target = Tab.insert(Obj.Var, "$fa_t" + tempCounter, elemType);		// element koji se trazi

        tempCounter++;

        findAnyTemps.put(designatorList_findany, new Obj[]{counter, target});

        designatorList_findany.obj = new Obj(Obj.Var, "bool", boolType);
    }
    
    
    // Contains
    
    public static java.util.Map<DesignatorList_contains, Obj[]> containsTemps = new java.util.HashMap<>();

    @Override
	public void visit(ContainsBegin containsBegin) {

	}
    
    @Override
    public void visit(DesignatorList_contains designatorList_contains) {
        Obj parent = designatorList_contains.getDesignatorList().obj;

        if (parent == Tab.noObj || parent == null) {
        	designatorList_contains.obj = Tab.noObj;
            return;
        }

        if (parent.getType().getKind() != Struct.Array) {
            report_error("contains moze biti samo nad nizom.", designatorList_contains);
            designatorList_contains.obj = Tab.noObj;
            return;
        }

        Struct elemType = parent.getType().getElemType();

        if (elemType != Tab.intType &&
                elemType != Tab.charType &&
                elemType != boolType) {

            report_error(
                    "contains moze biti samo nad nizom ugradjenog tipa.",
                    designatorList_contains
            );
            designatorList_contains.obj = Tab.noObj;
            return;
        }

        if (!designatorList_contains.getExpr().struct.assignableTo(elemType)) {
            report_error(
                    "Tip izraza u contains mora odgovarati tipu elemenata niza.",
                    designatorList_contains
            );
        }

        // Rezervisu se privremene promenljive u tabeli simbola 
        Obj counter = Tab.insert(Obj.Var, "$co_i" + tempCounter, Tab.intType); 	// brojac kroz niz
        Obj target = Tab.insert(Obj.Var, "$co_t" + tempCounter, elemType);		// element koji se trazi

        tempCounter++;

        containsTemps.put(designatorList_contains, new Obj[]{counter, target});

        designatorList_contains.obj = new Obj(Obj.Var, "bool", boolType);
    }
    
    // Filter
    public static java.util.Map<DesignatorList_filter, Obj[]> filterTemps = new java.util.HashMap<>();

   	@Override
  	public void visit(FilterBegin filterBegin) {
   		DesignatorList_filter parentNode = (DesignatorList_filter) filterBegin.getParent();
  	    Obj arr = parentNode.getDesignatorList().obj;

  	    if (arr == Tab.noObj || arr == null || arr.getType().getKind() != Struct.Array) {
  	        report_error("filter moze biti samo nad nizom.", filterBegin);
  	        return;
  	    }

  	    Struct elemType = arr.getType().getElemType();

  	    Obj identObj = Tab.insert(Obj.Var, parentNode.getI2(), elemType); // x => x + 5 - x je u tabeli simbola vec inicijalizovano 

  	    Obj readIdx   = Tab.insert(Obj.Var, "$filter_i" + tempCounter, Tab.intType);							// brojac za petlju i 
  	    Obj writeIdx   = Tab.insert(Obj.Var, "$filter_i" + tempCounter, Tab.intType);	
  	    Obj resultArr = Tab.insert(Obj.Var, "$filter_r" + tempCounter, new Struct(Struct.Array, elemType));	// novi arr koji se vraca
  	    tempCounter++;

  	    filterTemps.put(parentNode, new Obj[]{readIdx, writeIdx, resultArr, identObj});
  	}
     
     @Override
     public void visit(DesignatorList_filter designatorList_filter) {
         Obj parent = designatorList_filter.getDesignatorList().obj;

         if (parent == Tab.noObj || parent == null) {
        	 designatorList_filter.obj = Tab.noObj;
             return;
         }

         if (parent.getType().getKind() != Struct.Array) {
             report_error("filter moze biti samo nad nizom.", designatorList_filter);
             designatorList_filter.obj = Tab.noObj;
             return;
         }

         Struct elemType = parent.getType().getElemType();
         Struct boolType = Tab.find("bool").getType();

         if (elemType != Tab.intType &&
                 elemType != Tab.charType &&
                 elemType != boolType) {

             report_error("filter moze biti samo nad nizom ugradjenog tipa.", designatorList_filter);
             designatorList_filter.obj = Tab.noObj;
             return;
         }

         Obj identObj = Tab.find(designatorList_filter.getI2());

         if (identObj == Tab.noObj) {
             report_error("Nepostojeci identifikator u filter izrazu: "+ designatorList_filter.getI2(), designatorList_filter);
             designatorList_filter.obj = Tab.noObj;
             return;
         }

         if ((identObj.getKind() != Obj.Var &&
                 identObj.getKind() != Obj.Fld) ||
                 !identObj.getType().equals(elemType)) {

             report_error("Ident u filter izrazu mora biti promenljiva istog tipa kao elementi niza.", designatorList_filter);
             designatorList_filter.obj = Tab.noObj;
             return;
         }


         designatorList_filter.obj = new Obj(Obj.Var, "", new Struct(Struct.Array, elemType)); // ceo niz je tipa int[]
     }

    /* FACTOR CONDITIONS */

    // FactorSub

    @Override
    public void visit(FactorSub_num factorSub_num) {
        factorSub_num.struct = Tab.intType;
    }

    @Override
    public void visit(FactorSub_c factorSub_c) {
        factorSub_c.struct = Tab.charType;
    }

    @Override
    public void visit(FactorSub_b factorSub_b) {
        factorSub_b.struct = boolType;
    }

    @Override
    public void visit(FactorSub_var factorSub_var) {
        Obj obj = factorSub_var.getDesignator().obj;

        if (obj == null || obj == Tab.noObj) {
            factorSub_var.struct = Tab.noType;
            return;
        }
     
        factorSub_var.struct = obj.getType();
    }

	@Override
	public void visit(FactorSub_meth factorSub_meth) {
	    Obj meth = factorSub_meth.getDesignator().obj;
	    if (meth.getKind() != Obj.Meth) {
	        report_error("Poziv neadekvatne metode: " + meth.getName(), factorSub_meth);
	        factorSub_meth.struct = Tab.noType;
	        if (!actParsStack.isEmpty()) actParsStack.pop();
	        return;
	    }
	    if (!actParsStack.isEmpty()) {
	        java.util.List<Struct> actPars = actParsStack.pop();
	        int numFp = meth.getLevel();
	
	        if (actPars.size() != numFp) {
	            report_error("Pogresan broj argumenata za metodu: " + meth.getName(), factorSub_meth);
	        } else {
	            int idx = 0;
	            for (Obj o : meth.getLocalSymbols()) {
	                if (idx >= numFp) break;
	                if (!actPars.get(idx).assignableTo(o.getType()))
	                    report_error("Tip " + (idx+1) + ". argumenta nije kompatibilan.", factorSub_meth);
	                idx++;
	            }
	        }
	    }
	    factorSub_meth.struct = meth.getType();
	}
	
	@Override
	public void visit(FactorSub_new_array factorSub_new_array) {
	    Struct elemType = factorSub_new_array.getType().struct;   // umesto currentType
	    if (!factorSub_new_array.getExpr().struct.equals(Tab.intType)) {
	        report_error("Velicina niza nije int tipa.", factorSub_new_array);
	        factorSub_new_array.struct = Tab.noType;
	    } else {
	        factorSub_new_array.struct = new Struct(Struct.Array, elemType);
	    }
	}
	
	
	@Override
	public void visit(FactorSub_new_type factorSub_new_type) {
	    if (currentType.getKind() != Struct.Class) {
	        report_error("new moze biti samo nad klasom.", factorSub_new_type);
	        factorSub_new_type.struct = Tab.noType;
	    } else {
	        factorSub_new_type.struct = currentType;
	    }
	}
		
	@Override
	public void visit(FactorSub_expr factorSub_expr) {
		factorSub_expr.struct = factorSub_expr.getExpr().struct;
	}
	
	
	public static java.util.HashMap<FactorSub_maxArr, Obj[]> maxArrTemps = new java.util.HashMap<>();

	@Override
	public void visit(FactorSub_maxArr factorSub_maxArr) {
		Obj obj = factorSub_maxArr.getDesignator().obj;

        if (obj.getType().getKind() != Struct.Array) {
        	report_error("# moze da se poziva samo nad nizom", factorSub_maxArr);
        	factorSub_maxArr.struct = Tab.noType;
            return; 
        }
        
        Struct elemType = obj.getType().getElemType(); // tip elementa niza (npr. int)
        factorSub_maxArr.struct = elemType;            // jer #arr vraca int, a ne niz

        Obj arrTemp = Tab.insert(Obj.Var, "$maxarr_arr" + tempCounter, obj.getType()); // kopija arrayref-a
        Obj counter   = Tab.insert(Obj.Var, "$maxarr_i"   + tempCounter, Tab.intType);   // brojac petlje
        Obj maxTemp = Tab.insert(Obj.Var, "$maxarr_max" + tempCounter, elemType);      // tekuci maksimum
        tempCounter++;

        maxArrTemps.put(factorSub_maxArr, new Obj[]{arrTemp, counter, maxTemp});
    }
	
	//Factor
	@Override
	public void visit(Factor factor) {
		if(factor.getUnary() instanceof Unary_m) { //dohvata unarnog sina - da li se desio minus?
			if(factor.getFactorSub().struct.equals(Tab.intType)) 
				factor.struct = Tab.intType; // javio se minus i sin JESTE integer
			else {
				report_error("Negacija ne int vrednosti", factor);
				factor.struct = Tab.noType; // noType (greska) se prosledjuje ocu
			}
		}else
			factor.struct = factor.getFactorSub().struct; // prosledjujemo nagore samo - nema minusa pa se nista ne radi
	}
	
	//Condition

	@Override
	public void visit(CondFact_expr condFact_expr) {
		if(!condFact_expr.getExprBasic().struct.equals(boolType)) {
			report_error("Logicki operand nije tipa bool.", condFact_expr);
			condFact_expr.struct = Tab.noType;
		}
		else {
			condFact_expr.struct = boolType;
		}
	}

	@Override
	public void visit(CondFact_rel condFact_rel) {
		Struct left = condFact_rel.getExprBasic().struct;
		Struct right = condFact_rel.getExprBasic1().struct;
		if(left.compatibleWith(right)) {
			if(left.isRefType() || right.isRefType()) {
				if(condFact_rel.getRelop() instanceof Relop_ee || condFact_rel.getRelop() instanceof Relop_ne)
					condFact_rel.struct = boolType;
				else {
					report_error("Poredjenje ref tipova sa ne adekvatnim relacionim operatorom.", condFact_rel);
					condFact_rel.struct = Tab.noType;
				}
			}else
				condFact_rel.struct = boolType;
		}else {
			report_error("Logicki operandi nisu kompatibilni.", condFact_rel);
			condFact_rel.struct = Tab.noType;
		}
	}

	@Override
	public void visit(CondFactList_cf condFactList_cf) {
		condFactList_cf.struct = condFactList_cf.getCondFact().struct;
	}

	@Override
	public void visit(CondFactList_and condFactList_and) {
		Struct left = condFactList_and.getCondFactList().struct;
		Struct right = condFactList_and.getCondFact().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condFactList_and.struct = boolType;
		else {
			report_error("And operacija ne bool vrednosti.", condFactList_and);
			condFactList_and.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		condTerm.struct = condTerm.getCondFactList().struct;
	}
	
	@Override
	public void visit(CondTermList_ct condTermList_ct) {
		condTermList_ct.struct = condTermList_ct.getCondTerm().struct;
	}
	
	@Override
	public void visit(CondTermList_or condTermList_or) {
		Struct left = condTermList_or.getCondTermList().struct;
		Struct right = condTermList_or.getCondTerm().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condTermList_or.struct = boolType;
		else {
			report_error("Or operacija ne bool vrednosti.", condTermList_or);
			condTermList_or.struct = Tab.noType;
		}
	}


	@Override
	public void visit(Condition condition) {
		condition.struct = condition.getCondTermList().struct;
		if(!condition.struct.equals(boolType))
			report_error("Uslov nije tipa bool.", condition);
	}
	
	// Term
	
	@Override
	public void visit(Term term) {
	    term.struct = term.getTermList().struct;
	}
	
	@Override
	public void visit(AddopTermList_term addopTermList_term) {
	    addopTermList_term.struct = addopTermList_term.getTerm().struct;
	}
	
	@Override
	public void visit(AddopTermList_list addopTermList_list) {
	    Struct left = addopTermList_list.getAddopTermList().struct;
	    Struct right = addopTermList_list.getTerm().struct;
	    if (!left.equals(Tab.intType) || !right.equals(Tab.intType)) {
	        report_error("Operandi sabiranja/oduzimanja moraju biti tipa int.", addopTermList_list);
	        addopTermList_list.struct = Tab.noType;
	    } else {
	        addopTermList_list.struct = Tab.intType;
	    }
	}
	
	@Override
	public void visit(TermList_factor termList_factor) {
	    termList_factor.struct = termList_factor.getFactor().struct;
	}
	
	@Override
	
	public void visit(TermList_list termList_list) { 
	    Struct left = termList_list.getTermList().struct; // poziva se prvo TermList_factor pa onda ovo 
	    Struct right = termList_list.getFactor().struct;
	    if (!left.equals(Tab.intType) || !right.equals(Tab.intType)) { // moraju leva i desna strana da budu integer 
	        report_error("Operandi mnozenja/deljenja moraju biti tipa int.", termList_list);
	        termList_list.struct = Tab.noType;
	    } else {
	        termList_list.struct = Tab.intType;
	    }
	}
	
	// Expr
	@Override
	public void visit(ExprBasic exprBasic) {
	    exprBasic.struct = exprBasic.getAddopTermList().struct;
	}
	
	@Override
	public void visit(Expr_bas expr_bas) {
	    expr_bas.struct = expr_bas.getExprBasic().struct;
	}
	
	@Override
	public void visit(Expr_con expr_con) {
	    // Condition ? Expr : Expr
	    Struct second = expr_con.getExpr().struct;
	    Struct third = expr_con.getExpr1().struct;
	    if (!second.equals(third)) {
	        report_error("Drugi i treci izraz moraju biti istog tipa.", expr_con);
	        expr_con.struct = Tab.noType;
	    } else {
	        expr_con.struct = second;
	    }
	}
	
	// Ukljuci da bi continue i break mogli da se pozivaju unutar while i dowhile
	// DO WHILE
//	@Override
//	public void visit(DoNonterm doNonterm) {
//	    loopCnt++;
//	}
//
//	@Override
//	public void visit(Statement_do statement_do) {
//	    loopCnt--;
//	}
//	
//	// WHILE
//	@Override
//	public void visit(WhileBegin whileBegin) {
//	    loopCnt++;
//	}
//	
//	
//	@Override
//	public void visit(Statement_while statement_while) {
//	    loopCnt--;
//	}
	
	// FOR - inkrement pre tela
	@Override
	public void visit(ForBegin forBegin) {
	    loopCnt++;
	}
	
	
	@Override
	public void visit(Statement_for statement_for) {
	    loopCnt--;
	}
	
	/* FOREACH */
	
	public static java.util.Map<Statement_foreach, Obj[]> foreachTemps = new java.util.HashMap<>();
	
	@Override
	public void visit(ForeachBegin foreachBegin) {
	    loopCnt++;
	}
	
	@Override
	public void visit(Statement_foreach statement_foreach) {
		loopCnt--;
		
		Struct type0 = statement_foreach.getExpr().struct;
		Struct type1 = statement_foreach.getExpr1().struct;

		if (!type0.equals(Tab.intType) || !type1.equals(Tab.intType)) {
			report_error("expr unutar range moraju biti tipa int", statement_foreach);
		}
		
		String varName = statement_foreach.getI1();
	    Obj counter = Tab.find(varName); // dohvatas i iz tabele simbola

	    if (counter == Tab.noObj) {
	        report_error("Promenljiva '" + varName + "' nije deklarisana", statement_foreach);
	    } else if (counter.getKind() != Obj.Var || !counter.getType().equals(Tab.intType)) {
	        report_error("Promenljiva '" + varName + "' mora biti tipa int", statement_foreach);
	    }
				
 	    Obj endRange	= Tab.insert(Obj.Var, "$foreach_e" + tempCounter, Tab.intType); // privremeno cuva rezultat jedne iteracije npr x * 2
 	    Obj startRange 	= Tab.insert(Obj.Var, "$foreach_s" + tempCounter, Tab.intType);
 	    tempCounter++;

 	    foreachTemps.put(statement_foreach, new Obj[]{counter, startRange, endRange});
	}
	
	// BREAK
	@Override
	public void visit(Statement_break statement_break) {
	if (loopCnt == 0) {
	        report_error("Break se moze koristiti samo unutar for petlje", statement_break);
	    }
	/* if (loopCnt == 0 && switchCnt == 0) {
	        report_error("Break se moze koristiti samo unutar for petlje ili switch-a.", statement_break);
	    } */
	}
	
	// CONTINUE
	@Override
	public void visit(Statement_continue statement_continue) {
	    if (loopCnt == 0) {
	        report_error("Continue se moze koristiti samo unutar for petlje.", statement_continue);
	    }
	}
	
	// RETURN
	@Override
	public void visit(Statement_return1 statement_return1) {
	    if (currentMethod == null) {
	        report_error("Return iskaz se ne sme nalaziti van tela metode.", statement_return1);
	        return;
	    }
	    if (currentMethod.getType() != Tab.noType) {
	        report_error("Return bez vrednosti u ne-void metodi: " + currentMethod.getName(), statement_return1);
	    }
	    returnHappend = true;
	}
	
	@Override
	public void visit(Statement_return2 statement_return2) {
	    if (currentMethod == null) {
	        report_error("Return iskaz se ne sme nalaziti van tela metode.", statement_return2);
	        return;
	    }
	    if (currentMethod.getType() == Tab.noType) {
	        report_error("Return sa vrednoscu u void metodi: " + currentMethod.getName(), statement_return2);
	        return;
	    }
	    if (!statement_return2.getExpr().struct.assignableTo(currentMethod.getType())) {
	        report_error("Tip return izraza nije kompatibilan sa povratnim tipom metode.", statement_return2);
	    }
	    returnHappend = true;
	}
	
	// READ
	@Override
	public void visit(Statement_read statement_read) {
	    Obj des = statement_read.getDesignator().obj;
	    int kind = des.getKind();
	    if (kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
	        report_error("Read argument mora biti promenljiva, element niza ili polje.", statement_read);
	        return;
	    }
	    Struct type = des.getType();
	    if (!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType)) {
	        report_error("Read argument mora biti tipa int, char ili bool.", statement_read);
	    }
	}
	
	// PRINT
	public static java.util.Map<Statement_print1, Obj[]> printArrTemps = new java.util.HashMap<>();
	@Override
	public void visit(Statement_print1 statement_print1) {
	    Struct type = statement_print1.getExpr().struct;
	    if (!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType) && type.getKind() != Struct.Array) {
	        report_error("Print argument mora biti tipa int, char ili bool ili array.", statement_print1);
	    }
	    
	    Obj arrTemp = Tab.insert(Obj.Var, "$parr" + tempCounter, type);
        Obj idxTemp = Tab.insert(Obj.Var, "$pidx" + tempCounter, Tab.intType);
        tempCounter++;
        printArrTemps.put(statement_print1, new Obj[]{arrTemp, idxTemp});
        return;
	}
	
	@Override
	public void visit(Statement_print2 statement_print2) {
	    Struct type = statement_print2.getExpr().struct;
	    if (!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType) && type.getKind() != Struct.Array) {
	        report_error("Print argument mora biti tipa int, char ili bool ili array.", statement_print2);
	    }
	}
	
	// Designator statements
	
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
	    int kind = designatorStatement_assign.getDesignator().obj.getKind();
	    if (kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
	        report_error("Leva strana dodele mora biti promenljiva, element niza ili polje.", designatorStatement_assign);
	        return;
	    }
	    if (!designatorStatement_assign.getExpr().struct.assignableTo(designatorStatement_assign.getDesignator().obj.getType())) {
	        report_error("Nekompatibilni tipovi pri dodeli.", designatorStatement_assign);
	    }
	}
	
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
	    int kind = designatorStatement_inc.getDesignator().obj.getKind();
	    if (kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
	        report_error("Inkrement mora biti nad promenljivom, elementom niza ili poljem.", designatorStatement_inc);
	        return;
	    }
	    if (!designatorStatement_inc.getDesignator().obj.getType().equals(Tab.intType)) {
	        report_error("Inkrement mora biti tipa int.", designatorStatement_inc);
	    }
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
	    int kind = designatorStatement_dec.getDesignator().obj.getKind();
	    if (kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
	        report_error("Dekrement mora biti nad promenljivom, elementom niza ili poljem.", designatorStatement_dec);
	        return;
	    }
	    if (!designatorStatement_dec.getDesignator().obj.getType().equals(Tab.intType)) {
	        report_error("Dekrement mora biti tipa int.", designatorStatement_dec);
	    }
	}
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) {
	    Obj meth = designatorStatement_meth.getDesignator().obj;
	    if (meth.getKind() != Obj.Meth) {
	        report_error("Poziv mora biti nad metodom ili globalnom funkcijom.", designatorStatement_meth);
	        if (!actParsStack.isEmpty()) actParsStack.pop();
	        return;
	    }
	    if (!actParsStack.isEmpty()) {
	        java.util.List<Struct> actPars = actParsStack.pop();
	        int numFp = meth.getLevel();
	        if (actPars.size() != numFp) {
	            report_error("Pogresan broj argumenata za metodu: " + meth.getName(), designatorStatement_meth);
	        } else {
	            int idx = 0;
	            for (Obj o : meth.getLocalSymbols()) {
	                if (idx >= numFp) break;
	                if (!actPars.get(idx).assignableTo(o.getType()))
	                    report_error("Tip " + (idx+1) + ". argumenta nije kompatibilan.", designatorStatement_meth);
	                idx++;
	            }
	        }
	    }
	}
	
	
	@Override
	public void visit(DesignatorStatement_error designatorStatement_error) {
	    // oporavak od greske - ne radi se nista
	}
	
	
	// ActPar
	
	@Override
	public void visit(ActParBegin actParBegin) {
	    actParsStack.push(new java.util.ArrayList<>());
	}
	
	@Override
	public void visit(ActPar actPar) {
	    if (!actParsStack.isEmpty())
	        actParsStack.peek().add(actPar.getExpr().struct);
	}

}