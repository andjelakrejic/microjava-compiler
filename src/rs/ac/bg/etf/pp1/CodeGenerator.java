package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;


// nakon svakog izraza (posle ;) - expression stack mora biti prazan!!! 
// Code.put(0) - u compile timu ubacuje 1B 0 u Code memoriju
// Code.loadConst(0) - u compile timu generise instr const_0 koja u runtimu na ExprStack stavlja 0 velicine 4B

public class CodeGenerator extends VisitorAdaptor {

	
	private int mainPc;
	private Map<String, Integer> labels = new HashMap<>();
	private Map<String, List<Integer>> patchAddrs = new HashMap<>();
	
	public int getmainPc() {
		return this.mainPc;
	}
	
	/* METHOD DECLARATIONS */
	
	@Override
	public void visit(MethRetAndName_void methRetAndName_void) {
		methRetAndName_void.obj.setAdr(Code.pc); // pamti se adresa pocetka funkcije pre prve instr
		if (methRetAndName_void.getI1().equalsIgnoreCase("main"))
			this.mainPc = Code.pc;
		
		Code.put(Code.enter);
		Code.put(methRetAndName_void.obj.getLevel()); // b1 - broj formalnih param (iz level metoda)
		Code.put(methRetAndName_void.obj.getLocalSymbols().size()); //b2 - broj formalnih + lokalnih param
	}
	
	@Override
	public void visit(MethRetAndName_type methRetAndName_type) {
		methRetAndName_type.obj.setAdr(Code.pc);
		
		Code.put(Code.enter);
		Code.put(methRetAndName_type.obj.getLevel()); // b1
		Code.put(methRetAndName_type.obj.getLocalSymbols().size()); // b2
	}
	
	@Override
	public void visit(MethodDecl methodDecl) { // ovde se metoda zavrsi
		Code.put(Code.exit);
		Code.put(Code.return_);
	}  
	
	/* STATEMENTS */
	
	// Designator Statements
	
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) { // upis/dodela vrednosti
		Code.store(designatorStatement_assign.getDesignator().obj); 
	}
	
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) { // func() - skacemo u metodu
		Obj methObj = designatorStatement_meth.getDesignator().obj;
		
		if (methObj.getName().equals("ord") || methObj.getName().equals("chr")) {
			return; // arg je vec rezultat — ostavi ga na steku
		}
		
		int offset = methObj.getAdr() - Code.pc; // mora PRE call jer bi se promenio onda pc
		Code.put(Code.call);
		Code.put2(offset);

		if(methObj.getType() != Tab.noType) { // posto se funkcija samo poziva (nema dodele) ako povr tip nije void ostace nesto na steku!
			Code.put(Code.pop);
		}
	}
	
	@Override
	public void visit(DesignatorStatement_inc designatorStatement_inc) {
		if(designatorStatement_inc.getDesignator().obj.getKind() == Obj.Elem) // duplira na stek poslednji indeks i adresu niza jer prifali 
			Code.put(Code.dup2);
		else if(designatorStatement_inc.getDesignator().obj.getKind() == Obj.Fld) // kod rekorda je ubacio nz jel treba 
			Code.put(Code.dup);
		
		Code.load(designatorStatement_inc.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(designatorStatement_inc.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_dec designatorStatement_dec) {
		if(designatorStatement_dec.getDesignator().obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		else if(designatorStatement_dec.getDesignator().obj.getKind() == Obj.Fld) // nz jel treba 
			Code.put(Code.dup);
		
		Code.load(designatorStatement_dec.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(designatorStatement_dec.getDesignator().obj);
	}
	
	// Single Statements
	
	@Override
	public void visit(Statement_return1 statement_return1) { // return;
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_return2 statement_return2) { // return x; - ostavlja vrednost (expr) na steku
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_read statement_read) {
		if(statement_read.getDesignator().obj.getType().equals(Tab.charType))
			Code.put(Code.bread);
		else 
			Code.put(Code.read);
		Code.store(statement_read.getDesignator().obj);
	}
	
	@Override
	public void visit(Statement_print1 statement_print1) { // na steku: val, width
		Struct type = statement_print1.getExpr().struct;
		
		if (type.getKind() == Struct.Array) {
	        Obj[] temps = SemAnalyzer.printArrTemps.get(statement_print1);
	        Obj arrTemp = temps[0];
	        Obj idxTemp = temps[1];
	        boolean isChar = type.getElemType().equals(Tab.charType);

	        Code.store(arrTemp);      // adresa niza (vec na steku) -> temp
	        Code.loadConst(0);		// inicijalizacija brojaca
	        Code.store(idxTemp);      // idx = 0

	        int loopStart = Code.pc;
	        Code.load(idxTemp);
	        Code.load(arrTemp);
	        Code.put(Code.arraylength);
	        Code.putFalseJump(Code.lt, 0); // if !(idx < len) goto exit
	        int exitJump = Code.pc - 2;

	        Code.load(arrTemp);
	        Code.load(idxTemp);
	        Code.put(isChar ? Code.baload : Code.aload);
	        Code.loadConst(0); // width
	        Code.put(isChar ? Code.bprint : Code.print);

	        Code.load(idxTemp);
	        Code.loadConst(1);
	        Code.put(Code.add); // i = i + 1
	        Code.store(idxTemp); // idx = i
	        Code.putJump(loopStart);

	        Code.fixup(exitJump);
	        return;
	    }
		
		Code.loadConst(0); // width

		if (statement_print1.getExpr().struct.equals(Tab.charType)) // provera da li je Expr tipa char
			Code.put(Code.bprint); // ispisuje ascii za slovo, a print ispisuje to slovo!
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(Statement_print2 statement_print2) { // na steku: val, width
		if (statement_print2.getExpr().struct.getKind() == (Struct.Array)) { //tip Expr je niz tj Array
			boolean isChar = statement_print2.getExpr().struct.getElemType().equals(Tab.charType);

	        Code.loadConst(statement_print2.getN2()); // indeks (ne width!)
	        Code.put(isChar ? Code.baload : Code.aload);
	        Code.loadConst(0); // width
	        Code.put(isChar ? Code.bprint : Code.print);
	        return;
			
		} else if(statement_print2.getExpr().struct.equals(Tab.charType)) { // provera da li je Expr tipa char
			Code.loadConst(statement_print2.getN2()); // width
			Code.put(Code.bprint); // ispisuje ascii za slovo, a print ispisuje to slovo!else 
	
		} else {
			Code.loadConst(statement_print2.getN2()); // width
			Code.put(Code.print);
		}
	}
	
	
	/* EXPR */
	
	@Override
	public void visit(AddopTermList_list addopTermList_list) {
		if(addopTermList_list.getAddop() instanceof Addop_plus){
			Code.put(Code.add);
		} else if (addopTermList_list.getAddop() instanceof Addop_min) {
			Code.put(Code.sub);
		}
	}
	
	@Override
	public void visit(TermList_list termList_list) {
		if(termList_list.getMulop() instanceof Mulop_mul){
			Code.put(Code.mul);
		} else if (termList_list.getMulop() instanceof Mulop_div) {
			Code.put(Code.div);
		} else if (termList_list.getMulop() instanceof Mulop_mod) {
			Code.put(Code.rem);
		}
	}
	
	@Override
	public void visit(Factor factor) {
		if (factor.getUnary() instanceof Unary_m) // a=-5
			Code.put(Code.neg);
	}
	
	/* FACTOR */
	
	@Override
	public void visit(FactorSub_num factorSub_num) {
		Code.loadConst(factorSub_num.getN1()); // gura int na stek
	}
	
	@Override
	public void visit(FactorSub_c factorSub_c) {
		Code.loadConst(factorSub_c.getC1()); // gura char na stek
	}
	
	@Override
	public void visit(FactorSub_b factorSub_b) {
		Code.loadConst(factorSub_b.getB1()); // gura bool na stek
	}
		
	@Override
	public void visit(FactorSub_var factorSub_var) {
	    Designator d = factorSub_var.getDesignator();
	    DesignatorList dl = d.getDesignatorList();

	    if (dl instanceof DesignatorList_findany
	            || dl instanceof DesignatorList_map
	            || dl instanceof DesignatorList_length
	            || dl instanceof DesignatorList_contains
	            || dl instanceof DesignatorList_filter
	    		) {
	        return; // value already pushed: arraylength / findAny / map codegen already left it on the stack
	    }
	    Code.load(d.obj);
	}
	
	@Override
	public void visit(FactorSub_maxArr node) { 
	    Designator d = node.getDesignator();

	    Obj[] temps = SemAnalyzer.maxArrTemps.get(node);
	    Obj arr 	= temps[0];
	    Obj counter = temps[1];
	    Obj maxTemp = temps[2]; 
	    
	    // Designator ne ostavlja nista na steku pa moras da ga dohvatis - arr
	    Code.load(d.obj);
	    Code.store(arr);

	    // i = 0
	    Code.loadConst(0);
	    Code.store(counter);
	    
	    // max = arr[0]
	    Code.load(arr);
	    Code.load(counter);
	    Code.put(Code.aload);
	    Code.store(maxTemp);

	    int loopStart = Code.pc;
	    Code.load(counter);
	    Code.load(arr);
	    Code.put(Code.arraylength);
	    Code.putFalseJump(Code.lt, 0); // if !(i < len) goto notFound
	    int jmpNotFound = Code.pc - 2;

	    // arr[i], max
	    Code.load(arr);
	    Code.load(counter);
	    Code.put(Code.aload);
	    
	    Code.load(maxTemp);
	    
	    // if (arr[i] <= max) goto notMax
	    Code.putFalseJump(Code.gt, 0);
	    int jmpNotMax = Code.pc - 2;
	    
	    // max = arr[i]
	    Code.load(arr);
	    Code.load(counter);
	    Code.put(Code.aload);
	    Code.store(maxTemp);
	    
	    // else
	    Code.fixup(jmpNotMax);

	    // IDE DALJE SEKVENCIJALNO

	    // i++
	    Code.load(counter);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(counter);
	    Code.putJump(loopStart); // skaces na pocetak petlje

	    // Kraj
	    Code.fixup(jmpNotFound);
	    Code.load(maxTemp); // rezultat je trenutna max vrednost je sad na steku
	}
	
	@Override
	public void visit(FactorSub_meth factorSub_meth) {
	    Obj methObj = factorSub_meth.getDesignator().obj;
	    if (methObj.getName().equals("ord") || methObj.getName().equals("chr")) {
	        return; // no-op: char/int share representation, arg is already the result
	    }
	    int offset = methObj.getAdr() - Code.pc;
	    Code.put(Code.call);
	    Code.put2(offset);
	}
	
	@Override 
	public void visit(FactorSub_new_array factorSub_new_array) {
		Code.put(Code.newarray); // prethodno je duzina niza na steku
		if(factorSub_new_array.getType().struct.equals(Tab.charType))
			Code.put(0); // ako je tip niza char
		else
			Code.put(1); // ako tip niza nije char 
	}
	
	@Override
	public void visit(FactorSub_new_type factorSub_new_type) {
	    Code.put(Code.new_);
	    Code.put2(factorSub_new_type.getType().struct.getNumberOfFields() * 4); // ili kako vec racunas velicinu u recima/bajtovima
	}
	
	@Override
	public void visit(ArrBase arrBase) {
	    // roditelj je DesignatorList_expr; DesignatorList (sused ArrBase) je vec obj niza
	    DesignatorList_expr parent = (DesignatorList_expr) arrBase.getParent();
	    Code.load(parent.getDesignatorList().obj); // gura adresu niza NA STEK pre nego sto se Expr (indeks) izracuna
	}
	
	
	/* DESIGNATOR */
	
	@Override
	public void visit(DesignatorName designatorName) { // naziv niza - za sad izbrisi jer se kod printa duplira
		// Code.load(designatorName.obj); // adresa niza se uvek pushuje na stek (bez obzira dal je designator sa leve ili desne strane)
	}
	
		
	@Override
	public void visit(DesignatorList_ident designatorList_ident) {
		SyntaxNode par = designatorList_ident.getParent();
		if(par instanceof DesignatorList_ident || par instanceof DesignatorList_expr || par instanceof DesignatorList_length) {
			Code.load(designatorList_ident.obj);
		}
	}
	
	@Override
	public void visit(DesignatorList_length designatorList_length) {
	    Code.load(designatorList_length.getDesignatorList().obj);
	    Code.put(Code.arraylength);
	}
	
	
	/* CONDITION */
	
	private Stack <Integer> skipCondFact = new Stack<>(); 	// ovde guras sve adrese na kojima se desi netacan uslov u CondFact
	private Stack<Integer> skipCondition = new Stack<>();
	private Stack<Integer> skipThen = new Stack<>(); 		// mora da se fixupuje 
	private Stack<Integer> skipElse = new Stack<>();
	
	private int returnRelop(Relop relop) {
		if(relop instanceof Relop_ee) {
			return Code.eq;
		} else if (relop instanceof Relop_ne) {
			return Code.ne;
		}else if (relop instanceof Relop_g) {
			return Code.gt;
		}else if (relop instanceof Relop_ge) {
			return Code.ge;
		}else if (relop instanceof Relop_l) {
			return Code.lt;
		}else {
			return Code.le;
		}
	}
	
	@Override
	public void visit(CondFact_expr condFact_expr) { // if (x) goto 
		Code.loadConst(0); 				// na steku se vec nalazi expression koji se poredi sa nulom - ako su jednaki treba da skace
		Code.putFalseJump(Code.ne, 0); 	// skacemo ako nisu jednaki - 0 jer ne znas gde skaces
		skipCondFact.push(Code.pc - 2);
		// tacna
	}
	
	@Override
	public void visit(CondFact_rel condFact_rel) { // if (a<b) goto ??
		Code.putFalseJump(returnRelop(condFact_rel.getRelop()), 0); // skacemo ako nisu jednaki - 0 jer ne znas gde skaces
		skipCondFact.push(Code.pc - 2);								// cuva se adresa 
		// tacna		
	}
	
	@Override
	public void visit(CondTerm condTerm) { // kraj jednog ora ali i pocetak sledeceg
		// tacne
		Code.putJump(0); // tacne bacamo na THEN - BITNO da prvo izbacis tacno pa onda mozes da radis netacne
		skipCondition.push(Code.pc - 2);
		
		// netacne
		while(!skipCondFact.empty()) {
			Code.fixup(skipCondFact.pop());
		}	
	}
	
	// poziva se samo 1
	public void visit(Condition condition) { // kraj poslednjeg ora ali i pocetak then grane - prvo netacne bacas na else bezuslovno
		// netacni
		Code.putJump(0); // netacne bacamo na else
		skipThen.push(Code.pc - 2);
		
		// then 
		// barem 1 or je bio tacan (svi and-ovi jednog ora)
		while(!skipCondition.empty()) {
			Code.fixup(skipCondition.pop());
			
		// tacne
		}
	}
	
	public void visit(ElseStmt_no elseStmt_no) { // ako nema else vracas netacne koje su u skipThen
		// tacne 
		Code.fixup(skipThen.pop()); // nema else
		
		// tacne + netacne
		// prvo izbacis tacne pa vratis netacne
	}
	
	public void visit(Else else_) {
		// tacne - treba da ga preskoce
		Code.putJump(0);
		skipElse.push(Code.pc - 2);
		
		// netacne
		Code.fixup(skipThen.pop());
	}
	
	public void visit(ElseStmt_yes elseStmt_yes) { // ako nema else vracas netacne koje su u skipThen
		// tacne 
		Code.fixup(skipElse.pop()); // nema else - vracamo tacne koje su preskocili else
		
		// tacne + netacne
		// prvo izbacis tacne pa vratis netacne
	}
	
	// Label
	@Override
	public void visit(Label label) {
		labels.put(label.getI1(), Code.pc);
		
		if(patchAddrs.containsKey(label.getI1()))
			while(!patchAddrs.get(label.getI1()).isEmpty())
				Code.fixup(patchAddrs.get(label.getI1()).remove(0));
	}
	
	//Goto
	public void visit(Statement_goto statement_goto) {
		if(labels.containsKey(statement_goto.getI1()))
			Code.putJump(labels.get(statement_goto.getI1()));
		else {
			Code.putJump(0);
			int patchAddr = Code.pc - 2;
			List<Integer> l;
			if(patchAddrs.containsKey(statement_goto.getI1()))
				l = patchAddrs.get(statement_goto.getI1());
			else {
				l = new ArrayList<>();
				patchAddrs.put(statement_goto.getI1(), l);
			}
			l.add(patchAddr);
		}
		
	}
	

	// Else 
	
	@Override
	public void visit(ExprElse exprElse) {
		Code.putJump(0);            // nakon branch1, preskoci branch2
		skipElse.push(Code.pc - 2);
		Code.fixup(skipThen.pop()); // condition-false ovde: pocetak branch2
	}

	@Override
	public void visit(Expr_con expr_con) {
		Code.fixup(skipElse.pop()); // pokriva obe grane
	}
	
	/* BREAK, CONTINUE */
	
	private Stack<List<Integer>> breakJump = new Stack<>();		// za svaku petlju imas 1 listu - tako pratis ugnjezdavanje
	private Stack<List<Integer>> continueJumps = new Stack<>();
	
	@Override
	public void visit(Statement_break statement_break) { // bezuslovan skok na kraj petlje - adresu saznajemo na kraju petlje (na listu ba vrhu steka se ubacuje povratna adresa)
		Code.putJump(0);
		breakJump.peek().add(Code.pc - 2);
	}
	
	@Override
	public void visit(Statement_continue statement_continue) {
		if (!continueAddrs.isEmpty()) { 			// U for petlji continue skace direktno na korak (step)
			Code.putJump(continueAddrs.peek());
		} else { 									// Za while petlju koristi postojeci mehanizam
			Code.putJump(0);
			continueJumps.peek().add(Code.pc - 2);
		}
	}
		
	/* For */
	
	private Stack<Integer> forCondAdr = new Stack<>();
	private Stack<Integer> forStepAdr = new Stack<>();
	private Stack<Integer> forJumpToBodyAdr = new Stack<>();
	private Stack<Boolean> forHasCond = new Stack<>();
	private Stack<Integer> continueAddrs = new Stack<>();
// 	(Statement_for) FOR LPAREN Statement_f_des SEMI ForCond Statement_f_con SEMI ForStep Statement_f_des RPAREN ForBegin AllStatement

	@Override
	public void visit(ForCond forCond) {
		forCondAdr.push(Code.pc); 			// Pamti adresu pocetka uslova - gde se vraca na sledecu iteraciju
		breakJump.push(new ArrayList<>()); 	// Otvara novu listu za break skokove ove petlje
	}

	@Override
	public void visit(Statement_f_con_c statement_f_con_c) {
		forHasCond.push(true); // Petlja ima definisan uslov
	}

	@Override
	public void visit(Statement_f_con_e statement_f_con_e) {
		forHasCond.push(false); // Petlja nema uslov (npr. for(;;))
	}

	@Override
	public void visit(ForStep forStep) {
		Code.putJump(0); 						// Kada uslov prodje, preskacemo korak (step) i idemo u telo
		forJumpToBodyAdr.push(Code.pc - 2);
		
		int stepAdr = Code.pc; 					// Pocetak koda za korak (i++)
		forStepAdr.push(stepAdr);
		continueAddrs.push(stepAdr); 			// continue treba da skoci na korak
	}

	@Override
	public void visit(ForBegin forBegin) {
		Code.putJump(forCondAdr.peek()); // Nakon sto se izvrsi korak, skaci nazad na uslov
		Code.fixup(forJumpToBodyAdr.pop()); // Fiksiramo skok iz ForStep-a da vodi ovde (na pocetak tela)
	}

	@Override
	public void visit(Statement_for statement_for) {
		Code.putJump(forStepAdr.pop()); // Sa kraja tela petlje skaci na korak (step)
		
		// Ako je postojao uslov, netacan uslov iz Condition-a skace ovde (na kraj petlje)
		if (forHasCond.pop()) {
			Code.fixup(skipThen.pop());
		}
		
		// Bekrpokom fiksiramo sve break skokove iz ove petlje
		List<Integer> breaks = breakJump.pop();
		for (int adr : breaks) {
			Code.fixup(adr);
		}
		
		forCondAdr.pop();
		continueAddrs.pop();
	}
	
	/* do While */
	private Stack<Integer> doStartStack = new Stack<>();

	@Override
	public void visit(DoNonterm doNonterm) {
		doStartStack.push(Code.pc); 		// pocetak tela petlje - ovde se vraćamo ako je uslov tacan
//		breakJump.push(new ArrayList<>());  // otvara listu break skokova za ovu petlju (zbog ugnjezdjavanja petlji)
//		continueJumps.push(new ArrayList<>());
	}

	@Override
	public void visit(WhileNonterm whileNonterm) {
//		List<Integer> continues = continueJumps.pop();
//	    for (int adr : continues) {
//	        Code.fixup(adr);
//	    }
	}

	@Override
	public void visit(Statement_do statement_do) { // kraj do petlje
		Code.putJump(doStartStack.pop()); // tacan uslov -> nazad na pocetak tela
	    Code.fixup(skipThen.pop());       // netacan uslov -> izlaz iz petlje

//	    List<Integer> breaks = breakJump.pop(); // fiksiraj sve break skokove
//	    for (int adr : breaks) {
//	        Code.fixup(adr);
//	    }
	}
	
	/* While */
	private Stack<Integer> whileCondAdr = new Stack<>();

	@Override
	public void visit(WhileCond whileCond) {
		whileCondAdr.push(Code.pc);        // adresa pocetka uslova - ovde se vracamo na svaki ponovni pokusaj
//		breakJump.push(new ArrayList<>()); // nova lista break skokova za ovu petlju
//		continueAddrs.push(Code.pc);       // continue u while petlji skace direktno na proveru uslova
	}

	@Override
	public void visit(WhileBegin whileBegin) {
		// nema potrebe za posebnim kodom ovde - telo pocinje odmah posle ovog hook-a
		// (za razliku od for petlje, while nema odvojen "step" deo)
	}

	@Override
	public void visit(Statement_while statement_while) {
		Code.putJump(whileCondAdr.pop());  // po zavrsetku tela, bezuslovno nazad na proveru uslova
		Code.fixup(skipThen.pop());        // netacan uslov (iz Condition) izlazi ovde, iza petlje

//		List<Integer> breaks = breakJump.pop();
//		for (int adr : breaks) {
//			Code.fixup(adr);
//		}
//		continueAddrs.pop();
	}
	
	/* FOREACH */
	private Stack<Integer> foreachCondAdr = new Stack<>();
	private Stack<Integer> foreachEndJump = new Stack<>();

	@Override
	public void visit(ForeachStoreStart h) { // Na steku je Expr1
	    Obj[] temps = SemAnalyzer.foreachTemps.get((Statement_foreach) h.getParent());
	    Code.store(temps[1]);
	}
	@Override
	public void visit(ForeachStoreEnd h) { // Na steku je Expr2
	    Obj[] temps = SemAnalyzer.foreachTemps.get((Statement_foreach) h.getParent());
	    Code.store(temps[2]);
	}

	@Override
	public void visit(ForeachBegin foreachBegin) {
	    Statement_foreach parent = (Statement_foreach) foreachBegin.getParent();
	    Obj[] temps = SemAnalyzer.foreachTemps.get(parent);
	    Obj counter = temps[0];
	    Obj start = temps[1];
	    Obj end = temps[2];
	    
	    // i = start
	    Code.load(start);
	    Code.store(counter); 
	    
	    // Postavljas adresu pocetka Condition dela
	    int condAdr = Code.pc;
	    foreachCondAdr.push(condAdr); 

	    // if !(i < end) goto kraj
	    Code.load(counter);
	    Code.load(end);
	    Code.putFalseJump(Code.lt, 0);
	    foreachEndJump.push(Code.pc - 2);

	    // Preskoci step, idi na telo (step se izvrsava tek posle tela, preko continue/kraja)
	    Code.putJump(0);
	    int jumpOverStepAdr = Code.pc - 2;

	    // Step: ovde je prava adresa na koju continue treba da skoci
	    int stepAdr = Code.pc;
	    continueAddrs.push(stepAdr);

	    // i++
	    Code.load(counter);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(counter);

	    // Ides nazad na Proveru Condition
	    Code.putJump(condAdr);

	    // Body
	    Code.fixup(jumpOverStepAdr);

	    breakJump.push(new ArrayList<>());
	}

	@Override
	public void visit(Statement_foreach statement_foreach) { // zavrsila se petlja
		// Body je gotovo i treba -> u Step (isti put kao i continue)
	    Code.putJump(continueAddrs.pop());

	    // Postavljas pocetak KRAJA petlje
	    Code.fixup(foreachEndJump.pop());
	    foreachCondAdr.pop();

	    List<Integer> breaks = breakJump.pop();
	    for (int adr : breaks) {
	        Code.fixup(adr);
	    }
	}
	
	// FindAny
	
	private Stack<Integer> loopStartStack = new Stack<>();
	private Stack<Integer> exitJumpStack  = new Stack<>();
	
	@Override
	public void visit(FindAnyBegin findAnyBegin) {
	    DesignatorList_findany node = (DesignatorList_findany) findAnyBegin.getParent();
	    Obj[] temps = SemAnalyzer.findAnyTemps.get(node);
	    Obj counter = temps[0];

	    Code.loadConst(0);
	    Code.store(counter); // i = 0
	    // target ce se upisati posle Expr-a (u DesignatorList_findany)
	}

	// Stek stanje kad se udje u visit(DesignatorList_findany): stek: [ exprValue ]
	
	@Override
	public void visit(DesignatorList_findany node) {
	    Obj[] temps = SemAnalyzer.findAnyTemps.get(node);
	    Obj counter = temps[0];
	    Obj target  = temps[1];
	    Obj arr = node.getDesignatorList().obj;

	    Code.store(target); // Skida se Expr rezultat (koji je vec na steku) i stavlja se u target

	    int loopStart = Code.pc;
	    Code.load(counter); 			// stek: [ i ]
	    Code.load(arr);					// stek: [ i, arrRef ]
	    Code.put(Code.arraylength);		// pop arrRef, push arr.length -> stek: [i, length]
	    Code.putFalseJump(Code.lt, 0); 	// if !(i < len) goto notFound (skida obe vrednosti sa steka i uporedjuje ih)
	    // stek je prazan: [ ] 
	    
	    int jmpNotFound = Code.pc - 2;
	    
	    // provera: niz[i] == target ?
	    Code.load(arr);
	    Code.load(counter);
	    Code.put(arr.getType().getElemType().equals(Tab.charType) ? Code.baload : Code.aload); // ucitava se element niza
	    Code.load(target);
	    Code.putFalseJump(Code.eq, 0); // ako su jednaki ide u "found"
	    // stek je prazan: [ ] 
	    
	    // ako nisu jednaki
	    int jmpNotEqual = Code.pc - 2;

	    // found:
	    Code.loadConst(1);
	    Code.putJump(0);
	    int jmpEnd1 = Code.pc - 2; // desice se fixup - skacemo na kraj

	    // jmpNotEqual:
	    Code.fixup(jmpNotEqual);
	    
	    // i++
	    Code.load(counter);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(counter);
	    Code.putJump(loopStart);

	    Code.fixup(jmpNotFound);
	    Code.loadConst(0); // not found -> false

	    Code.fixup(jmpEnd1); // rezultat (0/1) je sad na steku, kao "vrednost" DesignatorList_findany
	}
	
	// Map
	
	@Override
	public void visit(MapBegin mapBegin) {
	    DesignatorList_map parentNode = (DesignatorList_map) mapBegin.getParent();
	    Obj[] temps = SemAnalyzer.mapTemps.get(parentNode);
	    Obj counter   = temps[0];
	    Obj resultArr = temps[1];
	    Obj identObj = temps[2];
	    Obj srcArr    = parentNode.getDesignatorList().obj;

	    Code.load(srcArr);			// ExprStack -> adr
	    Code.put(Code.arraylength); // ExprStack -> length
	    Code.put(Code.newarray);	// ExprStack -> new adr
	    Code.put(resultArr.getType().getElemType().equals(Tab.charType) ? 0 : 1);
	    Code.store(resultArr);		// ExprStack -> /

	    Code.loadConst(0);
	    Code.store(counter); // i = 0 
	    
	    // pocetak petlje
	    loopStartStack.push(Code.pc);

	    // if (i < length) NE SKACI, else goto ??? (KRAJ PETLJE)
	    Code.load(counter);					// ExprStack -> i
	    Code.load(srcArr);					// ExprStack -> i, adr
	    Code.put(Code.arraylength);			// ExprStack -> i, length
	    Code.putFalseJump(Code.lt, 0);		// ExprStack -> /
	    exitJumpStack.push(Code.pc - 2);	// Pamti se adresa ??? sto ce se kasnije popuniti - fixup

	    Code.load(srcArr);					// ExprStack -> adr
	    Code.load(counter);					// ExprStack -> adr, i
	    Code.put(srcArr.getType().getElemType().equals(Tab.charType) ? Code.baload : Code.aload); // ExprStack -> niz[i]
	    Code.store(identObj); 				// x = niz[i]
	    // identObj — tokom semanticke analiza, pa je garantovana validna addresa
	    
	    // ExprStack -> / 
	}

	@Override
	public void visit(DesignatorList_map node) { // Expr je na steku
	    Obj[] temps = SemAnalyzer.mapTemps.get(node);
	    Obj counter   = temps[0];
	    Obj resultArr = temps[1];
	    Obj valueTemp = temps[3]; // privremen rezultat

	    Code.store(valueTemp); // valueTemp = Expr
	    // Expr -> /

	    Code.load(resultArr); 	// Expr -> resAdr
	    Code.load(counter);		// Expr -> resAdr, i
	    Code.load(valueTemp);	// Expr -> resAdr, i, Expr
	    Code.put(resultArr.getType().getElemType().equals(Tab.charType) ? Code.bastore : Code.astore); // Expr -> / | resArr[i] = Expr

	    // i++
	    Code.load(counter);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(counter);

	    Code.putJump(loopStartStack.pop()); // uzima adresu pocetka petlje
	    Code.fixup(exitJumpStack.pop());	// popunjava ??? sa adresom trenutnog Code.pc

	    Code.load(resultArr);
	}	
	
	
	// Contains
	
	@Override
	public void visit(ContainsBegin containsBegin) {
		DesignatorList_contains node = (DesignatorList_contains) containsBegin.getParent();
	    Obj[] temps = SemAnalyzer.containsTemps.get(node);
	    Obj counter = temps[0];

	    Code.loadConst(0);
	    Code.store(counter); // i = 0
	    // target ce se upisati posle Expr-a (u DesignatorList_findany)
	}

	// Stek stanje kad se udje u visit(DesignatorList_findany): stek: [ exprValue ]
	
	@Override
	public void visit(DesignatorList_contains node) {
	    Obj[] temps = SemAnalyzer.containsTemps.get(node);
	    Obj counter = temps[0];
	    Obj target  = temps[1];
	    Obj arr = node.getDesignatorList().obj;

	    Code.store(target); // Skida se Expr rezultat (koji je vec na steku) i stavlja se u target

	    int loopStart = Code.pc;
	    Code.load(counter); 			// stek: [ i ]
	    Code.load(arr);					// stek: [ i, arrRef ]
	    Code.put(Code.arraylength);		// pop arrRef, push arr.length -> stek: [i, length]
	    Code.putFalseJump(Code.lt, 0); 	// if !(i < len) goto notFound (skida obe vrednosti sa steka i uporedjuje ih)
	    // stek je prazan: [ ] 
	    
	    int jmpNotFound = Code.pc - 2;
	    
	    // provera: niz[i] == target ?
	    Code.load(arr);
	    Code.load(counter);
	    Code.put(arr.getType().getElemType().equals(Tab.charType) ? Code.baload : Code.aload); // ucitava se element niza
	    Code.load(target);
	    Code.putFalseJump(Code.eq, 0); // ako su jednaki ide u "found"
	    // stek je prazan: [ ] 
	    
	    // ako nisu jednaki
	    int jmpNotEqual = Code.pc - 2;

	    // found:
	    Code.loadConst(1);
	    Code.putJump(0);
	    int jmpEnd1 = Code.pc - 2; // desice se fixup - skacemo na kraj

	    // jmpNotEqual:
	    Code.fixup(jmpNotEqual);
	    
	    // i++
	    Code.load(counter);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(counter);
	    Code.putJump(loopStart);

	    Code.fixup(jmpNotFound);
	    Code.loadConst(0); // not found -> false

	    Code.fixup(jmpEnd1); // rezultat (0/1) je sad na steku, kao "vrednost" DesignatorList_contains
	}
	
	// filter
	@Override
	public void visit(FilterBegin filterBegin) {
	    DesignatorList_filter parentNode = (DesignatorList_filter) filterBegin.getParent();
	    Obj[] temps = SemAnalyzer.filterTemps.get(parentNode);
	    Obj readIdx   = temps[0];
	    Obj writeIdx  = temps[1];
	    Obj resultArr = temps[2];
	    Obj identObj  = temps[3];
	    Obj srcArr    = parentNode.getDesignatorList().obj; 
	    

	    // kreiras novi niz iste duzine kao stari
	    Code.load(srcArr);
	    Code.put(Code.arraylength);
	    Code.put(Code.newarray);
	    Code.put(resultArr.getType().getElemType().equals(Tab.charType) ? 0 : 1);
	    Code.store(resultArr);

	    Code.loadConst(0);
	    Code.store(readIdx);   // i = 0

	    Code.loadConst(0);
	    Code.store(writeIdx);  // w = 0  <- razlika u odnosu na map

	    // pocetak petlje
	    loopStartStack.push(Code.pc);

	    Code.load(readIdx);
	    Code.load(srcArr);
	    Code.put(Code.arraylength);
	    Code.putFalseJump(Code.lt, 0); // if !(i < arr.length) goto endofloop
	    exitJumpStack.push(Code.pc - 2);

	    Code.load(srcArr);
	    Code.load(readIdx);
	    Code.put(srcArr.getType().getElemType().equals(Tab.charType) ? Code.baload : Code.aload);
	    Code.store(identObj);  // x = niz[i]		
	}
	
	@Override
	public void visit(DesignatorList_filter node) {
	    Obj[] temps = SemAnalyzer.filterTemps.get(node);
	    Obj readIdx   = temps[0];
	    Obj writeIdx  = temps[1];
	    Obj resultArr = temps[2];
	    Obj identObj  = temps[3];

	    // TACNA GRANA: upisi element u resultArr[writeIdx], pa writeIdx++
	    Code.load(resultArr);
	    Code.load(writeIdx);
	    Code.load(identObj);
	    Code.put(resultArr.getType().getElemType().equals(Tab.charType) ? Code.bastore : Code.astore); // resultArr[w] = elem

	    Code.load(writeIdx);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(writeIdx); // w++

	    // SPAJANJE: netacna grana (Condition-ov skipThen) sleti bas ovde,
	    // tacno kao kod ElseStmt_no kad nema else grane
	    Code.fixup(skipThen.pop());

	    // ZAJEDNICKI DEO (i tacna i netacna grana): i++
	    Code.load(readIdx);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.store(readIdx); // r++

	    Code.putJump(loopStartStack.pop()); // skaces na pocetak petlje u filterBegin
	    Code.fixup(exitJumpStack.pop());

	    Code.load(resultArr);
	}
}
