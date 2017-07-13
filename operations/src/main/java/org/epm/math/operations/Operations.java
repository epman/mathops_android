package org.epm.math.operations;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Random;

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

    void changeOpType(){
        //noinspection WrongConstant
        opType = (opType+1) % 5;
    }

    private static boolean hasReport(int op1, int op2) {
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
    @AnyThread
    void newOp() {
        switch (opType) {
            case OP_ADD_1:
                op1 = rnd.nextInt(8)+1;
                op2 = rnd.nextInt(8)+1;
                result = op1+op2;
                break;
            case OP_ADD_2:
                op1 = rnd.nextInt(80)+1;
                do {
                    op2 = rnd.nextInt(97) + 1;
                } while ((op1+op2)>99 && hasReport(op1, op2));
                result = op1+op2;
                break;
            case OP_ADD_3:
                op1 = rnd.nextInt(98) + 1;
                op2 = rnd.nextInt(98) + 1;
                result = op1+op2;
                break;
            case OP_SUB_1:
                op1 = rnd.nextInt(8)+2;
                do {
                    op2 = rnd.nextInt(7) + 1;
                } while (op2>=op1);
                result = op1-op2;
                break;
            case OP_SUB_2:
                op1 = rnd.nextInt(90)+9;
                do {
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
