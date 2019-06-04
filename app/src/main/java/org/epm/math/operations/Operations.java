package org.epm.math.operations;

import android.content.Context;
import androidx.annotation.AnyThread;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

import org.epm.math.app.R;

/**
 * https://github.com/epman/mathops_android
 * GNU General Public License v3.0
 */
final class Operations {
    final static int OP_ADD_1 = 0;
    final static int OP_ADD_2 = 1;
    final static int OP_ADD_3 = 2;
    final static int OP_SUB_1 = 3;
    final static int OP_SUB_2 = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({OP_ADD_1, OP_ADD_2, OP_ADD_3, OP_SUB_1,OP_SUB_2})
    @interface OperationType {}

    @StringRes
    final static int[] OPNAMES = {R.string.addition1,R.string.addition2,R.string.addition3,R.string.subtraction1,R.string.subtraction2};
    @OperationType
    private int opType = OP_ADD_1;

    int op1, op2, result;

    private final Random rnd = new Random();

    Operations(@NonNull final Context ctx) {

    }

    @OperationType
    int getOpType(){
        return opType;
    }

    void changeOpType() {
        //noinspection WrongConstant
        //opType = (opType+1) % 5;
        switch (opType) {
            case OP_ADD_1:
                opType = OP_ADD_2;
                break;
            case OP_ADD_2:
                opType = OP_SUB_1;
                break;
            case OP_SUB_1:
                opType = OP_SUB_2;
                break;
            default:
                opType = OP_ADD_1;
                break;
        }
    }

    private static boolean hasCarry(int op1, int op2) {
        while (op1>0 && op2>0) {
            int o = (op1 % 10) + (op2 % 10);
            if (o>=10) {
                return true;
            }
            op1/=10;
            op2/=10;
        }
        return false;
    }

    final boolean isOneDigit() {
        return opType==Operations.OP_SUB_1 || opType==Operations.OP_ADD_1;
    }

    final String getSignForAccessibility(final Context ctx) {
        boolean isSub = opType==Operations.OP_SUB_1 || opType==Operations.OP_SUB_2;
        return (isSub?ctx.getString(R.string.minus):"+");
    }
    @AnyThread
    void newOp() {
        switch (opType) {
            case OP_ADD_1:
                op1 = rnd.nextInt(8)+1;
                op2 = rnd.nextInt(8)+1;
                result = op1+op2;
                break;
            case OP_ADD_2:
                do {
                    op1 = rnd.nextInt(80)+1;
                    op2 = rnd.nextInt(97) + 1;
                } while ((op1+op2)>99 || hasCarry(op1, op2));
                result = op1+op2;
                break;
            case OP_ADD_3:
                op1 = rnd.nextInt(98) + 1;
                op2 = rnd.nextInt(98) + 1;
                result = op1+op2;
                break;
            case OP_SUB_1:
                do {
                    op1 = rnd.nextInt(8)+2;
                    op2 = rnd.nextInt(7) + 1;
                } while (op2>=op1);
                result = op1-op2;
                break;
            case OP_SUB_2:
                do {
                    op1 = rnd.nextInt(90)+9;
                    op2 = rnd.nextInt(97) + 1;
                } while (op2>=op1);
                result = op1-op2;
                break;
            default:
                op1 = rnd.nextInt(8)+1;
                op2 = rnd.nextInt(8)+1;
                result = op1+op2;
        }
    }
}
