package com.ucsd.globalties.dvs.core.model;

import com.ucsd.globalties.dvs.core.EyeDisease;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.util.*;

public class DiseaseRecord {
    @Getter @Setter private EyeDisease mDiseaseName;
    @Getter @Setter private int mStatus;
    @Getter @Setter private String mDescription;

    @Getter private ImageInfo mHorizontalImage;
    @Getter private ImageInfo mVerticalImage;

    public static final int PASS = 0;
    public static final int REFER = 1;
    public static final int NOT_IMPLEMENTED = 501;

    public static final int MYOPIA_THRESHOLD = 2;
    public static final int MYOPIA_VALUE = 3;
    public static final int HYPEROPIA_THRESHOLD = 4;
    public static final int HYPEROPIA_VALUE = 5;
    public static final int STRABISMUS_DISTANCE_THRESHOLD = 6;
    public static final int STRABISMUS_ANGLE_THRESHOLD = 7;
    public static final int STRABISMUS_DISTANCE_VALUE = 8;
    public static final int STRABISMUS_ANGLE_VALUE = 9;

    public DiseaseRecord() {
        this(null, REFER);
    }

    public DiseaseRecord(EyeDisease name, int status) {
        this(name, status, false);
    }

    public DiseaseRecord(EyeDisease name, int status, boolean debug) {
        mDiseaseName = name;
        mStatus = status;
        mHorizontalImage = new ImageInfo();
        mVerticalImage = new ImageInfo();

        if (debug) {
            mHorizontalImage.getMLeftEye().getMThresholds().put(MYOPIA_THRESHOLD, "1.75");
            mHorizontalImage.getMLeftEye().getMValues().put(MYOPIA_VALUE, "0.22");

            mHorizontalImage.getMRightEye().getMThresholds().put(MYOPIA_THRESHOLD, "1.75");
            mHorizontalImage.getMRightEye().getMValues().put(MYOPIA_VALUE, "0.75");
        }
    }

    public class ImageInfo {
        @Getter private EyeInfo mLeftEye;
        @Getter private EyeInfo mRightEye;

        public ImageInfo() {
            mLeftEye = new EyeInfo();
            mRightEye = new EyeInfo();
        }

        public class EyeInfo {
            @Getter @Setter private Map<Integer, String> mThresholds;
            @Getter @Setter private Map<Integer, String> mValues;

            public EyeInfo() {
                mThresholds = new HashMap<>();
                mValues = new HashMap<>();
            }
        }
    }
}