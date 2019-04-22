package com.example.paddy.fyp.routines;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.paddy.fyp.ExerciseActivity;
import com.example.paddy.fyp.NewExerciseActivity;
import com.example.paddy.fyp.R;
import com.example.paddy.fyp.adapters.ExerciseRecyclerAdapter;
import com.example.paddy.fyp.home.HomeActivity;
import com.example.paddy.fyp.models.Exercise;
import com.example.paddy.fyp.models.LogItem;
import com.example.paddy.fyp.models.RoutineExercise;
import com.example.paddy.fyp.models.RoutineHome;
import com.example.paddy.fyp.persistence.ExerciseRepository;
import com.example.paddy.fyp.persistence.RoutineExerciseRepository;

import java.util.ArrayList;
import java.util.List;

public class RoutinesAddExerciseActivity extends AppCompatActivity implements
        ExerciseRecyclerAdapter.OnExerciseListener,
        View.OnClickListener{

    private static final String TAG = "RoutineAddExerciseActivity";

    // UI components
    private RecyclerView mRecyclerView;
    private ImageButton mAddButton, mBackButton;

    // vars
    private ArrayList<Exercise> mExercise = new ArrayList<>();
    private ExerciseRecyclerAdapter mExerciseRecyclerAdapter;
    private ExerciseRepository mExerciseRepository;
    private RoutineExerciseRepository mRoutineExerciseRepository;
    private RoutineHome mRoutineHome;
    private boolean mIsNewLogItem;
    private LogItem mInitialLogItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_add_exercise);
        Log.d(TAG, "onCreate: started");
        
        mRecyclerView = findViewById(R.id.rvAddExercise);
        mAddButton = findViewById(R.id.toolbar_add);
        mBackButton = findViewById(R.id.toolbar_back_arrow_select_exercise);

        mExerciseRepository = new ExerciseRepository(this);
        mRoutineExerciseRepository = new RoutineExerciseRepository(this);

//        if(getIntent().hasExtra("selected_item1")){
//            LogItem logItem1 = getIntent().getParcelableExtra("selected_item1");
//            Log.d(TAG, "onCreateOne: " + logItem1.toString());
//        }

        if(getIntent().hasExtra("selected_routine_home")){
            mRoutineHome = getIntent().getParcelableExtra("selected_routine_home");
            Log.d(TAG, "onCreateExerciseSet: " + mRoutineHome.toString());
        }


//        getIncomingIntent();

        initRecyclerView();
        retrieveExercises();
        setListeners();

    }

//    private boolean getIncomingIntent(){
//        if(getIntent().hasExtra("selected_item1")){
//            mInitialLogItem = getIntent().getParcelableExtra("selected_item1");
//            Log.d(TAG, "getIncomingIntent: " + mInitialLogItem.toString());
//
//            mIsNewLogItem = false;
//            return false;
//        }
//        mIsNewLogItem = true;
//        return true;
//    }


    private void retrieveExercises(){
        mExerciseRepository.retrieveExerciseTask().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                if(mExercise.size() > 0){
                    mExercise.clear();
                }
                if(exercises != null){
                    mExercise.addAll(exercises);
                    for(int i = 0; i < exercises.size(); i++){
                        String name = exercises.get(i).getName();
                        Log.d(TAG, "onChanged: " + name);
                    }
                }
                mExerciseRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mExerciseRecyclerAdapter = new ExerciseRecyclerAdapter(mExercise, this);
        mRecyclerView.setAdapter(mExerciseRecyclerAdapter);
    }

    private void setListeners(){
        mAddButton.setOnClickListener(this);
        mBackButton.setOnClickListener(this);

    }


    @Override
    public void onExerciseClick(int position) {
        if(getIntent().hasExtra("selected_routine_home")){
            mRoutineHome = getIntent().getParcelableExtra("selected_routine_home");
            Log.d(TAG, "onCreateExerciseSetFour: " + mRoutineHome.toString());
        }
        RoutineExercise mFinalExercise = new RoutineExercise();
        mFinalExercise.setName(mExercise.get(position).getName());
        mFinalExercise.setCategory(mExercise.get(position).getCategory());
        mFinalExercise.setLogId(mRoutineHome.getId());
        mRoutineExerciseRepository.insertExerciseTask(mFinalExercise);
        Intent intent = new Intent(this, RoutineLogActivity.class);
        intent.putExtra("selected_exercise", mExercise.get(position));
        intent.putExtra("selected_item3", mInitialLogItem);
        intent.putExtra("selected_routine_item", mRoutineHome);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_add:{
                Intent intent = new Intent(this, RoutineNewExerciseActivity.class);
                intent.putExtra("selected_routine_item", mRoutineHome);
                startActivity(intent);
                break;
            }
            case R.id.toolbar_back_arrow_select_exercise:{
                Intent intent = new Intent(this, RoutineLogActivity.class);
                intent.putExtra("selected_routine_item", mRoutineHome);
                startActivity(intent);
                break;
            }
        }
    }

    private void deleteExercise(Exercise exercise){
        mExercise.remove(exercise);
        mExerciseRecyclerAdapter.notifyDataSetChanged();

        mExerciseRepository.deleteExercise(exercise);
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            deleteExercise(mExercise.get(viewHolder.getAdapterPosition()));
        }
    };
}
