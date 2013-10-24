/**
 * @(#)Instruction.java  4:05:05 PM Feb 3, 2009
 * 
 * Copyright (c) 2004-2009 Sadat Chowdhury (sadatc@gmail.com)
 * 
 * The author reserves all rights to this software. Please contact the author 
 * (sadatc@gmail.com) to obtain permission to use, modify, or redistribute this 
 * software in any form. 
 * 
 * Unless otherwise stated, the author issues no guarantees of success and 
 * offers no warranties against damages of any kind. 
 * 
 * Last Revision : $Rev:: 6                     $: 
 * Last Updated  : $Date:: 2012-10-14 22:50:12 #$:
 * 
 */

package com.synthverse.stacks;

import com.synthverse.synthscape.core.ResourceState;

/**
 * Defines Instruction Set of the Stacks language. The operation performed when
 * an Instruction is executed on the
 * {@link com.synthverse.stacks.VirtualMachine} is defined in
 * {@link #execute(VirtualMachine)}
 * 
 * NOTE: It is very important not to add new Instructions at the begining or
 * change the order of Instructions once they are defined within. It's very
 * difficult to programmatically ensure this - but if serialization from two
 * different versions of this enum where new Instructions were added in the
 * begining or order was changed, they might not match.
 * 
 * @author sadat
 * 
 */
public enum Instruction {

    //
    // DO NOT ADD NEW INSTRUCTIONS HERE! APPEND THEM AFTER THE LAST ONE
    //
    // DO NOT CHANGE ORDER AS THIS WILL BREAK BACKWARD COMPATIBILITY
    // IF SERIALIZATION IS USED.
    //

    NOOP("NOOP") {
	public void execute(VirtualMachine virtualMachine) {
	    // do nothing!
	    virtualMachine.incrementIP();

	}
    },

    CONST_TRUE("CONST.TRUE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(true);
	    virtualMachine.incrementIP();
	}
    },

    CONST_FALSE("CONST.FALSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(false);
	    virtualMachine.incrementIP();
	}
    },

    CONST_INT_ONE("CONST.1") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(1);
	    virtualMachine.incrementIP();
	}
    },
    CONST_INT_ZERO("CONST.0") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(0);
	    virtualMachine.incrementIP();
	}
    },
    CONST_FLOAT_ONE("CONST.1.0") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().push(1.0f);
	    virtualMachine.incrementIP();
	}
    },
    CONST_FLOAT_ZERO("CONST.0.0") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().push(0);
	    virtualMachine.incrementIP();
	}
    },

    CONST_FLOAT_PI("CONST.PI") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().push(Math.PI);
	    virtualMachine.incrementIP();
	}
    },

    CONST_FLOAT_E("CONST.E") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().push(Math.E);
	    virtualMachine.incrementIP();
	}
    },

    BOOLEAN_FLUSH("BOOLEAN.FLUSH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().flush();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_DUP("BOOLEAN.DUP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().dup();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_REVERSE("BOOLEAN.REVERSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().reverse();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_EQUAL("BOOLEAN.EQUAL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().equal();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_SWAP("BOOLEAN.SWAP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().swap();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_ROT("BOOLEAN.ROT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().rot();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_YANK("BOOLEAN.YANK") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getBooleanStack().yank(yankIndex);
	    }
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_YANKDUP("BOOLEAN.YANKDUP") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getBooleanStack().yankdup(yankIndex);
	    }
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_RAND("BOOLEAN.RAND") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().rand();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_SIZE("BOOLEAN.STACKDEPTH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(virtualMachine.getBooleanStack().size());
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_POP("BOOLEAN.POP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().pop();
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_FROM_FLOAT("BOOLEAN.FROM_FLOAT") {
	public void execute(VirtualMachine virtualMachine) {
	    FloatStack floatStack = virtualMachine.getFloatStack();
	    if (floatStack.size() > 0) {
		double floatValue = floatStack.pop();
		virtualMachine.getBooleanStack().fromFloat(floatValue);
	    }
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_FROM_INTEGER("BOOLEAN.FROM_INTEGER") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack intgerStack = virtualMachine.getIntegerStack();
	    if (intgerStack.size() > 0) {
		int intValue = intgerStack.pop();
		virtualMachine.getBooleanStack().fromInteger(intValue);
	    }
	    virtualMachine.incrementIP();

	}
    },

    BOOLEAN_NOT("BOOLEAN.NOT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().not();
	    virtualMachine.incrementIP();
	}
    },

    BOOLEAN_AND("BOOLEAN.AND") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().and();
	    virtualMachine.incrementIP();
	}
    },

    BOOLEAN_OR("BOOLEAN.OR") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().or();
	    virtualMachine.incrementIP();
	}
    },

    BOOLEAN_XOR("BOOLEAN.XOR") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().xor();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_FLUSH("INTEGER.FLUSH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().flush();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_DUP("INTEGER.DUP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().dup();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_REVERSE("INTEGER.REVERSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().reverse();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_EQUAL("INTEGER.EQUAL") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		virtualMachine.getBooleanStack().push(virtualMachine.getIntegerStack().equal());
	    }
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_SWAP("INTEGER.SWAP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().swap();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_ROT("INTEGER.ROT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().rot();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_YANK("INTEGER.YANK") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getIntegerStack().yank(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_YANKDUP("INTEGER.YANKDUP") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getIntegerStack().yankdup(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_RAND("INTEGER.RAND") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().rand();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_SIZE("INTEGER.STACKDEPTH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(virtualMachine.getIntegerStack().size());
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_POP("INTEGER.POP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().pop();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_FROM_FLOAT("INTEGER.FROM_FLOAT") {
	public void execute(VirtualMachine virtualMachine) {
	    FloatStack floatStack = virtualMachine.getFloatStack();
	    if (floatStack.size() > 0) {
		double floatValue = floatStack.pop();
		virtualMachine.getIntegerStack().fromFloat(floatValue);
	    }
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_FROM_BOOLEAN("INTEGER.FROM_BOOLEAN") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (booleanStack.size() > 0) {
		boolean booleanValue = booleanStack.pop();
		virtualMachine.getIntegerStack().fromBoolean(booleanValue);
	    }
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_MOD("INTEGER.MOD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().mod();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_MUL("INTEGER.MUL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().mul();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_DIV("INTEGER.DIV") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().div();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_SUB("INTEGER.SUB") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().sub();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_ADD("INTEGER.ADD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().add();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_LESS_THAN("INTEGER.<") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getIntegerStack().lessThan()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_GREATER_THAN("INTEGER.>") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getIntegerStack().greaterThan()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_LESS_THAN_EQUALS("INTEGER.<=") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getIntegerStack().lessThanEquals()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_GREATER_THAN_EQUALS("INTEGER.>=") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getIntegerStack().greaterThanEquals()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_MAX("INTEGER.MAX") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().max();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_MIN("INTEGER.MIN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().min();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_IS_ZERO("INTEGER.ISZ") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getIntegerStack().isZero()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_IS_POSITIVE("INTEGER.IS+") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getIntegerStack().isPositive()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_IS_EVEN("INTEGER.IS_EVEN") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getIntegerStack().isEven()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    INTEGER_INCREMENT("INTEGER.++") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().increment();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_DECREMENT("INTEGER.--") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().decrement();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_FILL("INTEGER.FILL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().fill();
	    virtualMachine.incrementIP();
	}
    },

    INTEGER_SORT("INTEGER.SORT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().sort();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_FLUSH("FLOAT.FLUSH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().flush();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_DUP("FLOAT.DUP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().dup();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_REVERSE("FLOAT.REVERSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().reverse();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_EQUAL("FLOAT.EQUAL") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		virtualMachine.getBooleanStack().push(virtualMachine.getFloatStack().equal());
	    }
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SWAP("FLOAT.SWAP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().swap();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ROT("FLOAT.ROT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().rot();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_YANK("FLOAT.YANK") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getFloatStack().yank(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_YANKDUP("FLOAT.YANKDUP") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getFloatStack().yankdup(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_RAND("FLOAT.RAND") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().rand();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SIZE("FLOAT.STACKDEPTH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(virtualMachine.getFloatStack().size());
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_POP("FLOAT.POP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().pop();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_FROM_INTEGER("FLOAT.FROM_INTEGER") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		double intValue = integerStack.pop();
		virtualMachine.getIntegerStack().fromFloat(intValue);
	    }
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_FROM_BOOLEAN("FLOAT.FROM_BOOLEAN") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (booleanStack.size() > 0) {
		boolean booleanValue = booleanStack.pop();
		virtualMachine.getFloatStack().fromBoolean(booleanValue);
	    }
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_MOD("FLOAT.MOD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().mod();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_MUL("FLOAT.MUL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().mul();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_DIV("FLOAT.DIV") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().div();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SUB("FLOAT.SUB") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().sub();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ADD("FLOAT.ADD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().add();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_LESS_THAN("FLOAT.<") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getFloatStack().lessThan()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_GREATER_THAN("FLOAT.>") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getFloatStack().greaterThan()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_LESS_THAN_EQUALS("FLOAT.<=") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getFloatStack().lessThanEquals()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_GREATER_THAN_EQUALS("FLOAT.>=") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();

	    if (virtualMachine.getFloatStack().greaterThanEquals()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_MAX("FLOAT.MAX") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().max();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_MIN("FLOAT.MIN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().min();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_IS_ZERO("FLOAT.ISZ") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getFloatStack().isZero()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_IS_POSITIVE("FLOAT.IS+") {
	public void execute(VirtualMachine virtualMachine) {
	    BooleanStack booleanStack = virtualMachine.getBooleanStack();
	    if (virtualMachine.getFloatStack().isPositive()) {
		booleanStack.push(true);
	    } else {
		booleanStack.push(false);
	    }

	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SIN("FLOAT.SIN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().sin();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SINH("FLOAT.SINH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().sinh();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ASIN("FLOAT.ASIN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().asin();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_COS("FLOAT.COS") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().cos();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_COSH("FLOAT.COSH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().cosh();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ACOS("FLOAT.ACOS") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().acos();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_TAN("FLOAT.TAN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().tan();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ATAN("FLOAT.ATAN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().atan();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_TANH("FLOAT.TANH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().tanh();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_ATAN2("FLOAT.ATAN2") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().atan2();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_CBRT("FLOAT.CBRT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().cbrt();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_CEIL("FLOAT.CEIL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().ceil();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_FLOOR("FLOAT.FLOOR") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().floor();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_SQRT("FLOAT.SQRT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().sqrt();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_EXP("FLOAT.EXP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().exp();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_EXPM1("FLOAT.EXPM1") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().expm1();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_LOG("FLOAT.LOG") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().log();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_LOG10("FLOAT.LOG10") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().log10();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_LOG1P("FLOAT.LOG1P") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().log1p();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_HYPOT("FLOAT.HYPOT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().hypot();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_POW("FLOAT.POW") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().pow();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_DEG2RAD("FLOAT.DEG2RAD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().deg2rad();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_RAD2DEG("FLOAT.RAD2DEG") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().rad2deg();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_INCREMENT("FLOAT.++") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().increment();
	    virtualMachine.incrementIP();
	}
    },

    FLOAT_DECREMENT("FLOAT.--") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getFloatStack().decrement();
	    virtualMachine.incrementIP();
	}
    },

    CODE_FLUSH("CODE.FLUSH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().flush();
	    virtualMachine.incrementIP();
	}
    },

    CODE_DUP("CODE.DUP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().dup();
	    virtualMachine.incrementIP();
	}
    },

    CODE_REVERSE("CODE.REVERSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().reverse();
	    virtualMachine.incrementIP();
	}
    },

    CODE_EQUAL("CODE.EQUAL") {
	public void execute(VirtualMachine virtualMachine) {
	    CodeStack codeStack = virtualMachine.getCodeStack();
	    if (codeStack.size() > 0) {
		virtualMachine.getBooleanStack().push(virtualMachine.getCodeStack().equal());
	    }
	    virtualMachine.incrementIP();
	}
    },

    CODE_SWAP("CODE.SWAP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().swap();
	    virtualMachine.incrementIP();
	}
    },

    CODE_ROT("CODE.ROT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().rot();
	    virtualMachine.incrementIP();
	}
    },

    CODE_YANK("CODE.YANK") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getCodeStack().yank(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    CODE_YANKDUP("CODE.YANKDUP") {
	public void execute(VirtualMachine virtualMachine) {
	    IntegerStack integerStack = virtualMachine.getIntegerStack();
	    if (integerStack.size() > 0) {
		int yankIndex = integerStack.glance();
		virtualMachine.getCodeStack().yankdup(yankIndex);
	    }
	    virtualMachine.incrementIP();
	}
    },

    CODE_RAND("CODE.RAND") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().rand();
	    virtualMachine.incrementIP();
	}
    },

    CODE_SIZE("CODE.STACKDEPTH") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getIntegerStack().push(virtualMachine.getCodeStack().size());
	    virtualMachine.incrementIP();
	}
    },

    CODE_POP("CODE.POP") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getCodeStack().pop();
	    virtualMachine.incrementIP();
	}
    },

    CODE_SWAP_IA("CODE.SWAP_IA") {
	public void execute(VirtualMachine virtualMachine) {

	    CodeStack cs = virtualMachine.getCodeStack();
	    MetaInstructionArray ia = virtualMachine.getInstructionArray();

	    MetaInstruction[] saved_cs = cs.getInternalArray();
	    cs.setInternalArray(ia.getInternalArray());
	    ia.setInternalArray(saved_cs);
	    virtualMachine.incrementIP();
	}
    },

    CODE_SWAP_IA_IP_RESET("CODE.SWAP_IA+") {
	public void execute(VirtualMachine virtualMachine) {

	    CodeStack cs = virtualMachine.getCodeStack();
	    MetaInstructionArray ia = virtualMachine.getInstructionArray();

	    MetaInstruction[] saved_cs = cs.getInternalArray();
	    cs.setInternalArray(ia.getInternalArray());
	    ia.setInternalArray(saved_cs);
	    virtualMachine.setIP(0);
	}
    },

    CODE_COPY_TO_IA("CODE.COPY_TO_IA") {
	public void execute(VirtualMachine virtualMachine) {

	    CodeStack cs = virtualMachine.getCodeStack();
	    MetaInstructionArray ia = virtualMachine.getInstructionArray();
	    MetaInstruction[] cs_array = cs.getInternalArray();
	    int size = cs.size();
	    ia.copyFromArray(cs_array, size);
	    virtualMachine.incrementIP();

	}
    },

    CODE_COPY_FROM_IA("CODE.COPY_FROM_IA") {
	public void execute(VirtualMachine virtualMachine) {

	    CodeStack cs = virtualMachine.getCodeStack();
	    MetaInstructionArray ia = virtualMachine.getInstructionArray();
	    MetaInstruction[] ia_array = ia.getInternalArray();
	    cs.copyFrom(ia_array);
	    virtualMachine.incrementIP();

	}
    },

    // ---------- MOVEMENT RELATED INSTRUCTIONS --------

    ACTION_MOVE_E("ACTION.MOVE_E") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveEast();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_W("ACTION.MOVE_W") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveWest();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_N("ACTION.MOVE_N") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveNorth();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_S("ACTION.MOVE_S") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveSouth();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_NE("ACTION.MOVE_NE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveNorthEast();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_NW("ACTION.MOVE_NW") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveNorthWest();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_SE("ACTION.MOVE_SE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveSouthEast();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_SW("ACTION.MOVE_SW") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveSouthWest();
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_CONDITIONALLY_NS("ACTION.MOVE_C_NS") {
	public void execute(VirtualMachine virtualMachine) {
	    boolean foundTrue = false;
	    if (virtualMachine.getBooleanStack().size() > 0) {
		foundTrue = virtualMachine.getBooleanStack().pop();
	    }
	    if (foundTrue) {
		virtualMachine.getAgent().operationMoveSouth();
	    } else {
		virtualMachine.getAgent().operationMoveNorth();
	    }
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_CONDITIONALLY_EW("ACTION.MOVE_C_EW") {
	public void execute(VirtualMachine virtualMachine) {
	    boolean foundTrue = false;
	    if (virtualMachine.getBooleanStack().size() > 0) {
		foundTrue = virtualMachine.getBooleanStack().pop();
	    }
	    if (foundTrue) {
		virtualMachine.getAgent().operationMoveEast();
	    } else {
		virtualMachine.getAgent().operationMoveWest();
	    }
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_CONDITIONALLY_NENW("ACTION.MOVE_C_NENW") {
	public void execute(VirtualMachine virtualMachine) {
	    boolean foundTrue = false;
	    if (virtualMachine.getBooleanStack().size() > 0) {
		foundTrue = virtualMachine.getBooleanStack().pop();
	    }
	    if (foundTrue) {
		virtualMachine.getAgent().operationMoveNorthEast();
	    } else {
		virtualMachine.getAgent().operationMoveNorthWest();
	    }
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_CONDITIONALLY_SESW("ACTION.MOVE_C_SESW") {
	public void execute(VirtualMachine virtualMachine) {
	    boolean foundTrue = false;
	    if (virtualMachine.getBooleanStack().size() > 0) {
		foundTrue = virtualMachine.getBooleanStack().pop();
	    }
	    if (foundTrue) {
		virtualMachine.getAgent().operationMoveSouthEast();
	    } else {
		virtualMachine.getAgent().operationMoveSouthWest();
	    }
	    virtualMachine.incrementIP();
	}
    },

    ACTION_MOVE_RANDOM("ACTION.MOVE_RANDOM") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationRandomMove();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_MOVE_CONDITIONALLY_RANDOM("ACTION.MOVE_C_RANDOM") {
	public void execute(VirtualMachine virtualMachine) {
	    boolean foundTrue = false;
	    if (virtualMachine.getBooleanStack().size() > 0) {
		foundTrue = virtualMachine.getBooleanStack().pop();
	    }
	    if (foundTrue) {
		virtualMachine.getAgent().operationRandomMove();
	    }
	    virtualMachine.incrementIP();
	}

    },

    ACTION_MOVE_TO_CLOSEST_HOME("ACTION.MOVE_TO_CLOSEST_HOME") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveToClosestCollectionSite();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_MOVE_TO_CLOSEST_AGENT("ACTION.MOVE_TO_CLOSEST_AGENT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationMoveToClosestAgent();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_FOLLOW_TRAIL("ACTION.FOLLOW_TRAIL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationFollowTrail();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_LEAVE_TRAIL("ACTION.LEAVE_TRAIL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationLeaveTrail();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_DETECT_HOME("ACTION.DETECT_HOME") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().operationDetectCollectionSite());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_DETECT_RAW_RESOURCE("ACTION.DETECT_RAW_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().operationDetectExtractedResource());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_DETECT_EXTRACTED_RESOURCE_RESOURCE("ACTION.DETECT_EXTRACTED_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().operationDetectExtractedResource());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_DETECT_PROCESSED_RESOURCE_RESOURCE("ACTION.DETECT_PROCESSED_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().operationDetectProcessedResource());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_DETECT_TRAIL("ACTION.DETECT_TRAIL") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().operationDetectTrail());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_RESOURCE_EXTRACT("ACTION.RESOURCE_EXTRACT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationExtractResource();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_RESOURCE_PROCESS("ACTION.RESOURCE_PROCESS") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationProcessResource();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_RESOURCE_LOAD("ACTION.RESOURCE_LOAD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationLoadResource();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_RESOURCE_UNLOAD("ACTION.RESOURCE_UNLOAD") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getAgent().operationUnLoadResource();
	    virtualMachine.incrementIP();
	}

    },

    ACTION_IS_CARRYING_RESOURCE("ACTION.IS_CARRYING_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(virtualMachine.getAgent().isCarryingResource());
	    virtualMachine.incrementIP();
	}

    },

    ACTION_IS_CARRYING_EXTRACTED_RESOURCE("ACTION.IS_CARRYING_EXTRACTED_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(
		    virtualMachine.getAgent().isCarryingResource()
			    && virtualMachine.getAgent().getStateOfCarriedResource() == ResourceState.EXTRACTED);
	    virtualMachine.incrementIP();
	}

    },

    ACTION_IS_CARRYING_PROCESSED_RESOURCE("ACTION.IS_CARRYING_PROCESSED_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(
		    virtualMachine.getAgent().isCarryingResource()
			    && virtualMachine.getAgent().getStateOfCarriedResource() == ResourceState.PROCESSED);
	    virtualMachine.incrementIP();
	}

    },

    ACTION_IS_CARRYING_RAW_RESOURCE("ACTION.IS_CARRYING_RAW_RESOURCE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getBooleanStack().push(
		    virtualMachine.getAgent().isCarryingResource()
			    && virtualMachine.getAgent().getStateOfCarriedResource() == ResourceState.RAW);
	    virtualMachine.incrementIP();
	}

    },

    /*
     * ACTION_RECHARGE("ACTION.RECHARGE") { public void execute(VirtualMachine
     * virtualMachine) { virtualMachine.getAgent().operationRecharge();
     * virtualMachine.incrementIP(); }
     * 
     * },
     */

    INSTRUCTION_REVERSE("INSTRUCTION.REVERSE") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getInstructionArray().reverse();
	    virtualMachine.incrementIP();
	}
    },

    INSTRUCTION_NOOP_LEFT("INSTRUCTION.NOOP_LEFT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getInstructionArray().noop_left();
	    virtualMachine.incrementIP();
	}
    },

    INSTRUCTION_NOOP_RIGHT("INSTRUCTION.NOOP_RIGHT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getInstructionArray().noop_right();
	    virtualMachine.incrementIP();
	}
    },

    INSTRUCTION_EXCHANGE_LEFT("INSTRUCTION.EXCHANGE_LEFT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getInstructionArray().exchange_left(virtualMachine.getIP());

	}
    },

    INSTRUCTION_EXCHANGE_RIGHT("INSTRUCTION.EXCHANGE_RIGHT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.getInstructionArray().exchange_right(virtualMachine.getIP());

	}
    },

    INSTRUCTION_HOP_LEFT("INSTRUCTION.HOP_LEFT") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.setValidatedIP(virtualMachine.getIP() - 2)) {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_MOVE_LEFT("INSTRUCTION.MOVE_LEFT") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.setValidatedIP(virtualMachine.getIP() - 1)) {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_HOP_RIGHT("INSTRUCTION.HOP_RIGHT") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.setValidatedIP(virtualMachine.getIP() + 2)) {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_IF_TRUE("INSTRUCTION.IF_TRUE") {
	public void execute(VirtualMachine virtualMachine) {
	    if (virtualMachine.getBooleanStack().pop()) {
		virtualMachine.incrementIP();
	    } else {
		if (!virtualMachine.setValidatedIP(virtualMachine.getIP() + 2)) {
		    virtualMachine.incrementIP();
		}
	    }
	}
    },

    INSTRUCTION_IF_FALSE("INSTRUCTION.IF_FALSE") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.getBooleanStack().pop()) {
		virtualMachine.incrementIP();
	    } else {
		if (!virtualMachine.setValidatedIP(virtualMachine.getIP() + 2)) {
		    virtualMachine.incrementIP();
		}
	    }
	}
    },

    INSTRUCTION_JUMP_ABS("INSTRUCTION.JUMP_ABS") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.setValidatedIP(virtualMachine.getIntegerStack().pop())) {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_JUMP_REL("INSTRUCTION.JUMP_REL") {
	public void execute(VirtualMachine virtualMachine) {
	    if (!virtualMachine.setValidatedIP(virtualMachine.getIP() + virtualMachine.getIntegerStack().pop())) {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_JUMP_BEGIN("INSTRUCTION.JUMP_BEGIN") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.setIP(0);
	}
    },

    INSTRUCTION_JUMP_END("INSTRUCTION.JUMP_END") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.setIP(virtualMachine.getInstructionArray().getSize() - 1);
	}
    },

    INSTRUCTION_LOOP("INSTRUCTION.LOOP") {
	public void execute(VirtualMachine virtualMachine) {
	    int n = virtualMachine.getIntegerStack().pop();
	    if (n > 0) {
		virtualMachine.getIntegerStack().push(n - 1);
		virtualMachine.setIP(virtualMachine.getIP() - 1);
	    } else {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_POP_CODE("INSTRUCTION.POP_CODE") {
	public void execute(VirtualMachine virtualMachine) {
	    if (virtualMachine.getCodeStack().size() > 0) {
		virtualMachine.getInstructionArray().setValue(virtualMachine.getIP(),
			virtualMachine.getCodeStack().pop());
	    } else {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_SEEK_NOOP("INSTRUCTION.SEEK_NOOP") {
	public void execute(VirtualMachine virtualMachine) {
	    int noopIndex = virtualMachine.getInstructionArray().findNOOPIndex(virtualMachine.getIP());
	    if (noopIndex > 0) {
		virtualMachine.setIP(noopIndex);
	    } else {
		virtualMachine.incrementIP();
	    }
	}
    },

    INSTRUCTION_EXIT("INSTRUCTION.EXIT") {
	public void execute(VirtualMachine virtualMachine) {
	    virtualMachine.setExitState();
	}
    },

    ;

    private String mnemonic = null;
    private int intValue = 0;
    private boolean boolValue = false;
    private double floatValue = 0.0;

    /**
     * Gets the Mnemonic associated with the Instruction. Mnemonics are an
     * alternative (generally user-friendly) description that is used to
     * describe the Instruction.
     * 
     * @return
     */
    public String getMnemonic() {
	return mnemonic;
    }

    private void setMnemonic(String mnemonic) {
	this.mnemonic = mnemonic;
    }

    Instruction(String mnemonic) {
	setMnemonic(mnemonic);
    }

    /**
     * Defines the operation that is performed on the virtual machine when
     * Instruction is encountered.
     * 
     * @param virtualMachine
     */
    public abstract void execute(VirtualMachine virtualMachine);

}
