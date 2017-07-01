package org.epm.math.operations;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.epm.math.base.Math;

public final class OperationsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    @Nullable
    private Operations operations = null;

    private final Button[] btns = new Button[10];

    @Nullable
    private MediaPlayer mpVictory = null;
    @Nullable
    private MediaPlayer mpLose = null;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operazioni);
        Math.hideSystemUI(getWindow().getDecorView());

        getSupportLoaderManager()
                .initLoader(0, null, this);

        operations = new Operations(this);

        findViewById(R.id.textView1).setOnClickListener(newOpOnClickListener);
        findViewById(R.id.textView2).setOnClickListener(newOpOnClickListener);
        findViewById(R.id.textViewResults).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.textViewResults)).setText("");
            }
        });

        initTextSwitcher(R.id.textSwitcerPoints);

        btns[0] = (Button) findViewById(R.id.button0);
        btns[1] = (Button) findViewById(R.id.button1);
        btns[2] = (Button) findViewById(R.id.button2);
        btns[3] = (Button) findViewById(R.id.button3);
        btns[4] = (Button) findViewById(R.id.button4);
        btns[5] = (Button) findViewById(R.id.button5);
        btns[6] = (Button) findViewById(R.id.button6);
        btns[7] = (Button) findViewById(R.id.button7);
        btns[8] = (Button) findViewById(R.id.button8);
        btns[9] = (Button) findViewById(R.id.button9);
        for (final Button b : btns) {
            b.setOnClickListener(buttonNumberOnClickListener);
        }
        findViewById(R.id.buttonDelete).setOnClickListener(deleteClickListener);
        findViewById(R.id.buttonSpace).setOnClickListener(deleteClickListener);

        newOperation();
    }

    //region Loaders

    /**
     * @see <a href="https://developer.android.com/guide/components/loaders.html">Loaders</a>
     */
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        final DataLoader l = new DataLoader(this);
        return l;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        final DataLoader l = (DataLoader)loader;
        mpLose = l.mpLose;
        mpVictory = l.mpVictory;
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
    //endregion



    private void initTextSwitcher(@IdRes final int tsId) {
        final TextSwitcher ts = (TextSwitcher)findViewById(tsId);

        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        ts.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                final TextView myText = new TextView(OperationsActivity.this);
                FrameLayout.LayoutParams layoutparams1 = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
                myText.setLayoutParams(layoutparams1);

                myText.setGravity(Gravity.CENTER_VERTICAL);
                TextViewCompat.setTextAppearance(myText, R.style.TextAppearance_AppCompat_Large);
                myText.setTextColor(Color.WHITE);
                return myText;
            }
        });

        // Declare the in and out animations and initialize them
        final Animation in = AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        final Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

        // set the animation type of textSwitcher
        ts.setInAnimation(in);
        ts.setOutAnimation(out);
    }

    private final View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            result = 0;
            updateUI();
        }
    };
    private final View.OnClickListener newOpOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newOperation();
        }
    };

    @UiThread
    private void newOperation() {
        if (operations!=null) {
            result = 0;
            operations.newOp();
            updateUI();
        }
    }
    private int result = 0;
    private final View.OnClickListener buttonNumberOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buttonPressed((Button)v);
        }
    };

    @UiThread
    private void buttonPressed(@NonNull final Button b) {
        if (operations==null)
            return;
        result = result * 10 + Integer.parseInt( (String) b.getTag() );
        updateUI();
        final int l1 = (int)java.lang.Math.log10(result)+1;
        final int l2 = (int)java.lang.Math.log10(operations.result)+1;
        if (result==operations.result) {
            victory();
        } else if (l1>=l2) {
            lose();
        }
    }

    @UiThread
    private void victory()
    {
        if (mpVictory!=null)
           mpVictory.start();
        final Math math = Math.getInstance(this);
        math.increasePoints(this);
        ((TextSwitcher)findViewById(R.id.textSwitcerPoints)).setText(Integer.toString(math.getPoints()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newOperation();
            }
        }, 1000);
    }
    @UiThread
    private void lose()
    {
        if (mpLose!=null)
            mpLose.start();
        final Math math = Math.getInstance(this);
        math.increasePoints(this);
        ((TextSwitcher)findViewById(R.id.textSwitcerPoints)).setText(Integer.toString(math.getPoints()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                newOperation();
            }
        }, 1000);
    }

    @UiThread
    private void updateUI()
    {
        if (operations==null)
            return;

        ((TextView)findViewById(R.id.textView1)).setText(Integer.toString(operations.op1)+"+");
        ((TextView)findViewById(R.id.textView2)).setText(Integer.toString(operations.op2)+"=");
        if (result==0) {
            ((TextView) findViewById(R.id.textViewResults)).setText("");
        } else {
            ((TextView) findViewById(R.id.textViewResults)).setText(Integer.toString(result) + " ");
        }
        final Math math = Math.getInstance(this);
        ((TextSwitcher)findViewById(R.id.textSwitcerPoints)).setText(Integer.toString(math.getPoints()));
    }

}
