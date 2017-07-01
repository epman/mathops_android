package org.epm.math.operations;

import android.content.Context;
import android.support.annotation.AnyThread;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

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
    public @interface OperationType {}

    @OperationType
    private int opType = OP_ADD_1;

    int op1, op2, result;

    private final Random rnd = new Random();

    Operations(@NonNull final Context ctx) {

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
                } while ((op1+op2)>99);
                result = op1+op2;
                break;
            case OP_ADD_3:
                op1 = rnd.nextInt(98) + 1;
                op2 = rnd.nextInt(98) + 1;
                result = op1+op2;
                break;
            case OP_SUB_1:
                op1 = rnd.nextInt(3)+1;
                do {
                    op2 = rnd.nextInt(7) + 1;
                } while (op2>op1);
                result = op1-op2;
                break;
            case OP_SUB_2:
                op1 = rnd.nextInt(98)+1;
                do {
                    op2 = rnd.nextInt(97) + 1;
                } while (op2>op1);
                result = op1-op2;
                break;
            default:
                op1 = rnd.nextInt(8)+1;
                op2 = rnd.nextInt(8)+1;
                result = op1+op2;
        }
    }
}
