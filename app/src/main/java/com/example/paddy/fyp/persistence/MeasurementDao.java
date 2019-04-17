package com.example.paddy.fyp.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.paddy.fyp.models.Bodyweight;
import com.example.paddy.fyp.models.Measurement;

import java.util.List;

@Dao
public interface MeasurementDao {

        @Insert
        long[] insertMeasurement(Measurement... measurements);

        @Query("SELECT * FROM measurement")
        LiveData<List<Measurement>> getMeasurement();

        @Delete
        int delete(Measurement... measurements);

        @Update
        int update(Measurement... measurements);


}
