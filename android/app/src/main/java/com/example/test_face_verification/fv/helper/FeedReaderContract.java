package com.example.test_face_verification.fv.helper;

import android.provider.BaseColumns;

public class FeedReaderContract {

	private FeedReaderContract() {
	}

	public static class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "entry";
		//public static final String SUBJECT_ID = "subject";
		public static final String EMPLOYEE_ID = "employee_id";
		public static final String IMAGE_URL = "image";
		public static final String SUBJECT_TEMPLATE = "template";
	}

}
